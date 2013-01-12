package no.webtech;

import no.webtech.enig.util.Base64Encoder;
import no.webtech.enig.util.StackableBase64Decoder;

public class HowtoUseB64 {

	public static void main(String[] args) {
		new HowtoUseB64().go();

	}

	private void go() {
		String original = "Hello world\nAbcDefGhix";
		char[] encode = Base64Encoder.encodeAsCharArray(new String(original).getBytes());
		String facit="SGVsbG8gd29ybGQKQWJjRGVmR2hpeA==";
		if (facit.equals(new String(encode))){
			System.out.println("Fine");
		}
		StackableBase64Decoder dec = new StackableBase64Decoder(1024);
		byte[] extractAll = dec.push(facit).extractAll();
		System.out.println(new String(extractAll));
		System.out.println(new String(encode));
	}
}
