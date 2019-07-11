package com.ypyproductions.restaurants.parsingdata;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ypyproductions.restaurants.constanst.IMyRestaurantConstants;
import com.ypyproductions.restaurants.object.CategoryObject;
import com.ypyproductions.restaurants.object.FilterObject;
import com.ypyproductions.restaurants.object.ItemObject;
import com.ypyproductions.restaurants.object.OfferObject;
import com.ypyproductions.restaurants.object.WishListObject;
import com.ypyproductions.utils.DBLog;
import com.ypyproductions.utils.StringUtils;

public class JsonParsingUtils implements IMyRestaurantConstants {

	public final static String TAG = JsonParsingUtils.class.getSimpleName();
	public final static String STATUS = "status";
	public final static String MESSAGE = "message";

	public static ArrayList<ItemObject> parseListItemObject(String mData) {
		DBLog.d(TAG, "============>parseListItemObject data=" + mData);
		if (!StringUtils.isEmptyString(mData)) {
			try {
				JSONArray mJsonArray = new JSONArray(mData);
				int lent = mJsonArray.length();
				if (lent > 0) {
					ArrayList<ItemObject> listItemObjects = new ArrayList<ItemObject>();
					for (int i = 0; i < lent; i++) {
						JSONObject mJsonObject = mJsonArray.getJSONObject(i);
						String id = mJsonObject.getString("id");
						String name = mJsonObject.getString("name");
						String price = mJsonObject.getString("price");
						String image = mJsonObject.getString("image");
						String description = mJsonObject.getString("description");
						String tag = mJsonObject.getString("tag");
						String category = mJsonObject.getString("category");
						String featured = mJsonObject.getString("featured");

						ItemObject mItemObject = new ItemObject(id, name, price, image, description, tag, category, featured);
						listItemObjects.add(mItemObject);
					}
					return listItemObjects;
				}
			}
			catch (Exception e) {
				DBLog.d(TAG, "================>parseListImageObject error=" + e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}

	public static WishListObject parseListWishListObject(String mData) {
		DBLog.d(TAG, "============>parseListWishListObject data=" + mData);
		if (!StringUtils.isEmptyString(mData)) {
			try {
				JSONObject mJsonObject = new JSONObject(mData);
				String id = mJsonObject.getString("id");
				String name = mJsonObject.getString("name");
				WishListObject mWishListObject = new WishListObject(id, name);
				JSONArray mJsonArray2 = mJsonObject.getJSONArray("items");
				int size1 = mJsonArray2.length();
				if (size1 > 0) {
					for (int i = 0; i < size1; i++) {
						JSONObject mItemObject = mJsonArray2.getJSONObject(i);
						String idItem = mItemObject.getString("id");
						String nameItem = mItemObject.getString("name");
						String price = mItemObject.getString("price");
						String image = mItemObject.getString("image");
						String description = mItemObject.getString("description");
						String tag = mItemObject.getString("tag");
						String category = mItemObject.getString("category");
						String feature = mItemObject.getString("feature");
						int quantity = mItemObject.getInt("quantity");

						ItemObject mItem = new ItemObject(idItem, nameItem, price, image, description, tag, category, feature);
						mItem.setQuantity(quantity);
						mWishListObject.addWishList(mItem);
					}
				}
				DBLog.d(TAG, "============>listWishtListObjects size=" + mWishListObject.getListItemObjects().size());
				return mWishListObject;
			}
			catch (Exception e) {
				DBLog.d(TAG, "================>parseListImageObject error=" + e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}

	public static ArrayList<OfferObject> parseListOfferObject(String mData) {
		DBLog.d(TAG, "============>parseListOfferObject data=" + mData);
		if (!StringUtils.isEmptyString(mData)) {
			try {
				JSONArray mJsonArray = new JSONArray(mData);
				int lent = mJsonArray.length();
				if (lent > 0) {
					ArrayList<OfferObject> listOfferObjects = new ArrayList<OfferObject>();
					for (int i = 0; i < lent; i++) {
						JSONObject mJsonObject = mJsonArray.getJSONObject(i);
						String id = mJsonObject.getString("id");
						String title = mJsonObject.getString("title");
						String image = mJsonObject.getString("image");
						String text = mJsonObject.getString("description");

						OfferObject mObject = new OfferObject(id, title, image, text);
						listOfferObjects.add(mObject);

					}
					DBLog.d(TAG, "================>listOfferObjects size=" + listOfferObjects.size());
					return listOfferObjects;
				}
			}
			catch (Exception e) {
				DBLog.d(TAG, "================>parseListImageObject error=" + e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}

	public static ArrayList<CategoryObject> parseListCategoryObject(String mData) {
		if (!StringUtils.isEmptyString(mData)) {
			try {
				JSONArray mJsonArray = new JSONArray(mData);
				int lent = mJsonArray.length();
				ArrayList<CategoryObject> listCatObjects = new ArrayList<CategoryObject>();
				if (lent > 0) {
					for (int i = 0; i < lent; i++) {
						JSONObject mJsonObject = mJsonArray.getJSONObject(i);
						String id = mJsonObject.getString("id");
						String name = mJsonObject.getString("name");

						CategoryObject mCateogoryObject = new CategoryObject(id, name);
						listCatObjects.add(mCateogoryObject);
					}
				}
				DBLog.d(TAG, "===========>size cat=" + listCatObjects.size());
				return listCatObjects;
			}
			catch (Exception e) {
				DBLog.d(TAG, "================>parseListImageObject error=" + e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}

	public static ArrayList<FilterObject> parseListFilterObject(String mData) {
		DBLog.d(TAG, "============>parseListFilterObject data=" + mData);
		if (!StringUtils.isEmptyString(mData)) {
			try {
				JSONArray mJsonArray = new JSONArray(mData);
				int lent = mJsonArray.length();
				ArrayList<FilterObject> listFilterObjects = new ArrayList<FilterObject>();
				if (lent > 0) {
					for (int i = 0; i < lent; i++) {
						JSONObject mJsonObject = mJsonArray.getJSONObject(i);
						String id = mJsonObject.getString("id");
						String tag = mJsonObject.getString("tag");
						FilterObject mFilterObject = new FilterObject(id, tag);
						listFilterObjects.add(mFilterObject);
					}
					DBLog.d(TAG, "============>size parseListFilterObject=" + listFilterObjects.size());
				}
				return listFilterObjects;
			}
			catch (Exception e) {
				DBLog.d(TAG, "================>parseListImageObject error=" + e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}

}
