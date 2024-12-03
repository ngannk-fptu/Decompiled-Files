/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexTask;
import com.atlassian.confluence.plugins.edgeindex.IndexableEdge;
import com.atlassian.confluence.plugins.edgeindex.model.Edge;
import com.atlassian.fugue.Maybe;
import com.atlassian.sal.api.user.UserKey;
import java.util.Date;

public interface EdgeIndexTaskFactory {
    public EdgeIndexTask createAddDocumentTask(Edge var1);

    public Maybe<EdgeIndexTask> createAddDocumentTask(IndexableEdge var1);

    public EdgeIndexTask createAddDocumentTask(String var1, UserKey var2, ContentEntityObject var3, Date var4, String var5);

    public Maybe<EdgeIndexTask> createDeleteDocumentTask(Edge var1);

    public EdgeIndexTask createDeleteDocumentTask(IndexableEdge var1);

    public EdgeIndexTask createDeleteDocumentTask(String var1);

    public EdgeIndexTask createDeleteEdgeTargetingDocumentTask(Edge var1);

    public EdgeIndexTask createDeleteEdgeTargetingDocumentTask(IndexableEdge var1);

    public EdgeIndexTask createDeleteEdgeTargetingDocumentTask(String var1);

    public EdgeIndexTask createReIndexPermissionsTask(ContentEntityObject var1);

    public EdgeIndexTask createReIndexPermissionsTask(IndexableEdge var1);

    public EdgeIndexTask createDeleteEdgeByTargetIdAndUserTask(Edge var1);

    public EdgeIndexTask createDeleteEdgeByTargetIdAndUserTask(IndexableEdge var1);
}

