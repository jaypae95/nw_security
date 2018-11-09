
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
	public static void main(String[] args) {
		String output;
		output = MD5("kakao talk application");
		System.out.println("MD5 : " + output);
		output = SHA_1("kakao talk application");
		System.out.println("SHA-1 : " + output);
		output = SHA_256("kakao talk application");
		System.out.println("SHA-256 : " + output);
		output = SHA_512("kakao talk application");
		System.out.println("SHA-512 : " + output);
	}
	
	public static String MD5(String str){
		String MD5 = ""; 
		try{
		MessageDigest md = MessageDigest.getInstance("MD5"); 
		md.update(str.getBytes()); 
		byte byteData[] = md.digest();

		StringBuffer sb = new StringBuffer(); 
		for(int i = 0 ; i < byteData.length ; i++){
		sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
		}
			MD5 = sb.toString();

		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			MD5 = null; 
		}
		return MD5;
		}
	public static String SHA_1(String str){
		String SHA_1 = ""; 
		try{
		MessageDigest sha_1 = MessageDigest.getInstance("SHA-1"); 
		sha_1.update(str.getBytes()); 
		byte byteData[] = sha_1.digest();

		StringBuffer sb = new StringBuffer(); 
		for(int i = 0 ; i < byteData.length ; i++){
		sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
		}
			SHA_1 = sb.toString();

		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			SHA_1 = null; 
		}
		return SHA_1;
		}
	
	public static String SHA_256(String str){
		String SHA_256 = ""; 
		try{
		MessageDigest sha_256 = MessageDigest.getInstance("SHA-256"); 
		sha_256.update(str.getBytes()); 
		byte byteData[] = sha_256.digest();

		StringBuffer sb = new StringBuffer(); 
		for(int i = 0 ; i < byteData.length ; i++){
		sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
		}
			SHA_256 = sb.toString();

		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			SHA_256 = null; 
		}
		return SHA_256;
		}
	
	public static String SHA_512(String str){
		String SHA_512 = ""; 
		try{
		MessageDigest sha_512 = MessageDigest.getInstance("SHA-512"); 
		sha_512.update(str.getBytes()); 
		byte byteData[] = sha_512.digest();

		StringBuffer sb = new StringBuffer(); 
		for(int i = 0 ; i < byteData.length ; i++){
		sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
		}
		SHA_512 = sb.toString();

		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			SHA_512 = null; 
		}
		return SHA_512;
		}
}
