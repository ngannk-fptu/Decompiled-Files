/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.AtlassianDocument
 *  com.atlassian.confluence.search.v2.SearchIndexWriter
 *  com.atlassian.confluence.search.v2.lucene.SearchIndex
 */
package com.atlassian.confluence.plugins.edgeindex.tasks;

import com.atlassian.confluence.plugins.edgeindex.EdgeIndexTask;
import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.io.IOException;

public class AddEdgeDocumentIndexTask
implements EdgeIndexTask {
    private final AtlassianDocument document;

    public AddEdgeDocumentIndexTask(AtlassianDocument document) {
        this.document = document;
    }

    public SearchIndex getSearchIndex() {
        return SearchIndex.CUSTOM;
    }

    public void perform(SearchIndexWriter writer) throws IOException {
        writer.add(this.document);
    }

    public String getDescription() {
        return String.format("Add Edge document to index: '%s'", this.document.toString());
    }
}

