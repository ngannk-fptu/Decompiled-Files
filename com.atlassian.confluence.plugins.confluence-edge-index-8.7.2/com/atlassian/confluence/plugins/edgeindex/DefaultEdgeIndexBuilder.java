/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.like.LikeEntity
 *  com.atlassian.confluence.search.v2.SearchIndexWriter
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Pair
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.joda.time.DateTime
 *  org.joda.time.Period
 *  org.joda.time.ReadablePeriod
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.like.LikeEntity;
import com.atlassian.confluence.plugins.edgeindex.EdgeContentQueries;
import com.atlassian.confluence.plugins.edgeindex.EdgeFactory;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexBuilder;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexManager;
import com.atlassian.confluence.plugins.edgeindex.EdgeSearchIndexAccessor;
import com.atlassian.confluence.plugins.edgeindex.model.Edge;
import com.atlassian.confluence.plugins.edgeindex.servlet.EdgeIndexRebuiltEvent;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Pair;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.ReadablePeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="edgeIndexBuilder")
public class DefaultEdgeIndexBuilder
implements EdgeIndexBuilder {
    private static final Logger log = LoggerFactory.getLogger(DefaultEdgeIndexBuilder.class);
    private final EdgeIndexManager edgeIndexManager;
    private final TransactionTemplate transactionTemplate;
    private final EdgeFactory edgeFactory;
    private final EdgeSearchIndexAccessor edgeSearchIndexAccessor;
    private final EdgeContentQueries edgeContentQueries;
    private final EventPublisher eventPublisher;

    @Autowired
    public DefaultEdgeIndexBuilder(EdgeIndexManager edgeIndexManager, TransactionTemplate transactionTemplate, EdgeFactory edgeFactory, EdgeSearchIndexAccessor edgeSearchIndexAccessor, EdgeContentQueries edgeContentQueries, EventPublisher eventPublisher) {
        this.edgeIndexManager = edgeIndexManager;
        this.transactionTemplate = transactionTemplate;
        this.edgeFactory = edgeFactory;
        this.edgeSearchIndexAccessor = edgeSearchIndexAccessor;
        this.edgeContentQueries = edgeContentQueries;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void rebuild(Period since, EdgeIndexBuilder.RebuildCondition rebuildCondition) {
        if (rebuildCondition == EdgeIndexBuilder.RebuildCondition.ONLY_IF_INDEX_PRESENT && this.getExistingDocumentCount() == 0) {
            log.info("No existing documents in edge index, skipping reindex");
            return;
        }
        Date startDate = new DateTime().minus((ReadablePeriod)since).toDate();
        this.deleteExistingDocuments();
        this.transactionTemplate.execute(() -> {
            List<ContentEntityObject> contentEntities = this.edgeContentQueries.getContentCreatedSince(startDate);
            this.edgeSearchIndexAccessor.withBatchUpdate(() -> {
                this.addContentToIndex(contentEntities);
                this.addLikesToIndex(startDate);
            });
            return null;
        });
        this.eventPublisher.publish((Object)new EdgeIndexRebuiltEvent(since));
    }

    private int getExistingDocumentCount() {
        return this.edgeSearchIndexAccessor.numDocs();
    }

    private void deleteExistingDocuments() {
        this.edgeSearchIndexAccessor.execute(SearchIndexWriter::deleteAll);
    }

    private void addLikesToIndex(Date startDate) {
        List<Pair<ContentEntityObject, LikeEntity>> likes = this.edgeContentQueries.getLikesSince(startDate);
        likes.forEach(like -> {
            ContentEntityObject contentEntity = (ContentEntityObject)like.left();
            LikeEntity likeEntity = (LikeEntity)like.right();
            Edge likeEdge = this.edgeFactory.getLikeEdge(likeEntity.getUser(), contentEntity, likeEntity.getCreationDate());
            this.edgeIndexManager.index(likeEdge);
        });
    }

    private void addContentToIndex(List<ContentEntityObject> contentEntities) {
        for (ContentEntityObject contentEntity : contentEntities) {
            try {
                Edge edge;
                if (!this.edgeFactory.canBuildCreatEdge(contentEntity) || (edge = this.edgeFactory.getCreateEdge(contentEntity)) == null) continue;
                this.edgeIndexManager.index(edge);
            }
            catch (Exception e) {
                log.debug("Error building edge for: " + contentEntity);
            }
        }
    }
}

