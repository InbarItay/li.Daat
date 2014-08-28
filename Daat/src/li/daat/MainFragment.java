package li.daat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import li.daat.DownloadTask.IDownloadDelegate;
import li.daat.adapters.MyListAdapter;
import li.daat.adapters.MySimpleCursorAdapterCallback;
import li.daat.data.DataContract;
import li.daat.data.DataContract.ItemEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements IDownloadDelegate{

	private ListView mListView;
	private MyListAdapter mListAdapter;
	private SimpleCursorAdapter mSimpleCursorAdapter;
	private MySimpleCursorAdapterCallback mCallback;
	
	
	/*loader*/
	private static final int lodaerId = 0;
	private static String mLocation;
	
	private static final String[] CURSOR_COLUMNS;
	static {
		CURSOR_COLUMNS = new String[ItemEntry.ItemColumns.VALUES.length];
		int i = 0;
		for (ItemEntry.ItemColumns col: ItemEntry.ItemColumns.VALUES) {
			CURSOR_COLUMNS[i] = col.toString();
			i++;
		}
	}
	
	/*
	public static final int COL_ID = 0;
	public static final int COL_USER_NAME = 1;
	public static final int COL_QUESTION = 2;
	public static final int COL_ANSWER = 3;
	public static final int COL_TYPE = 4;
	public static final int COL_USER_IMG = 5; 
	public static final int COL_ANSWERS_JSON = 6;
	*/
	
	public static final int ASKED_A_QUESTION_TYPE_POST = 0;
	public static final int ADDED_AN_ANSWER_TYPE_POST = 1;
	
	public static final String ASKED_A_QUESTION_TYPE_HEADLINE = "asked a question..";
	public static final String ADDED_AN_ANSWER_TYPE_HEADLINE = "added and answer..";
	private static final String LOG_TAG = "MAIN_FRAGMENT";
	
	public MainFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mCallback = new MySimpleCursorAdapterCallback(mSimpleCursorAdapter, getActivity(), CURSOR_COLUMNS);
		getLoaderManager().initLoader(lodaerId, null, mCallback);
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		updateData();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		mListView = (ListView)rootView.findViewById(R.id.main_list);
		setupCursorLoaderAdapter();
		return rootView;
	}
	
	private void setupCursorLoaderAdapter() {
		mSimpleCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_view_item_question,
                null,
                // the column names to use to fill the textviews
                new String[]{
                	DataContract.ItemEntry.ItemColumns.COLUMN_USER_NAME.toString(),
            		DataContract.ItemEntry.ItemColumns.COLUMN_QUESTION.toString(),
            		DataContract.ItemEntry.ItemColumns.COLUMN_ANSWER.toString()
                },
                // the textviews to fill with the data pulled from the columns above
                new int[]{
					R.id.ListItemUserName,
					R.id.ListItemTitleQuestion,
					R.id.ListItemTextAnswer
                },
                0);
		mListView.setAdapter(mSimpleCursorAdapter);
	}
	
	private void setupListAdapter() {
		mListAdapter = new MyListAdapter(getActivity(), R.layout.list_view_item_question, new ArrayList<Item>());
		mListView.setAdapter(mListAdapter);
		mListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Item item = mListAdapter.getItem(position);
				Intent intent = new Intent();
				intent.setClass(getActivity(), ItemActivity.class);
				intent.putExtra(Item.KEY_ITEM_INTENT, item);
				startActivity(intent);
				
				
			}
			
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.main_fragment_refresh) {
			updateData();
			return true;
		}else if (item.getItemId() == R.id.main_fragment_settings) {
			startActivity(new Intent(getActivity(),SettingsActivity.class));
			return true;
		}else if (item.getItemId() == R.id.main_fragment_map) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
			String str = sp.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
			Map map = sp.getAll();
			Uri uriData = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", str).build(); 
			intent.setData(uriData);
			if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
				startActivity(intent);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.main_fragment_options_menu, menu);
	}
	
	private void updateData() {
		DownloadTask dt = new DownloadTask(this);
		dt.execute((String[])null);
	}

	@Override
	public void downloadFinished(String result) {
		// TODO Auto-generated method stub
		List<Item> results;
		try {
			results = getResults(result);
		} catch (Exception e) {
			results = null;
			return;
		}
		updateSimpleCursorAdapter(results);
	} 
	
	private void updateSimpleCursorAdapter(List<Item> results) {
		ContentValues[] cvArray = getContentValues(results);
		getActivity().getContentResolver().bulkInsert(ItemEntry.CONTENT_URI, cvArray);
	}
	
	private ContentValues[] getContentValues(List<Item>results)
	{
		ContentValues[] cvArray = new ContentValues[results.size()];
		for (int i=0; i < results.size(); i++) {
			Item result = results.get(i);
			ContentValues tempCV = new ContentValues();
			tempCV.put(ItemEntry.ItemColumns.COLUMN_USER_NAME.toString(), result.mUser);
			tempCV.put(ItemEntry.ItemColumns.COLUMN_QUESTION.toString(), result.mQuestion);
			tempCV.put(ItemEntry.ItemColumns.COLUMN_ANSWER.toString(), result.mAnswer);
			tempCV.put(ItemEntry.ItemColumns.COLUMN_TYPE.toString(), result.mType);
			tempCV.put(ItemEntry.ItemColumns.COLUMN_USER_IMG.toString(), result.mUserPic);
			tempCV.put(ItemEntry.ItemColumns.COLUMN_ANSWERS_JSON.toString(), result.mAnswersJson);
			cvArray[i] = tempCV;
		}
		return cvArray;
	}
	
	private void updateListAdapter(List<Item> results) {
		mListAdapter.clear();
		mListAdapter.addAll(results);
	}
	
	private List<Item> getResults(String questionsJsonStr) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        
        final String DAAT_USERNAME= "username";
        final String DAAT_TITLE = "title";
        final String DAAT_ANSWERS = "answers";
        final String DAAT_TEXT = "text";
        final String DAAT_ANSWERS_OWNER  = "owner";
        final String DAAT_ANSWERS_TEXT = "text";
        final String DAAT_ANSWERS_OWNER_FULLNAME = "fullName"; 
        final String DAAT_ANSWERS_OWNER_IMGLINK = "imgLnk";

        JSONArray questionsArray = new JSONArray(questionsJsonStr);
        List<Item> items = new LinkedList<Item>();
        List<String> resultStrs = new ArrayList<String>();
        for(int i = 0; i < questionsArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String userName ="";
            String userQuestion="";
            String userAnswer="";
            String jsonAnswers="";
            String userImg = "";
            String title="";
            int type;
            // Get the JSON object representing the day
            JSONObject question = questionsArray.getJSONObject(i);
            title = question.getString(DAAT_TITLE);
            JSONArray answers = question.getJSONArray(DAAT_ANSWERS);
            userQuestion = question.getString(DAAT_TEXT);
            if(answers.length() == 0) {
            	type = ASKED_A_QUESTION_TYPE_POST;
            	userName = question.getString(DAAT_USERNAME);
            }else {
            	type = ADDED_AN_ANSWER_TYPE_POST;
            	jsonAnswers = answers.toString();
            	JSONObject answerObject = answers.getJSONObject(0);
            	userAnswer = Html.fromHtml(answerObject.getString(DAAT_ANSWERS_TEXT)).toString();
            	JSONObject ownerObject = answerObject.getJSONObject(DAAT_ANSWERS_OWNER);
            	userName = ownerObject.getString(DAAT_ANSWERS_OWNER_FULLNAME);
            	userImg = ownerObject.getString(DAAT_ANSWERS_OWNER_IMGLINK);
            }
            items.add(new Item(type,userName,userImg,userQuestion,userAnswer,jsonAnswers));
        }

        for (Item item : items) {
            Log.v(LOG_TAG, "Item entry: " + item.mUser + ", " + item.mQuestion + ", " + item.mUserPic);
        }
        return items;

    }

	
	/*loader callback*/
	

}