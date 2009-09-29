package com.fabianonunes.solar.thumbs.model;

import java.awt.image.BufferedImage;

public class PageImage {

	private Double standardDeviation;
	private BufferedImage image;
	private Integer pageNumber;

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setStandardDeviation(Double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}

	public Double getStandardDeviation() {
		return standardDeviation;
	}

}
