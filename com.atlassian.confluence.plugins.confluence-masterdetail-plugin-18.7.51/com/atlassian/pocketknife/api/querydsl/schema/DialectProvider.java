/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.pocketknife.api.querydsl.schema;

import com.atlassian.annotations.PublicApi;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLTemplates;
import java.sql.Connection;

@PublicApi
public interface DialectProvider {
    public Config getDialectConfig(Connection var1);

    public static class Config {
        private final SQLTemplates sqlTemplates;
        private final Configuration configuration;
        private final DatabaseInfo databaseInfo;

        public Config(SQLTemplates sqlTemplates, Configuration configuration, DatabaseInfo databaseInfo) {
            this.sqlTemplates = sqlTemplates;
            this.configuration = configuration;
            this.databaseInfo = databaseInfo;
        }

        public SQLTemplates getSqlTemplates() {
            return this.sqlTemplates;
        }

        public Configuration getConfiguration() {
            return this.configuration;
        }

        public DatabaseInfo getDatabaseInfo() {
            return this.databaseInfo;
        }
    }

    public static class DatabaseInfo {
        private final String databaseProductName;
        private final String databaseProductVersion;
        private final String driverName;
        private final int databaseMajorVersion;
        private final int databaseMinorVersion;
        private final int driverMajorVersion;
        private final int driverMinorVersion;
        private final SupportedDatabase supportedDatabase;

        public DatabaseInfo(SupportedDatabase supportedDatabase, String databaseProductName, String databaseProductVersion, int databaseMajorVersion, int databaseMinorVersion, String driverName, int driverMajorVersion, int driverMinorVersion) {
            this.supportedDatabase = supportedDatabase;
            this.databaseProductName = databaseProductName;
            this.databaseProductVersion = databaseProductVersion;
            this.databaseMajorVersion = databaseMajorVersion;
            this.databaseMinorVersion = databaseMinorVersion;
            this.driverName = driverName;
            this.driverMajorVersion = driverMajorVersion;
            this.driverMinorVersion = driverMinorVersion;
        }

        public SupportedDatabase getSupportedDatabase() {
            return this.supportedDatabase;
        }

        public String getDatabaseProductName() {
            return this.databaseProductName;
        }

        public String getDatabaseProductVersion() {
            return this.databaseProductVersion;
        }

        public String getDriverName() {
            return this.driverName;
        }

        public int getDatabaseMajorVersion() {
            return this.databaseMajorVersion;
        }

        public int getDatabaseMinorVersion() {
            return this.databaseMinorVersion;
        }

        public int getDriverMajorVersion() {
            return this.driverMajorVersion;
        }

        public int getDriverMinorVersion() {
            return this.driverMinorVersion;
        }
    }

    public static enum SupportedDatabase {
        POSTGRESSQL,
        ORACLE,
        MYSQL,
        SQLSERVER,
        HSQLDB,
        H2;

    }
}

