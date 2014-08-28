package li.daat;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {

	public final int mType;
	public final String mUser;
	public final String mUserPic;
	public final String mMessage;
	public static final String KEY_ITEM_INTENT = "keyItemIntent";
	public static final String KEY_ITEM_BUNDLE = "keyItemBundle";
	public Item(int type, String user, String userPic, String message) {
		mType = type;
		mUser = user;
		mUserPic = userPic;
		mMessage = message;
	}
	
	
	
	
	public int describeContents() {
        return 0;
    }

    /** save object in parcel */
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mType);
        String[] strArr = new String[3];
        strArr[0] = mUser;
        strArr[1] = mMessage;
        strArr[2] = mUserPic;
        out.writeStringArray(strArr);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    /** recreate object from parcel */
    private Item(Parcel in) {
        mType = in.readInt();
        String[] strArr = new String[3];
        in.readStringArray(strArr);
        mUser = strArr[0];
        mMessage = strArr[1];
        mUserPic = strArr[2];
    }
}
