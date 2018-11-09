import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HMAC {
	static int BLOCK_SIZE = 64;
	public static void main(String[] args) {
		
		String key = "Mutex";
		String plaintext = "Attack at 8 p.m";
		byte[] PlainText = null;
		byte[] Key = null;
		
		byte[] output = null;
		String outPut ="";		
		
		try {
			PlainText = plaintext.getBytes("ASCII"); //byte������ ��ȯ
			Key = key.getBytes("ASCII"); //byte������ ��ȯ
			
			output = HMAC_MD5(Key, PlainText); //MD5�� ����ϴ� HMAC �Լ� ��� ���� output�� ����
			
			//String���� ����ϱ� ����
			StringBuilder sb = new StringBuilder(output.length * 2); 
		       for(byte b: output)
		          sb.append(String.format("%02X ", b)); //�ڿ� �̾� ���̸鼭 ���
		       outPut = sb.toString(); 
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("HMAC : " + outPut); //��� �� ���
	
	}
	
	public static byte[] MD5(byte[] str){
		byte[] MD5 = null; 
		
		try{ //�޽��� ��������Ʈ ����
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(str); 
		MD5 = md.digest(); //�޽��� ��������Ʈ�� MD5�� �־���
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			MD5 = null; 
		}
		return MD5; //�޽��� ��������Ʈ ����
	}
	
	public static byte[] HMAC_MD5(byte[] key, byte[] PlainText) {
		byte[] Key = new byte[BLOCK_SIZE]; //Key�� ��ϻ����� ũ�⸸ŭ ����
		if(key.length>BLOCK_SIZE)  //���ڷ� ���� key�� ��ϻ����� ���� �������
			key = MD5(key); //�ؽ��Լ��� ���
		
		for(int i = 0; i<key.length; i++) //���ڷι��� key�Ǵ� �ؽ��Լ��κ��� ���� key�� Key���� ���� 
			Key[i] = key[i];
		
		if(Key.length < BLOCK_SIZE) { //Key���� ������ ���� ���̰� ��ϻ������ ���� ���
			for(int i = key.length; i<BLOCK_SIZE; i++)
				Key[i] = (byte)0x00; //������ �κ��� 0���� �е�
		}
		
		byte[] o_key_pad = new byte[BLOCK_SIZE]; //key�� xor������ �� outer pad
		byte[] i_key_pad = new byte[BLOCK_SIZE]; //key�� xor������ �� inner pad
		
		for (int i=0;i<BLOCK_SIZE;i++) {
			o_key_pad[i] = (byte)((byte)0x5c ^ Key[i]); //outer pad�� 0x5c�� �е��� �ϸ鼭 Key���� xor (�е��� ���� xor�����ϴ°Ͱ� ���� ���)
			i_key_pad[i] = (byte)((byte)0x36 ^ Key[i]); //inner pad�� ox36���� �е��� �ϸ鼭 Key���� xor
		}
		
		byte[]ipadOutput = new byte[BLOCK_SIZE + PlainText.length]; //inner pad�� ���� ���� ����� ��� ���� 
		
		for (int i=0; i<BLOCK_SIZE; i++)
			ipadOutput[i] = i_key_pad[i]; //key�� inner pad�� xor�� ���� ����
		for (int i=0; i<PlainText.length; i++)
			ipadOutput[i+BLOCK_SIZE] = PlainText[i]; //������ �� �ڿ� plaintext�� ������
		
		ipadOutput= MD5(ipadOutput); //hash�Լ��� �־���
		
		byte[] opadOutput = new byte[o_key_pad.length + ipadOutput.length]; //hash�Լ��� �� �Ͱ� key�� xor�� outer pad�� ���� ����� ��� ����
		
		for (int i=0; i<o_key_pad.length; i++)
			opadOutput[i] = o_key_pad[i]; //outer pad�� key�� xor�� ���� ����
		for (int i=0; i<ipadOutput.length; i++)
			opadOutput[i+o_key_pad.length] = (byte)ipadOutput[i]; //�� �ڿ� hash�Լ��� ���� key+inner pad�� ���ٿ���
		
		return MD5(opadOutput); //�� ���� ���� hash�� ������ ����
		
	}

}
