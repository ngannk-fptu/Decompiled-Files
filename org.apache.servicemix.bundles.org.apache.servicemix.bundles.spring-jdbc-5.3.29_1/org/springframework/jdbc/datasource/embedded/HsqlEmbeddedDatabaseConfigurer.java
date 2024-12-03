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

final class HsqlEmbeddedDatabaseConfigurer
extends AbstractEmbeddedDatabaseConfigurer {
    @Nullable
    private static HsqlEmbeddedDatabaseConfigurer instance;
    private final Class<? extends Driver> driverClass;

    public static synchronized HsqlEmbeddedDatabaseConfigurer getInstance() throws ClassNotFoundException {
        if (instance == null) {
            instance = new HsqlEmbeddedDatabaseConfigurer(ClassUtils.forName((String)"org.hsqldb.jdbcDriver", (ClassLoader)HsqlEmbeddedDatabaseConfigurer.class.getClassLoader()));
        }
        return instance;
    }

    private HsqlEmbeddedDatabaseConfigurer(Class<? extends Driver> driverClass) {
        this.driverClass = driverClass;
    }

    @Override
    public void configureConnectionProperties(ConnectionProperties properties, String databaseName) {
        properties.setDriverClass(this.driverClass);
        properties.setUrl("jdbc:hsqldb:mem:" + databaseName);
        properties.setUsername("sa");
        properties.setPassword("");
    }
}

