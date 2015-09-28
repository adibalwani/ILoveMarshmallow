package com.zappos.ilovemarshmallow;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ZapposActivity extends AppCompatActivity {

    private static final String LOG_TAG = ZapposActivity.class.getSimpleName();
    private RecyclerViewFragment mRecyclerViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(LOG_TAG, "Creating ZapposActivity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.zappos_activity);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            mRecyclerViewFragment = new RecyclerViewFragment();
            transaction.replace(R.id.main_container, mRecyclerViewFragment);
            transaction.commit();
        }

        setupToolbar();
        showToast(getResources().getString(R.string.black_screen));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_zappos, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search_menu_item).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isNetworkAvailable()) {
                    mRecyclerViewFragment.startSearch(query);
                } else {
                    showToast(getResources().getString(R.string.network_unavailable));
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_menu_item:
                return true;
            case R.id.share_menu_item:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.zappos_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setBackgroundColor(getResources().getColor(R.color.theme_primary));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showToast(String toastString) {
        Toast.makeText(getBaseContext(), toastString, Toast.LENGTH_LONG).show();
    }
}
