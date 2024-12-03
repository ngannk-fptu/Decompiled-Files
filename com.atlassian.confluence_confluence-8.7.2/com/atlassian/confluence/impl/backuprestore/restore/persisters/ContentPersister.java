/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.restore.EntityPersister;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.IdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.AncestorRecordsGenerator;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ContentPersisterHelper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ObjectPersister;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStash;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStashFactory;
import com.atlassian.confluence.impl.backuprestore.statistics.OnObjectsProcessingHandler;
import com.atlassian.confluence.impl.backuprestore.statistics.SkippedObjectsReason;
import com.atlassian.confluence.pages.Attachment;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentPersister
implements EntityPersister {
    private static final Logger log = LoggerFactory.getLogger(ContentPersister.class);
    final ObjectPersister objectPersister;
    final IdMapper idMapper;
    final ContentPersisterHelper contentPersisterHelper;
    final ImportedObjectsStashFactory importedObjectsStashFactory;
    @VisibleForTesting
    static final String TOP_LEVEL_CONTENT_OBJECTS_STASH_NAME = "top-level-content-objects";
    @VisibleForTesting
    static final String OTHER_CONTENT_OBJECTS_STASH_NAME = "other-content-objects";
    @VisibleForTesting
    static final String CHILDREN_CONTENT_OBJECTS_STASH_NAME = "children-content-objects";
    @VisibleForTesting
    static final String HISTORIC_CONTENT_OBJECTS_STASH_NAME = "historical-content-objects";
    @VisibleForTesting
    static final String ATTACHMENT_CONTENT_OBJECTS_STASH_NAME = "attachment-content-objects";
    @VisibleForTesting
    static final String HISTORIC_ATTACHMENT_CONTENT_OBJECTS_STASH_NAME = "historical-attachment-content-objects";
    private final ImportedObjectsStash topLevelPagesStash;
    private final ImportedObjectsStash objectsWithParentReferencesOnlyStash;
    private final ImportedObjectsStash attachmentsStash;
    private final ImportedObjectsStash historicAttachmentsStash;
    private final ImportedObjectsStash historicObjectsStash;
    private final ImportedObjectsStash otherObjectsStash;
    private final Collection<ImportedObjectsStash> allStashes = new ArrayList<ImportedObjectsStash>();
    private final AncestorRecordsGenerator ancestorRecordsGenerator;
    private final OnObjectsProcessingHandler onObjectsProcessingHandler;

    public ContentPersister(ObjectPersister objectPersister, ImportedObjectsStashFactory importedObjectsStashFactory, IdMapper idMapper, ContentPersisterHelper contentPersisterHelper, AncestorRecordsGenerator ancestorRecordsGenerator, OnObjectsProcessingHandler onObjectsProcessingHandler) {
        this.objectPersister = objectPersister;
        this.idMapper = idMapper;
        this.contentPersisterHelper = contentPersisterHelper;
        this.importedObjectsStashFactory = importedObjectsStashFactory;
        this.ancestorRecordsGenerator = ancestorRecordsGenerator;
        this.onObjectsProcessingHandler = onObjectsProcessingHandler;
        this.topLevelPagesStash = this.registerStash(importedObjectsStashFactory.createStash(TOP_LEVEL_CONTENT_OBJECTS_STASH_NAME));
        this.objectsWithParentReferencesOnlyStash = this.registerStash(importedObjectsStashFactory.createStash(CHILDREN_CONTENT_OBJECTS_STASH_NAME));
        this.attachmentsStash = this.registerStash(importedObjectsStashFactory.createStash(ATTACHMENT_CONTENT_OBJECTS_STASH_NAME));
        this.historicAttachmentsStash = this.registerStash(importedObjectsStashFactory.createStash(HISTORIC_ATTACHMENT_CONTENT_OBJECTS_STASH_NAME));
        this.otherObjectsStash = this.registerStash(importedObjectsStashFactory.createStash(OTHER_CONTENT_OBJECTS_STASH_NAME));
        this.historicObjectsStash = this.registerStash(importedObjectsStashFactory.createStash(HISTORIC_CONTENT_OBJECTS_STASH_NAME));
    }

    private ImportedObjectsStash registerStash(ImportedObjectsStash stash) {
        this.allStashes.add(stash);
        return stash;
    }

    @Override
    public Collection<Class<?>> getSupportedClasses() {
        return Collections.singleton(ContentEntityObject.class);
    }

    @Override
    public boolean canAccept(ImportedObjectV2 importedObject) {
        Class<?> objectClass = importedObject.getEntityClass();
        return ContentEntityObject.class.isAssignableFrom(objectClass);
    }

    @Override
    public void persist(ImportedObjectV2 importedObject) throws BackupRestoreException {
        if (!this.canAccept(importedObject)) {
            throw new BackupRestoreException("Content entity object persister got unacceptable object with class " + importedObject.getEntityClass());
        }
        Collection<HibernateField> nonEmptyContentEntityReferences = this.contentPersisterHelper.getNotEmptyDependencies(importedObject, ContentEntityObject.class);
        if (this.contentPersisterHelper.isTopLevelPage(importedObject)) {
            this.topLevelPagesStash.add(importedObject);
        } else if (importedObject.getEntityClass().equals(Attachment.class)) {
            if (!this.contentPersisterHelper.isHistoricPage(importedObject)) {
                this.attachmentsStash.add(importedObject);
            } else {
                this.historicAttachmentsStash.add(importedObject);
            }
        } else if (this.contentPersisterHelper.isHistoricPage(importedObject)) {
            this.historicObjectsStash.add(importedObject);
        } else if (this.contentPersisterHelper.isChildPage(nonEmptyContentEntityReferences)) {
            this.objectsWithParentReferencesOnlyStash.add(importedObject);
        } else {
            this.otherObjectsStash.add(importedObject);
        }
    }

    @Override
    public long persistNextChunkOfData() throws BackupRestoreException {
        long processedObjectsNumber = 0L;
        for (ImportedObjectsStash stash : this.allStashes) {
            if (!stash.hasMoreRecords()) continue;
            log.debug("Processing stash {} (iteration {}) with {} elements", new Object[]{stash.getName(), stash.getIterationNumber(), stash.getNumberOfWrittenObjects()});
            processedObjectsNumber += this.processStashRecursively(stash);
        }
        return processedObjectsNumber;
    }

    private long processStashRecursively(ImportedObjectsStash initialStash) throws BackupRestoreException {
        int numberOfPersistedObjects = 0;
        ImportedObjectsStash currentStash = initialStash;
        while (true) {
            long initialNumberOfObjects = currentStash.getNumberOfWrittenObjects();
            if (!currentStash.hasMoreRecords()) {
                return numberOfPersistedObjects;
            }
            ImportedObjectsStash newStash = this.processTheStashInOneIteration(currentStash, currentStash.getIterationNumber() + 1);
            if (newStash.getNumberOfWrittenObjects() >= initialNumberOfObjects) {
                this.logInformationAboutNotPersistedObjects(newStash);
                return numberOfPersistedObjects;
            }
            numberOfPersistedObjects = (int)((long)numberOfPersistedObjects + (initialNumberOfObjects - newStash.getNumberOfWrittenObjects()));
            currentStash = newStash;
        }
    }

    private void logInformationAboutNotPersistedObjects(ImportedObjectsStash stash) throws BackupRestoreException {
        while (stash.getNumberOfWrittenObjects() - stash.getNumberOfRetrievedObjects() > 0L) {
            this.onObjectsProcessingHandler.onObjectsSkipping(stash.readObjects(this.contentPersisterHelper.getBatchSize()), SkippedObjectsReason.PARENT_WAS_NOT_PERSISTED);
        }
    }

    private ImportedObjectsStash processTheStashInOneIteration(ImportedObjectsStash stash, int iterationNumber) throws BackupRestoreException {
        ImportedObjectsStash stashWithNotSatisfiedDependencies = this.createStashForNotSatisfiedDependencies(stash, iterationNumber + 1);
        ArrayList allTasks = new ArrayList();
        while (stash.hasMoreRecords()) {
            List<ImportedObjectV2> sourceObjects = stash.readObjects(this.contentPersisterHelper.getBatchSize());
            ArrayList<ImportedObjectV2> objectsReadyToPersist = new ArrayList<ImportedObjectV2>();
            for (ImportedObjectV2 importedObject : sourceObjects) {
                Collection<HibernateField> initialNotSatisfiedDependencies;
                ImportedObjectV2 importedObjectWithFixedUserDependencies = this.contentPersisterHelper.clearNotSatisfiedUserDependencies(importedObject, initialNotSatisfiedDependencies = this.idMapper.getAllNotSatisfiedDependencies(importedObject));
                Collection<HibernateField> finalNotSatisfiedDependencies = this.idMapper.getAllNotSatisfiedDependencies(importedObjectWithFixedUserDependencies);
                if (finalNotSatisfiedDependencies.isEmpty()) {
                    objectsReadyToPersist.add(importedObjectWithFixedUserDependencies);
                    continue;
                }
                stashWithNotSatisfiedDependencies.add(importedObjectWithFixedUserDependencies);
            }
            Lists.partition(objectsReadyToPersist, (int)this.contentPersisterHelper.getBatchSize()).forEach(contentPartition -> {
                ArrayList<ImportedObjectV2> pagesWithAncestors = new ArrayList<ImportedObjectV2>();
                pagesWithAncestors.addAll((Collection<ImportedObjectV2>)contentPartition);
                pagesWithAncestors.addAll(this.createRecordsForAncestors((List<ImportedObjectV2>)contentPartition));
                allTasks.add(this.objectPersister.persistAsynchronouslyInOneTransaction(pagesWithAncestors, "persisting content entity records with ancestors"));
            });
        }
        this.waitUntilAllJobsComplete(allTasks);
        return stashWithNotSatisfiedDependencies;
    }

    private Collection<? extends ImportedObjectV2> createRecordsForAncestors(List<ImportedObjectV2> contentEntityRecords) {
        return this.ancestorRecordsGenerator.generateAncestorObjects(contentEntityRecords);
    }

    private ImportedObjectsStash createStashForNotSatisfiedDependencies(ImportedObjectsStash stash, int iterationNumber) throws BackupRestoreException {
        return this.importedObjectsStashFactory.createStash(stash.getName(), iterationNumber);
    }

    private void waitUntilAllJobsComplete(Collection<Future<?>> allTasks) throws BackupRestoreException {
        this.objectPersister.waitUntilAllJobsFinish(allTasks);
    }
}

