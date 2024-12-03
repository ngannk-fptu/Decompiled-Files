/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.user.InternalUser
 *  com.atlassian.crowd.model.user.InternalUserCredentialRecord
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.restore.EntityPersister;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.IdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.DirectoryMappingPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ListIndexColumnValueCalculator;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ObjectPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.PersisterHelper;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStash;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStashFactory;
import com.atlassian.confluence.impl.backuprestore.statistics.SkippedObjectsReason;
import com.atlassian.crowd.model.user.InternalUser;
import com.atlassian.crowd.model.user.InternalUserCredentialRecord;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalUserCredentialRecordPersister
implements EntityPersister {
    private static final Logger log = LoggerFactory.getLogger(DirectoryMappingPersister.class);
    private final ImportedObjectsStash stash;
    private final ObjectPersister objectPersister;
    private final PersisterHelper persisterHelper;
    private final IdMapper idMapper;
    private final ListIndexColumnValueCalculator listIndexColumnValueCalculator;

    public InternalUserCredentialRecordPersister(ObjectPersister objectPersister, PersisterHelper persisterHelper, ImportedObjectsStashFactory importedObjectsStashFactory, IdMapper idMapper, ListIndexColumnValueCalculator listIndexColumnValueCalculator) {
        this.objectPersister = objectPersister;
        this.persisterHelper = persisterHelper;
        this.stash = importedObjectsStashFactory.createStash(InternalUserCredentialRecordPersister.class.getSimpleName());
        this.idMapper = idMapper;
        this.listIndexColumnValueCalculator = listIndexColumnValueCalculator;
    }

    @Override
    public Collection<Class<?>> getSupportedClasses() {
        return Set.of(InternalUserCredentialRecord.class, InternalUser.class);
    }

    @Override
    public boolean canAccept(ImportedObjectV2 importedObject) {
        return this.getSupportedClasses().contains(importedObject.getEntityClass());
    }

    @Override
    public void persist(ImportedObjectV2 importedObject) throws BackupRestoreException {
        if (!this.canAccept(importedObject)) {
            throw new BackupRestoreException("InternalUserCredentialRecordPersister received unacceptable object with class " + importedObject.getEntityClass());
        }
        if (InternalUserCredentialRecord.class.equals(importedObject.getEntityClass())) {
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
                this.persisterHelper.logInformationAboutNotPersistedObject(importedObject, SkippedObjectsReason.INVALID_FIELDS, Collections.emptySet());
                log.warn("Couldn't resolve listIndex property value for internal user credential record with id {}", importedObject.getId());
                continue;
            }
            objectsReadyToPersist.add(objectToPersist.get());
        }
        if (!objectsReadyToPersist.isEmpty()) {
            this.objectPersister.persistAsynchronously(objectsReadyToPersist, "Persisting internal user credential records");
        }
        return counter;
    }
}

