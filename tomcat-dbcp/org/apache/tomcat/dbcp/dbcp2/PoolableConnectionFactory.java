/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.dbcp.dbcp2.ConnectionFactory;
import org.apache.tomcat.dbcp.dbcp2.DelegatingPreparedStatement;
import org.apache.tomcat.dbcp.dbcp2.Jdbc41Bridge;
import org.apache.tomcat.dbcp.dbcp2.LifetimeExceededException;
import org.apache.tomcat.dbcp.dbcp2.PStmtKey;
import org.apache.tomcat.dbcp.dbcp2.PoolableConnection;
import org.apache.tomcat.dbcp.dbcp2.PoolingConnection;
import org.apache.tomcat.dbcp.dbcp2.Utils;
import org.apache.tomcat.dbcp.pool2.DestroyMode;
import org.apache.tomcat.dbcp.pool2.ObjectPool;
import org.apache.tomcat.dbcp.pool2.PooledObject;
import org.apache.tomcat.dbcp.pool2.PooledObjectFactory;
import org.apache.tomcat.dbcp.pool2.impl.DefaultPooledObject;
import org.apache.tomcat.dbcp.pool2.impl.GenericKeyedObjectPool;
import org.apache.tomcat.dbcp.pool2.impl.GenericKeyedObjectPoolConfig;

