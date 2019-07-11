package com.ypyproductions.restaurants.object;

import com.ypyproductions.restaurants.constanst.IMyRestaurantConstants;

public class OfferObject implements IMyRestaurantConstants{
	private String id;
	private String title;
	private String image;
	private String text;
	
	public OfferObject(String id, String title, String image, String text) {
		super();
		this.id = id;
		this.title = title;
		this.image = image;
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}


}
