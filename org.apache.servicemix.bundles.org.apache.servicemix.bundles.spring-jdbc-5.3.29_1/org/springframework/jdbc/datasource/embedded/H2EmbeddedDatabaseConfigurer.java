/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 */
package org.springframework.jdbc.datasource.embedded;

import java.sql.Driver;
import org.springframework.jdbc.datasource.embedded.AbstractEmbeddedDatabaseConfigurer;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

final class H2EmbeddedDatabaseConfigurer
extends AbstractEmbeddedDatabaseConfigurer {
    @Nullable
    private static H2EmbeddedDatabaseConfigurer instance;
    private final Class<? extends Driver> driverClass;

    public static synchronized H2EmbeddedDatabaseConfigurer getInstance() throws ClassNotFoundException {
        if (instance == null) {
            instance = new H2EmbeddedDatabaseConfigurer(ClassUtils.forName((String)"org.h2.Driver", (ClassLoader)H2EmbeddedDatabaseConfigurer.class.getClassLoader()));
        }
        return instance;
    }

    private H2EmbeddedDatabaseConfigurer(Class<? extends Driver> driverClass) {
        this.driverClass = driverClass;
    }

    @Override
    public void configureConnectionProperties(ConnectionProperties properties, String databaseName) {
        properties.setDriverClass(this.driverClass);
        properties.setUrl(String.format("jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false", databaseName));
        properties.setUsername("sa");
        properties.setPassword("");
    }
}

