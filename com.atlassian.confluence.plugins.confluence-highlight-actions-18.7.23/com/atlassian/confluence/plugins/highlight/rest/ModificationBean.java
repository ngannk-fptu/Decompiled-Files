/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.highlight.rest;

public class ModificationBean {
    protected long pageId;
    protected int numMatches;
    protected int index;
    protected String selectedText;
    protected long lastFetchTime;

    public long getPageId() {
        return this.pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public int getNumMatches() {
        return this.numMatches;
    }

    public void setNumMatches(int numMatches) {
        this.numMatches = numMatches;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getSelectedText() {
        return this.selectedText;
    }

    public void setSelectedText(String selectedText) {
        this.selectedText = selectedText;
    }

    public long getLastFetchTime() {
        return this.lastFetchTime;
    }

    public void setLastFetchTime(long lastFetchTime) {
        this.lastFetchTime = lastFetchTime;
    }
}