public class PoolableConnectionFactory
implements PooledObjectFactory<PoolableConnection> {
    private static final Log log = LogFactory.getLog(PoolableConnectionFactory.class);
    static final int UNKNOWN_TRANSACTION_ISOLATION = -1;
    private final ConnectionFactory connectionFactory;
    private final ObjectName dataSourceJmxObjectName;
    private volatile String validationQuery;
    private volatile Duration validationQueryTimeoutDuration = Duration.ofSeconds(-1L);
    private Collection<String> connectionInitSqls;
    private Collection<String> disconnectionSqlCodes;
    private boolean fastFailValidation = true;
    private volatile ObjectPool<PoolableConnection> pool;
    private Boolean defaultReadOnly;
    private Boolean defaultAutoCommit;
    private boolean autoCommitOnReturn = true;
    private boolean rollbackOnReturn = true;
    private int defaultTransactionIsolation = -1;
    private String defaultCatalog;
    private String defaultSchema;
    private boolean cacheState;
    private boolean poolStatements;
    private boolean clearStatementPoolOnReturn;
    private int maxOpenPreparedStatements = 8;
    private Duration maxConnDuration = Duration.ofMillis(-1L);
    private final AtomicLong connectionIndex = new AtomicLong();
    private Duration defaultQueryTimeoutDuration;

    public PoolableConnectionFactory(ConnectionFactory connFactory, ObjectName dataSourceJmxObjectName) {
        this.connectionFactory = connFactory;
        this.dataSourceJmxObjectName = dataSourceJmxObjectName;
    }

    @Override
    public void activateObject(PooledObject<PoolableConnection> p) throws SQLException {
        this.validateLifetime(p);
        PoolableConnection pConnection = p.getObject();
        pConnection.activate();
        if (this.defaultAutoCommit != null && pConnection.getAutoCommit() != this.defaultAutoCommit.booleanValue()) {
            pConnection.setAutoCommit(this.defaultAutoCommit);
        }
        if (this.defaultTransactionIsolation != -1 && pConnection.getTransactionIsolation() != this.defaultTransactionIsolation) {
            pConnection.setTransactionIsolation(this.defaultTransactionIsolation);
        }
        if (this.defaultReadOnly != null && pConnection.isReadOnly() != this.defaultReadOnly.booleanValue()) {
            pConnection.setReadOnly(this.defaultReadOnly);
        }
        if (this.defaultCatalog != null && !this.defaultCatalog.equals(pConnection.getCatalog())) {
            pConnection.setCatalog(this.defaultCatalog);
        }
        if (this.defaultSchema != null && !this.defaultSchema.equals(Jdbc41Bridge.getSchema(pConnection))) {
            Jdbc41Bridge.setSchema(pConnection, this.defaultSchema);
        }
        pConnection.setDefaultQueryTimeout(this.defaultQueryTimeoutDuration);
    }

    @Override
    public void destroyObject(PooledObject<PoolableConnection> p) throws SQLException {
        p.getObject().reallyClose();
    }

    @Override
    public void destroyObject(PooledObject<PoolableConnection> p, DestroyMode mode) throws SQLException {
        if (mode == DestroyMode.ABANDONED) {
            p.getObject().getInnermostDelegate().abort(Runnable::run);
        } else {
            p.getObject().reallyClose();
        }
    }

    public boolean getCacheState() {
        return this.cacheState;
    }

    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }

    protected AtomicLong getConnectionIndex() {
        return this.connectionIndex;
    }

    public Collection<String> getConnectionInitSqls() {
        return this.connectionInitSqls;
    }

    public ObjectName getDataSourceJmxName() {
        return this.dataSourceJmxObjectName;
    }

    public ObjectName getDataSourceJmxObjectName() {
        return this.dataSourceJmxObjectName;
    }

    public Boolean getDefaultAutoCommit() {
        return this.defaultAutoCommit;
    }

    public String getDefaultCatalog() {
        return this.defaultCatalog;
    }

    @Deprecated
    public Integer getDefaultQueryTimeout() {
        return this.getDefaultQueryTimeoutSeconds();
    }

    public Duration getDefaultQueryTimeoutDuration() {
        return this.defaultQueryTimeoutDuration;
    }

    @Deprecated
    public Integer getDefaultQueryTimeoutSeconds() {
        return this.defaultQueryTimeoutDuration == null ? null : Integer.valueOf((int)this.defaultQueryTimeoutDuration.getSeconds());
    }

    public Boolean getDefaultReadOnly() {
        return this.defaultReadOnly;
    }

    public String getDefaultSchema() {
        return this.defaultSchema;
    }

    public int getDefaultTransactionIsolation() {
        return this.defaultTransactionIsolation;
    }

    public Collection<String> getDisconnectionSqlCodes() {
        return this.disconnectionSqlCodes;
    }

    public Duration getMaxConnDuration() {
        return this.maxConnDuration;
    }

    public long getMaxConnLifetimeMillis() {
        return this.maxConnDuration.toMillis();
    }

    protected int getMaxOpenPreparedStatements() {
        return this.maxOpenPreparedStatements;
    }

    public synchronized ObjectPool<PoolableConnection> getPool() {
        return this.pool;
    }

    public boolean getPoolStatements() {
        return this.poolStatements;
    }

    public String getValidationQuery() {
        return this.validationQuery;
    }

    public Duration getValidationQueryTimeoutDuration() {
        return this.validationQueryTimeoutDuration;
    }

    @Deprecated
    public int getValidationQueryTimeoutSeconds() {
        return (int)this.validationQueryTimeoutDuration.getSeconds();
    }

    protected void initializeConnection(Connection conn) throws SQLException {
        Collection<String> sqls = this.connectionInitSqls;
        if (conn.isClosed()) {
            throw new SQLException("initializeConnection: connection closed");
        }
        if (!Utils.isEmpty(sqls)) {
            try (Statement statement = conn.createStatement();){
                for (String sql : sqls) {
                    statement.execute(Objects.requireNonNull(sql, "null connectionInitSqls element"));
                }
            }
        }
    }

    public boolean isAutoCommitOnReturn() {
        return this.autoCommitOnReturn;
    }

    @Deprecated
    public boolean isEnableAutoCommitOnReturn() {
        return this.autoCommitOnReturn;
    }

    public boolean isFastFailValidation() {
        return this.fastFailValidation;
    }

    public boolean isRollbackOnReturn() {
        return this.rollbackOnReturn;
    }

    @Override
    public PooledObject<PoolableConnection> makeObject() throws SQLException {
        ObjectName connJmxName;
        Connection conn = this.connectionFactory.createConnection();
        if (conn == null) {
            throw new IllegalStateException("Connection factory returned null from createConnection");
        }
        try {
            this.initializeConnection(conn);
        }
        catch (SQLException e) {
            Utils.closeQuietly((AutoCloseable)conn);
            throw e;
        }
        long connIndex = this.connectionIndex.getAndIncrement();
        if (this.poolStatements) {
            conn = new PoolingConnection(conn);
            GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
            config.setMaxTotalPerKey(-1);
            config.setBlockWhenExhausted(false);
            config.setMaxWait(Duration.ZERO);
            config.setMaxIdlePerKey(1);
            config.setMaxTotal(this.maxOpenPreparedStatements);
            if (this.dataSourceJmxObjectName != null) {
                StringBuilder base = new StringBuilder(this.dataSourceJmxObjectName.toString());
                base.append(",connectionpool=connections,connection=");
                base.append(connIndex);
                config.setJmxNameBase(base.toString());
                config.setJmxNamePrefix(",statementpool=statements");
            } else {
                config.setJmxEnabled(false);
            }
            PoolingConnection poolingConn = (PoolingConnection)conn;
            GenericKeyedObjectPool<PStmtKey, DelegatingPreparedStatement> stmtPool = new GenericKeyedObjectPool<PStmtKey, DelegatingPreparedStatement>(poolingConn, config);
            poolingConn.setStatementPool(stmtPool);
            poolingConn.setClearStatementPoolOnReturn(this.clearStatementPoolOnReturn);
            poolingConn.setCacheState(this.cacheState);
        }
        if (this.dataSourceJmxObjectName == null) {
            connJmxName = null;
        } else {
            String name = this.dataSourceJmxObjectName.toString() + ",connectionpool=connections,connection=" + connIndex;
            try {
                connJmxName = new ObjectName(name);
            }
            catch (MalformedObjectNameException e) {
                Utils.closeQuietly((AutoCloseable)conn);
                throw new SQLException(name, e);
            }
        }
        PoolableConnection pc = new PoolableConnection(conn, this.pool, connJmxName, this.disconnectionSqlCodes, this.fastFailValidation);
        pc.setCacheState(this.cacheState);
        return new DefaultPooledObject<PoolableConnection>(pc);
    }

    @Override
    public void passivateObject(PooledObject<PoolableConnection> p) throws SQLException {
        this.validateLifetime(p);
        PoolableConnection conn = p.getObject();
        Boolean connAutoCommit = null;
        if (this.rollbackOnReturn && !(connAutoCommit = Boolean.valueOf(conn.getAutoCommit())).booleanValue() && !conn.isReadOnly()) {
            conn.rollback();
        }
        conn.clearWarnings();
        if (this.autoCommitOnReturn) {
            if (connAutoCommit == null) {
                connAutoCommit = conn.getAutoCommit();
            }
            if (!connAutoCommit.booleanValue()) {
                conn.setAutoCommit(true);
            }
        }
        conn.passivate();
    }

    public void setAutoCommitOnReturn(boolean autoCommitOnReturn) {
        this.autoCommitOnReturn = autoCommitOnReturn;
    }

    public void setCacheState(boolean cacheState) {
        this.cacheState = cacheState;
    }

    public void setClearStatementPoolOnReturn(boolean clearStatementPoolOnReturn) {
        this.clearStatementPoolOnReturn = clearStatementPoolOnReturn;
    }

    public void setConnectionInitSql(Collection<String> connectionInitSqls) {
        this.connectionInitSqls = connectionInitSqls;
    }

    public void setDefaultAutoCommit(Boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public void setDefaultCatalog(String defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
    }

    public void setDefaultQueryTimeout(Duration defaultQueryTimeoutDuration) {
        this.defaultQueryTimeoutDuration = defaultQueryTimeoutDuration;
    }

    @Deprecated
    public void setDefaultQueryTimeout(Integer defaultQueryTimeoutSeconds) {
        this.defaultQueryTimeoutDuration = defaultQueryTimeoutSeconds == null ? null : Duration.ofSeconds(defaultQueryTimeoutSeconds.longValue());
    }

    public void setDefaultReadOnly(Boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
    }

    public void setDefaultSchema(String defaultSchema) {
        this.defaultSchema = defaultSchema;
    }

    public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }

    public void setDisconnectionSqlCodes(Collection<String> disconnectionSqlCodes) {
        this.disconnectionSqlCodes = disconnectionSqlCodes;
    }

    @Deprecated
    public void setEnableAutoCommitOnReturn(boolean autoCommitOnReturn) {
        this.autoCommitOnReturn = autoCommitOnReturn;
    }

    public void setFastFailValidation(boolean fastFailValidation) {
        this.fastFailValidation = fastFailValidation;
    }

    public void setMaxConn(Duration maxConnDuration) {
        this.maxConnDuration = maxConnDuration;
    }

    @Deprecated
    public void setMaxConnLifetimeMillis(long maxConnLifetimeMillis) {
        this.maxConnDuration = Duration.ofMillis(maxConnLifetimeMillis);
    }

    public void setMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
        this.maxOpenPreparedStatements = maxOpenPreparedStatements;
    }

    @Deprecated
    public void setMaxOpenPrepatedStatements(int maxOpenPreparedStatements) {
        this.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
    }

    public synchronized void setPool(ObjectPool<PoolableConnection> pool) {
        if (null != this.pool && pool != this.pool) {
            Utils.closeQuietly(this.pool);
        }
        this.pool = pool;
    }

    public void setPoolStatements(boolean poolStatements) {
        this.poolStatements = poolStatements;
    }

    public void setRollbackOnReturn(boolean rollbackOnReturn) {
        this.rollbackOnReturn = rollbackOnReturn;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public void setValidationQueryTimeout(Duration validationQueryTimeoutDuration) {
        this.validationQueryTimeoutDuration = validationQueryTimeoutDuration;
    }

    @Deprecated
    public void setValidationQueryTimeout(int validationQueryTimeoutSeconds) {
        this.validationQueryTimeoutDuration = Duration.ofSeconds(validationQueryTimeoutSeconds);
    }

    public void validateConnection(PoolableConnection conn) throws SQLException {
        if (conn.isClosed()) {
            throw new SQLException("validateConnection: connection closed");
        }
        conn.validate(this.validationQuery, this.validationQueryTimeoutDuration);
    }

    private void validateLifetime(PooledObject<PoolableConnection> p) throws LifetimeExceededException {
        Utils.validateLifetime(p, this.maxConnDuration);
    }

    @Override
    public boolean validateObject(PooledObject<PoolableConnection> p) {
        try {
            this.validateLifetime(p);
            this.validateConnection(p.getObject());
            return true;
        }
        catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug((Object)Utils.getMessage("poolableConnectionFactory.validateObject.fail"), (Throwable)e);
            }
            return false;
        }
    }
}

