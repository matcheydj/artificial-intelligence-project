package ro.aligotec.ai;

public class TestingSubversion {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "<a> Agent </a>Bsdsa";
		System.out.println(s + "\n"
				+ (s.toLowerCase().indexOf("<a>")+3) + "->"
				+ s.toLowerCase().indexOf("</a>"));
	}

}
