package li.daat.adapters;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import li.daat.Item;
import li.daat.MainFragment;
import li.daat.R;
import li.daat.data.DataContract.ItemEntry.ItemColumns;
import li.daat.download.DownloadImagesService;
import li.daat.download.DownloadImgesUtil;
import li.daat.download.ImagesCachingList;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyCursorAdapter extends CursorAdapter{

	private final static String TAG = MyCursorAdapter.class.getSimpleName();
	private final Map<String, List<WeakReference<ImageView>>> mMapUserImagesToListOfView = new HashMap<String, List<WeakReference<ImageView>>>();
	private final List<String> mUsersCurrentlyDownloading = new LinkedList<String>();
	private static final int MAX_CHARS_ALLOWED = 200;
	private Handler mImagesHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			if(bundle == null || !bundle.containsKey(DownloadImagesService.ITEM_KEY)) {
				Log.e(TAG, "couldn't find item in service response");
				return;
			}
			
			Item item = (Item)bundle.get(DownloadImagesService.ITEM_KEY);
			String userName = item.mUser;
			List<WeakReference<ImageView>> imageViewList = mMapUserImagesToListOfView.get(userName);
			if(imageViewList == null || imageViewList.size() == 0) {
				Log.e(TAG, "couldn't find image view for user name: " + userName);
				return;
			}
			
			File file = DownloadImgesUtil.getImageFile(userName);
			if(file== null) {
				Log.e(this.getClass().getSimpleName(), "null file for user name: " + userName);
				return;
			}
			Bitmap imageBitmap = BitmapFactory.decodeFile(file.getPath());
			if(imageBitmap == null) {
				Log.e(this.getClass().getSimpleName(), "null bitmap for user name: " + userName);
				file.delete();
				return;
			}
			ImagesCachingList.getInstance().put(userName, imageBitmap);
			for(WeakReference<ImageView> viewRef : imageViewList) {
				ImageView view = viewRef.get();
				if(view != null) {
					((ImageView)view).setImageBitmap(imageBitmap);
				}
			}
			
		};
	};
	
	public MyCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout view;
		HolderView holderView = new HolderView();
		holderView.viewType = Item.getType(cursor);
		switch(holderView.viewType) {
			case MainFragment.ADDED_AN_ANSWER_TYPE_POST:
				view = (LinearLayout)inflator.inflate(R.layout.list_view_item_answer, parent, false);
				holderView.userName = (TextView)view.findViewById(R.id.ListItemUserNameAnswer);
				holderView.userImage = (ImageView)view.findViewById(R.id.ListItemUserImgAnswer);
				holderView.itemHeadLine = (TextView)view.findViewById(R.id.ListItemHeadlineAnswer);
				holderView.questionTitle = (TextView)view.findViewById(R.id.ListItemTitleAnswer);
				holderView.itemAnswer = (TextView)view.findViewById(R.id.ListItemTextAnswer);
				break;
			case MainFragment.ASKED_A_QUESTION_TYPE_POST:
			default:
				view = (LinearLayout)inflator.inflate(R.layout.list_view_item_question, parent, false);
				holderView.userName = (TextView)view.findViewById(R.id.ListItemUserNameQuestion);
				holderView.userImage = (ImageView)view.findViewById(R.id.ListItemUserImgQuestion);
				holderView.itemHeadLine = (TextView)view.findViewById(R.id.ListItemHeadlineQuestion);
				holderView.questionTitle = (TextView)view.findViewById(R.id.ListItemTitleQuestion);
				break;
		}
		
		view.setTag(holderView);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		HolderView holderView = (HolderView)view.getTag();
		Item item = new Item(cursor);
		String headLineStr;
		switch(item.mType) {
			case MainFragment.ADDED_AN_ANSWER_TYPE_POST:
				headLineStr = MainFragment.ADDED_AN_ANSWER_TYPE_HEADLINE;
			break;
			case MainFragment.ASKED_A_QUESTION_TYPE_POST:
			default:
				headLineStr = MainFragment.ASKED_A_QUESTION_TYPE_HEADLINE;
			break;
		}
		
		holderView.itemHeadLine.setText(headLineStr);
		holderView.userName.setText(item.mUser);
		holderView.questionTitle.setText(item.mQuestionTitle);
		if (item.mAnswer != null && item.mAnswer.length() > 1 && holderView.itemAnswer != null) {
			int end = MAX_CHARS_ALLOWED;
			String suffix = "..";
			if (item.mAnswer.length() <= MAX_CHARS_ALLOWED) {
				end = item.mAnswer.length();
				suffix = "";
			}
			 
			String str = Html.fromHtml(item.mAnswer.substring(0, end)) + suffix;
			holderView.itemAnswer.setText(str);
		}
		holderView.userName.setText(item.mUser);
		addImageViewToList(holderView.userImage, item.mUser);
		setImage(holderView.userImage, item);
	}
	
	@Override
	public int getItemViewType(int position) {
		Cursor cursor = (Cursor)getItem(position);
		int type = Item.getType(cursor);
		return type;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	private void setImage(ImageView imageView, Item item) {
		String userName = item.mUser;
		Bitmap userBitmapImage = ImagesCachingList.getInstance().get(userName);
		if(userBitmapImage != null) {
			imageView.setImageBitmap(userBitmapImage);
		}else {
			File imageFile = DownloadImgesUtil.getImageFile(userName);
			if(imageFile != null) {
				userBitmapImage = BitmapFactory.decodeFile(imageFile.getPath());
				if(userBitmapImage == null) {
					Log.e(TAG, "file is null for user name: "+ userName);
					imageFile.delete();
					downloadImage(item);
					return;
				}
				ImagesCachingList.getInstance().put(userName, userBitmapImage);
				imageView.setImageBitmap(userBitmapImage);
			} else {
				downloadImage(item);
			}
		}
	}
	
	private void downloadImage(Item item) {
		String userName = item.mUser;
		if (!mUsersCurrentlyDownloading.contains(userName)) {
			mUsersCurrentlyDownloading.add(userName);//runs only on the main thread
			mContext.startService(DownloadImagesService.makeIntent(item, mContext, mImagesHandler));
		}
	}
	
	public List<WeakReference<ImageView>> getImageViewList(String userName){
		return new LinkedList<WeakReference<ImageView>> (mMapUserImagesToListOfView.get(userName));
	}

	private void addImageViewToList(ImageView imageView, String userName) {
		List<WeakReference<ImageView>> list = mMapUserImagesToListOfView.get(userName);
		if (list == null)
		{
			list = new LinkedList<WeakReference<ImageView>>();
			list.add(new WeakReference<ImageView>(imageView));
			mMapUserImagesToListOfView.put(userName, list);
		}else {
			WeakReference<ImageView> temp = new WeakReference<ImageView>(imageView);
			if (!list.contains(temp)) {//assuming WeakReference's equals() is implemented properly
				list.add(temp);
			}
		}
	}
	
	class HolderView{
		ImageView userImage;//ListItemUserImg
		TextView userName;//ListItemUserName
		TextView itemHeadLine;//ListItemHeadline
		TextView questionTitle;//ListItemTitleQuestion
		TextView itemAnswer;//ListItemTextAnswer
		int viewType;
	}

}
