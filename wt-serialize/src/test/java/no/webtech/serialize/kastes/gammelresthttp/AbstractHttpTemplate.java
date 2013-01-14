package no.webtech.serialize.kastes.gammelresthttp;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import no.webtech.serialize.coreapi.RowMapper;
import no.webtech.serialize.coreapi.SessionController;
import no.webtech.serialize.resthttppoc.PocHttpSessionController;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractHttpTemplate extends no.webtech.serialize.unusedclasses.AbstractDetachableSessionSupport {
	private static Logger logger = LoggerFactory.getLogger(AbstractHttpTemplate.class);

	@Override
	protected Serializable internalObtainSerializableObject(SessionController sessinternal, Object... query) {
		
		StringBuffer sb = new StringBuffer();
		PocHttpSessionController sess = (PocHttpSessionController) sessinternal;
		
	    // Create an instance of HttpClient.
	    HttpClient client = new HttpClient();
	    
	    // Create a method instance.
	    String req = "http://"+sess.getHost() + ":" + sess.getPort() + query;
	    GetMethod method = new GetMethod(uriPercentEncode(req));	    
	    // Hent fra sessionController
	    String value = "application/xml";
		String name = "Accept";
		Header header = new Header(name , value);
		method.setRequestHeader(header);
		client.getParams().setParameter("http.protocol.content-charset", "UTF-8");
		
		
	    
	    logger.debug("Request: "+req);
	    // Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

	    try {
	      // Execute the method.
	      int statusCode = client.executeMethod(method);

	      if (statusCode != HttpStatus.SC_OK) {
	        logger.error("Method failed: " + method.getStatusLine() + " failing request: "+req);
	        return method.getStatusLine().toString();
	      }

	      // Read the response body.	     
	      sb.append(new String(method.getResponseBody()));

	      // Deal with the response.
	      // Use caution: ensure correct character encoding and is not binary data
	      //byte[] responseBody = method.getResponseBody();
	     // System.out.println(sb.toString());

	    } catch (HttpException e) {
	      System.err.println("Fatal protocol violation: " + e.getMessage());
	      e.printStackTrace();
	    } catch (IOException e) {
	      System.err.println("Fatal transport error: " + e.getMessage());
	      e.printStackTrace();
	    } finally {
	      // Release the connection.
	      method.releaseConnection();
	    }		
		return sb.toString();
	}

	private String uriPercentEncode(String req) {
	    URI u;
		try {
			u = new URI(req);
			return u.toASCIIString();
		} catch (URISyntaxException e) {
			
			e.printStackTrace();
		}
		return null;
	}

	//@Override
	public String queryForString(SessionController sessionController, String qsh) {
		return (String) internalQuery(sessionController,qsh);
	}

	//@Override
	public List<String> queryForListOfStrings(SessionController sessionController, String qsh) {
		throw new UnsupportedOperationException("this implemntation does not support this method");
	}

	//@Override
	public <T> List<T> queryForList(SessionController sessionController, RowMapper<T> rowMapper, String qsh) {
		throw new UnsupportedOperationException("this implemntation does not support this method");
	}

	//@Override
	public <T> T queryForObject(SessionController sessionController, RowMapper<T> rowMapper, String qsh) {
		throw new UnsupportedOperationException("this implemntation does not support this method");
	}
}
