/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.monitor.db;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlOperation<T> {
    public T execute() throws SQLException;
}

