/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Pair
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pocketknife.internal.querydsl.dialect;

import com.atlassian.pocketknife.api.querydsl.schema.DialectProvider;
import com.querydsl.sql.SQLServer2005Templates;
import com.querydsl.sql.SQLServer2008Templates;
import com.querydsl.sql.SQLServer2012Templates;
import com.querydsl.sql.SQLServerTemplates;
import com.querydsl.sql.SQLTemplates;
import io.atlassian.fugue.Pair;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DialectHelper {
    private static final Logger log = LoggerFactory.getLogger(DialectHelper.class);
    public static final String DB_URL_IDENTIFIER_POSTGRES = ":postgresql:";
    public static final String DB_URL_IDENTIFIER_ORACLE = ":oracle:";
    public static final String DB_URL_IDENTIFIER_HSQLDB = ":hsqldb:";
    public static final String DB_URL_IDENTIFIER_SQLSERVER = ":sqlserver:";
    public static final String DB_URL_IDENTIFIER_MYSQL = ":mysql:";
    public static final String DB_URL_IDENTIFIER_H2 = ":h2:";
    public static final int SQLSERVER_2005 = 9;
    public static final int SQLSERVER_2008 = 10;
    public static final int SQLSERVER_2012 = 11;

    public static boolean isSQLServer(@Nonnull String connStr) {
        return connStr.contains(DB_URL_IDENTIFIER_SQLSERVER);
    }

    public static Pair<SQLTemplates.Builder, DialectProvider.SupportedDatabase> getSQLServerDBTemplate(@Nonnull DatabaseMetaData metaData) throws SQLException {
        int currentSqlServerVersion = metaData.getDatabaseMajorVersion();
        log.debug("Initialize SQLServer template for version {}", (Object)currentSqlServerVersion);
        if (currentSqlServerVersion >= 11) {
            return Pair.pair((Object)SQLServer2012Templates.builder(), (Object)((Object)DialectProvider.SupportedDatabase.SQLSERVER));
        }
        if (currentSqlServerVersion >= 10) {
            return Pair.pair((Object)SQLServer2008Templates.builder(), (Object)((Object)DialectProvider.SupportedDatabase.SQLSERVER));
        }
        if (currentSqlServerVersion >= 9) {
            return Pair.pair((Object)SQLServer2005Templates.builder(), (Object)((Object)DialectProvider.SupportedDatabase.SQLSERVER));
        }
        return Pair.pair((Object)SQLServerTemplates.builder(), (Object)((Object)DialectProvider.SupportedDatabase.SQLSERVER));
    }
}

