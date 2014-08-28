package li.daat;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyListAdapter extends ArrayAdapter<Item> {
	
	
	private LayoutInflater mInflater;
	ArrayList<Item> mObjects;
	
	public MyListAdapter(Context context, int resource, ArrayList<Item> objects) {
		super(context, resource, objects);
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mObjects = objects;
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		int type = getItemViewType(position);
		if(convertView == null) {
			switch(type){
				case MainFragment.ADDED_AN_ANSWER_TYPE_POST:
					
					convertView = mInflater.inflate(R.layout.list_view_item_question, parent, false);
					
				break;
			case MainFragment.ASKED_A_QUESTION_TYPE_POST:
					convertView = mInflater.inflate(R.layout.list_view_item_question, parent, false);
					
				break;
			}
			HolderView holderView = new HolderView();
			holderView.listItemTextHeadline = (TextView)convertView.findViewById(R.id.ListItemHeadline);
			holderView.listItemUserName = (TextView)convertView.findViewById(R.id.ListItemUserName);
			holderView.listItemTextContent = (TextView)convertView.findViewById(R.id.ListItemText);
			holderView.listItemUserImage = (ImageView)convertView.findViewById(R.id.ListItemUserImg);
			convertView.setTag(holderView);
		}
		
		HolderView holderView = (HolderView)convertView.getTag();
		Item item = mObjects.get(position);
		String headLineStr;
		switch(type) {
			case MainFragment.ADDED_AN_ANSWER_TYPE_POST:
				headLineStr = MainFragment.ADDED_AN_ANSWER_TYPE_HEADLINE;
			break;
			case MainFragment.ASKED_A_QUESTION_TYPE_POST:
				headLineStr = MainFragment.ASKED_A_QUESTION_TYPE_HEADLINE;
			break;
			default:
				headLineStr = MainFragment.ASKED_A_QUESTION_TYPE_HEADLINE;
		}
		
		holderView.listItemTextHeadline.setText(headLineStr);
		String text = "*";
		try {
			text = new String(item.mMessage.getBytes(), "UTF-8");
		}catch(Exception e) {
			
		}
		holderView.listItemTextContent.setText(Html.fromHtml(text));
		holderView.listItemUserName.setText(item.mUser);
		//TODO:holderView.listItemUserImage download + set Image
		return convertView;
	}
	
	@Override
	 public int getItemViewType(int position) {
		return mObjects.get(position).mType;
//        return position == 0 ? 0 : 1;
    }

	@Override
    public int getViewTypeCount() {
        return 2;
    }
	
	private class HolderView{
		private TextView listItemTextHeadline;
		private TextView listItemTextContent;
		private TextView listItemUserName;
		private ImageView listItemUserImage;
	}
    
}