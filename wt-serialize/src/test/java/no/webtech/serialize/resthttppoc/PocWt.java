package no.webtech.serialize.resthttppoc;

import no.webtech.serialize.plainapi.ObjectLoaderProxy;

import org.apache.log4j.BasicConfigurator;

public class PocWt {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PocWt().go();

	}

	private void go() {		
		
		
		//String aa = "\u00e5";
		BasicConfigurator.configure();

		PocHttpSessionController ctrl = new PocHttpSessionController() {
			public int getPort() { return 16089; }
			public String getHost() { return "svvufastrh02"; }
		};
			
		 
		String sQuery = "/mime-rest/services/search?query=" +
				//"bil" +
				//"vei" +
				"b\u00e5t" + /* båt */
				"&hits=10&offset=0&user=TYTORS&yalla_source=arkiv.FOLDER&" +
				"source=saksmappe&facet:enable=false&header:enable=false";
		System.out.println(sQuery);
		PocQueryHolder mqh = new PocQueryHolder(sQuery);		
		PocHttpRestStringBufferObjectLoader rhp = new PocHttpRestStringBufferObjectLoader();	
		ObjectLoaderProxy<StringBuffer, PocHttpSessionController, PocQueryHolder> qlp 
		= new ObjectLoaderProxy<StringBuffer, PocHttpSessionController, PocQueryHolder>().lazy(rhp);
		StringBuffer sb = qlp.loadObject(ctrl, mqh);		
		System.out.println("END\n"+sb);
		
	}

	
}
