/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.spi.PersistenceProvider
 *  javax.persistence.spi.PersistenceUnitInfo
 *  org.springframework.lang.Nullable
 */
package org.springframework.orm.jpa;

import java.util.Collections;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.JpaDialect;

public interface JpaVendorAdapter {
    public PersistenceProvider getPersistenceProvider();

    @Nullable
    default public String getPersistenceProviderRootPackage() {
        return null;
    }

    default public Map<String, ?> getJpaPropertyMap(PersistenceUnitInfo pui) {
        return this.getJpaPropertyMap();
    }

    default public Map<String, ?> getJpaPropertyMap() {
        return Collections.emptyMap();
    }

    @Nullable
    default public JpaDialect getJpaDialect() {
        return null;
    }

    default public Class<? extends EntityManagerFactory> getEntityManagerFactoryInterface() {
        return EntityManagerFactory.class;
    }

    default public Class<? extends EntityManager> getEntityManagerInterface() {
        return EntityManager.class;
    }

    default public void postProcessEntityManagerFactory(EntityManagerFactory emf) {
    }

    default public void postProcessEntityManager(EntityManager em) {
    }
}

