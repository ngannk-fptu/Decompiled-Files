/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.restore.EntityPersister;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.IdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ObjectPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ObjectQueueProcessor;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.PersisterHelper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.deferredoperations.DeferredAction;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.deferredoperations.DeferredActionsFactory;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.deferredoperations.DeferredActionsHolder;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStash;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStashFactory;
import com.atlassian.confluence.impl.backuprestore.statistics.OnObjectsProcessingHandler;
import com.atlassian.confluence.impl.backuprestore.statistics.SkippedObjectsReason;
import com.atlassian.confluence.spaces.Space;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpacePersister
implements EntityPersister {
    public static final String LOWER_KEY_PROPERTY_NAME = "lowerKey";
    private final IdMapper idMapper;
    final ObjectQueueProcessor objectQueueProcessor;
    final DeferredActionsHolder deferredActionsHolder;
    final DeferredActionsFactory deferredActionsFactory;
    private final OnObjectsProcessingHandler onObjectsProcessingHandler;
    private final Function<String, Boolean> allowedLowerSpaceKeyFilter;
    private final ImportedObjectsStash stash;

    public SpacePersister(ImportedObjectsStashFactory importedObjectsStashFactory, ObjectPersister objectPersister, IdMapper idMapper, DeferredActionsHolder deferredActionsHolder, DeferredActionsFactory deferredActionsFactory, PersisterHelper persisterHelper, OnObjectsProcessingHandler onObjectsProcessingHandler, Optional<Set<String>> allowedSpaceKeys) {
        this.objectQueueProcessor = new ObjectQueueProcessor(objectPersister, persisterHelper);
        this.idMapper = idMapper;
        this.deferredActionsHolder = deferredActionsHolder;
        this.deferredActionsFactory = deferredActionsFactory;
        this.stash = importedObjectsStashFactory.createStash("space");
        this.onObjectsProcessingHandler = onObjectsProcessingHandler;
        this.allowedLowerSpaceKeyFilter = this.createSpaceKeyFilter(allowedSpaceKeys);
    }

    private Function<String, Boolean> createSpaceKeyFilter(Optional<Set<String>> allowedSpaceKeys) {
        if (allowedSpaceKeys.isEmpty()) {
            return lowerSpaceKey -> true;
        }
        Set loweredSpaceKeys = allowedSpaceKeys.get().stream().map(String::toLowerCase).collect(Collectors.toSet());
        return loweredSpaceKeys::contains;
    }

    @Override
    public Collection<Class<?>> getSupportedClasses() {
        return Collections.singleton(Space.class);
    }

    @Override
    public boolean canAccept(ImportedObjectV2 importedObject) {
        return importedObject.getEntityClass().equals(Space.class);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void persist(ImportedObjectV2 importedObject) throws BackupRestoreException {
        ImportedObjectV2 importedObjectWithoutUnsatisfiedReferences;
        if (!this.canAccept(importedObject)) {
            throw new BackupRestoreException("Space persisted got unacceptable object with class " + importedObject.getEntityClass());
        }
        if (!this.isSpaceAllowed(importedObject)) {
            this.onObjectsProcessingHandler.onObjectsSkipping(Collections.singleton(importedObject), SkippedObjectsReason.SPACE_IS_NOT_ALLOWED);
            return;
        }
        Collection<HibernateField> notSatisfiedDependencies = this.idMapper.getAllNotSatisfiedDependencies(importedObject);
        HashMap<String, Object> valuesToOverride = new HashMap<String, Object>();
        if (!notSatisfiedDependencies.isEmpty()) {
            SpacePersister spacePersister = this;
            synchronized (spacePersister) {
                for (HibernateField hibernateFieldToDefer : notSatisfiedDependencies) {
                    DeferredAction deferredAction = this.deferredActionsFactory.createUpdateOperation(importedObject.getEntityInfo(), hibernateFieldToDefer, importedObject.getId(), importedObject.getFieldValue(hibernateFieldToDefer.getPropertyName()));
                    this.deferredActionsHolder.addAction(deferredAction);
                    valuesToOverride.put(hibernateFieldToDefer.getPropertyName(), null);
                }
            }
        }
        if (!this.objectQueueProcessor.tryToPersistObjectAsync(importedObjectWithoutUnsatisfiedReferences = importedObject.overridePropertyValues(importedObject.getId(), valuesToOverride))) {
            this.stash.add(importedObjectWithoutUnsatisfiedReferences);
        }
    }

    private boolean isSpaceAllowed(ImportedObjectV2 importedObject) {
        String lowerKey = (String)importedObject.getFieldValue(LOWER_KEY_PROPERTY_NAME);
        return this.allowedLowerSpaceKeyFilter.apply(lowerKey);
    }

    @Override
    public long persistNextChunkOfData() throws BackupRestoreException {
        return this.objectQueueProcessor.persistAllPendingObjectsInTheQueue() + (long)this.objectQueueProcessor.processChunkOfStash(this.stash, this.idMapper);
    }
}

