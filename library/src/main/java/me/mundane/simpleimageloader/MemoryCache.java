package me.mundane.simpleimageloader;

import android.graphics.Bitmap;
import android.util.LruCache;

import me.mundane.simpleimageloader.utils.MD5Util;


/**
 * Created by mundane on 2017/11/27 下午9:30
 */

public class MemoryCache implements ImageCache {
	private LruCache<String, Bitmap> mMemoryCache;
	
	public MemoryCache() {
		final int MAX_MEMORY = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int CACHE_SIZE = MAX_MEMORY / 4;
		mMemoryCache = new LruCache<String, Bitmap>(CACHE_SIZE) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount() / 1024;
			}
		};
	}
	
	@Override
	public Bitmap get(String url) {
		return mMemoryCache.get(getKey(url));
	}
	
	public void put(String url, Bitmap bitmap) {
		mMemoryCache.put(getKey(url), bitmap);
	}
	
	private String getKey(String url) {
		return MD5Util.hashKeyFromUrl(url);
	}
	
	@Override
	public void remove(String url) {
		mMemoryCache.remove(getKey(url));
	}
}
