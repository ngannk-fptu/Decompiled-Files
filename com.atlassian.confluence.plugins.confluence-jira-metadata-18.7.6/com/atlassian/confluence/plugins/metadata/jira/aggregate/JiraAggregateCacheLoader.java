/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.metadata.jira.aggregate;

import com.atlassian.confluence.plugins.metadata.jira.aggregate.JiraAggregateCacheStore;
import com.atlassian.confluence.plugins.metadata.jira.aggregate.JiraAggregateProvider;
import com.atlassian.confluence.plugins.metadata.jira.helper.JiraMetadataErrorHelper;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraAggregate;
import com.atlassian.confluence.plugins.metadata.jira.util.JiraAggregates;
import com.atlassian.util.concurrent.ThreadFactories;
import com.google.common.collect.Maps;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JiraAggregateCacheLoader {
    private static final Logger log = LoggerFactory.getLogger(JiraAggregateCacheLoader.class);
    private static final int LOADING_TIMEOUT_SECS = Integer.getInteger("jira.metadata.aggregate.cacheloader.timeout.secs", 3);
    private static final String THREAD_NAME_PREFIX = "JIRAMetadataPlugin_CacheLoader";
    private static final int EXECUTOR_POOL_SIZE = Integer.getInteger("jira.metadata.aggregate.cacheloader.executor.pool.size", 5);
    private static final int EXECUTOR_TASK_QUEUE_SIZE = Integer.getInteger("jira.metadata.aggregate.cacheloader.executor.queue.size", 100);
    private final ConcurrentMap<Long, Future<JiraAggregate>> loading;
    private final ExecutorService executorService = new ThreadPoolExecutor(EXECUTOR_POOL_SIZE, EXECUTOR_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(EXECUTOR_TASK_QUEUE_SIZE), ThreadFactories.namedThreadFactory((String)"JIRAMetadataPlugin_CacheLoader", (ThreadFactories.Type)ThreadFactories.Type.DAEMON));
    private final JiraAggregateProvider jiraAggregateProvider;
    private final JiraAggregateCacheStore jiraAggregateCacheStore;

    @Autowired
    public JiraAggregateCacheLoader(JiraAggregateProvider jiraAggregateProvider, JiraAggregateCacheStore jiraAggregateCacheStore) {
        this.jiraAggregateProvider = jiraAggregateProvider;
        this.jiraAggregateCacheStore = jiraAggregateCacheStore;
        this.loading = Maps.newConcurrentMap();
    }

    @PreDestroy
    public void destroy() {
        this.executorService.shutdownNow();
    }

    Future<JiraAggregate> loadCacheAsync(long pageId, JiraMetadataErrorHelper errorHelper) {
        FutureCallable<JiraAggregate> newTask = this.createCacheLoadingTask(pageId, errorHelper);
        Future<JiraAggregate> currentTask = (Future<JiraAggregate>)this.loading.putIfAbsent(pageId, newTask);
        if (currentTask == null) {
            try {
                currentTask = this.executorService.submit(newTask);
                this.loading.put(pageId, currentTask);
            }
            catch (RejectedExecutionException rejectedException) {
                if (log.isDebugEnabled()) {
                    log.debug("Failed to submit new cache loading task", (Throwable)rejectedException);
                }
                this.loading.remove(pageId);
            }
            catch (Exception e) {
                this.loading.remove(pageId);
                errorHelper.handleException(e);
            }
        }
        return currentTask;
    }

    private FutureCallable<JiraAggregate> createCacheLoadingTask(long pageId, JiraMetadataErrorHelper errorHelper) {
        return new FutureCallable<JiraAggregate>(() -> {
            try {
                JiraAggregate aggregateData = this.jiraAggregateCacheStore.get(pageId);
                if (aggregateData == null) {
                    aggregateData = this.jiraAggregateProvider.getAggregateData(pageId, errorHelper);
                    if (!Thread.currentThread().isInterrupted()) {
                        this.jiraAggregateCacheStore.put(pageId, aggregateData);
                    }
                }
                JiraAggregate jiraAggregate = aggregateData;
                return jiraAggregate;
            }
            catch (Exception e) {
                errorHelper.handleException(e);
                JiraAggregate jiraAggregate = null;
                return jiraAggregate;
            }
            finally {
                this.loading.remove(pageId);
            }
        });
    }

    void invalidateCacheLoadingTask(long pageId) {
        Future task = (Future)this.loading.get(pageId);
        if (task != null) {
            task.cancel(true);
        }
    }

    void invalidateAllCacheLoadingTasks() {
        for (Future task : this.loading.values()) {
            task.cancel(true);
        }
    }

    JiraAggregate getValue(long pageId, JiraMetadataErrorHelper errorHelper) {
        JiraAggregate aggregateData = this.getAggregateFromTask((Future)this.loading.get(pageId), errorHelper);
        if (aggregateData == null) {
            aggregateData = this.getAggregateFromTask(this.loadCacheAsync(pageId, errorHelper), errorHelper);
        }
        return aggregateData;
    }

    @Nullable
    private JiraAggregate getAggregateFromTask(Future<JiraAggregate> task, JiraMetadataErrorHelper errorHelper) {
        if (task == null) {
            return null;
        }
        try {
            return task.get(LOADING_TIMEOUT_SECS, TimeUnit.SECONDS);
        }
        catch (TimeoutException toe) {
            if (log.isDebugEnabled()) {
                log.debug("Timed out getting aggregation from task", (Throwable)toe);
            }
            return JiraAggregates.timedOut();
        }
        catch (Exception e) {
            errorHelper.handleException(e);
            return null;
        }
    }

    private static class FutureCallable<V>
    extends FutureTask<V>
    implements Callable<V> {
        private final Callable<V> callable;

        public FutureCallable(Callable<V> callable) {
            super(callable);
            this.callable = callable;
        }

        @Override
        public V call() throws Exception {
            return this.callable.call();
        }
    }
}

