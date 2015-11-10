import java.util.HashMap;

// Class providing methods for creating a Hamming code with the given size parameter, as well as encoding and decoding using this code
public class HammingCode {

	// Corresponds to the number of check bits in a code word
	protected final int sizeParameter;
	// Full length of a code word
	protected final int totalLength;
	// Length of a source word
	protected final int dataLength;
	protected boolean[][] parityCheckMatrix;
	protected final boolean[][] generatorMatrix;
	// Mapping between integer and its corresponding position in the code word
	protected HashMap<Integer, Integer> integerPositionMapping = new HashMap<>();

	public HammingCode(int sizeParameter) {
		this.sizeParameter = sizeParameter;
		// Length of a Hamming code is 2^sizePar-1 and dimension is 2^sizePar-sizePar-1
		totalLength = (int) (Math.pow(2, sizeParameter)) - 1;
		dataLength = (int)(Math.pow(2, sizeParameter)) - sizeParameter -1;
		
		parityCheckMatrix = getParityCheckMatrix();
		System.out.println("Parity check matrix for Hamming code [" + totalLength + "," + dataLength + "," + sizeParameter + "]:\n");
		printMatrix(parityCheckMatrix);

		generatorMatrix = getGeneratorMatrix();
		System.out.println("\n\nGenerator matrix for Hamming code [" + totalLength + "," + dataLength + "," + sizeParameter + "]:\n");
		printMatrix(generatorMatrix);
	}


	// Obtains a parity check matrix by taking the binary representations of the integers from 1 to the length of a code word, and ordering them in the matrix
	private boolean[][] getParityCheckMatrix () {

		boolean[][] result = new boolean[totalLength][sizeParameter];
		int skippedNumbers = 0;
		boolean powOfTwo = false;

		// Go through integers from 1 to the length of a code word
		for (int rowIndex = 0; rowIndex<totalLength; rowIndex++) {
			powOfTwo = false;
			int currentInt = rowIndex + 1;

			// Numbers that are power of two are the check bits and will be added to the
			if ((currentInt & (currentInt - 1))==0) {
				skippedNumbers++;
				powOfTwo = true;
			}

			// Get binary representation of the number, and pad with zeros to get the necessary length
			String binaryCurrent = Integer.toBinaryString(currentInt);
			while(binaryCurrent.length()<sizeParameter) {
				binaryCurrent = "0" + binaryCurrent;
			}

			// Add the obtained binary number to the matrix bit by bit
			for (int columnIndex = 0; columnIndex<sizeParameter; columnIndex++){
				// If the number is not a power of two, add it to the beginning 
				if (!powOfTwo) {
					// "1" is represented by "true", and "0" by "false
					result[rowIndex-skippedNumbers][columnIndex] = (binaryCurrent.charAt(columnIndex) == '1');	
					// Keep track of where the particular number is stored
					integerPositionMapping.put(rowIndex, rowIndex-skippedNumbers);
				}
				// If the number is a power of two, add it to the end
				else {
					// "1" is represented by "true", and "0" by "false
					result[totalLength-skippedNumbers][columnIndex] = (binaryCurrent.charAt(columnIndex) == '1');
					// Keep track of where the particular number is stored
					integerPositionMapping.put(rowIndex, totalLength-skippedNumbers);
				}
			}			
		}
		return result;
	}


	// Obtains a generator matrix from the parity check matrix
	private boolean[][] getGeneratorMatrix () {

		boolean[][] result = new boolean[dataLength][totalLength];

		// Take the rows corresponding to integers that are not of a power of two, and add them to the end
		// The beginning of the matrix consists of a unity matrix
		for (int rowIndex = 0; rowIndex < dataLength; rowIndex ++) {
			for (int columnIndex = 0; columnIndex < totalLength - sizeParameter; columnIndex++) {
				result[rowIndex][columnIndex] = (columnIndex == rowIndex);
			}
			for (int columnIndex = totalLength-sizeParameter; columnIndex < totalLength; columnIndex++) {
				result[rowIndex][columnIndex] = parityCheckMatrix[rowIndex][columnIndex-totalLength+sizeParameter];
			}
		}
		return result;
	}

	
	// Prints a matrix row by row
	public void printMatrix (boolean[][] matrix) {

		for (int row = 0; row < matrix.length; row++) {
			for (int column = 0; column < matrix[0].length; column++) {
				if (matrix[row][column]) {
					System.out.print(1 + " ");
				}
				else {
					System.out.print(0 + " ");					
				}
			}
			System.out.println();
		}
	}


	// Prints a binary vector (represented in boolean)
	public void printCodeWord (boolean[] word) {
		for (int column = 0; column < word.length; column++) {
			if (word[column]) {
				System.out.print(1 + " ");
			}
			else {
				System.out.print(0 + " ");					
			}
		}
	}


	// Takes an incoming source word and encodes it by multiplying it by the generator matrix  (sourceWord.GeneratorMatrix = codeWord)
	public boolean[] encodeSource(boolean[] source) {

		boolean[] result = new boolean[totalLength];

		// Go through the generator matrix column by column
		for (int columnIndex = 0; columnIndex < totalLength; columnIndex++) {
			boolean currentBit = false; 
			// Go through all of the elements of the column
			for (int rowIndex = 0; rowIndex < dataLength; rowIndex++) {
				currentBit = currentBit^(source[rowIndex] & generatorMatrix[rowIndex][columnIndex]);
			}
			result[columnIndex] = currentBit;
		}
		return result;
	}


	// Computes the syndrome corresponding to the received word by the use of parity check matrix  (word.ParityCheckMatrix = syndrome)
	public String computeSyndrome (boolean[] codeWord, boolean[][] parityChMatrix) {

		String result = "";
		
		// Go through the parity check matrix column by column
		for (int columnIndex = 0; columnIndex < parityChMatrix[0].length; columnIndex++) {
			boolean currentSyndromeBit = false;
			// Go through each column bit by bit to get the result of multiplication
			for (int rowIndex = 0; rowIndex < totalLength; rowIndex++) {
				currentSyndromeBit = currentSyndromeBit ^ (codeWord[rowIndex] & parityChMatrix[rowIndex][columnIndex]);
			}
			
			// "0" is mapped to "false", "1" is mapped to "true"
			if (currentSyndromeBit) {
				result = result + "1";
			}
			else {
				result = result + "0";
			}
		}
		return result;
	}


	// Returns the source word by finding the syndrome and correcting the corrupted bit (if any)
	// only one error can be correctly corrected
	public boolean[] findSourceWord (boolean[] codeWord) {

		boolean[] result = new boolean[dataLength];
		
		// Find the syndrome and determine the bit that is corrupted
		String syndrome = computeSyndrome(codeWord, this.parityCheckMatrix);
		int errorBit = Integer.parseInt(syndrome, 2) -1;

		// If the syndrome is not 0, correct the error bit
		if (errorBit>-1) {
			// Get the location of the corrupted bit
			int errorBitLocation = integerPositionMapping.get(errorBit);
			codeWord[errorBitLocation] = !codeWord[errorBitLocation];
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
