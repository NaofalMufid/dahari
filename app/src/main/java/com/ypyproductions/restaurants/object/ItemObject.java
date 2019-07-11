package com.ypyproductions.restaurants.object;

import java.util.ArrayList;

import org.json.JSONObject;

import com.ypyproductions.restaurants.constanst.IMyRestaurantConstants;
import com.ypyproductions.utils.StringUtils;

public class ItemObject implements IMyRestaurantConstants {
	private String id;
	private String name;
	private String price;
	private String image;
	private String description;
	private String tag;
	private String category;
	private String feature;
	private ArrayList<String> listTags = new ArrayList<String>();
	private int quantity;

	public ItemObject(String id, String name, String price, String image, String description, String tag, String category, String feature) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.image = image;
		this.description = description;
		this.tag = tag;
		this.category = category;
		this.feature = feature;
		if (!StringUtils.isEmptyString(tag)) {
			String[] datas = tag.split("\\s");
			if (datas != null && datas.length > 0) {
				for (String str : datas) {
					if (!StringUtils.isEmptyString(str) && !str.equalsIgnoreCase("#")) {
						listTags.add(str);
					}
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public boolean isFeatured() {
		return !StringUtils.isEmptyString(feature) && feature.equals("1");

	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	

	public JSONObject toJson() {
		try {
			JSONObject mJsonObject = new JSONObject();
			mJsonObject.put("id", id);
			mJsonObject.put("name", name);
			mJsonObject.put("price", price);
			mJsonObject.put("image", image);
			mJsonObject.put("description", description);
			mJsonObject.put("tag", tag);
			mJsonObject.put("category", category);
			mJsonObject.put("feature", feature);
			mJsonObject.put("quantity", quantity);

			return mJsonObject;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<String> getListTags() {
		return listTags;
	}

	public void setListTags(ArrayList<String> listTags) {
		this.listTags = listTags;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	

}
