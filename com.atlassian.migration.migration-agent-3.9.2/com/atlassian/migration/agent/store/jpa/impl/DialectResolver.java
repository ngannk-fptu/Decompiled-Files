/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.confluence.setup.BootstrapManager
 *  lombok.Generated
 *  org.hibernate.dialect.Dialect
 */
package com.atlassian.migration.agent.store.jpa.impl;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.agent.newexport.DbType;
import com.atlassian.migration.agent.store.jpa.impl.OracleDialect;
import com.atlassian.migration.agent.store.jpa.impl.SQLServerDialect;
import java.util.Optional;
import lombok.Generated;
import org.hibernate.dialect.Dialect;

public class DialectResolver {
    private final DbType dbType;

    public DialectResolver(BootstrapManager bootstrapManager) {
        this.dbType = this.detectDbType(bootstrapManager.getApplicationConfig());
    }

    private DbType detectDbType(ApplicationConfiguration applicationConfig) {
        String dialect = String.valueOf(applicationConfig.getProperty((Object)"hibernate.dialect")).toLowerCase();
        if (dialect.contains("mysql")) {
            return DbType.MYSQL;
        }
        if (dialect.contains("sqlserver")) {
            return DbType.MSSQL;
        }
        if (dialect.contains("oracle")) {
            return DbType.ORACLE;
        }
        if (dialect.contains("h2")) {
            return DbType.H2;
        }
        return DbType.POSTGRES;
    }

    public Optional<Dialect> getCustomDialect() {
        switch (this.dbType) {
            case ORACLE: {
                return Optional.of(new OracleDialect());
            }
            case MSSQL: {
                return Optional.of(new SQLServerDialect());
            }
        }
        return Optional.empty();
    }

    @Generated
    public DbType getDbType() {
        return this.dbType;
    }
}

