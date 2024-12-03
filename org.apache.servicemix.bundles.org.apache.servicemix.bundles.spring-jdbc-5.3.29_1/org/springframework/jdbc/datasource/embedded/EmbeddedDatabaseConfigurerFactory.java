/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.datasource.embedded;

import org.springframework.jdbc.datasource.embedded.DerbyEmbeddedDatabaseConfigurer;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseConfigurer;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.embedded.H2EmbeddedDatabaseConfigurer;
import org.springframework.jdbc.datasource.embedded.HsqlEmbeddedDatabaseConfigurer;
import org.springframework.util.Assert;

final class EmbeddedDatabaseConfigurerFactory {
    private EmbeddedDatabaseConfigurerFactory() {
    }

    public static EmbeddedDatabaseConfigurer getConfigurer(EmbeddedDatabaseType type) throws IllegalStateException {
        Assert.notNull((Object)((Object)type), (String)"EmbeddedDatabaseType is required");
        try {
            switch (type) {
                case HSQL: {
                    return HsqlEmbeddedDatabaseConfigurer.getInstance();
                }
                case H2: {
                    return H2EmbeddedDatabaseConfigurer.getInstance();
                }
                case DERBY: {
                    return DerbyEmbeddedDatabaseConfigurer.getInstance();
                }
            }
            throw new UnsupportedOperationException("Embedded database type [" + (Object)((Object)type) + "] is not supported");
        }
        catch (ClassNotFoundException | NoClassDefFoundError ex) {
            throw new IllegalStateException("Driver for test database type [" + (Object)((Object)type) + "] is not available", ex);
        }
    }
}

