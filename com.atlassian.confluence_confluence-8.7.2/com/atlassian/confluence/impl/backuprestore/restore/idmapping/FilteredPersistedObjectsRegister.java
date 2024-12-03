/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.idmapping;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.PersistedObjectsRegister;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class FilteredPersistedObjectsRegister
implements PersistedObjectsRegister {
    private final PersistedObjectsRegister realPersister;
    private final Collection<Class<?>> entityClassesWithIncomingReferencesOnly;

    public FilteredPersistedObjectsRegister(PersistedObjectsRegister realPersister, Collection<ExportableEntityInfo> importableEntitiesInfo) {
        this.realPersister = realPersister;
        this.entityClassesWithIncomingReferencesOnly = this.getEntitiesWithIncomingReferencesOnly(importableEntitiesInfo);
    }

    @Override
    public boolean isPersistedDatabaseId(Class<?> entityClass, Object databaseId) {
        if (!this.isEntityClassHasToBeRegistered(entityClass)) {
            throw new IllegalArgumentException("isPersistedDatabaseId got an entity class " + entityClass + " which must be called because it does not have any incoming referenced. Dev bug?");
        }
        return this.realPersister.isPersistedDatabaseId(entityClass, databaseId);
    }

    @Override
    public void markIdsAsPersisted(Class<?> entityClass, List<Object> ids) {
        if (this.isEntityClassHasToBeRegistered(entityClass)) {
            this.realPersister.markIdsAsPersisted(entityClass, ids);
        }
    }

    private boolean isEntityClassHasToBeRegistered(Class<?> entityClass) {
        return this.entityClassesWithIncomingReferencesOnly.contains(entityClass);
    }

    private Collection<Class<?>> getEntitiesWithIncomingReferencesOnly(Collection<ExportableEntityInfo> importableEntitiesInfo) {
        HashSet allReferencedClasses = new HashSet();
        importableEntitiesInfo.forEach(exportableEntityInfo -> allReferencedClasses.addAll(exportableEntityInfo.getAllExternalReferences().stream().map(HibernateField::getReferencedClass).collect(Collectors.toSet())));
        return importableEntitiesInfo.stream().map(ExportableEntityInfo::getEntityClass).map(entityClass -> ContentEntityObject.class.isAssignableFrom((Class<?>)entityClass) ? ContentEntityObject.class : entityClass).filter(allReferencedClasses::contains).collect(Collectors.toSet());
    }
}

