package com.ypyproductions.restaurants.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.ypyproductions.abtractclass.fragment.DBFragment;
import com.ypyproductions.net.task.DBTask;
import com.ypyproductions.restaurants.MainActivity;
import com.ypyproductions.restaurants.R;
import com.ypyproductions.restaurants.constanst.IMyRestaurantConstants;
import com.ypyproductions.restaurants.object.ItemObject;
import com.ypyproductions.restaurants.parsingdata.TotalDataManager;
import com.ypyproductions.utils.DBLog;
import com.ypyproductions.utils.StringUtils;

public class FragmentDetailItem extends DBFragment implements IMyRestaurantConstants {

	public static final String TAG = FragmentDetailItem.class.getSimpleName();

	private MainActivity mContext;

	private TextView mTvName;

	private TextView mTvDescription;

	private TextView mTvPrice;

	private ImageView mImgFeature;

	private String mIDItem;

	private ProgressBar mProgressBar;

	private ItemObject mItem;

	private DBTask mDBTask;

	protected Bitmap mBitmap;

	@Override
	public View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_detail_item, container, false);
	}

	@Override
	public void findView() {
		setAllowFindViewContinous(true);

		this.mContext = (MainActivity) getActivity();
		this.mTvName = (TextView) mRootView.findViewById(R.id.tv_name);
		this.mTvDescription = (TextView) mRootView.findViewById(R.id.tv_des);
		this.mTvPrice = (TextView) mRootView.findViewById(R.id.tv_price);
		this.mImgFeature = (ImageView) mRootView.findViewById(R.id.img_feature);
		Button mBtShare = (Button) mRootView.findViewById(R.id.btn_share);
		mBtShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mItem != null) {
					String linkAapp = String.format(URL_FORMAT_LINK_APP, mContext.getPackageName());
					String content = mItem.getName() + "\n" + mItem.getDescription() + "\n" + mItem.getPrice() + "\n" + linkAapp;
					DBLog.d(TAG, "============>content=" + content);
					Intent intent = new Intent(android.content.Intent.ACTION_SEND);
					intent.setType("text/plain");
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
					intent.putExtra(Intent.EXTRA_TEXT, content);
					startActivity(intent);

				}
			}
		});
		this.mTvPrice = (TextView) mRootView.findViewById(R.id.tv_price);
		mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressBar1);

		if (!StringUtils.isEmptyString(mIDItem)) {
			mItem = TotalDataManager.getInstance().getItemObject(mIDItem);
			setUpInfo(mItem);
		}
	}

	private void setUpInfo(final ItemObject mItem) {
		mTvName.setText(mItem.getName());
		mTvDescription.setText(mItem.getDescription());
		mTvPrice.setText(mItem.getPrice() + " " + CURRENCY);

		String itemImg = mItem.getImage();
		if (!StringUtils.isEmptyString(itemImg)) {
			if (!itemImg.startsWith("http")) {
				itemImg = "assets://" + itemImg;
			}
			ImageLoader.getInstance().displayImage(itemImg, this.mImgFeature, mContext.mBigOptions, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					mProgressBar.setVisibility(View.GONE);
				}
			});
		}

	}

	@Override
	public void onExtractData() {
		super.onExtractData();
		Bundle args = getArguments();
		if (args != null) {
			mIDItem = args.getString(KEY_ID);
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mDBTask != null) {
			mDBTask.cancel(true);
			mDBTask = null;
		}
	}

}
