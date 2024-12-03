/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.admin.tables;

import com.google.common.base.Preconditions;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import net.java.ao.DatabaseProvider;
import net.java.ao.sql.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RowCounter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DatabaseProvider provider;

    private RowCounter(DatabaseProvider provider) {
        this.provider = Objects.requireNonNull(provider);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    int count(String tableName) {
        int n;
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            connection = this.provider.getConnection();
            stmt = this.provider.preparedStatement(connection, "SELECT COUNT(*) FROM " + this.provider.withSchema(tableName));
            res = stmt.executeQuery();
            Preconditions.checkState((boolean)res.next());
            n = res.getInt(1);
            SqlUtils.closeQuietly(res);
        }
        catch (SQLException e) {
            this.logger.warn("Could not count number of rows for table '{}'", (Object)tableName);
            this.logger.warn("Here is the exception:", (Throwable)e);
            int n2 = -1;
            return n2;
        }
        finally {
            SqlUtils.closeQuietly(res);
            SqlUtils.closeQuietly(stmt);
            SqlUtils.closeQuietly(connection);
        }
        SqlUtils.closeQuietly(stmt);
        SqlUtils.closeQuietly(connection);
        return n;
    }

    static RowCounter from(DatabaseProvider provider) {
        return new RowCounter(provider);
    }
}

