package com.fabianonunes.reserva.decoder;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.filter.FlateFilter;

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStream;

public class FlateDecoder implements IDecoder {

	PdfStream stream;

	public FlateDecoder(PdfStream dict) {
		setDict(dict);
	}

	protected void setDict(PdfStream dict) {
		this.stream = dict;
	}

	protected PdfStream getDict() {
		return stream;
	}

	@Override
	public BufferedImage decode(byte[] data) throws DecoderException {

		try {
			byte[] bytes = PdfReader.getStreamBytes((PRStream) stream);

			// bytes = flateDecode(data).toByteArray();

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

	public static ByteArrayOutputStream flateDecode(byte[] data)
			throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		
		// {fn}: Para descompactar, utilizei o `FlateFilter` do PDFBox.
		FlateFilter filter = new FlateFilter();

		filter.decode(bais, baos, new COSDictionary(), 0);

		return baos;

	}

}
