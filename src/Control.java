
public class Control {
	
	public static void main(String[] args) {
		
		ExtendedHammingCode he3 = new ExtendedHammingCode(3);
		StreamReceiver receiver = new StreamReceiver(3, 6, he3);
		StreamGenerator generator = new StreamGenerator(3, 6, 0.7, 0.08, 0.9, receiver, he3);
		
		generator.transmitCode();
		
		receiver.decodeData();
		
		
		
		
//		boolean[] codeWord = {true, false, false, true};
//		
////		boolean[] encodedWord = he3.encodeSource(codeWord);
//		boolean[] encodedWord = {true, true, false, true, true, false, false};
//
//		
//		System.out.println();
//		System.out.println("Encoded:");
//		he3.printCodeWord(encodedWord);
//		
//		String syndrome = he3.computeSyndrome(encodedWord);
//
//		
//		System.out.println();
//		System.out.println();
//		System.out.println("Syndrome:");
//		System.out.println(syndrome);
	}
	
	
	
	
	public void test() {
		boolean[] t1 = {true, false, true};
		boolean[] t2 = {true, false, true};
		System.out.println(t1.equals(t2));
 	}

}
