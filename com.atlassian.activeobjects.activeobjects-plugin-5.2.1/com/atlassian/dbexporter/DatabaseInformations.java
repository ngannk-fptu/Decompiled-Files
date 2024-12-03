/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter;

import com.atlassian.dbexporter.DatabaseInformation;
import java.util.Locale;

public final class DatabaseInformations {
    private DatabaseInformations() {
    }

    public static Database database(DatabaseInformation info) {
        return new DatabaseImpl(info.get("database.name", new DatabaseTypeConverter()));
    }

    private static class DatabaseImpl
    implements Database {
        private final Database.Type type;

        public DatabaseImpl(Database.Type type) {
            this.type = type;
        }

        @Override
        public Database.Type getType() {
            return this.type;
        }

        public String toString() {
            return String.valueOf((Object)this.getType());
        }
    }

    private static class DatabaseTypeConverter
    extends DatabaseInformation.AbstractStringConverter<Database.Type> {
        private DatabaseTypeConverter() {
        }

        @Override
        public Database.Type convert(String dbName) {
            if (this.isEmpty(dbName)) {
                return Database.Type.UNKNOWN;
            }
            if (this.isH2(dbName)) {
                return Database.Type.H2;
            }
            if (this.isHsql(dbName)) {
                return Database.Type.HSQL;
            }
            if (this.isMySql(dbName)) {
                return Database.Type.MYSQL;
            }
            if (this.isPostgres(dbName)) {
                return Database.Type.POSTGRES;
            }
            if (this.isOracle(dbName)) {
                return Database.Type.ORACLE;
            }
            if (this.isMsSql(dbName)) {
                return Database.Type.MSSQL;
            }
            return Database.Type.UNKNOWN;
        }

        private boolean isEmpty(String dbName) {
            return dbName == null || dbName.trim().length() == 0;
        }

        private boolean isH2(String dbName) {
            return this.startsWithIgnoreCase(dbName, "H2");
        }

        private boolean isHsql(String dbName) {
            return this.startsWithIgnoreCase(dbName, "HSQL");
        }

        private boolean isMySql(String dbName) {
            return this.startsWithIgnoreCase(dbName, "MySQL");
        }

        private boolean isPostgres(String dbName) {
            return this.startsWithIgnoreCase(dbName, "PostgreSQL");
        }

        private boolean isOracle(String dbName) {
            return this.startsWithIgnoreCase(dbName, "Oracle");
        }

        private boolean isMsSql(String dbName) {
            return this.startsWithIgnoreCase(dbName, "Microsoft");
        }

        private boolean startsWithIgnoreCase(String s, String start) {
            return this.toLowerCase(s).startsWith(this.toLowerCase(start));
        }

        private String toLowerCase(String s) {
            return s == null ? s : s.toLowerCase(Locale.ENGLISH);
        }
    }

    public static interface Database {
        public Type getType();

        public static enum Type {
            H2,
            HSQL,
            MYSQL,
            POSTGRES,
            ORACLE,
            MSSQL,
            UNKNOWN;

        }
    }
}

