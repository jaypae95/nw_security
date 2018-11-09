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
			PlainText = plaintext.getBytes("ASCII"); //byte형으로 변환
			Key = key.getBytes("ASCII"); //byte형으로 변환
			
			output = HMAC_MD5(Key, PlainText); //MD5를 사용하는 HMAC 함수 결과 값을 output에 저장
			
			//String으로 출력하기 위함
			StringBuilder sb = new StringBuilder(output.length * 2); 
		       for(byte b: output)
		          sb.append(String.format("%02X ", b)); //뒤에 이어 붙이면서 출력
		       outPut = sb.toString(); 
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("HMAC : " + outPut); //결과 값 출력
	
	}
	
	public static byte[] MD5(byte[] str){
		byte[] MD5 = null; 
		
		try{ //메시지 다이제스트 생성
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(str); 
		MD5 = md.digest(); //메시지 다이제스트를 MD5에 넣어줌
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			MD5 = null; 
		}
		return MD5; //메시지 다이제스트 리턴
	}
	
	public static byte[] HMAC_MD5(byte[] key, byte[] PlainText) {
		byte[] Key = new byte[BLOCK_SIZE]; //Key를 블록사이즈 크기만큼 선언
		if(key.length>BLOCK_SIZE)  //인자로 받은 key가 블록사이즈 보다 작을경우
			key = MD5(key); //해쉬함수를 사용
		
		for(int i = 0; i<key.length; i++) //인자로받은 key또는 해쉬함수로부터 얻은 key를 Key에다 복사 
			Key[i] = key[i];
		
		if(Key.length < BLOCK_SIZE) { //Key에다 복사한 것의 길이가 블록사이즈보다 작을 경우
			for(int i = key.length; i<BLOCK_SIZE; i++)
				Key[i] = (byte)0x00; //나머지 부분을 0으로 패딩
		}
		
		byte[] o_key_pad = new byte[BLOCK_SIZE]; //key와 xor연산을 할 outer pad
		byte[] i_key_pad = new byte[BLOCK_SIZE]; //key와 xor연산을 할 inner pad
		
		for (int i=0;i<BLOCK_SIZE;i++) {
			o_key_pad[i] = (byte)((byte)0x5c ^ Key[i]); //outer pad를 0x5c로 패딩을 하면서 Key값과 xor (패딩을 한후 xor연산하는것과 같은 결과)
			i_key_pad[i] = (byte)((byte)0x36 ^ Key[i]); //inner pad를 ox36으로 패딩을 하면서 Key값과 xor
		}
		
		byte[]ipadOutput = new byte[BLOCK_SIZE + PlainText.length]; //inner pad와 평문을 붙인 결과를 담기 위함 
		
		for (int i=0; i<BLOCK_SIZE; i++)
			ipadOutput[i] = i_key_pad[i]; //key와 inner pad를 xor한 것을 복사
		for (int i=0; i<PlainText.length; i++)
			ipadOutput[i+BLOCK_SIZE] = PlainText[i]; //복사한 것 뒤에 plaintext를 덧붙임
		
		ipadOutput= MD5(ipadOutput); //hash함수에 넣어줌
		
		byte[] opadOutput = new byte[o_key_pad.length + ipadOutput.length]; //hash함수에 들어간 것과 key와 xor한 outer pad를 붙인 결과를 담기 위함
		
		for (int i=0; i<o_key_pad.length; i++)
			opadOutput[i] = o_key_pad[i]; //outer pad와 key를 xor한 것을 복사
		for (int i=0; i<ipadOutput.length; i++)
			opadOutput[i+o_key_pad.length] = (byte)ipadOutput[i]; //그 뒤에 hash함수로 나온 key+inner pad를 덧붙여줌
		
		return MD5(opadOutput); //다 붙인 것을 hash로 돌린후 리턴
		
	}

}
