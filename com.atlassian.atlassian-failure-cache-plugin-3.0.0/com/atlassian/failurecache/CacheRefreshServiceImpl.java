/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.primitives.Ints
 *  com.google.common.util.concurrent.ListenableFuture
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.atlassian.failurecache;

import com.atlassian.failurecache.CacheRefreshService;
import com.atlassian.failurecache.Cacheable;
import com.atlassian.failurecache.Refreshable;
import com.atlassian.failurecache.executor.DaemonExecutorService;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class CacheRefreshServiceImpl
implements ApplicationContextAware,
CacheRefreshService,
Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CacheRefreshServiceImpl.class);
    private final AtomicReference<Future<?>> runningFuture = new AtomicReference();
    private final DaemonExecutorService daemonExecutorService;
    private volatile ApplicationContext applicationContext;

    public CacheRefreshServiceImpl(DaemonExecutorService daemonExecutorService) {
        this.daemonExecutorService = daemonExecutorService;
    }

    @Override
    public synchronized Future<?> refreshAll(boolean mayInterruptIfRunning) {
        Future<?> currentJob = this.runningFuture.get();
        if (currentJob != null && mayInterruptIfRunning) {
            currentJob.cancel(mayInterruptIfRunning);
        }
        if (currentJob == null || currentJob.isDone()) {
            this.runningFuture.compareAndSet(currentJob, this.daemonExecutorService.submit(this));
        }
        return this.runningFuture.get();
    }

    @Override
    public void run() {
        List<Cacheable> cacheList = this.getCaches();
        Collections.sort(cacheList, this.byCachePriority());
        for (Cacheable cache : cacheList) {
            cache.clearCache();
            if (!(cache instanceof Refreshable) || this.waitForCacheSuccessfullyRefreshed((Refreshable)((Object)cache))) continue;
            return;
        }
    }

    private boolean waitForCacheSuccessfullyRefreshed(Refreshable refreshable) {
        ListenableFuture<?> listenableFuture = refreshable.refreshCache();
        try {
            listenableFuture.get();
            return true;
        }
        catch (InterruptedException e) {
            logger.debug("Interrupted while waiting for the cache to be rebuild; cancelling cache rebuild", (Throwable)e);
            return false;
        }
        catch (ExecutionException e) {
            logger.debug("Exception occurred while waiting for the cache to be rebuild; cancelling cache rebuild", (Throwable)e);
            return false;
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ApplicationContext)Preconditions.checkNotNull((Object)applicationContext);
    }

    private List<Cacheable> getCaches() {
        Map beansOfType = this.applicationContext.getBeansOfType(Cacheable.class);
        return new ArrayList<Cacheable>(beansOfType.values());
    }

    private Comparator<Cacheable> byCachePriority() {
        return new Comparator<Cacheable>(){

            @Override
            public int compare(Cacheable o1, Cacheable o2) {
                return Ints.compare((int)o1.getCachePriority(), (int)o2.getCachePriority());
            }
        };
    }
}

