/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.ScoreDoc;
import com.atlassian.lucene36.search.SortField;
import com.atlassian.lucene36.search.TopDocs;

public class TopFieldDocs
extends TopDocs {
    public SortField[] fields;

    public TopFieldDocs(int totalHits, ScoreDoc[] scoreDocs, SortField[] fields, float maxScore) {
        super(totalHits, scoreDocs, maxScore);
        this.fields = fields;
    }
}

