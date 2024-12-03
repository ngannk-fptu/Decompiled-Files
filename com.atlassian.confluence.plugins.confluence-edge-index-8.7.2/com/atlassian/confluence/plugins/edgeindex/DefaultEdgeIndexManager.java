/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.VersionChildOwnerPolicy
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.VersionChildOwnerPolicy;
import com.atlassian.confluence.plugins.edgeindex.EdgeFactory;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexManager;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexTaskQueue;
import com.atlassian.confluence.plugins.edgeindex.IndexTaskType;
import com.atlassian.confluence.plugins.edgeindex.model.Edge;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeType;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={EdgeIndexManager.class})
public class DefaultEdgeIndexManager
implements EdgeIndexManager {
    private final EdgeIndexTaskQueue taskQueue;
    private final EdgeFactory edgeFactory;
    private final ContentEntityManager contentEntityManager;

    @Autowired
    public DefaultEdgeIndexManager(EdgeIndexTaskQueue taskQueue, EdgeFactory edgeFactory, ContentEntityManager contentEntityManager) {
        this.taskQueue = taskQueue;
        this.edgeFactory = edgeFactory;
        this.contentEntityManager = contentEntityManager;
    }

    @Override
    public void index(Edge edge) {
        this.taskQueue.enqueue(IndexTaskType.ADD_DOCUMENT, edge);
    }

    @Override
    public void unIndex(Edge edge) {
        if (edge.getTarget() == null || !(edge.getTarget() instanceof ContentEntityObject)) {
            return;
        }
        EdgeType edgeType = edge.getEdgeType();
        if (edge.getEdgeId() != null) {
            this.taskQueue.enqueue(IndexTaskType.DELETE_DOCUMENT, edge);
        } else if (edgeType.getDeletionMode() == EdgeType.DeletionMode.BY_TARGET_ID_AND_USER) {
            this.taskQueue.enqueue(IndexTaskType.DELETE_EDGE_BY_TARGET_ID_AND_USER, edge);
        } else if (edgeType.getDeletionMode() == EdgeType.DeletionMode.BY_TARGET_ID) {
            this.taskQueue.enqueue(IndexTaskType.DELETE_EDGE_TARGETING_DOCUMENT, edge);
        } else {
            throw new UnsupportedOperationException("Cannot unindex edge: " + edge + ". This type not just supported.");
        }
    }

    @Override
    public void reIndexPermissions(Object target) {
        if (!(target instanceof ContentEntityObject)) {
            return;
        }
        ContentEntityObject content = (ContentEntityObject)target;
        this.taskQueue.enqueue(IndexTaskType.REINDEX_PERMISSIONS, content);
    }

    public void contentEntityRemoved(boolean deletingVersion, ContentEntityObject contentEntity) {
        this.unindexChildren(deletingVersion, contentEntity);
        if (this.edgeFactory.canBuildCreatEdge(contentEntity)) {
            this.unIndex(this.edgeFactory.getCreateEdge(contentEntity));
        }
        this.taskQueue.enqueue(IndexTaskType.DELETE_EDGE_TARGETING_DOCUMENT, contentEntity);
    }

    @Override
    public void contentEntityRemoved(ContentEntityObject contentEntity) {
        this.contentEntityRemoved(false, contentEntity);
    }

    @Override
    public void contentEntityVersionRemoved(ContentEntityObject contentEntity) {
        this.contentEntityRemoved(true, contentEntity);
    }

    private void contentEntityRemoved(List<? extends ContentEntityObject> contentEntityObjects) {
        for (ContentEntityObject contentEntityObject : contentEntityObjects) {
            this.contentEntityRemoved(contentEntityObject);
        }
    }

    private void unindexChildren(boolean deletingVersion, ContentEntityObject contentEntity) {
        this.unindexChildrenAttachments(deletingVersion, contentEntity);
        this.unindexChildrenComments(deletingVersion, contentEntity);
    }

    private void unindexChildrenAttachments(boolean deletingVersion, ContentEntityObject contentEntity) {
        if (!deletingVersion && contentEntity.getVersionChildPolicy(ContentType.ATTACHMENT) == VersionChildOwnerPolicy.originalVersion) {
            ContentEntityObject version = (ContentEntityObject)contentEntity.getLatestVersion();
            while (version != null) {
                this.contentEntityRemoved(version.getAttachments());
                version = this.contentEntityManager.getPreviousVersion(version);
            }
        } else {
            this.contentEntityRemoved(contentEntity.getAttachments());
        }
    }

    private void unindexChildrenComments(boolean deletingVersion, ContentEntityObject contentEntity) {
        if (!deletingVersion && contentEntity.getVersionChildPolicy(ContentType.COMMENT) == VersionChildOwnerPolicy.originalVersion) {
            ContentEntityObject version = (ContentEntityObject)contentEntity.getLatestVersion();
            while (version != null) {
                this.contentEntityRemoved(version.getComments());
                version = this.contentEntityManager.getPreviousVersion(version);
            }
        } else {
            this.contentEntityRemoved(contentEntity.getComments());
        }
    }
}

