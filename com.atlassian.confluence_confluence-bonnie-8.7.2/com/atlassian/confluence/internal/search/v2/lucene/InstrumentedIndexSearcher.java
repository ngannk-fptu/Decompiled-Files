/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timer
 *  javax.annotation.Nonnull
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.search.Collector
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.ScoreDoc
 *  org.apache.lucene.search.Sort
 *  org.apache.lucene.search.TopDocs
 *  org.apache.lucene.search.TopFieldDocs
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneIndexMetrics;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timer;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;

public class InstrumentedIndexSearcher
extends IndexSearcher {
    private final Timer searchMetric;
    private final Timer searchAfterMetric;

    public InstrumentedIndexSearcher(@Nonnull IndexReader reader, @Nonnull LuceneIndexMetrics metrics) {
        super(reader);
        this.searchMetric = Objects.requireNonNull(metrics).timer("IndexSearcher", "IndexSearcher.Search");
        this.searchAfterMetric = Objects.requireNonNull(metrics).timer("IndexSearcher", "IndexSearcher.SearchAfter");
    }

    public TopDocs searchAfter(ScoreDoc after, Query query, int n) throws IOException {
        try (Ticker ignored = this.searchAfterMetric.start(new String[0]);){
            TopDocs topDocs = super.searchAfter(after, query, n);
            return topDocs;
        }
    }

    public TopDocs searchAfter(ScoreDoc after, Query query, Filter filter, int n) throws IOException {
        try (Ticker ignored = this.searchAfterMetric.start(new String[0]);){
            TopDocs topDocs = super.searchAfter(after, query, filter, n);
            return topDocs;
        }
    }

    public TopDocs searchAfter(ScoreDoc after, Query query, Filter filter, int n, Sort sort) throws IOException {
        try (Ticker ignored = this.searchAfterMetric.start(new String[0]);){
            TopDocs topDocs = super.searchAfter(after, query, filter, n, sort);
            return topDocs;
        }
    }

    public TopDocs searchAfter(ScoreDoc after, Query query, int n, Sort sort) throws IOException {
        try (Ticker ignored = this.searchAfterMetric.start(new String[0]);){
            TopDocs topDocs = super.searchAfter(after, query, n, sort);
            return topDocs;
        }
    }

    public TopDocs searchAfter(ScoreDoc after, Query query, Filter filter, int n, Sort sort, boolean doDocScores, boolean doMaxScore) throws IOException {
        try (Ticker ignored = this.searchAfterMetric.start(new String[0]);){
            TopDocs topDocs = super.searchAfter(after, query, filter, n, sort, doDocScores, doMaxScore);
            return topDocs;
        }
    }

    public TopDocs search(Query query, Filter filter, int n) throws IOException {
        try (Ticker ignored = this.searchMetric.start(new String[0]);){
            TopDocs topDocs = super.search(query, filter, n);
            return topDocs;
        }
    }

    public void search(Query query, Filter filter, Collector results) throws IOException {
        try (Ticker ignored = this.searchMetric.start(new String[0]);){
            super.search(query, filter, results);
        }
    }

    public void search(Query query, Collector results) throws IOException {
        try (Ticker ignored = this.searchMetric.start(new String[0]);){
            super.search(query, results);
        }
    }

    public TopFieldDocs search(Query query, Filter filter, int n, Sort sort) throws IOException {
        try (Ticker ignored = this.searchMetric.start(new String[0]);){
            TopFieldDocs topFieldDocs = super.search(query, filter, n, sort);
            return topFieldDocs;
        }
    }

    public TopFieldDocs search(Query query, Filter filter, int n, Sort sort, boolean doDocScores, boolean doMaxScore) throws IOException {
        try (Ticker ignored = this.searchMetric.start(new String[0]);){
            TopFieldDocs topFieldDocs = super.search(query, filter, n, sort, doDocScores, doMaxScore);
            return topFieldDocs;
        }
    }

    public TopFieldDocs search(Query query, int n, Sort sort) throws IOException {
        try (Ticker ignored = this.searchMetric.start(new String[0]);){
            TopFieldDocs topFieldDocs = super.search(query, n, sort);
            return topFieldDocs;
        }
    }
}

