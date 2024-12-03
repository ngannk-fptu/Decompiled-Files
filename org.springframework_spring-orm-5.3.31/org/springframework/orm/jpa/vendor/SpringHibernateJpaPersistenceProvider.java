/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.spi.PersistenceUnitInfo
 *  org.hibernate.jpa.HibernatePersistenceProvider
 *  org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl
 *  org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor
 *  org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor
 */
package org.springframework.orm.jpa.vendor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.springframework.orm.jpa.persistenceunit.SmartPersistenceUnitInfo;

class SpringHibernateJpaPersistenceProvider
extends HibernatePersistenceProvider {
    SpringHibernateJpaPersistenceProvider() {
    }

    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map properties) {
        final ArrayList<String> mergedClassesAndPackages = new ArrayList<String>(info.getManagedClassNames());
        if (info instanceof SmartPersistenceUnitInfo) {
            mergedClassesAndPackages.addAll(((SmartPersistenceUnitInfo)info).getManagedPackages());
        }
        return new EntityManagerFactoryBuilderImpl((PersistenceUnitDescriptor)new PersistenceUnitInfoDescriptor(info){

            public List<String> getManagedClassNames() {
                return mergedClassesAndPackages;
            }
        }, properties).build();
    }
}

