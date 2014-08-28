package li.daat;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {

	private static final int NUM_OF_FIELDS = 5;
	public final int mType;
	public final String mUser;
	public final String mUserPic;
	public final String mQuestion;
	public final String mAnswer;
	public final String mAnswersJson;
	public static final String KEY_ITEM_INTENT = "keyItemIntent";
	public static final String KEY_ITEM_BUNDLE = "keyItemBundle";
	
	public Item(int type, String user, String userPic, String question, String answer, String answersJson) {
		mType = type;
		mUser = user != null ? user : "";
		mUserPic = userPic != null ? userPic : "";
		mQuestion = question != null ? question : "";
		mAnswer = answer != null ? answer : "";
		mAnswersJson = answersJson != null ? answersJson : "";
	}
	
	public int describeContents() {
        return 0;
    }

    /** save object in parcel */
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mType);
        String[] strArr = new String[NUM_OF_FIELDS];
        strArr[0] = mUser;
        strArr[1] = mQuestion;
        strArr[2] = mUserPic;
        strArr[3] = mAnswer;
        strArr[4] = mAnswersJson;
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
        String[] strArr = new String[NUM_OF_FIELDS];
        in.readStringArray(strArr);
        mUser = strArr[0];
        mQuestion = strArr[1];
        mUserPic = strArr[2];
        mAnswer = strArr[3];
        mAnswersJson = strArr[4];
    }
}
