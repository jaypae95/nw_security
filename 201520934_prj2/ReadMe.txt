linux에서 구현했지만 window에서도 가능하게 구현 하였음
과제 채점은 linux에서 해주시기를 바랍니다.(리눅스에서 컴파일 해서 돌려봤기 때문에)

터미널 창을 두개를 열어 각각의 터미널에서 

client 폴더로 이동 후
gcc-o client client.c -lpthread -lcrypto -lssl

server폴더로 이동 후
gcc-o server server.c -lpthread -lcrypto -lssl

후

./server을 먼저 실행 시킨 후

./client를 실행 시킨다.

서버는 개인 키 공개 키 쌍을 만들어 클라이언트에게 전송 후
클라이언트는 받은 공개 키로 aes에서 사용할 대칭 키를 암호화 함
암호화 한 키를 서버에게 전송 후 서버는 개인 키로 복호화 함

둘이 대칭 키로 통신을 함.

ctrl+C 또는 bye 를 입력하면 종료
클라이언트가 종료되면 서버는 남아서 다시 연결을 기다림.
서버가 종료되면 클라이언트도 종료

출처
ppt/ 네트워크보안 및 실습 프로젝트 추가 참고
ppt/SSL프로젝트(2)
https://stackoverflow.com/questions/12332499/load-openssl-rsa-key-from-a-string-in-c
https://shanetully.com/2012/04/simple-public-key-encryption-with-rsa-and-openssl/
http://hayageek.com/rsa-encryption-decryption-openssl-c/