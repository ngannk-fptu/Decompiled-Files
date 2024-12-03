/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.osgi.javaconfig.OsgiServices
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.migration.agent;

import com.atlassian.migration.agent.annotation.ConditionalOnClass;
import com.atlassian.migration.agent.store.jpa.impl.CustomDatabaseConnectionHelper;
import com.atlassian.migration.agent.store.jpa.interfaces.ConnectionHelper;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(value={"com.atlassian.confluence.persistence.DatabaseConnectionProvider"})
public class CustomDatabaseServiceBeans {
    @Bean
    public Object customDatabaseConnectionProvider() throws ClassNotFoundException {
        return OsgiServices.importOsgiService(Class.forName("com.atlassian.confluence.persistence.DatabaseConnectionProvider"));
    }

    @Bean
    public ConnectionHelper connectionHelper() throws ClassNotFoundException {
        return new CustomDatabaseConnectionHelper(this.customDatabaseConnectionProvider());
    }
}

