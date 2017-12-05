package me.mundane.simpleimageloader;

import java.io.OutputStream;

/**
 * Created by mundane on 2017/12/3 下午11:17
 */

public interface ImageFetcher {
	boolean downloadSuccess(String urlString, OutputStream outputStream);
}
