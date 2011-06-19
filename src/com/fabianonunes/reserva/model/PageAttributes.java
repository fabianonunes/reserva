package com.fabianonunes.reserva.model;

public class PageAttributes {

	private Integer pageNumber;
	private Double key;
	private Double bottomKey;
	private Double topKey;
	private Double middleKey;
	private String textInPage;

	public PageAttributes(Integer pageNumber, Double key, Double bottomKey,
			Double topKey, Double middleKey, String textInPage) {

		setPageNumber(pageNumber);
		setKey(key);
		setBottomKey(bottomKey);
		setTopKey(topKey);
		setMiddleKey(middleKey);
		setTextInPage(textInPage);
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setKey(Double key) {
		this.key = key;
	}

	public Double getKey() {
		return key;
	}

	public void setBottomKey(Double bottomKey) {
		this.bottomKey = bottomKey;
	}

	public Double getBottomKey() {
		return bottomKey;
	}

	public void setTopKey(Double topKey) {
		this.topKey = topKey;
	}

	public Double getTopKey() {
		return topKey;
	}

	public void setMiddleKey(Double middleKey) {
		this.middleKey = middleKey;
	}

	public Double getMiddleKey() {
		return middleKey;
	}

	public void setTextInPage(String textInPage) {
		this.textInPage = textInPage;
	}

	public String getTextInPage() {
		return textInPage;
	}

}
