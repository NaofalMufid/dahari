
package com.ypyproductions.restaurants.object;

public class FilterObject {
	private String id;
	private String tag;
	private boolean isChecked;
	private boolean isTempChecked;

	public FilterObject(String id, String tag) {
		super();
		this.id = id;
		this.tag = tag;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
		this.isTempChecked=isChecked;
	}

	public boolean isTempChecked() {
		return isTempChecked;
	}

	public void setTempChecked(boolean isTempChecked) {
		this.isTempChecked = isTempChecked;
	}
	
	public void onResetTemp(){
		this.isTempChecked=isChecked;
	}
	public void onApplyTemp(){
		this.isChecked=isTempChecked;
	}
	
	
}
