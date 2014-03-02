package com.canadianopendataexperience.datadrop.code2014.data;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for Act search results.
 *
 * Created by smcintyre on 01/03/14.
 */
public class ActResultDAO {
    private Context context;
    private static ActResultDAO singleton;

    private ActResultDAO(Context context) {
        this.context = context;
    }

    public static ActResultDAO getActResultDAO(Context context) {
        if (singleton == null) {
            singleton = new ActResultDAO(context);
        } else {
            singleton.context = context;
        }
        return singleton;
    }

    public List<Integer> getRelevantActs(String query) {
        //all relevant act ids in order of descending relevance
        List<Integer> result = new ArrayList<Integer>();
        String[] arguments = new String[1];
        arguments[0] = query;
        Cursor cursor = ActDatabase.getActDatabase(context).getCurrentConnection().rawQuery(
                "select distinct k.aid as aid " +
                "from stem s " +
                "inner join keyword k on  k.sid = s.sid " +
                "where ? like s.stem||'%' " +
                "order by k.freq desc " +
                "limit 10", arguments);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.add(cursor.getInt(cursor.getColumnIndex("aid")));
            cursor.moveToNext();
        }
        cursor.close();

        return result;
    }

    public RelevantActResult getRelevantAct(String query, int actId) {
        //fill in RelevantActResult info given a particular act
        List<String> relevantWords = new ArrayList<String>();
        List<Integer> relevance = new ArrayList<Integer>();
        String link = "", actName = "";
        int queryRelevance = 0;

        String[] arguments = new String[4];
        arguments[0] = ""+actId;
        arguments[1] = arguments[0];
        arguments[2] = query;
        arguments[3] = arguments[0];
        Cursor cursor = ActDatabase.getActDatabase(context).getCurrentConnection().rawQuery(
                "select medWord, freq, title, link from " +
                        "(select k.medWord, k.freq, a.title, a.link " +
                                "from keyword k " +
                                "inner join act a on k.aid = a.aid " +
                                "where k.aid = ? " +
                                "order by k.freq desc " +
                                "limit 30) " +
                "UNION " +
                "select k.medWord, k.freq, a.title, a.link " +
                "from keyword k " +
                "inner join act a on k.aid = a.aid " +
                "where k.aid = ? and " +
                "sid = (select k.sid " +
                "from stem s " +
                "inner join keyword k on  k.sid = s.sid " +
                "where ? like stem||'%' and aid = ?) " +
        "order by freq desc", arguments);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (actName.length() == 0) {
                actName = cursor.getString(cursor.getColumnIndex("title"));
                link = cursor.getString(cursor.getColumnIndex("link"));
            }
            if (cursor.getString(cursor.getColumnIndex("medWord")).equals(query)) {
                queryRelevance = cursor.getInt(cursor.getColumnIndex("freq"));
            } else {
                relevantWords.add(cursor.getString(cursor.getColumnIndex("medWord")));
                relevance.add(cursor.getInt(cursor.getColumnIndex("freq")));
            }
            cursor.moveToNext();
        }
        cursor.close();

        return new RelevantActResult(
                actName,
                link,
                query,
                queryRelevance,
                relevantWords,
                relevance
        );
    }
}
