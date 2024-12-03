/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.setup.settings;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DatabaseErrorMessageConverter {
    private static final String MSSQL_JDBC_DRIVER_EXCEPTION = "com.microsoft.sqlserver.jdbc.SQLServerException";
    private static final String DEFAULT_KEY = "setup.database.test.connection.failed.generic";
    private static final Map<String, Map<String, String>> dbMessage = new HashMap<String, Map<String, String>>();

    public static String getMessageKey(@NonNull String database, @NonNull SQLException exception) {
        String sqlError = DatabaseErrorMessageConverter.getErrorKey(database, exception);
        Map<String, String> messages = dbMessage.get(DatabaseErrorMessageConverter.getDatabaseKey(database));
        if (messages == null) {
            return DEFAULT_KEY;
        }
        String key = messages.get(sqlError);
        return key == null ? DEFAULT_KEY : key;
    }

    private static String getDatabaseKey(@NonNull String database) {
        return database.startsWith("oracle") ? "oracle" : database;
    }

    private static String getErrorKey(String database, SQLException exception) {
        if (database.startsWith("oracle")) {
            return Integer.toString(exception.getErrorCode());
        }
        if ("mssql".equals(database) && MSSQL_JDBC_DRIVER_EXCEPTION.equals(exception.getClass().getName())) {
            return DatabaseErrorMessageConverter.mix(exception.getSQLState(), exception.getErrorCode());
        }
        return exception.getSQLState();
    }

    private static String mix(String sqlState, int errorCode) {
        return String.format("[%s][%d]", sqlState, errorCode);
    }

    static {
        HashMap<String, String> mssql = new HashMap<String, String>();
        dbMessage.put("mssql", mssql);
        mssql.put("08S01", "setup.database.test.connection.failed.host.or.port");
        mssql.put("08S03", "setup.database.test.connection.failed.host.or.port");
        mssql.put("28000", "setup.database.test.connection.failed.credential");
        mssql.put("S1000", "setup.database.test.connection.failed.permission");
        mssql.put(DatabaseErrorMessageConverter.mix("S0001", 4060), "setup.database.test.connection.failed.permission");
        mssql.put(DatabaseErrorMessageConverter.mix("S0001", 18456), "setup.database.test.connection.failed.credential");
        mssql.put(DatabaseErrorMessageConverter.mix("08S01", 0), "setup.database.test.connection.failed.host.or.port");
        HashMap<String, String> mysql = new HashMap<String, String>();
        dbMessage.put("mysql", mysql);
        mysql.put("08S01", "setup.database.test.connection.failed.host.or.port");
        mysql.put("28000", "setup.database.test.connection.failed.credential");
        mysql.put("42000", "setup.database.test.connection.failed.permission");
        HashMap<String, String> oracle = new HashMap<String, String>();
        dbMessage.put("oracle", oracle);
        oracle.put("12541", "setup.database.test.connection.failed.host.or.port");
        oracle.put("17002", "setup.database.test.connection.failed.host.or.port");
        oracle.put("17868", "setup.database.test.connection.failed.host.or.port");
        oracle.put("1005", "setup.database.test.connection.failed.credential");
        oracle.put("1017", "setup.database.test.connection.failed.credential");
        oracle.put("1045", "setup.database.test.connection.failed.permission");
        HashMap<String, String> postgreSql = new HashMap<String, String>();
        dbMessage.put("postgresql", postgreSql);
        postgreSql.put("08001", "setup.database.test.connection.failed.host.or.port");
        postgreSql.put("28P01", "setup.database.test.connection.failed.credential");
        postgreSql.put("3D000", "setup.database.test.connection.failed.permission");
    }
}

