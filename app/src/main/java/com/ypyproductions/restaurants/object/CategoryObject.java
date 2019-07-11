
package com.ypyproductions.restaurants.object;

import java.util.ArrayList;
import java.util.Locale;

import android.view.View;

import com.ypyproductions.utils.StringUtils;

public class CategoryObject {
	private String id;
	private String name;
	private ArrayList<ItemObject> listItemObjects = new ArrayList<ItemObject>();
	private ArrayList<ItemObject> listSearchObjects = new ArrayList<ItemObject>();
	private View view;
	
	public CategoryObject(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public void addItemObject(ItemObject mItemObject){
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
	
	
	public ArrayList<ItemObject> getListSearchObjects() {
		return listSearchObjects;
	}

	public ArrayList<ItemObject> findListSearchItemObject(String keyword){
		if(listItemObjects!=null && listItemObjects.size()>0 && !StringUtils.isEmptyString(keyword)){
			if(listSearchObjects!=null){
				listSearchObjects.clear();
			}
			for(ItemObject mItemObject:listItemObjects){
				if(mItemObject.getName().toLowerCase(Locale.US).startsWith(keyword.toLowerCase(Locale.US))){
					listSearchObjects.add(mItemObject);
				}
			}
		}
		return listSearchObjects;
	}
	
	public ArrayList<ItemObject> filterItemToList(ArrayList<ItemObject> listItems){
		if(listItemObjects!=null && listItemObjects.size()>0 && listItems!=null && listItems.size()>0){
			if(listSearchObjects!=null){
				listSearchObjects.clear();
			}
			for(ItemObject mItemObject:listItems){
				if(listItemObjects.indexOf(mItemObject)>=0){
					listSearchObjects.add(mItemObject);
				}
			}
		}
		return listSearchObjects;
		
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}
	
}
