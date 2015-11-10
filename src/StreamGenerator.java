import java.util.Random;

// Class for generating information stream, simulating channels for creating burst errors and transmitting data using interleaving
public class StreamGenerator {

	// Parameters of the Hamming code
	private final int codeWordLength;
	private final int totalLength;
	// The number of codeWords that are buffered together for interleaving
	private final int interleavingTableHeight;
	// Probability of a bit getting corrupted when the bad channel is used
	private final double probOfError;
	// Probabilities of changing channels
	private final double probGoodToBad;
	private final double probBadToGood;
	// The receiver that the encoded information is to be sent to
	private final StreamReceiver receiver;
	// Hamming code to be used for encoding information
	private final HammingCode hammingCode;
	private static final int GOOD_CHANNEL = 1;
	private static final int BAD_CHANNEL = 2;
	private int currentChannel = GOOD_CHANNEL;


	public StreamGenerator (int sizeParameter, int interleavingTableHeight, double probOfError, double probGoodToBad, double probBadToGood, StreamReceiver receiver, HammingCode hammingCode) {
		codeWordLength = (int)(Math.pow(2, sizeParameter)) - sizeParameter -1;
		totalLength = hammingCode.getTotalLength();
		this.interleavingTableHeight = interleavingTableHeight;
		this.probBadToGood = probBadToGood;
		this.probGoodToBad = probGoodToBad;
		this.probOfError = probOfError;
		this.receiver = receiver;
		this.hammingCode = hammingCode;
	}


	// Transmits information to receiver by generating random source words, getting them encoded and interleaving the code words before sending
	public void transmitCode() {
		boolean[][] codeMatrix = getcodeMatrix();
		
		System.out.println("\nGenerated:");
		hammingCode.printMatrix(codeMatrix);
		
		// Go through the code word table column by column (sending one bit from each code word - interleaving)
		for (int i = 0; i < totalLength; i++) {
			boolean[] currentColumn = new boolean[interleavingTableHeight];
			// Go through the rows, noting down the i'th bit of each
			for (int j = 0; j < interleavingTableHeight; j++) {
				// The "good" channel is guaranteed to transmit information without errors
				if (currentChannel == GOOD_CHANNEL) {
					currentColumn[j] = codeMatrix[j][i];
				}
				// The "bad" channel might corrupt bits
				else {
					currentColumn[j] = determineBitFlip(codeMatrix[j][i]);
				}
			}
			
			// Send the packet to the receiver
			receiver.receiveData(currentColumn);
			
			// Determine a channel change
			determineChannel();
		}
	}
	
	
	// Generates a matrix whose rows are composed by randomly generated source words that are then encoded
	private boolean[][] getcodeMatrix() {
		
		boolean[][] codeMatrix = new boolean[interleavingTableHeight][totalLength];
		
		// Generate a given number of source words and encode them
		for (int i = 0; i<interleavingTableHeight; i++) {
			boolean[] newSource = getSourceInfo();
			codeMatrix[i] = hammingCode.encodeSource(newSource);
		}
		
		return codeMatrix;
	}


	// Simulates a channel flip (or staying in the same channel)
	private void determineChannel () {
		
		// Use a randomly generated double number between 0 and 1 to determine whether the channel should be changed
		Random randomizer = new Random();
		double channelSwitch = randomizer.nextDouble();

		// In case we are currently using the good channel, we need to compare the random double to the probability of switching from the good channel to the bad one
		if (currentChannel == GOOD_CHANNEL) {
			if (channelSwitch < probGoodToBad) {
				currentChannel = BAD_CHANNEL;
			}
		}
		// Otherwise compare it to the probability of switching from the bad channel to the good one
		else {
			if (channelSwitch < probBadToGood) {
				currentChannel = GOOD_CHANNEL;
			}
		}
	}
	
	// When transmitted via the "bad" channel, each bit has a probability probOfError of being flipped
	// This method simulates a bit being flipped, taking into account the given porbability
	private boolean determineBitFlip(boolean bit) {
		
		// Get a random double number between 0 and 1 (with a uniform probability)
		Random randomizer = new Random();
		double flipBit = randomizer.nextDouble();
		
		// If the number generated is smaller than the probability, assume that the bit is flipped
		if (flipBit<probOfError) {
			return !bit;
		}
		else return bit;
	}

	// Simulates information source by creating a boolean array with equal probabilities of true and false (standing for 0 and 1) for any position.
	private boolean[] getSourceInfo() {
		Random randomizer = new Random();
		boolean[] sourceInfo = new boolean[codeWordLength];
		for (int i = 0; i<codeWordLength; i++) {
			sourceInfo[i] = randomizer.nextBoolean();
		}
		return sourceInfo;
	}

}
