/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.ApplicationImpl
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 *  com.atlassian.diagnostics.Severity
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.type.CustomType
 *  org.hibernate.type.Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.domain;

import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateMetadataHelper;
import com.atlassian.confluence.impl.backuprestore.restore.EntityInfoSqlHelper;
import com.atlassian.confluence.importexport.xmlimport.model.CollectionProperty;
import com.atlassian.confluence.importexport.xmlimport.model.ComponentProperty;
import com.atlassian.confluence.importexport.xmlimport.model.CompositeId;
import com.atlassian.confluence.importexport.xmlimport.model.ContentTypeEnumProperty;
import com.atlassian.confluence.importexport.xmlimport.model.EnumProperty;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveId;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveProperty;
import com.atlassian.confluence.importexport.xmlimport.model.ReferenceProperty;
import com.atlassian.crowd.model.application.ApplicationImpl;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import com.atlassian.diagnostics.Severity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.CustomType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportedObjectV2
implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(ImportedObjectV2.class);
    private static final long serialVersionUID = 3070937942528383244L;
    final Map<String, Object> originalPropertyValueMap;
    private final Class<?> entityClass;
    private final transient ExportableEntityInfo entityInfo;
    private final Object id;

    public static ImportedObjectV2 fromLegacyImportedObject(ImportedObject legacyImportedObject, ExportableEntityInfo exportableEntityInfo, EntityInfoSqlHelper entityInfoSqlHelper, HibernateMetadataHelper hibernateMetadataHelper) throws ClassNotFoundException {
        Object originalId = ImportedObjectV2.extractOriginalIdValue(legacyImportedObject.getIdProperty(), legacyImportedObject.getCompositeId(), exportableEntityInfo, entityInfoSqlHelper);
        return new ImportedObjectV2(exportableEntityInfo, originalId, ImportedObjectV2.buildProperties(originalId, exportableEntityInfo, legacyImportedObject.getProperties(), entityInfoSqlHelper, hibernateMetadataHelper));
    }

    public ImportedObjectV2 overridePropertyValues(Object newId, Map<String, Object> propertiesToOverride) {
        HashMap<String, Object> newProperties = new HashMap<String, Object>(this.originalPropertyValueMap);
        newProperties.putAll(propertiesToOverride);
        return new ImportedObjectV2(this.entityInfo, newId, newProperties);
    }

    public ImportedObjectV2(ExportableEntityInfo exportableEntityInfo, Object id, Map<String, Object> propertyValueMap) {
        this.id = id;
        this.entityClass = exportableEntityInfo.getEntityClass();
        this.entityInfo = exportableEntityInfo;
        this.originalPropertyValueMap = Collections.unmodifiableMap(propertyValueMap);
    }

    public ImportedObjectV2 clearValues(Collection<String> propertiesToClear) {
        HashMap<String, Object> newProperties = new HashMap<String, Object>(this.originalPropertyValueMap);
        for (String propertyName : propertiesToClear) {
            newProperties.remove(propertyName);
        }
        return new ImportedObjectV2(this.entityInfo, this.id, newProperties);
    }

    private static Object extractOriginalIdValue(PrimitiveId idProperty, CompositeId compositeId, ExportableEntityInfo entityInfo, EntityInfoSqlHelper entityInfoSqlHelper) {
        if (compositeId != null) {
            ArrayList<Object> idValues = new ArrayList<Object>(compositeId.getProperties().size());
            List<Type> idSubTypes = entityInfo.getId().getTypes();
            List<String> idPropertyNames = entityInfo.getId().getPropertyNames();
            for (int i = 0; i < idSubTypes.size(); ++i) {
                Type subtype = idSubTypes.get(i);
                String propertyName = idPropertyNames.get(i);
                String value = compositeId.getPropertyValue(propertyName);
                idValues.add(entityInfoSqlHelper.convertToDbReadyValue(subtype, value));
            }
            return idValues;
        }
        return entityInfoSqlHelper.convertToDbReadyValue(entityInfo.getId().getType(), idProperty.getValue());
    }

    public ExportableEntityInfo getEntityInfo() {
        return this.entityInfo;
    }

    public Object getFieldValue(String propertyName) {
        return this.originalPropertyValueMap.get(propertyName);
    }

    public Class<?> getEntityClass() {
        return this.entityClass;
    }

    public Object getId() {
        return this.id;
    }

    private static Map<String, Object> buildProperties(Object originalId, ExportableEntityInfo exportableEntityInfo, Collection<ImportedProperty> properties, EntityInfoSqlHelper entityInfoSqlHelper, HibernateMetadataHelper hibernateMetadataHelper) throws ClassNotFoundException {
        HashMap<String, Object> propertyMap = new HashMap<String, Object>();
        for (ImportedProperty property : properties) {
            String propertyName = property.getName();
            Type hibernateType = ImportedObjectV2.getHibernateType(exportableEntityInfo, propertyName);
            if (hibernateType != null) {
                Object dbReadyValue = ImportedObjectV2.convertPropertyToDbReadyValue(exportableEntityInfo, property, hibernateType, entityInfoSqlHelper, hibernateMetadataHelper);
                if (dbReadyValue == null) continue;
                propertyMap.put(propertyName, dbReadyValue);
                continue;
            }
            log.warn("Unexpected imported property '{}' will be skipped. Entity class '{}', id '{}'.", new Object[]{property, exportableEntityInfo.getEntityClass().getName(), originalId});
        }
        return propertyMap;
    }

    private static Object convertPropertyToDbReadyValue(ExportableEntityInfo exportableEntityInfo, ImportedProperty property, Type hibernateType, EntityInfoSqlHelper entityInfoSqlHelper, HibernateMetadataHelper hibernateMetadataHelper) throws ClassNotFoundException {
        if (property instanceof CollectionProperty) {
            return ImportedObjectV2.convertCollectionToDbReadyValue((CollectionProperty)property, exportableEntityInfo);
        }
        if (property instanceof PrimitiveProperty) {
            return entityInfoSqlHelper.convertToDbReadyValue(hibernateType, ((PrimitiveProperty)property).getValue());
        }
        if (property instanceof ReferenceProperty) {
            ReferenceProperty referenceProperty = (ReferenceProperty)property;
            ExportableEntityInfo referencedEntityInfo = hibernateMetadataHelper.getEntityInfoByClass(hibernateType.getReturnedClass());
            Type referencedEntityType = referencedEntityInfo.getId().getType();
            return entityInfoSqlHelper.convertToDbReadyValue(referencedEntityType, referenceProperty.getId().getValue());
        }
        if (property instanceof ContentTypeEnumProperty) {
            return ((ContentTypeEnumProperty)property).getEnumValueByRepresentation();
        }
        if (property instanceof EnumProperty) {
            return ImportedObjectV2.convertEnumToToDbValue(hibernateType, ((EnumProperty)property).getValue());
        }
        if (property instanceof ComponentProperty) {
            ComponentProperty componentProperty = (ComponentProperty)property;
            if (!componentProperty.getName().equals("credential")) {
                throw new IllegalArgumentException(String.format("Unknown component type for component %s", componentProperty.getName()));
            }
            return componentProperty.getPropertyStringValue("credential");
        }
        throw new IllegalArgumentException(String.format("Unexpected property of class %s. Property name %s", property.getClass().getName(), property.getName()));
    }

    private static Object convertEnumToToDbValue(Type hibernateType, String value) {
        int[] sqlTypes = ((CustomType)hibernateType).getUserType().sqlTypes();
        if (sqlTypes.length != 1) {
            throw new IllegalStateException("Composite columns with enums are not supported at the moment.");
        }
        int sqlType = sqlTypes[0];
        if (sqlType == 4 || sqlType == -5) {
            return ImportedObjectV2.getEnumId(hibernateType.getReturnedClass(), value);
        }
        return value;
    }

    private static int getEnumId(Class<?> returnedClass, String value) {
        if (returnedClass.equals(Severity.class)) {
            if (StringUtils.isNumeric((CharSequence)value)) {
                return Integer.parseInt(value);
            }
            return Severity.valueOf((String)value).getId();
        }
        throw new IllegalArgumentException("Unsupported enum: " + returnedClass);
    }

    private static Object convertCollectionToDbReadyValue(CollectionProperty property, ExportableEntityInfo exportableEntityInfo) {
        switch (property.getName()) {
            case "contentProperties": 
            case "directoryMappings": 
            case "credentialRecords": {
                return ImportedObjectV2.convertListOfReferences(property);
            }
            case "allowedOperations": {
                return ImportedObjectV2.convertSetOfEnums(property);
            }
            case "attributes": {
                if (ApplicationImpl.class.equals(exportableEntityInfo.getEntityClass()) || DirectoryImpl.class.equals(exportableEntityInfo.getEntityClass())) {
                    return ImportedObjectV2.convertMapOfAttributes(property);
                }
                return null;
            }
        }
        return null;
    }

    private static Object convertListOfReferences(CollectionProperty property) {
        return property.getValues().stream().filter(ReferenceProperty.class::isInstance).map(value -> Long.parseLong(((ReferenceProperty)value).getId().getValue())).collect(Collectors.toList());
    }

    private static Object convertSetOfEnums(CollectionProperty property) {
        return property.getValues().stream().filter(EnumProperty.class::isInstance).map(value -> ((EnumProperty)value).getValue()).collect(Collectors.toSet());
    }

    private static Object convertMapOfAttributes(CollectionProperty property) {
        return property.getValues().stream().filter(PrimitiveProperty.class::isInstance).map(PrimitiveProperty.class::cast).collect(Collectors.toMap(ImportedProperty::getName, PrimitiveProperty::getValue));
    }

    private static Type getHibernateType(ExportableEntityInfo exportableEntityInfo, String propertyName) {
        return exportableEntityInfo.getHibernateTypeByFieldName(propertyName);
    }

    public Map<String, Object> getPropertyValueMap() {
        return this.originalPropertyValueMap;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        ImportedObjectV2 that = (ImportedObjectV2)obj;
        return Objects.equals(this.entityClass, that.entityClass) && Objects.equals(this.entityInfo, that.entityInfo) && Objects.equals(this.id, that.id) && Objects.equals(this.originalPropertyValueMap, that.originalPropertyValueMap);
    }

    public int hashCode() {
        return Objects.hash(this.entityClass, this.entityInfo, this.id, this.originalPropertyValueMap);
    }

    public String toString() {
        return "ImportedObjectV2{id=" + this.id + ", entityClass=" + this.entityClass + ", originalPropertyValueMap=" + this.originalPropertyValueMap + "}";
    }
}

