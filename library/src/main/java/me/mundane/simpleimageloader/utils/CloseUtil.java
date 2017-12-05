package me.mundane.simpleimageloader.utils;

import java.io.Closeable;

/**
 * Created by mundane on 2017/11/27 下午11:22
 */

public class CloseUtil {
	private CloseUtil() {
	}
	
	public static void CloseQuietly(Closeable closeable) {
		if (closeable == null) {
			return;
		}
		try {
			closeable.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
