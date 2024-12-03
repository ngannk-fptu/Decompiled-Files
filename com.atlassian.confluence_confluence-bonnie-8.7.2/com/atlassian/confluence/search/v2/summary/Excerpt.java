/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.summary;

import com.atlassian.confluence.search.v2.summary.Summary;
import java.util.Enumeration;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

public class Excerpt {
    Vector passages = new Vector();
    SortedSet tokenSet = new TreeSet();
    int numTerms = 0;

    public void addToken(String token) {
        this.tokenSet.add(token);
    }

    public int numUniqueTokens() {
        return this.tokenSet.size();
    }

    public int numFragments() {
        return this.passages.size();
    }

    public int getNumTerms() {
        return this.numTerms;
    }

    public void setNumTerms(int numTerms) {
        this.numTerms = numTerms;
    }

    public void add(Summary.Fragment fragment) {
        this.passages.add(fragment);
    }

    public Enumeration elements() {
        return this.passages.elements();
    }
}

