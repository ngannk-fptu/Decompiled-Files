/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SQLUtils {
    private static final Logger log = LoggerFactory.getLogger(SQLUtils.class);

    private SQLUtils() {
    }

    public static void closeConnectionQuietly(Connection c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    public static void closeStatementQuietly(Statement s) {
        if (s == null) {
            return;
        }
        try {
            s.close();
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    public static void closeResultSetQuietly(ResultSet s) {
        if (s == null) {
            return;
        }
        try {
            s.close();
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }
}

