/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.setup;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.setup.DatabaseEnum;
import com.atlassian.confluence.setup.JDBCUrlBuilder;
import com.atlassian.confluence.setup.MySQLJDBCUrlBuilder;
import com.atlassian.confluence.setup.OracleJDBCUrlBuilder;
import com.atlassian.confluence.setup.PostgreSQLJDBCUrlBuilder;
import com.atlassian.confluence.setup.SQLServerJDBCUrlBuilder;

@Internal
public class JDBCUrlBuilderFactory {
    public static JDBCUrlBuilder getInstance(String databaseType) {
        if (DatabaseEnum.MSSQL.getType().equals(databaseType)) {
            return new SQLServerJDBCUrlBuilder();
        }
        if (DatabaseEnum.ORACLE.getType().equals(databaseType)) {
            return new OracleJDBCUrlBuilder();
        }
        if (DatabaseEnum.MYSQL.getType().equals(databaseType)) {
            return new MySQLJDBCUrlBuilder();
        }
        if (DatabaseEnum.POSTGRESQL.getType().equals(databaseType)) {
            return new PostgreSQLJDBCUrlBuilder();
        }
        throw new IllegalArgumentException("Unsupported database type: " + databaseType);
    }
}

