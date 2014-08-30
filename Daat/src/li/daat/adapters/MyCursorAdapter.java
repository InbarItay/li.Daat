package li.daat.adapters;

import li.daat.Item;
import li.daat.MainFragment;
import li.daat.R;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyCursorAdapter extends CursorAdapter{

	private static final int MAX_CHARS_ALLOWED = 200;
	class HolderView{
		ImageView userImage;//ListItemUserImg
		TextView userName;//ListItemUserName
		TextView itemHeadLine;//ListItemHeadline
		TextView questionTitle;//ListItemTitleQuestion
		TextView itemAnswer;//ListItemTextAnswer
		int type;
	}
	public MyCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout view;
		HolderView holderView = new HolderView();
		holderView.type = Item.getType(cursor);
		
		switch(holderView.type) {
			case MainFragment.ADDED_AN_ANSWER_TYPE_POST:
				view = (LinearLayout)inflator.inflate(R.layout.list_view_item_answer, parent, false);
				holderView.userName = (TextView)view.findViewById(R.id.ListItemUserNameAnswer);
				holderView.userImage = (ImageView)view.findViewById(R.id.ListItemUserImgAnswer);
				holderView.itemHeadLine = (TextView)view.findViewById(R.id.ListItemHeadlineAnswer);
				holderView.questionTitle = (TextView)view.findViewById(R.id.ListItemTitleAnswer);
				holderView.itemAnswer = (TextView)view.findViewById(R.id.ListItemTextAnswer);
				break;
			case MainFragment.ASKED_A_QUESTION_TYPE_POST:
			default:
				view = (LinearLayout)inflator.inflate(R.layout.list_view_item_question, parent, false);
				holderView.userName = (TextView)view.findViewById(R.id.ListItemUserNameQuestion);
				holderView.userImage = (ImageView)view.findViewById(R.id.ListItemUserImgQuestion);
				holderView.itemHeadLine = (TextView)view.findViewById(R.id.ListItemHeadlineQuestion);
				holderView.questionTitle = (TextView)view.findViewById(R.id.ListItemTitleQuestion);
				break;
		}
		
		view.setTag(holderView);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		HolderView holderView = (HolderView)view.getTag();
		Item item = new Item(cursor);
		String headLineStr;
		switch(item.mType) {
			case MainFragment.ADDED_AN_ANSWER_TYPE_POST:
				headLineStr = MainFragment.ADDED_AN_ANSWER_TYPE_HEADLINE;
			break;
			case MainFragment.ASKED_A_QUESTION_TYPE_POST:
			default:
				headLineStr = MainFragment.ASKED_A_QUESTION_TYPE_HEADLINE;
			break;
		}
		holderView.itemHeadLine.setText(headLineStr);
		holderView.userName.setText(item.mUser);
		holderView.questionTitle.setText(item.mQuestionTitle);
		if (item.mAnswer != null && item.mAnswer.length() > 1 && holderView.itemAnswer != null) {
			int end = MAX_CHARS_ALLOWED;
			String suffix = "..";
			if (item.mAnswer.length() <= MAX_CHARS_ALLOWED) {
				end = item.mAnswer.length();
				suffix = "";
			}
			 
			String str = Html.fromHtml(item.mAnswer.substring(0, end)) + suffix;
			holderView.itemAnswer.setText(str);
		}
		holderView.userName.setText(item.mUser);
	}
	
	@Override
	public int getItemViewType(int position) {
		Cursor cursor = (Cursor)getItem(position);
		int type = Item.getType(cursor);
		return type;
	}
	
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

}
