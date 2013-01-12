package no.webtech.serialize.resthttppoc;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import no.webtech.serialize.plainapi.ObjectLoader;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PocHttpRestStringBufferObjectLoader implements ObjectLoader<StringBuffer, PocHttpSessionController, PocQueryHolder> {
	private static Logger logger = LoggerFactory.getLogger(PocHttpRestStringBufferObjectLoader.class);
	public StringBuffer loadObject(PocHttpSessionController sess,PocQueryHolder query) {
		
		StringBuffer sb = new StringBuffer();
	    // Create an instance of HttpClient.
	    HttpClient client = new HttpClient();
	    
	    // Create a method instance.
	    String req = "http://"+sess.getHost() + ":" + sess.getPort() + query.getQuery();
	    
	    GetMethod method = new GetMethod(uriPercentEncode(req));
	    
	    Header[] ha = query.getHeaders();
	    if (ha != null) {
	    	for (Header h: ha) {
	    		method.setRequestHeader(h);
	    	}
	    }
		client.getParams().setParameter("http.protocol.content-charset", "UTF-8");	
		
	    
	    logger.debug("Request: "+req);
	    // Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

	    try {
	      // Execute the method.
	      int statusCode = client.executeMethod(method);

	      if (statusCode != HttpStatus.SC_OK) {
	        logger.error("Method failed: " + method.getStatusLine() + " failing request: "+req);
	        return null; //method.getStatusLine().toString();
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
		return sb; //.toString();
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

	
}
