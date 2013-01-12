package no.webtech;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import no.webtech.serialize.impl.SuperSnapUtil;
import no.webtech.serialize.impl.TheObject;

public class ExampleGenerateManyDatesAndSerializeDeserialize {
	String example = "-----BEGIN SERIALIZED java.util.Date[] comment-----\r\n"+
			"rO0ABXVyABFbTGphdmEudXRpbC5EYXRlO0MXJKAwAxjgAgAAeHAAAAAMc3IA\r\n"+
			"DmphdmEudXRpbC5EYXRlaGqBAUtZdBkDAAB4cHcIAAABO5CH2ep4c3EAfgAC\r\n"+
			"dwgAAAE7kIfd0nhzcQB+AAJ3CAAAATuQh+G6eHNxAH4AAncIAAABO5CH5aJ4\r\n"+
			"c3EAfgACdwgAAAE7kIfpinhzcQB+AAJ3CAAAATuQh+1yeHNxAH4AAncIAAAB\r\n"+
			"O5CH8Vp4c3EAfgACdwgAAAE7kIf1QnhzcQB+AAJ3CAAAATuQh/kqeHNxAH4A\r\n"+
			"AncIAAABO5CH/RJ4c3EAfgACdwgAAAE7kIgA+nhzcQB+AAJ3CAAAATuQiATi\r\n"+
			"eA==\r\n"+
			"-----END SERIALIZED-----\r\n";


	private Date generateDate(int iDay, int iMonth, int iYear, int iHour, int iMinute, int iSeconds) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.SECOND, iSeconds);
		calendar.set(Calendar.MINUTE, iMinute);
		calendar.set(Calendar.HOUR_OF_DAY, iHour);		
		calendar.set(Calendar.DAY_OF_MONTH, iDay);
		calendar.set(Calendar.MONTH, iMonth-1);		
		calendar.set(Calendar.YEAR, iYear);
		return calendar.getTime();
	}
	
	public static void main(String[] args) {
		ExampleGenerateManyDatesAndSerializeDeserialize g = new ExampleGenerateManyDatesAndSerializeDeserialize();
		StringBuffer ser = g.serializeDateArray(1212);
		g.deSerializedateArray(ser.toString());
		System.out.println(ser);
	}

	private void deSerializedateArray(String res) {
		Object object = TheObject.createInstance(res.getBytes(), 0).getObject();
		Date[] ard = (Date[]) object;
		prtArd(ard);		
	}

	private StringBuffer serializeDateArray(int nSamples) {
		Date[] ard = new Date[nSamples];
		for(int iSecs=0; iSecs<nSamples; iSecs++){
			ard[iSecs] = generateDate(12,12,2012,20,12, iSecs);
		}
		//prtArd(ard);
		return SuperSnapUtil.createSerializedFormattedOutput(ard, "comment");		
	}

	private void prtArd(Date[] ard) {
		for(Date d:ard) 
			System.out.println(d);
	}
}
