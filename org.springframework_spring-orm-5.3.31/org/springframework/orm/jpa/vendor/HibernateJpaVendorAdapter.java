/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.spi.PersistenceProvider
 *  javax.persistence.spi.PersistenceUnitInfo
 *  javax.persistence.spi.PersistenceUnitTransactionType
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.dialect.DB2Dialect
 *  org.hibernate.dialect.DerbyTenSevenDialect
 *  org.hibernate.dialect.H2Dialect
 *  org.hibernate.dialect.HANAColumnStoreDialect
 *  org.hibernate.dialect.HSQLDialect
 *  org.hibernate.dialect.Informix10Dialect
 *  org.hibernate.dialect.MySQL57Dialect
 *  org.hibernate.dialect.Oracle12cDialect
 *  org.hibernate.dialect.PostgreSQL95Dialect
 *  org.hibernate.dialect.SQLServer2012Dialect
 *  org.hibernate.dialect.SybaseDialect
 *  org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode
 *  org.springframework.lang.Nullable
 */
package org.springframework.orm.jpa.vendor;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.DerbyTenSevenDialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.HANAColumnStoreDialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.Informix10Dialect;
import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.dialect.Oracle12cDialect;
import org.hibernate.dialect.PostgreSQL95Dialect;
import org.hibernate.dialect.SQLServer2012Dialect;
import org.hibernate.dialect.SybaseDialect;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.SpringHibernateJpaPersistenceProvider;

public class HibernateJpaVendorAdapter
extends AbstractJpaVendorAdapter {
    private final HibernateJpaDialect jpaDialect = new HibernateJpaDialect();
    private final PersistenceProvider persistenceProvider = new SpringHibernateJpaPersistenceProvider();
    private final Class<? extends EntityManagerFactory> entityManagerFactoryInterface = SessionFactory.class;
    private final Class<? extends EntityManager> entityManagerInterface = Session.class;

    public void setPrepareConnection(boolean prepareConnection) {
        this.jpaDialect.setPrepareConnection(prepareConnection);
    }

    @Override
    public PersistenceProvider getPersistenceProvider() {
        return this.persistenceProvider;
    }

    @Override
    public String getPersistenceProviderRootPackage() {
        return "org.hibernate";
    }

    public Map<String, Object> getJpaPropertyMap(PersistenceUnitInfo pui) {
        return this.buildJpaPropertyMap(this.jpaDialect.prepareConnection && pui.getTransactionType() != PersistenceUnitTransactionType.JTA);
    }

    public Map<String, Object> getJpaPropertyMap() {
        return this.buildJpaPropertyMap(this.jpaDialect.prepareConnection);
    }

    private Map<String, Object> buildJpaPropertyMap(boolean connectionReleaseOnClose) {
        HashMap<String, Object> jpaProperties = new HashMap<String, Object>();
        if (this.getDatabasePlatform() != null) {
            jpaProperties.put("hibernate.dialect", this.getDatabasePlatform());
        } else {
            Class<?> databaseDialectClass = this.determineDatabaseDialectClass(this.getDatabase());
            if (databaseDialectClass != null) {
                jpaProperties.put("hibernate.dialect", databaseDialectClass.getName());
            }
        }
        if (this.isGenerateDdl()) {
            jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        }
        if (this.isShowSql()) {
            jpaProperties.put("hibernate.show_sql", "true");
        }
        if (connectionReleaseOnClose) {
            jpaProperties.put("hibernate.connection.handling_mode", PhysicalConnectionHandlingMode.DELAYED_ACQUISITION_AND_HOLD);
        }
        return jpaProperties;
    }

    @Nullable
    protected Class<?> determineDatabaseDialectClass(Database database) {
        switch (database) {
            case DB2: {
                return DB2Dialect.class;
            }
            case DERBY: {
                return DerbyTenSevenDialect.class;
            }
            case H2: {
                return H2Dialect.class;
            }
            case HANA: {
                return HANAColumnStoreDialect.class;
            }
            case HSQL: {
                return HSQLDialect.class;
            }
            case INFORMIX: {
                return Informix10Dialect.class;
            }
            case MYSQL: {
                return MySQL57Dialect.class;
            }
            case ORACLE: {
                return Oracle12cDialect.class;
            }
            case POSTGRESQL: {
                return PostgreSQL95Dialect.class;
            }
            case SQL_SERVER: {
                return SQLServer2012Dialect.class;
            }
            case SYBASE: {
                return SybaseDialect.class;
            }
        }
        return null;
    }

    @Override
    public HibernateJpaDialect getJpaDialect() {
        return this.jpaDialect;
    }

    @Override
    public Class<? extends EntityManagerFactory> getEntityManagerFactoryInterface() {
        return this.entityManagerFactoryInterface;
    }

    @Override
    public Class<? extends EntityManager> getEntityManagerInterface() {
        return this.entityManagerInterface;
    }
}

