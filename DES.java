import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class DES {
	public static SecretKey key;

	public static void main(String[] args) {
		double begin = System.currentTimeMillis();
		// TODO Auto-generated method stub
		byte[] k = {(byte)0xA1,(byte)0xB1,(byte) 0xC1,0x11,0x11,0x11,0x11,0x11};
		key = new SecretKeySpec(k, 0, k.length, "DES");
		
		try {
			System.out.println(DES_decrypt(DES_encrypt("HELLO WORLD! BYE")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double end = System.currentTimeMillis();
		System.out.println((end-begin)/1000 + " sec");


	}
	
	public static String DES_encrypt(String data) throws Exception { 
        if (data == null || data.length() == 0) 
            return ""; 

        Cipher cipher = Cipher.getInstance("DES"); 
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] inputBytes1 = data.getBytes("ASCII"); 
        byte[] outputBytes1 = cipher.doFinal(inputBytes1);
        
        System.out.println(byteArrayToHex(outputBytes1));
        
        sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder(); 
        return encoder.encode(outputBytes1); 
    }
	public static String DES_decrypt(String data) throws Exception {
        if (data == null || data.length() == 0) 
            return ""; 
        javax.crypto.Cipher cipher = javax.crypto.Cipher 
                .getInstance("DES");
        
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key);
              
        sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder(); 
        byte[] inputBytes1 = decoder.decodeBuffer(data);
        byte[] outputBytes2 = cipher.doFinal(inputBytes1); 
        return new String(outputBytes2, "ASCII");
    }
	public static String byteArrayToHex(byte[] a) {
	       StringBuilder sb = new StringBuilder(a.length * 2);
	       for(byte b: a)
	          sb.append(String.format("%02X ", b & 0xff));
	       return sb.toString();
	    }

	



}
