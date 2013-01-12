package no.webtech.serialize.rvoapi;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Rvo is a ResultsetValueObject and is used as an intermediate class to
 * isolate. This class is used to isolate results from classes that otherwise
 * would impose large dependencies, for example Documentum result sets. The idea
 * is that this class may be a generic result from many type of "queries", for
 * instance sql-queries and so on.
 * 
 * This class is designed so it will not introduce other dependencies other than
 * those found in java core api.
 * 
 * @author jhs
 * 
 */
public class Rvo implements Serializable {

	private static final long serialVersionUID = 2L; // Recall: the serialVersonUID is significant in the serialization process
	private Set<String> attributeNames;
	private List<Map<String, String>> rows;

	public Set<String> getAttributeNames() {
		return attributeNames;
	}

	public void setAttributeNames(Set<String> attributeNames) {
		this.attributeNames = attributeNames;
	}

	public List<Map<String, String>> getRows() {
		return rows;
	}

	public void setRows(List<Map<String, String>> rows) {
		this.rows = rows;
	}
}
