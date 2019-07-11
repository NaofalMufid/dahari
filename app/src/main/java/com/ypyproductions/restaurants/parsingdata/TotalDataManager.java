package com.ypyproductions.restaurants.parsingdata;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;

import com.ypyproductions.net.task.IDBCallback;
import com.ypyproductions.restaurants.constanst.IMyRestaurantConstants;
import com.ypyproductions.restaurants.object.CategoryObject;
import com.ypyproductions.restaurants.object.FilterObject;
import com.ypyproductions.restaurants.object.ItemObject;
import com.ypyproductions.restaurants.object.OfferObject;
import com.ypyproductions.restaurants.object.WishListObject;
import com.ypyproductions.utils.DBListExcuteAction;
import com.ypyproductions.utils.DBLog;
import com.ypyproductions.utils.IOUtils;
import com.ypyproductions.utils.StringUtils;

public class TotalDataManager implements IMyRestaurantConstants {

	public static final String TAG = TotalDataManager.class.getSimpleName();

	private static TotalDataManager totalDataManager;

	private ArrayList<ItemObject> listItems;
	private ArrayList<FilterObject> listFilterObjects;
	private ArrayList<ItemObject> listCurrentItemObjects;
	private ArrayList<ItemObject> listFeatureItemObjects;
	private ArrayList<OfferObject> listOfferObjects;
	private ArrayList<CategoryObject> listCategoryObjects;
	private WishListObject wishListObject;

	public static TotalDataManager getInstance() {
		if (totalDataManager == null) {
			totalDataManager = new TotalDataManager();
		}
		return totalDataManager;
	}

	private TotalDataManager() {

	}

	public void onDestroy() {
		onResetData();
		if (listCategoryObjects != null) {
			listCategoryObjects.clear();
			listCategoryObjects = null;
		}
		wishListObject = null;
		totalDataManager = null;
	}

	public WishListObject getWishListObject() {
		return wishListObject;
	}

	public void setWishListObject(WishListObject wishListObject) {
		this.wishListObject = wishListObject;
	}

	public void onResetData() {
		if (listItems != null) {
			listItems.clear();
			listItems = null;
		}
		if (listCurrentItemObjects != null) {
			listCurrentItemObjects.clear();
			listCurrentItemObjects = null;
		}
		if (listFeatureItemObjects != null) {
			listFeatureItemObjects.clear();
			listFeatureItemObjects = null;
		}
		if (listFilterObjects != null) {
			listFilterObjects.clear();
			listFilterObjects = null;
		}
	}

	public ArrayList<ItemObject> getListItems() {
		return listItems;
	}

	public void setListItems(ArrayList<ItemObject> listFeatureItems) {
		this.listItems = listFeatureItems;
	}

	public ArrayList<FilterObject> getListFilterObjects() {
		return listFilterObjects;
	}

	public void setListFilterObjects(ArrayList<FilterObject> listFilterObjects) {
		this.listFilterObjects = listFilterObjects;
	}

	public ArrayList<ItemObject> getListCurrentItemObjects() {
		return listCurrentItemObjects;
	}

	public void setListCurrentItemObjects(ArrayList<ItemObject> listCurrentItemObjects) {
		this.listCurrentItemObjects = listCurrentItemObjects;
	}

	public ArrayList<ItemObject> getListFeatureItemObjects() {
		return listFeatureItemObjects;
	}

	public void setListFeatureItemObjects(ArrayList<ItemObject> listFeatureItemObjects) {
		this.listFeatureItemObjects = listFeatureItemObjects;
	}

	public ItemObject getItemObject(String id) {
		if (listItems != null && listItems.size() > 0 && id != null) {
			for (ItemObject mItemObject : listItems) {
				if (mItemObject.getId().equalsIgnoreCase(id)) {
					return mItemObject;
				}
			}
		}
		return null;
	}

	public OfferObject getOfferObject(String id) {
		if (listOfferObjects != null && listOfferObjects.size() > 0 && id != null) {
			for (OfferObject mOfferObject : listOfferObjects) {
				if (mOfferObject.getId().equalsIgnoreCase(id)) {
					return mOfferObject;
				}
			}
		}
		return null;
	}

