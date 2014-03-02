package com.canadianopendataexperience.datadrop.code2014.data;

import java.util.List;

/**
 * Created by smcintyre on 01/03/14.
 */
public class RelevantActResult {
    private String actName; //name of the act
    private String actLink; //URL of the act
    private String query; //the query that generated this act result
    private int queryRelevance; //the relevance of the query to this act result
    private List<String> relevantWords; //should be in descending order of relevance
    private List<Integer> relevance; //integer values for each word, giving their relevance

    public RelevantActResult(String actName, String actLink, String query, int queryRelevance, List<String> relevantWords, List<Integer> relevance) {
        this.actName = actName;
        this.actLink = actLink;
        this.query = query;
        this.queryRelevance = queryRelevance;
        this.relevantWords = relevantWords;
        this.relevance = relevance;
    }

    public String getActName() {
        return actName;
    }

    public String getQuery() {
        return query;
    }

    public int getQueryRelevance() {
        return queryRelevance;
    }

    public List<String> getRelevantWords() {
        return relevantWords;
    }

    public List<Integer> getRelevance() {
        return relevance;
    }

    public String getActLink() {
        return actLink;
    }
}
