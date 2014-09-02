package li.daat.download;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;

public class ImagesCachingList {

	private static final int STARTING_SIZE = 0;
	private final long mMaxCachingSize;

	private static ImagesCachingList mInstance;

	private long mCurrentSize = STARTING_SIZE; 
	private Map<String, Bitmap> cachingList = java.util.Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(16,0.75f,true));

	public static ImagesCachingList getInstance() {
		if(mInstance == null) {
			synchronized(ImagesCachingList.class) {
				if(mInstance == null) {
					mInstance = new ImagesCachingList();
				}
			}
		}

		return mInstance;
	}

	private ImagesCachingList() {
		mMaxCachingSize = Runtime.getRuntime().maxMemory()/4;
	}

	public Bitmap get(String userName) {
		if(!cachingList.containsKey(userName))
			return null;

		return cachingList.get(userName);
	}

	public void put(String userName, Bitmap bitmap) {
		if(cachingList.containsKey(userName))
			mCurrentSize -= getSize(cachingList.get(userName));
		cachingList.put(userName, bitmap);
		mCurrentSize += getSize(bitmap);
		checkSize();
	}

	private void checkSize() {
		if(mCurrentSize > mMaxCachingSize) {
			for (Entry<String, Bitmap> entry : cachingList.entrySet()) {
				mCurrentSize -= getSize(entry.getValue());
				cachingList.remove(entry.getKey());

				if(mCurrentSize <= mMaxCachingSize)
					break;
			}
		}
	}

	public void deleteCache() {
		if(cachingList != null) {
			cachingList.clear();
		}
		mCurrentSize = STARTING_SIZE;
	}

	long getSize(Bitmap bitmapCached) {
		if(bitmapCached == null)
			return 0;
		return bitmapCached.getHeight() * bitmapCached.getRowBytes();
	}
}