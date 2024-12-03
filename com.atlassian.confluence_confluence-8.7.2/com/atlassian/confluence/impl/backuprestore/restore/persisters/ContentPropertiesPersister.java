/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.content.ContentProperty;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.restore.EntityPersister;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.IdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ObjectPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.PersisterHelper;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStash;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStashFactory;
import com.atlassian.confluence.impl.backuprestore.statistics.SkippedObjectsReason;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentPropertiesPersister
implements EntityPersister {
    private static final Logger log = LoggerFactory.getLogger(ContentPropertiesPersister.class);
    final Map<Long, Long> contentPropertiesMap = new ConcurrentHashMap<Long, Long>();
    final ObjectPersister objectPersister;
    final ImportedObjectsStashFactory importedObjectsStashFactory;
    final PersisterHelper persisterHelper;
    private final IdMapper idMapper;
    private final ImportedObjectsStash stash;
    private AtomicBoolean ignoreCollections;
    private AtomicBoolean foundInvalidContentProperties;

    public ContentPropertiesPersister(ObjectPersister objectPersister, ImportedObjectsStashFactory importedObjectsStashFactory, boolean ignoreCollections, PersisterHelper persisterHelper, IdMapper idMapper) {
        this.objectPersister = objectPersister;
        this.importedObjectsStashFactory = importedObjectsStashFactory;
        this.ignoreCollections = new AtomicBoolean(ignoreCollections);
        this.persisterHelper = persisterHelper;
        this.idMapper = idMapper;
        this.stash = importedObjectsStashFactory.createStash(ContentPropertiesPersister.class.getSimpleName());
        this.foundInvalidContentProperties = new AtomicBoolean(false);
    }

    @Override
    public Collection<Class<?>> getSupportedClasses() {
        return Set.of(ContentEntityObject.class, ContentProperty.class);
    }

    @Override
    public boolean canAccept(ImportedObjectV2 importedObject) {
        return this.isContentEntity(importedObject) || this.isContentPropertyEntity(importedObject);
    }

    private boolean isContentPropertyEntity(ImportedObjectV2 importedObject) {
        return importedObject.getEntityClass().equals(ContentProperty.class);
    }

    private boolean isContentEntity(ImportedObjectV2 importedObject) {
        Class<?> objectClass = importedObject.getEntityClass();
        return ContentEntityObject.class.isAssignableFrom(objectClass);
    }

    @Override
    public void persist(ImportedObjectV2 importedObject) throws BackupRestoreException {
        if (!this.canAccept(importedObject)) {
            throw new BackupRestoreException("ContentPropertiesPersister got unacceptable object with class " + importedObject.getEntityClass());
        }
        if (this.isContentPropertyEntity(importedObject)) {
            if (!this.ignoreCollections.get() && importedObject.getFieldValue("content") != null) {
                this.ignoreCollections.compareAndSet(false, true);
                this.contentPropertiesMap.clear();
            }
            this.stash.add(importedObject);
        } else if (this.isContentEntity(importedObject)) {
            if (this.ignoreCollections.get()) {
                return;
            }
            this.registerContentPropertiesFromContentEntity(importedObject);
        } else {
            throw new IllegalArgumentException("ContentPropertyPersister received unexpected entity with class " + importedObject.getEntityClass());
        }
    }

    @Override
    public long persistNextChunkOfData() throws BackupRestoreException {
        log.debug("Processing stash {} (iteration {}) with {} elements", new Object[]{this.stash.getName(), this.stash.getIterationNumber(), this.stash.getNumberOfWrittenObjects()});
        return this.processOneBatchOfTheStash(this.stash);
    }

    private long processOneBatchOfTheStash(ImportedObjectsStash stash) throws BackupRestoreException {
        int counter = 0;
        if (stash.hasMoreRecords()) {
            List<ImportedObjectV2> sourceObjects = stash.readObjects(this.persisterHelper.getBatchSize());
            counter = sourceObjects.size();
            ArrayList<ImportedObjectV2> objectsToPersist = new ArrayList<ImportedObjectV2>();
            for (ImportedObjectV2 importedObject : sourceObjects) {
                if (this.ignoreCollections.get()) {
                    if (importedObject.getFieldValue("content") == null) {
                        this.logInformationAboutNotPersistedObject(importedObject, SkippedObjectsReason.PARENT_NO_LONGER_EXISTS);
                        continue;
                    }
                    objectsToPersist.add(importedObject);
                    continue;
                }
                Long targetContentID = this.popContentIDForContentPropertyEntity((Long)importedObject.getId());
                if (targetContentID == null) {
                    this.logInformationAboutNotPersistedObject(importedObject, SkippedObjectsReason.PARENT_NO_LONGER_EXISTS);
                    continue;
                }
                ImportedObjectV2 newObject = importedObject.overridePropertyValues(importedObject.getId(), Collections.singletonMap("content", targetContentID));
                objectsToPersist.add(newObject);
            }
            List<ImportedObjectV2> objectsWithAllDependenciesSatisfied = objectsToPersist.stream().filter(object -> this.idMapper.getAllNotSatisfiedDependencies((ImportedObjectV2)object).isEmpty()).collect(Collectors.toList());
            if (!objectsWithAllDependenciesSatisfied.isEmpty()) {
                this.objectPersister.persistAsynchronously(objectsWithAllDependenciesSatisfied, "content properties");
            }
            this.notifyStatisticsAboutSkippedObjects(objectsToPersist, objectsWithAllDependenciesSatisfied);
        }
        return counter;
    }

    private void notifyStatisticsAboutSkippedObjects(List<ImportedObjectV2> objectsReadyToPersist, List<ImportedObjectV2> objectsWithoutUnsatisfiedDependencies) {
        if (objectsReadyToPersist.size() > objectsWithoutUnsatisfiedDependencies.size()) {
            objectsReadyToPersist.removeAll(objectsWithoutUnsatisfiedDependencies);
            objectsReadyToPersist.forEach(object -> this.persisterHelper.logInformationAboutNotPersistedObject((ImportedObjectV2)object, SkippedObjectsReason.NOT_SATISFIED_DEPENDENCIES, (Collection<HibernateField>)Collections.emptySet()));
        }
    }

    private void registerContentPropertiesFromContentEntity(ImportedObjectV2 importedObjectV2) throws BackupRestoreException {
        Object contentPropertiesObject = importedObjectV2.getFieldValue("contentProperties");
        if (contentPropertiesObject == null) {
            return;
        }
        if (!Collection.class.isAssignableFrom(contentPropertiesObject.getClass())) {
            throw new BackupRestoreException("property entry 'contentProperties' for object '" + importedObjectV2.getId() + "' should be of type Collection");
        }
        Collection contentPropertiesCollection = (Collection)contentPropertiesObject;
        for (Object obj : contentPropertiesCollection) {
            if (obj instanceof Long) continue;
            throw new BackupRestoreException("contentProperty for object '" + importedObjectV2.getId() + "' is not of type Long");
        }
        Collection contentProperties = contentPropertiesCollection;
        this.contentPropertiesMap.putAll(contentProperties.stream().collect(Collectors.toMap(Function.identity(), value -> (Long)importedObjectV2.getId())));
    }

    private Long popContentIDForContentPropertyEntity(Long contentPropertyID) {
        return this.contentPropertiesMap.remove(contentPropertyID);
    }

    private void logInformationAboutNotPersistedObject(ImportedObjectV2 importedObject, SkippedObjectsReason skippedObjectsReason) {
        if (!this.foundInvalidContentProperties.getAndSet(true)) {
            log.warn("ContentPropertiesPersister detected invalid contentProperties during import which were ignored.");
        }
        this.persisterHelper.logInformationAboutNotPersistedObject(importedObject, skippedObjectsReason, Collections.emptyList());
    }
}

