/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.IdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ObjectPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.PersisterHelper;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStash;
import com.atlassian.confluence.impl.backuprestore.statistics.SkippedObjectsReason;
import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class ObjectQueueProcessor {
    private static final int DEFAULT_QUEUE_SIZE = Integer.getInteger("confluence.restore.object-queue-size", 10000);
    private static final ReentrantLock drainToLock = new ReentrantLock();
    private final ObjectPersister objectPersister;
    private final PersisterHelper persisterHelper;
    private final BlockingQueue<ImportedObjectV2> objectsQueue;
    private final BlockingQueue<Future<?>> currentFutures = new LinkedBlockingQueue();

    public ObjectQueueProcessor(ObjectPersister objectPersister, PersisterHelper persisterHelper) {
        this(objectPersister, persisterHelper, new ArrayBlockingQueue<ImportedObjectV2>(DEFAULT_QUEUE_SIZE));
    }

    @VisibleForTesting
    public ObjectQueueProcessor(ObjectPersister objectPersister, PersisterHelper persisterHelper, BlockingQueue<ImportedObjectV2> objectsQueue) {
        this.objectPersister = objectPersister;
        this.persisterHelper = persisterHelper;
        this.objectsQueue = objectsQueue;
    }

    boolean tryToPersistObjectAsync(ImportedObjectV2 importedObject) throws BackupRestoreException {
        try {
            boolean bl = this.objectsQueue.offer(importedObject);
            return bl;
        }
        finally {
            this.processElementsInTheQueueIfThereAreNoTasksInTheProgress();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processElementsInTheQueueIfThereAreNoTasksInTheProgress() throws BackupRestoreException {
        if (!this.allFuturesAreDone(new ArrayList(this.currentFutures))) {
            return;
        }
        if (!drainToLock.tryLock()) {
            return;
        }
        try {
            this.clearFutures();
            ArrayList<ImportedObjectV2> objectsToPersist = new ArrayList<ImportedObjectV2>();
            this.objectsQueue.drainTo(objectsToPersist, this.persisterHelper.getBatchSize());
            if (objectsToPersist.size() > 0) {
                String info = "processing elements in the queue while we are parsing the input XML file. The first object has type " + ((ImportedObjectV2)objectsToPersist.get(0)).getEntityClass().getSimpleName();
                Collection<Future<?>> futures = this.objectPersister.persistAsynchronously(objectsToPersist, info);
                this.currentFutures.addAll(futures);
            }
        }
        finally {
            drainToLock.unlock();
        }
    }

    public long persistAllPendingObjectsInTheQueue() {
        if (this.objectsQueue.size() == 0) {
            return 0L;
        }
        ArrayList<ImportedObjectV2> objectsToPersist = new ArrayList<ImportedObjectV2>();
        this.objectsQueue.drainTo(objectsToPersist);
        if (objectsToPersist.size() > 0) {
            String info = "processing pending objects from the queue (before processing the stash), first object has type " + ((ImportedObjectV2)objectsToPersist.get(0)).getEntityClass().getSimpleName();
            this.objectPersister.persistAsynchronously(objectsToPersist, info);
        }
        return objectsToPersist.size();
    }

    public int processChunkOfStash(ImportedObjectsStash stash, IdMapper idMapper) throws BackupRestoreException {
        if (!stash.hasMoreRecords()) {
            return 0;
        }
        List<ImportedObjectV2> sourceObjects = stash.readObjects(this.persisterHelper.getBatchSize());
        if (sourceObjects.isEmpty()) {
            return 0;
        }
        ArrayList<ImportedObjectV2> objectsReadyToPersist = new ArrayList<ImportedObjectV2>();
        for (ImportedObjectV2 importedObject : sourceObjects) {
            Collection<HibernateField> notSatisfiedDependencies = idMapper.getAllNotSatisfiedDependencies(importedObject);
            if (notSatisfiedDependencies.isEmpty()) {
                objectsReadyToPersist.add(importedObject);
                continue;
            }
            this.persisterHelper.logInformationAboutNotPersistedObject(importedObject, SkippedObjectsReason.NOT_SATISFIED_DEPENDENCIES, notSatisfiedDependencies);
        }
        if (!objectsReadyToPersist.isEmpty()) {
            this.objectPersister.persistAsynchronously(objectsReadyToPersist, "processing stash " + stash.getName() + ":" + stash.getIterationNumber());
        }
        return sourceObjects.size();
    }

    private boolean allFuturesAreDone(Collection<Future<?>> futures) {
        return futures.stream().allMatch(Future::isDone);
    }

    private void clearFutures() throws BackupRestoreException {
        try {
            for (Future future : this.currentFutures) {
                future.get();
            }
            this.currentFutures.clear();
        }
        catch (ExecutionException e) {
            throw new BackupRestoreException(e);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

