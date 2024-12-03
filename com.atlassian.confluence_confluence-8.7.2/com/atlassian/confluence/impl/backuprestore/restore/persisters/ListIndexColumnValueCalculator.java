/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ListIndexColumnValueCalculator {
    private static final String LIST_INDEX_PROPERTY_NAME = "listIndex";
    private final String collectionPropertyName;
    private final String referencePropertyName;
    private final Map<Object, HashMap<Object, Integer>> referencedRecordsMap = new ConcurrentHashMap<Object, HashMap<Object, Integer>>();

    public ListIndexColumnValueCalculator(String collectionPropertyName, String referencePropertyName) {
        this.collectionPropertyName = collectionPropertyName;
        this.referencePropertyName = referencePropertyName;
    }

    public void trackRecordsOrderInList(ImportedObjectV2 importedObjectV2) throws BackupRestoreException {
        Object collectionObject = importedObjectV2.getFieldValue(this.collectionPropertyName);
        if (collectionObject == null) {
            return;
        }
        if (!List.class.isAssignableFrom(collectionObject.getClass())) {
            throw new BackupRestoreException(String.format("property entry '%s' for object '%s' should be of type List", this.collectionPropertyName, importedObjectV2.getId()));
        }
        HashMap recordIdToPositionInListMap = new HashMap();
        List collectionOfRecordsWithSpecificOrder = (List)collectionObject;
        for (int i = 0; i < collectionOfRecordsWithSpecificOrder.size(); ++i) {
            recordIdToPositionInListMap.put(collectionOfRecordsWithSpecificOrder.get(i), i);
        }
        this.referencedRecordsMap.put(importedObjectV2.getId(), recordIdToPositionInListMap);
    }

    public Optional<ImportedObjectV2> resolveListIndexProperty(ImportedObjectV2 importedObject) {
        if (importedObject.getFieldValue(LIST_INDEX_PROPERTY_NAME) != null) {
            return Optional.of(importedObject);
        }
        Integer listIndex = this.getListIndexValue(importedObject);
        if (listIndex == null) {
            return Optional.empty();
        }
        Map<String, Object> propertiesToOverride = Map.of(LIST_INDEX_PROPERTY_NAME, listIndex);
        return Optional.of(importedObject.overridePropertyValues(importedObject.getId(), propertiesToOverride));
    }

    private Integer getListIndexValue(ImportedObjectV2 importedObject) {
        HashMap<Object, Integer> recordIdToPositionInListMap;
        Object referencedRecordId = importedObject.getFieldValue(this.referencePropertyName);
        if (referencedRecordId != null && (recordIdToPositionInListMap = this.referencedRecordsMap.get(referencedRecordId)) != null) {
            return recordIdToPositionInListMap.get(importedObject.getId());
        }
        return null;
    }
}

