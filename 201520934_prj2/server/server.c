#include <openssl/ssl.h>
#include <openssl/bio.h>
#include <openssl/err.h>
#include <openssl/rand.h>
#include <openssl/evp.h>
#include <openssl/aes.h>
#include <openssl/rsa.h>
#include <openssl/pem.h>


#include <stdio.h>
#include <string.h>
#include <assert.h>

#if defined _WIN32 || defined _WIN64 //윈도우
#include <openssl/applink.c>
#include <windows.h>
#else //리눅스
#include <pthread.h>
#include <unistd.h>
#include <arpa/inet.h>
#define _write write
#endif

int AES(unsigned char*, int, unsigned char *,int);
void sendThread(BIO*);
int readFixedLength(BIO*, unsigned char*, unsigned long);
void makeRSAKeyFile(RSA **, RSA **, char *, char *);
void decryptRSAFile(RSA *rsaPriv, char*, int);
unsigned char key[16] = {0, };
#if defined _WIN32 || defined _WIN64 //윈도우
DWORD WINAPI ThreadFunction(LPVOID lpParam) {
	sendThread((BIO*)lpParam); //cio를 BIO *로 형변환해서 sendThread함수 실행
}
#else //리눅스
void *ThreadFunction(void *args) {
	sendThread((BIO *)args); //cio를 BIO *로 형변환해서 sendThread함수 실행
}
#endif

void makeRSAKeyFile(RSA **rsaPriv, RSA **rsaPub, char *privKeyFn, char *pubKeyFn) {
	FILE *fp; 
	int res;
	*rsaPriv = RSA_generate_key(2048, RSA_F4, NULL, NULL); //개인 키를 만듬
	assert(rsaPriv);
	*rsaPub = RSAPublicKey_dup(*rsaPriv); //공개 키를 만듬(사실상 개인 키의 복제)
	assert(rsaPub);

	fp = fopen(privKeyFn, "wb"); //privKey.pem
	assert(fp);
	res = PEM_write_RSAPrivateKey(fp, *rsaPriv, NULL, NULL, 0, NULL, NULL); //개인 키를 파일에 씀
	assert(res);

	fclose(fp);

	fp = fopen(pubKeyFn, "wb"); //pubKey.pem
	assert(fp);
	res = PEM_write_RSAPublicKey(fp, *rsaPub); //파일에 공개키를 씀
	assert(res);
	fclose(fp);

}

void decryptRSAFile(RSA *rsaPriv, char *symKey, int len) {
	int dsize;
	dsize = RSA_private_decrypt(len, symKey, key, rsaPriv, RSA_PKCS1_OAEP_PADDING); //암호화 된 대칭키를 복호화 함
	assert(dsize>=0); //복호화 리턴 값이 0이상이 아닌 지 확인

}
int AES(unsigned char *p, int len, unsigned char *c, int enc) {
	int cipher_len, out_len;
	unsigned char temp[1024];
	EVP_CIPHER_CTX ctx;
	EVP_CIPHER_CTX_init(&ctx); //ctx 초기화
	EVP_CipherInit_ex(&ctx, EVP_aes_128_cbc(), NULL, key, NULL, enc); //어떠한 암호화 알고리즘을 사용할지 정함
	EVP_CipherUpdate(&ctx, temp, &out_len, p, len);  //암호화(복호화) partial data
	memcpy(c, temp, out_len); //temp메모리를 c에 복사
	cipher_len = out_len; //길이를 cipher_len에 저장
	EVP_CipherFinal_ex(&ctx, temp, &out_len); //암호화(복호화) final data생성
	memcpy(&c[cipher_len], temp, out_len); //temp메모리를 c[암호문 길이] 복사
	cipher_len += out_len; //길이를 cipher_len과 더해 cipher_len에 저장
	EVP_CIPHER_CTX_cleanup(&ctx); //ctx 정보를 다 clear해준다
	return cipher_len; //암호화(복호화)한 데이터의 길이를 리턴
}

void sendThread(BIO *cbio) {
	unsigned long len;
	unsigned long l;
	unsigned char buf[1024];
	unsigned char enc[1024];
	while (1) {
		memset(buf, 0, sizeof(buf)); //buf 초기화
		fgets(buf, 1024, stdin); //키보드로 입력 받은 것을 buf에 저장

		len = strlen(buf); //buf 길이를 len에 저장

		len = AES(buf, len, enc, AES_ENCRYPT); //입력 받은 데이터 암호화

		l = htonl(len); //hostlong(unsigned integer)를 host byte order에서 network byte order로 변환
		BIO_write(cbio, &l, sizeof(l)); //암호화 된 길이를 클라이언트에 보냄

		BIO_write(cbio, enc, len); //암호화된 데이터를 클라이언트에 보냄
		printf("<< %s\n", buf); //입력 메시지를 출력

		if(!strcmp(buf, "bye\n")) {
			exit(0);
		}
	}
}

