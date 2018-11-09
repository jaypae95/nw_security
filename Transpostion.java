
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
		
		//코드 작성하기
		
		for(int i=0; i < Key; i++) { //for문을 key값만큼 반복
			for (int j = 0; j <= (plan.length() / Key); j ++) {	//for문을 (plaintext의 길이 / key)값만큼 반복
				if ((i+Key*j)>=plan.length()) break;  //만약 (i+Key*j)가 plaintext의 길이보다 커지면 break;
				result += (char)plan.charAt(i+(Key*j)); // plaintext의 i+Key*j번째 알파벳을 차례로 result값에다가 넣음
				/* ***********************************************************
				Common sense is not so common. 을
				key값 8만큼 나열하고 다음줄로 넘어가서 나열하고 하면
				
				Common s
				ense is 
				not so c
				ommon.    이 된다 여기서 처음 Ceno를 출력하려면 문자의 1번째 9번째 17번째 25번째를 result값에 넣으면 된다.(세로방향)
						  string의 첫번째 index값은 1이 아닌 0 이므로 0번째 8번째 16번째 24번째라고 하고
						  i+Key*j를 하면 처음 j for문이 돌때 0+8*0 = 0번째, 0+8*1 = 8번째
						  이렇게 가는 것이다. 그러다가 i+Key+j가 30과 31이 될 때 가 있는데 이땐 문장의 길이보다 넘어가므로 에러가난다.
						  그래서 if문을 통해 예외처리를 해주었다.
				************************************************************* */
				
			}
		
		}
		
		
		
		return result;
	}

}
