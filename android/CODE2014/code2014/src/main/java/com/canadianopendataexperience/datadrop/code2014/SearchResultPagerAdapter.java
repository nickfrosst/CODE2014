package com.canadianopendataexperience.datadrop.code2014;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.canadianopendataexperience.datadrop.code2014.data.ActResultDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smcintyre on 01/03/14.
 */
public class SearchResultPagerAdapter extends FragmentPagerAdapter {

    private Code2014Activity context;
    private String currentQuery;
    private List<SearchResultFragment> results;
    private int count;

    public SearchResultPagerAdapter(Code2014Activity context, FragmentManager fm) {
        super(fm);
        this.context = context;
        this.results = new ArrayList<SearchResultFragment>();
        count = 0;
        this.notifyDataSetChanged();
    }

    public void setQuery(String query) {
        Log.d("DEBUG", "New query received: " + query);
        this.currentQuery = query;
        this.count = 0;
        this.notifyDataSetChanged();
        new LoadSearchResults(context, query).execute();
    }

    public void setResults(List<Integer> actKeys) {
        int fragId = 0;
        //reuse existing fragments if we can
        while(fragId < Math.min(results.size(), actKeys.size())) {
            Log.d("DEBUG", "Reusing fragment " + fragId);
            results.get(fragId).setInfo(this.currentQuery, actKeys.get(fragId));
            fragId++;
        }
        //then create extra fragments if necessary
        if (fragId < actKeys.size()) {
            while(fragId < actKeys.size()) {
                Log.d("DEBUG", "Creating fragment " + fragId);
                results.add(new SearchResultFragment(this.currentQuery, actKeys.get(fragId)));
                fragId++;
            }
        }
        this.count = fragId;
        this.notifyDataSetChanged();
        SearchResultPagerAdapter.this.onQueryFinished();
    }

    public void onQueryFinished() {
        context.onQueryFinished();
    }

    @Override
    public Fragment getItem(int position) {
        return results.get(position);
    }

    @Override
    public int getCount() {
        return count;
    }

    private class LoadSearchResults extends AsyncTask<Void, Void, List<Integer>> {
        private Context context;
        private String query;

        public LoadSearchResults(Context context, String query) {
            this.context = context;
            this.query = query;
        }

        @Override
        protected List<Integer> doInBackground(Void... params) {
            return ActResultDAO.getActResultDAO(context).getRelevantActs(query);
        }

        //Runs on UI thread.
        @Override
        protected void onPostExecute(List<Integer> actKeys) {
            SearchResultPagerAdapter.this.setResults(actKeys);
        }
    }
}
