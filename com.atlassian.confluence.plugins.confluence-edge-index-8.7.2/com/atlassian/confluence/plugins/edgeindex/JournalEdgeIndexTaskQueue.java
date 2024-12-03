/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.api.service.journal.EntryProcessorResult
 *  com.atlassian.confluence.api.service.journal.JournalService
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.fugue.Effect
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Preconditions
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.api.service.journal.EntryProcessorResult;
import com.atlassian.confluence.api.service.journal.JournalService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexTask;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexTaskFactory;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexTaskQueue;
import com.atlassian.confluence.plugins.edgeindex.IndexTaskType;
import com.atlassian.confluence.plugins.edgeindex.IndexableEdge;
import com.atlassian.confluence.plugins.edgeindex.model.Edge;
import com.atlassian.fugue.Effect;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Preconditions;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JournalEdgeIndexTaskQueue
implements EdgeIndexTaskQueue {
    private static final Logger log = LoggerFactory.getLogger(JournalEdgeIndexTaskQueue.class);
    static final JournalIdentifier JOURNAL_ID = new JournalIdentifier("edge_index");
    public static final int BATCH_SIZE = 1000;
    private final JournalService journalService;
    private final EdgeIndexTaskFactory edgeIndexTaskFactory;
    private final TransactionTemplate transactionTemplate;
    private final ObjectMapper jsonMapper = new ObjectMapper();

    public JournalEdgeIndexTaskQueue(JournalService journalService, EdgeIndexTaskFactory edgeIndexTaskFactory, TransactionTemplate transactionTemplate) {
        this.journalService = (JournalService)Preconditions.checkNotNull((Object)journalService);
        this.edgeIndexTaskFactory = (EdgeIndexTaskFactory)Preconditions.checkNotNull((Object)edgeIndexTaskFactory);
        this.transactionTemplate = (TransactionTemplate)Preconditions.checkNotNull((Object)transactionTemplate);
    }

    @Override
    public void enqueue(IndexTaskType indexTaskType, ContentEntityObject target) {
        this.enqueue(indexTaskType, this.toJson(JournalEdgeIndexTaskQueue.buildIndexableEdge(target)));
    }

    @Override
    public void enqueue(IndexTaskType indexTaskType, Edge edge) {
        this.enqueue(indexTaskType, this.toJson(JournalEdgeIndexTaskQueue.buildIndexableEdge(edge)));
    }

    @Override
    public long getSize() {
        return this.journalService.countEntries(JOURNAL_ID);
    }

    private static IndexableEdge buildIndexableEdge(ContentEntityObject target) {
        return new IndexableEdge(null, null, target != null ? target.getId() : 0L, null, null);
    }

    private static IndexableEdge buildIndexableEdge(Edge edge) {
        String edgeId = edge.getEdgeId() != null ? edge.getEdgeId().toString() : null;
        String userKey = edge.getUser() != null ? edge.getUser().getKey().getStringValue() : null;
        ContentEntityObject target = (ContentEntityObject)edge.getTarget();
        long targetId = target != null ? target.getId() : 0L;
        String edgeTypeKey = edge.getEdgeType() != null ? edge.getEdgeType().getKey() : null;
        return new IndexableEdge(edgeId, userKey, targetId, edge.getDate(), edgeTypeKey);
    }

    private String toJson(IndexableEdge indexableEdge) {
        try {
            return this.jsonMapper.writeValueAsString((Object)indexableEdge);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to serialize object", e);
        }
    }

    private void enqueue(IndexTaskType indexTaskType, String message) {
        this.journalService.enqueue(new JournalEntry(JOURNAL_ID, indexTaskType.name(), message));
    }

    @Override
    public void processEntries(Effect<EdgeIndexTask> action) {
        this.transactionTemplate.execute(() -> {
            Integer successCount;
            while ((successCount = this.processEntriesInternal(action)) == 1000) {
            }
            return null;
        });
    }

    private Integer processEntriesInternal(Effect<EdgeIndexTask> action) {
        return (Integer)this.journalService.processNewEntries(JOURNAL_ID, 1000, entries -> {
            int successCount = 0;
            for (JournalEntry entry : entries) {
                try {
                    this.toTask(entry).foreach(action);
                    ++successCount;
                }
                catch (RuntimeException e) {
                    log.warn("Failed to process edge index task for entry '" + entry + "'", (Throwable)e);
                    return EntryProcessorResult.failure((Object)successCount, (long)entry.getId());
                }
            }
            return EntryProcessorResult.success((Object)successCount);
        });
    }

    private Maybe<EdgeIndexTask> toTask(JournalEntry entry) {
        try {
            IndexableEdge edge = (IndexableEdge)this.jsonMapper.readValue(entry.getMessage(), IndexableEdge.class);
            switch (IndexTaskType.valueOf(entry.getType())) {
                case ADD_DOCUMENT: {
                    return this.edgeIndexTaskFactory.createAddDocumentTask(edge);
                }
                case DELETE_DOCUMENT: {
                    return Option.some((Object)this.edgeIndexTaskFactory.createDeleteDocumentTask(edge));
                }
                case DELETE_EDGE_BY_TARGET_ID_AND_USER: {
                    return Option.some((Object)this.edgeIndexTaskFactory.createDeleteEdgeByTargetIdAndUserTask(edge));
                }
                case DELETE_EDGE_TARGETING_DOCUMENT: {
                    return Option.some((Object)this.edgeIndexTaskFactory.createDeleteEdgeTargetingDocumentTask(edge));
                }
                case REINDEX_PERMISSIONS: {
                    return Option.some((Object)this.edgeIndexTaskFactory.createReIndexPermissionsTask(edge));
                }
            }
            throw new IllegalArgumentException("Cannot handle entries with type '" + entry.getType() + "'");
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Failed to parse edge from message '" + entry.getMessage() + "'");
        }
    }
}

