package com.fabianonunes.reserva.decoder;

import java.awt.image.BufferedImage;

public interface IDecoder {
	
	public BufferedImage decode(byte[] data) throws DecoderException;

}
