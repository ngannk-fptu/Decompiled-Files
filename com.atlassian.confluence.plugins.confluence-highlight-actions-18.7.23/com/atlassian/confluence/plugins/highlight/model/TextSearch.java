/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.highlight.model;

public class TextSearch {
    private int numMatches;
    private int matchIndex;
    private String text;

    public TextSearch(String text) {
        this.text = text;
        this.numMatches = 1;
        this.matchIndex = 0;
    }

    public TextSearch(String text, int numMatches, int matchIndex) {
        this.text = text;
        this.numMatches = numMatches;
        this.matchIndex = matchIndex;
    }

    public int getNumMatches() {
        return this.numMatches;
    }

    public int getMatchIndex() {
        return this.matchIndex;
    }

    public String getText() {
        return this.text;
    }
}

