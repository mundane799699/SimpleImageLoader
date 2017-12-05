package me.mundane.sample.recycler;

import android.view.View;
import android.widget.ImageView;

import java.util.List;

import me.mundane.sample.R;
import me.mundane.sample.model.GankMeiziResult;
import me.mundane.simpleimageloader.ImageLoader;

/**
 * Created by mundane on 2017/12/4 下午8:25
 */

public class MeiZiAdapter extends BaseRecyclerViewAdapter<GankMeiziResult.GankBeauty> {
	/**
	 * @param list the datas to attach the adapter
	 */
	public MeiZiAdapter(List<GankMeiziResult.GankBeauty> list) {
		super(list);
	}
	
	@Override
	public int provideItemLayout() {
		return R.layout.layout_item;
	}
	
	@Override
	protected BaseViewHolder<GankMeiziResult.GankBeauty> getViewHolder(View itemView) {
		return new MeiZiViewHolder(itemView);
	}
	
	static class MeiZiViewHolder extends BaseViewHolder<GankMeiziResult.GankBeauty> {
		ImageView iv;
		public MeiZiViewHolder(View itemView) {
			super(itemView);
			iv = itemView.findViewById(R.id.iv);
		}
		
		@Override
		public void bindHolder(GankMeiziResult.GankBeauty gankBeauty) {
			ImageLoader.getInstance(itemView.getContext()).displayImage(gankBeauty.url, iv);
		}
	}
	
}
