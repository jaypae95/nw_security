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
		// 코드 작성하기
		
		for(int i = 0; i < plan.length(); i++) { //for문을 plaintext의 길이 만큼 반복한다
			if(word.indexOf(Plan.charAt(i)) != -1) { //만약 plaintext의 i번째 값이 알파벳이면
				result += word.charAt((word.indexOf(Plan.charAt(i)) + Key) % 26); //result에다가 plaintext의 글자
													//의 다음 두번째 알파벳을 넣고(Z를 넘어가는 것을 대비해 %26을 사용)
			}
			else result += Plan.charAt(i); //알파벳이 아니면(스페이스 바, . 등등) plaintext의 문자를 바꾸지않고 그대로 넣는다
		}
		/* ************************************************
		 indexOf라는 함수로 알파벳이 몇번째에 있는지 찾는다. 예를 들면 word라는 스트링에 첫번째에 있으니까 1이고 B는 2이다.
		 word에 없을 경우 -1을 반환하므로 -1이 아닐 경우 +Key 처리를 해준다.
		 word.charAt((word.indexOf(Plan.charAt(i)) + Key ) % 26) 을 통해
		 plaintext의 알파벳이 word스트링에서 몇번째에 있는지 찾고 그 값에다 + Key를 해준 후 % 26을 해줘서 26이 넘어가는 27 28 등을 1 2 로 만들어 주었다.
		 그렇게 +Key를 해준 값이 word에서 어떤 알파벳을 가리키는지 찾아서 result 값에 넣어주었다.
		 예를 들면 plaintext에서 처음 T는 20번째이다. 20 + 2 를 한 것을 word에서 찾아보면 다다음 알파벳인 V이므로 V를 반환해준다.
		 * 
		 ************************************************* */

		return result; //결과 값 반환
	}


}
