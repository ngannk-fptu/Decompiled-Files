/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.ApplicationImpl
 *  com.atlassian.crowd.model.application.DirectoryMapping
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.restore.EntityPersister;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.IdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ListIndexColumnValueCalculator;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ObjectPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.OperationRecordsGenerator;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.PersisterHelper;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStash;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStashFactory;
import com.atlassian.confluence.impl.backuprestore.statistics.SkippedObjectsReason;
import com.atlassian.crowd.model.application.ApplicationImpl;
import com.atlassian.crowd.model.application.DirectoryMapping;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryMappingPersister
implements EntityPersister {
    private static final Logger log = LoggerFactory.getLogger(DirectoryMappingPersister.class);
    private final ImportedObjectsStash stash;
    private final ObjectPersister objectPersister;
    private final IdMapper idMapper;
    private final OperationRecordsGenerator operationRecordsGenerator;
    private final PersisterHelper persisterHelper;
    private final ListIndexColumnValueCalculator listIndexColumnValueCalculator;

    public DirectoryMappingPersister(ObjectPersister objectPersister, ImportedObjectsStashFactory importedObjectsStashFactory, PersisterHelper persisterHelper, IdMapper idMapper, OperationRecordsGenerator operationRecordsGenerator, ListIndexColumnValueCalculator listIndexColumnValueCalculator) {
        this.objectPersister = objectPersister;
        this.stash = importedObjectsStashFactory.createStash(DirectoryMappingPersister.class.getSimpleName());
        this.persisterHelper = persisterHelper;
        this.idMapper = idMapper;
        this.operationRecordsGenerator = operationRecordsGenerator;
        this.listIndexColumnValueCalculator = listIndexColumnValueCalculator;
    }

    @Override
    public Collection<Class<?>> getSupportedClasses() {
        return Set.of(DirectoryMapping.class, ApplicationImpl.class);
    }

    @Override
    public boolean canAccept(ImportedObjectV2 importedObject) {
        return this.getSupportedClasses().contains(importedObject.getEntityClass());
    }

    @Override
    public void persist(ImportedObjectV2 importedObject) throws BackupRestoreException {
        if (!this.canAccept(importedObject)) {
            throw new BackupRestoreException("DirectoryMappingPersister received unacceptable object with class " + importedObject.getEntityClass());
        }
        if (importedObject.getEntityClass().equals(DirectoryMapping.class)) {
            this.stash.add(importedObject);
        } else {
            this.listIndexColumnValueCalculator.trackRecordsOrderInList(importedObject);
        }
    }

    @Override
    public long persistNextChunkOfData() throws BackupRestoreException {
        if (!this.stash.hasMoreRecords()) {
            return 0L;
        }
        List<ImportedObjectV2> sourceObjects = this.stash.readObjects(this.persisterHelper.getBatchSize());
        int counter = sourceObjects.size();
        ArrayList<ImportedObjectV2> objectsReadyToPersist = new ArrayList<ImportedObjectV2>();
        for (ImportedObjectV2 importedObject : sourceObjects) {
            Collection<HibernateField> notSatisfiedDependencies = this.idMapper.getAllNotSatisfiedDependencies(importedObject);
            if (!notSatisfiedDependencies.isEmpty()) {
                this.persisterHelper.logInformationAboutNotPersistedObject(importedObject, SkippedObjectsReason.NOT_SATISFIED_DEPENDENCIES, notSatisfiedDependencies);
                continue;
            }
            Optional<ImportedObjectV2> objectToPersist = this.listIndexColumnValueCalculator.resolveListIndexProperty(importedObject);
            if (objectToPersist.isEmpty()) {
                log.warn("Couldn't resolve listIndex property value for directory mapping with id {}", importedObject.getId());
                this.persisterHelper.logInformationAboutNotPersistedObject(importedObject, SkippedObjectsReason.INVALID_FIELDS, Collections.emptySet());
                continue;
            }
            objectsReadyToPersist.add(objectToPersist.get());
            Collection<ImportedObjectV2> directoryOperationRecords = this.operationRecordsGenerator.prepareOperationRecords(objectToPersist.get());
            log.trace("Prepared {} directory mapping operation records for directory mapping with id {}", (Object)directoryOperationRecords.size(), importedObject.getId());
            objectsReadyToPersist.addAll(directoryOperationRecords);
        }
        if (!objectsReadyToPersist.isEmpty()) {
            this.objectPersister.persistAsynchronouslyInOneTransaction(objectsReadyToPersist, "Persisting directory mapping records together with directory mapping operations");
        }
        return counter;
    }
}

