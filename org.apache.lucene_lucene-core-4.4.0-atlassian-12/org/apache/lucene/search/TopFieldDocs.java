/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;

public class TopFieldDocs
extends TopDocs {
    public SortField[] fields;

    public TopFieldDocs(int totalHits, ScoreDoc[] scoreDocs, SortField[] fields, float maxScore) {
        super(totalHits, scoreDocs, maxScore);
        this.fields = fields;
    }
}

