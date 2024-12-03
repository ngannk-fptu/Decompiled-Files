/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class CleanupUtils {
    public static void attemptClose(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        }
        catch (SQLException sQLException) {
            sQLException.printStackTrace();
        }
    }

    public static void attemptClose(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        }
        catch (SQLException sQLException) {
            sQLException.printStackTrace();
        }
    }

    public static void attemptRollback(Connection connection) {
        try {
            if (connection != null) {
                connection.rollback();
            }
        }
        catch (SQLException sQLException) {
            sQLException.printStackTrace();
        }
    }

    private CleanupUtils() {
    }
}

