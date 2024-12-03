/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class AttributeRecordsGenerator {
    private static final String ATTRIBUTES_PROPERTY_NAME = "attributes";
    private static final String ATTRIBUTE_NAME_PROPERTY_NAME = "attributeName";
    private static final String ATTRIBUTE_VALUE_PROPERTY_NAME = "attributeValue";
    private final ExportableEntityInfo attributeEntityInfo;
    private final String idPropertyName;

    public AttributeRecordsGenerator(ExportableEntityInfo attributeEntityInfo, String idPropertyName) {
        this.attributeEntityInfo = attributeEntityInfo;
        this.idPropertyName = idPropertyName;
    }

    public Collection<ImportedObjectV2> prepareAttributeRecords(ImportedObjectV2 importedObjectV2) throws BackupRestoreException {
        Object attributesObject = importedObjectV2.getFieldValue(ATTRIBUTES_PROPERTY_NAME);
        if (attributesObject == null) {
            return Collections.emptyList();
        }
        if (!Map.class.isAssignableFrom(attributesObject.getClass())) {
            throw new BackupRestoreException(String.format("Property entry '%s' for object '%s' should be of type %s", ATTRIBUTES_PROPERTY_NAME, importedObjectV2.getId(), Map.class.getName()));
        }
        Map attributesMap = (Map)attributesObject;
        long id = (Long)importedObjectV2.getId();
        ArrayList<ImportedObjectV2> attributeRecords = new ArrayList<ImportedObjectV2>();
        attributesMap.forEach((attributeName, attributeValue) -> attributeRecords.add(this.generateOperationObject(id, attributeName, attributeValue)));
        return attributeRecords;
    }

    private ImportedObjectV2 generateOperationObject(Object id, Object attributeName, Object attributeValue) {
        Map<String, Object> properties = Map.of(this.idPropertyName, id, ATTRIBUTE_NAME_PROPERTY_NAME, attributeName, ATTRIBUTE_VALUE_PROPERTY_NAME, attributeValue);
        return new ImportedObjectV2(this.attributeEntityInfo, null, properties);
    }
}

