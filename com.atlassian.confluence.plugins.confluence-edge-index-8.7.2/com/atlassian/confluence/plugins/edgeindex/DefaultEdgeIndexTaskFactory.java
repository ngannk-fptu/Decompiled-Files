/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.InheritedContentPermissionManager
 *  com.atlassian.confluence.search.v2.AtlassianDocument
 *  com.atlassian.confluence.search.v2.ContentPermissionCalculator
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.InheritedContentPermissionManager;
import com.atlassian.confluence.plugins.edgeindex.EdgeDocumentFactory;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexTask;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexTaskFactory;
import com.atlassian.confluence.plugins.edgeindex.EdgeSearchIndexAccessor;
import com.atlassian.confluence.plugins.edgeindex.IndexableEdge;
import com.atlassian.confluence.plugins.edgeindex.model.Edge;
import com.atlassian.confluence.plugins.edgeindex.tasks.AddEdgeDocumentIndexTask;
import com.atlassian.confluence.plugins.edgeindex.tasks.DeleteEdgeDocumentIndexTask;
import com.atlassian.confluence.plugins.edgeindex.tasks.DeleteEdgeTargetingDocumentIndexTask;
import com.atlassian.confluence.plugins.edgeindex.tasks.DeleteEdgeTypeByUserDocumentIndexTask;
import com.atlassian.confluence.plugins.edgeindex.tasks.ReIndexPermissionsIndexTask;
import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.confluence.search.v2.ContentPermissionCalculator;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Preconditions;
import java.util.Date;

public class DefaultEdgeIndexTaskFactory
implements EdgeIndexTaskFactory {
    private final EdgeDocumentFactory edgeDocumentFactory;
    private final ContentEntityManager contentEntityManager;
    private final TransactionTemplate txTemplate;
    private final EdgeSearchIndexAccessor edgeSearchIndexAccessor;
    private final ContentPermissionCalculator contentPermissionCalculator;

    public DefaultEdgeIndexTaskFactory(EdgeDocumentFactory edgeDocumentFactory, InheritedContentPermissionManager inheritedContentPermissionManager, ContentEntityManager contentEntityManager, TransactionTemplate txTemplate, EdgeSearchIndexAccessor edgeSearchIndexAccessor, ContentPermissionCalculator contentPermissionCalculator) {
        this.edgeDocumentFactory = edgeDocumentFactory;
        this.txTemplate = txTemplate;
        this.contentEntityManager = contentEntityManager;
        this.edgeSearchIndexAccessor = edgeSearchIndexAccessor;
        this.contentPermissionCalculator = contentPermissionCalculator;
    }

    @Override
    public EdgeIndexTask createAddDocumentTask(Edge edge) {
        Preconditions.checkArgument((boolean)(edge.getTarget() instanceof ContentEntityObject), (Object)("this target object is not supported: " + edge.getTarget()));
        UserKey userKey = edge.getUser() != null ? edge.getUser().getKey() : null;
        ContentEntityObject target = (ContentEntityObject)edge.getTarget();
        String edgeId = edge.getEdgeId() != null ? edge.getEdgeId().toString() : null;
        String edgeTypeKey = edge.getEdgeType() != null ? edge.getEdgeType().getKey() : null;
        return this.createAddDocumentTask(edgeId, userKey, target, edge.getDate(), edgeTypeKey);
    }

    @Override
    public Maybe<EdgeIndexTask> createAddDocumentTask(IndexableEdge edge) {
        ContentEntityObject target = this.contentEntityManager.getById(edge.getTargetId().longValue());
        if (target == null) {
            return Option.none();
        }
        UserKey userKey = edge.getUserKey() != null ? new UserKey(edge.getUserKey()) : null;
        return Option.some((Object)this.createAddDocumentTask(edge.getEdgeId(), userKey, target, edge.getDate(), edge.getTypeKey()));
    }

    @Override
    public EdgeIndexTask createAddDocumentTask(String edgeId, UserKey userKey, ContentEntityObject target, Date date, String edgeTypeKey) {
        AtlassianDocument document = this.edgeDocumentFactory.buildDocument(edgeId, userKey, target, date, edgeTypeKey);
        return new AddEdgeDocumentIndexTask(document);
    }

    @Override
    public Maybe<EdgeIndexTask> createDeleteDocumentTask(Edge edge) {
        return edge.getEdgeId() != null ? Option.some((Object)this.createDeleteDocumentTask(edge.getEdgeId().toString())) : Option.none();
    }

    @Override
    public EdgeIndexTask createDeleteDocumentTask(IndexableEdge edge) {
        return this.createDeleteDocumentTask(edge.getEdgeId());
    }

    @Override
    public EdgeIndexTask createDeleteDocumentTask(String edgeId) {
        return new DeleteEdgeDocumentIndexTask(edgeId);
    }

    @Override
    public EdgeIndexTask createDeleteEdgeTargetingDocumentTask(Edge edge) {
        ContentEntityObject target = (ContentEntityObject)edge.getTarget();
        return this.createDeleteEdgeTargetingDocumentTask(target.getIdAsString());
    }

    @Override
    public EdgeIndexTask createDeleteEdgeTargetingDocumentTask(IndexableEdge edge) {
        return this.createDeleteEdgeTargetingDocumentTask(edge.getTargetId().toString());
    }

    @Override
    public EdgeIndexTask createDeleteEdgeTargetingDocumentTask(String targetId) {
        return new DeleteEdgeTargetingDocumentIndexTask(targetId);
    }

    @Override
    public EdgeIndexTask createReIndexPermissionsTask(ContentEntityObject ceo) {
        return new ReIndexPermissionsIndexTask(ceo.getIdAsString(), this.contentEntityManager, this.txTemplate, this.edgeSearchIndexAccessor, this.contentPermissionCalculator);
    }

    @Override
    public EdgeIndexTask createReIndexPermissionsTask(IndexableEdge edge) {
        return new ReIndexPermissionsIndexTask(edge.getTargetId().toString(), this.contentEntityManager, this.txTemplate, this.edgeSearchIndexAccessor, this.contentPermissionCalculator);
    }

    @Override
    public EdgeIndexTask createDeleteEdgeByTargetIdAndUserTask(Edge edge) {
        ContentEntityObject target = (ContentEntityObject)edge.getTarget();
        UserKey userKey = edge.getUser() != null ? edge.getUser().getKey() : null;
        return new DeleteEdgeTypeByUserDocumentIndexTask(edge.getEdgeType().getKey(), target.getIdAsString(), userKey);
    }

    @Override
    public EdgeIndexTask createDeleteEdgeByTargetIdAndUserTask(IndexableEdge edge) {
        return new DeleteEdgeTypeByUserDocumentIndexTask(edge.getTypeKey(), edge.getTargetId().toString(), new UserKey(edge.getUserKey()));
    }
}

