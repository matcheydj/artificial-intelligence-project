package ro.aligotec.ai.utils;

public class STRING {
	
	
	public static String repeat(String str, int numOfTimes){
		StringBuffer r = new StringBuffer();
		for(int i = 0; i < numOfTimes; i++){
			r.append(str);
		}
		return r.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
