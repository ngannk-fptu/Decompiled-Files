/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.tomcat.dbcp.dbcp2.AbandonedTrace;
import org.apache.tomcat.dbcp.dbcp2.DelegatingCallableStatement;
import org.apache.tomcat.dbcp.dbcp2.DelegatingConnection;
import org.apache.tomcat.dbcp.dbcp2.DelegatingPreparedStatement;
import org.apache.tomcat.dbcp.dbcp2.PStmtKey;
import org.apache.tomcat.dbcp.pool2.KeyedObjectPool;

public class PoolableCallableStatement
extends DelegatingCallableStatement {
    private final KeyedObjectPool<PStmtKey, DelegatingPreparedStatement> pool;
    private final PStmtKey key;

    public PoolableCallableStatement(CallableStatement callableStatement, PStmtKey key, KeyedObjectPool<PStmtKey, DelegatingPreparedStatement> pool, DelegatingConnection<Connection> connection) {
        super(connection, callableStatement);
        this.pool = pool;
        this.key = key;
        this.removeThisTrace(connection);
    }

    @Override
    public void activate() throws SQLException {
        this.setClosedInternal(false);
        AbandonedTrace.add(this.getConnectionInternal(), this);
        super.activate();
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
                throw new SQLException("Cannot close CallableStatement (return to pool failed)", e);
            }
        }
    }

    @Override
    public void passivate() throws SQLException {
        this.prepareToReturn();
    }
}

