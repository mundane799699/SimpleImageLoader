package me.mundane.simpleimageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by mundane on 2017/11/27 下午9:10
 */

public class ImageLoader {
	private final String TAG = "ImageLoader";
	private MemoryCache mMemoryCache;
	private DiskCache mDiskCache;
	private static volatile ImageLoader INSTANCE;
	public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
	private static final int MAX_POOL_SIZE = 2 * CPU_COUNT + 1;
	private static final long KEEP_ALIVE_TIME = 5L;
	public Executor mThreadPoolExecutor;
	private Handler mHandler;
	private ImageLoader(Context context) {
		mMemoryCache = new MemoryCache();
		mDiskCache = new DiskCache(context);
		mThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
//		mThreadPoolExecutor = Executors.newCachedThreadPool();
		mHandler = new Handler(Looper.getMainLooper());
	}
	
	public static ImageLoader getInstance(Context context) {
		if (INSTANCE == null) {
			synchronized (ImageLoader.class) {
				if (INSTANCE == null) {
					INSTANCE = new ImageLoader(context.getApplicationContext());
				}
			}
		}
		return INSTANCE;
	}
	
	public void displayImage(String url, final ImageView imageView) {
		displayImage(url, imageView, 0, 0);
	}
	
	// imageview的宽度不变, 高度按照bitmap的宽高比例进行缩放
	// FIXME: 2017/12/5
	public void displayImageWithScale(final String url, final ImageView imageView) {
		imageView.setTag(url);
		Bitmap bitmap = mMemoryCache.get(url);
		if (bitmap != null) {
			changeLayoutParamsAndDisplayImage(imageView, bitmap);
			return;
		}
		Runnable loadBitmapRunnable = new Runnable() {
			@Override
			public void run() {
				// 从本地或者网络获取图片, 在子线程中进行
				final Bitmap bitmap = mDiskCache.get(url, 0, 0);
				if (bitmap != null) {
					// 添加到内存缓存中
					mMemoryCache.put(url, bitmap);
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							// 判断是否数据错乱, 因为加载图片的过程过程是异步的, 哪些图片先下载好是不一定的
							// 有可能一张图片先下了, 但是另一张图片后下载的却比它下载的更快
							String uri =(String)imageView.getTag();
							if (TextUtils.equals(url, uri)) {
								changeLayoutParamsAndDisplayImage(imageView, bitmap);
							} else {
								Log.w(TAG, "The url associated with imageView has changed");
							}
						}
					});
				}
				
			}
		};
		mThreadPoolExecutor.execute(loadBitmapRunnable);
	}
	
	public void changeLayoutParamsAndDisplayImage(ImageView imageView, Bitmap bitmap) {
		// 当imageView还没有测量完成的时候, imageViewHeight计算出来是0, 所以会出问题
		int imageViewHeight = (int) (bitmap.getHeight() * 1.0 * imageView.getWidth() / bitmap.getWidth());
		ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
		layoutParams.height = imageViewHeight;
		imageView.setLayoutParams(layoutParams);
		imageView.setImageBitmap(bitmap);
	}
	
	
	public void displayImage(final String url, final ImageView imageView, final int reqWidth, final int reqHeight) {
		imageView.setTag(url);
		Bitmap bitmap = mMemoryCache.get(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			return;
		}
		Runnable loadBitmapRunnable = new Runnable() {
			@Override
			public void run() {
				// 从本地或者网络获取图片, 在子线程中进行
				final Bitmap bitmap = mDiskCache.get(url, reqWidth, reqHeight);
				if (bitmap == null) {
					return;
				}
				// 添加到内存缓存中
				mMemoryCache.put(url, bitmap);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						// 判断是否数据错乱, 因为加载图片的过程过程是异步的, 哪些图片先下载好是不一定的
						// 有可能一张图片先下了, 但是另一张图片后下载的却比它下载的更快
						String uri =(String)imageView.getTag();
						if (TextUtils.equals(url, uri)) {
							imageView.setImageBitmap(bitmap);
						} else {
							Log.w(TAG, "The url associated with imageView has changed");
						}
					}
				});
			}
		};
		mThreadPoolExecutor.execute(loadBitmapRunnable);
	}
	
	
}
