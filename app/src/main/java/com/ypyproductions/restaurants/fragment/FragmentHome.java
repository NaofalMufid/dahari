package com.ypyproductions.restaurants.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.ypyproductions.abtractclass.fragment.DBFragmentAdapter;
import com.ypyproductions.net.task.DBTask;
import com.ypyproductions.net.task.IDBTaskListener;
import com.ypyproductions.restaurants.IMainCallBack;
import com.ypyproductions.restaurants.MainActivity;
import com.ypyproductions.restaurants.R;
import com.ypyproductions.restaurants.adapter.ExpandableListItemAdapter;
import com.ypyproductions.restaurants.constanst.IMyRestaurantConstants;
import com.ypyproductions.restaurants.object.CategoryObject;
import com.ypyproductions.restaurants.object.FilterObject;
import com.ypyproductions.restaurants.object.ItemObject;
import com.ypyproductions.restaurants.parsingdata.JsonParsingUtils;
import com.ypyproductions.restaurants.parsingdata.TotalDataManager;
import com.ypyproductions.utils.ApplicationUtils;
import com.ypyproductions.utils.DownloadUtils;
import com.ypyproductions.utils.IOUtils;
import com.ypyproductions.utils.ResolutionUtils;
import com.ypyproductions.utils.StringUtils;
import com.ypyproductions.view.parallaxscroll.YPYExpandableListView;

public class FragmentHome extends Fragment implements IMyRestaurantConstants {

	public static final String TAG = FragmentHome.class.getSimpleName();
	
	public static final int TIME_NEXT_AUTO=30000;

	private View mRootView;

	private MainActivity mContext;

	private boolean isFindView;

	private TextView mTvNoResult;

	private IMainCallBack mCallback;

	private Handler mHandler = new Handler();

	private ExpandableListItemAdapter mTotalAdapter;

	private YPYExpandableListView mExListItemDish;

	private View mLayoutHeader;

	private ViewPager mViewPager;

	private LinearLayout mLayoutPage;

	private ArrayList<Fragment> mListFeatureFragment;

	private View mLayoutHeaderRoot;

	private LayoutParams mLayoutParams;

	private YPYExpandableListView mExListSearchItemDish;

