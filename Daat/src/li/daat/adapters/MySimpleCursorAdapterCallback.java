package li.daat.adapters;

import java.lang.ref.WeakReference;

import li.daat.data.DataContract;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

public class MySimpleCursorAdapterCallback implements LoaderCallbacks<Cursor>{

	WeakReference<SimpleCursorAdapter> mSimpleCursorAdapter;
	WeakReference<Context> mContext;
	String[] CURSOR_COLUMNS;
	
	public MySimpleCursorAdapterCallback(SimpleCursorAdapter cursorAdapter, Context context, String[] columns) {
		super();
		mSimpleCursorAdapter = new WeakReference<SimpleCursorAdapter>(cursorAdapter);
		mContext = new WeakReference<Context>(context);
		CURSOR_COLUMNS = columns;
	}
	
	
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return new CursorLoader(mContext.get(), DataContract.ItemEntry.buildItem(),CURSOR_COLUMNS, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		mSimpleCursorAdapter.get().swapCursor(arg1);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mSimpleCursorAdapter.get().swapCursor(null);
		
	}

}
