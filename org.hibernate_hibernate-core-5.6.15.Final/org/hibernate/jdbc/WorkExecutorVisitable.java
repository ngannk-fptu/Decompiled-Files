/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.jdbc.WorkExecutor;

public interface WorkExecutorVisitable<T> {
    public T accept(WorkExecutor<T> var1, Connection var2) throws SQLException;
}

