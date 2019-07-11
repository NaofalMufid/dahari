package com.ypyproductions.restaurants.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ypyproductions.abtractclass.DBBaseAdapter;
import com.ypyproductions.restaurants.R;
import com.ypyproductions.restaurants.object.FilterObject;
import com.ypyproductions.utils.DBLog;

public class ListFilterAdapter extends DBBaseAdapter {

	public static final String TAG = ListFilterAdapter.class.getSimpleName();
	
	private OnListFilterListener onListFilterListener;

	public ListFilterAdapter(Activity mContext, ArrayList<? extends Object> listObjects) {
		super(mContext, listObjects);
	}

	@Override
	public View getAnimatedView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	@Override
	public View getNormalView(int position, View convertView, ViewGroup parent) {
		final ViewHolder mHolder;
		LayoutInflater mInflater;
		if (convertView == null) {
			mHolder = new ViewHolder();
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.item_filter, null);
			convertView.setTag(mHolder);
		}
		else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		mHolder.mTvFilterName = (TextView) convertView.findViewById(R.id.tv_name_filter);
		mHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.cb_selected);
		
		final FilterObject mFilterObject = (FilterObject) mListObjects.get(position);
		mHolder.mTvFilterName.setText(mFilterObject.getTag());
		mHolder.mCheckBox.setChecked(mFilterObject.isTempChecked());
		mHolder.mCheckBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(onListFilterListener!=null){
					onListFilterListener.onCheckedListener(mFilterObject, mHolder.mCheckBox.isChecked());
				}
			}
		});
		
		return convertView;
	}
	
	private static class ViewHolder {
		public TextView mTvFilterName;
		public CheckBox mCheckBox;
	}
	
	
	public String getListIdFilters(){
		if(mListObjects!=null && mListObjects.size()>0){
			String id="";
			int count=-1;
			for(Object mObject:mListObjects){
				FilterObject mFilterObject = (FilterObject)mObject;
				if(mFilterObject.isChecked()){
					count++;
					if(count>0){
						id=id+"|"+mFilterObject.getId();
					}
					else{
						id=id+mFilterObject.getId();
					}
				}
			}
			DBLog.d(TAG, "=============>id="+id);
			return id;
			
		}
		return "";
	}
	public void onResetChecked(){
		if(mListObjects!=null && mListObjects.size()>0){
			for(Object mObject:mListObjects){
				FilterObject mFilterObject = (FilterObject)mObject;
				mFilterObject.onResetTemp();
			}
			notifyDataSetChanged();
		}
	}
	public void onApplyChecked(){
		if(mListObjects!=null && mListObjects.size()>0){
			for(Object mObject:mListObjects){
				FilterObject mFilterObject = (FilterObject)mObject;
				mFilterObject.onApplyTemp();
			}
			notifyDataSetChanged();
		}
	}
	public boolean isNoChecked(){
		if(mListObjects!=null && mListObjects.size()>0){
			for(Object mObject:mListObjects){
				FilterObject mFilterObject = (FilterObject)mObject;
				if(mFilterObject.isChecked()){
					return false;
				}
			}
		}
		return true;
	}
	
	
	
	public void setOnListFilterListener(OnListFilterListener onListFilterListener) {
		this.onListFilterListener = onListFilterListener;
	}

	public interface OnListFilterListener{
		public void onCheckedListener(FilterObject mFilterObject, boolean b);
	}
	
	

}
