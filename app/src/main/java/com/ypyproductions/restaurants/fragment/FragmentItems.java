package com.ypyproductions.restaurants.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.ypyproductions.abtractclass.fragment.DBFragment;
import com.ypyproductions.abtractclass.fragment.DBFragmentAdapter;
import com.ypyproductions.restaurants.MainActivity;
import com.ypyproductions.restaurants.R;
import com.ypyproductions.restaurants.constanst.IMyRestaurantConstants;
import com.ypyproductions.restaurants.object.ItemObject;
import com.ypyproductions.restaurants.parsingdata.TotalDataManager;
import com.ypyproductions.utils.DBLog;
import com.ypyproductions.utils.StringUtils;

public class FragmentItems extends DBFragment implements IMyRestaurantConstants, OnClickListener {

	public static final String TAG = FragmentItems.class.getSimpleName();

	private MainActivity mContext;

	private Button mBtnBack;

	private Button mBtnAdd;

	private String mCurrentIDItem;

	private ViewPager mViewPagers;

	private ArrayList<ItemObject> mListItems;
	private ArrayList<Fragment> mListFragments= new ArrayList<Fragment>();

	private DBFragmentAdapter mPagerAdapter;

	@Override
	public View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_items, container, false);
	}

	@Override
	public void findView() {
		this.mContext = (MainActivity) getActivity();

		this.mBtnBack = (Button) mRootView.findViewById(R.id.btn_back);
		this.mBtnAdd = (Button) mRootView.findViewById(R.id.btn_add);

		mViewPagers = (ViewPager) mRootView.findViewById(R.id.layout_items);

		this.mBtnBack.setOnClickListener(this);
		this.mBtnAdd.setOnClickListener(this);
		if(mNameFragment!=null && mNameFragment.equals(TAG_WISHLIST_ITEM)){
			mListItems = new ArrayList<ItemObject>();
			ArrayList<ItemObject> mListTotals = TotalDataManager.getInstance().getListCurrentItemObjects();
			if(mListTotals!=null && mListTotals.size()>0){
				for(ItemObject mItemObject:mListTotals){
					mListItems.add(mItemObject);
				}
			}
		}
		else{
			mListItems = TotalDataManager.getInstance().getListCurrentItemObjects();
		}
		if (mListItems != null && mListItems.size() > 0) {
			setUpFragments();
		}
	}

	private void setUpFragments() {
		int size = mListItems.size();
		int currentPage=0;
		for (int i = 0; i < size; i++) {
			Bundle mBundle1 = new Bundle();
			mBundle1.putString(KEY_ID, mListItems.get(i).getId());
			Fragment mFragment = Fragment.instantiate(mContext, FragmentDetailItem.class.getName(), mBundle1);
			mListFragments.add(mFragment);
			
			if (mListItems.get(i).getId().equalsIgnoreCase(mCurrentIDItem)) {
				currentPage=i;
			}
		}
		DBLog.d(TAG, "============>currentPage="+currentPage);
		mPagerAdapter = new DBFragmentAdapter(getChildFragmentManager(), mListFragments);
		mViewPagers.setAdapter(mPagerAdapter);
		mViewPagers.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				checkWishList(mListItems.get(position));
				mContext.showTags(mListItems.get(position), true);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
		mViewPagers.setCurrentItem(currentPage, false);
		checkWishList(mListItems.get(currentPage));
		mContext.showTags(mListItems.get(currentPage), true);
		
	}
	
	private void checkWishList(final ItemObject mItem){
		boolean isWishtList = TotalDataManager.getInstance().isWishListItem(mItem.getId());
		mBtnAdd.setBackgroundResource(isWishtList?R.drawable.bt_big_heart:R.drawable.bt_add_tag);
		mBtnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkAddToWishList(mItem, mBtnAdd);
			}
		});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mListFragments!=null){
			mListFragments.clear();
			mListFragments=null;
		}
	}

	@Override
	public void onExtractData() {
		super.onExtractData();
		Bundle args = getArguments();
		if (args != null) {
			mCurrentIDItem = args.getString(KEY_ID);
		}

	}

	public void checkAddToWishList(final ItemObject mItemObject, final Button mImgAdd) {
		boolean isWistList = TotalDataManager.getInstance().isWishListItem(mItemObject.getId());
		if (!isWistList) {
			TotalDataManager.getInstance().addItemToWishList(mContext, mItemObject);
			mImgAdd.setBackgroundResource(R.drawable.bt_big_heart);
			String info = String.format(getString(R.string.format_info_add_wishlist), mItemObject.getName());
			mContext.showToast(info);
		}
		else {
			TotalDataManager.getInstance().removeItemInWishList(mContext, mItemObject);
			mImgAdd.setBackgroundResource(R.drawable.bt_add_tag);
			String info = String.format(getString(R.string.format_info_remove_wishlist), mItemObject.getName());
			mContext.showToast(info);
		}
		mContext.updateWishList();
		
		FragmentManager mFragmentManager = mContext.getSupportFragmentManager();
		Fragment mFragmentHome = null;
		if (mIdFragment > 0) {
			mFragmentHome = mFragmentManager.findFragmentById(mIdFragment);
		}
		else {
			if (!StringUtils.isEmptyString(mNameFragment)) {
				mFragmentHome = mFragmentManager.findFragmentByTag(mNameFragment);
			}
		}
		if(mFragmentHome!=null){
			if(mFragmentHome instanceof FragmentWishList){
				((FragmentWishList)mFragmentHome).onNotificationUpdate(mItemObject, !isWistList);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add:
			break;
		case R.id.btn_back:
			mContext.showTags(null, false);
			mContext.backStack(null);
			break;
		default:
			break;
		}
	}

}
