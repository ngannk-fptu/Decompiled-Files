/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.EntityType
 */
package org.hibernate.metamodel.model.domain.spi;

import javax.persistence.metamodel.EntityType;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.model.domain.spi.EmbeddedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.EntityTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;

public class DomainModelHelper {
    public static EntityPersister resolveEntityPersister(EntityTypeDescriptor<?> entityType, SessionFactoryImplementor sessionFactory) {
        String hibernateEntityName = entityType.getName();
        return sessionFactory.getMetamodel().entityPersister(hibernateEntityName);
    }

    public static <T, S extends T> ManagedTypeDescriptor<S> resolveSubType(ManagedTypeDescriptor<T> baseType, String subTypeName, SessionFactoryImplementor sessionFactory) {
        MetamodelImplementor metamodel = sessionFactory.getMetamodel();
        if (baseType instanceof EmbeddedTypeDescriptor) {
            return baseType;
        }
        String importedClassName = metamodel.getImportedClassName(subTypeName);
        if (importedClassName != null) {
            EntityType subManagedType = metamodel.entity(importedClassName);
            if (subManagedType != null) {
                return subManagedType;
            }
            try {
                Class subTypeClass = sessionFactory.getServiceRegistry().getService(ClassLoaderService.class).classForName(importedClassName);
                return metamodel.managedType(subTypeClass);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        throw new IllegalArgumentException("Unknown sub-type name (" + baseType.getName() + ") : " + subTypeName);
    }

    public static <S> ManagedTypeDescriptor<S> resolveSubType(ManagedTypeDescriptor<? super S> baseType, Class<S> subTypeClass, SessionFactoryImplementor sessionFactory) {
        MetamodelImplementor metamodel = sessionFactory.getMetamodel();
        return metamodel.managedType(subTypeClass);
    }
}

