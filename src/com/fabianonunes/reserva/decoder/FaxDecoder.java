package com.fabianonunes.reserva.decoder;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfStream;
import com.sun.pdfview.decode.CCITTFaxDecode;
import com.sun.pdfview.decode.CCITTFaxDecoder;

public class FaxDecoder extends CCITTFaxDecode implements IDecoder {

	PdfStream stream;

	public FaxDecoder(PdfStream dict) {
		setDict(dict);
	}

	protected void setDict(PdfStream dict) {
		this.stream = dict;
	}

	protected PdfStream getDict() {
		return stream;
	}

	protected static byte[] decode(PdfStream dict, byte[] source)
			throws IOException {

		Integer width = 1728;

		PdfObject widthDef = dict.get(PdfName.WIDTH);

		if (widthDef == null) {
			widthDef = dict.get(PdfName.W);
		}

		if (widthDef != null) {
			width = Integer.parseInt(widthDef.toString());
		}

		Integer height = 0;

		PdfObject heightDef = dict.get(PdfName.HEIGHT);

		if (heightDef == null) {
			heightDef = dict.get(PdfName.H);
		}
		if (heightDef != null) {
			height = Integer.parseInt(heightDef.toString());
		}

		PdfDictionary parms = dict.getAsDict(PdfName.DECODEPARMS);

		//
		Integer columns = (parms.getAsNumber(PdfName.COLUMNS) == null) ? width
				: parms.getAsNumber(PdfName.COLUMNS).intValue();

		Integer rows = (parms.getAsNumber(PdfName.ROWS) == null) ? height
				: parms.getAsNumber(PdfName.ROWS).intValue();

		Integer k = (parms.getAsNumber(PdfName.K) == null) ? 0 : parms
				.getAsNumber(PdfName.K).intValue();

		int size = rows * ((columns + 7) >> 3);
		byte[] destination = new byte[size];

		Boolean align = (parms.getAsBoolean(PdfName.ENCODEDBYTEALIGN) == null) ? false
				: parms.getAsBoolean(PdfName.ENCODEDBYTEALIGN).booleanValue();

		Boolean blackIs1 = (dict.get(PdfName.BLACKIS1) == null) ? false
				: Boolean.parseBoolean(dict.get(PdfName.BLACKIS1).toString());

		CCITTFaxDecoder decoder = new CCITTFaxDecoder(1, columns, rows);
		decoder.setAlign(align);
		if (k == 0) {
			decoder.decodeT41D(destination, source, 0, rows);
		} else if (k > 0) {
			decoder.decodeT42D(destination, source, 0, rows);
		} else if (k < 0) {
			decoder.decodeT6(destination, source, 0, rows);
		}

		if (!blackIs1) {
			for (int i = 0; i < destination.length; i++) {
				// bitwise not
				destination[i] = (byte) ~destination[i];
			}
		}

		return destination;
	}

	public static byte[] decodes(PdfStream dict, byte[] codedData)
			throws IOException {

		return decode(dict, codedData);

	}
	
	public byte[] decodeAsByteArray(byte[] data) throws IOException{
		
		byte[] bytes = decode(getDict(), data);
		
		return bytes;
		
	}

	@Override
	public BufferedImage decode(byte[] data) throws DecoderException {

		byte[] bytes;

		try {

			bytes = decode(getDict(), data);

			int width = stream.getAsNumber(PdfName.WIDTH).intValue();
			int height = stream.getAsNumber(PdfName.HEIGHT).intValue();
			int len = bytes.length;

			byte[] copy = new byte[len];

			System.arraycopy(bytes, 0, copy, 0, len);
			DataBuffer db = new DataBufferByte(copy, copy.length);

			WritableRaster raster = Raster.createPackedRaster(db, width,
					height, 1, null);

			BufferedImage image = new BufferedImage(width, height,
					BufferedImage.TYPE_BYTE_BINARY);

			image.setData(raster);

			return image;

		} catch (IOException e) {

			throw new DecoderException(e);

		}

	}

}
