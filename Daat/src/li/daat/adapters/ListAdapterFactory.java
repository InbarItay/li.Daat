package li.daat.adapters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import li.daat.Item;
import li.daat.ItemActivity;
import li.daat.R;
import li.daat.data.DataContract.ItemEntry;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.widget.BaseAdapter;
public class ListAdapterFactory {
	
	public static abstract class ListAdapterWrapper{
		public BaseAdapter mListAdapter;
		WeakReference<Context> mContext;
		
		public abstract void updateAdapter(List<Item> results);
		
		protected abstract Item getItem(int position);
		
		public void onItemClicked(int position) {
			Item item = getItem(position);
			Intent intent = new Intent();
			Context context = mContext.get();
			intent.setClass(context, ItemActivity.class);
			intent.putExtra(Item.KEY_ITEM_INTENT, item);
			context.startActivity(intent);
		}
	}
	
	public static enum AdapterTypes{
		LIST_ADAPTER_TYPE(),
		CURSOR_ADAPTER_TYPE();
	}
	
	private final AdapterTypes mType;
	
	public ListAdapterFactory(AdapterTypes type) {
		mType = type;
	}
	
	public ListAdapterWrapper getAdapterWrapper(Context context) {
		switch(mType) {
			case LIST_ADAPTER_TYPE:
				return new MyListAdapterWrapper(context);
				
			case CURSOR_ADAPTER_TYPE:
				return new MyCursorAdapterWrapper(context);
				
		}
		
		return null;
	}
	
	private static class MyCursorAdapterWrapper extends ListAdapterWrapper{
		
		private MyCursorAdapterCallback mCallBack;
		
		/*loader*/
		private static final int lodaerId = 0;
		
		@Override
		protected Item getItem(int position) {
			Cursor cursor = (Cursor)mListAdapter.getItem(position);
			return new Item(cursor);
			
		}
		
		public MyCursorAdapterWrapper(Context context) {
			Cursor c = context.getContentResolver().query(ItemEntry.CONTENT_URI, null, null, null, null);
			mListAdapter = new MyCursorAdapter(context, c, true);//getCursorLoaderAdapter(context);
			mContext = new WeakReference<Context>(context);
			mCallBack = new MyCursorAdapterCallback((MyCursorAdapter)mListAdapter, context);
			((FragmentActivity)context).getSupportLoaderManager().initLoader(lodaerId, null, (LoaderCallbacks<Cursor>)mCallBack);
		}
		
		@Override
		public void updateAdapter(List<Item> results) {
				ContentValues[] cvArray = getContentValues(results);
				mContext.get().getContentResolver().bulkInsert(ItemEntry.CONTENT_URI, cvArray);
			}
		
		private ContentValues[] getContentValues(List<Item>results)
		{
			ContentValues[] cvArray = new ContentValues[results.size()];
			for (int i=0; i < results.size(); i++) {
				Item result = results.get(i);
				ContentValues tempCV = new ContentValues();
				tempCV.put(ItemEntry.ItemColumns.COLUMN_USER_NAME.toString(), result.mUser);
				tempCV.put(ItemEntry.ItemColumns.COLUMN_QUESTION.toString(), result.mQuestion);
				tempCV.put(ItemEntry.ItemColumns.COLUMN_QUESTION_TITLE.toString(), result.mQuestionTitle);
				tempCV.put(ItemEntry.ItemColumns.COLUMN_ANSWER.toString(), result.mAnswer);
				tempCV.put(ItemEntry.ItemColumns.COLUMN_TYPE.toString(), result.mType);
				tempCV.put(ItemEntry.ItemColumns.COLUMN_USER_IMG.toString(), result.mUserPic);
				tempCV.put(ItemEntry.ItemColumns.COLUMN_ANSWERS_JSON.toString(), result.mAnswersJson);
				cvArray[i] = tempCV;
			}
			return cvArray;
		}
		
		
	}
	
	private static class MyListAdapterWrapper extends ListAdapterWrapper{
		
		public MyListAdapterWrapper(Context context) {
			mListAdapter = getListAdapter(context);
			mContext = new WeakReference<Context>(context);
		}
		
		private MyListAdapter getListAdapter(Context context) {
			MyListAdapter listAdapter = new MyListAdapter(context, R.layout.list_view_item_question, new ArrayList<Item>());
			return listAdapter;
		}

		@Override
		public void updateAdapter(List<Item> results) {
			((MyListAdapter)mListAdapter).clear();
			((MyListAdapter)mListAdapter).addAll(results);
		}

		@Override
		protected Item getItem(int position) {
			return (Item)mListAdapter.getItem(position);
			
		}
	}
}
