package no.webtech.serialize.resthttppoc;

import org.apache.commons.httpclient.Header;

import no.webtech.serialize.coreapi.QueryHolder;
import no.webtech.serialize.coreapi.QueryHolderAdapter;

public class PocQueryHolder implements QueryHolder {
	private QueryHolder qh;
	private Header header;

	public PocQueryHolder(String qry) {
		header = new Header("Accept", "application/xml");
		qh = QueryHolderAdapter.query(qry, header.toExternalForm());
	}

	public String getQuery() {
		return qh.getQuery();
	}

	public String queryFingerprint() {
		return qh.queryFingerprint();
	}

	public Header[] getHeaders() {
		return new Header[] { header };
	}
}