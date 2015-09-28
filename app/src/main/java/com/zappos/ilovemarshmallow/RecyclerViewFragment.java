package com.zappos.ilovemarshmallow;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RecyclerViewFragment extends Fragment {

    private static final String LOG_TAG = RecyclerViewFragment.class.getSimpleName();
    private static final int SPAN_COUNT_PORTRAIT = 2;
    private static final int SPAN_COUNT_LANDSCAPE = 4;

    protected RecyclerView mRecyclerView;
    protected RecycleViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private SingleItem[] mDataSet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Creating RecyclerView");

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_fragment, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT_PORTRAIT);
        } else {
            mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT_LANDSCAPE);
        }

        mRecyclerView.setLayoutManager(mLayoutManager);

        return rootView;
    }

    public void startSearch(String query) {
        new FetchSearchResultTask().execute(query);
    }

    private class FetchSearchResultTask extends AsyncTask<String, Void, SingleItem[]> {

        private final String LOG_TAG = FetchSearchResultTask.class.getSimpleName();

        @Override
        protected SingleItem[] doInBackground(String... params) {

            final String SEARCH_BASE_URL = "https://zappos.amazon.com/mobileapi/v1/search";
            final String TERM_PARAM = "term";

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            Uri builtUri = Uri.parse(SEARCH_BASE_URL).buildUpon()
                    .appendQueryParameter(TERM_PARAM, params[0]).build();

            // Will contain the raw JSON response as a string.
            String searchResultJson = null;

            try {

                URL url = new URL(builtUri.toString());

                // Create the request to ZapposAPI, and open the connection
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
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                searchResultJson = buffer.toString();

            } catch (MalformedURLException e) {

                Log.e(LOG_TAG, "Search URL could not be built");

            } catch (IOException e) {

                Log.e(LOG_TAG, "Failed to open URL connection");

            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }

                try {
                    if (searchResultJson != null) {
                        return getSingleItemFromJson(searchResultJson);
                    } else {
                        return null;
                    }

                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage());
                    return null;
                }

            }
        }

        @Override
        protected void onPostExecute(SingleItem[] singleItems) {
            if (singleItems != null) {
                mDataSet = singleItems;
                mAdapter = new RecycleViewAdapter(mDataSet, getActivity().getBaseContext());
                // Set RecycleViewAdapter as the adapter for RecyclerView.
                mRecyclerView.setAdapter(mAdapter);
            } else {
                Toast.makeText(getActivity().getBaseContext(),
                        "No item found",
                        Toast.LENGTH_LONG).show();
            }
        }

        /**
         * Take the String representing the complete result in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         */
        private SingleItem[] getSingleItemFromJson(String jsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String ARRAY = "results";
            final String IMAGE_URL = "imageUrl";
            final String PRODUCT_NAME = "productName";
            final String PRICE = "price";
            final String BRAND_NAME = "brandName";
            final String ASIN = "asin";
            final String PRODUCT_RATING = "productRating";

            JSONObject searchJson = new JSONObject(jsonStr);
            JSONArray resultArray = searchJson.getJSONArray(ARRAY);

            int totalResults = resultArray.length();
            SingleItem[] result = new SingleItem[totalResults];

            for (int i = 0; i < totalResults; i++) {

                // Get the JSON object representing the item
                JSONObject item = resultArray.getJSONObject(i);

                String imageUrl = item.getString(IMAGE_URL);
                String productName = item.getString(PRODUCT_NAME);
                String price = item.getString(PRICE);
                String brandName = item.getString(BRAND_NAME);
                String asin = item.getString(ASIN);
                float productRating = (float) item.getDouble(PRODUCT_RATING);

                result[i] = new SingleItem(imageUrl, productName, price, brandName, asin, productRating);

            }

            return result;

        }
    }
}
