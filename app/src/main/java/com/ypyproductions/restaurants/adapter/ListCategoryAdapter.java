package com.ypyproductions.restaurants.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ypyproductions.abtractclass.DBBaseAdapter;
import com.ypyproductions.restaurants.R;
import com.ypyproductions.restaurants.object.CategoryObject;

public class ListCategoryAdapter extends DBBaseAdapter {

	public static final String TAG = ListCategoryAdapter.class.getSimpleName();

	public ListCategoryAdapter(Activity mContext, ArrayList<? extends Object> listObjects) {
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
			convertView = mInflater.inflate(R.layout.item_restaurant, null);
			convertView.setTag(mHolder);
		}
		else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		mHolder.mTvRestaurant = (TextView) convertView.findViewById(R.id.tv_name_restaurant);
		CategoryObject mCat = (CategoryObject) mListObjects.get(position);
		mHolder.mTvRestaurant.setText(mCat.getName());
		
		return convertView;
	}
	
	private static class ViewHolder {
		public TextView mTvRestaurant;
	}

}
