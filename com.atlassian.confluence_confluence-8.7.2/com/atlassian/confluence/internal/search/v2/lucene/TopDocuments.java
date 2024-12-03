/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.document.Document
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import java.util.Collections;
import java.util.List;
import org.apache.lucene.document.Document;

public final class TopDocuments {
    public static final TopDocuments EMPTY = new TopDocuments(Collections.emptyList(), 0, true, -1L);
    private final List<Document> documents;
    private final List<String> explanations;
    private final int totalHits;
    private final boolean isLastPage;
    private final long searchToken;

    public TopDocuments(List<Document> documents, int totalHits, boolean isLastPage, long searchToken) {
        this(documents, Collections.emptyList(), totalHits, isLastPage, searchToken);
    }

    public TopDocuments(List<Document> documents, List<String> explanations, int totalHits, boolean isLastPage, long searchToken) {
        this.documents = documents == null ? Collections.emptyList() : documents;
        this.explanations = explanations == null ? Collections.emptyList() : explanations;
        this.totalHits = totalHits;
        this.isLastPage = isLastPage;
        this.searchToken = searchToken;
    }

    public List<Document> getDocuments() {
        return this.documents;
    }

    public List<String> getExplanations() {
        return this.explanations;
    }

    public int getTotalHits() {
        return this.totalHits;
    }

    public boolean isLastPage() {
        return this.isLastPage;
    }

    public long getSearchToken() {
        return this.searchToken;
    }
}

