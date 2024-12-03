/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.managed;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import javax.management.ObjectName;
import org.apache.tomcat.dbcp.dbcp2.DelegatingPreparedStatement;
import org.apache.tomcat.dbcp.dbcp2.PStmtKey;
import org.apache.tomcat.dbcp.dbcp2.PoolableConnection;
import org.apache.tomcat.dbcp.dbcp2.PoolableConnectionFactory;
import org.apache.tomcat.dbcp.dbcp2.PoolingConnection;
import org.apache.tomcat.dbcp.dbcp2.managed.PoolableManagedConnection;
import org.apache.tomcat.dbcp.dbcp2.managed.TransactionRegistry;
import org.apache.tomcat.dbcp.dbcp2.managed.XAConnectionFactory;
import org.apache.tomcat.dbcp.pool2.PooledObject;
import org.apache.tomcat.dbcp.pool2.impl.DefaultPooledObject;
import org.apache.tomcat.dbcp.pool2.impl.GenericKeyedObjectPool;
import org.apache.tomcat.dbcp.pool2.impl.GenericKeyedObjectPoolConfig;

public class PoolableManagedConnectionFactory
extends PoolableConnectionFactory {
    private final TransactionRegistry transactionRegistry;

    public PoolableManagedConnectionFactory(XAConnectionFactory connFactory, ObjectName dataSourceJmxName) {
        super(connFactory, dataSourceJmxName);
        this.transactionRegistry = connFactory.getTransactionRegistry();
    }

    public TransactionRegistry getTransactionRegistry() {
        return this.transactionRegistry;
    }

    @Override
    public synchronized PooledObject<PoolableConnection> makeObject() throws SQLException {
        Connection conn = this.getConnectionFactory().createConnection();
        if (conn == null) {
            throw new IllegalStateException("Connection factory returned null from createConnection");
        }
        this.initializeConnection(conn);
        if (this.getPoolStatements()) {
            conn = new PoolingConnection(conn);
            GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
            config.setMaxTotalPerKey(-1);
            config.setBlockWhenExhausted(false);
            config.setMaxWait(Duration.ZERO);
            config.setMaxIdlePerKey(1);
            config.setMaxTotal(this.getMaxOpenPreparedStatements());
            ObjectName dataSourceJmxName = this.getDataSourceJmxName();
            long connIndex = this.getConnectionIndex().getAndIncrement();
            if (dataSourceJmxName != null) {
                StringBuilder base = new StringBuilder(dataSourceJmxName.toString());
                base.append(",connectionpool=connections,connection=");
                base.append(connIndex);
                config.setJmxNameBase(base.toString());
                config.setJmxNamePrefix(",statementpool=statements");
            } else {
                config.setJmxEnabled(false);
            }
            GenericKeyedObjectPool<PStmtKey, DelegatingPreparedStatement> stmtPool = new GenericKeyedObjectPool<PStmtKey, DelegatingPreparedStatement>((PoolingConnection)conn, config);
            ((PoolingConnection)conn).setStatementPool(stmtPool);
            ((PoolingConnection)conn).setCacheState(this.getCacheState());
        }
        PoolableManagedConnection pmc = new PoolableManagedConnection(this.transactionRegistry, conn, this.getPool(), this.getDisconnectionSqlCodes(), this.isFastFailValidation());
        pmc.setCacheState(this.getCacheState());
        return new DefaultPooledObject<PoolableConnection>(pmc);
    }
}

