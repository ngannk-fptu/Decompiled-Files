/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.tomcat.dbcp.dbcp2.AbandonedTrace;
import org.apache.tomcat.dbcp.dbcp2.DelegatingConnection;
import org.apache.tomcat.dbcp.dbcp2.DelegatingPreparedStatement;
import org.apache.tomcat.dbcp.pool2.KeyedObjectPool;

public class PoolablePreparedStatement<K>
extends DelegatingPreparedStatement {
    private final KeyedObjectPool<K, PoolablePreparedStatement<K>> pool;
    private final K key;
    private volatile boolean batchAdded;

    public PoolablePreparedStatement(PreparedStatement stmt, K key, KeyedObjectPool<K, PoolablePreparedStatement<K>> pool, DelegatingConnection<?> conn) {
        super(conn, stmt);
        this.pool = pool;
        this.key = key;
        this.removeThisTrace(conn);
    }

    @Override
    public void activate() throws SQLException {
        this.setClosedInternal(false);
        AbandonedTrace.add(this.getConnectionInternal(), this);
        super.activate();
    }

    @Override
    public void addBatch() throws SQLException {
        super.addBatch();
        this.batchAdded = true;
    }

    @Override
    public void clearBatch() throws SQLException {
        this.batchAdded = false;
        super.clearBatch();
    }

    @Override
    public void close() throws SQLException {
        if (!this.isClosed()) {
            try {
                this.pool.returnObject(this.key, this);
            }
            catch (RuntimeException | SQLException e) {
                throw e;
            }
            catch (Exception e) {
                throw new SQLException("Cannot close preparedstatement (return to pool failed)", e);
            }
        }
    }

    @Override
    public void passivate() throws SQLException {
        if (this.batchAdded) {
            this.clearBatch();
        }
        this.prepareToReturn();
    }
}

