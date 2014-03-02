package com.canadianopendataexperience.datadrop.code2014;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.canadianopendataexperience.datadrop.code2014.data.ActDatabase;

public class Code2014Activity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    private PageIndicatorView pageIndicator;
    private TextView helpText, noResultsText;
    private ProgressBar loadingView;
    private ViewPager pager;
    private SearchResultPagerAdapter resultAdapter;
    private String query;

    private BroadcastReceiver queryChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Code2014Activity.this.setQuery(intent.getStringExtra("query"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set style
        super.setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_code2014);

        helpText = (TextView)findViewById(R.id.helpText);
        helpText.setTypeface(Typeface.createFromAsset(this.getApplicationContext().getAssets(), "fonts/Roboto-Bold.ttf"));

        noResultsText = (TextView)findViewById(R.id.noResultText);
        noResultsText.setTypeface(Typeface.createFromAsset(this.getApplicationContext().getAssets(), "fonts/Roboto-Bold.ttf"));
        noResultsText.setVisibility(View.GONE);

        resultAdapter = new SearchResultPagerAdapter(this, getSupportFragmentManager());
        pager = ((ViewPager)findViewById(R.id.SearchResultsPager));
        pager.setAdapter(resultAdapter);

        pageIndicator = (PageIndicatorView)findViewById(R.id.search_result_page_indicator);

        loadingView = (ProgressBar)findViewById(R.id.loadingSpinner);
        loadingView.setVisibility(View.GONE);

        this.query = null;


        //TagCloudView tags = (TagCloudView)this.findViewById(R.id.TagCloudView);
        //tags.setRelevantActResult(actResult);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(queryChanged, new IntentFilter("com.canadianopendataexperience.datadrop.code2014#queryChanged"));
        ActDatabase.getActDatabase(this).open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(queryChanged);
        ActDatabase.getActDatabase(this).close();
    }

    private void setQuery(String query) {
        this.query = query;
        helpText.setVisibility(View.GONE);
        noResultsText.setVisibility(View.GONE);
        pager.setVisibility(View.GONE);
        pageIndicator.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        resultAdapter.setQuery(query);
    }

    public void onQueryFinished() {
        loadingView.setVisibility(View.GONE);
        if (resultAdapter.getCount() > 0) {
            noResultsText.setVisibility(View.GONE);
            pager.setVisibility(View.VISIBLE);
            pageIndicator.setVisibility(View.VISIBLE);
            pager.setCurrentItem(0);
            pager.setOnPageChangeListener(this);
            pageIndicator.setPageInfo(0, resultAdapter.getCount());
        } else {
            noResultsText.setVisibility(View.VISIBLE);
            pager.setVisibility(View.GONE);
            pageIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //set page indicator with total pages and position!
        pageIndicator.setPageInfo(position, resultAdapter.getCount());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
