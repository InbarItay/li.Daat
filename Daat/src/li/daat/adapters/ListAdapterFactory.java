package li.daat.adapters;

import java.util.ArrayList;
import java.util.List;

import li.daat.Item;
import li.daat.R;
import li.daat.data.DataContract;
import li.daat.data.DataContract.ItemEntry;
import android.content.ContentValues;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.BaseAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.app.Fragment.*;
import android.content.Context;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
public class ListAdapterFactory {
	
	public static abstract class ListAdapterWrapper{
		public BaseAdapter mListAdapter;
		Context mContext;
		
		public abstract void updateAdapter(List<Item> results);
	}
	
	public static enum AdapterTypes{
		LIST_ADAPTER_TYPE(),
		SIMPLE_CURSOR_ADAPTER_TYPE();
	}
	
	private final AdapterTypes mType;
	public ListAdapterFactory(AdapterTypes type) {
		mType = type;
	}
	
	public ListAdapterWrapper getAdapterWrapper(Context context) {
		switch(mType) {
			case LIST_ADAPTER_TYPE:
				return new SimpleCursorLoaderWrapper(context);
			case SIMPLE_CURSOR_ADAPTER_TYPE:
				return new MyListAdapterWrapper(context);
		}
		return null;
	}
	
	private static class SimpleCursorLoaderWrapper extends ListAdapterWrapper{
		
		private MySimpleCursorAdapterCallback mCallBack;
		
		/*loader*/
		private static final int lodaerId = 0;
		
		private static final String[] CURSOR_COLUMNS;
		static {
			CURSOR_COLUMNS = new String[ItemEntry.ItemColumns.VALUES.length];
			int i = 0;
			for (ItemEntry.ItemColumns col: ItemEntry.ItemColumns.VALUES) {
				CURSOR_COLUMNS[i] = col.toString();
				i++;
			}
		}
		
		public SimpleCursorLoaderWrapper(Context context) {
			mListAdapter = getCursorLoaderAdapter(context);
			mContext = context;
			mCallBack = new MySimpleCursorAdapterCallback((SimpleCursorAdapter)mListAdapter, context, CURSOR_COLUMNS);
			((Activity)context).getLoaderManager().initLoader(lodaerId, null, (LoaderCallbacks<Cursor>)mCallBack);
		}
		
		@Override
		public void updateAdapter(List<Item> results) {
				ContentValues[] cvArray = getContentValues(results);
				mContext.getContentResolver().bulkInsert(ItemEntry.CONTENT_URI, cvArray);
			}
		
		private SimpleCursorAdapter getCursorLoaderAdapter(Context context) {
			SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
	                context,
	                R.layout.list_view_item_question,
	                null,
	                // the column names to use to fill the textviews
	                new String[]{
	                	DataContract.ItemEntry.ItemColumns.COLUMN_USER_NAME.toString(),
	            		DataContract.ItemEntry.ItemColumns.COLUMN_QUESTION.toString(),
	            		DataContract.ItemEntry.ItemColumns.COLUMN_ANSWER.toString()
	                },
	                // the textviews to fill with the data pulled from the columns above
	                new int[]{
						R.id.ListItemUserName,
						R.id.ListItemTitleQuestion,
						R.id.ListItemTextAnswer
	                },
	                0);
			return cursorAdapter;
		}
		
		private ContentValues[] getContentValues(List<Item>results)
		{
			ContentValues[] cvArray = new ContentValues[results.size()];
			for (int i=0; i < results.size(); i++) {
				Item result = results.get(i);
				ContentValues tempCV = new ContentValues();
				tempCV.put(ItemEntry.ItemColumns.COLUMN_USER_NAME.toString(), result.mUser);
				tempCV.put(ItemEntry.ItemColumns.COLUMN_QUESTION.toString(), result.mQuestion);
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
			mContext = context;
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
	}
}
