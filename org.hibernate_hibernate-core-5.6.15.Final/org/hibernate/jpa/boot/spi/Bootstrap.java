/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.spi.PersistenceUnitInfo
 *  javax.persistence.spi.PersistenceUnitTransactionType
 */
package org.hibernate.jpa.boot.spi;

import java.net.URL;
import java.util.Map;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.hibernate.jpa.boot.internal.PersistenceXmlParser;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;

public final class Bootstrap {
    private Bootstrap() {
    }

    public static EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(PersistenceUnitDescriptor persistenceUnitDescriptor, Map integration) {
        return new EntityManagerFactoryBuilderImpl(persistenceUnitDescriptor, integration);
    }

    public static EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(URL persistenceXmlUrl, String persistenceUnitName, Map integration) {
        return Bootstrap.getEntityManagerFactoryBuilder(persistenceXmlUrl, persistenceUnitName, PersistenceUnitTransactionType.RESOURCE_LOCAL, integration);
    }

    public static EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(URL persistenceXmlUrl, String persistenceUnitName, PersistenceUnitTransactionType transactionType, Map integration) {
        return new EntityManagerFactoryBuilderImpl(PersistenceXmlParser.parse(persistenceXmlUrl, transactionType, integration).get(persistenceUnitName), integration);
    }

    public static EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(PersistenceUnitDescriptor persistenceUnitDescriptor, Map integration, ClassLoader providedClassLoader) {
        return new EntityManagerFactoryBuilderImpl(persistenceUnitDescriptor, integration, providedClassLoader);
    }

    public static EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(PersistenceUnitDescriptor persistenceUnitDescriptor, Map integration, ClassLoaderService providedClassLoaderService) {
        return new EntityManagerFactoryBuilderImpl(persistenceUnitDescriptor, integration, providedClassLoaderService);
    }

    public static EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(PersistenceUnitInfo persistenceUnitInfo, Map integration) {
        return Bootstrap.getEntityManagerFactoryBuilder(new PersistenceUnitInfoDescriptor(persistenceUnitInfo), integration);
    }

    public static EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(PersistenceUnitInfo persistenceUnitInfo, Map integration, ClassLoader providedClassLoader) {
        return Bootstrap.getEntityManagerFactoryBuilder((PersistenceUnitDescriptor)new PersistenceUnitInfoDescriptor(persistenceUnitInfo), integration, providedClassLoader);
    }

    public static EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(PersistenceUnitInfo persistenceUnitInfo, Map integration, ClassLoaderService providedClassLoaderService) {
        return Bootstrap.getEntityManagerFactoryBuilder((PersistenceUnitDescriptor)new PersistenceUnitInfoDescriptor(persistenceUnitInfo), integration, providedClassLoaderService);
    }
}

