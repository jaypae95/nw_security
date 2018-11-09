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
				byte[] output = cipher.doFinal(Arrays.copyOfRange(plainText, i * 16, (i + 1) * 16)); //���� 16���� ��� ��ȣȭ �� �� output�� �־���

				for (int j = 0; j < 16; j++) //result���� �־���
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
					IV[j] = (byte)(PainText[j + i*16] ^ IV[j]); //���� 16���� ���� �� iv�� xor������ ���ְ� �� ���� �ٽ� iv�� �־��ش�
							                                    //iv�� �־��ִ� ������ ���� ��Ͽ��� �� ���� �򹮰� xor�� ���ֱ����ؼ�
				IV = cipher.doFinal(Arrays.copyOfRange(IV, 0, 16)); // ���� ����� ��ȣȭ ����
				for (int j = 0; j < 16; j++)  
					result[j + i * 16] = IV[j]; //�� ���� reuslt�� �־���

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
				IV = cipher.doFinal(Arrays.copyOfRange(IV, 0, 16)); //iv�� ��ȣȭ�ؼ� iv�� �־���(���� ��� �� iv�� ���� ��ȣȭ���ֱ����� iv�� ����)
				for(int j = 0; j<16; j++)
					IV[j] = (byte)(PainText[j + i*16] ^ IV[j]); //���� 16���� ��� ��ȣȭ�� ���� xor���ش�
				for (int j = 0; j < 16; j++) 
					result[j + i * 16] = IV[j]; //xor���� ���� result�� �־��ش�.
				

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
				nonce[nonce.length-1] = (byte)(0x00+i); //nonce ���� ������ 00 00 00 00 �� ī�����̴� ���� �� ������ 00�� i����ŭ ���������ش�.
				byte[] output = cipher.doFinal(Arrays.copyOfRange(nonce, 0, 16)); //���������� ��(ó���� 0 ����)�� ��ȣȭ�Ѵ�.
				for(int j = 0; j<16; j++)
					result[j + i * 16] = (byte)(PainText[j + i*16] ^ output[j]); //��ȣȭ�� ���� result�� �־��ش�.

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