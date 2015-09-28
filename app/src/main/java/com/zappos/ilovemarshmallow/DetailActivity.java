package com.zappos.ilovemarshmallow;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private String price;
    private String asin;
    private float rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(LOG_TAG, "Creating ZapposActivity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        Intent intent = getIntent();
        asin = intent.getStringExtra(getResources().getString(R.string.asin));
        price = intent.getStringExtra(getResources().getString(R.string.price));
        rating = intent.getFloatExtra(getResources().getString(R.string.rating), 0);
        if (isNetworkAvailable()) {
            new FetchDetailTask().execute(asin);
        } else {
            Toast.makeText(getBaseContext(),
                    getResources().getString(R.string.network_unavailable),
                    Toast.LENGTH_LONG).show();
        }

        setupToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        // Get the menu item.
        MenuItem menuItem = menu.findItem(R.id.share_menu_item);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.e(LOG_TAG, "Share Action Provider is null?");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.share_menu_item) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private Intent createShareForecastIntent() {
        final String DETAIL_BASE_URL = "https://zappos.amazon.com/mobileapi/v1/product/asin";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, DETAIL_BASE_URL + "/" + asin);
        return shareIntent;

    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.zappos_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setBackgroundColor(getResources().getColor(R.color.theme_primary));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
    }

    private class FetchDetailTask extends AsyncTask<String, Void, DetailItem> {

        private final String LOG_TAG = FetchDetailTask.class.getSimpleName();

        @Override
        protected DetailItem doInBackground(String... params) {

            final String DETAIL_BASE_URL = "https://zappos.amazon.com/mobileapi/v1/product/asin";

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            Uri builtUri = Uri.parse(DETAIL_BASE_URL).buildUpon()
                    .appendPath(params[0]).build();

            // Will contain the raw JSON response as a string.
            String detailResultJson = null;

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
                detailResultJson = buffer.toString();

            } catch (MalformedURLException e) {

                Log.e(LOG_TAG, "Detail URL could not be built");

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
                    return getDetailItemFromJson(detailResultJson);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage());
                    return null;
                }

            }
        }

        @Override
        protected void onPostExecute(DetailItem detailItem) {
            if (detailItem != null) {
                ImageView productImage = (ImageView) findViewById(R.id.product_image);
                TextView brandName = (TextView) findViewById(R.id.brand_name);
                TextView price = (TextView) findViewById(R.id.price);
                TextView productName = (TextView) findViewById(R.id.product_name);
                RatingBar productRating = (RatingBar) findViewById(R.id.product_rating);
                WebView productDescription = (WebView) findViewById(R.id.product_description);

                float rating = detailItem.getProductRating();
                String brandNameString = detailItem.getBrandName();
                String priceString = detailItem.getPrice();
                String productNameString = detailItem.getProductName();
                String productDescriptionString = detailItem.getProductDescription();

                brandNameString = brandNameString == null ? "" : brandNameString;
                priceString = priceString == null ? "" : priceString;
                productNameString = productNameString == null ? "" : productNameString;

                Picasso.with(getBaseContext()).load(detailItem.getImageUrl()).into(productImage);
                brandName.setText(brandNameString);
                price.setText(priceString);
                productName.setText(productNameString);
                String htmlString = "<html><head>"
                        + "<style type=\"text/css\">body{color: #414141; background-color: #eeeeee;}"
                        + "</style></head>"
                        + "<body>"
                        + productDescriptionString
                        + "</body></html>";
                productDescription.loadData(htmlString, "text/html", null);


                if (rating > 0) {
                    productRating.setRating(rating);
                } else {
                    productRating.setVisibility(View.INVISIBLE);
                }
            }
        }

        /**
         * Take the String representing the complete result in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         */
        private DetailItem getDetailItemFromJson(String jsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String IMAGE_URL = "defaultImageUrl";
            final String PRODUCT_NAME = "productName";
            final String BRAND_NAME = "brandName";
            final String PRODUCT_DESCRIPTION = "description";

            JSONObject searchJson = new JSONObject(jsonStr);
            String imageUrl = searchJson.getString(IMAGE_URL);
            String brandName = searchJson.getString(BRAND_NAME);
            String productName = searchJson.getString(PRODUCT_NAME);
            String productDescription = searchJson.getString(PRODUCT_DESCRIPTION);

            return new DetailItem(imageUrl, brandName, price, productName, rating, productDescription);

        }
    }

}
