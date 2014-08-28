package li.daat.data;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class DataContract {
	
	// The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "li.daat";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEM = "item";
    
	public static final class ItemEntry {
		
		/*Columns*/
		public static enum ItemColumns implements BaseColumns {
			COLUMN_USER_NAME("column_user_name"),
			COLUMN_USER_IMG("column_user_img"),
			COLUMN_QUESTION("column_question_text"),
			COLUMN_ANSWER("column_answer_text"),
			COLUMN_TYPE("column_type"),
			COLUMN_ANSWERS_JSON("column_answers_json"),
			COLUMN_ID(_ID);
			private String strValue;
			
			@Override
			public String toString() {
				return strValue;
			}

			ItemColumns(String strValue) {
				this.strValue = strValue;
			}

			public static final ItemColumns[] VALUES = ItemColumns.values();

			public static ItemColumns get(int value) {
				if ((value < 0) || (value >= VALUES.length)) {
					return null;
				}
				return VALUES[value];
			}
		}
			
		public static final String TABLE_NAME ="table_item";
		
		/*Content Provider*/
		// base for all queries
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEM).build();
		
		//mime types
		public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;
        
        //supported urls
        public static Uri buildItemWithUserName(String userName) {
            return CONTENT_URI.buildUpon().appendPath(ItemColumns.COLUMN_USER_NAME.toString()).appendPath(userName).build();
        }
        
        public static Uri buildItemWithId(String userId) {
            return CONTENT_URI.buildUpon().appendPath(ItemColumns.COLUMN_ID.toString()).appendPath(userId).build();
        }
        
        public static Uri buildItem() {
            return CONTENT_URI;
        }

        public static Uri buildItemWithType(int type) {
            return CONTENT_URI.buildUpon().appendPath(ItemColumns.COLUMN_TYPE.toString()).appendPath(String.valueOf(type)).build();
        }

        public static Uri buildItemWithQuestion(String question) {
        	return CONTENT_URI.buildUpon().appendPath(ItemColumns.COLUMN_QUESTION.toString()).appendPath(question).build();
        }

	}
}
