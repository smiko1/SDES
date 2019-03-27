
import java.util.Scanner;

public class SDES {

	/**
	 * This program implements a simplified DES algorithm to encrypt 
	 * a string of binary text, and returns the encrypted message.
	 * 
	 */
	public static void main(String[] args) {
		
		// ask the user for the message, master key, and number of rounds
		Scanner input = new Scanner(System.in);
		System.out.println("Enter the plaintext you'd like to encrypt: ");
		String text = input.next();
		System.out.println("Now, enter the master key you'd like to use: ");
		String masterKey = input.next();
		System.out.println("Finally, enter the number of rounds you want: ");
		int numRounds = input.nextInt();
		input.close();
		
		String[] roundKeys = new String[numRounds];
		
		// create the round key for each round
		for (int i = 0; i < numRounds; i++) {
			if (i == 0)
				roundKeys[0] = masterKey.substring(0, 8);
			else {
				roundKeys[i] = masterKey.substring(i);
				
				// if the current key is shorter than 8 bits, wrap around the master key until it gets to 8 bits
				if (roundKeys[i].length() != 8)
					roundKeys[i] += masterKey.substring(0, 8 - roundKeys[i].length());
			}
		}
		// arrays used to store R and L for each round
		String[] R = new String[numRounds + 1];
		String[] L = new String[numRounds + 1];
		
		for (int i = 0; i < numRounds + 1; i++) {
			if (i == 0) {
				L[0] = split(text)[0];
				R[0] = split(text)[1];
			} else {
				L[i] = R[i-1];
				
				// call the f function to get the 6 bit result from the S-boxes
				String sBoxResults = f(R[i-1], roundKeys[i - 1]);
				
				// XOR those results to the left 6 bits and set it equal to R
				R[i] = xor(sBoxResults, L[i-1]);
			}
		}
		// print the result
		String result = L[numRounds] + R[numRounds];
		System.out.println("Result: " + result);

	}// end of main method
	
	
	/**
	 * Splits the binary digit into a right and left half.
	 * 
	 * @param text  the binary digit to split
	 * @return  an array of the split binary digits
	 */
	public static String[] split(String text) {
		String[] split = new String[2];
		if (text.length() % 2 != 0) {
			System.out.println("Invalid text length");
			return null;
		} else {
			split[0] = text.substring(0, text.length() / 2);
			split[1] = text.substring(text.length() / 2);
			return split;
		}
	}// end of split method
	
	
	/**
	 * F Function; expands R[x] into 8-bits, XOR's it to the key, 
	 * and runs each half of the result through the S-boxes to 
	 * receive a 6-bit binary string.
	 * 
	 * @param R  the right half of the binary text
	 * @param key  the round key being used
	 * @return  the S-box results (a 6-bit binary string)
	 */
	public static String f(String R, String key) {
		
		String expandedR = "", xor;
		
		for (int i = 0; i < R.length(); i++) {
			// when we get to the 3rd digit, we will annex the 4th and the 3rd twice, and then continue with the last two digits
			if (i == 2) {
				expandedR += R.substring(3, 4) + R.substring(2, 3) + R.substring(3, 4) + R.substring(2, 3);
				i++;
			} else {
				if (i == R.length() - 1)
					expandedR += R.substring(i);
				else
					expandedR += R.substring(i, i+1);
			}
		}
		// XOR the expanded 8-bit R with the round key
		xor = xor(expandedR, key);
		
		// return the results from passing the bits through the S-boxes
		return passThroughSBoxes(xor);
	}// end of f function
	
	
	/**
	 * Completes an XOR calculation of binary digits.
	 * 
	 * @param x  the first binary number
	 * @param y  the second binary number
	 * @return  the calculation results
	 */
	public static String xor(String x, String y) {
		String result = "";
		for (int i = 0; i < x.length(); i++) {
			if (i < x.length() - 1)
				result += (Integer.parseInt(x.substring(i, i+1)) + Integer.parseInt(y.substring(i, i+1))) % 2;
			else
				result += (Integer.parseInt(x.substring(i)) + Integer.parseInt(y.substring(i))) % 2;
		}
		return result;

	}// end of xor method
	
	
	/**
	 * Runs the 8-bit binary String through the S-boxes to condense 
	 * it back down to 6-bits.
	 * 
	 * @param text  the binary String to be put through the two S-boxes
	 * @return  the results for S-box1 and S-box2
	 */
	public static String passThroughSBoxes(String text) {
		String[][] S1 = {{"101", "010", "001", "110", "011", "100", "111", "000"}, {"001", "100", "110", "010", "000", "111", "101", "011"}};
		String[][] S2 = {{"100", "000", "110", "101", "111", "001", "011", "010"}, {"101", "011", "000", "111", "110", "010", "010", "100"}};
		String[] result = new String[2];
		
		// split binary digits to left and right sides
		String left = split(text)[0];
		String right = split(text)[1];
		
		// calculates binary to integer
		int leftInt = Integer.parseInt(left.substring(1), 2);
		int rightInt = Integer.parseInt(right.substring(1), 2);
		
		// passes right side through S-box1 and left side through S-box2 to get 6-bit result
		result[0] = S1[Integer.parseInt(left.substring(0, 1))][leftInt];
		result[1] = S2[Integer.parseInt(right.substring(0, 1))][rightInt];

		return result[0] + result[1];
	}// end of calculateWithSBoxes method

}// end of SDES class
