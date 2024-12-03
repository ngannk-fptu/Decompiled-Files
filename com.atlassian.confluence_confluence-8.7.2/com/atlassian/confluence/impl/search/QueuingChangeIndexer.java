/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.bonnie.Searchable
 *  com.google.common.base.Suppliers
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.internal.search.ChangeDocumentIndexPolicy;
import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.search.ChangeIndexer;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.IndexTaskQueue;
import com.atlassian.confluence.search.IndexerControl;
import com.atlassian.confluence.search.v2.SearchIndexAccessor;
import com.atlassian.confluence.spaces.Space;
import com.google.common.base.Suppliers;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueuingChangeIndexer
implements ChangeIndexer {
    private IndexTaskQueue taskQueue;
    private IndexTaskFactoryInternal indexTaskFactory;
    private IndexerControl indexerControl;
    private SearchIndexAccessor searchIndexAccessor;
    private Supplier<ChangeIndexer> syncIndexer = Suppliers.memoize(() -> new InternalChangeIndexer(this.indexerControl, this.indexTaskFactory, indexTask -> this.searchIndexAccessor.execute(indexTask::perform)){

        @Override
        public ChangeIndexer synchronous() {
            return this;
        }
    });
    private Supplier<ChangeIndexer> asyncIndexer = Suppliers.memoize(() -> new InternalChangeIndexer(this.indexerControl, this.indexTaskFactory, this.taskQueue::enqueue){

        @Override
        public ChangeIndexer synchronous() {
            return QueuingChangeIndexer.this.syncIndexer.get();
        }
    });

    public void index(Searchable searchable) {
        if (searchable == null) {
            return;
        }
        this.asyncIndexer.get().index(searchable);
    }

    public void unIndex(Searchable searchable) {
        if (searchable == null) {
            return;
        }
        this.asyncIndexer.get().unIndex(searchable);
    }

    @Override
    public void unIndexSpace(Space space) {
        this.asyncIndexer.get().unIndexSpace(space);
    }

    @Override
    public void reindexUsersInGroup(String groupName) {
        this.asyncIndexer.get().reindexUsersInGroup(groupName);
    }

    public void reIndex(Searchable searchable) {
        if (searchable == null) {
            return;
        }
        this.asyncIndexer.get().reIndex(searchable);
    }

    @Override
    public void reIndexAllVersions(Searchable searchable) {
        if (searchable == null) {
            return;
        }
        this.asyncIndexer.get().reIndexAllVersions(searchable);
    }

    @Override
    public ChangeIndexer synchronous() {
        return this.asyncIndexer.get().synchronous();
    }

    public void setTaskQueue(IndexTaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public void setIndexTaskFactory(IndexTaskFactoryInternal indexTaskFactory) {
        this.indexTaskFactory = indexTaskFactory;
    }

    public void setIndexerControl(IndexerControl indexerControl) {
        this.indexerControl = indexerControl;
    }

    public void setSearchIndexAccessor(SearchIndexAccessor searchIndexAccessor) {
        this.searchIndexAccessor = searchIndexAccessor;
    }

    @VisibleForTesting
    public void setAsyncIndexer(Supplier<ChangeIndexer> asyncIndexer) {
        this.asyncIndexer = asyncIndexer;
    }

    @VisibleForTesting
    public void setSyncIndexer(Supplier<ChangeIndexer> syncIndexer) {
        this.syncIndexer = syncIndexer;
    }

    private static abstract class InternalChangeIndexer
    implements ChangeIndexer {
        private static final Logger log = LoggerFactory.getLogger(InternalChangeIndexer.class);
        private final IndexerControl indexerControl;
        private final IndexTaskFactoryInternal indexTaskFactory;
        private final Consumer<ConfluenceIndexTask> taskAction;

        InternalChangeIndexer(IndexerControl indexerControl, IndexTaskFactoryInternal indexTaskFactory, Consumer<ConfluenceIndexTask> taskAction) {
            this.indexerControl = Objects.requireNonNull(indexerControl, "indexerControl");
            this.indexTaskFactory = Objects.requireNonNull(indexTaskFactory, "indexTaskFactory");
            this.taskAction = Objects.requireNonNull(taskAction, "taskAction");
        }

        public void index(Searchable searchable) {
            if (this.indexerControl.indexingDisabled()) {
                log.debug("Not queuing up index for : {}", (Object)searchable.getId());
                return;
            }
            if (ChangeDocumentIndexPolicy.shouldIndex(searchable)) {
                log.trace("Queueing change document for re-indexing: {}", (Object)searchable.getId());
                this.taskAction.accept(this.indexTaskFactory.createAddChangeDocumentTask(searchable));
            } else {
                log.debug("Not queuing change document for re-indexing: {}", (Object)searchable.getId());
            }
        }

        public void unIndex(Searchable searchable) {
            if (this.indexerControl.indexingDisabled()) {
                log.debug("Not queueing change document for un-indexing: {}", (Object)searchable.getId());
                return;
            }
            if (ChangeDocumentIndexPolicy.shouldUnIndex(searchable)) {
                if (searchable instanceof Versioned && !((Versioned)searchable).isLatestVersion()) {
                    log.debug("Not queueing change document un-indexing for old version: {}", (Object)searchable.getId());
                    return;
                }
                log.trace("Queueing change document for un-indexing: {}", (Object)searchable);
                this.taskAction.accept(this.indexTaskFactory.createDeleteChangeDocumentsIndexTask(searchable));
            } else {
                log.debug("Not queueing change document for un-indexing: {}", (Object)searchable.getId());
            }
        }

        @Override
        public void unIndexSpace(Space space) {
            if (this.indexerControl.indexingEnabled()) {
                log.trace("Queuing unindex space task: {}", (Object)space);
                this.taskAction.accept(this.indexTaskFactory.createUnIndexSpaceChangeIndexTask(space));
            } else {
                log.debug("Not queueing unindex space task: {}", (Object)space);
            }
        }

        @Override
        public void reindexUsersInGroup(String groupName) {
            if (this.indexerControl.indexingEnabled()) {
                log.trace("Queuing reindex group task: {}", (Object)groupName);
                this.taskAction.accept(this.indexTaskFactory.createReindexUsersInGroupChangeTask(groupName));
            } else {
                log.debug("Not queueing reindex group task: {}", (Object)groupName);
            }
        }

        public void reIndex(Searchable searchable) {
            if (this.indexerControl.indexingDisabled()) {
                log.debug("Not queuing re-index for: {}", (Object)searchable.getId());
                return;
            }
            if (ChangeDocumentIndexPolicy.shouldIndex(searchable)) {
                log.trace("Queuing re-index for: {}", (Object)searchable.getId());
                this.taskAction.accept(this.indexTaskFactory.createAddChangeDocumentTask(searchable));
            } else {
                log.debug("Not queuing re-index  for: {}", (Object)searchable.getId());
            }
        }

        @Override
        public void reIndexAllVersions(Searchable searchable) {
            if (!(searchable instanceof Versioned)) {
                throw new IllegalArgumentException("changeable not versioned: " + searchable);
            }
            if (this.indexerControl.indexingDisabled()) {
                log.debug("Not queuing up reindex of all versions for : {}", (Object)searchable.getId());
                return;
            }
            if (!((Versioned)searchable).isLatestVersion()) {
                log.debug("Trying to reindex a non-latest version of {}, not performing reindex content", (Object)searchable.getId());
                return;
            }
            if (ChangeDocumentIndexPolicy.shouldIndex(searchable)) {
                log.trace("Queuing re-index of all versions for: {}", (Object)searchable.getId());
                this.taskAction.accept(this.indexTaskFactory.createRebuildChangeDocumentsIndexTask(searchable));
            } else {
                log.debug("Not queuing re-index of all versions for: {}", (Object)searchable.getId());
            }
        }
    }
}

