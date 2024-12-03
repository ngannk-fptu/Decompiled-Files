/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.checkerframework.checker.nullness.qual.Nullable;

public class JdbcBlackHole {
    public static void close(@Nullable Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    public static void close(@Nullable Statement s) {
        try {
            if (s != null) {
                s.close();
            }
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    public static void close(@Nullable ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }
}

