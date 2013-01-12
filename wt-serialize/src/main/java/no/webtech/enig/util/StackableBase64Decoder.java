package no.webtech.enig.util;


/**
 * 
 * A table driven base64 decoder designed for chained operation.
 * 
 * This decoder has the significant feature of "remembering" unused base64-chars
 * and reuse those later on (eg: next call)
 * 
 * This decoder may also be stacked into a chain providing a
 * base64-decoding-base64-decoding functionality (eg: as in
 * base64decode(base64decode(...(base64decode(arg))...))
 * 
 * It is the users responsibility to empty the outbuffer before buffer capacity is
 * exhausted, either by calling {@link #extract(int)} or {@link #stack()}.
 * 
 * @author jhs
 * 
 */
public class StackableBase64Decoder {
	// private static Logger log = LoggerFactory.getLogger(StackableB64Decoder.class);

	// (valAlpa[i] << 2)
	private final static char[] valueUpshifted2 = { 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 248, 0, 0, 0, 252, 208, 212,
			216, 220, 224, 228, 232, 236, 240, 244, 0, 0, 0, 0, 0, 0, 0, 0, 4,
			8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48, 52, 56, 60, 64, 68, 72,
			76, 80, 84, 88, 92, 96, 100, 0, 0, 0, 0, 0, 0, 104, 108, 112, 116,
			120, 124, 128, 132, 136, 140, 144, 148, 152, 156, 160, 164, 168,
			172, 176, 180, 184, 188, 192, 196, 200, 204, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		}; // END(valAlpa[i] << 2)

