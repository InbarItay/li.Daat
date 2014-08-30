package li.daat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ItemActivity extends ActionBarActivity{
	TextView mHeadline;
	TextView mContent;
	Item mItem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item);
		mHeadline = (TextView)findViewById(R.id.ActivityItemHeadLine);
		mContent = (TextView)findViewById(R.id.ActivityItemContentText);
		if(savedInstanceState == null) {
			Intent intent = getIntent();
			if (intent.hasExtra(Item.KEY_ITEM_INTENT)) {
				mItem = intent.getParcelableExtra(Item.KEY_ITEM_INTENT);
			}
		}else {
			mItem = savedInstanceState.getParcelable(Item.KEY_ITEM_BUNDLE);
		}
		
		mContent.setText(Html.fromHtml(mItem.mAnswer));
		mHeadline.setText(mItem.mQuestionTitle);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(Item.KEY_ITEM_BUNDLE, mItem);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.activity_item_menu, menu);
		MenuItem menuItem = menu.findItem(R.id.action_share);
		ShareActionProvider ap = (ShareActionProvider)MenuItemCompat.getActionProvider(menuItem);
		if (ap != null) {
			ap.setShareIntent(createShareIntent());
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		return super.onOptionsItemSelected(item);
	}
	
	private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(mItem.mQuestion));
        return shareIntent;
    }
	
	
}
