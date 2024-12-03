/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.spi.PersistenceUnitInfo
 *  org.springframework.lang.Nullable
 */
package org.springframework.orm.jpa.vendor;

import java.util.Collections;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;

public abstract class AbstractJpaVendorAdapter
implements JpaVendorAdapter {
    private Database database = Database.DEFAULT;
    @Nullable
    private String databasePlatform;
    private boolean generateDdl = false;
    private boolean showSql = false;

    public void setDatabase(Database database) {
        this.database = database;
    }

    protected Database getDatabase() {
        return this.database;
    }

    public void setDatabasePlatform(@Nullable String databasePlatform) {
        this.databasePlatform = databasePlatform;
    }

    @Nullable
    protected String getDatabasePlatform() {
        return this.databasePlatform;
    }

    public void setGenerateDdl(boolean generateDdl) {
        this.generateDdl = generateDdl;
    }

    protected boolean isGenerateDdl() {
        return this.generateDdl;
    }

    public void setShowSql(boolean showSql) {
        this.showSql = showSql;
    }

    protected boolean isShowSql() {
        return this.showSql;
    }

    @Override
    @Nullable
    public String getPersistenceProviderRootPackage() {
        return null;
    }

    @Override
    public Map<String, ?> getJpaPropertyMap(PersistenceUnitInfo pui) {
        return this.getJpaPropertyMap();
    }

    @Override
    public Map<String, ?> getJpaPropertyMap() {
        return Collections.emptyMap();
    }

    @Override
    @Nullable
    public JpaDialect getJpaDialect() {
        return null;
    }

    @Override
    public Class<? extends EntityManagerFactory> getEntityManagerFactoryInterface() {
        return EntityManagerFactory.class;
    }

    @Override
    public Class<? extends EntityManager> getEntityManagerInterface() {
        return EntityManager.class;
    }

    @Override
    public void postProcessEntityManagerFactory(EntityManagerFactory emf) {
    }

    @Override
    public void postProcessEntityManager(EntityManager em) {
    }
}

