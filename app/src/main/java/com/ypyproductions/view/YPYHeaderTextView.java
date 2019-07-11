package com.ypyproductions.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ypyproductions.utils.ResolutionUtils;

/**
 * 
 * 
 * @author:YPY Productions
 * @Skype: baopfiev_k50
 * @Mobile : +84 983 028 786
 * @Email: dotrungbao@gmail.com
 * @Website: www.ypyproductions.com
 * @Project:AlArabPhone
 * @Date:Jan 13, 2015
 * 
 */
public class YPYHeaderTextView extends TextView {

	public static final String TAG = YPYHeaderTextView.class.getSimpleName();
	private Context mContext;
	private Paint mPaint;
	private Rect mRect;
	private int backgroundTextColor = Color.parseColor("#80000000");
	private int PADDING = 1;

	public YPYHeaderTextView(Context context) {
		super(context);
		this.mContext = context;
		this.init();
	}

	public YPYHeaderTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.mContext = context;
		this.init();
	}

	public YPYHeaderTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		this.init();
	}

	private void init() {
		mRect = new Rect();
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setColor(backgroundTextColor);
		mPaint.setAntiAlias(true);
	}

	public void setBackgroundTextColor(int backgroundTextColor) {
		this.backgroundTextColor = backgroundTextColor;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int height = getHeight();
		int lineHeight = getLineHeight();
		int count = height / lineHeight;
		if (getLineCount() > count) {
			count = getLineCount();
		}
		float lastBottom = 0;
		for (int i = 0; i < count; i++) {
			getLineBounds(i, mRect);
			int lineStart = getLayout().getLineStart(i);
			int lineEnd = getLayout().getLineEnd(i);
			String text = getText().subSequence(lineStart, lineEnd).toString();
			float lineWidth = getPaint().measureText(text);

			// mRect.left= (int)
			// (mRect.left-ResolutionUtils.convertDpToPixel(mContext, 1));
			mRect.right = (int) (lineWidth + ResolutionUtils.convertDpToPixel(mContext, PADDING)+getPaddingRight());
			mRect.left = (int) (mRect.right - lineWidth - ResolutionUtils.convertDpToPixel(mContext, PADDING)-getPaddingLeft());
			if (i == 0) {
				mRect.top = (int) (mRect.top - getPaddingTop());
				lastBottom = mRect.bottom;
			}
			else {
				mRect.top = (int) (lastBottom + ResolutionUtils.convertDpToPixel(mContext, PADDING));
				mRect.bottom = (int) (mRect.top + mRect.height() + getPaddingBottom());
				lastBottom = mRect.bottom;
			}
			canvas.drawRect(mRect, mPaint);
		}
		super.onDraw(canvas);
	}

}
