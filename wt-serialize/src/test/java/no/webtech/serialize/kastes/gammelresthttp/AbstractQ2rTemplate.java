package no.webtech.serialize.kastes.gammelresthttp;
//package no.webtech.serialize.rvoapi;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import no.webtech.serialize.coreapi.QueryHolder;
//import no.webtech.serialize.coreapi.QueryHolderAdapter;
//import no.webtech.serialize.coreapi.RowMapper;
//import no.webtech.serialize.coreapi.SessionController;
//import no.webtech.serialize.coreapi.Template;
//import no.webtech.serialize.plainapi.AbstractDetachableSessionSupport;
//
///**
// * This anchestor of classes hierarchy implements the full Template interface, but
// * relies on the {@link Rvo} The alternative is to use 
// * the {@link AbstractDetachableSessionSupport} where one must implement a method to
// * obtain a Serializable.
// * 
// * @author jhs
// * 
// */
//public abstract class AbstractQ2rTemplate implements Template {
//	
//	public abstract Q2R getQ2r();
//	
//	public <T> List<T> queryForList(SessionController sessionController, RowMapper<T> rowMapper,String query) {
//		QueryHolder qsh = QueryHolderAdapter.query(query);
//		Rvo rvo = this.getQ2r().fromQueryStringHolder(sessionController, qsh);
//		if (rvo == null)
//			return null;
//		List<T> ret = new ArrayList<T>();
//		for (Map<String, String> map : rvo.getRows()) {
//			ret.add(rowMapper.mapRow(map));
//		}
//		return ret;
//	}
//	
//	/* TODO: Maybe improve this method to retrieve value directly */
//	public <T> T queryForObject(SessionController sessionController, RowMapper<T> rowMapper, String query){
//		List<T> li = queryForList(sessionController,rowMapper,query);
//		if (li==null || li.size()==0) return null;
//		return li.get(0);
//	}
//	
//	public String queryForString(SessionController sessionController, String query){
//		QueryHolder qsh = QueryHolderAdapter.query(query);
//		Rvo rvo = this.getQ2r().fromQueryStringHolder(sessionController, qsh);
//		if (rvo == null)
//			return null;
//		String attrName = obtainFirstAndOnlyAttrName(rvo.getAttributeNames());
//		
//		List<Map<String, String>> rows = rvo.getRows();
//		if (rows==null || rows.size()<1) return null;		
//		if (rows.size()>1) throw new IllegalArgumentException("More than one value - use queryForListOfStrings() method to obtain a List<String>");
//		
//		return rows.get(0).get(attrName);
//	}
//	
//	public List<String> queryForListOfStrings(SessionController sessionController, String query) {
//		QueryHolder qsh = QueryHolderAdapter.query(query);
//		Rvo rvo = this.getQ2r().fromQueryStringHolder(sessionController, qsh);
//		if (rvo == null)
//			return null;
//		String attrName = obtainFirstAndOnlyAttrName(rvo.getAttributeNames());
//		List<Map<String, String>> rows = rvo.getRows();
//		if (rows == null || rows.size() < 1)
//			return null;
//		List<String> ret = new ArrayList<String>();
//		for (Iterator<Map<String, String>> iterator = rows.iterator(); iterator.hasNext();)
//			ret.add(iterator.next().get(attrName));
//		return ret;
//	}
//	
//
//	private String obtainFirstAndOnlyAttrName(Set<String> attributeNames) {
//		if (attributeNames.size()< 1) throw new IllegalArgumentException("zero columns returned");		
//		// TODO: Decide if more than one column should result in error
//		if (attributeNames.size()> 1) throw new IllegalArgumentException("select returns more than one columns, maybe this is an error.");
//		return attributeNames.iterator().next();
//	}
//}