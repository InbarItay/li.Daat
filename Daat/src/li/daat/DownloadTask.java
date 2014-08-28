package li.daat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadTask extends AsyncTask<String, Integer, String> {
	final static String QUESTIONS_BASE_URL =
            "http://daat.herokuapp.com/questions/newest";
    final static String QUERY_PARAM = "sinceDate";
	WeakReference<IDownloadDelegate> mDownloadDelegate;
	public DownloadTask(IDownloadDelegate downloadDelegate) {
		super();
		mDownloadDelegate = new WeakReference<DownloadTask.IDownloadDelegate>(downloadDelegate);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected String doInBackground(String... params) {
		// These two need to be declared outside the try/catch
		// so that they can be closed in the finally block.
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;
		 
		// Will contain the raw JSON response as a string.
		String questionsJsonStr = null;
		 
		try {
		    // Construct the URL for the OpenWeatherMap query
		    // Possible parameters are available at OWM's forecast API page, at
		    // http://openweathermap.org/API#forecast
			
			URL url;
			if (params != null) {
				Uri builtUri = Uri.parse(QUESTIONS_BASE_URL+"?").buildUpon()
	                    .appendQueryParameter(QUERY_PARAM, params[0])
	                    .build();
			    url = new URL(builtUri.toString());
			}else {
				url= new URL(QUESTIONS_BASE_URL);
			}
            
            ///questions/newest?sinceDate=2014-08-17T20:27:46.285Z
            
            
		 
		    // Create the request to OpenWeatherMap, and open the connection
		    urlConnection = (HttpURLConnection) url.openConnection();
		    urlConnection.setRequestMethod("GET");
		    urlConnection.connect();
		 
		    // Read the input stream into a String
		    InputStream inputStream = urlConnection.getInputStream();
		    StringBuffer buffer = new StringBuffer();
		    if (inputStream == null) {
		        // Nothing to do.
		        questionsJsonStr = null;
		    }
		    reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
		 
		    String line;
		    while ((line = reader.readLine()) != null) {
		        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
		        // But it does make debugging a *lot* easier if you print out the completed
		        // buffer for debugging.
		        buffer.append(line + "\n");
		    }
		 
		    if (buffer.length() == 0) {
		        // Stream was empty.  No point in parsing.
		        questionsJsonStr = null;
		    }
		    questionsJsonStr = buffer.toString();
		} catch (IOException e) {
		    Log.e("MainFragment", "Error ", e);
		    // If the code didn't successfully get the weather data, there's no point in attempting
		    // to parse it.
		    questionsJsonStr = null;
		} finally{
		    if (urlConnection != null) {
		        urlConnection.disconnect();
		    }
		    if (reader != null) {
		        try {
		            reader.close();
		        } catch (final IOException e) {
		            Log.e("PlaceholderFragment", "Error closing stream", e);
		        }
		    }
		}
		return questionsJsonStr;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (result!=null) {
			mDownloadDelegate.get().downloadFinished(result);
		}
	}
	
	
	
	
	public interface IDownloadDelegate{
		public void downloadFinished(String result);
	}
}
