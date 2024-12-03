/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.backuprestore.restore.EntityPersister;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.PersistersCreator;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.deferredoperations.DeferredActionsHolder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportedObjectsDispatcher {
    private static final Logger log = LoggerFactory.getLogger(ImportedObjectsDispatcher.class);
    private final List<Collection<EntityPersister>> persistersGroupedByStages;
    private final Map<Class<?>, Collection<EntityPersister>> persistersByClass;
    private final DeferredActionsHolder deferredActionsHolder;

    public ImportedObjectsDispatcher(PersistersCreator persistersCreator, DeferredActionsHolder deferredActionsHolder) {
        this.deferredActionsHolder = deferredActionsHolder;
        this.persistersGroupedByStages = new CopyOnWriteArrayList<Collection<EntityPersister>>(persistersCreator.getPersistersGroupedByStages());
        this.persistersByClass = this.getPersistersByClassMap(this.persistersGroupedByStages.stream().flatMap(Collection::stream).collect(Collectors.toSet()));
    }

    private Map<Class<?>, Collection<EntityPersister>> getPersistersByClassMap(Collection<EntityPersister> persisters) {
        HashMap persistersByClass = new HashMap();
        for (EntityPersister persister : persisters) {
            persister.getSupportedClasses().forEach(aClass -> persistersByClass.computeIfAbsent((Class<?>)aClass, k -> new ArrayList()).add(persister));
        }
        return persistersByClass;
    }

    public boolean processIncomingImportedObject(ImportedObjectV2 importedObject) throws BackupRestoreException {
        Collection<EntityPersister> persistersForObject = this.persistersByClass.get(this.extractEntityClass(importedObject));
        if (persistersForObject == null || persistersForObject.isEmpty()) {
            log.warn("No persisters found for the entity with class " + importedObject.getEntityClass());
            return false;
        }
        for (EntityPersister persister : persistersForObject) {
            persister.persist(importedObject);
        }
        return true;
    }

    private Class<?> extractEntityClass(ImportedObjectV2 importedObject) {
        Class<?> originalClass = importedObject.getEntityClass();
        if (ContentEntityObject.class.isAssignableFrom(originalClass)) {
            return ContentEntityObject.class;
        }
        return originalClass;
    }

    private void processAllStashesOfThisStage(Collection<EntityPersister> persisters) throws BackupRestoreException {
        ArrayList<EntityPersister> persistersWithDataInStashes = new ArrayList<EntityPersister>(persisters);
        while (this.processChunkFromEveryPersister(persistersWithDataInStashes) > 0L) {
        }
    }

    private long processChunkFromEveryPersister(Collection<EntityPersister> persisters) throws BackupRestoreException {
        long counter = 0L;
        Iterator<EntityPersister> persisterIterator = persisters.iterator();
        while (persisterIterator.hasNext()) {
            long processedElementsNumber = persisterIterator.next().persistNextChunkOfData();
            if (processedElementsNumber > 0L) {
                counter += processedElementsNumber;
                continue;
            }
            persisterIterator.remove();
        }
        return counter;
    }

    public void runDeferredOperations() throws BackupRestoreException {
        this.deferredActionsHolder.runDeferredOperations();
    }

    public boolean processNextStashPhase() throws BackupRestoreException {
        if (this.persistersGroupedByStages.isEmpty()) {
            return false;
        }
        this.processAllStashesOfThisStage(this.persistersGroupedByStages.remove(0));
        return true;
    }
}

