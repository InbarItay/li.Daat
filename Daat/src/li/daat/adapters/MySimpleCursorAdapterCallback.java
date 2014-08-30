package li.daat.adapters;

import java.lang.ref.WeakReference;

import li.daat.data.DataContract;
import li.daat.data.DataContract.ItemEntry.ItemColumns;
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
	
	public MySimpleCursorAdapterCallback(SimpleCursorAdapter cursorAdapter, Context context) {
		super();
		mSimpleCursorAdapter = new WeakReference<SimpleCursorAdapter>(cursorAdapter);
		mContext = new WeakReference<Context>(context);
	}
	
	
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return new CursorLoader(mContext.get(), DataContract.ItemEntry.buildItem(),ItemColumns.VALUES_STR, null, null, null);
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
