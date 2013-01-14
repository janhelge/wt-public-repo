package no.webtech.serialize.unusedclasses;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UnusedSnippetsOfCodeToBeKept {
	// FIXME Denne er bare til bruk i dfc-ser midlertidig for dette er refakturert

	private static String toSHA1(byte[] convertme) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return byteArrayToHexString(md.digest(convertme));
	}

	private static String byteArrayToHexString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
			sb.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
		return sb.toString();
	}
	
	private static boolean isPropTrue(String prop){
		String sc  = System.getProperty(prop);
		return sc != null && sc.equals("true");
	}
	
	private static boolean isPropValue(String prop, String value){
		String sc  = System.getProperty(prop);
		return sc != null && sc.equals(value);
	}
	
	/**
	 * Make a string pointing to where we was called from
	 * This method may be useful to find the actual place
	 * where from a call was made, and for this reason, this method
	 * is not (yet) removed.
	 * 
	 * @param delim
	 * @return
	 */
	private static StringBuilder callStack(String delim) {
		StackTraceElement[] cause = Thread.currentThread().getStackTrace();
		StringBuilder sb = new StringBuilder();
		String dlm="";
		for (int i = 2; i < cause.length; i++, dlm=delim)
			sb
			.append(dlm)
			.append(cause[i].getClassName())
			.append(".")
			.append(cause[i].getMethodName())
			.append("()#")
			.append(cause[i].getFileName())
			.append("[")
			.append(cause[i].getLineNumber())
			.append("]");
		
		return sb;
	}

}
