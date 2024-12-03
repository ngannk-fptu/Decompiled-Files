/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.confluence.pages.Attachment
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.conversion.impl;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.conversion.api.ConversionType;
import com.atlassian.confluence.plugins.conversion.impl.FileSystemConversionState;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TimeoutConversionRunnable
implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(TimeoutConversionRunnable.class);
    private final Attachment attachment;
    private final ConversionType conversionType;
    private final ClusterLockService clusterLockService;
    private final Runnable task;
    private final long timeout;
    private final FileSystemConversionState conversionState;
    private final String conversionLockPrefix;

    protected TimeoutConversionRunnable(Attachment attachment, ConversionType conversionType, ClusterLockService clusterLockService, Runnable task, FileSystemConversionState conversionState, String conversionLockPrefix, long timeout) {
        this.attachment = attachment;
        this.conversionType = conversionType;
        this.clusterLockService = clusterLockService;
        this.task = task;
        this.timeout = timeout;
        this.conversionState = conversionState;
        this.conversionLockPrefix = conversionLockPrefix;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        if (this.conversionState.isConverted() || this.conversionState.isError()) {
            return;
        }
        String lockName = this.conversionLockPrefix + this.attachment.getId();
        ClusterLock lock = this.clusterLockService.getLockForName(lockName);
        if (!lock.tryLock()) {
            log.debug("Trying to convert document, but another thread is already converting for attachment: " + this.attachment.getId());
            return;
        }
        ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).setPriority(1).setNameFormat(Thread.currentThread().getName() + "-internal").build());
        Future<?> future = null;
        try {
            log.debug("Started conversion for attachment {} ({})", (Object)this.attachment.getId(), (Object)this.attachment.getFileName());
            future = executor.submit(this.task);
            future.get(this.timeout, TimeUnit.SECONDS);
            log.debug("Ended conversion for attachment {} ({})", (Object)this.attachment.getId(), (Object)this.attachment.getFileName());
        }
        catch (InterruptedException e) {
            future.cancel(true);
            this.conversionState.markAsError();
            log.error("Cannot convert. Thread has been interrupted: ", (Throwable)e);
        }
        catch (ExecutionException e) {
            this.conversionState.markAsError();
            log.error("Cannot convert. Execution error: ", e.getCause());
        }
        catch (TimeoutException e) {
            future.cancel(true);
            this.conversionState.markAsError();
            log.error("Cannot convert. Timeout error: ", (Throwable)e);
        }
        catch (RuntimeException e) {
            this.conversionState.markAsError();
            log.error("Cannot convert. Error: ", (Throwable)e);
        }
        finally {
            if (future != null) {
                future.cancel(true);
            }
            executor.shutdownNow();
            lock.unlock();
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        TimeoutConversionRunnable other = (TimeoutConversionRunnable)obj;
        if (this.attachment == null ? other.attachment != null : !this.attachment.equals((Object)other.attachment)) {
            return false;
        }
        return this.conversionType == other.conversionType;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.attachment == null ? 0 : this.attachment.hashCode());
        result = 31 * result + (this.conversionType == null ? 0 : this.conversionType.hashCode());
        return result;
    }
}

