package com.canadianopendataexperience.datadrop.code2014;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.canadianopendataexperience.datadrop.code2014.data.ActResultDAO;
import com.canadianopendataexperience.datadrop.code2014.data.RelevantActResult;

/**
 * Created by smcintyre on 01/03/14.
 */
public class SearchResultFragment extends Fragment {
    private String query;
    private int actId;
    private View rootView;

    public SearchResultFragment() {
        super();
    }

    public SearchResultFragment(String query, int actId) {
        this.query = query;
        this.actId = actId;
    }

    public void setInfo(String query, int actId) {
        this.query = query;
        this.actId = actId;
        if (this.isResumed()) {
            new LoadSearchResult(this.getActivity(), query, actId, rootView).execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_result, container, false);
        if (this.query != null) {
            new LoadSearchResult(this.getActivity(), query, actId, rootView).execute();
        }
        return rootView;
    }

    @Override
    public void onPause() {
        Log.d("DEBUG", "Fragment paused");
        super.onPause();
        //TODO If an async job is running, cancel it here!
    }

    @Override
    public void onResume() {
        Log.d("DEBUG", "Fragment resumed");
        super.onResume();
    }

    private class LoadSearchResult extends AsyncTask<Void, Void, RelevantActResult> {
        private Context context;
        private String query;
        private int actId;
        private View target;

        public LoadSearchResult(Context context, String query, int actId, View target) {
            this.context = context;
            this.query = query;
            this.actId = actId;
            this.target = target;
        }

        @Override
        protected void onPreExecute() {
            if (target != null) {
                TagCloudView cloud = (TagCloudView)target.findViewById(R.id.TagCloudView);
                cloud.setRelevantActResult(null);
                target.invalidate();
                target.findViewById(R.id.loadingSpinner).setVisibility(View.VISIBLE);
                target.findViewById(R.id.tapHint).setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected RelevantActResult doInBackground(Void... params) {
            return ActResultDAO.getActResultDAO(context).getRelevantAct(query, actId);
        }

        //Runs on UI thread.
        @Override
        protected void onPostExecute(RelevantActResult result) {
            if (target != null) {
                TagCloudView cloud = (TagCloudView)target.findViewById(R.id.TagCloudView);
                cloud.setRelevantActResult(result);
                target.invalidate();
                target.findViewById(R.id.loadingSpinner).setVisibility(View.GONE);
                target.findViewById(R.id.tapHint).setVisibility(View.GONE);
            }
        }
    }
}
