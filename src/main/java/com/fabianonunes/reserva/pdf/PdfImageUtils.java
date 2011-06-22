package com.fabianonunes.reserva.pdf;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;

import com.fabianonunes.reserva.decoder.DecoderException;
import com.fabianonunes.reserva.decoder.FaxDecoder;
import com.fabianonunes.reserva.decoder.FlateDecoder;
import com.fabianonunes.reserva.decoder.JBIG2DecoderC;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStream;

public class PdfImageUtils {

	public static byte[] extractImageFromPageAsByteArray(PdfDictionary page)
			throws IOException {

		PdfName subtype;
		PdfStream stream;
		PdfDictionary resources;
		PdfDictionary xobjs;

		resources = (PdfDictionary) PdfReader.getPdfObject(page
				.get(PdfName.RESOURCES));

		xobjs = (PdfDictionary) PdfReader.getPdfObject(resources
				.get(PdfName.XOBJECT));

		if (xobjs != null) {

			for (Iterator<?> it = xobjs.getKeys().iterator(); it.hasNext();) {

				PdfObject obj = xobjs.get((PdfName) it.next());

				if (obj.isIndirect()) {

					PdfDictionary imageObj = (PdfDictionary) PdfReader
							.getPdfObject(obj);

					subtype = imageObj.getAsName(PdfName.SUBTYPE);

					if (PdfName.IMAGE.equals(subtype)) {

						stream = (PdfStream) imageObj;

						byte[] img = PdfReader
								.getStreamBytesRaw((PRStream) stream);

						PdfName filter = stream.getAsName(PdfName.FILTER);

						// TODO: Criar classe decoder para CCITTFaxDecode
						if (filter.equals(PdfName.CCITTFAXDECODE)) {

							FaxDecoder decoder = new FaxDecoder(stream);

							return decoder.decodeAsByteArray(img);

						} else {

							return null;

						}
					}
				}
			}
		}
		return null;

	}

	public static BufferedImage extractImageFromPage(PdfDictionary page)
			throws IOException, DecoderException {

		BufferedImage pageImage = null;
		PdfName subtype;
		PdfStream stream;
		PdfDictionary resources;
		PdfDictionary xobjs;

		resources = (PdfDictionary) PdfReader.getPdfObject(page
				.get(PdfName.RESOURCES));

		xobjs = (PdfDictionary) PdfReader.getPdfObject(resources
				.get(PdfName.XOBJECT));

		if (xobjs != null) {

			for (Iterator<?> it = xobjs.getKeys().iterator(); it.hasNext();) {

				PdfObject obj = xobjs.get((PdfName) it.next());

				if (obj.isIndirect()) {

					PdfDictionary imageObj = (PdfDictionary) PdfReader
							.getPdfObject(obj);

					subtype = imageObj.getAsName(PdfName.SUBTYPE);

					if (PdfName.IMAGE.equals(subtype)) {

						stream = (PdfStream) imageObj;

						pageImage = getStreamAsBufferedImage(stream);

					}
				}
			}
		}

		return pageImage;

	}

	// TODO: criar classe
	public static BufferedImage getStreamAsBufferedImage(PdfStream stream)
			throws IOException, DecoderException {

		PdfName filter;
		BufferedImage image;

		byte[] img = PdfReader.getStreamBytesRaw((PRStream) stream);

		filter = stream.getAsName(PdfName.FILTER);
		
		// TODO: Criar classe decoder para CCITTFaxDecode
		if (filter.equals(PdfName.CCITTFAXDECODE)) {

			FaxDecoder decoder = new FaxDecoder(stream);

			image = decoder.decode(img);

		} else if (filter.toString().equals("/JBIG2Decode")) {

			image = JBIG2DecoderC.getInstance().decode(img);

		} else if (filter.equals(PdfName.FLATEDECODE)) {

			FlateDecoder decoder = new FlateDecoder(stream);
			
			image = decoder.decode(img);

		} else {

			image = null;

		}

		return image;

	}

}
