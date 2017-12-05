package me.mundane.sample.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by mundane on 2017/12/5 下午8:54
 */

public class RatioImageView extends ImageView {
	public RatioImageView(Context context) {
		super(context);
	}
	
	public RatioImageView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}
	
	public RatioImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	private float ratio;
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		ratio = bm.getHeight() * 1.0f / bm.getWidth();
		super.setImageBitmap(bm);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (ratio > 0) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = (int) (width * ratio);
			setMeasuredDimension(width, height);
			return;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
