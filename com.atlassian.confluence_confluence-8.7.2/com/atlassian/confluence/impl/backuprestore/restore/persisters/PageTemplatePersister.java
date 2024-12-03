/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.restore.EntityPersister;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.IdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ObjectPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ObjectQueueProcessor;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.PageTemplatePersisterHelper;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStash;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStashFactory;
import com.atlassian.confluence.impl.backuprestore.statistics.SkippedObjectsReason;
import com.atlassian.confluence.pages.templates.PageTemplate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageTemplatePersister
implements EntityPersister {
    private static final Logger log = LoggerFactory.getLogger(PageTemplatePersister.class);
    static final String LATEST_PAGE_TEMPLATE_STASH = "latest-page-template-stash";
    static final String HISTORICAL_PAGE_TEMPLATE_STASH = "historical-page-template-stash";
    final ObjectPersister objectPersister;
    final IdMapper idMapper;
    final PageTemplatePersisterHelper pageTemplatePersisterHelper;
    final ImportedObjectsStashFactory importedObjectsStashFactory;
    final ObjectQueueProcessor objectQueueProcessor;
    private final ImportedObjectsStash latestStash;
    private final ImportedObjectsStash historicalStash;
    private final Collection<ImportedObjectsStash> allStashes = new ArrayList<ImportedObjectsStash>();

    public PageTemplatePersister(ObjectPersister objectPersister, IdMapper idMapper, ImportedObjectsStashFactory importedObjectsStashFactory, PageTemplatePersisterHelper pageTemplatePersisterHelper, ObjectQueueProcessor objectQueueProcessor) {
        this.objectPersister = objectPersister;
        this.idMapper = idMapper;
        this.importedObjectsStashFactory = importedObjectsStashFactory;
        this.pageTemplatePersisterHelper = pageTemplatePersisterHelper;
        this.objectQueueProcessor = objectQueueProcessor;
        this.latestStash = this.registerStash(importedObjectsStashFactory.createStash(LATEST_PAGE_TEMPLATE_STASH));
        this.historicalStash = this.registerStash(importedObjectsStashFactory.createStash(HISTORICAL_PAGE_TEMPLATE_STASH));
    }

    private ImportedObjectsStash registerStash(ImportedObjectsStash stash) {
        this.allStashes.add(stash);
        return stash;
    }

    @Override
    public Collection<Class<?>> getSupportedClasses() {
        return Collections.singleton(PageTemplate.class);
    }

    @Override
    public boolean canAccept(ImportedObjectV2 importedObject) {
        return importedObject.getEntityClass().equals(PageTemplate.class);
    }

    @Override
    public void persist(ImportedObjectV2 importedObject) throws BackupRestoreException {
        if (!this.canAccept(importedObject)) {
            throw new BackupRestoreException("Page template persister got an unexpected class '" + importedObject.getEntityClass() + "'");
        }
        if (importedObject.getEntityInfo().getAllExternalReferences().isEmpty()) {
            if (!this.objectQueueProcessor.tryToPersistObjectAsync(importedObject)) {
                this.latestStash.add(importedObject);
            }
        } else if (this.pageTemplatePersisterHelper.isLatestPageTemplate(importedObject)) {
            this.latestStash.add(importedObject);
        } else {
            this.historicalStash.add(importedObject);
        }
    }

    @Override
    public long persistNextChunkOfData() throws BackupRestoreException {
        long processedObjectsNumber = 0L;
        for (ImportedObjectsStash stash : this.allStashes) {
            if (!stash.hasMoreRecords()) continue;
            log.debug("Processing stash {} with {} elements", (Object)stash.getName(), (Object)stash.getNumberOfWrittenObjects());
            processedObjectsNumber += this.processEntireStash(stash);
        }
        return processedObjectsNumber;
    }

    private long processEntireStash(ImportedObjectsStash stash) throws BackupRestoreException {
        long counter = 0L;
        ArrayList allTasks = new ArrayList();
        while (stash.hasMoreRecords()) {
            List<ImportedObjectV2> sourceObjects = stash.readObjects(this.pageTemplatePersisterHelper.getBatchSize());
            ArrayList<ImportedObjectV2> objectsReadyToPersist = new ArrayList<ImportedObjectV2>();
            for (ImportedObjectV2 importedObject : sourceObjects) {
                Collection<HibernateField> initialNotSatisfiedDependencies;
                ImportedObjectV2 importedObjectWithFixedUserDependencies = this.pageTemplatePersisterHelper.clearNotSatisfiedUserDependencies(importedObject, initialNotSatisfiedDependencies = this.idMapper.getAllNotSatisfiedDependencies(importedObject));
                Collection<HibernateField> finalNotSatisfiedDependencies = this.idMapper.getAllNotSatisfiedDependencies(importedObjectWithFixedUserDependencies);
                if (finalNotSatisfiedDependencies.isEmpty()) {
                    objectsReadyToPersist.add(importedObjectWithFixedUserDependencies);
                    ++counter;
                    continue;
                }
                this.pageTemplatePersisterHelper.logInformationAboutNotPersistedObject(importedObject, SkippedObjectsReason.NOT_SATISFIED_DEPENDENCIES, finalNotSatisfiedDependencies);
            }
            allTasks.addAll(this.objectPersister.persistAsynchronously(objectsReadyToPersist, "persisting page templates"));
        }
        this.waitUntilAllJobsComplete(allTasks);
        return counter;
    }

    private void waitUntilAllJobsComplete(Collection<Future<?>> allTasks) throws BackupRestoreException {
        this.objectPersister.waitUntilAllJobsFinish(allTasks);
    }
}

