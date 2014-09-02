package li.daat.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import li.daat.Item;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class DownloadImagesService extends Service{

	ExecutorService mThreadPool;
	private static final int MAX_THREADS = 4;
	public static final String ITEM_KEY = "itemKey";
	public static final String MESSENGER_KEY = "messengerKey";
	private static final String TAG = DownloadImagesService.class.getSimpleName();
	
	// Binder given to clients
//    private final IBinder mBinder = new MyBinder();
	
	private int mLastStartId;
	
	public static Intent makeIntent(Item item, Context context, Handler handler) {
		Intent intent = new Intent(context, DownloadImagesService.class);
		intent.putExtra(ITEM_KEY, item);
		intent.putExtra(MESSENGER_KEY, new Messenger(handler));
		return intent;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mLastStartId = startId;
		Bundle bundle = intent.getExtras();
		
		if (!mThreadPool.isShutdown() && bundle != null && bundle.containsKey(ITEM_KEY) && bundle.containsKey(MESSENGER_KEY)) {
			Item item = (Item) bundle.get(ITEM_KEY);
			Messenger messenger = (Messenger) bundle.get(MESSENGER_KEY);
			Runnable runaable = new DownloadRunnable(item,messenger);
			mThreadPool.execute(runaable);
		}else {
			Log.e(TAG, "couldn't download: " + String.valueOf(startId));
		}
		
		return START_REDELIVER_INTENT;
	}
	
	@Override
	public void onCreate() {
		mThreadPool = Executors.newFixedThreadPool(MAX_THREADS);

		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mThreadPool.shutdown();
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
//		return mBinder;
		return null;
	}
	
	private static class DownloadRunnable implements Runnable{
		
		private final Item mItem;
		private final Messenger mMessenger;
		
		public DownloadRunnable(Item item, Messenger messenger) {
			mItem = item;
			mMessenger = messenger;
		}
		
		@Override
		public void run() {
			boolean downloadSuccess = downloadFile();
			if (downloadSuccess) {
				sendItem();
			}
		}
		
		private boolean downloadFile() {
			File file = new File(DownloadImgesUtil.getImagePath(mItem.mUser));
			try {
	            
	            if(file.exists())
	            	return true;
	            
	            file.createNewFile();
	            URL url = new URL(mItem.mUserPic);
	            final InputStream in = (InputStream) url.getContent();
	            final OutputStream out = new FileOutputStream(file);

	            final int BUFFER_LENGTH = 1024;
		        final byte[] buffer = new byte[BUFFER_LENGTH];
		        int read = 0;

		        while ((read = in.read(buffer)) != -1) {
		            out.write(buffer, 0, read);		
		        }
	            in.close();
	            out.close();
	
	            return true;
	            
	        } catch (Exception e) {
		        Log.e(TAG, "Exception while downloading. Returning null.");
		        Log.e(TAG, e.toString());
		        file.delete();
		        e.printStackTrace();
		        return false;
	        }
		}
		
		private void sendItem () {
			Message msg = Message.obtain();
			Bundle data = new Bundle();
			data.putParcelable(ITEM_KEY, mItem);
			msg.setData(data);
			try {
				mMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
	}
	
//	public class MyBinder extends Binder{
//		DownloadImagesService getService() {
//            // Return this instance of LocalService so clients can call public methods
//            return DownloadImagesService.this;
//        }
//		
//	}
}
