package com.ypyproductions.restaurants;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.ypyproductions.net.task.DBTask;
import com.ypyproductions.net.task.IDBTaskListener;
import com.ypyproductions.restaurants.constanst.IMyRestaurantConstants;
import com.ypyproductions.restaurants.object.WishListObject;
import com.ypyproductions.restaurants.parsingdata.JsonParsingUtils;
import com.ypyproductions.restaurants.parsingdata.TotalDataManager;
import com.ypyproductions.utils.ApplicationUtils;
import com.ypyproductions.utils.DBLog;
import com.ypyproductions.utils.DirectionUtils;
import com.ypyproductions.utils.IOUtils;
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

public class SplashActivity extends DBFragmentActivity implements IMyRestaurantConstants {

	public static final String TAG = SplashActivity.class.getSimpleName();

	private ProgressBar mProgressBar;
	private boolean isPressBack;

	private Handler mHandler = new Handler();
	private TextView mTvVersion;
	private TextView mTvLogo;
	private TextView mTvCopyright;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_splash);
		this.mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		this.mTvVersion = (TextView) findViewById(R.id.tv_version);
		this.mTvLogo = (TextView) findViewById(R.id.tv_logo);
		this.mTvCopyright = (TextView) findViewById(R.id.tv_copyright);

		this.mTvLogo.setTypeface(mTypefaceSologan);
		this.mTvVersion.setTypeface(mTypefaceNormal);
		this.mTvCopyright.setTypeface(mTypefaceNormal);

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).memoryCacheExtraOptions(300, 300).diskCacheExtraOptions(300, 300, null)
				.threadPriority(Thread.NORM_PRIORITY).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(80 * 1024 * 1024) // 50
																																													// Mb
				.tasksProcessingOrder(QueueProcessingType.FIFO).writeDebugLogs().build();
		ImageLoader.getInstance().init(config);

		mTvVersion.setText(String.format(getString(R.string.info_version_format), ApplicationUtils.getVersionName(this)));

		DBLog.setDebug(DEBUG);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!ApplicationUtils.isOnline(this) && !StringUtils.isEmptyString(URL_LIST_ALL_ITEM) && URL_LIST_ALL_ITEM.startsWith("http")) {
			isPressBack = true;
			showDialogFragment(DIALOG_LOSE_CONNECTION);
		}
		else {
			isPressBack = false;
			mProgressBar.setVisibility(View.VISIBLE);
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					startLoad();
				}
			}, 2000);
		}
	}

	private void startLoad() {
		final File mFile = IOUtils.getDiskCacheDir(this, DIR_FAVORITE);
		if (!mFile.exists()) {
			mFile.mkdirs();
		}
		DBTask mDBTask = new DBTask(new IDBTaskListener() {

			@Override
			public void onPreExcute() {
				mProgressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onDoInBackground() {
				String dataWishList = IOUtils.readString(SplashActivity.this, mFile.getAbsolutePath(), FILE_FAVORITE_DISH);
				if (!StringUtils.isEmptyString(dataWishList)) {
					WishListObject mWishListObject = JsonParsingUtils.parseListWishListObject(dataWishList);
					if (mWishListObject != null) {
						TotalDataManager.getInstance().setWishListObject(mWishListObject);
					}
					else {
						TotalDataManager.getInstance().setWishListObject(new WishListObject("1", "WistList"));
					}
				}
				else {
					TotalDataManager.getInstance().setWishListObject(new WishListObject("1", "WistList"));
				}
			}

			@Override
			public void onPostExcute() {
				mProgressBar.setVisibility(View.INVISIBLE);
				Intent mIt = new Intent(SplashActivity.this, MainActivity.class);
				DirectionUtils.changeActivity(SplashActivity.this, R.anim.slide_in_from_right, R.anim.slide_out_to_left, true, mIt);
			}

		});
		mDBTask.execute();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isPressBack) {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
