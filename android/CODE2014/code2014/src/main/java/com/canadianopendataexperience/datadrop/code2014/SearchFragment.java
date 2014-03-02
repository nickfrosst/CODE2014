package com.canadianopendataexperience.datadrop.code2014;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by smcintyre on 01/03/14.
 */
public class SearchFragment extends Fragment implements TextView.OnEditorActionListener {
    private EditText searchBox;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        searchBox = (EditText)rootView.findViewById(R.id.query);
        searchBox.setOnEditorActionListener(this);
        return rootView;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v == searchBox && actionId == EditorInfo.IME_ACTION_SEARCH) {
            Intent intent = new Intent("com.canadianopendataexperience.datadrop.code2014#queryChanged");
            String query = searchBox.getText().toString();
            query = query.trim();
            if (query.length() > 0) {
                query = query.split(" ")[0];
            }
            query = query.trim();
            query = query.toLowerCase();
            intent.putExtra("query", query);
            LocalBroadcastManager.getInstance(this.getActivity()).sendBroadcast(intent);
        }
        return false;
    }
}
