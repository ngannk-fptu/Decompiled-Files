/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 */
package com.atlassian.confluence.plugins.search.api.model;

import com.atlassian.confluence.plugins.search.api.model.SearchResult;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value=XmlAccessType.FIELD)
public class SearchResultList {
    private List<SearchResult> results;
    private int total;
    private int archivedResultsCount;
    private String uuid;
    private long timeSpent;

    private SearchResultList() {
    }

    public SearchResultList(List<SearchResult> results, int total, int archivedResultsCount, String uuid, long timeSpent) {
        this.results = results;
        this.total = total;
        this.archivedResultsCount = archivedResultsCount;
        this.uuid = uuid;
        this.timeSpent = timeSpent;
    }

    public List<SearchResult> getResults() {
        return this.results;
    }

    public int getTotal() {
        return this.total;
    }

    public int getArchivedResultsCount() {
        return this.archivedResultsCount;
    }

    public String getUuid() {
        return this.uuid;
    }

    public long getTimeSpent() {
        return this.timeSpent;
    }

    private void setResults(List<SearchResult> results) {
        this.results = results;
    }

    private void setTotal(int total) {
        this.total = total;
    }

    private void setArchivedResultsCount(int archivedResultsCount) {
        this.archivedResultsCount = archivedResultsCount;
    }

    private void setUuid(String uuid) {
        this.uuid = uuid;
    }

    private void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }
}

