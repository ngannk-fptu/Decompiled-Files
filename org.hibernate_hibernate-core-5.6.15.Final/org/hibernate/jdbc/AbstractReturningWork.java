/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.WorkExecutor;
import org.hibernate.jdbc.WorkExecutorVisitable;

public abstract class AbstractReturningWork<T>
implements ReturningWork<T>,
WorkExecutorVisitable<T> {
    @Override
    public T accept(WorkExecutor<T> executor, Connection connection) throws SQLException {
        return executor.executeReturningWork(this, connection);
    }
}