int readFixedLength(BIO *cbio, unsigned char *buf, unsigned long l) {
	int len = 0;
	int idx = 0;
	while (1) {
		len = BIO_read(cbio, &buf[idx], l - idx); //클라이언트에서 온 것을 읽어서 buf에 저장
		if (len <= 0) //연결이 끊긴 것을 의미
			return len; //len값 리턴
		idx += len; //idx에 길이를 계속 더해서
		if (idx == l) //수신 받은 길이 idx가 입력 받을 길이 l과 같아지면
			return idx; //수신 받은 길이 idx를 리턴
	}

	return idx; //수신 받은 길이 idx 리턴
}

int main() {
	BIO *abio; //연결 수신을 위한 용도
	BIO	*cbio; //연결된 클라이언트와 통신을 위한 용도
	FILE *fp;
	unsigned long l;
	unsigned int len;
	unsigned char buf[1024];
	unsigned char dec[1024];
#if defined _WIN32 || defined _WIN64 //윈도우
	DWORD dwThreadId;
	DWORD dwThredParam;
	HANDLE hThread;
#else //리눅스
	pthread_t hThread;
#endif
	//초기화 함수들로 반드시 호출해야하는 함수
	ERR_load_BIO_strings();
	SSL_load_error_strings(); //libssl 오류 문자열을 등록합니다.
	OpenSSL_add_all_algorithms(); //해쉬 함수 및 암호화 함수들을 사용하기 위해 호출됨
	ERR_load_crypto_strings(); //모든 libcrypto 기능에 대한 오류 문자열을 등록합니다.

	abio = BIO_new_accept("127.0.0.1:60010"); //accept bio생성 60001번 포트를 listen

	if (BIO_do_accept(abio) <= 0) { //accept bio초기화
		ERR_print_errors_fp(stderr);
		exit(0);
	}

	while(1) {
		printf("Waiting Connection..\n"); 
		if (BIO_do_accept(abio) <= 0) { //incoming connection을 기다림(blocking)
			ERR_print_errors_fp(stderr);
			exit(0);
		}
		cbio = BIO_pop(abio); //클라이언트로부터 연결이 되면 클라이언트와 통신할 수 있는 cbio생성
		printf("Connected\n"); //연결 메시지 출력
//////////////////////////////////////////////////////////////////////////////////////////////////////////
		RSA *rsaPriv = NULL, *rsaPub = NULL; //RSA 구조체 타입의 변수 두 개 선언(개인/공개 키)

		char symKey[2048] = {0, }; //대칭 키(아직 정보 없음)

		makeRSAKeyFile(&rsaPriv, &rsaPub, "privKey.pem", "pubKey.pem"); //키 쌍 생성
		
		PEM_write_bio_RSAPublicKey(cbio, rsaPub); //공개 키를 클라이언트에게 전송
		printf("Send Public Key Successed\n");

		sleep(1); //write를 먼저 하게 하기 위해 sleep
		len = BIO_read(cbio, symKey, 256); // 클라이언트로부터 암호화 된 대칭 키를 받음
		if (len <= 0) { //받은 길이가 0이거나 그 이하면(에러) 에러처리
			printf("Key Receiving Error\n");
			BIO_free(cbio);
			return 0;
		}
		usleep(1000);
		printf("Received Encrypted Key\n");

		decryptRSAFile(rsaPriv, symKey, len); //암호화 된 키를 복호화
		printf("Key Decrypt Successed!\n"); //함수를 빠져나왔다면 복호화 성공 !
											//함수 내에 assert가 있기 때문.
		printf("\n========START CHATTING========\n\n"); //채팅 시작
////////////////////////////////////////////////////////////////////////////////////////////////
#if defined _WIN32 || defined _WIN64 //윈도우
		hThread = CreateThread(NULL, 0, ThreadFunction, (LPVOID)cbio, 0, &dwThreadId); //쓰레드 생성
#else //리눅스
		pthread_create(&hThread, NULL, ThreadFunction, (void *)cbio); //쓰레드 생성
#endif
		while(1) {
			len = readFixedLength(cbio, (unsigned char *)&l, sizeof(l)); //서버로 부터 길이를 읽음 
			if(len <= 0 )
				break;

			l = ntohl(l); //hostlong(unsigned integer)를 network byte order에서 host byte order로 변환

			memset(buf, 0, sizeof(buf)); //buf 초기화
			len = readFixedLength(cbio, buf, l); //서버로 부터 데이터를 읽음
			if (len <= 0)
				break;

			memset(dec, 0, sizeof(dec)); //dec 초기화
			len = AES(buf, len, dec, AES_DECRYPT); //복호화
			printf(">> %s\n", dec);
		}

#if defined _WIN32 || defined _WIN64 //윈도우
		CloseHandle(hThread);
		WaitForSingObject(hThread, INFINITE );
#else //리눅스
		pthread_cancel(hThread); //쓰레드 종료
		pthread_join(hThread, NULL); //쓰레드를 기다림
#endif
		BIO_free(cbio); //연결 해제
		printf("Disconnected\n"); //연결 해제 메시지 출력
	}
}
