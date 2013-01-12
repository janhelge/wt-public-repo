package no.webtech.serialize.coreapi;

/**
 * Qsh is an acronym for Query String Holder
 * @author jhs
 *
 */
public interface QueryHolder {
	
	String getQuery();

	String queryFingerprint();
	
}