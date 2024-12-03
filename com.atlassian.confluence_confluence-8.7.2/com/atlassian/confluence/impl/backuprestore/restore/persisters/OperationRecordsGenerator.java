/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OperationRecordsGenerator {
    private static final String ALLOWED_OPERATIONS_PROPERTY_NAME = "allowedOperations";
    private final ExportableEntityInfo operationEntityInfo;
    private final String idPropertyName;
    private final String operationTypePropertyName;

    public OperationRecordsGenerator(ExportableEntityInfo operationEntityInfo, String idPropertyName, String operationTypePropertyName) {
        this.operationEntityInfo = operationEntityInfo;
        this.idPropertyName = idPropertyName;
        this.operationTypePropertyName = operationTypePropertyName;
    }

    public Collection<ImportedObjectV2> prepareOperationRecords(ImportedObjectV2 importedObjectV2) throws BackupRestoreException {
        Object allowedOperationsObject = importedObjectV2.getFieldValue(ALLOWED_OPERATIONS_PROPERTY_NAME);
        if (allowedOperationsObject == null) {
            return Collections.emptySet();
        }
        if (!Set.class.isAssignableFrom(allowedOperationsObject.getClass())) {
            throw new BackupRestoreException(String.format("Property entry '%s' for object '%s' should be of type %s", ALLOWED_OPERATIONS_PROPERTY_NAME, importedObjectV2.getId(), Set.class.getName()));
        }
        Set allowedOperationsSet = (Set)allowedOperationsObject;
        long id = (Long)importedObjectV2.getId();
        return allowedOperationsSet.stream().map(operation -> this.generateOperationObject(id, operation)).collect(Collectors.toSet());
    }

    private ImportedObjectV2 generateOperationObject(Object id, Object operationType) {
        Map<String, Object> properties = Map.of(this.idPropertyName, id, this.operationTypePropertyName, operationType);
        return new ImportedObjectV2(this.operationEntityInfo, null, properties);
    }
}

