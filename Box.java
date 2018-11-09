public class Box {
	
	public static void main(String[] args) {
		String PlanText = "ABCDEFGH";
		int[] index1 = { 2, 6, 3, 1, 4, 8, 5, 7 };
		int[] index2 = { 4, 1, 3, 5, 7, 2, 8, 6 };
		String rst1 = PBox(PlanText,index1);
		String rst2 = PBox(rst1,index2);		
		System.out.println(rst1);
		System.out.println(rst2);

	}
	public static String PBox(String P, int[] index) {
		String result = "";
		if (P.length() != 8)
			return "err";

		for(int i = 0; i < 8; i++) { //plain text길이만큼 for문을 돌린다.
			result += P.charAt(index[i]-1); //plain text의 index[i]번째 값을 뽑아서 result에다가 넣어준다.
		}

		return result;
	}

}
