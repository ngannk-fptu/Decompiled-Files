/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import java.sql.Connection;
import java.sql.SQLException;
import net.java.ao.DatabaseProvider;
import net.java.ao.EntityManager;
import net.java.ao.sql.SqlUtils;

public abstract class Transaction<T> {
    private final EntityManager manager;

    public Transaction(EntityManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("EntityManager instance cannot be null");
        }
        this.manager = manager;
    }

    protected final EntityManager getEntityManager() {
        return this.manager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T execute() throws SQLException {
        DatabaseProvider provider = this.manager.getProvider();
        TransactionState state = TransactionState.START;
        Connection c = null;
        try {
            c = provider.startTransaction();
            state = TransactionState.RUNNING;
            T back = this.run();
            provider.commitTransaction(c);
            state = TransactionState.COMMITTED;
            T t = back;
            return t;
        }
        finally {
            if (state == TransactionState.RUNNING && c != null) {
                provider.rollbackTransaction(c);
            }
            provider.setCloseable(c, true);
            SqlUtils.closeQuietly(c);
        }
    }

    protected abstract T run() throws SQLException;

    private static enum TransactionState {
        START,
        RUNNING,
        COMMITTED;

    }
}

