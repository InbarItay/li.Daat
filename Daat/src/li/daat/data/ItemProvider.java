package li.daat.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class ItemProvider extends ContentProvider{

	
	private static final int ITEM_WITH_QUESTION = 100;
    private static final int ITEM_WITH_TYPE = 101;
    private static final int ITEM_WITH_USER_NAME = 102;
    private static final int ITEM_WITH_ID = 103;
    private static final int ITEM = 300;
	
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    
    private static UriMatcher buildUriMatcher() {

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, DataContract.PATH_ITEM + "/" + DataContract.ItemEntry.ItemColumns.COLUMN_QUESTION_TITLE + "/*", ITEM_WITH_QUESTION);
        matcher.addURI(authority, DataContract.PATH_ITEM + "/" + DataContract.ItemEntry.ItemColumns.COLUMN_TYPE + "/#", ITEM_WITH_TYPE);
        matcher.addURI(authority, DataContract.PATH_ITEM + "/" + DataContract.ItemEntry.ItemColumns.COLUMN_USER_NAME + "/*", ITEM_WITH_USER_NAME);
        matcher.addURI(authority, DataContract.PATH_ITEM + "/" + DataContract.ItemEntry.ItemColumns.COLUMN_ID + "/*", ITEM_WITH_ID);
        matcher.addURI(authority, DataContract.PATH_ITEM , ITEM);
        
        return matcher;
    }
    
	ItemDb mOpenHelper;
	@Override
	public boolean onCreate() {
		mOpenHelper = new ItemDb(getContext());
		return true;
	}

	@Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
        	case ITEM_WITH_USER_NAME: {
        		retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.ItemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "item/*/*"
            case ITEM_WITH_ID:
            {
            	retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.ItemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "item/*"
            case ITEM_WITH_TYPE: {
            	retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.ItemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "item"
            case ITEM_WITH_QUESTION: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.ItemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case ITEM:
            	retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.ItemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            	break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

	@Override
	public String getType(Uri uri) {
		// Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ITEM_WITH_ID:
                return DataContract.ItemEntry.CONTENT_ITEM_TYPE;
            case ITEM_WITH_TYPE:
                return DataContract.ItemEntry.CONTENT_DIR_TYPE;
            case ITEM_WITH_USER_NAME:
            	return DataContract.ItemEntry.CONTENT_DIR_TYPE;
            case ITEM_WITH_QUESTION:
                return DataContract.ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase writeDb = mOpenHelper.getWritableDatabase();
		Uri returnUri;
		int match = sUriMatcher.match(uri);
		
		switch (match) {
        	case ITEM:
        		long id = writeDb.insert(DataContract.ItemEntry.TABLE_NAME, null, values);
        		if (id!=-1) {
        			returnUri = DataContract.ItemEntry.buildItemWithId(String.valueOf(id));
        		}else {
        			return null;
        		}
        		break;
        	default:
        		return null;
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return returnUri;
	}
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		SQLiteDatabase writeDb = mOpenHelper.getWritableDatabase();
		int count = 0;
		switch (sUriMatcher.match(uri)) {
        	case ITEM:
        		try {
        			writeDb.beginTransaction();
	        		for(ContentValues value : values) {
	        			
		        		long id = writeDb.insert(DataContract.ItemEntry.TABLE_NAME, null, value);
		        		if (id!=-1) {
		        			count++;
		        		}else {
		        			writeDb.endTransaction();
		        			return 0;
		        		}
	        		}
	        		writeDb.setTransactionSuccessful();
	        		getContext().getContentResolver().notifyChange(uri, null);
        		} catch(Exception e) {
        			Log.d(getClass().getSimpleName(),"couldn't bulk insert");
        			count = 0;
        		} finally {
        			writeDb.endTransaction();
        		}
        		break;
        	default:
        		return super.bulkInsert(uri, values);
		}
		
		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

}
