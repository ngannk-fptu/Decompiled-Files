/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.FetchMode
 *  org.hibernate.boot.Metadata
 *  org.hibernate.mapping.Collection
 *  org.hibernate.mapping.Fetchable
 *  org.hibernate.mapping.PersistentClass
 *  org.hibernate.mapping.Property
 *  org.hibernate.mapping.Value
 */
package com.atlassian.confluence.importexport.impl;

import java.util.Iterator;
import org.hibernate.FetchMode;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Fetchable;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;

public final class BackupRestoreHibernateUtil {
    static void prepareConfigurationForBackupOperation(Metadata metadata) {
        for (PersistentClass persistentClass : metadata.getEntityBindings()) {
            BackupRestoreHibernateUtil.removeProxyConfigurationFromClass(persistentClass);
            BackupRestoreHibernateUtil.removeLazyLoadingFromMappedCollections(persistentClass);
        }
    }

    private static void removeProxyConfigurationFromClass(PersistentClass persistentClass) {
        persistentClass.setProxyInterfaceName(persistentClass.getMappedClass().getName());
    }

    private static void removeLazyLoadingFromMappedCollections(PersistentClass persistentClass) {
        Iterator propertyIterator = persistentClass.getPropertyIterator();
        while (propertyIterator.hasNext()) {
            Property property = (Property)propertyIterator.next();
            Value value = property.getValue();
            if (value instanceof Collection) {
                ((Collection)value).setLazy(true);
            }
            if (!(value instanceof Fetchable)) continue;
            ((Fetchable)value).setFetchMode(FetchMode.SELECT);
        }
    }
}

