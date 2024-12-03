/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.search.v2.AtlassianDocument
 *  com.atlassian.confluence.search.v2.SearchIndexWriter
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex.indexaction;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.tasklist.ao.AOInlineTask;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexaction.SearchIndexActionWithNumberOfIds;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.lucene.InlineTaskSearchDocumentFactory;
import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import java.io.IOException;

public class AddSearchDocumentForInlineTaskAction
implements SearchIndexActionWithNumberOfIds {
    final InlineTaskSearchDocumentFactory inlineTaskSearchDocumentFactory;
    final ContentEntityObject contentEntityObject;
    final AOInlineTask task;
    final AtlassianDocument atlassianDocument;

    public AddSearchDocumentForInlineTaskAction(InlineTaskSearchDocumentFactory inlineTaskSearchDocumentFactory, ContentEntityObject contentEntityObject, AOInlineTask task) {
        this.inlineTaskSearchDocumentFactory = inlineTaskSearchDocumentFactory;
        this.contentEntityObject = contentEntityObject;
        this.task = task;
        this.atlassianDocument = inlineTaskSearchDocumentFactory.buildDocument(contentEntityObject, task);
    }

    public void accept(SearchIndexWriter searchIndexWriter) throws IOException {
        searchIndexWriter.add(this.atlassianDocument);
    }

    @Override
    public int getNumberOfIds() {
        return 1;
    }
}

