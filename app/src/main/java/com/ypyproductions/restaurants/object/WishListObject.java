package com.ypyproductions.restaurants.object;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ypyproductions.utils.StringUtils;

public class WishListObject {
	private String id;
	private String name;
	private ArrayList<ItemObject> listItemObjects = new ArrayList<ItemObject>();

	public WishListObject(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public WishListObject() {
		super();
	}
	
	public void addWishList(ItemObject mItemObject){
		if(listItemObjects!=null && mItemObject!=null){
			listItemObjects.add(mItemObject);
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

	public ArrayList<ItemObject> getListItemObjects() {
		return listItemObjects;
	}

	public void setListItemObjects(ArrayList<ItemObject> listItemObjects) {
		this.listItemObjects = listItemObjects;
	}
	
	public boolean removeItem(String id){
		if(listItemObjects!=null && listItemObjects.size()>0 && !StringUtils.isEmptyString(id)){
			Iterator<ItemObject> mIterator = listItemObjects.iterator();
			while (mIterator.hasNext()) {
				ItemObject itemObject = (ItemObject) mIterator.next();
				if(itemObject.getId().equalsIgnoreCase(id)){
					mIterator.remove();
					return true;
				}
				
			}
		}
		return false;
	}
	
	public JSONObject toJson(){
		try {
			JSONObject mJsonObject = new JSONObject();
			if(StringUtils.isEmptyString(id)){
				mJsonObject.put("id", "0");
			}
			else{
				mJsonObject.put("id", id);
			}
			if(StringUtils.isEmptyString(name)){
				mJsonObject.put("name", "Unknow Name");
			}
			else{
				mJsonObject.put("name", name);
			}
			JSONArray mJsonArray = new JSONArray();
			if(listItemObjects!=null && listItemObjects.size()>0){
				for(ItemObject mItemObject:listItemObjects){
					JSONObject mJsonO = mItemObject.toJson();
					if(mJsonO!=null){
						mJsonArray.put(mJsonO);
					}
				}
			}
			mJsonObject.put("items", mJsonArray);
			return mJsonObject;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