	public boolean isWishListItem(String id) {
		if (wishListObject != null && !StringUtils.isEmptyString(id)) {
			ArrayList<ItemObject> mListItems = wishListObject.getListItemObjects();
			for (ItemObject mItemObject : mListItems) {
				if (mItemObject.getId().equalsIgnoreCase(id)) {
					return true;
				}
			}
		}
		return false;
	}

	public void addItemToWishList(final Context mContext, ItemObject mItemObject) {
		if (wishListObject != null && mItemObject != null) {
			mItemObject.setQuantity(1);
			wishListObject.addWishList(mItemObject);
			DBListExcuteAction.getInstance().queueAction(new IDBCallback() {
				@Override
				public void onAction() {
					saveWishList(mContext);
				}
			});
		}
	}

	public ArrayList<ItemObject> getListItemsObject(String keyword) {
		DBLog.d(TAG, "=============>start search keyword=" + keyword);
		ArrayList<ItemObject> listItemObjects = new ArrayList<ItemObject>();
		if (listItems != null && listItems.size() > 0 && !StringUtils.isEmptyString(keyword)) {
			for (ItemObject mItemObject : listItems) {
				if (mItemObject.getName().toLowerCase(Locale.US).startsWith(keyword.toLowerCase(Locale.US))) {
					listItemObjects.add(mItemObject);
				}
			}
		}
		return listItemObjects;
	}

	public ArrayList<CategoryObject> getListCategoryObjects(String keyword) {
		DBLog.d(TAG, "=============>start search keyword=" + keyword);
		ArrayList<CategoryObject> listCatObjects = new ArrayList<CategoryObject>();
		if (listCategoryObjects != null && listCategoryObjects.size() > 0) {
			for (CategoryObject mCategoryObject : listCategoryObjects) {
				ArrayList<ItemObject> listItems = mCategoryObject.findListSearchItemObject(keyword);
				if (listItems != null && listItems.size() > 0) {
					listCatObjects.add(mCategoryObject);
				}
			}
		}
		DBLog.d(TAG, "=============>listCatObjects=" + listCatObjects.size());
		return listCatObjects;
	}

	public ArrayList<CategoryObject> getListCategoryObjectsFromListItem(ArrayList<ItemObject> listItems) {
		ArrayList<CategoryObject> listCatObjects = new ArrayList<CategoryObject>();
		if (listCategoryObjects != null && listCategoryObjects.size() > 0) {
			for (CategoryObject mCategoryObject : listCategoryObjects) {
				ArrayList<ItemObject> listSearchItems = mCategoryObject.filterItemToList(listItems);
				if (listSearchItems != null && listSearchItems.size() > 0) {
					listCatObjects.add(mCategoryObject);
				}
			}
		}
		DBLog.d(TAG, "=============>listCatObjects=" + listCatObjects.size());
		return listCatObjects;
	}

	public void removeItemInWishList(final Context mContext, ItemObject mItemObject) {
		if (wishListObject != null && mItemObject != null) {
			wishListObject.removeItem(mItemObject.getId());
			DBListExcuteAction.getInstance().queueAction(new IDBCallback() {
				@Override
				public void onAction() {
					saveWishList(mContext);
				}
			});
		}
	}

	public synchronized void saveWishList(Context mContext) {
		File mFile = IOUtils.getDiskCacheDir(mContext, DIR_FAVORITE);
		if (!mFile.exists()) {
			mFile.mkdirs();
		}
		if (wishListObject != null) {
			IOUtils.writeString(mFile.getAbsolutePath(), FILE_FAVORITE_DISH, wishListObject.toJson().toString());
			return;
		}
		IOUtils.writeString(mFile.getAbsolutePath(), FILE_FAVORITE_DISH, "");
	}

	public ArrayList<OfferObject> getListOfferObjects() {
		return listOfferObjects;
	}

	public void setListOfferObjects(ArrayList<OfferObject> listOfferObjects) {
		this.listOfferObjects = listOfferObjects;
	}

	public ArrayList<CategoryObject> getListCategoryObjects() {
		return listCategoryObjects;
	}

	public void setListCategoryObjects(ArrayList<CategoryObject> listCategoryObjects) {
		this.listCategoryObjects = listCategoryObjects;
	}

}
