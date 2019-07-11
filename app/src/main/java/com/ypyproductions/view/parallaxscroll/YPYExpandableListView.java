package com.ypyproductions.view.parallaxscroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;

/**
 * 
 *
 * @author:YPY Productions
 * @Skype: baopfiev_k50
 * @Mobile : +84 983 028 786
 * @Email: dotrungbao@gmail.com
 * @Website: www.ypyproductions.com
 * @Project:MyRestaurant
 * @Date:Jan 25, 2015 
 *
 */
public class YPYExpandableListView extends ExpandableListView {

	private YPYListViewHelper helper;

	public YPYExpandableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public YPYExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	protected void init(Context context, AttributeSet attrs) {
		helper = new YPYListViewHelper(context, attrs, this);
		super.setOnScrollListener(helper);
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		helper.setOnScrollListener(l);
	}
	
	public void addParallaxedHeaderView(View v) {
		super.addHeaderView(v);
		helper.addParallaxedHeaderView(v);
	}

	public void addParallaxedHeaderView(View v, Object data, boolean isSelectable) {
		super.addHeaderView(v, data, isSelectable);
		helper.addParallaxedHeaderView(v, data, isSelectable);
	}
}
