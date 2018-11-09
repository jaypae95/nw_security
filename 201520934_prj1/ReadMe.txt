linux에서 구현했지만 window에서도 가능하게 구현 하였음
과제 채점은 linux에서 해주시기를 바랍니다.(리눅스에서 컴파일 해서 돌려봤기 때문에)

터미널 창을 두개를 열어 각각의 터미널에서 
gcc-o client client.c -lpthread -lcrypto -lssl
gcc-o server server.c -lpthread -lcrypto -lssl

후

./server을 먼저 실행 시킨 후

./client를 실행 시킨다.

ctrl+C 또는 bye 를 입력하면 종료
클라이언트가 종료되면 서버는 남아서 다시 연결을 기다림.
서버가 종료되면 클라이언트도 종료

출처
http://yappeekaboo.tistory.com/15
http://nine01223.tistory.com/281
http://yappeekaboo.tistory.com/18
ppt/ 네트워크보안 및 실습 프로젝트 추가 참고
ppt/SSL프로젝트(1)