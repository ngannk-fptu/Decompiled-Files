/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.user.propertyset.BucketPropertySetItem
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import bucket.user.propertyset.BucketPropertySetItem;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.EntityPersister;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.IdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ObjectQueueProcessor;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStash;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStashFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class GenericPersister
implements EntityPersister {
    private final Collection<Class<?>> supportedClasses;
    private final IdMapper idMapper;
    private final ImportedObjectsStash stash;
    private final ObjectQueueProcessor objectQueueProcessor;

    public GenericPersister(ObjectQueueProcessor objectQueueProcessor, Class<?> supportedClass, IdMapper idMapper, ImportedObjectsStashFactory importedObjectsStashFactory) {
        this.objectQueueProcessor = objectQueueProcessor;
        this.supportedClasses = Collections.singleton(supportedClass);
        this.idMapper = idMapper;
        this.stash = importedObjectsStashFactory.createStash(supportedClass.getSimpleName());
    }

    @Override
    public Collection<Class<?>> getSupportedClasses() {
        return this.supportedClasses;
    }

    @Override
    public boolean canAccept(ImportedObjectV2 importedObject) {
        return this.supportedClasses.contains(importedObject.getEntityClass());
    }

    @Override
    public void persist(ImportedObjectV2 importedObject) throws BackupRestoreException {
        if (!this.canAccept(importedObject)) {
            String supportedClassesAsString = this.supportedClasses.stream().map(Class::getSimpleName).collect(Collectors.joining());
            throw new BackupRestoreException("Generic persister for " + supportedClassesAsString + " persisted got unacceptable object with class " + importedObject.getEntityClass());
        }
        if (importedObject.getEntityInfo().getAllExternalReferences().isEmpty() && !importedObject.getEntityClass().equals(BucketPropertySetItem.class)) {
            if (!this.objectQueueProcessor.tryToPersistObjectAsync(importedObject)) {
                this.stash.add(importedObject);
            }
        } else {
            this.stash.add(importedObject);
        }
    }

    @Override
    public long persistNextChunkOfData() throws BackupRestoreException {
        return this.objectQueueProcessor.persistAllPendingObjectsInTheQueue() + (long)this.objectQueueProcessor.processChunkOfStash(this.stash, this.idMapper);
    }
}

