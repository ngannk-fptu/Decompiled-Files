/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.impl.backuprestore.backup.exporters.CommonPersister;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.DatabaseExporterHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.Converter;
import com.atlassian.confluence.impl.backuprestore.backup.models.DbRawObjectData;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonDatabaseDataExporter {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected final AtomicInteger batchCounter = new AtomicInteger();
    protected final AtomicLong fullDuration = new AtomicLong();
    protected final AtomicInteger processedObjectsCounter = new AtomicInteger();
    private final Converter converter;
    private final DatabaseExporterHelper helper;
    private final CommonPersister persister;

    public CommonDatabaseDataExporter(Converter converter, DatabaseExporterHelper helper, CommonPersister persister) {
        this.converter = converter;
        this.helper = helper;
        this.persister = persister;
    }

    public void exportInBatchByQueryWithInCondition(String query, String fieldName, Collection<Object> objectIdList, String callerName) {
        List partitions = Lists.partition(new ArrayList<Object>(objectIdList), (int)this.helper.getBatchSize(this.getEntityInfo()));
        for (List partition : partitions) {
            this.getHelper().runTaskAsync(() -> {
                try {
                    StopWatch stopWatch = StopWatch.createStarted();
                    int batchNumber = this.batchCounter.getAndIncrement();
                    List<EntityObjectReadyForExport> objects = this.getEntityObjectReadyForExports(query, fieldName, partition);
                    Collection objectIdsToBeExported = objects.stream().map(o -> o.getId()).collect(Collectors.toList());
                    Set<Object> unexportedObjectIds = this.persister.getPersistableObjects(objectIdsToBeExported);
                    List<EntityObjectReadyForExport> newObjects = objects.stream().filter(o -> unexportedObjectIds.contains(o.getId())).collect(Collectors.toList());
                    if (newObjects.isEmpty()) {
                        return null;
                    }
                    this.getHelper().writeObjectsAndNotifyOtherExporters(newObjects);
                    int processedObjectsSoFar = this.processedObjectsCounter.addAndGet(newObjects.size());
                    long iterationDuration = stopWatch.getTime();
                    this.fullDuration.addAndGet(iterationDuration);
                    this.log.debug("Processed batch {} (caller '{}'), got {} ids and processed objects in this iteration: {} (in {} ms), overall duration: {} ms (for {} objects), duration per 1000 objects: {} ms", new Object[]{batchNumber, callerName, partition.size(), newObjects.size(), iterationDuration, this.fullDuration.get(), processedObjectsSoFar, this.fullDuration.get() * 1000L / (long)processedObjectsSoFar});
                }
                catch (InterruptedException e) {
                    this.log.warn("process '{}' was interrupted: {}", (Object)callerName, (Object)e.getMessage());
                    Thread.currentThread().interrupt();
                }
                return null;
            }, callerName);
        }
    }

    public void exportUniqueInBatchByQueryWithInCondition(String query, String fieldName, Collection<Object> objectIdList, String callerName) {
        Set<Object> idsToPersist = this.persister.getPersistableObjects(objectIdList);
        if (idsToPersist.isEmpty()) {
            return;
        }
        List partitions = Lists.partition(new ArrayList<Object>(idsToPersist), (int)this.helper.getRegularBatchSize());
        for (List partition : partitions) {
            this.helper.runTaskAsync(() -> {
                this.helper.writeObjectsAndNotifyOtherExporters(this.getEntityObjectReadyForExportByIds(query, fieldName, partition));
                return null;
            }, callerName);
        }
    }

    private List<EntityObjectReadyForExport> getEntityObjectReadyForExportByIds(String query, String fieldName, Collection<?> idsToPersist) {
        return (List)this.helper.doInReadOnlyTransaction(tx -> {
            List<DbRawObjectData> rawObjectData = this.helper.runQueryWithInCondition(query, fieldName, idsToPersist);
            return this.converter.convertToObjectsReadyForSerialisation(rawObjectData);
        });
    }

    private ExportableEntityInfo getEntityInfo() {
        return this.getConverter().getEntityInfo();
    }

    public List<EntityObjectReadyForExport> getEntityObjectReadyForExports(String query, String fieldName, Collection<?> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return (List)this.getHelper().doInReadOnlyTransaction(tx -> {
            List<DbRawObjectData> dbRawObjectData = this.getHelper().runQueryWithInCondition(query, fieldName, ids);
            return this.getConverter().convertToObjectsReadyForSerialisation(dbRawObjectData);
        });
    }

    public Converter getConverter() {
        return this.converter;
    }

    public DatabaseExporterHelper getHelper() {
        return this.helper;
    }
}

