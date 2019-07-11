package com.ypyproductions.restaurants.adapter;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ypyproductions.abtractclass.DBBaseAdapter;
import com.ypyproductions.net.task.IDBCallback;
import com.ypyproductions.restaurants.R;
import com.ypyproductions.restaurants.constanst.IMyRestaurantConstants;
import com.ypyproductions.restaurants.object.ItemObject;
import com.ypyproductions.restaurants.parsingdata.TotalDataManager;
import com.ypyproductions.utils.DBListExcuteAction;
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
 * @Date:Jan 25, 2015
 * 
 */
public class NewWishListAdapter extends DBBaseAdapter implements IMyRestaurantConstants{

	public static final String TAG = NewWishListAdapter.class.getSimpleName();

	private DisplayImageOptions mOptions;

	private OnWistListListener onWistListListener;

	public NewWishListAdapter(Activity mContext, ArrayList<? extends Object> listObjects, DisplayImageOptions mOptions) {
		super(mContext, listObjects);
		this.mOptions = mOptions;
	}

	@Override
	public View getAnimatedView(int arg0, View arg1, ViewGroup arg2) {
		return null;
	}

	@Override
	public View getNormalView(int position, View convertView, ViewGroup parent) {
		final ChildHolder holder;
		if (convertView == null) {
			convertView =LayoutInflater.from(mContext).inflate(R.layout.item_wishlist_dish, null);
			holder = new ChildHolder();
			convertView.setTag(holder);
			
			holder.mImgBtnAdd = (ImageView) convertView.findViewById(R.id.btn_add);
			holder.mImgBtnSub = (ImageView) convertView.findViewById(R.id.btn_sub);
			holder.mImgDish = (ImageView) convertView.findViewById(R.id.img_dish);
			holder.mLayoutRoot = (RelativeLayout) convertView.findViewById(R.id.layout_root);

			holder.mTvPrice = (TextView) convertView.findViewById(R.id.tv_price);
			holder.mTvDescription = (TextView) convertView.findViewById(R.id.tv_description);
			holder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
		}
		else {
			holder = (ChildHolder) convertView.getTag();
		}

		final ItemObject mItemObject = (ItemObject) mListObjects.get(position);
		holder.mTvDescription.setText(mItemObject.getDescription());
		float mUnitPrice = Float.parseFloat(mItemObject.getPrice());

		float totalPrice = mItemObject.getQuantity() * mUnitPrice;
		holder.mTvPrice.setText(formatMoney(totalPrice));
		holder.mTvName.setText(String.valueOf(mItemObject.getQuantity()) + "x " + mItemObject.getName());
		
		holder.mLayoutRoot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(onWistListListener!=null){
					onWistListListener.onViewDetail(mItemObject);
				}
			}
		});

		holder.mImgBtnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int quantity = mItemObject.getQuantity();
				quantity++;
				mItemObject.setQuantity(quantity);
				notifyDataSetChanged();
				if (onWistListListener != null) {
					onWistListListener.onChangeTotalPriceWishList();
				}
				DBListExcuteAction.getInstance().queueAction(new IDBCallback() {
					@Override
					public void onAction() {
						TotalDataManager.getInstance().saveWishList(mContext);
					}
				});
			}
		});

		holder.mImgBtnSub.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int quantity = mItemObject.getQuantity();
				quantity--;
				if (quantity <= 0) {
					mItemObject.setQuantity(quantity);
					quantity = 0;
					if (onWistListListener != null) {
						onWistListListener.onRemoveItemToWishList(mItemObject);
					}
				}
				else {
					mItemObject.setQuantity(quantity);
					notifyDataSetChanged();
					if (onWistListListener != null) {
						onWistListListener.onChangeTotalPriceWishList();
					}
					DBListExcuteAction.getInstance().queueAction(new IDBCallback() {
						@Override
						public void onAction() {
							TotalDataManager.getInstance().saveWishList(mContext);
						}
					});
				}
			}
		});
		if (!StringUtils.isEmptyString(mItemObject.getImage())) {
			String itemImg = mItemObject.getImage();
			if(!itemImg.startsWith("http")){
				itemImg="assets://"+itemImg;
			}
			ImageLoader.getInstance().displayImage(itemImg, holder.mImgDish, mOptions);
		}
		return convertView;
	}
	
	private String formatMoney(float total) {
		String formatSubTotal = String.format(Locale.US, "%.2f", total);
		try {
			formatSubTotal = formatSubTotal.replace(",", ".");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return formatSubTotal;
	}
	
//	public void removeItemToWishList(ItemObject mItemObject){
//		TotalDataManager.getInstance().removeItemInWishList(mContext, mItemObject);
//		String info = String.format(mContext.getString(R.string.format_info_remove_wishlist), mItemObject.getName());
//		mContext.showToast(info);
//		mItemObject.updateWishList(false);
//		
//		notifyDataSetChanged();
//		if(onWistListListener!=null && TotalDataManager.getInstance().getListWishListObjects().size()==0){
//			onWistListListener.outOfWistList();
//		}
//	}
	
	public void setOnWistListListener(OnWistListListener onWistListListener) {
		this.onWistListListener = onWistListListener;
	}

	public interface OnWistListListener{
		public void onChangeTotalPriceWishList();
		public void onRemoveItemToWishList(ItemObject mItemObject);
		public void onViewDetail(ItemObject mItemObject);
	}

	private static class ChildHolder {
		public ImageView mImgBtnSub;
		public ImageView mImgBtnAdd;
		public ImageView mImgDish;
		public TextView mTvPrice;
		public TextView mTvDescription;
		public TextView mTvName;
		public RelativeLayout mLayoutRoot;
	}

}
