/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.user.propertyset.BucketPropertySetItem
 *  com.atlassian.confluence.api.model.backuprestore.JobSource
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.idmapping;

import bucket.user.propertyset.BucketPropertySetItem;
import com.atlassian.confluence.api.model.backuprestore.JobSource;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.restore.dao.RestoreDao;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.AbstractIdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.FakeObjectProvider;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.PersistedObjectsRegister;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders.ExistingEntityFinder;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders.ExistingEntityFindersProvider;
import com.atlassian.confluence.impl.backuprestore.statistics.OnObjectsProcessingHandler;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceRestoreIdMapper
extends AbstractIdMapper {
    private static final Logger log = LoggerFactory.getLogger(SpaceRestoreIdMapper.class);
    private final ExistingEntityFindersProvider entityFinderProvider;
    private final RestoreDao restoreDao;
    private final JobSource jobSource;
    private final FakeObjectProvider fakeObjectProvider;
    private final Map<Class<?>, XmlIdToDatabaseIdMap> idMap = new ConcurrentHashMap();
    private final OnObjectsProcessingHandler onObjectsProcessingHandler;

    public SpaceRestoreIdMapper(PersistedObjectsRegister persistedObjectsRegister, RestoreDao restoreDao, ExistingEntityFindersProvider entityFinderProvider, FakeObjectProvider fakeObjectProvider, JobSource jobSource, OnObjectsProcessingHandler onObjectsProcessingHandler) {
        super(persistedObjectsRegister);
        this.restoreDao = restoreDao;
        this.entityFinderProvider = entityFinderProvider;
        this.fakeObjectProvider = fakeObjectProvider;
        this.jobSource = jobSource;
        this.onObjectsProcessingHandler = onObjectsProcessingHandler;
    }

    @Override
    public Object getDatabaseId(Class<?> clazz, Object xmlId) {
        Class<?> entityClass = this.fixContentEntityObjectClass(clazz);
        return this.idMap.computeIfAbsent(entityClass, eClass -> new XmlIdToDatabaseIdMap()).getDatabaseId(xmlId);
    }

    @Override
    public Collection<ImportedObjectV2> prepareObjectsToBePersisted(ExportableEntityInfo entityInfo, Collection<ImportedObjectV2> importedObjects) {
        StopWatch stopWatch = StopWatch.createStarted();
        Collection<ImportedObjectV2> importedObjectsWithNewIds = entityInfo.getEntityClass().equals(BucketPropertySetItem.class) ? this.replaceBucketPropertySetItemIds(importedObjects) : this.replaceIds(entityInfo, importedObjects);
        log.debug("Generated a collection of {} new ids of type {}. Duration: {}", new Object[]{importedObjects.size(), entityInfo.getEntityClass().getSimpleName(), stopWatch});
        return importedObjectsWithNewIds;
    }

    private Collection<ImportedObjectV2> replaceIds(ExportableEntityInfo entityInfo, Collection<ImportedObjectV2> importedObjects) {
        Map<ImportedObjectV2, Object> objectToDatabaseIdMap = this.generateOrFindDatabaseIds(entityInfo, importedObjects);
        return objectToDatabaseIdMap.entrySet().stream().map(entry -> {
            ImportedObjectV2 importedObject = (ImportedObjectV2)entry.getKey();
            Map<String, Object> propertiesToOverride = this.createMapOfDatabaseIds(importedObject, importedObject.getEntityInfo().getAllExternalReferences());
            return importedObject.overridePropertyValues(entry.getValue(), propertiesToOverride);
        }).collect(Collectors.toList());
    }

    private Collection<ImportedObjectV2> replaceBucketPropertySetItemIds(Collection<ImportedObjectV2> importedObjects) {
        return importedObjects.stream().map(item -> {
            ArrayList xmlCompositeId = (ArrayList)item.getId();
            Object xmlEntityId = xmlCompositeId.get(1);
            Object databaseEntityId = this.getDatabaseId(ContentEntityObject.class, xmlEntityId);
            if (databaseEntityId != null) {
                List newCompositeId = xmlCompositeId.stream().map(id -> id instanceof Long ? databaseEntityId : id).collect(Collectors.toList());
                return item.overridePropertyValues(newCompositeId, Collections.emptyMap());
            }
            return item;
        }).collect(Collectors.toList());
    }

    private Map<ImportedObjectV2, Object> generateOrFindDatabaseIds(ExportableEntityInfo entityInfo, Collection<ImportedObjectV2> importingObjects) {
        HashSet<ImportedObjectV2> pendingImportingObjects = new HashSet<ImportedObjectV2>(importingObjects);
        ExistingEntityFinder existingEntityFinder = this.entityFinderProvider.getExistingEntityFinder(entityInfo.getEntityClass(), this.jobSource);
        if (existingEntityFinder != null) {
            String profileName = "generateOrFindDatabaseIds: usingEntityFinder: " + existingEntityFinder.getClass().getName();
            try (Ticker ignored = Timers.start((String)profileName);){
                Map<ImportedObjectV2, Object> importingObjectToNewIdMap = existingEntityFinder.findExistingObjectIds(importingObjects);
                for (Map.Entry<ImportedObjectV2, Object> importingObjectToNewIdEntry : importingObjectToNewIdMap.entrySet()) {
                    ImportedObjectV2 importingObject = importingObjectToNewIdEntry.getKey();
                    Object xmlId = importingObject.getId();
                    Object databaseId = importingObjectToNewIdEntry.getValue();
                    pendingImportingObjects.remove(importingObject);
                    this.addXmlIdToDatabaseIdMapping(entityInfo.getEntityClass(), xmlId, databaseId);
                    this.markObjectsAsPersisted(entityInfo, Collections.singletonList(databaseId));
                    this.onObjectsProcessingHandler.onObjectsReusing(Collections.singleton(importingObjectToNewIdEntry.getKey()));
                }
            }
        }
        return this.generateDatabaseIds(entityInfo, pendingImportingObjects);
    }

    private Map<ImportedObjectV2, Object> generateDatabaseIds(ExportableEntityInfo entityInfo, Set<ImportedObjectV2> importingObjects) {
        if (importingObjects.isEmpty()) {
            return Collections.emptyMap();
        }
        if (entityInfo.getId() == null) {
            return importingObjects.stream().collect(HashMap::new, (map, value) -> map.put(value, null), HashMap::putAll);
        }
        Object fakeObject = this.fakeObjectProvider.getFakeObjectForIdGeneration(entityInfo.getEntityClass());
        Collection<Object> newlyGeneratedIds = this.restoreDao.generateIds(entityInfo.getIdentifierGenerator(), fakeObject, importingObjects.size());
        if (newlyGeneratedIds.size() != importingObjects.size()) {
            throw new IllegalStateException("Requested to generate " + importingObjects.size() + " ids, but received " + newlyGeneratedIds.size());
        }
        HashMap<ImportedObjectV2, Object> importedObjectToDatabaseIdMap = new HashMap<ImportedObjectV2, Object>();
        Iterator<ImportedObjectV2> objectsIterator = importingObjects.iterator();
        for (Object databaseId : newlyGeneratedIds) {
            ImportedObjectV2 importedObjectV2 = objectsIterator.next();
            this.addXmlIdToDatabaseIdMapping(entityInfo.getEntityClass(), importedObjectV2.getId(), databaseId);
            importedObjectToDatabaseIdMap.put(importedObjectV2, databaseId);
        }
        return importedObjectToDatabaseIdMap;
    }

    private Map<String, Object> createMapOfDatabaseIds(ImportedObjectV2 importedObject, Collection<HibernateField> references) {
        HashMap<String, Object> mapOfDatabaseIds = new HashMap<String, Object>();
        for (HibernateField reference : references) {
            String propertyName = reference.getPropertyName();
            Object referencedValue = importedObject.getFieldValue(propertyName);
            if (referencedValue == null) continue;
            Object newReferenceValue = this.getDatabaseId(reference.getReferencedClass(), referencedValue);
            mapOfDatabaseIds.put(propertyName, newReferenceValue);
        }
        return mapOfDatabaseIds;
    }

    private void addXmlIdToDatabaseIdMapping(Class<?> clazz, Object xmlId, Object databaseId) {
        Class<?> entityClass = this.fixContentEntityObjectClass(clazz);
        this.idMap.computeIfAbsent(entityClass, eClass -> new XmlIdToDatabaseIdMap()).addDatabaseIdToXmlIdMapping(xmlId, databaseId);
    }

    private static class XmlIdToDatabaseIdMap {
        private final Map<Object, Object> idMap = new ConcurrentHashMap<Object, Object>();

        private XmlIdToDatabaseIdMap() {
        }

        void addDatabaseIdToXmlIdMapping(Object xmlId, Object databaseId) {
            this.idMap.put(xmlId, databaseId);
        }

        Object getDatabaseId(Object xmlId) {
            return this.idMap.get(xmlId);
        }
    }
}

