package no.webtech.serialize.coreapi;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class QueryHolderAdapter implements QueryHolder {

	public static QueryHolder query(String... q){
		return new QueryHolderAdapter(q);
	}
	
	private String query;
	private String queryFingerPrint;
	
	private QueryHolderAdapter(){} // Not used
	
	private QueryHolderAdapter(String... queryArray) {
		if (queryArray == null || queryArray.length == 0)
			throw new NullPointerException("Cannot understand the need for null qry, Must not be null, Please implement when understood");

		query = queryArray[0];
		StringBuilder sb = new StringBuilder();
		for (String q : queryArray)
			sb.append(q);
		queryFingerPrint = toSHA1(this.query.trim().replaceAll("[\r\n ]", " ").getBytes());

	}

	public String getQuery() {
		return query;
	}

	public String queryFingerprint() {
		return queryFingerPrint;
	}

	private String toSHA1(byte[] convertme) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException("SHA-1 dows not exist as algorithm -- very strange");
		}
		byte[] b = md.digest(convertme);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
			sb.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
		return sb.toString();
	}
}
