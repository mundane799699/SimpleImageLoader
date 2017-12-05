package me.mundane.simpleimageloader;

import android.os.Looper;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import me.mundane.simpleimageloader.utils.CloseUtil;


/**
 * Created by mundane on 2017/11/29 下午11:39
 */

public class UrlConnectionImageFetcher implements ImageFetcher{
	private final String TAG = "ImageFetcher";
	
	@Override
	public boolean downloadSuccess(String urlString, OutputStream outputStream) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			throw new RuntimeException("Do not load Bitmap in main thread.");
		}
		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		
		try {
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			in = new BufferedInputStream(urlConnection.getInputStream());
			out = new BufferedOutputStream(outputStream);
			
			int len;
			byte[] buffer = new byte[8 * 1024];
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			return true;
		} catch (final Exception e) {
			Log.e(TAG, "Error in downloadBitmap - " + e);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			CloseUtil.CloseQuietly(in);
			CloseUtil.CloseQuietly(out);
		}
		return false;
	}
	
}
