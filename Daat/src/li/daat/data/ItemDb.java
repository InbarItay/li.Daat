package li.daat.data;

import li.daat.data.DataContract.ItemEntry;
import li.daat.data.DataContract.ItemEntry.ItemColumns;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemDb extends SQLiteOpenHelper{

	public static final int DB_VERSION = 2;
	public static final String DATABASE_NAME = "itemzzz.db";
	
	public ItemDb(Context context) {
		
		super(context, DATABASE_NAME, null, DB_VERSION);
		context.deleteDatabase(DATABASE_NAME);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		final String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " (" +
                ItemEntry.ItemColumns.COLUMN_ID.toString() + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemEntry.ItemColumns.COLUMN_USER_NAME.toString() + " TEXT NOT NULL, " +
                ItemEntry.ItemColumns.COLUMN_QUESTION.toString() + " TEXT NOT NULL, " +
                ItemEntry.ItemColumns.COLUMN_QUESTION_TITLE.toString() + " TEXT NOT NULL, " +
                ItemEntry.ItemColumns.COLUMN_ANSWER.toString() + " TEXT NOT NULL, " +
                ItemEntry.ItemColumns.COLUMN_TYPE.toString() + " INTEGER NOT NULL, " +
                ItemEntry.ItemColumns.COLUMN_USER_IMG.toString() + " INTEGER NOT NULL, " +
                ItemEntry.ItemColumns.COLUMN_ANSWERS_JSON.toString() + " TEXT NOT NULL, " +
                "UNIQUE( " + ItemEntry.ItemColumns.COLUMN_QUESTION_TITLE.toString() + " , " + ItemEntry.ItemColumns.COLUMN_USER_NAME.toString() + ") ON CONFLICT REPLACE)";
		
		db.execSQL(SQL_CREATE_ITEM_TABLE);		
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME);
        onCreate(db);
	}

}
