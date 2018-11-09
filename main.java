import java.io.UnsupportedEncodingException;

public class main {
	public static void main(String[] args) {
		String key = "abcdef";
		String plantext = "test message";
		
		byte[] Key = null;
		byte[] PlainText = null;
		byte[] KeyStream = null;
		byte[] CipherText = null;
		
		int[] S = new int[256];
		int tmp = 0;
		
		try {
			Key = key.getBytes("ASCII");
			System.out.println(Key);
			PlainText = plantext.getBytes("ASCII");
			KeyStream = new byte[PlainText.length];
			CipherText = new byte[PlainText.length];
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		//KSA
		int i = 0, j = 0; //초기화
		for(i=0; i<256; i++ ) {
			S[i] = i; //S에 0 부터 255까지 값을 넣어줌
		}
		for (i=0;i<256;i++) { 
			j=(j + S[i] + Key[(i%key.length())]) % 256; //i%key.length로 나머지 key를 반복형태로 채워주고 그것을 Key의 index값 + s[i] + j를 통해 랜덤한 값을 j에 넣어줌(난수생성)
			System.out.println(i%key.length());
			tmp = S[j]; //S[i]와 S[j]의 값을 바꿔줌
			S[j] = S[i];
			S[i] = tmp; 
		}
		//PRGA
		i=0; j=0;
		for(int k = 0; k<PlainText.length; k++) {
			i = (i+1) % 256; //i 값 1 증가 (%256)
			j = (j + S[i]) % 256; // j값 s[i]만큼 증가시켜줌(%256)
			tmp = S[j]; //s[i]와 s[j]의 값을 바꿔줌
			S[j] = S[i];
			S[i] = tmp;
			KeyStream[k] = (byte)S[((S[i] + S[j])%256)]; //s[i]와 s[j]를 더한 것을(%256) s의 인덱스로 한 후 byte로 형전환한 것을 KeyStream[k]에 넣어줌
			CipherText[k] = (byte)(PlainText[k] ^ KeyStream[k]); //PlainText와 KeyStream을 xor한 후 byte로 형전환 후 CipherText[k]에 넣어줌
			System.out.printf("%02x ",CipherText[k]); //출력
		}
		
		
	}

}
