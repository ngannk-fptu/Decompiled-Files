/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.PersistenceException
 *  javax.persistence.spi.LoadState
 *  javax.persistence.spi.PersistenceProvider
 *  javax.persistence.spi.PersistenceUnitInfo
 *  javax.persistence.spi.ProviderUtil
 *  org.jboss.logging.Logger
 */
package org.hibernate.jpa;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.spi.LoadState;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.internal.PersistenceXmlParser;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.hibernate.jpa.boot.spi.ProviderChecker;
import org.hibernate.jpa.internal.util.PersistenceUtilHelper;
import org.jboss.logging.Logger;

public class HibernatePersistenceProvider
implements PersistenceProvider {
    private static final Logger log = Logger.getLogger(HibernatePersistenceProvider.class);
    private final PersistenceUtilHelper.MetadataCache cache = new PersistenceUtilHelper.MetadataCache();
    private final ProviderUtil providerUtil = new ProviderUtil(){

        public LoadState isLoadedWithoutReference(Object proxy, String property) {
            return PersistenceUtilHelper.isLoadedWithoutReference(proxy, property, HibernatePersistenceProvider.this.cache);
        }

        public LoadState isLoadedWithReference(Object proxy, String property) {
            return PersistenceUtilHelper.isLoadedWithReference(proxy, property, HibernatePersistenceProvider.this.cache);
        }

        public LoadState isLoaded(Object o) {
            return PersistenceUtilHelper.isLoaded(o);
        }
    };

    public EntityManagerFactory createEntityManagerFactory(String persistenceUnitName, Map properties) {
        log.tracef("Starting createEntityManagerFactory for persistenceUnitName %s", (Object)persistenceUnitName);
        EntityManagerFactoryBuilder builder = this.getEntityManagerFactoryBuilderOrNull(persistenceUnitName, properties);
        if (builder == null) {
            log.trace((Object)"Could not obtain matching EntityManagerFactoryBuilder, returning null");
            return null;
        }
        return builder.build();
    }

    protected EntityManagerFactoryBuilder getEntityManagerFactoryBuilderOrNull(String persistenceUnitName, Map properties) {
        return this.getEntityManagerFactoryBuilderOrNull(persistenceUnitName, properties, null, null);
    }

    protected EntityManagerFactoryBuilder getEntityManagerFactoryBuilderOrNull(String persistenceUnitName, Map properties, ClassLoader providedClassLoader) {
        return this.getEntityManagerFactoryBuilderOrNull(persistenceUnitName, properties, providedClassLoader, null);
    }

    protected EntityManagerFactoryBuilder getEntityManagerFactoryBuilderOrNull(String persistenceUnitName, Map properties, ClassLoaderService providedClassLoaderService) {
        return this.getEntityManagerFactoryBuilderOrNull(persistenceUnitName, properties, null, providedClassLoaderService);
    }

    private EntityManagerFactoryBuilder getEntityManagerFactoryBuilderOrNull(String persistenceUnitName, Map properties, ClassLoader providedClassLoader, ClassLoaderService providedClassLoaderService) {
        List<ParsedPersistenceXmlDescriptor> units;
        log.tracef("Attempting to obtain correct EntityManagerFactoryBuilder for persistenceUnitName : %s", (Object)persistenceUnitName);
        Map integration = HibernatePersistenceProvider.wrap(properties);
        try {
            units = PersistenceXmlParser.locatePersistenceUnits(integration);
        }
        catch (Exception e) {
            log.debug((Object)"Unable to locate persistence units", (Throwable)e);
            throw new PersistenceException("Unable to locate persistence units", (Throwable)e);
        }
        log.debugf("Located and parsed %s persistence units; checking each", units.size());
        if (persistenceUnitName == null && units.size() > 1) {
            throw new PersistenceException("No name provided and multiple persistence units found");
        }
        for (ParsedPersistenceXmlDescriptor persistenceUnit : units) {
            boolean matches;
            log.debugf("Checking persistence-unit [name=%s, explicit-provider=%s] against incoming persistence unit name [%s]", (Object)persistenceUnit.getName(), (Object)persistenceUnit.getProviderClassName(), (Object)persistenceUnitName);
            boolean bl = matches = persistenceUnitName == null || persistenceUnit.getName().equals(persistenceUnitName);
            if (!matches) {
                log.debug((Object)"Excluding from consideration due to name mis-match");
                continue;
            }
            if (!ProviderChecker.isProvider(persistenceUnit, properties)) {
                log.debug((Object)"Excluding from consideration due to provider mis-match");
                continue;
            }
            if (providedClassLoaderService != null) {
                return this.getEntityManagerFactoryBuilder((PersistenceUnitDescriptor)persistenceUnit, integration, providedClassLoaderService);
            }
            return this.getEntityManagerFactoryBuilder((PersistenceUnitDescriptor)persistenceUnit, integration, providedClassLoader);
        }
        log.debug((Object)"Found no matching persistence units");
        return null;
    }

    protected static Map wrap(Map properties) {
        return properties == null ? Collections.emptyMap() : Collections.unmodifiableMap(properties);
    }

    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map properties) {
        log.tracef("Starting createContainerEntityManagerFactory : %s", (Object)info.getPersistenceUnitName());
        return this.getEntityManagerFactoryBuilder(info, properties).build();
    }

    public void generateSchema(PersistenceUnitInfo info, Map map) {
        log.tracef("Starting generateSchema : PUI.name=%s", (Object)info.getPersistenceUnitName());
        EntityManagerFactoryBuilder builder = this.getEntityManagerFactoryBuilder(info, map);
        builder.generateSchema();
    }

    public boolean generateSchema(String persistenceUnitName, Map map) {
        log.tracef("Starting generateSchema for persistenceUnitName %s", (Object)persistenceUnitName);
        EntityManagerFactoryBuilder builder = this.getEntityManagerFactoryBuilderOrNull(persistenceUnitName, map);
        if (builder == null) {
            log.trace((Object)"Could not obtain matching EntityManagerFactoryBuilder, returning false");
            return false;
        }
        builder.generateSchema();
        return true;
    }

    protected EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(PersistenceUnitInfo info, Map integration) {
        return Bootstrap.getEntityManagerFactoryBuilder(info, integration);
    }

    protected EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(PersistenceUnitDescriptor persistenceUnitDescriptor, Map integration, ClassLoader providedClassLoader) {
        return Bootstrap.getEntityManagerFactoryBuilder(persistenceUnitDescriptor, integration, providedClassLoader);
    }

    protected EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(PersistenceUnitDescriptor persistenceUnitDescriptor, Map integration, ClassLoaderService providedClassLoaderService) {
        return Bootstrap.getEntityManagerFactoryBuilder(persistenceUnitDescriptor, integration, providedClassLoaderService);
    }

    public ProviderUtil getProviderUtil() {
        return this.providerUtil;
    }
}

