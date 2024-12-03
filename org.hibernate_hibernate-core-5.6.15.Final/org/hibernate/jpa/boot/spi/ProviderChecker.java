/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.jpa.boot.spi;

import java.util.Map;
import org.hibernate.internal.HEMLogging;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.jboss.logging.Logger;

public final class ProviderChecker {
    private static final Logger log = Logger.getLogger(ProviderChecker.class);

    public static boolean isProvider(PersistenceUnitDescriptor persistenceUnit, Map integration) {
        return ProviderChecker.hibernateProviderNamesContain(ProviderChecker.extractRequestedProviderName(persistenceUnit, integration));
    }

    public static boolean hibernateProviderNamesContain(String requestedProviderName) {
        log.tracef("Checking requested PersistenceProvider name [%s] against Hibernate provider names", (Object)requestedProviderName);
        String deprecatedPersistenceProvider = "org.hibernate.ejb.HibernatePersistence";
        if ("org.hibernate.ejb.HibernatePersistence".equals(requestedProviderName)) {
            HEMLogging.messageLogger(ProviderChecker.class).deprecatedPersistenceProvider("org.hibernate.ejb.HibernatePersistence", HibernatePersistenceProvider.class.getName());
            return true;
        }
        return HibernatePersistenceProvider.class.getName().equals(requestedProviderName);
    }

    public static String extractRequestedProviderName(PersistenceUnitDescriptor persistenceUnit, Map integration) {
        String integrationProviderName = ProviderChecker.extractProviderName(integration);
        if (integrationProviderName != null) {
            log.debugf("Integration provided explicit PersistenceProvider [%s]", (Object)integrationProviderName);
            return integrationProviderName;
        }
        String persistenceUnitRequestedProvider = ProviderChecker.extractProviderName(persistenceUnit);
        if (persistenceUnitRequestedProvider != null) {
            log.debugf("Persistence-unit [%s] requested PersistenceProvider [%s]", (Object)persistenceUnit.getName(), (Object)persistenceUnitRequestedProvider);
            return persistenceUnitRequestedProvider;
        }
        log.debug((Object)"No PersistenceProvider explicitly requested, assuming Hibernate");
        return HibernatePersistenceProvider.class.getName();
    }

    private static String extractProviderName(Map integration) {
        if (integration == null) {
            return null;
        }
        String setting = (String)integration.get("javax.persistence.provider");
        if (setting == null) {
            setting = (String)integration.get("jakarta.persistence.provider");
        }
        return setting == null ? null : setting.trim();
    }

    private static String extractProviderName(PersistenceUnitDescriptor persistenceUnit) {
        String persistenceUnitRequestedProvider = persistenceUnit.getProviderClassName();
        return persistenceUnitRequestedProvider == null ? null : persistenceUnitRequestedProvider.trim();
    }

    private ProviderChecker() {
    }
}

