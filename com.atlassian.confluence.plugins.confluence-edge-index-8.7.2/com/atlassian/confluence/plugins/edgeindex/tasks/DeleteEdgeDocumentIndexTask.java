/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchIndexWriter
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.lucene.SearchIndex
 *  com.atlassian.confluence.search.v2.query.TermQuery
 */
package com.atlassian.confluence.plugins.edgeindex.tasks;

import com.atlassian.confluence.plugins.edgeindex.EdgeIndexTask;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.io.IOException;

public class DeleteEdgeDocumentIndexTask
implements EdgeIndexTask {
    private final String edgeId;

    public DeleteEdgeDocumentIndexTask(String edgeId) {
        this.edgeId = edgeId;
    }

    public String getDescription() {
        return String.format("Delete document with id '%s' from edge index.", this.edgeId);
    }

    public SearchIndex getSearchIndex() {
        return SearchIndex.CUSTOM;
    }

    public void perform(SearchIndexWriter searchIndexWriter) throws IOException {
        searchIndexWriter.delete((SearchQuery)new TermQuery("edge.id", this.edgeId));
    }
}

