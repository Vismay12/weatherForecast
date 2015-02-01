package com.example.android.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.os.AsyncTask.execute;

/**
* Created by Vismay on 12/27/2014.
*/
public class ForecastFragment extends Fragment {
public static final String TAG=ForecastFragment.class.getSimpleName();
public ForecastFragment() {
}
private ArrayAdapter<String> weekforecastAdapter=null;
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
}

@Override
public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.forecastfragment, menu);
}

public void updateweather(){
    Log.d("yo","inside updateweather");
    SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getActivity());
    String location=prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
    Log.d("yo",location);

    new FetchWeatherTask().execute(location);
}
public String formatHighLow(double high,double low) {
    SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(getActivity());
    String unitType = sharedpref.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_metric));

    if (unitType.equals(getString(R.string.pref_units_imperial))) {
        high=(high*1.8)+32;
        low=(low*1.8)+32;

    }else if(!unitType.equals(getString(R.string.pref_units_metric))){
            Log.d("yo","Unit type not found:"+unitType);
                }
    long roundedHigh = Math.round(high);
    long roundedLow=Math.round(low);
    String highLowStr=roundedHigh+"/"+roundedHigh;
    return highLowStr;
}
@Override
public void onStart() {
    super.onStart();
    Log.d("yo","inside onStart");
   // updateweather();

}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_refresh) {
        updateweather();
      //  new FetchWeatherTask().execute("75001");

        return true;
    }

    return super.onOptionsItemSelected(item);
}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);
    List<String> weekforecast = new ArrayList<String>();
    weekforecast.add("Today--Sunny-- 88/63");
    weekforecast.add("Tomorrow--Foggy-- 20/46");
    weekforecast.add("Weds--Cloudy-- 72/63");
    weekforecast.add("Thurs--Rainy-- 64/51");
    weekforecast.add("Fri--Foggy-- 70/46");
    weekforecast.add("Sat--Sunny-- 76/68");

    weekforecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekforecast);

    ListView view_list = (ListView) rootView.findViewById(R.id.listview_forecast);
    view_list.setAdapter(weekforecastAdapter);
    view_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String forecast=weekforecastAdapter.getItem(position);
            Intent detail_intent = new Intent(getActivity(), DetalActivity.class).putExtra(Intent.EXTRA_TEXT,forecast);
            startActivity(detail_intent);
        }
    });
    return rootView;

}

public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();


    @Override
    protected String[] doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }
        // Will contain the raw JSON response as a string.

        String forecastJsonStr = null;
// These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;

        // Construct the URL for the OpenWeatherMap query
        // Possible parameters are available at OWM's forecast API page, at
        // http://openweathermap.org/API#forecast
        // new FetchWeatherTask().execute();

            BufferedReader reader = null;
            String format = "json";
            String units = "metric";
            int numDays = 7;
            try {
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
//                Uri.Builder builder = new Uri.Builder();
//                builder.scheme("https").authority("http://api.openweathermap.org").appendPath("data").appendPath("2.5").appendPath("forecast")
//                        .appendPath("daily").appendQueryParameter("q", params[0]).appendQueryParameter("mode", "json").appendQueryParameter("units", "metric").appendQueryParameter("cnt", "7");
//                String myUri = builder.build().toString();
//                Log.d("yo", myUri);
//                URL url = new URL(myUri);
                Log.d("yo000000000000000000",params[0]);
final String FORECAST_BASE_URL="http://api.openweathermap.org/data/2.5/forecast/daily?";
final String QUERY_PARAM = "q";
final String FORMAT_PARAM = "mode";
final String UNITS_PARAM = "units";
final String DAYS_PARAM = "cnt";

                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
                Uri builtUri=Uri.parse(FORECAST_BASE_URL).buildUpon().appendQueryParameter(QUERY_PARAM,params[0])
                        .appendQueryParameter(FORMAT_PARAM,format)
                        .appendQueryParameter(UNITS_PARAM,units)
                        .appendQueryParameter(DAYS_PARAM,Integer.toString(numDays)).build();
               // URL url=new URL(builtUri.toString());
                Log.d("yo","BUILT URI"+builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                    Log.d("yoline",line);
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.d("yoforecastJsonStr", forecastJsonStr);
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
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
            try

            {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e("yo", e.getMessage(), e);
                e.printStackTrace();
            }

            return null;


        }

    private String getReadableDateString(long time) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p/>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {
        Log.d(TAG,"inside getWeatherDataFromJson");
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DATETIME = "dt";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
        Log.d(TAG,""+weatherArray);
        String[] resultStrs = new String[numDays];
        for (int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime = dayForecast.getLong(OWM_DATETIME);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);
            Log.d(TAG,"value of high is"+high);
            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + high;
        }
        for(String s: resultStrs){
            Log.d("yo","Forecast entry"+s);
        }

        return resultStrs;
    }


    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);
        if(strings!=null)   {
            weekforecastAdapter.clear();
            for(String dayForecastStr:strings){
                weekforecastAdapter.add(dayForecastStr);
            }
        }
    }



}
}