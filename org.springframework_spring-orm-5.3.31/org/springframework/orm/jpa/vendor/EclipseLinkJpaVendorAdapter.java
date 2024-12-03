/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.spi.PersistenceProvider
 *  org.eclipse.persistence.jpa.JpaEntityManager
 *  org.eclipse.persistence.jpa.PersistenceProvider
 *  org.springframework.lang.Nullable
 */
package org.springframework.orm.jpa.vendor;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;

public class EclipseLinkJpaVendorAdapter
extends AbstractJpaVendorAdapter {
    private final javax.persistence.spi.PersistenceProvider persistenceProvider = new PersistenceProvider();
    private final EclipseLinkJpaDialect jpaDialect = new EclipseLinkJpaDialect();

    @Override
    public javax.persistence.spi.PersistenceProvider getPersistenceProvider() {
        return this.persistenceProvider;
    }

    public Map<String, Object> getJpaPropertyMap() {
        HashMap<String, Object> jpaProperties = new HashMap<String, Object>();
        if (this.getDatabasePlatform() != null) {
            jpaProperties.put("eclipselink.target-database", this.getDatabasePlatform());
        } else {
            String targetDatabase = this.determineTargetDatabaseName(this.getDatabase());
            if (targetDatabase != null) {
                jpaProperties.put("eclipselink.target-database", targetDatabase);
            }
        }
        if (this.isGenerateDdl()) {
            jpaProperties.put("eclipselink.ddl-generation", "create-tables");
            jpaProperties.put("eclipselink.ddl-generation.output-mode", "database");
        }
        if (this.isShowSql()) {
            jpaProperties.put("eclipselink.logging.level.sql", Level.FINE.toString());
            jpaProperties.put("eclipselink.logging.parameters", Boolean.TRUE.toString());
        }
        return jpaProperties;
    }

    @Nullable
    protected String determineTargetDatabaseName(Database database) {
        switch (database) {
            case DB2: {
                return "DB2";
            }
            case DERBY: {
                return "Derby";
            }
            case HANA: {
                return "HANA";
            }
            case HSQL: {
                return "HSQL";
            }
            case INFORMIX: {
                return "Informix";
            }
            case MYSQL: {
                return "MySQL";
            }
            case ORACLE: {
                return "Oracle";
            }
            case POSTGRESQL: {
                return "PostgreSQL";
            }
            case SQL_SERVER: {
                return "SQLServer";
            }
            case SYBASE: {
                return "Sybase";
            }
        }
        return null;
    }

    @Override
    public EclipseLinkJpaDialect getJpaDialect() {
        return this.jpaDialect;
    }

    @Override
    public Class<? extends EntityManager> getEntityManagerInterface() {
        return JpaEntityManager.class;
    }
}

