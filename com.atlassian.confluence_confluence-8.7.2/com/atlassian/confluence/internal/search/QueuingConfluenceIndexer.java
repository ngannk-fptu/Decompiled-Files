/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.bonnie.Searchable
 *  com.google.common.base.Suppliers
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.search.ChangeIndexer;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.ConfluenceIndexer;
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

@LuceneIndependent
@Internal
public class QueuingConfluenceIndexer
implements ConfluenceIndexer {
    private static final Logger log = LoggerFactory.getLogger(QueuingConfluenceIndexer.class);
    private IndexTaskQueue<ConfluenceIndexTask> taskQueue;
    private IndexTaskFactoryInternal indexTaskFactory;
    private IndexerControl indexerControl;
    private ChangeIndexer changeIndexer;
    private SearchIndexAccessor searchIndexAccessor;
    private Supplier<ConfluenceIndexer> syncIndexer = Suppliers.memoize(() -> new InternalConfluenceIndexer(this.indexerControl, this.indexTaskFactory, indexTask -> this.searchIndexAccessor.execute(indexTask::perform), this.changeIndexer.synchronous()){

        @Override
        public ConfluenceIndexer synchronous() {
            return this;
        }
    });
    private Supplier<ConfluenceIndexer> asyncIndexer = Suppliers.memoize(() -> new InternalConfluenceIndexer(this.indexerControl, this.indexTaskFactory, indexTask -> this.taskQueue.enqueue((ConfluenceIndexTask)indexTask), this.changeIndexer){

        @Override
        public ConfluenceIndexer synchronous() {
            return QueuingConfluenceIndexer.this.syncIndexer.get();
        }
    });

    @Override
    public void index(Searchable searchable) {
        if (searchable == null) {
            return;
        }
        this.asyncIndexer.get().index(searchable);
    }

    @Override
    public void unIndex(Searchable searchable) {
        if (searchable == null) {
            return;
        }
        this.asyncIndexer.get().unIndex(searchable);
    }

    @Override
    public void reIndex(Searchable searchable) {
        if (searchable == null) {
            return;
        }
        this.asyncIndexer.get().reIndex(searchable);
    }

    @Override
    public void reIndexExcludingDependents(Searchable searchable) {
        if (searchable == null) {
            return;
        }
        this.asyncIndexer.get().reIndexExcludingDependents(searchable);
    }

    @Override
    public void unIndexSpace(Space space) {
        this.asyncIndexer.get().unIndexSpace(space);
    }

    @Override
    public void reindexUsersInGroup(String groupName) {
        this.asyncIndexer.get().reindexUsersInGroup(groupName);
    }

    @Override
    public void unIndexIncludingDependents(Searchable searchable) {
        if (searchable == null) {
            return;
        }
        this.asyncIndexer.get().unIndexIncludingDependents(searchable);
    }

    @Override
    public void indexIncludingDependents(Searchable searchable) {
        if (searchable == null) {
            return;
        }
        this.asyncIndexer.get().indexIncludingDependents(searchable);
    }

    @Override
    public ConfluenceIndexer synchronous() {
        return this.asyncIndexer.get().synchronous();
    }

    @VisibleForTesting
    public void setSyncIndexer(Supplier<ConfluenceIndexer> syncIndexer) {
        this.syncIndexer = syncIndexer;
    }

    @VisibleForTesting
    public void setAsyncIndexer(Supplier<ConfluenceIndexer> asyncIndexer) {
        this.asyncIndexer = asyncIndexer;
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

    public void setChangeIndexer(ChangeIndexer changeIndexer) {
        this.changeIndexer = changeIndexer;
    }

    public void setSearchIndexAccessor(SearchIndexAccessor searchIndexAccessor) {
        this.searchIndexAccessor = searchIndexAccessor;
    }

    private static abstract class InternalConfluenceIndexer
    implements ConfluenceIndexer {
        private final IndexerControl indexerControl;
        private final IndexTaskFactoryInternal indexTaskFactory;
        private final Consumer<ConfluenceIndexTask> taskAction;
        private final ChangeIndexer changeIndexer;

        InternalConfluenceIndexer(IndexerControl indexerControl, IndexTaskFactoryInternal indexTaskFactory, Consumer<ConfluenceIndexTask> taskAction, ChangeIndexer changeIndexer) {
            this.indexTaskFactory = Objects.requireNonNull(indexTaskFactory, "indexTaskFactory");
            this.indexerControl = Objects.requireNonNull(indexerControl, "indexerControl");
            this.taskAction = Objects.requireNonNull(taskAction, "taskAction");
            this.changeIndexer = Objects.requireNonNull(changeIndexer, "changeIndexer");
        }

        @Override
        public void unIndexSpace(Space space) {
            if (this.indexerControl.indexingEnabled()) {
                log.trace("Queuing unindex space task: {}", (Object)space);
                this.changeIndexer.unIndexSpace(space);
                this.taskAction.accept(this.indexTaskFactory.createUnIndexSpaceContentIndexTask(space));
            } else {
                log.debug("Not queueing unindex space task: {}", (Object)space);
            }
        }

        @Override
        public void reindexUsersInGroup(String groupName) {
            if (this.indexerControl.indexingEnabled()) {
                log.trace("Queuing reindex group task: {}", (Object)groupName);
                this.changeIndexer.reindexUsersInGroup(groupName);
                this.taskAction.accept(this.indexTaskFactory.createReindexUsersInGroupContentTask(groupName));
            } else {
                log.debug("Not queueing reindex group task: {}", (Object)groupName);
            }
        }

        @Override
        public void unIndexIncludingDependents(Searchable searchable) {
            if (this.indexerControl.indexingDisabled()) {
                log.debug("Not queuing unindex with dependents: {}", (Object)searchable);
                return;
            }
            for (Object o : searchable.getSearchableDependants()) {
                Searchable dependent = (Searchable)o;
                this.unIndexIncludingDependents(dependent);
            }
            this.unIndex(searchable);
        }

        @Override
        public void unIndex(Searchable searchable) {
            if (this.indexerControl.indexingDisabled()) {
                log.debug("Not queuing unindex: {}", (Object)searchable);
                return;
            }
            this.changeIndexer.unIndex(searchable);
            log.trace("Queueing searchable for un-indexing: {}", (Object)searchable);
            this.taskAction.accept(this.indexTaskFactory.createDeleteDocumentTask(searchable));
        }

        @Override
        public void reIndex(Searchable searchable) {
            this.doReindex(searchable, true);
        }

        @Override
        public void reIndexExcludingDependents(Searchable searchable) {
            this.doReindex(searchable, false);
        }

        private void doReindex(Searchable searchable, boolean includeDependents) {
            if (this.indexerControl.indexingDisabled()) {
                log.debug("shouldIndex = false; Not queuing re-indexing: {}", (Object)searchable);
                return;
            }
            this.changeIndexer.reIndex(searchable);
            if (searchable.isIndexable()) {
                log.trace("Queuing searchable for re-indexing: {}", (Object)searchable);
                this.taskAction.accept(this.indexTaskFactory.createUpdateDocumentTask(searchable, includeDependents));
            } else {
                log.debug("Not queuing searchable for re-indexing: {}", (Object)searchable);
            }
        }

        @Override
        public void indexIncludingDependents(Searchable searchable) {
            if (this.indexerControl.indexingDisabled()) {
                log.debug("Not queuing index with dependents: {}", (Object)searchable);
                return;
            }
            for (Object o : searchable.getSearchableDependants()) {
                Searchable dependent = (Searchable)o;
                this.indexIncludingDependents(dependent);
            }
            this.index(searchable);
        }

        @Override
        public void index(Searchable searchable) {
            if (this.indexerControl.indexingDisabled()) {
                log.debug("shouldIndex = false; Not queuing index: {}", (Object)searchable);
                return;
            }
            this.changeIndexer.index(searchable);
            if (searchable.isIndexable()) {
                log.trace("Queueing searchable for indexing: {}", (Object)searchable);
                this.taskAction.accept(this.indexTaskFactory.createUpdateDocumentTask(searchable));
            } else {
                log.debug("Not queuing searchable for indexing: {}", (Object)searchable);
            }
        }
    }
}

