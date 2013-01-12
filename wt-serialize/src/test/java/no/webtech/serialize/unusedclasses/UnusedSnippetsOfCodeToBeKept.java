package no.webtech.serialize.unusedclasses;

public class UnusedSnippetsOfCodeToBeKept {
	
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
