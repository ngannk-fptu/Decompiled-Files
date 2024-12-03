/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.ParallelTasksExecutor;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.Persister;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectPersister {
    private static final Logger log = LoggerFactory.getLogger(ObjectPersister.class);
    private final int batchSize;
    private final ParallelTasksExecutor parallelTasksExecutor;
    private List<Persister> persisters;

    public ObjectPersister(ParallelTasksExecutor parallelTasksExecutor, List<Persister> persisters) {
        this(parallelTasksExecutor, persisters, Integer.getInteger("confluence.restore.persisting-batch-size", 1000));
    }

    @VisibleForTesting
    public ObjectPersister(ParallelTasksExecutor parallelTasksExecutor, List<Persister> persisters, int batchSize) {
        this.parallelTasksExecutor = parallelTasksExecutor;
        this.persisters = persisters;
        this.batchSize = batchSize;
    }

    public Collection<Future<?>> persistAsynchronously(Collection<ImportedObjectV2> objectsToPersist, String info) {
        List<List<ImportedObjectV2>> partitions = this.splitToPartitions(objectsToPersist);
        AtomicInteger batchCounter = new AtomicInteger();
        return partitions.stream().map(partition -> this.persistAsynchronouslyInOneTransaction((Collection<ImportedObjectV2>)partition, info + ", partition " + batchCounter.getAndIncrement())).collect(Collectors.toList());
    }

    public Future<?> persistAsynchronouslyInOneTransaction(Collection<ImportedObjectV2> objectsToPersist, String info) {
        Map<ExportableEntityInfo, Collection<ImportedObjectV2>> objectsToPersistGroupedByClass = this.groupObjectsByEntityInfo(objectsToPersist);
        return this.parallelTasksExecutor.runTaskAsync(() -> {
            StopWatch stopWatch = StopWatch.createStarted();
            for (Persister persister : this.persisters) {
                if (!persister.shouldPersist(objectsToPersistGroupedByClass)) continue;
                persister.persist(objectsToPersistGroupedByClass);
            }
            log.debug("Inserted a batch of {} records. Duration: {}", (Object)objectsToPersist.size(), (Object)stopWatch);
            return null;
        }, info);
    }

    private Map<ExportableEntityInfo, Collection<ImportedObjectV2>> groupObjectsByEntityInfo(Collection<ImportedObjectV2> objectsToPersist) {
        LinkedHashMap<ExportableEntityInfo, Collection<ImportedObjectV2>> result = new LinkedHashMap<ExportableEntityInfo, Collection<ImportedObjectV2>>();
        objectsToPersist.forEach(importedObjectV2 -> result.computeIfAbsent(importedObjectV2.getEntityInfo(), list -> new ArrayList()).add(importedObjectV2));
        return result;
    }

    public void waitUntilAllJobsFinish(Collection<Future<?>> futures) throws BackupRestoreException {
        log.debug("waitUntilAllJobsFinish got a collections of futures containing {} elements", (Object)futures.size());
        ConcurrentLinkedQueue futuresQueue = new ConcurrentLinkedQueue(futures);
        try {
            while (!futuresQueue.isEmpty()) {
                Future future = (Future)futuresQueue.poll();
                future.get();
            }
            log.debug("waitUntilAllJobsFinish processed all {} futures successfully", (Object)futures.size());
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch (ExecutionException e) {
            throw new BackupRestoreException(e);
        }
    }

    private List<List<ImportedObjectV2>> splitToPartitions(Collection<ImportedObjectV2> objects) {
        return Lists.partition(new ArrayList<ImportedObjectV2>(objects), (int)this.batchSize);
    }
}

