
package com.ypyproductions.restaurants;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;

import com.ypyproductions.net.task.IDBCallback;


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
public class YPYAnimationUtils {
	
	public static final String TAG = YPYAnimationUtils.class.getSimpleName();
	
	public static void animTranslateX(final View mView,float fromX,  float toX, final IDBCallback mDCallback){
		mView.setX(fromX);
		
		AccelerateInterpolator mInterpolator = new AccelerateInterpolator();
		mView.clearAnimation();
		final ViewPropertyAnimator localViewPropertyAnimator = mView.animate().x(toX).setDuration(300).setInterpolator(mInterpolator);
		localViewPropertyAnimator.setListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				if(mDCallback!=null){
					mDCallback.onAction();
				}
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				if(mDCallback!=null){
					mDCallback.onAction();
				}
			}
		});
		localViewPropertyAnimator.start();
	}
}
