/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.builder;

import net.java.ao.DatabaseProvider;
import net.java.ao.builder.DatabaseProperties;
import net.java.ao.builder.SupportedDatabase;

class DatabaseProviderFactory {
    DatabaseProviderFactory() {
    }

    static DatabaseProvider getDatabaseProvider(DatabaseProperties databaseProperties) {
        SupportedDatabase supportedDb = SupportedDatabase.fromUri(databaseProperties.getUrl());
        return supportedDb.getDatabaseProvider(databaseProperties.getConnectionPool(), databaseProperties.getUrl(), databaseProperties.getUsername(), databaseProperties.getPassword(), databaseProperties.getSchema());
    }
}

