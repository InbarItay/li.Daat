package li.daat.data;
import android.content.ContentResolver;
import android.content.ContentUris;
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
    
	public static final class ItemEntry implements BaseColumns{
		/*Columns*/
		public static final String TABLE_NAME ="table_item";
		public static final String COLUMN_USER_NAME ="column_user_name";
		public static final String COLUMN_USER_IMG ="column_user_img";
		public static final String COLUMN_QUESTION ="column_question_text";
		public static final String COLUMN_ANSWER ="column_answer_text";
		public static final String COLUMN_TYPE ="column_type";
		public static final String COLUMN_ANSWERS_JSON ="column_answers_json";
		
		/*Content Provider*/
		// base for all queries
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEM).build();
		
		//mime types
		public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;
        
        //supported urls
        public static Uri buildItemWithUserName(String userName) {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_USER_NAME).appendPath(userName).build();
        }
        
        public static Uri buildItemWithId(String userId) {
            return CONTENT_URI.buildUpon().appendPath(_ID).appendPath(userId).build();
        }
        
        public static Uri buildItem() {
            return CONTENT_URI;
        }

        public static Uri buildItemWithType(int type) {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_TYPE).appendPath(String.valueOf(type)).build();
        }

        public static Uri buildItemWithQuestion(String question) {
        	return CONTENT_URI.buildUpon().appendPath(COLUMN_QUESTION).appendPath(question).build();
        }

//        public static String getLocationSettingFromUri(Uri uri) {
//            return uri.getPathSegments().get(1);
//        }
//
//        public static String getDateFromUri(Uri uri) {
//            return uri.getPathSegments().get(2);
//        }
//
//        public static String getStartDateFromUri(Uri uri) {
//            return uri.getQueryParameter(COLUMN_DATETEXT);
//        }
        
        
		
		
	}
}
