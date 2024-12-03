/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.DocIdSet;
import com.atlassian.lucene36.search.SpanFilter;
import com.atlassian.lucene36.search.SpanFilterResult;
import com.atlassian.lucene36.search.spans.SpanQuery;
import com.atlassian.lucene36.search.spans.Spans;
import com.atlassian.lucene36.util.FixedBitSet;
import java.io.IOException;
import java.util.ArrayList;

public class SpanQueryFilter
extends SpanFilter {
    protected SpanQuery query;

    protected SpanQueryFilter() {
    }

    public SpanQueryFilter(SpanQuery query) {
        this.query = query;
    }

    public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
        SpanFilterResult result = this.bitSpans(reader);
        return result.getDocIdSet();
    }

    public SpanFilterResult bitSpans(IndexReader reader) throws IOException {
        FixedBitSet bits = new FixedBitSet(reader.maxDoc());
        Spans spans = this.query.getSpans(reader);
        ArrayList<SpanFilterResult.PositionInfo> tmp = new ArrayList<SpanFilterResult.PositionInfo>(20);
        int currentDoc = -1;
        SpanFilterResult.PositionInfo currentInfo = null;
        while (spans.next()) {
            int doc = spans.doc();
            bits.set(doc);
            if (currentDoc != doc) {
                currentInfo = new SpanFilterResult.PositionInfo(doc);
                tmp.add(currentInfo);
                currentDoc = doc;
            }
            currentInfo.addPosition(spans.start(), spans.end());
        }
        return new SpanFilterResult(bits, tmp);
    }

    public SpanQuery getQuery() {
        return this.query;
    }

    public String toString() {
        return "SpanQueryFilter(" + this.query + ")";
    }

    public boolean equals(Object o) {
        return o instanceof SpanQueryFilter && this.query.equals(((SpanQueryFilter)o).query);
    }

    public int hashCode() {
        return this.query.hashCode() ^ 0x923F64B9;
    }
}

