/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model;

import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;

public class CustomSql {
    private final String sql;
    private final boolean isCallable;
    private final ExecuteUpdateResultCheckStyle checkStyle;

    public CustomSql(String sql, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
        this.sql = sql;
        this.isCallable = callable;
        this.checkStyle = checkStyle;
    }

    public String getSql() {
        return this.sql;
    }

    public boolean isCallable() {
        return this.isCallable;
    }

    public ExecuteUpdateResultCheckStyle getCheckStyle() {
        return this.checkStyle;
    }
}

