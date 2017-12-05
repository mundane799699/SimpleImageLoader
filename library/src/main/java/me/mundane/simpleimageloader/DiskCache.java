package me.mundane.simpleimageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import me.mundane.simpleimageloader.utils.BitmapUtils;
import me.mundane.simpleimageloader.utils.MD5Util;


/**
 * Created by mundane on 2017/11/27 下午9:39
 */

public class DiskCache implements ImageCache {
	private DiskLruCache mDiskLruCache;
	private static final int DISK_CACHE_SIZE = 10 * 1024 * 1024; // 10MB
	private final String TAG = "DiskCache";
	private ImageFetcher mImageFetcher;
	
	public DiskCache(Context context) {
		mImageFetcher = new UrlConnectionImageFetcher();
		File cacheDir = getDiskCacheDir(context, "bitmap");
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		try {
			mDiskLruCache = DiskLruCache.open(cacheDir, BuildConfig.VERSION_CODE, 1, DISK_CACHE_SIZE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public File getDiskCacheDir(Context context, String dirName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + dirName);
	}
	
	@Override
	public Bitmap get(String url) {
		Bitmap bitmap = getFromDiskCache(url, 0, 0);
		if (bitmap != null) {
			return bitmap;
		}
		downloadBitmapToDiskCache(url);
		return getFromDiskCache(url, 0, 0);
	}
	
	public Bitmap get(String url, int reqWidth, int reqHeight) {
		Bitmap bitmap = getFromDiskCache(url, reqWidth, reqHeight);
		if (bitmap != null) {
			return bitmap;
		}
		downloadBitmapToDiskCache(url);
		return getFromDiskCache(url, reqWidth, reqHeight);
	}
	
	public Bitmap getFromDiskCache(String url, int reqWidth, int reqHeight) {
		String key = MD5Util.hashKeyFromUrl(url);
		try {
			DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
			if (snapshot == null) {
				return null;
			}
			FileInputStream fis = (FileInputStream) snapshot.getInputStream(0);
			if (reqWidth <= 0 || reqHeight <= 0) {
				return BitmapFactory.decodeStream(fis);
			} else {
				return BitmapUtils.getSmallBitmap(fis.getFD(), reqWidth, reqHeight);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public void downloadBitmapToDiskCache(String url) {
		String key = MD5Util.hashKeyFromUrl(url);
		try {
			DiskLruCache.Editor editor = mDiskLruCache.edit(key);
			if (editor == null) {
				return;
			}
			// 由于在创建DiskLruCache的时候valueCount指定为1, 所以这里索引传0就可以了
			OutputStream outputStream = editor.newOutputStream(0);
			if (mImageFetcher.downloadSuccess(url, outputStream)) {
				editor.commit();
			} else {
				editor.abort();
			}
			mDiskLruCache.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void remove(String url) {
		String key = MD5Util.hashKeyFromUrl(url);
		try {
			mDiskLruCache.remove(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void removeAll() {
		try {
			// 用于将所有的缓存数据全部删除
			mDiskLruCache.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
