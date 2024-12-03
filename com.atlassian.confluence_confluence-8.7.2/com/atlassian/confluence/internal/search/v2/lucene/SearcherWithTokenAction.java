/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection$SearcherWithTokenAction
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.lucene.document.Document
 *  org.apache.lucene.search.BooleanQuery$TooManyClauses
 *  org.apache.lucene.search.Collector
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.ScoreDoc
 *  org.apache.lucene.search.Sort
 *  org.apache.lucene.search.TopFieldDocs
 *  org.apache.lucene.search.TopScoreDocCollector
 *  org.apache.lucene.search.similarities.Similarity
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.BM25LSimilarity;
import com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection;
import com.atlassian.confluence.internal.search.v2.lucene.TopDocuments;
import com.atlassian.confluence.search.v2.SearchConstants;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.similarities.Similarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearcherWithTokenAction
implements ILuceneConnection.SearcherWithTokenAction<TopDocuments> {
    private static final Logger log = LoggerFactory.getLogger(SearcherWithTokenAction.class);
    private final Query query;
    private final Filter filter;
    private final Sort sort;
    private final Set<String> requestedFields;
    private boolean explain = false;
    private static final float K_1 = 1.25f;
    private static final float B = 0.3f;
    private static final float DELTA = 0.5f;
    private final Similarity similarity = new BM25LSimilarity(1.25f, 0.3f, 0.5f);
    @VisibleForTesting
    final int startOffset;
    @VisibleForTesting
    final int limit;

    public SearcherWithTokenAction(Query query, Filter filter, Sort sort, int startOffset, int limit, Set<String> requestedFields) {
        this.query = query;
        this.filter = filter;
        this.sort = sort;
        this.startOffset = Math.min(startOffset, SearchConstants.MAX_START_OFFSET);
        this.limit = Math.min(limit, SearchConstants.MAX_LIMIT);
        this.requestedFields = requestedFields;
    }

    public TopDocuments perform(IndexSearcher indexSearcher, long searchToken) throws IOException {
        return this.internalPerform(indexSearcher, searchToken);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private TopDocuments internalPerform(IndexSearcher indexSearcher, long searchToken) throws IOException {
        TopFieldDocs topDocs;
        int totalHits;
        long now = 0L;
        if (log.isDebugEnabled()) {
            now = System.currentTimeMillis();
        }
        indexSearcher.setSimilarity(this.similarity);
        try {
            if (this.sort == null) {
                TopScoreDocCollector collector = TopScoreDocCollector.create((int)(this.startOffset + this.limit), (boolean)true);
                indexSearcher.search(this.query, this.filter, (Collector)collector);
                totalHits = collector.getTotalHits();
                topDocs = collector.topDocs(this.startOffset, this.limit);
            } else {
                if (this.startOffset > 0) {
                    topDocs = indexSearcher.search(this.query, this.filter, this.startOffset, this.sort);
                    if (topDocs.totalHits > 0) {
                        ScoreDoc lastDoc = topDocs.scoreDocs[topDocs.scoreDocs.length - 1];
                        topDocs = indexSearcher.searchAfter(lastDoc, this.query, this.filter, this.limit, this.sort);
                    }
                } else {
                    topDocs = indexSearcher.search(this.query, this.filter, this.limit, this.sort);
                }
                totalHits = topDocs.totalHits;
            }
        }
        catch (BooleanQuery.TooManyClauses e) {
            if (log.isDebugEnabled()) {
                log.debug("Error encountered in lucene search: " + e.getMessage(), (Throwable)e);
            } else {
                log.warn("Error encountered in lucene search: " + e.getMessage());
            }
            TopDocuments topDocuments = TopDocuments.EMPTY;
            return topDocuments;
        }
        catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().endsWith("no terms in field modified - cannot determine sort type")) {
                log.info("Error encountered in lucene search - most likely empty index", (Throwable)e);
            } else {
                log.error("Error encountered in lucene search: " + e.getMessage(), (Throwable)e);
            }
            TopDocuments topDocuments = TopDocuments.EMPTY;
            return topDocuments;
        }
        finally {
            if (log.isDebugEnabled()) {
                log.debug("Query time = " + (System.currentTimeMillis() - now) + "ms, Query = " + this.query);
            }
        }
        ArrayList<Document> documents = new ArrayList<Document>(topDocs.scoreDocs.length);
        ArrayList<String> explanations = new ArrayList<String>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            documents.add(indexSearcher.doc(scoreDoc.doc, this.requestedFields));
            if (!this.isExplain()) continue;
            explanations.add(indexSearcher.explain(this.query, scoreDoc.doc).toString());
        }
        boolean moreResults = totalHits - (this.startOffset + topDocs.scoreDocs.length) > 0;
        return new TopDocuments(documents, explanations, totalHits, !moreResults, searchToken);
    }

    public void setExplain(boolean explain) {
        this.explain = explain;
    }

    public boolean isExplain() {
        return this.explain;
    }
}

