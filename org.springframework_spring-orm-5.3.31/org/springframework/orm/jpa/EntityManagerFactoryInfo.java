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

import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.JpaDialect;

public interface EntityManagerFactoryInfo {
    @Nullable
    public PersistenceProvider getPersistenceProvider();

    @Nullable
    public PersistenceUnitInfo getPersistenceUnitInfo();

    @Nullable
    public String getPersistenceUnitName();

    @Nullable
    public DataSource getDataSource();

    @Nullable
    public Class<? extends EntityManager> getEntityManagerInterface();

    @Nullable
    public JpaDialect getJpaDialect();

    public ClassLoader getBeanClassLoader();

    public EntityManagerFactory getNativeEntityManagerFactory();

    public EntityManager createNativeEntityManager(@Nullable Map<?, ?> var1);
}

