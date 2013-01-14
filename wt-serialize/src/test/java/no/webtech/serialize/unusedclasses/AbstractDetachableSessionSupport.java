package no.webtech.serialize.unusedclasses;

import java.io.Serializable;

import no.webtech.serialize.coreapi.QueryHolderAdapter;
import no.webtech.serialize.coreapi.SessionController;
import no.webtech.serialize.impl.UseSnapUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractDetachableSessionSupport {
	
	private static Logger logger = LoggerFactory.getLogger(AbstractDetachableSessionSupport.class);
	
	abstract protected Serializable internalObtainSerializableObject(SessionController sess, Object... qsh);
	
	abstract protected boolean isQuickloading();
	
	/**
	 * Is called by abstract sibling 
	 * that implements {@link #internalObtainSerializableObject(SessionController, Object...)} 
	 * 
	 * @param sess
	 * @param qsh
	 * @return
	 */
	protected Serializable internalQuery(SessionController sess, String qsh) {
		Serializable ret = null;
		if (isQuickloading() && (ret = loadObjectByQsh(qsh)) != null)			
			return ret;
		 
		ret = internalObtainSerializableObject(sess, qsh);
		
		if (isQuickloading() && ret != null)			
			saveObjectIdentifiedByQsh(ret, qsh);
		
		return ret;
	}
	
	private void saveObjectIdentifiedByQsh(Serializable ret,String qsh) {
		logger.debug("Note: Quickloading. Persisting object for later use");
		UseSnapUtil.dumpToFile(ret,  QueryHolderAdapter.query(qsh));
	}

	private Serializable loadObjectByQsh(String qsh) {		
		Serializable ret = UseSnapUtil.fromFile(QueryHolderAdapter.query(qsh));		
		if (ret == null)
			logger.debug("Note: Quickloading, but we dont have previousely persisted object.");
		else
			logger.debug("Note: Quickloading. Obtaining previousely persisted object.");
		return ret;
	}
}
