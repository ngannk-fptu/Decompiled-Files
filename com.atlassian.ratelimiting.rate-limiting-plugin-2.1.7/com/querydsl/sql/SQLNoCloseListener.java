/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.SQLBaseListener;
import com.querydsl.sql.SQLListenerContext;

public final class SQLNoCloseListener
extends SQLBaseListener {
    public static final SQLNoCloseListener DEFAULT = new SQLNoCloseListener();

    private SQLNoCloseListener() {
    }

    @Override
    public void start(SQLListenerContext context) {
        context.setData(AbstractSQLQuery.PARENT_CONTEXT, context);
    }

    @Override
    public void end(SQLListenerContext context) {
        context.setData(AbstractSQLQuery.PARENT_CONTEXT, null);
    }
}

