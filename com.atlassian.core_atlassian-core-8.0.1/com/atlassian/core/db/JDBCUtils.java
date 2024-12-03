/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtils {
    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
    }

    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
    }

    public static void close(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }
}

