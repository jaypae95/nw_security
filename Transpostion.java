
public class Transpostion {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String PlanText = "Common sense is not so common.";
		String rst = transposition_encrypt(PlanText, 8);

		System.out.println(PlanText);
		System.out.println(rst);

	}
	
	public static String transposition_encrypt(String plan, int Key) {
		int col = 0;
		String result = "";
		String row;
		char tmp;
		
		//�ڵ� �ۼ��ϱ�
		
		for(int i=0; i < Key; i++) { //for���� key����ŭ �ݺ�
			for (int j = 0; j <= (plan.length() / Key); j ++) {	//for���� (plaintext�� ���� / key)����ŭ �ݺ�
				if ((i+Key*j)>=plan.length()) break;  //���� (i+Key*j)�� plaintext�� ���̺��� Ŀ���� break;
				result += (char)plan.charAt(i+(Key*j)); // plaintext�� i+Key*j��° ���ĺ��� ���ʷ� result�����ٰ� ����
				/* ***********************************************************
				Common sense is not so common. ��
				key�� 8��ŭ �����ϰ� �����ٷ� �Ѿ�� �����ϰ� �ϸ�
				
				Common s
				ense is 
				not so c
				ommon.    �� �ȴ� ���⼭ ó�� Ceno�� ����Ϸ��� ������ 1��° 9��° 17��° 25��°�� result���� ������ �ȴ�.(���ι���)
						  string�� ù��° index���� 1�� �ƴ� 0 �̹Ƿ� 0��° 8��° 16��° 24��°��� �ϰ�
						  i+Key*j�� �ϸ� ó�� j for���� ���� 0+8*0 = 0��°, 0+8*1 = 8��°
						  �̷��� ���� ���̴�. �׷��ٰ� i+Key+j�� 30�� 31�� �� �� �� �ִµ� �̶� ������ ���̺��� �Ѿ�Ƿ� ����������.
						  �׷��� if���� ���� ����ó���� ���־���.
				************************************************************* */
				
			}
		
		}
		
		
		
		return result;
	}

}
