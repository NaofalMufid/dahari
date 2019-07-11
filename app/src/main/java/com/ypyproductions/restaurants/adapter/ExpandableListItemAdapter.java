package com.ypyproductions.restaurants.adapter;

import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ypyproductions.restaurants.DBFragmentActivity;
import com.ypyproductions.restaurants.R;
import com.ypyproductions.restaurants.constanst.IMyRestaurantConstants;
import com.ypyproductions.restaurants.object.CategoryObject;
import com.ypyproductions.restaurants.object.ItemObject;
import com.ypyproductions.restaurants.parsingdata.TotalDataManager;
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
public class ExpandableListItemAdapter extends BaseExpandableListAdapter implements IMyRestaurantConstants, Filterable {

	public static final String TAG = ExpandableListItemAdapter.class.getSimpleName();

	private DBFragmentActivity mContext;
	private ArrayList<CategoryObject> listCategoryObjects;
	private OnSearchItemListener onSearchItemListener;

	protected ArrayList<CategoryObject> resultList;

	private DisplayImageOptions mOptions;

	private boolean isSearch;

	public ExpandableListItemAdapter(DBFragmentActivity mContext, ArrayList<CategoryObject> listCategoryObjects, 
			DisplayImageOptions mOptions, boolean isSearch) {
		this.mContext = mContext;
		this.listCategoryObjects = listCategoryObjects;
		this.mOptions = mOptions;
		this.isSearch=isSearch;
	}

	@Override
	public ItemObject getChild(int groupPosition, int childPosition) {
		if(isSearch){
			return getGroup(groupPosition).getListSearchObjects().get(childPosition);
		}
		else{
			return getGroup(groupPosition).getListItemObjects().get(childPosition);
		}
		
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (getGroup(groupPosition).getListItemObjects() != null) {
			if(isSearch){
				return getGroup(groupPosition).getListSearchObjects().size();
			}
			else{
				return getGroup(groupPosition).getListItemObjects().size();
			}
		}
		return 0;
	}

	@Override
	public CategoryObject getGroup(int groupPosition) {
		return listCategoryObjects.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return listCategoryObjects.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		final ChildHolder holder;
		if (convertView == null) {
			convertView = mContext.getLayoutInflater().inflate(R.layout.item_search_dish, null);
			holder = new ChildHolder();

			convertView.setTag(holder);
		}
		else {
			holder = (ChildHolder) convertView.getTag();
		}
		holder.mImgBtnAdd = (ImageView) convertView.findViewById(R.id.img_indicator);
		holder.mTvPrice = (TextView) convertView.findViewById(R.id.tv_price);
		holder.mTvDescription = (TextView) convertView.findViewById(R.id.tv_description);
		holder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
		holder.mImgDish = (ImageView) convertView.findViewById(R.id.img_dish);
		holder.mLayoutRoot = (RelativeLayout) convertView.findViewById(R.id.layout_root);

		final ItemObject mItemObject = getChild(groupPosition, childPosition);
		holder.mTvDescription.setText(mItemObject.getDescription());
		holder.mTvPrice.setText(mItemObject.getPrice());
		holder.mTvName.setText(mItemObject.getName());

		holder.mImgBtnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onSearchItemListener != null) {
					onSearchItemListener.addToWishList(mItemObject);
				}
			}
		});
		holder.mLayoutRoot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onSearchItemListener != null) {
					onSearchItemListener.onGoToDetail(mItemObject);
				}
			}
		});
		boolean isWistList = TotalDataManager.getInstance().isWishListItem(mItemObject.getId());
		holder.mImgBtnAdd.setImageResource(isWistList ? R.drawable.small_heart_normal : R.drawable.ic_add_normal);

		if (!StringUtils.isEmptyString(mItemObject.getImage())) {
			String itemImg = mItemObject.getImage();
			if(!itemImg.startsWith("http")){
				itemImg="assets://"+itemImg;
			}
			ImageLoader.getInstance().displayImage(itemImg, holder.mImgDish, mOptions);
		}
		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		GroupHolder holder;
		if (convertView == null) {
			convertView = mContext.getLayoutInflater().inflate(R.layout.item_category, null);
			holder = new GroupHolder();
			holder.mTvCatName = (TextView) convertView.findViewById(R.id.tv_cat);
			holder.mImageIndicator = (ImageView) convertView.findViewById(R.id.img_indicator);
			convertView.setTag(holder);
		}
		else {
			holder = (GroupHolder) convertView.getTag();
		}
		CategoryObject entry = getGroup(groupPosition);
		holder.mTvCatName.setText(entry.getName());
		holder.mImageIndicator.setImageResource(isExpanded ? R.drawable.icon_expand : R.drawable.icon_collapse);
		return convertView;
	}

	private static class ChildHolder {
		public ImageView mImgBtnAdd;
		public ImageView mImgDish;
		public TextView mTvPrice;
		public TextView mTvDescription;
		public TextView mTvName;
		public RelativeLayout mLayoutRoot;
	}

	private static class GroupHolder {
		public TextView mTvCatName;
		public ImageView mImageIndicator;
	}

	public interface OnSearchItemListener {
		public void addToWishList(ItemObject mItemObject);

		public void onGoToDetail(ItemObject mItemObject);

		public void onSearchFail();

		public void onSearchDone();
	}

	public void setOnSearchItemListener(OnSearchItemListener onSearchItemListener) {
		this.onSearchItemListener = onSearchItemListener;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public ArrayList<CategoryObject> getListCategoryObjects() {
		return listCategoryObjects;
	}

	public void setListCategoryObjects(ArrayList<CategoryObject> listCategoryObjects) {
		if (listCategoryObjects != null) {
			this.listCategoryObjects.clear();
			this.listCategoryObjects = listCategoryObjects;
			notifyDataSetChanged();
		}
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					String data = constraint.toString();
					if (!StringUtils.isEmptyString(data)) {
						resultList = TotalDataManager.getInstance().getListCategoryObjects(data);
						if (resultList != null) {
							filterResults.values = resultList;
							filterResults.count = resultList.size();
						}
					}
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results != null) {
					setListCategoryObjects(resultList);
					if (resultList == null || resultList.size() == 0) {
						if (onSearchItemListener != null) {
							onSearchItemListener.onSearchFail();
						}
					}
					else {
						if (onSearchItemListener != null) {
							onSearchItemListener.onSearchDone();
						}
					}
				}
			}
		};
		return filter;
	}
}
