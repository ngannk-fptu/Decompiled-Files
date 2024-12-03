/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl
 *  org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
 *  org.hibernate.hikaricp.internal.HikariCPConnectionProvider
 *  org.hibernate.service.spi.Stoppable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.config.AbstractFactoryBean
 *  org.springframework.jndi.JndiTemplate
 */
package com.atlassian.confluence.impl.core.persistence.hibernate;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.impl.core.persistence.hibernate.MonitoringConnectionProviderFactory;
import java.util.function.UnaryOperator;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import org.hibernate.service.spi.Stoppable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.jndi.JndiTemplate;

final class ConnectionProviderFactoryBean
extends AbstractFactoryBean<ConnectionProvider> {
    private static final Logger log = LoggerFactory.getLogger(ConnectionProviderFactoryBean.class);
    private final HibernateConfig hibernateConfig;
    private final UnaryOperator<ConnectionProvider> proxyFactory;
    private final JndiTemplate jndiTemplate;

    public ConnectionProviderFactoryBean(HibernateConfig hibernateConfig, MonitoringConnectionProviderFactory proxyFactory) {
        this(hibernateConfig, proxyFactory::proxy, new JndiTemplate());
    }

    ConnectionProviderFactoryBean(HibernateConfig hibernateConfig, UnaryOperator<ConnectionProvider> proxyFactory, JndiTemplate jndiTemplate) {
        this.hibernateConfig = hibernateConfig;
        this.proxyFactory = proxyFactory;
        this.jndiTemplate = jndiTemplate;
    }

    protected ConnectionProvider createInstance() throws Exception {
        return (ConnectionProvider)this.proxyFactory.apply(this.createConnectionProvider());
    }

    ConnectionProvider createConnectionProvider() throws NamingException {
        String dataSourceName = this.hibernateConfig.getHibernateProperties().getProperty("hibernate.connection.datasource");
        if (dataSourceName != null) {
            log.info("Creating ConnectionProvider for DataSource {}", (Object)dataSourceName);
            return this.createDataSourceProvider(dataSourceName);
        }
        log.info("Creating HikariCP HikariCPConnectionProvider for URL {}", this.hibernateConfig.getHibernateProperties().get("hibernate.connection.url"));
        return new HikariCPConnectionProvider();
    }

    private ConnectionProvider createDataSourceProvider(String dataSourceName) throws NamingException {
        DataSource dataSource = (DataSource)this.jndiTemplate.lookup(dataSourceName, DataSource.class);
        DatasourceConnectionProviderImpl connectionProvider = new DatasourceConnectionProviderImpl();
        connectionProvider.setDataSource(dataSource);
        return connectionProvider;
    }

    protected void destroyInstance(ConnectionProvider connectionProvider) {
        if (connectionProvider instanceof Stoppable) {
            ((Stoppable)connectionProvider).stop();
        }
    }

    public Class<?> getObjectType() {
        return ConnectionProvider.class;
    }
}

