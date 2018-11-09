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

#if defined _WIN32 || defined _WIN64 //������
#include <openssl/applink.c>
#include <windows.h>
#else //������
#include <pthread.h>
#include <unistd.h>
#include <arpa/inet.h>
#endif

int AES(unsigned char*, int, unsigned char *,int);
void sendThread(BIO*);
int readFixedLength(BIO*, unsigned char*, unsigned long);
int encryptRSAFile(RSA *, char*);

unsigned char key[16] = "ABCDEFGHIJKLMNOP";
unsigned char symKey[2048] = {0, };
#if defined _WIN32 || defined _WIN64 //������
DWORD WINAPI ThreadFunction(LPVOID lpParam) {
	sendThread((BIO*)lpParam); //cio�� BIO *�� ����ȯ�ؼ� sendThread�Լ� ����
}
#else //������
void *ThreadFunction(void *args) {
	sendThread((BIO *)args); //cio�� BIO *�� ����ȯ�ؼ� sendThread�Լ� ����
}
#endif

int encryptRSAFile(RSA *rsaPub, char *key) {
	int csize;
	csize = RSA_public_encrypt(16, key, symKey, rsaPub, RSA_PKCS1_OAEP_PADDING); //��ĪŰ�� ��ȣȭ��
	assert(csize >= 0); //��ȣȭ �Լ� ���� ���� 0�̻��� �� Ȯ��

	return csize; //��ȣȭ �Լ� ���� �� ����
}
int AES(unsigned char *p, int len, unsigned char *c, int enc) {
	int cipher_len, out_len; 
	unsigned char temp[1024];
	EVP_CIPHER_CTX ctx; 
	EVP_CIPHER_CTX_init(&ctx); //ctx �ʱ�ȭ
	EVP_CipherInit_ex(&ctx, EVP_aes_128_cbc(), NULL, key, NULL, enc); //��� ��ȣȭ �˰����� ������� ����
	EVP_CipherUpdate(&ctx, temp, &out_len, p, len);  //��ȣȭ(��ȣȭ) partial data
	memcpy(c, temp, out_len); //temp�޸𸮸� c�� ����
	cipher_len = out_len; //���̸� cipher_len�� ����
	EVP_CipherFinal_ex(&ctx, temp, &out_len); //��ȣȭ(��ȣȭ) final data����
	memcpy(&c[cipher_len], temp, out_len); //temp�޸𸮸� c[��ȣ�� ����] ����
	cipher_len += out_len; //���̸� cipher_len�� ���� cipher_len�� ����
	EVP_CIPHER_CTX_cleanup(&ctx); //ctx ������ �� clear���ش�
	return cipher_len; //��ȣȭ(��ȣȭ)�� �������� ���̸� ����
}

void sendThread(BIO *cbio) {
	unsigned long len;
	unsigned long l;
	unsigned char buf[1024];
	unsigned char enc[1024];
	while(1) {
		memset(buf, 0, sizeof(buf)); //buf �ʱ�ȭ
		fgets(buf, 1024, stdin); //Ű����� �Է� ���� ���� buf�� ����

		len = strlen(buf); //buf ���̸� len�� ����

		len = AES(buf, len, enc, AES_ENCRYPT); //�Է� ���� ������ ��ȣȭ

		l = htonl(len); //hostlong(unsigned integer)�� host byte order���� network byte order�� ��ȯ
		BIO_write(cbio, &l, sizeof(l)); //��ȣȭ �� ���̸� ������ ����

		BIO_write(cbio, enc, len); //��ȣȭ�� �����͸� ������ ����
		printf("<< %s\n", buf); //�Է� �޽����� ���

		if(!strcmp(buf, "bye\n")) {
			exit(0);
		}
	}
}

int readFixedLength(BIO *cbio, unsigned char *buf, unsigned long l) {
	int len = 0;
	int idx = 0;
	while(1) {
		len = BIO_read(cbio, &buf[idx], l-idx); //�������� �� ���� �о buf�� ����
		if(len <= 0) //������ ���� ���� �ǹ�
			return len; //len�� ����
		idx += len; //idx�� ���̸� ��� ���ؼ�
		if(idx==l) //���� ���� ���� idx�� �Է� ���� ���� l�� ��������
			return idx; //���� ���� ���� idx�� ����
	}

	return idx; //���� ���� ���� idx ����
}

