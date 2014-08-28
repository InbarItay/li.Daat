package li.daat.data;

import li.daat.data.DataContract.ItemEntry;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemDb extends SQLiteOpenHelper{

	public static final int DB_VERSION = 1;
	public static final String DATABASE_NAME = "item.db";
	
	public ItemDb(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		final String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " (" +
                ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemEntry.COLUMN_USER_NAME + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_QUESTION + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_ANSWER + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_TYPE + " INTEGER NOT NULL, " +
                ItemEntry.COLUMN_USER_IMG + " INTEGER NOT NULL, " +
                ItemEntry.COLUMN_ANSWERS_JSON + " TEXT NOT NULL, " +
                "UNIQUE( " + ItemEntry.COLUMN_QUESTION + " , " + ItemEntry.COLUMN_USER_NAME + ") ON CONFLICT REPLACE)";
		
		db.execSQL(SQL_CREATE_ITEM_TABLE);		
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME);
        onCreate(db);
	}

}
