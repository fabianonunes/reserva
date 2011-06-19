package com.fabianonunes.cleaner.decoder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.jpedal.jbig2.JBIG2Decoder;
import org.jpedal.jbig2.JBIG2Exception;



public class JBIG2DecoderJPedal implements IDecoder {
	
	private JBIG2DecoderJPedal() {
	}
 
	private static class Holder {
		private static final JBIG2DecoderJPedal decoder = new JBIG2DecoderJPedal();
	}
 
	public static JBIG2DecoderJPedal getInstance() {
		return Holder.decoder;
	}

	
	@Override
	public BufferedImage decode(byte[] img) throws DecoderException {

		JBIG2Decoder decoder = new JBIG2Decoder();

		// {fn}: Executar o `decodeJBIG2` é mais eficiente em
		// `ByteArrayInputStream`do que em `byte[]`
		try {
			decoder.decodeJBIG2(new ByteArrayInputStream(img));
		} catch (IOException e) {
			throw new DecoderException(e);
		} catch (JBIG2Exception e) {
			throw new DecoderException(e);
		}

		BufferedImage bImage = decoder.getPageAsBufferedImage(0);

		return bImage;

	}

}
