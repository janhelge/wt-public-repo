package no.webtech.serialize.rvoapi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.webtech.serialize.coreapi.QueryHolder;
import no.webtech.serialize.coreapi.QueryHolderAdapter;
import no.webtech.serialize.coreapi.RowMapper;
import no.webtech.serialize.coreapi.SessionController;
import no.webtech.serialize.coreapi.Template;
// import no.webtech.serialize.plainapi.AbstractDetachableSessionSupport;
import no.webtech.serialize.plainapi.ObjectLoader;

/**
 * This ancestor of classes hierarchy implements the full Template interface, but
 * relies on the {@link Rvo} The alternative is to use 
 * the {@link AbstractDetachableSessionSupport} where one must implement a method to
 * obtain a Serializable.
 * 
 * NOTE: This class actually fakes the QueryHolder using the adapter. Sophisticated use of 
 * another queryHolder implementation will fail because of this. Please dig into this problem
 * when it becomes a relevant problem, and for no, just use the adapter 
 * 
 * @author jhs
 * 
 */
public class ObjectLoaderBasedTemplate<C extends SessionController> implements Template<C> {
	
	private ObjectLoader<Rvo, C, QueryHolder> objectLoader;
	
	public void setObjectLoader(ObjectLoader<Rvo, C, QueryHolder> objectLoader){
		this.objectLoader=objectLoader;
	}
	
	private Rvo fetchRvo(C sessionController, QueryHolder qsh){
		return objectLoader.loadObject(sessionController, qsh);
	}
	
	public <T> List<T> queryForList(C sessionController, RowMapper<T> rowMapper, String query) {
		Rvo rvo = fetchRvo(sessionController, QueryHolderAdapter.query(query));
		if (rvo == null)
			return null;
		List<T> ret = new ArrayList<T>();
		for (Map<String, String> map : rvo.getRows()) {
			ret.add(rowMapper.mapRow(map));
		}
		return ret;
	}
	
	/* TODO: Maybe improve this method to retrieve value directly */
	public <T> T queryForObject(C sessionController, RowMapper<T> rowMapper, String query){
		List<T> li = queryForList(sessionController,rowMapper,query);
		if (li==null || li.size()==0) return null;
		return li.get(0);
	}
	
	public String queryForString(C sessionController, String query){
		Rvo rvo = fetchRvo(sessionController, QueryHolderAdapter.query(query));
		if (rvo == null)
			return null;
		String attrName = obtainFirstAndOnlyAttrName(rvo.getAttributeNames());
		
		List<Map<String, String>> rows = rvo.getRows();
		if (rows==null || rows.size()<1) return null;		
		if (rows.size()>1) throw new IllegalArgumentException("More than one value - use queryForListOfStrings() method to obtain a List<String>");
		
		return rows.get(0).get(attrName);
	}
	
	public List<String> queryForListOfStrings(C sessionController, String query) {
		Rvo rvo = fetchRvo(sessionController, QueryHolderAdapter.query(query));
		if (rvo == null)
			return null;
		String attrName = obtainFirstAndOnlyAttrName(rvo.getAttributeNames());
		List<Map<String, String>> rows = rvo.getRows();
		if (rows == null || rows.size() < 1)
			return null;
		List<String> ret = new ArrayList<String>();
		for (Iterator<Map<String, String>> iterator = rows.iterator(); iterator.hasNext();)
			ret.add(iterator.next().get(attrName));
		return ret;
	}
	

	private String obtainFirstAndOnlyAttrName(Set<String> attributeNames) {
		if (attributeNames.size()< 1) throw new IllegalArgumentException("zero columns returned");		
		// TODO: Decide if more than one column should result in error
		if (attributeNames.size()> 1) throw new IllegalArgumentException("select returns more than one columns, maybe this is an error.");
		return attributeNames.iterator().next();
	}
}