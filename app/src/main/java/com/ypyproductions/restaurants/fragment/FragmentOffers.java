
package com.ypyproductions.restaurants.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.ypyproductions.abtractclass.fragment.DBFragment;
import com.ypyproductions.abtractclass.fragment.DBFragmentAdapter;
import com.ypyproductions.abtractclass.fragment.IDBFragmentConstants;
import com.ypyproductions.net.task.DBTask;
import com.ypyproductions.net.task.IDBTaskListener;
import com.ypyproductions.restaurants.MainActivity;
import com.ypyproductions.restaurants.R;
import com.ypyproductions.restaurants.constanst.IMyRestaurantConstants;
import com.ypyproductions.restaurants.object.OfferObject;
import com.ypyproductions.restaurants.parsingdata.JsonParsingUtils;
import com.ypyproductions.restaurants.parsingdata.TotalDataManager;
import com.ypyproductions.utils.ApplicationUtils;
import com.ypyproductions.utils.DownloadUtils;
import com.ypyproductions.utils.IOUtils;
import com.ypyproductions.utils.ResolutionUtils;
import com.ypyproductions.utils.StringUtils;

public class FragmentOffers extends DBFragment implements IMyRestaurantConstants,IDBFragmentConstants {
	
	public static final String TAG = FragmentOffers.class.getSimpleName();
	
	private MainActivity mContext;
	private LinearLayout mLayoutPage;
	private ViewPager mViewPagers;

	private Button mBtBack;

	private DBTask mDBTask;

	protected ArrayList<OfferObject> mListOffers;

	private TextView mTvResult;
	private ArrayList<Fragment> mListFragments= new ArrayList<Fragment>();

	private DBFragmentAdapter mPagerAdapter;

	@Override
	public View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_offers, container, false);
	}

	@Override
	public void findView() {
		this.mContext = (MainActivity) getActivity();
		this.mBtBack = (Button) mRootView.findViewById(R.id.btn_back);
		this.mTvResult =(TextView) mRootView.findViewById(R.id.tv_no_result);
		
		mBtBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mContext.backStack(null);
			}
		});
		mLayoutPage = (LinearLayout) mRootView.findViewById(R.id.layout_indicator);
		mViewPagers = (ViewPager) mRootView.findViewById(R.id.layout_offers);
		startLoadOffers();
	}
	
	private void startLoadOffers(){
		if(!ApplicationUtils.isOnline(mContext)  && !StringUtils.isEmptyString(URL_LIST_ALL_ITEM) && URL_LIST_ALL_ITEM.startsWith("http")){
			mContext.showToast(R.string.info_lose_internet);
			return;
		}
		mDBTask = new DBTask(new IDBTaskListener() {
			
			@Override
			public void onPreExcute() {
				mContext.showProgressDialog();
			}
			
			@Override
			public void onDoInBackground() {
				String dataOffers="";
				if(!StringUtils.isEmptyString(URL_LIST_OFFERS)){
					if(URL_LIST_OFFERS.startsWith("http")){
						dataOffers = DownloadUtils.downloadString(URL_LIST_OFFERS);
					}
					else{
						dataOffers=IOUtils.readStringFromAssets(mContext, URL_LIST_OFFERS);
					}
				}
				if(!StringUtils.isEmptyString(dataOffers)){
					mListOffers =JsonParsingUtils.parseListOfferObject(dataOffers);
					if(mListOffers!=null && mListOffers.size()>0){
						TotalDataManager.getInstance().setListOfferObjects(mListOffers);
					}
				}
				
			}
			@Override
			public void onPostExcute() {
				mContext.dimissProgressDialog();
				setUpFragment();
			}
			
		});
		mDBTask.execute();
	}
	
	public void setUpFragment() {
		if(mListOffers!=null && mListOffers.size()>0){
			mTvResult.setVisibility(View.GONE);
			int size = mListOffers.size();
			for (int i = 0; i < size; i++) {
				Bundle mBundle1 = new Bundle();
				mBundle1.putString(KEY_ID, mListOffers.get(i).getId());
				Fragment mFragment = Fragment.instantiate(mContext, FragmentDetailOffer.class.getName(), mBundle1);
				mListFragments.add(mFragment);
				
				if(size>1){
					ImageView mImageView = new ImageView(mContext);
					int width = (int) ResolutionUtils.convertDpToPixel(mContext, 10);
					LinearLayout.LayoutParams mLayoutParams = new LayoutParams(width, width);
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
			mPagerAdapter = new DBFragmentAdapter(getChildFragmentManager(), mListFragments);
			mViewPagers.setAdapter(mPagerAdapter);
			mViewPagers.setOnPageChangeListener(new OnPageChangeListener() {
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
		}
		else{
			mTvResult.setVisibility(View.VISIBLE);
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mListFragments!=null){
			mListFragments.clear();
			mListFragments=null;
		}
	}
}
