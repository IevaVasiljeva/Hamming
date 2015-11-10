import java.util.Arrays;

public class ExtendedHammingCode extends HammingCode{
	
	private final boolean[][] parityCheckMatrix;
	private final int totalLength;
	

	public ExtendedHammingCode(int sizeParameter) {
		super(sizeParameter);

		this.totalLength = super.totalLength + 1;
		this.parityCheckMatrix = getExtendParityCheckMatrix();
		
		System.out.println("\nParity check matrix for the extended Hamming code [" + totalLength + "," + dataLength + "," + sizeParameter + 1 + "]:\n");
		printMatrix(parityCheckMatrix);
	}
	
	// Extend the parity check matrix by adding a column of zeros on the left hand side and a row of ones at the bottom
	private boolean[][] getExtendParityCheckMatrix() {
		
		boolean[][] extendedMatrix = new boolean[this.totalLength][this.sizeParameter + 1];
		
		// Add a row of all 0s to the top
		for (int columnIndex = 0; columnIndex < this.sizeParameter; columnIndex ++) {
			extendedMatrix[0][columnIndex] = false;
		}
		
		// Add one to the end of the first row
		extendedMatrix[0][this.sizeParameter] = true;
		
		// Go through the basic Hamming code parity check matrix row by row
		for (int rowIndex = 1; rowIndex < this.totalLength; rowIndex++) {
			// Add one to the end of each row
			extendedMatrix[rowIndex][this.sizeParameter] = true;
			// Copy the rest of the row from the basic Hamming code parity check matrix
			for (int columnIndex = 0; columnIndex < this.sizeParameter; columnIndex ++) {
				extendedMatrix[rowIndex][columnIndex] = super.parityCheckMatrix[rowIndex-1][columnIndex];
			}
		}
		
		return extendedMatrix;
	}
	
	// Encodes a source information by taking the encoding provided by the basic Hamming code and adding a parity check bit (checking all of the code bits) to the end
	public boolean[] encodeSource(boolean[] source) {
		
		boolean[] extendedEncoding = new boolean[totalLength];
		boolean[] basicEncoding = super.encodeSource(source);
		
		boolean parityCheckBit = false;
		
		for (int index = 0; index < totalLength -1; index++) {
			// Calculate the check bit by XORing all of the bits
			parityCheckBit = parityCheckBit^basicEncoding[index];
			// Copy the bits
			extendedEncoding[index] = basicEncoding[index];
		}
		
		// Add the parity check bit
		extendedEncoding[totalLength-1] = parityCheckBit;
		
		return extendedEncoding;
	}

	
	public boolean[] findSourceWord (boolean[] codeWord) {
		
		boolean[] result = new boolean[dataLength];
		
		// Find the syndrome and determine the bit that is corrupted
		String syndrome = computeSyndrome(codeWord, this.parityCheckMatrix);
		int errorBit = Integer.parseInt(syndrome, 2) -1;

		// If the syndrome is not 0, correct the error bit
		if (errorBit>-1) {
			boolean[] boolSyndrome = new boolean[syndrome.length()];
			for (int index = 0; index < syndrome.length(); index++) {
				boolSyndrome[index] = syndrome.charAt(index) == '1';
			}
			// Check whether the syndrome equals a row of the parity check matrix - if so, one error is detected and can be corrected
			boolean foundMatch = false;
			for (int index = 0; index < this.totalLength; index++) {
				if (Arrays.equals(boolSyndrome, parityCheckMatrix[index])) {
					foundMatch = true;
					codeWord[index] = !codeWord[index];
				}
			}
			// Otherwise two errors have been detected
			if (! foundMatch) {
				System.out.println("2 errors detected!");
			}
		}

		// Take the part of the codeword that represents the source (leave out the check bits)
		for (int index = 0; index < dataLength; index++) {
			result[index] = codeWord[index];
		}
		
		return result;
	}
	
	public int getTotalLength() {
		return totalLength;
	}
	
}
