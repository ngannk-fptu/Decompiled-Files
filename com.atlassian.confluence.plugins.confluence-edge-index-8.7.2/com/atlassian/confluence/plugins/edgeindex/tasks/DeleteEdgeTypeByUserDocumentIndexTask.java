/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchIndexWriter
 *  com.atlassian.confluence.search.v2.lucene.SearchIndex
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.plugins.edgeindex.tasks;

import com.atlassian.confluence.plugins.edgeindex.EdgeIndexSchema;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexTask;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Preconditions;
import java.io.IOException;

public class DeleteEdgeTypeByUserDocumentIndexTask
implements EdgeIndexTask {
    private final String edgeTypeKey;
    private final String targetId;
    private final UserKey userKey;

    public DeleteEdgeTypeByUserDocumentIndexTask(String edgeTypeKey, String targetId, UserKey userKey) {
        this.edgeTypeKey = (String)Preconditions.checkNotNull((Object)edgeTypeKey);
        this.targetId = (String)Preconditions.checkNotNull((Object)targetId);
        this.userKey = userKey;
    }

    public String getDescription() {
        return String.format("Delete like edge on document '%s' from index.", this.targetId);
    }

    public SearchIndex getSearchIndex() {
        return SearchIndex.CUSTOM;
    }

    public void perform(SearchIndexWriter searchIndexWriter) throws IOException {
        BooleanQuery.Builder booleanQuery = BooleanQuery.builder();
        booleanQuery.addMust((Object)new TermQuery("edge.type", this.edgeTypeKey));
        booleanQuery.addMust((Object)new TermQuery(EdgeIndexSchema.EDGE_TARGET_ID, this.targetId));
        booleanQuery.addMust((Object)new TermQuery(EdgeIndexSchema.EDGE_USERKEY, this.userKey == null ? "" : this.userKey.getStringValue()));
        searchIndexWriter.delete(booleanQuery.build());
    }
}

