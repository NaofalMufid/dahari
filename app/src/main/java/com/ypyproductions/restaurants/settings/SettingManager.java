package com.ypyproductions.restaurants.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingManager implements ISettingConstants {

	public static final String TAG = SettingManager.class.getSimpleName();

	public static final String DYSONIT_SHARPREFS = "SHARED_PREFERENCES";

	public static void saveSetting(Context mContext, String mKey, String mValue) {
		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(DYSONIT_SHARPREFS, Context.MODE_PRIVATE);
		Editor editor = mSharedPreferences.edit();
		editor.putString(mKey, mValue);
		editor.commit();
	}

	public static String getSetting(Context mContext, String mKey, String mDefValue) {
		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(DYSONIT_SHARPREFS, Context.MODE_PRIVATE);
		return mSharedPreferences.getString(mKey, mDefValue);
	}

}
