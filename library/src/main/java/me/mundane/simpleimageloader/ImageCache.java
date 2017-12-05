package me.mundane.simpleimageloader;

import android.graphics.Bitmap;

/**
 * Created by mundane on 2017/11/27 下午9:09
 */

public interface ImageCache {
	Bitmap get(String url);
	
	void remove(String url);
}
