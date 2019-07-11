package com.ypyproductions.restaurants;

import java.util.ArrayList;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.ypyproductions.abtractclass.fragment.IDBFragmentConstants;
import com.ypyproductions.net.task.DBTask;
import com.ypyproductions.net.task.IDBCallback;
import com.ypyproductions.net.task.IDBTaskListener;
import com.ypyproductions.restaurants.adapter.ListCategoryAdapter;
import com.ypyproductions.restaurants.adapter.ListFilterAdapter;
import com.ypyproductions.restaurants.adapter.ListFilterAdapter.OnListFilterListener;
import com.ypyproductions.restaurants.adapter.ListTagAdapter;
import com.ypyproductions.restaurants.fragment.FragmentHome;
import com.ypyproductions.restaurants.fragment.FragmentItems;
import com.ypyproductions.restaurants.fragment.FragmentOffers;
import com.ypyproductions.restaurants.fragment.FragmentWishList;
import com.ypyproductions.restaurants.object.CategoryObject;
import com.ypyproductions.restaurants.object.FilterObject;
import com.ypyproductions.restaurants.object.ItemObject;
import com.ypyproductions.restaurants.parsingdata.TotalDataManager;
import com.ypyproductions.utils.ApplicationUtils;
import com.ypyproductions.utils.DBLog;
import com.ypyproductions.utils.DirectionUtils;
import com.ypyproductions.utils.ShareActionUtils;
import com.ypyproductions.utils.StringUtils;

/**
 * 
 *
 * @author:YPY Productions
 * @Skype: baopfiev_k50
 * @Mobile : +84 983 028 786
 * @Email: dotrungbao@gmail.com
 * @Website: www.ypyproductions.com
 * @Project:MyRestaurant
 * @Date:Jan 31, 2015 
 *
 */
public class MainActivity extends DBFragmentActivity implements OnClickListener, IMainCallBack, IDBFragmentConstants {

	public static final String TAG = MainActivity.class.getSimpleName();

	private RelativeLayout mRootLayoutMenu;
	private LinearLayout mLayoutMenu;
	private boolean isAnimationRun;

	private ImageView mImageBg;

	private RelativeLayout mRootLayoutFilter;
	private ListView mListViewFilters;
	private ListFilterAdapter mListFilterAdapter;
	private RelativeLayout mBtnAllRes;
	private RelativeLayout mBtnOffers;

	private RelativeLayout mLayoutFilter;

	private TextView mTvHeaderFilter;

	private TextView mTvCaptionFilter;

	private ListView mListViewTags;

	private ListTagAdapter mAdapterTags;

	private String mSearchItemId;
	private Handler mHandler = new Handler();

	public DisplayImageOptions mBigOptions;
	public DisplayImageOptions mOptions;

	private AdView adView;

	private InterstitialAd mInterstitial;

	private ListView mListCategories;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

		Intent mIntent = getIntent();
		if (mIntent != null) {
			mSearchItemId = mIntent.getStringExtra(KEY_ID);
		}

		mImageBg = (ImageView) findViewById(R.id.img_backgroud);
		mBtnAllRes = (RelativeLayout) findViewById(R.id.btn_all_restaurant);
		mBtnOffers = (RelativeLayout) findViewById(R.id.btn_offers);

		mBtnOffers.setOnClickListener(this);
		mBtnAllRes.setOnClickListener(this);

