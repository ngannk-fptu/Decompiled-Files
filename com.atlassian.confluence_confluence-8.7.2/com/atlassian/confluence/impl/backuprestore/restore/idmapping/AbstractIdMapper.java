/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.user.propertyset.BucketPropertySetItem
 */
package com.atlassian.confluence.impl.backuprestore.restore.idmapping;

import bucket.user.propertyset.BucketPropertySetItem;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.IdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.PersistedObjectsRegister;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractIdMapper
implements IdMapper {
    protected final PersistedObjectsRegister persistedObjectsRegister;
    private static final int ENTITY_NAME_FIELD_INDEX = 0;
    private static final int ENTITY_ID_FIELD_INDEX = 1;

    protected AbstractIdMapper(PersistedObjectsRegister persistedObjectsRegister) {
        this.persistedObjectsRegister = persistedObjectsRegister;
    }

    @Override
    public boolean isPersistedXmlId(Class<?> clazz, Object xmlId) {
        Class<?> entityClass = this.fixContentEntityObjectClass(clazz);
        Objects.requireNonNull(xmlId, () -> "Original ID can't be null (entity class: " + entityClass.getName() + ")");
        Object databaseId = this.getDatabaseId(entityClass, xmlId);
        if (databaseId == null) {
            return false;
        }
        return this.persistedObjectsRegister.isPersistedDatabaseId(entityClass, databaseId);
    }

    @Override
    public void markObjectsAsPersisted(ExportableEntityInfo entityInfo, List<Object> databaseObjectIds) {
        this.persistedObjectsRegister.markIdsAsPersisted(this.fixContentEntityObjectClass(entityInfo.getEntityClass()), databaseObjectIds);
    }

    @Override
    public Collection<HibernateField> getAllNotSatisfiedDependencies(ImportedObjectV2 importedObject) {
        if (importedObject.getEntityClass().equals(BucketPropertySetItem.class)) {
            return this.getUnsatisfiedBucketPropertySetItemReference(importedObject);
        }
        Collection<HibernateField> allReferences = importedObject.getEntityInfo().getAllExternalReferences();
        return allReferences.stream().filter(reference -> !this.isReferenceSatisfied((HibernateField)reference, importedObject)).collect(Collectors.toList());
    }

    protected Class<?> fixContentEntityObjectClass(Class<?> clazz) {
        return ContentEntityObject.class.isAssignableFrom(clazz) ? ContentEntityObject.class : clazz;
    }

    private boolean isReferenceSatisfied(HibernateField reference, ImportedObjectV2 importedObject) {
        String propertyName = reference.getPropertyName();
        Object value = importedObject.getFieldValue(propertyName);
        if (value == null) {
            return true;
        }
        return this.isPersistedXmlId(reference.getReferencedClass(), value);
    }

    private Collection<HibernateField> getUnsatisfiedBucketPropertySetItemReference(ImportedObjectV2 importedObject) {
        ArrayList bucketPropertyIDs = (ArrayList)importedObject.getId();
        String bucketPropertyName = (String)bucketPropertyIDs.get(0);
        Long bucketPropertyReference = (Long)bucketPropertyIDs.get(1);
        if (!bucketPropertyName.equals("confluence_ContentEntityObject")) {
            return Collections.emptyList();
        }
        if (bucketPropertyReference == 0L) {
            return Collections.singleton(importedObject.getEntityInfo().getId());
        }
        return this.isPersistedXmlId(ContentEntityObject.class, bucketPropertyReference) ? Collections.emptyList() : Collections.singleton(importedObject.getEntityInfo().getId());
    }
}

