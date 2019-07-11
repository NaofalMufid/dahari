package com.ypyproductions.restaurants.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ypyproductions.restaurants.R;
import com.ypyproductions.abtractclass.DBBaseAdapter;

public class ListTagAdapter extends DBBaseAdapter {

	public static final String TAG = ListTagAdapter.class.getSimpleName();
	

	public ListTagAdapter(Activity mContext, ArrayList<? extends Object> listObjects) {
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
			convertView = mInflater.inflate(R.layout.item_tag, null);
			convertView.setTag(mHolder);
		}
		else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		mHolder.mTvTagName = (TextView) convertView.findViewById(R.id.tv_tags);
		final String tag = (String) mListObjects.get(position);
		mHolder.mTvTagName.setText(tag);
		
		return convertView;
	}
	
	private static class ViewHolder {
		public TextView mTvTagName;
	}

}
