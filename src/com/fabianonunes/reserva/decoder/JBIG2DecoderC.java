package com.fabianonunes.reserva.decoder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.media.jai.RenderedOp;
import javax.media.jai.operator.PNMDescriptor;

import org.apache.commons.io.FileUtils;


import com.sun.media.jai.codec.FileSeekableStream;

@SuppressWarnings("restriction")
public class JBIG2DecoderC implements IDecoder {

	private File decompiler;

	private JBIG2DecoderC() {
	}

	private static class Holder {
		private static final JBIG2DecoderC decoder = new JBIG2DecoderC();
	}

	public static JBIG2DecoderC getInstance() {
		return Holder.decoder;
	}

	public File getDecompiler() throws FileNotFoundException, IOException {

		if (decompiler == null) {
			decompiler = JBIG2Decompressor.getDecompressor();
		}

		return decompiler;

	}

	@Override
	public BufferedImage decode(byte[] img) throws DecoderException {

		File jbig2dec;

		try {
			jbig2dec = getDecompiler();
		} catch (FileNotFoundException e) {
			throw new DecoderException(e);
		} catch (IOException e) {
			throw new DecoderException(e);
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte one_two[] = { (byte) 0x97, (byte) 0x4A, (byte) 0x42, (byte) 0x32,
				(byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A,
				(byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x32,
				(byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0xFF,
				(byte) 0xFD, (byte) 0xFF, (byte) 0x02, (byte) 0xFE,
				(byte) 0xFE, (byte) 0xFE, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x01, (byte) 0x2A, (byte) 0xE2,
				(byte) 0x25, (byte) 0xAE, (byte) 0xA9, (byte) 0xA5,
				(byte) 0xA5, (byte) 0x38, (byte) 0xB4, (byte) 0xD9,
				(byte) 0x99, (byte) 0x9C, (byte) 0x5C, (byte) 0x8E,
				(byte) 0x56, (byte) 0xEF, (byte) 0x0F, (byte) 0x87,
				(byte) 0x27, (byte) 0xF2, (byte) 0xB5, (byte) 0x3D,
				(byte) 0x4E, (byte) 0x37, (byte) 0xEF, (byte) 0x79,
				(byte) 0x5C, (byte) 0xC5, (byte) 0x50, (byte) 0x6D,
				(byte) 0xFF, (byte) 0xAC };

		byte four[] = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03,
				(byte) 0x31, (byte) 0x00, (byte) 0x01, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x33,
				(byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

		try {

			baos.write(one_two);
			baos.write(img);
			baos.write(four);

			File tmpImage;
			tmpImage = File.createTempFile("img", "tmp");
			tmpImage.deleteOnExit();

			FileOutputStream fos = new FileOutputStream(tmpImage);
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();

			File toFile;
			toFile = File.createTempFile("tmp", "pbm");
			toFile.deleteOnExit();

			Process proc = Runtime.getRuntime().exec( //
					jbig2dec.getAbsolutePath() + " -o " + //
							toFile.getAbsolutePath() + " " + //
							tmpImage.getAbsolutePath()); //

			proc.waitFor();
			proc.destroy();

			proc = null;

			FileSeekableStream s = new FileSeekableStream(toFile);

			RenderedOp rop = PNMDescriptor.create(s, null);

			BufferedImage bImage = rop.getAsBufferedImage();

			FileUtils.deleteQuietly(toFile);
			FileUtils.deleteQuietly(tmpImage);

			return bImage;
			
		} catch (IOException e) {
			throw new DecoderException(e);
		} catch (InterruptedException e) {
			throw new DecoderException(e);
		}

	}

}
