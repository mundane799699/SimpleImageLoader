package me.mundane.sample.recycler;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder<T>> {
	
	/**
	 * click listener
	 */
	protected OnItemClickListener mOnItemClickListener;
	
	public interface OnItemClickListener<T> {
		void onClick(View view, T item);
	}
	
	/**
	 * long click listener
	 */
	protected OnItemLongClickListener mOnItemLongClickListener;
	
	public interface OnItemLongClickListener<T> {
		void onLongClick(View view, T item);
	}
	
	/**
	 * data
	 */
	protected List<T> mList;
	
	/**
	 * @param list the datas to attach the adapter
	 */
	public BaseRecyclerViewAdapter(List<T> list) {
		mList = list;
	}
	
	/**
	 * get a item by index
	 *
	 * @param position
	 * @return
	 */
	protected T getItem(int position) {
		return mList.get(position);
	}
	
	@Override
	public int getItemCount() {
		return mList == null ? 0 : mList.size();
	}
	
	/**
	 * set a long click listener
	 *
	 * @param onItemLongClickListener
	 */
	public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
		mOnItemLongClickListener = onItemLongClickListener;
	}
	
	/**
	 * set a click listener
	 *
	 * @param onItemClickListener
	 */
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
	}
	
	@Override
	public BaseViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(provideItemLayout(), parent, false);
		return getViewHolder(itemView);
	}
	
	public abstract @LayoutRes int provideItemLayout();
	
	protected abstract BaseViewHolder<T> getViewHolder(View itemView);
	
	@Override
	public void onBindViewHolder(BaseViewHolder<T> holder, final int position) {
		if (mOnItemClickListener != null) {
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOnItemClickListener.onClick(v, getItem(position));
				}
			});
		}
		if (mOnItemLongClickListener != null) {
			holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					mOnItemLongClickListener.onLongClick(v, getItem(position));
					return false;
				}
			});
		}
		holder.bindHolder(getItem(position));
	}
	
}
