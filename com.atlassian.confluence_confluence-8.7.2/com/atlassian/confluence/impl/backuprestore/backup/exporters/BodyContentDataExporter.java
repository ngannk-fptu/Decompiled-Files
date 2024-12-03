/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.time.StopWatch
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.DatabaseExporterHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Exporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.ReferenceEntityFromBodyContentExtractor;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Subscriber;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.AbstractDatabaseDataConverter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.Converter;
import com.atlassian.confluence.impl.backuprestore.backup.models.DbRawObjectData;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BodyContentDataExporter
implements Exporter,
Subscriber {
    private static final Logger log = LoggerFactory.getLogger(BodyContentDataExporter.class);
    private final DatabaseExporterHelper helper;
    private final Converter converter;
    private final ReferenceEntityFromBodyContentExtractor referenceEntityFromBodyContentExtractor;
    private final String ENTITY_BY_ID_QUERY;
    private final AtomicInteger batchCounter = new AtomicInteger();
    private final AtomicLong fullDuration = new AtomicLong();
    private final AtomicInteger processedObjectsCounter = new AtomicInteger();

    public BodyContentDataExporter(@Nonnull DatabaseExporterHelper databaseExporterHelper, @NonNull AbstractDatabaseDataConverter converter, @NonNull ReferenceEntityFromBodyContentExtractor referenceEntityFromBodyContentExtractor) {
        this.helper = databaseExporterHelper;
        this.converter = converter;
        this.referenceEntityFromBodyContentExtractor = referenceEntityFromBodyContentExtractor;
        this.ENTITY_BY_ID_QUERY = this.buildQuery(this.getEntityInfo());
    }

    private String buildQuery(ExportableEntityInfo entityInfo) {
        return "SELECT * FROM " + this.helper.checkNameDoesNotHaveSqlInjections(entityInfo.getTableName()) + " WHERE CONTENTID IN (:values)";
    }

    @Override
    public ExportableEntityInfo getEntityInfo() {
        return this.converter.getEntityInfo();
    }

    @Override
    public ExportableEntityInfo getEntityInfo(Class<?> exportedClass) {
        return this.converter.getEntityInfo(exportedClass);
    }

    @Override
    public Collection<Class<?>> getWatchingEntityClasses() {
        return Collections.singleton(ContentEntityObject.class);
    }

    @Override
    public void onMonitoredObjectsExport(Class<?> exportedClass, Collection<Object> idList) throws InterruptedException, BackupRestoreException {
        if (!ContentEntityObject.class.isAssignableFrom(exportedClass)) {
            throw new IllegalStateException("BodyContentDataExporter should not receive any notifications except from ContentEntityObject");
        }
        this.exportInBatchByQueryWithCondition(idList);
    }

    private void exportInBatchByQueryWithCondition(Collection<Object> objectIdList) {
        String callerName = "BodyContent export";
        List partitions = Lists.partition(new ArrayList<Object>(objectIdList), (int)this.helper.getBatchSize(this.getEntityInfo()));
        for (List partition : partitions) {
            this.helper.runTaskAsync(() -> {
                try {
                    StopWatch stopWatch = StopWatch.createStarted();
                    int batchNumber = this.batchCounter.getAndIncrement();
                    List<EntityObjectReadyForExport> objectsForExport = this.getEntityObjectReadyForExports(partition);
                    if (objectsForExport.isEmpty()) {
                        return null;
                    }
                    this.helper.writeObjectsAndNotifyOtherExporters(objectsForExport);
                    this.extractReferencesFromBody(objectsForExport);
                    int processedObjectsSoFar = this.processedObjectsCounter.addAndGet(objectsForExport.size());
                    long iterationDuration = stopWatch.getTime();
                    this.fullDuration.addAndGet(iterationDuration);
                    log.debug("Processed batch {} (caller '{}'), got {} ids and processed objects in this iteration: {} (in {} ms), overall duration: {} ms (for {} objects), duration per 1000 objects: {} ms", new Object[]{batchNumber, "BodyContent export", partition.size(), objectsForExport.size(), iterationDuration, this.fullDuration.get(), processedObjectsSoFar, this.fullDuration.get() * 1000L / (long)processedObjectsSoFar});
                }
                catch (InterruptedException e) {
                    log.warn("process '{}' was interrupted: {}", (Object)"BodyContent export", (Object)e.getMessage());
                    Thread.currentThread().interrupt();
                }
                return null;
            }, "BodyContent export");
        }
    }

    private List<EntityObjectReadyForExport> getEntityObjectReadyForExports(Collection<?> ids) {
        return (List)this.helper.doInReadOnlyTransaction(tx -> {
            List<DbRawObjectData> dbRawObjectData = this.helper.runQueryWithInCondition(this.ENTITY_BY_ID_QUERY, "values", ids);
            return this.converter.convertToObjectsReadyForSerialisation(dbRawObjectData);
        });
    }

    private void extractReferencesFromBody(List<EntityObjectReadyForExport> objectsForExport) throws BackupRestoreException, InterruptedException {
        ArrayList<EntityObjectReadyForExport> objectsWithSimpleReferences = new ArrayList<EntityObjectReadyForExport>();
        for (EntityObjectReadyForExport objectForExport : objectsForExport) {
            HashSet<EntityObjectReadyForExport.Reference> simpleReferences = new HashSet<EntityObjectReadyForExport.Reference>();
            Set<EntityObjectReadyForExport.Reference> references = this.extractReferencesFromBody(objectForExport);
            for (EntityObjectReadyForExport.Reference ref : references) {
                simpleReferences.add(ref);
            }
            if (simpleReferences.isEmpty()) continue;
            objectsWithSimpleReferences.add(this.createObjectWithContentReferences(objectForExport, simpleReferences));
        }
        if (!objectsWithSimpleReferences.isEmpty()) {
            log.debug("There are {} simple references extracted from body", (Object)objectsWithSimpleReferences.size());
            this.helper.writeAllReferencedSimpleObjects(objectsWithSimpleReferences);
        }
    }

    private Set<EntityObjectReadyForExport.Reference> extractReferencesFromBody(EntityObjectReadyForExport entityObjectReadyForExport) throws BackupRestoreException {
        String bodyContent = null;
        try {
            String bodyType = entityObjectReadyForExport.getProperty("bodyType").getValue().toString();
            if (bodyType.equals(String.valueOf(BodyType.XHTML.toInt()))) {
                bodyContent = entityObjectReadyForExport.getProperty("body").getStringValue();
            }
        }
        catch (Exception e) {
            throw new BackupRestoreException("Failed to extract body from BodyContent", e);
        }
        if (StringUtils.isNotEmpty(bodyContent)) {
            try {
                HashSet<EntityObjectReadyForExport.Reference> references = new HashSet<EntityObjectReadyForExport.Reference>(this.referenceEntityFromBodyContentExtractor.extractReferenceContentFrom(bodyContent));
                log.debug("There are {} for current Body Content of Content {}", (Object)references.size(), entityObjectReadyForExport.getId().getValue());
                return references;
            }
            catch (XhtmlException e) {
                log.warn("Failed to extract user references from BodyContent with id {}", entityObjectReadyForExport.getId().getValue());
                log.debug("Failed to extract user references exception.", (Throwable)e);
            }
        }
        return Collections.emptySet();
    }

    private EntityObjectReadyForExport createObjectWithContentReferences(EntityObjectReadyForExport objectForExport, Set<EntityObjectReadyForExport.Reference> references) {
        EntityObjectReadyForExport objectWithAllReferences = new EntityObjectReadyForExport(objectForExport.getId(), objectForExport.getClazz());
        objectWithAllReferences.getReferences().addAll(references);
        return objectWithAllReferences;
    }
}

