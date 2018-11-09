import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

	public static SecretKey key;

	public static void main(String[] args) throws Exception {

		double begin = System.currentTimeMillis();
		// TODO Auto-generated method stub
		byte[] k = { (byte) 0xFD, (byte) 0xE8, (byte) 0xF7, (byte) 0xA9, (byte) 0xB8, 0x6C, 0x3B, (byte) 0xFF,
				(byte) 0x07, (byte) 0xC0, (byte) 0xD3, (byte) 0x9D, (byte) 0x04, (byte) 0x60, (byte) 0x5E,
				(byte) 0xDD };
		key = new SecretKeySpec(k, 0, k.length, "AES");
		System.out.println(AES_Decrypt(AES_Encrypt("HELLO WORLD! BYE")));


		double end = System.currentTimeMillis();
		System.out.println((end-begin)/1000 + " sec");
	}

	public static String AES_Encrypt(String data) throws Exception {
		if (data == null || data.length() == 0)
			return "";

		Cipher cipher = Cipher.getInstance("AES");

		cipher.init(Cipher.ENCRYPT_MODE, key);

		byte[] inputBytes1 = data.getBytes("ASCII");
		byte[] outputBytes1 = cipher.doFinal(inputBytes1);
		
		System.out.println(byteArrayToHex(outputBytes1));

		sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
		return encoder.encode(outputBytes1);

	}

	public static String AES_Decrypt(String data) throws Exception {
		if (data == null || data.length() == 0)
			return "";

		Cipher cipher = Cipher.getInstance("AES");

		cipher.init(Cipher.DECRYPT_MODE, key);

		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
		byte[] inputBytes1 = decoder.decodeBuffer(data);
		byte[] outputBytes2 = cipher.doFinal(inputBytes1);
		return new String(outputBytes2, "ASCII");
	}

	public static String byteArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder(a.length * 2);
		for (byte b : a)
			sb.append(String.format("%02X ", b & 0xff));
		return sb.toString();
	}

}