int main() {
	BIO *cbio; //������ ����� ���� bio
	FILE *fp;
	unsigned long l;
	unsigned int len;
	unsigned char buf[1024];
	unsigned char dec[1024];
#if defined _WIN32 || defined _WIN64 //������
	DWORD dwThreadId;
	DWORD dwThredParam;
	HANDLE hThread;
#else //������
	pthread_t hThread;
#endif
	//�ʱ�ȭ �Լ���� �ݵ�� ȣ���ؾ��ϴ� �Լ�
	ERR_load_BIO_strings();
	SSL_load_error_strings(); //libssl ���� ���ڿ��� ����մϴ�.
	OpenSSL_add_all_algorithms(); //�ؽ� �Լ� �� ��ȣȭ �Լ����� ����ϱ� ���� ȣ���
	ERR_load_crypto_strings(); //��� libcrypto ��ɿ� ���� ���� ���ڿ��� ����մϴ�.

	cbio = BIO_new_connect("127.0.0.1:60010"); //�Լ��� ȣ���Ͽ� ������ ���� �õ�

	if (BIO_do_connect(cbio) <= 0) { //���� ���н� ���� �޽��� ��� �� ����
		fprintf(stderr, "Error connecting to server\n");
		ERR_print_errors_fp(stderr);
		return(0);
	}
	
	printf("Connected\n"); //���� �޽��� ���
///////////////////////////////////////////////////////////////////////////////////////////////////////
	char rsaPub_rec[2048] = {0, }; //���� Ű�� ���� �迭
	RSA *rsaPub = NULL; //RSA Ÿ���� ����
	usleep(1000*10);

	BIO_read(cbio, rsaPub_rec, 2048); //������ ���� ����Ű�� ����
	printf("Received Public Key\n");

	BIO *keybio = BIO_new_mem_buf(rsaPub_rec, -1); //���ο� bio ���� �� ���� ���� Ű ����
	rsaPub = PEM_read_bio_RSAPublicKey(keybio, NULL, NULL, NULL); //���� �� ���� �ٽ� ����

	len = encryptRSAFile(rsaPub, key); //���� ����Ű�� ��ĪŰ ��ȣȭ
	printf("Key Encrypt Successed!\n");

	BIO_write(cbio, symKey, len); //��ȣȭ �� ��ĪŰ�� ������ ����
	printf("Send Encrpyted Key Successed!\n");

	sleep(1);
	printf("\n========START CHATTING========\n\n"); //ä�� ����!
/////////////////////////////////////////////////////////////////////////////////////////////////////
#if defined _WIN32 || defined _WIN64 //������
	hThread = CreateThread(NULL, 0, ThreadFunction, (LPVOID)cbio, 0, &dwThreadId); //������ ����
#else //������
	pthread_create(&hThread, NULL, ThreadFunction, (void *)cbio); //������ ����
#endif
	while(1) {
		len = readFixedLength(cbio, (unsigned char *)&l, sizeof(l)); //������ ���� ���̸� ����
		if(len <= 0  ) 
			break;

		l = ntohl(l); //hostlong(unsigned integer)�� network byte order���� host byte order�� ��ȯ

		memset(buf, 0, sizeof(buf)); //buf �ʱ�ȭ
		len = readFixedLength(cbio, buf, l); //������ ���� �����͸� ����
		if (len <= 0)
			break;

		memset(dec, 0, sizeof(dec)); //dec �ʱ�ȭ
		len = AES(buf, len, dec, AES_DECRYPT); //��ȣȭ
		printf(">> %s\n", dec);
	}

#if defined _WIN32 || defined _WIN64 //������
	CloseHandle(hThread);
	WaitForSingObject(hThread, INFINITE );
#else //������
	pthread_cancel(hThread); //������ ����
	pthread_join(hThread, NULL); //�����带 ��ٸ�
#endif
	BIO_free(cbio); //���� ����
	printf("Disconnected\n");

	return 0;
}
