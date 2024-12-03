/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.QueryException;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.SQLBaseListener;
import com.querydsl.sql.SQLListenerContext;
import java.sql.Connection;
import java.sql.SQLException;

public final class SQLCloseListener
extends SQLBaseListener {
    public static final SQLCloseListener DEFAULT = new SQLCloseListener();

    private SQLCloseListener() {
    }

    @Override
    public void end(SQLListenerContext context) {
        Connection connection = context.getConnection();
        if (connection != null && context.getData(AbstractSQLQuery.PARENT_CONTEXT) == null) {
            try {
                connection.close();
            }
            catch (SQLException e) {
                throw new QueryException(e);
            }
        }
    }
}