	private ExpandableListItemAdapter mSearchAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_home, container, false);
		return mRootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (!isFindView) {
			isFindView = true;
			this.findView();
		}
	}

	private void findView() {
		this.mContext = (MainActivity) getActivity();
		mTvNoResult = (TextView) mRootView.findViewById(R.id.tv_no_result);
		mExListItemDish = (YPYExpandableListView) mRootView.findViewById(R.id.list_restaurant);
		mExListSearchItemDish = (YPYExpandableListView) mRootView.findViewById(R.id.list_search_restaurant);

		setUpHeader();
		startGetListItem();
	}

	private void setUpHeader() {
		mLayoutHeader = LayoutInflater.from(mContext).inflate(R.layout.header_home, null);
		mLayoutHeaderRoot = mLayoutHeader.findViewById(R.id.layout_root);

		mLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, mContext.getScreenHeight() / 2);
		mLayoutHeaderRoot.setLayoutParams(mLayoutParams);

		mViewPager = (ViewPager) mLayoutHeader.findViewById(R.id.layout_features);
		mLayoutPage = (LinearLayout) mLayoutHeader.findViewById(R.id.layout_page);

		mExListItemDish.addParallaxedHeaderView(mLayoutHeader);
	}
	private void scheduleFeatureDisplay(){
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(mViewPager!=null && mListFeatureFragment!=null &&  mListFeatureFragment.size()>=2){
					int current = mViewPager.getCurrentItem()+1;
					if(current>=mListFeatureFragment.size()){
						current=0;
					}
					mViewPager.setCurrentItem(current,true);
					scheduleFeatureDisplay();
				}
			}
		}, TIME_NEXT_AUTO);
	}
	private void setUpFeatureItem(ArrayList<ItemObject> listItemObjects) {
		if (listItemObjects != null && listItemObjects.size() > 0) {
			TotalDataManager.getInstance().setListFeatureItemObjects(listItemObjects);
			
			mLayoutParams.height = mContext.getScreenHeight() / 2;
			mLayoutHeaderRoot.setLayoutParams(mLayoutParams);

			mListFeatureFragment = new ArrayList<Fragment>();
			int size = listItemObjects.size();
			for (int i = 0; i < size; i++) {
				Bundle mBundle1 = new Bundle();
				mBundle1.putString(KEY_ID, listItemObjects.get(i).getId());
				Fragment mFragment = Fragment.instantiate(mContext, FragmentFeatureItem.class.getName(), mBundle1);
				mListFeatureFragment.add(mFragment);
				
				if(size>1){
					ImageView mImageView = new ImageView(mContext);
					int width = (int) ResolutionUtils.convertDpToPixel(mContext, 10);
					LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(width, width);
					if (i != 0) {
						mLayoutParams.leftMargin = (int) ResolutionUtils.convertDpToPixel(mContext, 5);
						mImageView.setImageResource(R.drawable.circle_grey);
					}
					else {
						mImageView.setImageResource(R.drawable.circle_primary_color);
					}
					mLayoutPage.addView(mImageView, mLayoutParams);
				}
			}
			DBFragmentAdapter mPagerAdapter = new DBFragmentAdapter(getChildFragmentManager(), mListFeatureFragment);
			mViewPager.setAdapter(mPagerAdapter);
			mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					int count = mLayoutPage.getChildCount();
					for (int i = 0; i < count; i++) {
						ImageView mImgView = (ImageView) mLayoutPage.getChildAt(i);
						if (position != i) {
							mImgView.setImageResource(R.drawable.circle_grey);
						}
						else {
							mImgView.setImageResource(R.drawable.circle_primary_color);
						}
					}
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {

				}

				@Override
				public void onPageScrollStateChanged(int state) {

				}
			});
			mViewPager.setCurrentItem(0, false);
			scheduleFeatureDisplay();
		}
		else {
			mHandler.removeCallbacksAndMessages(null);
			mLayoutParams.height = LayoutParams.WRAP_CONTENT;
			mLayoutHeaderRoot.setLayoutParams(mLayoutParams);
			
		}

	}
	
	public boolean checkBack(){
		if(mExListItemDish.getVisibility()!=View.VISIBLE){
			setUpInfoForAllItem(TotalDataManager.getInstance().getListItems());
			return true;
		}
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
	}

	private void startGetListItem() {
		if (!ApplicationUtils.isOnline(mContext) && !StringUtils.isEmptyString(URL_LIST_ALL_ITEM) && URL_LIST_ALL_ITEM.startsWith("http")) {
			mContext.showToast(R.string.info_lose_internet);
			return;
		}
		ArrayList<ItemObject> listItems = TotalDataManager.getInstance().getListItems();
		if (listItems != null && listItems.size() > 0) {
			setUpInfoForAllItem(listItems);
			mContext.setUpCategories(TotalDataManager.getInstance().getListCategoryObjects());
			return;
		}
		TotalDataManager.getInstance().onResetData();
		DBTask mDBTask = new DBTask(new IDBTaskListener() {

			private ArrayList<ItemObject> mListFilterItemObjects;

			@Override
			public void onPreExcute() {
				mContext.showProgressDialog();
				mTvNoResult.setVisibility(View.GONE);
			}

			@Override
			public void onDoInBackground() {
				String dataItems="";
				if(!StringUtils.isEmptyString(URL_LIST_ALL_ITEM)){
					if(URL_LIST_OFFERS.startsWith("http")){
						dataItems = DownloadUtils.downloadString(URL_LIST_ALL_ITEM);
					}
					else{
						dataItems=IOUtils.readStringFromAssets(mContext, URL_LIST_ALL_ITEM);
					}
				}
				if (!StringUtils.isEmptyString(dataItems)) {
					ArrayList<ItemObject> mListItemObjects = JsonParsingUtils.parseListItemObject(dataItems);
					if (mListItemObjects != null && mListItemObjects.size() > 0) {
						TotalDataManager.getInstance().setListItems(mListItemObjects);
						TotalDataManager.getInstance().setListCurrentItemObjects(mListItemObjects);
						processLoadFilter();
						mListFilterItemObjects = mListItemObjects;
						
						String dataCat ="" ;
						if(!StringUtils.isEmptyString(URL_LIST_CATEGORIES)){
							if(URL_LIST_CATEGORIES.startsWith("http")){
								dataCat=DownloadUtils.downloadString(URL_LIST_CATEGORIES);
							}
							else{
								dataCat=IOUtils.readStringFromAssets(mContext, URL_LIST_CATEGORIES);
							}
						}
						if (!StringUtils.isEmptyString(dataCat)) {
							ArrayList<CategoryObject> listCategoryObjects = JsonParsingUtils.parseListCategoryObject(dataCat);
							if (listCategoryObjects != null && listCategoryObjects.size() > 0) {
								TotalDataManager.getInstance().setListCategoryObjects(listCategoryObjects);
								for (ItemObject mItemObject : mListItemObjects) {
									for (CategoryObject mCategoryObject : listCategoryObjects) {
										if (mItemObject.getCategory().equals(mCategoryObject.getId())) {
											mCategoryObject.addItemObject(mItemObject);
											break;
										}
									}
								}

							}
						}
					}
				}

			}

			@Override
			public void onPostExcute() {
				mContext.dimissProgressDialog();
				setUpInfoForAllItem(mListFilterItemObjects);
				mContext.setUpCategories(TotalDataManager.getInstance().getListCategoryObjects());

			}

		});
		mDBTask.execute();
	}

	public void setUpInfoForAllItem(final ArrayList<ItemObject> listTotalItems) {
		ArrayList<CategoryObject> mListCategory = TotalDataManager.getInstance().getListCategoryObjects();
		mExListSearchItemDish.setVisibility(View.GONE);
		if (mTotalAdapter != null) {
			mTvNoResult.setVisibility(View.GONE);
			mExListItemDish.setVisibility(View.VISIBLE);
			return;
		}
		if (listTotalItems == null || listTotalItems.size() == 0 || mListCategory == null || mListCategory.size() == 0) {
			mTvNoResult.setVisibility(View.VISIBLE);
			mExListItemDish.setVisibility(View.GONE);
			return;
		}
		mExListItemDish.setVisibility(View.VISIBLE);
		mTvNoResult.setVisibility(View.GONE);
		final ArrayList<ItemObject> listFeatureItems = new ArrayList<ItemObject>();
		for (ItemObject mItemObject : listTotalItems) {
			if (mItemObject.isFeatured()) {
				listFeatureItems.add(mItemObject);
			}
		}
		setUpFeatureItem(listFeatureItems);

		mTotalAdapter = new ExpandableListItemAdapter(mContext, mListCategory, mContext.mOptions, false);
		mExListItemDish.setAdapter(mTotalAdapter);
		mTotalAdapter.setOnSearchItemListener(new ExpandableListItemAdapter.OnSearchItemListener() {
			@Override
			public void onSearchFail() {

			}

			@Override
			public void onGoToDetail(ItemObject mItemObject) {
				if (mCallback != null) {
					TotalDataManager.getInstance().setListCurrentItemObjects(listTotalItems);
					mContext.showTags(mItemObject, true);
					mCallback.onShowItemDetail(R.id.fragment_home, "", mItemObject.getId());
				}
			}

			@Override
			public void addToWishList(ItemObject mItemObject) {
				checkAddToWishList(mItemObject);
			}

			@Override
			public void onSearchDone() {

			}
		});
		int count = mTotalAdapter.getGroupCount();
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				mExListItemDish.expandGroup(i);
			}
		}

	}

	public void setUpInfoForSearching(final ArrayList<CategoryObject> mListCategory, boolean isSearch) {
		mExListItemDish.setVisibility(View.GONE);
		if (mListCategory == null || mListCategory.size() == 0) {
			mTvNoResult.setVisibility(View.VISIBLE);
			mExListSearchItemDish.setVisibility(View.GONE);
			return;
		}
		mExListSearchItemDish.setVisibility(View.VISIBLE);
		mTvNoResult.setVisibility(View.GONE);

		mSearchAdapter = new ExpandableListItemAdapter(mContext, mListCategory, mContext.mOptions,isSearch);
		mExListSearchItemDish.setAdapter(mSearchAdapter);

		mSearchAdapter.setOnSearchItemListener(new ExpandableListItemAdapter.OnSearchItemListener() {
			@Override
			public void onSearchFail() {

			}

			@Override
			public void onGoToDetail(ItemObject mItemObject) {
				if (mCallback != null) {
					mContext.showTags(mItemObject, true);
					mCallback.onShowItemDetail(R.id.fragment_home, "", mItemObject.getId());
				}
			}

			@Override
			public void addToWishList(ItemObject mItemObject) {
				checkAddToWishList(mItemObject);
			}

			@Override
			public void onSearchDone() {

			}
		});
		int count = mSearchAdapter.getGroupCount();
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				mExListSearchItemDish.expandGroup(i);
			}
		}
	}

	public void updateWishList() {
		if (mSearchAdapter != null) {
			mSearchAdapter.notifyDataSetChanged();
		}
		if (mTotalAdapter != null) {
			mTotalAdapter.notifyDataSetChanged();
		}
	}

	public void checkAddToWishList(final ItemObject mItemObject) {
		boolean isWistList = TotalDataManager.getInstance().isWishListItem(mItemObject.getId());
		if (!isWistList) {
			TotalDataManager.getInstance().addItemToWishList(mContext, mItemObject);
			mTotalAdapter.notifyDataSetChanged();
			String info = String.format(getString(R.string.format_info_add_wishlist), mItemObject.getName());
			mContext.showToast(info);
		}
		else {
			TotalDataManager.getInstance().removeItemInWishList(mContext, mItemObject);
			String info = String.format(getString(R.string.format_info_remove_wishlist), mItemObject.getName());
			mTotalAdapter.notifyDataSetChanged();
			mContext.showToast(info);
		}
		updateWishList();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (IMainCallBack) activity;
		}
		catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement IHomeListener");
		}
	}

	public void processLoadFilter() {
		ArrayList<ItemObject> mListItems = TotalDataManager.getInstance().getListItems();
		if (mListItems != null && mListItems.size() > 0) {
			ArrayList<FilterObject> listFilterObjects = new ArrayList<FilterObject>();
			for (ItemObject mItemObject : mListItems) {
				String tag = mItemObject.getTag();
				String[] datas = tag.split("\\s");
				if (datas != null && datas.length > 0) {
					for (String mStr : datas) {
						if (!StringUtils.isEmptyString(mStr) && !mStr.equalsIgnoreCase("#")) {
							boolean isAddTag = true;
							if (listFilterObjects.size() > 0) {
								for (FilterObject mFilterObject : listFilterObjects) {
									if (mFilterObject.getTag().equalsIgnoreCase(mStr)) {
										isAddTag = false;
										break;
									}
								}
							}
							if (isAddTag) {
								FilterObject mFilterObject = new FilterObject(String.valueOf(listFilterObjects.size() + 1), mStr);
								listFilterObjects.add(mFilterObject);
							}
						}
					}
				}
			}
			TotalDataManager.getInstance().setListFilterObjects(listFilterObjects);
		}
	}

}