	// (valAlpa[v1] >>> 4)
	int[] valueDownshifted4 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 3, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2,
			2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, }; 
	// END(valAlpa[i] >>> 4)


	// (valAlpa[v1] & 0xf) << 4
	private final static char[] valuesFourLowbitUpshifted4 = { 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 224, 0, 0, 0, 240,
			64, 80, 96, 112, 128, 144, 160, 176, 192, 208, 0, 0, 0, 0, 0, 0, 0,
			0, 16, 32, 48, 64, 80, 96, 112, 128, 144, 160, 176, 192, 208, 224,
			240, 0, 16, 32, 48, 64, 80, 96, 112, 128, 144, 0, 0, 0, 0, 0, 0,
			160, 176, 192, 208, 224, 240, 0, 16, 32, 48, 64, 80, 96, 112, 128,
			144, 160, 176, 192, 208, 224, 240, 0, 16, 32, 48, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
			}; // END(valAlpa[v1] & 0xf) << 4

	// (valAlpa[v2] >>> 2)
	private final static char[] valueDownshifted2 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 15, 0, 0, 0, 15, 13, 13, 13, 13, 14, 14, 14, 14,
			15, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3,
			3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 0, 0, 0, 0, 0, 0, 6, 6, 7,
			7, 7, 7, 8, 8, 8, 8, 9, 9, 9, 9, 10, 10, 10, 10, 11, 11, 11, 11,
			12, 12, 12, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, }; // END(valAlpa[v2] >>> 2)

	// ((valAlpa[v2] & 3) << 6)
	private final static char[] valuesTwoLowbitDownshifted6 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 128, 0, 0, 0, 192, 0, 64, 128, 192, 0, 64,
			128, 192, 0, 64, 0, 0, 0, 0, 0, 0, 0, 0, 64, 128, 192, 0, 64, 128,
			192, 0, 64, 128, 192, 0, 64, 128, 192, 0, 64, 128, 192, 0, 64, 128,
			192, 0, 64, 0, 0, 0, 0, 0, 0, 128, 192, 0, 64, 128, 192, 0, 64,
			128, 192, 0, 64, 128, 192, 0, 64, 128, 192, 0, 64, 128, 192, 0, 64,
			128, 192, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, };
	// END((valAlpa[v2] & 3) << 6)

	// valAlpa[v3])
	private final static char[] value = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 62, 0, 0, 0, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60,
			61, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
			13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 0, 0, 0, 0, 0,
			0, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41,
			42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, }; // END (valAlpa[v3])

	// isAlpa[v3])
	private final static boolean[] isB64DefinedChar = { false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false,
			false, true /* + */, false, false, false, true/* / */, true, true,
			true, true, true, true, true, true, true, true, false, false,
			false, true/* = */, false, false, false, true, true, true, true,
			true, true, true,
			true, true, true, true, true, true, true, true, true, true, true,
			true, true, true, true, true, true, true, true, false, false,
			false, false, false, false, true, true, true, true, true, true,
			true, true, true, true, true, true, true, true, true, true, true,
			true, true, true, true, true, true, true, true, true, false, false,
			false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, }; // END (isA[v3])
	
	public int b64CharPointer;
	public char[] b64Char;
	public byte[] resultBuffer;
	public int resultIndex = 0;
	
	@Deprecated @SuppressWarnings("unused")
	private StackableBase64Decoder() {
		this(1024);
	}
	
	public StackableBase64Decoder(int resultBufferCapacity) {
		resultBuffer = new byte[resultBufferCapacity];
		resultIndex = 0; 
		b64CharPointer=0;
		b64Char = new char[4];
	}

	public StackableBase64Decoder push(CharSequence c) {
		for (int i = 0; c != null && i < c.length(); i++)
			put(c.charAt(i));
		return this;
	}
	
	private void put(char c){
		if (isB64DefinedChar[c]){ 			// <== Only process chars with base64 meaning		
			b64Char[b64CharPointer++]=c; 	// <== Push a valid b64-char to stack		
			if (b64CharPointer == 4) { 		// <== Only process a fully filled stack
				if (b64Char[3] != '=') {	// Eg: trailing "xxx",  where x is not =
					// Output three bytes 
					resultBuffer[resultIndex++] = (byte) (valueUpshifted2[b64Char[0]] | valueDownshifted4[b64Char[1]]);
					resultBuffer[resultIndex++] = (byte) (valuesFourLowbitUpshifted4[b64Char[1]] | valueDownshifted2[b64Char[2]]);
					resultBuffer[resultIndex++] = (byte) (valuesTwoLowbitDownshifted6[b64Char[2]] | value[b64Char[3]]);
				} else if (b64Char[2] != '=') { // Eg: trailing "xx=" where x is not =
					// Output two bytes
					resultBuffer[resultIndex++] = (byte) (valueUpshifted2[b64Char[0]] | valueDownshifted4[b64Char[1]]);
					resultBuffer[resultIndex++] = (byte) (valuesFourLowbitUpshifted4[b64Char[1]] | valueDownshifted2[b64Char[2]]);
				} else if (b64Char[1] != '=') { // Eg: trailing "x=="
					// output one byte
					resultBuffer[resultIndex++] = (byte) (valueUpshifted2[b64Char[0]] | valueDownshifted4[b64Char[1]]);
				} else if (b64Char[0] != '=') { // Eg: trailing "===" Actually ILLEGAL,signal error
					throw new IllegalArgumentException(
							"Sequence has a \"===\" and is " +
							"not according to RFC4648, sorry");
				}				
				b64CharPointer = 0;
			}
		}		
	}

	/**
	 * Extracts whole buffer.This method is logically equivalent with {@link extract} plus a
	 * check that all Base64 characters actually are processed.
	 * 
	 * @throws IllegalStateException if any unprocessed b64 characters remain in pipe,
	 * @return a byte array with the base64 decoded bytes
	 */

	public byte[] extractAll() throws IllegalStateException {
		if (b64CharPointer != 0)
			throw new IllegalStateException("Error: " + b64CharPointer +
					" base64 characters still remain unused in pipe.");
		return extract(1);
	}
	
	/**
	 * return a buffer which is a multiple of argument multipleOf
	 * @param multipleOf
	 * @return
	 */
	public byte[] extract(int multipleOf) {
		int returnBufferSize = resultIndex - resultIndex % multipleOf;// & ~0xf;
		byte[] returnBuffer = null;
		if (returnBufferSize > 0) {
			returnBuffer = new byte[returnBufferSize];
			System.arraycopy(resultBuffer, 0, returnBuffer, 0, returnBufferSize);
			if ((resultIndex - returnBufferSize) > 0) {
				System.arraycopy(resultBuffer, returnBuffer.length,
						resultBuffer, 0, resultIndex - returnBufferSize);
			}
			resultIndex -= returnBufferSize;
			// log.debug("Resetting size by {} to new size {}",size,oIx);
		}
		return returnBuffer;
	}

	/**
	 * This method gives away its produced content as bytes onto the
	 * {@linkplain StackableBase64Decoder} nextHigherLevelBase64Decoder assuming
	 * that these bytes represents new base64 characters that is to be decoded further.
	 * 
	 * Note: This method preserves received, but not yet used base64 characters.
	 * This feature makes it trivial to continue to fill in following base64
	 * bytes without loosing base64 characters.
	 * 
	 * @param nextHigherLevelBase64Decoder
	 */
	public void stack(StackableBase64Decoder nextHigherLevelBase64Decoder) {
		int i = 0;
		while (i < resultIndex)
			nextHigherLevelBase64Decoder.put((char) resultBuffer[i++]);
		resultIndex=0;
	}

	/**
	 * This method outputs actual content of internal buffers
	 * Mainly intended for easy testing
	 */
	public String toString() {
		StringBuffer ret = new StringBuffer();
		for (int i = 0; i < b64CharPointer; i++)
			ret.append('{').append(b64Char[i]).append('}');		 
		if (resultIndex>0) 
			ret.append(String.format("%02x", resultBuffer[0]));
		for (int i = 1; i < resultIndex; i++)
			ret.append(String.format(" %02x", resultBuffer[i]));
		return ret.toString();
	}

	/**
	 * Utility function to pick out part of a byte buffer to use for
	 * base64-decoding. <code><pre>
	 * 	byte[] rawascii=
	 *		( "-----BEGIN ENCRYPTED PRIVATE KEY-----\r\n"
	 *		+ "MIIBpjBABgkqhkiG9w0BBQ0wMzAbBgkqhkiG9w0BBQwwDgQIeFeOWl1jywYCAggA\r\n"
	 *		+ "....BlaBlaBla...\r\n"
	 *		+ "u4xtxT/hoK3krEs/IN3d70qjlUJ36SEw1UaZ82PWhakQbdtu39ZraMJB\r\n"
	 *		+ "-----END ENCRYPTED PRIVATE KEY-----\r\n"
	 *		).getBytes();
	 * 	
	 * 	int start = StackableBase64Decoder.locateTagInBuffer(rawascii, 0,  "-----\r\n", false);
	 * 	int end = StackableBase64Decoder.locateTagInBuffer(rawascii, start,"\r\n-----END ", true);
	 * 	byte[] inBetween = Arrays.copyOfRange(rawascii, start, end);
	 * </pre></code>
	 * 
	 * @param b
	 * @param startAt
	 * @param tag
	 * @param isEndTag whenever the tag will be included in the inBetween buffer.
	 * @return
	 */
	public static int locateTagInBuffer(CharSequence b, int startAt, String tag, boolean isEndTag) {
		if (tag == null || b == null || startAt == -1) return -1;
		int i = 0;
		int j = startAt;
 		CharSequence l = tag.toLowerCase();		
		CharSequence u = tag.toUpperCase();
		while (j < b.length() - tag.length()) {
			for (i = 0; i < tag.length()
					&& (l.charAt(i) == b.charAt(i + j) || u.charAt(i) == b.charAt(i + j)); i++);
			if (i == tag.length())
				return i + j - (isEndTag?tag.length():0);
			j++;
		}
		return -1;
	}
	
	public static int locateTagInBuffer(byte[] b, int startAt, String tag, boolean isEndTag) {
		if (tag == null || b == null || startAt == -1) return -1;
		int i = 0;
		int j = startAt;
		byte[] l = tag.toLowerCase().getBytes();
		byte[] u = tag.toUpperCase().getBytes();
		while (j < b.length - tag.length()) {
			for (i = 0; i < tag.length()
					&& (l[i] == b[i + j] || u[i] == b[i + j]); i++);
			if (i == tag.length())
				return i + j - (isEndTag?tag.length():0);
			j++;
		}
		return -1;
	}
}