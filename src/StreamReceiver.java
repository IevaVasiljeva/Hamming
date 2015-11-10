
// Class for simulating receiving interleaved data and using Hamming code to obtain the corresponding source code
public class StreamReceiver {
	
	// Parameters of the Hamming code
	private final int interleavingTableHeight;
	private final int totalLength;
	private final int dataLength;
	
	// Hamming code to be used
	private final HammingCode hammingCode;

	// To keep track of the incoming data, as it has to be buffered before decoding
	private int numOfDataPacketsReceived = 0;
	private boolean[][] incomingDataMatrix;
	
	
	public StreamReceiver (int sizeParameter, int interleavingTableHeight, HammingCode hammingCode) {
		dataLength = (int)(Math.pow(2, sizeParameter)) - sizeParameter -1;
		totalLength = hammingCode.getTotalLength();
		this.hammingCode = hammingCode;
		this.interleavingTableHeight = interleavingTableHeight;
		incomingDataMatrix = new boolean[interleavingTableHeight][totalLength];
	}
	
	// Receives the data and buffers it, as the data is interleaved, and thus one bit from each of the code words is received at a time
	public void receiveData(boolean[] incomingData) {
		for (int i = 0; i < interleavingTableHeight; i++) {
			incomingDataMatrix[i][numOfDataPacketsReceived] = incomingData[i];
		}
		numOfDataPacketsReceived++;
	}
	
	// Decodes the data received using the provided Hamming code
	public void decodeData() {
		
		System.out.println("\nReceived:");
		hammingCode.printMatrix(incomingDataMatrix);
		
		// Decode the data row by row
		boolean[][] decodedData = new boolean[interleavingTableHeight][dataLength];
		for (int i = 0; i < interleavingTableHeight; i++) {
			decodedData[i] = hammingCode.findSourceWord(incomingDataMatrix[i]);
		}
		
		System.out.println("\nDecoded:");
		hammingCode.printMatrix(decodedData);
	}

}
