public class Caesar {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String PlanText="The quick brown fox jumps over the lazy dog.";
		String CiperText= Caesar(PlanText,2,"encrypt");
		String rst= Caesar(CiperText,2,"decrypt");
		System.out.println(PlanText);
		System.out.println(CiperText);
		System.out.println(rst);
	}
	
	public static String Caesar(String plan, int Key, String mode) {
		String word = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String Plan = plan.toUpperCase();
		String result = "";

		if (mode.equals("decrypt"))
			Key = (word.length()- Key)%word.length();
		else if (mode.equals("encrypt"))
			Key = Key%word.length();
		else
			return "err";	
		// �ڵ� �ۼ��ϱ�
		
		for(int i = 0; i < plan.length(); i++) { //for���� plaintext�� ���� ��ŭ �ݺ��Ѵ�
			if(word.indexOf(Plan.charAt(i)) != -1) { //���� plaintext�� i��° ���� ���ĺ��̸�
				result += word.charAt((word.indexOf(Plan.charAt(i)) + Key) % 26); //result���ٰ� plaintext�� ����
													//�� ���� �ι�° ���ĺ��� �ְ�(Z�� �Ѿ�� ���� ����� %26�� ���)
			}
			else result += Plan.charAt(i); //���ĺ��� �ƴϸ�(�����̽� ��, . ���) plaintext�� ���ڸ� �ٲ����ʰ� �״�� �ִ´�
		}
		/* ************************************************
		 indexOf��� �Լ��� ���ĺ��� ���°�� �ִ��� ã�´�. ���� ��� word��� ��Ʈ���� ù��°�� �����ϱ� 1�̰� B�� 2�̴�.
		 word�� ���� ��� -1�� ��ȯ�ϹǷ� -1�� �ƴ� ��� +Key ó���� ���ش�.
		 word.charAt((word.indexOf(Plan.charAt(i)) + Key ) % 26) �� ����
		 plaintext�� ���ĺ��� word��Ʈ������ ���°�� �ִ��� ã�� �� ������ + Key�� ���� �� % 26�� ���༭ 26�� �Ѿ�� 27 28 ���� 1 2 �� ����� �־���.
		 �׷��� +Key�� ���� ���� word���� � ���ĺ��� ����Ű���� ã�Ƽ� result ���� �־��־���.
		 ���� ��� plaintext���� ó�� T�� 20��°�̴�. 20 + 2 �� �� ���� word���� ã�ƺ��� �ٴ��� ���ĺ��� V�̹Ƿ� V�� ��ȯ���ش�.
		 * 
		 ************************************************* */

		return result; //��� �� ��ȯ
	}


}
