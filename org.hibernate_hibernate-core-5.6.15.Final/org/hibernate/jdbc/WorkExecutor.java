/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;

public class WorkExecutor<T> {
    public <T> T executeWork(Work work, Connection connection) throws SQLException {
        work.execute(connection);
        return null;
    }

    public <T> T executeReturningWork(ReturningWork<T> work, Connection connection) throws SQLException {
        return work.execute(connection);
    }
}

