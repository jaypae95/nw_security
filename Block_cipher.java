import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Block_cipher {
	public static void main(String[] args) {
		byte[] k = { (byte) 0xFD, (byte) 0xE8, (byte) 0xF7, (byte) 0xA9, (byte) 0xB8, 0x6C, 0x3B, (byte) 0xFF,
				(byte) 0x07, (byte) 0xC0, (byte) 0xD3, (byte) 0x9D, (byte) 0x04, (byte) 0x60, (byte) 0x5E,
				(byte) 0xDD };
		byte[] iv = { (byte) 0xFD, (byte) 0xE8, (byte) 0xF7, (byte) 0xA9, (byte) 0xB8, 0x6C, 0x3B, (byte) 0xFF,
				(byte) 0x07, (byte) 0xC0, (byte) 0xD3, (byte) 0x9D, (byte) 0x04, (byte) 0x60, (byte) 0x5E,
				(byte) 0xDD };
		byte[] nonce = { (byte) 0xFD, (byte) 0xE8, (byte) 0xF7, (byte) 0xA9, (byte) 0xB8, 0x6C, 0x3B, (byte) 0xFF,
				(byte) 0x07, (byte) 0xC0, (byte) 0xD3, (byte) 0x9D, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00 };

		String plantext = "The top half of the workspace consists of an arrangement of other existing components to calculate an MD5-HMAC. "
				+ "These individual components are labelled to show which part of the final HMAC output they generate."
				+ " In order for this arrangement to generate a correct result, the HMAC key chosen must have a length of exactly 64 bytes.";
		byte[] PlainText = null;

		try {
			PlainText = plantext.getBytes("ASCII");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Key   : " + byteArrayToHex(k));
		System.out.println("iv    : " + byteArrayToHex(iv));
		System.out.println("nonce : " + byteArrayToHex(nonce));
		System.out.println();

		// padding
		int padding_length = 16 - (PlainText.length % 16);
		byte[] padding_result = new byte[PlainText.length + padding_length];

		for (int i = 0; i < PlainText.length; i++)
			padding_result[i] = PlainText[i];

		for (int i = PlainText.length; i < padding_result.length; i++)
			padding_result[i] = (byte) padding_length;
		System.out.println("Padding  : " + byteArrayToHex(padding_result));
		System.out.println();
		
		System.out.println("ECB : " + byteArrayToHex(ENC_AES128_ECB_PKCS7Padding(k, padding_result)));
		System.out.println("CBC : " + byteArrayToHex(ENC_AES128_CBC_PKCS7Padding(iv, k, padding_result)));
		System.out.println("CFB : " + byteArrayToHex(ENC_AES128_CFB_PKCS7Padding(iv, k, padding_result)));
		System.out.println("CTR : " + byteArrayToHex(ENC_AES128_CTR_PKCS7Padding(nonce, k, padding_result)));

	}

	public static byte[] ENC_AES128_ECB_PKCS7Padding(byte[] key, byte[] plainText) {
		SecretKey Key;
		Key = new SecretKeySpec(key, 0, key.length, "AES");
		Cipher cipher = null;

		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, Key);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int block_count = plainText.length / 16;
		byte[] result = new byte[block_count * 16];

		for (int i = 0; i < block_count; i++) {
			try {
				byte[] output = cipher.doFinal(Arrays.copyOfRange(plainText, i * 16, (i + 1) * 16)); //평문을 16개씩 끊어서 암호화 한 후 output에 넣어줌

				for (int j = 0; j < 16; j++) //result에다 넣어줌
					result[j + i * 16] = output[j];

			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	public static byte[] ENC_AES128_CBC_PKCS7Padding(byte[] iv, byte[] key, byte[] plainText) {
		SecretKey Key;
		Key = new SecretKeySpec(key, 0, key.length, "AES");
		Cipher cipher = null;
		byte[] IV = Arrays.copyOfRange(iv, 0, iv.length);
		byte[] PainText = Arrays.copyOfRange(plainText, 0, plainText.length);

		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, Key);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int block_count = PainText.length / 16;
		byte[] result = new byte[block_count * 16];

		for (int i = 0; i < block_count; i++) {
			try {
				for(int j = 0; j<16; j++)
					IV[j] = (byte)(PainText[j + i*16] ^ IV[j]); //평문을 16개씩 끊은 후 iv와 xor연산을 해주고 그 값을 다시 iv에 넣어준다
							                                    //iv에 넣어주는 이유는 다음 블록연산 때 다음 평문과 xor을 해주기위해서
				IV = cipher.doFinal(Arrays.copyOfRange(IV, 0, 16)); // 위의 결과를 암호화 해줌
				for (int j = 0; j < 16; j++)  
					result[j + i * 16] = IV[j]; //그 값을 reuslt에 넣어줌

			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	public static byte[] ENC_AES128_CFB_PKCS7Padding(byte[] iv, byte[] key, byte[] plainText) {
		SecretKey Key;
		Key = new SecretKeySpec(key, 0, key.length, "AES");
		Cipher cipher = null;
		byte[] IV = Arrays.copyOfRange(iv, 0, iv.length);
		byte[] PainText = Arrays.copyOfRange(plainText, 0, plainText.length);
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, Key);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int block_count = PainText.length / 16;
		byte[] result = new byte[block_count * 16];

		for (int i = 0; i < block_count; i++) {
			try {
				IV = cipher.doFinal(Arrays.copyOfRange(IV, 0, 16)); //iv를 암호화해서 iv에 넣어줌(다음 블록 때 iv의 값을 암호화해주기위해 iv에 넣음)
				for(int j = 0; j<16; j++)
					IV[j] = (byte)(PainText[j + i*16] ^ IV[j]); //평문을 16개씩 끊어서 암호화된 값과 xor해준다
				for (int j = 0; j < 16; j++) 
					result[j + i * 16] = IV[j]; //xor해준 값을 result에 넣어준다.
				

			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	public static byte[] ENC_AES128_CTR_PKCS7Padding(byte[] nonce, byte[] key, byte[] plainText) {
		SecretKey Key;
		Key = new SecretKeySpec(key, 0, key.length, "AES");
		Cipher cipher = null;

		byte[] PainText = Arrays.copyOfRange(plainText, 0, plainText.length);
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, Key);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int block_count = PainText.length / 16;
		byte[] result = new byte[block_count * 16];

		for (int i = 0; i < block_count; i++) {
			try {
				nonce[nonce.length-1] = (byte)(0x00+i); //nonce 값중 마지막 00 00 00 00 은 카운터이다 그중 맨 마지막 00에 i값만큼 증가시켜준다.
				byte[] output = cipher.doFinal(Arrays.copyOfRange(nonce, 0, 16)); //증가시켜준 값(처음엔 0 증가)을 암호화한다.
				for(int j = 0; j<16; j++)
					result[j + i * 16] = (byte)(PainText[j + i*16] ^ output[j]); //암호화된 값을 result에 넣어준다.

			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	public static String byteArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder(a.length * 2);
		for (byte b : a)
			sb.append(String.format("%02X ", b & 0xff));
		return sb.toString();
	}

}