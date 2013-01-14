package no.webtech.serialize.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import no.webtech.enig.util.Base64Encoder;


public class SuperSnapUtil {

	

	/**
	 * Utility to read a whole file into a byte buffer.
	 * @param fn
	 * @return
	 * @throws IOException
	 */
	public static byte[] readWholeFile(String fn) throws IOException {

		File f = new File(fn);
		byte[] ba = new byte[(int) f.length()];
		FileInputStream fis = new FileInputStream(f);

		int i = 0;
		int off = 0;
		int len = ba.length;

		while (i < len) {
			len -= i;
			off += i;
			i = fis.read(ba, off, len);
		}
		fis.close();
		return ba;
	}
	
	/**
	 * Utility method that search for "searchFor"-bytes in buffer, starting from
	 * iStartBufferOffset
	 * 
	 * @param buffer
	 * @param searchFor
	 * @param iStartBufferOffset
	 * @return -1 if not found, else the offset plus the length of the searchFor
	 *         byte array
	 */
	public static int searchFor(byte[] buffer, byte[] searchFor, int iStartBufferOffset) {
		int iWrk = iStartBufferOffset;
		int iX;

		if (buffer == null)
			throw new NullPointerException("buffer is null");
		if (searchFor == null)
			throw new NullPointerException("searchFor is null");
		if (iStartBufferOffset < 0)
			throw new IllegalArgumentException("iStartBufferOffset < 0");
		if (iStartBufferOffset > buffer.length)
			throw new IllegalArgumentException("iStartBufferOffset > buffer.length");

		while (iWrk < buffer.length - searchFor.length +1) {
			iX = -1;
			boolean testRemaining = iX < searchFor.length - 1;
			boolean sofarCorrect = true;

			while (testRemaining && sofarCorrect) {
				iX++;
				sofarCorrect = (buffer[iWrk + iX] == searchFor[iX]);
				testRemaining = iX < searchFor.length - 1;
			}
			if (testRemaining == false && sofarCorrect) {
				return iWrk + iX + 1;
			}
			iWrk++;
		}
		return -1;
	}
	
	
	/**
	 * Creates a base64 encoded stringBuffer representation of the actual object
	 * @param toBeSerialized
	 * @param comment
	 * @return the string buffer
	 */
	public static StringBuffer createSerializedFormattedOutput(
			Serializable toBeSerialized, String comment) {
		StringBuffer sb = new StringBuffer();

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(toBeSerialized);
			oos.close();

			sb.append("-----BEGIN SERIALIZED "
					+ toBeSerialized.getClass().getCanonicalName() + " "
					+ comment + "-----\r\n");
			char[] ca2 = Base64Encoder.encodeAsCharArray(baos.toByteArray());
			for (int start = 0; start < ca2.length; start += 60) {
				int iLen = 60;
				if (iLen > ca2.length - start)
					iLen = ca2.length - start;
				sb.append(ca2, start, iLen);
				sb.append("\r\n");
			}
			sb.append("-----END SERIALIZED-----\r\n");
		} catch (NotSerializableException e) { // Will not happen since argument is Serializable
			sb.append("Sorry - Exception occured, ")
					.append(toBeSerialized.getClass().getCanonicalName())
					.append(" did throw NotSerializableException.");
		} catch (IOException e) {
			// Never happens since we are using byte arrays..
			e.printStackTrace();
		}
		return sb;
	}
	

}