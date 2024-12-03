/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.migration.agent;

import com.atlassian.migration.agent.CustomDatabaseServiceBeans;
import com.atlassian.migration.agent.HibernateServiceBeans;
import com.atlassian.migration.agent.store.jpa.impl.BridgeConnectionProvider;
import com.atlassian.migration.agent.store.jpa.impl.ConfluenceWrapperDataSource;
import com.atlassian.migration.agent.store.jpa.impl.ConnectionSupplier;
import com.atlassian.migration.agent.store.jpa.impl.DefaultConnectionSupplier;
import com.atlassian.migration.agent.store.jpa.interfaces.ConnectionHelper;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={HibernateServiceBeans.class, CustomDatabaseServiceBeans.class})
public class DatabaseConnectionServiceBeans {
    @Bean
    public ConnectionProvider connectionProvider(ConnectionHelper connectionHelper) {
        return new BridgeConnectionProvider(connectionHelper);
    }

    @Bean
    public ConnectionSupplier connectionSupplier(ConnectionHelper connectionHelper) {
        return new DefaultConnectionSupplier(connectionHelper);
    }

    @Bean
    public ConfluenceWrapperDataSource confluenceWrapperDataSource(ConnectionProvider connectionProvider) {
        return new ConfluenceWrapperDataSource(connectionProvider);
    }
}

