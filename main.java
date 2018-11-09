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
		int i = 0, j = 0; //�ʱ�ȭ
		for(i=0; i<256; i++ ) {
			S[i] = i; //S�� 0 ���� 255���� ���� �־���
		}
		for (i=0;i<256;i++) { 
			j=(j + S[i] + Key[(i%key.length())]) % 256; //i%key.length�� ������ key�� �ݺ����·� ä���ְ� �װ��� Key�� index�� + s[i] + j�� ���� ������ ���� j�� �־���(��������)
			System.out.println(i%key.length());
			tmp = S[j]; //S[i]�� S[j]�� ���� �ٲ���
			S[j] = S[i];
			S[i] = tmp; 
		}
		//PRGA
		i=0; j=0;
		for(int k = 0; k<PlainText.length; k++) {
			i = (i+1) % 256; //i �� 1 ���� (%256)
			j = (j + S[i]) % 256; // j�� s[i]��ŭ ����������(%256)
			tmp = S[j]; //s[i]�� s[j]�� ���� �ٲ���
			S[j] = S[i];
			S[i] = tmp;
			KeyStream[k] = (byte)S[((S[i] + S[j])%256)]; //s[i]�� s[j]�� ���� ����(%256) s�� �ε����� �� �� byte�� ����ȯ�� ���� KeyStream[k]�� �־���
			CipherText[k] = (byte)(PlainText[k] ^ KeyStream[k]); //PlainText�� KeyStream�� xor�� �� byte�� ����ȯ �� CipherText[k]�� �־���
			System.out.printf("%02x ",CipherText[k]); //���
		}
		
		
	}

}
