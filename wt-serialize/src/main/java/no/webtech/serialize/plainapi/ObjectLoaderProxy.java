package no.webtech.serialize.plainapi;

import java.io.Serializable;

import no.webtech.serialize.coreapi.QueryHolder;
import no.webtech.serialize.coreapi.SessionController;
import no.webtech.serialize.impl.UseSnapUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectLoaderProxy<S extends Serializable, C extends SessionController, Q extends QueryHolder> implements ObjectLoader<S,C,Q>{
	
	private static Logger logger = LoggerFactory.getLogger(ObjectLoaderProxy.class);
	private ObjectLoader<S,C,Q> objectLoader;
	private boolean isLazy=true;

	public ObjectLoaderProxy<S,C,Q> iger(ObjectLoader<S,C,Q> objectLoader){
		this.objectLoader=objectLoader;
		isLazy=false;
		return this;
	}
	
	public ObjectLoaderProxy<S,C,Q> lazy(ObjectLoader<S,C,Q> objectLoader){
		this.objectLoader=objectLoader;
		isLazy=true;
		return this;
	}
	/**
	 * Is called by abstract sibling 
	 * that implements {@link #internalObtainSerializableObject(SessionController, Object...)} 
	 * 
	 * @param sess
	 * @param qsh
	 * @return
	 */
	
	public S loadObject(C sess, Q qsh) {
		S ret = null;
		if (isLazy && (ret = loadObjectByQsh(qsh)) != null)			
			return ret;
		
		ret = objectLoader.loadObject(sess, qsh);
		
		if (isLazy && ret != null)			
			saveObjectIdentifiedByQsh(ret, qsh);
		
		return ret;
	}
	
	private void saveObjectIdentifiedByQsh(S ret,Q qsh) {
		logger.debug("Note: Quickloading. Persisting object for later use in file " + UseSnapUtil.serializedObjectFileName(qsh));
		UseSnapUtil.dumpToFile(ret,  qsh);
	}

	private S loadObjectByQsh(QueryHolder qsh) {		
		@SuppressWarnings("unchecked") // following cast cannot be avoided since we rely on a Serializable
		S ret = (S) UseSnapUtil.fromFile(qsh);		
		if (ret == null) {			
				logger.debug("Note: Quickloading, but we do not have previousely persisted object.");
		} else {
			logger.debug("Note: Quickloading. Obtaining previousely persisted object from file " + UseSnapUtil.serializedObjectFileName(qsh));
		}
		return ret;
	}
}
