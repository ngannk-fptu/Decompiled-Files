/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ObjectUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.restore.EntityInfoSqlHelper;
import com.atlassian.confluence.impl.backuprestore.restore.dao.RestoreDao;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.IdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.Persister;
import com.atlassian.confluence.impl.backuprestore.statistics.OnObjectsProcessingHandler;
import com.atlassian.confluence.impl.backuprestore.statistics.SkippedObjectsReason;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabasePersister
implements Persister {
    private static final Logger log = LoggerFactory.getLogger(DatabasePersister.class);
    private final RestoreDao restoreDao;
    private final EntityInfoSqlHelper entityInfoSqlHelper;
    private final IdMapper idMapper;
    private final OnObjectsProcessingHandler onObjectsProcessingHandler;

    public DatabasePersister(RestoreDao restoreDao, EntityInfoSqlHelper entityInfoSqlHelper, IdMapper idMapper, OnObjectsProcessingHandler onObjectsProcessingHandler) {
        this.restoreDao = restoreDao;
        this.entityInfoSqlHelper = entityInfoSqlHelper;
        this.idMapper = idMapper;
        this.onObjectsProcessingHandler = onObjectsProcessingHandler;
    }

    @Override
    public boolean shouldPersist(Map<ExportableEntityInfo, Collection<ImportedObjectV2>> objectsWithDatabaseIdsByClass) {
        return true;
    }

    @Override
    public void persist(Map<ExportableEntityInfo, Collection<ImportedObjectV2>> objectsToPersistGroupedByClass) throws BackupRestoreException {
        LinkedHashMap<ExportableEntityInfo, Collection> objectsWithDatabaseIdsByClass = new LinkedHashMap<ExportableEntityInfo, Collection>();
        ArrayList allObjectsWithDatabaseIds = new ArrayList();
        objectsToPersistGroupedByClass.forEach((entityInfo, objectsWithXmlIds) -> {
            Collection<ImportedObjectV2> importedObjectsWithDatabaseIds = this.idMapper.prepareObjectsToBePersisted((ExportableEntityInfo)entityInfo, (Collection<ImportedObjectV2>)objectsWithXmlIds);
            allObjectsWithDatabaseIds.addAll(importedObjectsWithDatabaseIds);
            Collection importedObjectsWithoutEmptyFields = importedObjectsWithDatabaseIds.stream().filter(object -> !this.hasEmptyObligatoryFields((ExportableEntityInfo)entityInfo, (ImportedObjectV2)object)).collect(Collectors.toList());
            objectsWithDatabaseIdsByClass.put((ExportableEntityInfo)entityInfo, importedObjectsWithoutEmptyFields);
        });
        Collection recordsForBatchInserts = objectsWithDatabaseIdsByClass.entrySet().stream().map(entry -> {
            ExportableEntityInfo entityInfo = (ExportableEntityInfo)entry.getKey();
            Collection objectsWithDatabaseIds = (Collection)entry.getValue();
            return this.createRecordsForOneInsertBatch(entityInfo, new ArrayList<ImportedObjectV2>(objectsWithDatabaseIds));
        }).filter(Objects::nonNull).collect(Collectors.toList());
        Collection<ImportedObjectV2> persistedObjects = this.restoreDao.insertRecordsInTransaction(recordsForBatchInserts);
        this.sendNotificationsAboutPersistedObjects(persistedObjects);
        ArrayList<ImportedObjectV2> skippedObjects = new ArrayList<ImportedObjectV2>(allObjectsWithDatabaseIds);
        skippedObjects.removeAll(persistedObjects);
        this.sendNotificationsAboutSkippedObjects(skippedObjects, SkippedObjectsReason.INVALID_FIELDS);
        objectsWithDatabaseIdsByClass.forEach(this::markIdsAsPersisted);
    }

    private boolean hasEmptyObligatoryFields(ExportableEntityInfo entityInfo, ImportedObjectV2 importedObject) {
        for (HibernateField field : entityInfo.getPersistableFields()) {
            if (field.isNullable() || !ObjectUtils.isEmpty((Object)importedObject.getFieldValue(field.getPropertyName()))) continue;
            log.warn("Object has an empty obligatory field and will be skipped. Field: {}, Object: {}", (Object)field, (Object)importedObject);
            return true;
        }
        return false;
    }

    private void sendNotificationsAboutSkippedObjects(ArrayList<ImportedObjectV2> skippedObjects, SkippedObjectsReason skippedObjectsReason) throws BackupRestoreException {
        this.onObjectsProcessingHandler.onObjectsSkipping(skippedObjects, skippedObjectsReason);
    }

    private void sendNotificationsAboutPersistedObjects(Collection<ImportedObjectV2> persistedObjects) throws BackupRestoreException {
        this.onObjectsProcessingHandler.onObjectsPersist(persistedObjects);
    }

    private RestoreDao.RecordsForBatchInsert createRecordsForOneInsertBatch(ExportableEntityInfo entityInfo, List<ImportedObjectV2> objectsWithDatabaseIds) {
        log.debug("Generating data for insert for {} records of the type {}", (Object)objectsWithDatabaseIds.size(), entityInfo.getEntityClass());
        if (objectsWithDatabaseIds.isEmpty()) {
            return null;
        }
        HashMap[] valuesForTheWholeBatch = new HashMap[objectsWithDatabaseIds.size()];
        int counter = 0;
        for (ImportedObjectV2 objectToPersist : objectsWithDatabaseIds) {
            Map<String, Object> idRecord = this.entityInfoSqlHelper.createValuesForInsert(objectToPersist);
            valuesForTheWholeBatch[counter++] = idRecord;
        }
        return new RestoreDao.RecordsForBatchInsert(this.entityInfoSqlHelper.getInsertQuery(entityInfo), valuesForTheWholeBatch, objectsWithDatabaseIds);
    }

    private void markIdsAsPersisted(ExportableEntityInfo entityInfo, Collection<ImportedObjectV2> importedObjects) {
        List<Object> ids = importedObjects.stream().map(ImportedObjectV2::getId).filter(Objects::nonNull).collect(Collectors.toList());
        this.idMapper.markObjectsAsPersisted(entityInfo, ids);
    }
}