		this.mBigOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_load).resetViewBeforeLoading(false).cacheInMemory(true).cacheOnDisk(true)
				.considerExifParams(true).build();

		this.mOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_launcher).resetViewBeforeLoading(false).cacheInMemory(true)
				.displayer(new RoundedBitmapDisplayer(1000)).cacheOnDisk(true).considerExifParams(true).build();

		createArrayFragment();
		setUpMainMenu();
		setUpFilter();
		setUpLayoutAdmob();

		if (!StringUtils.isEmptyString(mSearchItemId)) {
			ItemObject mItemObject = TotalDataManager.getInstance().getItemObject(mSearchItemId);
			if (mItemObject != null) {
				showTags(mItemObject, true);
				onShowItemDetail(R.id.fragment_home, "", mSearchItemId);
			}
		}
	}

	private void setUpLayoutAdmob() {
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_ad);
		if (SHOW_ADVERTISEMENT) {
			if(ApplicationUtils.isOnline(this)){
				adView = new AdView(this);
				adView.setAdUnitId(ADMOB_ID_BANNER);
				adView.setAdSize(AdSize.BANNER);

				layout.addView(adView);
				AdRequest mAdRequest = new AdRequest.Builder().build();
				adView.loadAd(mAdRequest);

				mInterstitial = new InterstitialAd(this);
				mInterstitial.setAdUnitId(ADMOB_ID_INTERTESTIAL);
				AdRequest adRequest = new AdRequest.Builder().build();
				mInterstitial.loadAd(adRequest);
			}
			else{
				layout.setVisibility(View.GONE);
			}
			
		}
		else {
			layout.setVisibility(View.GONE);
		}
	}

	public void setUpCategories(final ArrayList<CategoryObject> listCategoryObjects) {
		if (listCategoryObjects != null && listCategoryObjects.size() > 0) {
			mListCategories.setVisibility(View.VISIBLE);
			ListCategoryAdapter mAdapter = new ListCategoryAdapter(this, listCategoryObjects);
			mListCategories.setAdapter(mAdapter);
			mListCategories.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long id) {
					if (mListFragments != null && mListFragments.size() > 0) {
						backStack(null);
					}
					hideAnim(mLayoutMenu, mRootLayoutMenu, new IDBCallback() {
						@Override
						public void onAction() {
							mImageBg.setVisibility(View.GONE);
							showCategory(listCategoryObjects.get(position));
						}
					});
				}

			});
		}
		else {
			mListCategories.setVisibility(View.GONE);
		}
	}

	private void setUpMainMenu() {
		mRootLayoutMenu = (RelativeLayout) findViewById(R.id.layout_menu);
		RelativeLayout mLayoutCall = (RelativeLayout) mRootLayoutMenu.findViewById(R.id.layout_call);
		RelativeLayout mLayoutInfo = (RelativeLayout) mRootLayoutMenu.findViewById(R.id.layout_info);
		mListCategories = (ListView) mRootLayoutMenu.findViewById(R.id.list_category);
		mLayoutInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onHideMenu();
				showDiaglogAboutUs();
			}
		});
		mLayoutCall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShareActionUtils.callNumber(MainActivity.this, YOUR_PHONE_CONTACT);
			}
		});
		mImageBg.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return onHideMenu();
			}
		});
		mLayoutMenu = (LinearLayout) mRootLayoutMenu.findViewById(R.id.layout_main_menu);
		mRootLayoutMenu.setVisibility(View.GONE);
	}
	
	private void showDiaglogAboutUs() {
		AlertDialog mDialog = new AlertDialog.Builder(this).setTitle(R.string.title_contact).setItems(R.array.list_share, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					ShareActionUtils.shareViaEmail(MainActivity.this, YOUR_EMAIL_CONTACT, "", "");
				}
				else if (which == 1) {
					Intent mIntent = new Intent(MainActivity.this, ShowUrlActivity.class);
					mIntent.putExtra(KEY_URL, URL_YOUR_WEBSITE);
					mIntent.putExtra(KEY_HEADER, getString(R.string.title_website));
					startActivity(mIntent);
				}
				else if (which == 2) {
					Intent mIntent = new Intent(MainActivity.this, ShowUrlActivity.class);
					mIntent.putExtra(KEY_URL, URL_YOUR_FACE_BOOK);
					mIntent.putExtra(KEY_HEADER, getString(R.string.title_facebook));
					startActivity(mIntent);
				}
				else if (which == 3) {
					Intent mIntent = new Intent(MainActivity.this, ShowUrlActivity.class);
					mIntent.putExtra(KEY_URL, URL_YOUR_TWITTER);
					mIntent.putExtra(KEY_HEADER, getString(R.string.title_twitter));
					startActivity(mIntent);
				}
				else if (which == 4) {
					String url = String.format(URL_FORMAT_LINK_APP, getPackageName());
					ShareActionUtils.goToUrl(MainActivity.this, url);
				}

			}
		}).setPositiveButton(getString(R.string.title_cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		}).create();
		mDialog.show();
	}

	private boolean onHideMenu() {
		if (mRootLayoutFilter.getVisibility() == View.VISIBLE) {
			if (mListFilterAdapter != null) {
				mListFilterAdapter.onResetChecked();
				mListFilterAdapter.notifyDataSetChanged();
			}
			hideFilter(new IDBCallback() {
				@Override
				public void onAction() {
					mImageBg.setVisibility(View.GONE);
				}
			});
			return true;
		}
		hideAnim(mLayoutMenu, mRootLayoutMenu, new IDBCallback() {
			@Override
			public void onAction() {
				mImageBg.setVisibility(View.GONE);
			}
		});
		return true;
	}

	private void setUpFilter() {
		mRootLayoutFilter = (RelativeLayout) findViewById(R.id.layout_filter);
		mLayoutFilter = (RelativeLayout) mRootLayoutFilter.findViewById(R.id.layout_list_filter);
		mTvHeaderFilter = (TextView) mRootLayoutFilter.findViewById(R.id.tv_header_filter);
		mTvCaptionFilter = (TextView) mRootLayoutFilter.findViewById(R.id.tv_caption_filter);
		mListViewTags = (ListView) mRootLayoutFilter.findViewById(R.id.list_tags);

		mListViewFilters = (ListView) mRootLayoutFilter.findViewById(R.id.list_filters);
		RelativeLayout mLayoutBackFilter = (RelativeLayout) mRootLayoutFilter.findViewById(R.id.layout_back_filter);

		mAdapterTags = new ListTagAdapter(this, new ArrayList<String>());
		mListViewTags.setAdapter(mAdapterTags);

		mLayoutBackFilter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListFilterAdapter != null) {
					mListFilterAdapter.onResetChecked();
					mListFilterAdapter.notifyDataSetChanged();
				}
				hideFilter(new IDBCallback() {
					@Override
					public void onAction() {
						mImageBg.setVisibility(View.GONE);
					}
				});
			}
		});
		mRootLayoutFilter.findViewById(R.id.tv_done).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListFilterAdapter != null) {
					mListFilterAdapter.onApplyChecked();
					mListFilterAdapter.notifyDataSetChanged();
				}
				hideFilter(new IDBCallback() {
					@Override
					public void onAction() {
						mImageBg.setVisibility(View.GONE);
						if(mListFilterAdapter.isNoChecked()){
							ArrayList<ItemObject> mListImtes = TotalDataManager.getInstance().getListItems();
							TotalDataManager.getInstance().setListCurrentItemObjects(mListImtes);
							showListItem(mListImtes);
							return;
						}
						ArrayList<ItemObject> mListItemsObjects = invalidateFilter(TotalDataManager.getInstance().getListItems());
						if(mListItemsObjects!=null && mListItemsObjects.size()>0){
							TotalDataManager.getInstance().setListCurrentItemObjects(mListItemsObjects);
							ArrayList<CategoryObject> mListCategory = TotalDataManager.getInstance().getListCategoryObjectsFromListItem(mListItemsObjects);
							showListCategory(mListCategory, true);
						}
						
					}
				});
			}
		});
		mRootLayoutFilter.setVisibility(View.GONE);
	}

	public void showTags(ItemObject mItemObject, boolean b) {
		if (b) {
			mListViewTags.setVisibility(View.VISIBLE);
			mLayoutFilter.setVisibility(View.GONE);
			mTvHeaderFilter.setText(R.string.info_tags);
			mTvCaptionFilter.setText(R.string.info_item_contains);
			mAdapterTags.setListObjects(mItemObject.getListTags(), false);

		}
		else {
			mListViewTags.setVisibility(View.GONE);
			mLayoutFilter.setVisibility(View.VISIBLE);
			mTvHeaderFilter.setText(R.string.title_filter);
			mTvCaptionFilter.setText(R.string.info_filter);
		}
	}

	private void startGetFilter() {
		ArrayList<FilterObject> mListFilter = TotalDataManager.getInstance().getListFilterObjects();
		if (mListFilter != null) {
			if (mListViewFilters.getAdapter() == null) {
				setUpListFilter(mListFilter);
			}
			return;
		}
		if (!ApplicationUtils.isOnline(this) && !StringUtils.isEmptyString(URL_LIST_ALL_ITEM) && URL_LIST_ALL_ITEM.startsWith("http")) {
			showToast(R.string.info_lose_internet);
			return;
		}
		DBTask mDBTask = new DBTask(new IDBTaskListener() {

			private ArrayList<FilterObject> listFilterObjects;

			@Override
			public void onPreExcute() {
				showProgressDialog();
			}

			@Override
			public void onDoInBackground() {
				ArrayList<ItemObject> mListItems = TotalDataManager.getInstance().getListItems();
				if (mListItems != null && mListItems.size() > 0) {
					listFilterObjects = new ArrayList<FilterObject>();
					for (ItemObject mItemObject : mListItems) {
						String tag = mItemObject.getTag();
						String[] datas = tag.split("\\s");
						if (datas != null && datas.length > 0) {
							for (String mStr : datas) {
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
					TotalDataManager.getInstance().setListFilterObjects(listFilterObjects);
				}
			}

			@Override
			public void onPostExcute() {
				dimissProgressDialog();
				setUpListFilter(listFilterObjects);
			}

		});
		mDBTask.execute();
	}

	private void setUpListFilter(ArrayList<FilterObject> listFilterObjects) {
		if (listFilterObjects != null && listFilterObjects.size() > 0) {
			mListViewFilters.setVisibility(View.VISIBLE);
			mListFilterAdapter = new ListFilterAdapter(this, listFilterObjects);
			mListViewFilters.setAdapter(mListFilterAdapter);
			mListFilterAdapter.setOnListFilterListener(new OnListFilterListener() {
				@Override
				public void onCheckedListener(FilterObject mFilterObject, boolean b) {
					mFilterObject.setTempChecked(b);
					mListFilterAdapter.notifyDataSetChanged();
				}
			});
		}
		else {
			mListViewFilters.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_menu:
			isAnimationRun = false;
			showAnim(mLayoutMenu, mRootLayoutMenu, null);
			break;
		case R.id.btn_wishlist:
			showWishList();
			break;
		case R.id.btn_filter:
			showFilter(new IDBCallback() {
				@Override
				public void onAction() {
					startGetFilter();
				}
			});
			break;
		case R.id.btn_search:
			Intent mIt = new Intent(this, SearchActivity.class);
			DirectionUtils.changeActivity(this, R.anim.slide_in_from_right, R.anim.slide_out_to_left, true, mIt);
			break;
		case R.id.btn_all_restaurant:
			hideAnim(mLayoutMenu, mRootLayoutMenu, new IDBCallback() {
				@Override
				public void onAction() {
					if (mListFragments != null && mListFragments.size() > 0) {
						backStack(null);
					}
					mImageBg.setVisibility(View.GONE);
					ArrayList<ItemObject> mListItems = TotalDataManager.getInstance().getListItems();
					TotalDataManager.getInstance().setListCurrentItemObjects(mListItems);
					showListItem(mListItems);
				}
			});
			break;
		case R.id.btn_offers:
			hideAnim(mLayoutMenu, mRootLayoutMenu, new IDBCallback() {
				@Override
				public void onAction() {
					if (mListFragments != null && mListFragments.size() > 0) {
						backStack(null);
					}
					mImageBg.setVisibility(View.GONE);
					showOffers();
				}
			});
		default:
			break;
		}
	}

	private void showFilter(final IDBCallback mCallback) {
		if (!isAnimationRun) {
			isAnimationRun = true;
			mRootLayoutFilter.setVisibility(View.VISIBLE);
			mImageBg.setVisibility(View.VISIBLE);
			YPYAnimationUtils.animTranslateX(mRootLayoutFilter, getScreenWidth(), 0, new IDBCallback() {
				@Override
				public void onAction() {
					isAnimationRun = false;
					if (mCallback != null) {
						mCallback.onAction();
					}
				}
			});
		}
	}

	private void hideFilter(final IDBCallback mCallback) {
		if (!isAnimationRun) {
			isAnimationRun = true;
			YPYAnimationUtils.animTranslateX(mRootLayoutFilter, 0, getScreenWidth(), new IDBCallback() {
				@Override
				public void onAction() {
					isAnimationRun = false;
					mRootLayoutFilter.setVisibility(View.GONE);
					if (mCallback != null) {
						mCallback.onAction();
					}
				}
			});
		}
	}

	private void showAnim(final View mLayout, final RelativeLayout mRootLayout, final IDBCallback mCallback) {
		if (!isAnimationRun) {
			isAnimationRun = true;
			mRootLayout.setVisibility(View.VISIBLE);
			mImageBg.setVisibility(View.VISIBLE);
			YPYAnimationUtils.animTranslateX(mLayout, -2f * getScreenWidth() / 3f, 0f, new IDBCallback() {
				@Override
				public void onAction() {
					isAnimationRun = false;
					if (mCallback != null) {
						mCallback.onAction();
					}
				}
			});
		}
	}

	private void hideAnim(final View mLayout, final RelativeLayout mRootLayout, final IDBCallback mCallback) {
		if (!isAnimationRun) {
			isAnimationRun = true;
			YPYAnimationUtils.animTranslateX(mLayout, 0f, -2f * getScreenWidth() / 3f, new IDBCallback() {
				@Override
				public void onAction() {
					isAnimationRun = false;
					mRootLayout.setVisibility(View.GONE);
					if (mCallback != null) {
						mCallback.onAction();
					}
				}
			});
		}
	}

	public void showListItem(final ArrayList<ItemObject> listItemObjects) {
		FragmentHome mFragment = (FragmentHome) getSupportFragmentManager().findFragmentById(R.id.fragment_home);
		mFragment.setUpInfoForAllItem(listItemObjects);
	}

	public void showCategory(final CategoryObject mCategoryObject) {
		ArrayList<CategoryObject> list = new ArrayList<CategoryObject>();
		list.add(mCategoryObject);
		if(mCategoryObject.getListItemObjects()!=null && mCategoryObject.getListItemObjects().size()>0){
			TotalDataManager.getInstance().setListCurrentItemObjects(mCategoryObject.getListItemObjects());
		}
		showListCategory(list, false);
	}

	public void showListCategory(final ArrayList<CategoryObject> list, final boolean isSearch) {
		FragmentHome mFragment = (FragmentHome) getSupportFragmentManager().findFragmentById(R.id.fragment_home);
		mFragment.setUpInfoForSearching(list, isSearch);
	}

	public void updateWishList() {
		FragmentHome mFragment = (FragmentHome) getSupportFragmentManager().findFragmentById(R.id.fragment_home);
		if (mFragment != null) {
			mFragment.updateWishList();
		}
	}

	public ArrayList<ItemObject> invalidateFilter(ArrayList<ItemObject> mListItems) {
		DBLog.d(TAG, "============>list Item=" + (mListItems == null ? 0 : mListItems.size()));
		ArrayList<FilterObject> mListFilterObjects = TotalDataManager.getInstance().getListFilterObjects();
		if (mListItems != null && mListItems.size() > 0) {
			ArrayList<ItemObject> listFilter = new ArrayList<ItemObject>();
			for (ItemObject mItemObject : mListItems) {
				try {
					if (mListFilterObjects != null && mListFilterObjects.size() > 0) {
						for (FilterObject mFilterObject : mListFilterObjects) {
							if (mFilterObject.isChecked()) {
								if (mItemObject.getTag().toLowerCase(Locale.US)
										.contains(mFilterObject.getTag().toLowerCase(Locale.US))) {
									listFilter.add(mItemObject);
									break;
								}
							}
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			return listFilter;

		}
		return mListItems;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mImageBg.getVisibility() == View.VISIBLE) {
				onHideMenu();
				return true;
			}
			Fragment mFragment1 =getSupportFragmentManager().findFragmentByTag(TAG_DETAIL_ITEM);
			if(mFragment1!=null){
				showTags(null, false);
			}
			boolean b = backStack(null);
			if (b) {
				return true;
			}
			FragmentHome mFragment = (FragmentHome) getSupportFragmentManager().findFragmentById(R.id.fragment_home);
			if(mFragment!=null && mFragment.checkBack()){
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
		mBigOptions = null;
	}

	@Override
	public void onDestroyData() {
		super.onDestroyData();
		if (mInterstitial != null && mInterstitial.isLoaded()) {
			mInterstitial.show();
		}
		try {
			ImageLoader.getInstance().stop();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		TotalDataManager.getInstance().onDestroy();
	}

	@Override
	public void onShowItemDetail(int idParent, String nameParent, String id) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		FragmentItems mFragment = new FragmentItems();
		Bundle mBundle = new Bundle();

		mBundle.putString(KEY_NAME_FRAGMENT, nameParent);
		mBundle.putInt(KEY_ID_FRAGMENT, idParent);
		mBundle.putString(KEY_ID, id);
		mFragment.setArguments(mBundle);
		addFragment(mFragment);

		transaction.add(R.id.layout_container, mFragment, TAG_DETAIL_ITEM);
		Fragment mFragmentParent = null;
		if (idParent > 0) {
			mFragmentParent = getSupportFragmentManager().findFragmentById(R.id.fragment_home);
		}
		else {
			mFragmentParent = getSupportFragmentManager().findFragmentByTag(nameParent);
		}
		if (mFragmentParent != null) {
			transaction.hide(mFragmentParent);
		}
		transaction.commit();
	}

	public void showWishList() {
		if (mListFragments != null && mListFragments.size() > 0) {
			backStack(null);
		}

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		FragmentWishList mFragment = new FragmentWishList();
		Bundle mBundle = new Bundle();

		mBundle.putString(KEY_NAME_FRAGMENT, "");
		mBundle.putInt(KEY_ID_FRAGMENT, R.id.fragment_home);
		mFragment.setArguments(mBundle);
		addFragment(mFragment);

		transaction.add(R.id.layout_container, mFragment, TAG_WISHLIST_ITEM);
		transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_home));
		transaction.commit();
	}

	public void showOffers() {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		FragmentOffers mFragment = new FragmentOffers();
		Bundle mBundle = new Bundle();

		mBundle.putString(KEY_NAME_FRAGMENT, "");
		mBundle.putInt(KEY_ID_FRAGMENT, R.id.fragment_home);
		mFragment.setArguments(mBundle);
		addFragment(mFragment);

		transaction.add(R.id.layout_container, mFragment, TAG_OFFER_ITEM);
		transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_home));
		transaction.commit();
	}
}
