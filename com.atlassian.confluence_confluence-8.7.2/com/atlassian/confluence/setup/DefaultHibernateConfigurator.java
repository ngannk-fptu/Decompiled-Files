/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.db.DatabaseDetails
 *  com.atlassian.config.db.HibernateConfigurator
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.confluence.impl.hibernate.ConfluenceHibernateTransactionManager
 *  com.atlassian.spring.container.ContainerManager
 *  org.hibernate.SessionFactory
 *  org.hibernate.cfg.Configuration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.support.ClassPathXmlApplicationContext
 *  org.springframework.transaction.PlatformTransactionManager
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.ConfigurationException;
import com.atlassian.config.db.DatabaseDetails;
import com.atlassian.config.db.HibernateConfigurator;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.atlassian.confluence.impl.core.persistence.hibernate.HibernateMetadataSource;
import com.atlassian.confluence.impl.core.persistence.hibernate.schema.ConfluenceSchemaCreator;
import com.atlassian.confluence.impl.hibernate.ConfluenceHibernateTransactionManager;
import com.atlassian.confluence.impl.setup.BootstrapConfigurer;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.DenormalisedPermissionsDdlExecutor;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;
import com.atlassian.spring.container.ContainerManager;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

public class DefaultHibernateConfigurator
implements HibernateConfigurator {
    public static final String HIBERNATE_HIKARICP_IDLE_TIMEOUT = "hibernate.hikari.idleTimeout";
    public static final String HIBERNATE_HIKARICP_MAX_POOL_SIZE = "hibernate.hikari.maximumPoolSize";
    public static final String HIBERNATE_HIKARICP_MIN_POOL_SIZE = "hibernate.hikari.minimumIdle";
    public static final String HIBERNATE_HIKARICP_REGISTER_MBEANS = "hibernate.hikari.registerMbeans";
    public static final String SPRING_HIKARICP_REGISTER_MBEANS = "spring.datasource.hikari.registerMbeans";
    private static Logger log = LoggerFactory.getLogger(DefaultHibernateConfigurator.class);
    private static final AtomicBoolean DATABASE_CONFIGURED = new AtomicBoolean();
    private static final AtomicBoolean DATASOURCE_CONFIGURED = new AtomicBoolean();

    public void configureDatabase(DatabaseDetails dbDetails, boolean embedded) throws ConfigurationException {
        if (DATABASE_CONFIGURED.getAndSet(true)) {
            log.info("Database has already been setup.");
            return;
        }
        boolean failed = false;
        try {
            Properties properties = this.populateDatabaseProperties(dbDetails);
            this.createSchema(properties);
        }
        catch (ConfigurationException | RuntimeException ce) {
            failed = true;
            throw ce;
        }
        finally {
            if (failed) {
                log.warn("An error occurred while creating database.");
                DATABASE_CONFIGURED.set(false);
            }
        }
    }

    protected Properties populateDatabaseProperties(DatabaseDetails dbDetails) {
        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.driver_class", dbDetails.getDriverClassName());
        if (dbDetails.getDatabaseUrl() != null) {
            properties.setProperty("hibernate.connection.url", dbDetails.getDatabaseUrl());
        }
        properties.setProperty("hibernate.connection.username", dbDetails.getUserName());
        properties.setProperty("hibernate.connection.password", dbDetails.getPassword());
        properties.setProperty("hibernate.dialect", dbDetails.getDialect());
        properties.setProperty(HIBERNATE_HIKARICP_IDLE_TIMEOUT, "30000");
        properties.setProperty(HIBERNATE_HIKARICP_MAX_POOL_SIZE, String.valueOf(dbDetails.getPoolSize()));
        properties.setProperty(HIBERNATE_HIKARICP_MIN_POOL_SIZE, "20");
        properties.setProperty("hibernate.connection.autocommit", "false");
        properties.setProperty(HIBERNATE_HIKARICP_REGISTER_MBEANS, "true");
        properties.setProperty(SPRING_HIKARICP_REGISTER_MBEANS, "true");
        properties.setProperty("hibernate.connection.isolation", "2");
        if (dbDetails.getExtraHibernateProperties() != null) {
            Properties extraHibernateConnectionSettings = dbDetails.getExtraHibernateProperties();
            for (Map.Entry<Object, Object> entry : extraHibernateConnectionSettings.entrySet()) {
                properties.setProperty((String)entry.getKey(), (String)entry.getValue());
            }
        }
        return properties;
    }

    public void configureDatasource(String datasourceName, String dialect) throws ConfigurationException {
        if (DATASOURCE_CONFIGURED.getAndSet(true)) {
            log.info("Datasource has already been configured.");
            return;
        }
        boolean failed = false;
        try {
            Properties properties = this.populateDatasourceProperties(datasourceName, dialect);
            this.createSchema(properties);
        }
        catch (ConfigurationException | RuntimeException ce) {
            failed = true;
            throw ce;
        }
        finally {
            if (failed) {
                log.warn("An error occurred when creating datasource.");
                DATASOURCE_CONFIGURED.set(false);
            }
        }
    }

    private Properties populateDatasourceProperties(String datasourceName, String dialect) {
        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.datasource", datasourceName);
        properties.setProperty("hibernate.dialect", dialect);
        return properties;
    }

    public void unconfigureDatabase() {
        Properties properties = BootstrapUtils.getBootstrapManager().getHibernateProperties();
        for (Object key : properties.keySet()) {
            BootstrapUtils.getBootstrapManager().removeProperty((String)key);
        }
        try {
            BootstrapConfigurer.getBootstrapConfigurer().save();
        }
        catch (ConfigurationException e) {
            log.error("Unable to unconfigure failed database config: " + e.getMessage(), (Throwable)e);
        }
        DATABASE_CONFIGURED.set(false);
    }

    private void createSchema(Properties properties) throws ConfigurationException {
        log.debug("Configuring database with properties: {}", (Object)properties);
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            BootstrapConfigurer.getBootstrapConfigurer().setProperty((String)entry.getKey(), entry.getValue());
        }
        BootstrapConfigurer.getBootstrapConfigurer().save();
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"setupHibernateContext.xml", "schemaCreatorContext.xml"}, BootstrapUtils.getBootstrapContext());){
            ((ConfluenceSchemaCreator)context.getBean(ConfluenceSchemaCreator.class)).createSchema(false);
        }
        BootstrapConfigurer.getBootstrapConfigurer().setProperty("hibernate.setup", "true");
    }

    @Deprecated
    public static ConfluenceSchemaCreator createConfluenceSchemaCreator(Configuration hibernateConfig) {
        SessionFactory sessionFactory = DefaultHibernateConfigurator.getSessionFactory(hibernateConfig);
        PlatformTransactionManager txManager = DefaultHibernateConfigurator.getPlatformTransactionManager(hibernateConfig);
        HibernateMetadataSource metadataSource = (HibernateMetadataSource)ContainerManager.getComponent((String)"hibernateMetadataSource", HibernateMetadataSource.class);
        DdlExecutor ddlExecutor = (DdlExecutor)ContainerManager.getComponent((String)"ddlExecutor", DdlExecutor.class);
        DenormalisedPermissionsDdlExecutor denormalisedPermissionsDdlExecutor = (DenormalisedPermissionsDdlExecutor)ContainerManager.getComponent((String)"denormalisedPermissionsDdlExecutor", DenormalisedPermissionsDdlExecutor.class);
        return new ConfluenceSchemaCreator(sessionFactory, txManager, metadataSource, ddlExecutor, (HibernateDatabaseCapabilities)BootstrapUtils.getBootstrapContext().getBean(HibernateDatabaseCapabilities.class), denormalisedPermissionsDdlExecutor);
    }

    private static SessionFactory getSessionFactory(Configuration hibernateConfig) {
        SessionFactory sessionFactory = (SessionFactory)ContainerManager.getComponent((String)"sessionFactory");
        if (sessionFactory != null) {
            return sessionFactory;
        }
        sessionFactory = hibernateConfig.buildSessionFactory();
        return sessionFactory;
    }

    private static PlatformTransactionManager getPlatformTransactionManager(Configuration hibernateConfig) {
        PlatformTransactionManager transactionManager = (PlatformTransactionManager)ContainerManager.getComponent((String)"transactionManager");
        if (transactionManager != null) {
            return transactionManager;
        }
        ConfluenceHibernateTransactionManager confluenceHibernateTransactionManager = new ConfluenceHibernateTransactionManager();
        confluenceHibernateTransactionManager.setSessionFactory(DefaultHibernateConfigurator.getSessionFactory(hibernateConfig));
        return confluenceHibernateTransactionManager;
    }
}

