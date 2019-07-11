package com.ypyproductions.restaurants;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.ypyproductions.restaurants.adapter.ExpandableListItemAdapter;
import com.ypyproductions.restaurants.object.CategoryObject;
import com.ypyproductions.restaurants.object.ItemObject;
import com.ypyproductions.restaurants.parsingdata.TotalDataManager;
import com.ypyproductions.utils.ApplicationUtils;
import com.ypyproductions.utils.DirectionUtils;

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
public class SearchActivity extends DBFragmentActivity implements OnClickListener {

	public static final String TAG = SplashActivity.class.getSimpleName();

	private TextView mTvHeaderSearch;
	private TextView mBtCancel;
	private EditText mEdSearch;
	private ExpandableListView mListRestaurant;
	private Handler mHandler = new Handler();

	private TextView mTvResult;

	private ExpandableListItemAdapter mAdapter;

	private DisplayImageOptions mOptions;

	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_search);
		mTvHeaderSearch = (TextView) findViewById(R.id.tv_search);
		mBtCancel = (TextView) findViewById(R.id.btn_back);
		mEdSearch = (EditText) findViewById(R.id.ed_search);
		
		mTvResult =(TextView) findViewById(R.id.tv_no_result);
		mListRestaurant = (ExpandableListView) findViewById(R.id.list_restaurant);
		
		this.mOptions = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.ic_launcher)
        .resetViewBeforeLoading(false)  
        .cacheInMemory(true)
        .displayer(new RoundedBitmapDisplayer(1000))
        .cacheOnDisk(true)
        .considerExifParams(true)
        .build();
		
		mAdapter = new ExpandableListItemAdapter(this, new ArrayList<CategoryObject>(), mOptions,true);
		mListRestaurant.setAdapter(mAdapter);
		
		mEdSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					ApplicationUtils.hiddenVirtualKeyboard(SearchActivity.this, mEdSearch);
					String text = mEdSearch.getText().toString();
					performSearch(text);
					return true;
				}
				return false;
			}
		});
		mEdSearch.addTextChangedListener(new TextWatcher() {
		    @Override
		    public void onTextChanged(final CharSequence s, int start, int before, int count) {
		    	performSearch(String.valueOf(s));
		    }
		     
		    @Override
		    public void beforeTextChanged(CharSequence s, int start, int count,
		            int after) {
		         
		    }
		     
		    @Override
		    public void afterTextChanged(Editable s) {
		    	
		    }
		});
		mAdapter.setOnSearchItemListener(new ExpandableListItemAdapter.OnSearchItemListener() {
			
			@Override
			public void onSearchFail() {
				mTvResult.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onGoToDetail(ItemObject mItemObject) {
				//showLayoutSearch(false);
				Intent mIntent = new Intent(SearchActivity.this, MainActivity.class);
				mIntent.putExtra(KEY_ID, mItemObject.getId());
				DirectionUtils.changeActivity(SearchActivity.this, R.anim.slide_in_from_left, R.anim.slide_out_to_right, true, mIntent);
			}
			
			@Override
			public void addToWishList(ItemObject mItemObject) {
				checkAddToWishList(mItemObject);
			}

			@Override
			public void onSearchDone() {
				int count =mAdapter.getGroupCount();
				if(count>0){
					for(int i=0;i<count;i++){
						mListRestaurant.expandGroup(i);
					}
				}
			}
		});
		showLayoutSearch(true);
		setUpLayoutAdmob();
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
			}
			else{
				layout.setVisibility(View.GONE);
			}
			
		}
		else {
			layout.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
	}
	public void checkAddToWishList(final ItemObject mItemObject){
		boolean isWistList = TotalDataManager.getInstance().isWishListItem(mItemObject.getId());
		if(!isWistList){
			TotalDataManager.getInstance().addItemToWishList(this, mItemObject);
			mAdapter.notifyDataSetChanged();
			String info = String.format(getString(R.string.format_info_add_wishlist), mItemObject.getName());
			showToast(info);
		}
		else{
			TotalDataManager.getInstance().removeItemInWishList(this, mItemObject);
			String info = String.format(getString(R.string.format_info_remove_wishlist), mItemObject.getName());
			mAdapter.notifyDataSetChanged();
			showToast(info);
		}
	}

	private void performSearch(final String text) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mTvResult.setVisibility(View.GONE);
				mAdapter.getFilter().filter(text);
			}
		}, 500);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			showLayoutSearch(false);
			break;
		case R.id.btn_search:
			showLayoutSearch(true);
			break;

		default:
			break;
		}
	}

	private void showLayoutSearch(boolean b) {
		mTvHeaderSearch.setVisibility(b ? View.GONE : View.VISIBLE);
		mEdSearch.setVisibility(b ? View.VISIBLE : View.GONE);
		mBtCancel.setVisibility(b ? View.VISIBLE : View.GONE);
		if (b) {
			mEdSearch.setText("");
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mEdSearch.requestFocus();
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			        if (imm != null) {
			            imm.showSoftInput(mEdSearch,0);
			        }
				}
			}, 200);

		}
		else {
			ApplicationUtils.hiddenVirtualKeyboard(this, mEdSearch);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
		mAdapter=null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(mEdSearch.getVisibility()==View.VISIBLE){
				showLayoutSearch(false);
				return true;
			}
			backToHome();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void backToHome() {
		Intent mIt = new Intent(this, MainActivity.class);
		DirectionUtils.changeActivity(this, R.anim.slide_in_from_left, R.anim.slide_out_to_right, true, mIt);
	}
}
