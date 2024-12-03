/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.jdbc.Work;
import org.hibernate.jdbc.WorkExecutor;
import org.hibernate.jdbc.WorkExecutorVisitable;

public abstract class AbstractWork
implements Work,
WorkExecutorVisitable<Void> {
    @Override
    public Void accept(WorkExecutor<Void> executor, Connection connection) throws SQLException {
        return executor.executeWork(this, connection);
    }
}

