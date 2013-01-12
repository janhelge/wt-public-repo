package no.webtech.serialize.impl;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import no.webtech.enig.util.StackableBase64Decoder;

public class TheObject {
//	private static Logger logger = LoggerFactory.getLogger(TheObject.class);
	
	private TheObject(){}

	private int classNameOffset = -1;
	private int classNameLen = -1;
	private int commentOffset = -1;
	private int commentLen = -1;
	private int bufferOffset = -1;
	private int bufferLen = -1;
	private int entryEnd = -1;
	private byte[] baseBuffer = null;

	public String getClassName() {
		return new String(baseBuffer,classNameOffset,classNameLen);
	}

	public String getComment() {
		return new String(baseBuffer,commentOffset,commentLen);
	}

	private String getBuffer() {
		return new String(baseBuffer,bufferOffset,bufferLen);
	}

	public int getEntryEnd() {
		return entryEnd;
	}

	
	public static TheObject createInstance(byte[] ba, int startFrom) {
		int iStartClassName = -1;
		int iEndClassNamePlusSpace = -1;
		int iEndCommentPlus5 = -1;
		int iEndBufferPlus25 = -1;
		TheObject r = new TheObject();

		iStartClassName = SuperSnapUtil.searchFor(ba, "-----BEGIN SERIALIZED ".getBytes(), startFrom);
		if (iStartClassName != -1) {
			// System.out.println(">" + new String(ba, iStartClassName, 5) + "<");
			iEndClassNamePlusSpace = SuperSnapUtil.searchFor(ba, " ".getBytes(), iStartClassName);
			if (iEndClassNamePlusSpace != -1) {
				// System.out.println("ClassName: >" + new String(ba, iStartClassName, iEndClassNamePlusSpace - iStartClassName - 1) + "<");
				iEndCommentPlus5 = SuperSnapUtil.searchFor(ba, "-----".getBytes(), iEndClassNamePlusSpace);
				if (iEndCommentPlus5 != -1) {
					// System.out.println("Comment: >" + new String(ba, iEndClassNamePlusSpace, iEndCommentPlus5 - iEndClassNamePlusSpace - 5) + "<");
					iEndBufferPlus25 = SuperSnapUtil.searchFor(ba, "-----END SERIALIZED-----".getBytes(), iEndCommentPlus5);

					//System.out.println(iEndBufferPlus25 + " " + ba[iEndBufferPlus25]);
					if (iEndBufferPlus25 != -1) {
						// System.out.println("Buffer: >" + new String(ba, iEndCommentPlus5 + 1, iEndBufferPlus25 - iEndCommentPlus5 - 26) + "<");
						r.classNameOffset = iStartClassName;
						r.classNameLen = iEndClassNamePlusSpace - iStartClassName - 1;

						r.commentOffset = iEndClassNamePlusSpace;
						r.commentLen = iEndCommentPlus5 - iEndClassNamePlusSpace - 5;

						r.bufferOffset = iEndCommentPlus5 + 1;
						r.bufferLen = iEndBufferPlus25 - iEndCommentPlus5 - 26;

						r.entryEnd = iEndBufferPlus25;
						r.baseBuffer = ba;
						return r;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Retrieves the actual object to be un-serialized.
	 * @return the actual object or null if unsuccessful
	 * @throws ClassNotFoundException
	 */
	public Object getObject() /*throws ClassNotFoundException*/ {

		Object o = null;				 
		// We need approximate (bufferLen+3)*3/4, minus space for newline chars
		// We start with this formula (bufferLen+3)*3/4. This formula gives some extra bytes
		// for the line-shift (can be \r\n or \n), plus some extra for last line
		int iAllocatedWorkBufferLength=3*(bufferLen+3)/4;
		StackableBase64Decoder dec = new StackableBase64Decoder(iAllocatedWorkBufferLength);
		 
		try {
//			byte[] ba = dec.push(getBuffer()).extractAll();			
//			logger.debug("BUFFER LENGTH ESTIMATE"+
//					" ActualFacit: "+ba.length +
//					" WorkBuffer:"+iAllocatedWorkBufferLength+
//					" Delta:"+(iAllocatedWorkBufferLength-ba.length)+
//					" (ParmBufferLen:"+bufferLen +
//					" parmbufferOffset:"+ bufferOffset+")");
			
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(dec.push(getBuffer()).extractAll()));
			o = ois.readObject();
			ois.close();
		} catch (IOException e) {
			// never happens since object is only created when buffer exist
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Real exception is ClassNotFoudException", e);
		}
		// Note: may return null if unsuccessful de serialization
		return o;
	}
	
}
