package li.daat;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import li.daat.adapters.ListAdapterFactory;
import li.daat.adapters.ListAdapterFactory.ListAdapterWrapper;
import li.daat.download.DownloadQuestionsTask;
import li.daat.download.DownloadQuestionsTask.IDownloadDelegate;
import li.daat.download.ImagesCachingList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class MainFragment extends Fragment implements IDownloadDelegate{

	private final List<String> imagesPathToLoad = new LinkedList<String>(); 
	private final static String TAG = MainFragment.class.getSimpleName();
	private ListView mListView;
	private ListAdapterWrapper mListAdapterWrapper;
	
	//TODO: switch to enum + move to somewhere better
	public static final int ASKED_A_QUESTION_TYPE_POST = 0;
	public static final int ADDED_AN_ANSWER_TYPE_POST = 1;
	
	public static final String ASKED_A_QUESTION_TYPE_HEADLINE = "asked a question..";
	public static final String ADDED_AN_ANSWER_TYPE_HEADLINE = "added an answer..";
	private static final String LOG_TAG = "MAIN_FRAGMENT";
	
	/** Defines callbacks for service binding, passed to bindService() */
//    private ServiceConnection mConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName className, IBinder service) {
//            // We've bound to LocalService, cast the IBinder and get LocalService instance
//            MyBinder binder = (MyBinder) service;
////            mService = binder.getService();
////            mBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
////            mBound = false;
//        }
//    };
	
	public MainFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
//		getActivity().bindService(DownloadImagesService.makeIntent(null, getActivity()), mConnection, Context.BIND_AUTO_CREATE);
		updateData();
	}
	
	@Override
	public void onStop() {
		super.onStop();
//		getActivity().unbindService(mConnection);
	}
	
	@Override
	public void onDestroy() {
		ImagesCachingList.getInstance().deleteCache();
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		mListView = (ListView)rootView.findViewById(R.id.main_list);
		ListAdapterFactory factory = new ListAdapterFactory(ListAdapterFactory.AdapterTypes.CURSOR_ADAPTER_TYPE);
		mListAdapterWrapper = factory.getAdapterWrapper(getActivity());
		mListView.setAdapter(mListAdapterWrapper.mListAdapter);
		mListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mListAdapterWrapper.onItemClicked(position);
			}
			
		});
		return rootView;
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
		DownloadQuestionsTask dt = new DownloadQuestionsTask(this);
		dt.execute((String[])null);
	}
	
	@Override
	public void downloadFinished(String result) {
		List<Item> results;
		try {
			results = getResults(result);
		} catch (Exception e) {
			Log.e(TAG, "couldn't parse the results");
			return;
		}
		mListAdapterWrapper.updateAdapter(results);
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
        
        for(int i = 0; i < questionsArray.length(); i++) {
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
            title = question.getString(DAAT_TITLE);
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
            items.add(new Item(type,userName,userImg,title,userQuestion,userAnswer,jsonAnswers));
        }
        
        return items;
    }
	
	
}