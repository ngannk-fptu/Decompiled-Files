/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pocketknife.api.querydsl.util;

import com.atlassian.annotations.PublicApi;
import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PublicApi
public class Connections {
    private static final Logger log = LoggerFactory.getLogger(Connections.class);

    public static void close(DatabaseConnection connection) {
        try {
            if (connection != null) {
                log.debug("Closing connection...");
                connection.close();
                log.debug("Closed connection");
            }
        }
        catch (Exception e) {
            log.warn("Unable to close SQL connection " + e);
        }
    }

    public static void close(Connection connection) {
        try {
            if (connection != null) {
                log.debug("Closing connection...");
                connection.close();
                log.debug("Closed connection");
            }
        }
        catch (SQLException e) {
            log.warn("Unable to close SQL connection " + e);
        }
    }

    public static void close(Statement statement) {
        try {
            if (statement != null) {
                log.debug("Closing statement...");
                statement.close();
                log.debug("Closed statement");
            }
        }
        catch (SQLException e) {
            log.warn("Unable to close SQL statement " + e);
        }
    }

    public static void close(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                log.debug("Closing result set...");
                resultSet.close();
                log.debug("Closed result set");
            }
        }
        catch (SQLException e) {
            log.warn("Unable to close SQL result set " + e);
        }
    }
}

