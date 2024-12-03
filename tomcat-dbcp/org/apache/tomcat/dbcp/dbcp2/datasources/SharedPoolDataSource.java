/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.datasources;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionPoolDataSource;
import org.apache.tomcat.dbcp.dbcp2.datasources.InstanceKeyDataSource;
import org.apache.tomcat.dbcp.dbcp2.datasources.InstanceKeyDataSourceFactory;
import org.apache.tomcat.dbcp.dbcp2.datasources.KeyedCPDSConnectionFactory;
import org.apache.tomcat.dbcp.dbcp2.datasources.PooledConnectionAndInfo;
import org.apache.tomcat.dbcp.dbcp2.datasources.PooledConnectionManager;
import org.apache.tomcat.dbcp.dbcp2.datasources.SharedPoolDataSourceFactory;
import org.apache.tomcat.dbcp.dbcp2.datasources.UserPassKey;
import org.apache.tomcat.dbcp.pool2.KeyedObjectPool;
import org.apache.tomcat.dbcp.pool2.impl.GenericKeyedObjectPool;
import org.apache.tomcat.dbcp.pool2.impl.GenericKeyedObjectPoolConfig;

public class SharedPoolDataSource
extends InstanceKeyDataSource {
    private static final long serialVersionUID = -1458539734480586454L;
    private int maxTotal = -1;
    private transient KeyedObjectPool<UserPassKey, PooledConnectionAndInfo> pool;
    private transient KeyedCPDSConnectionFactory factory;

    @Override
    public void close() throws SQLException {
        if (this.pool != null) {
            this.pool.close();
        }
        InstanceKeyDataSourceFactory.removeInstance(this.getInstanceKey());
    }

    @Override
    protected PooledConnectionManager getConnectionManager(UserPassKey userPassKey) {
        return this.factory;
    }

    public int getMaxTotal() {
        return this.maxTotal;
    }

    public int getNumActive() {
        return this.pool == null ? 0 : this.pool.getNumActive();
    }

    public int getNumIdle() {
        return this.pool == null ? 0 : this.pool.getNumIdle();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected PooledConnectionAndInfo getPooledConnectionAndInfo(String userName, String userPassword) throws SQLException {
        SharedPoolDataSource sharedPoolDataSource = this;
        synchronized (sharedPoolDataSource) {
            if (this.pool == null) {
                try {
                    this.registerPool(userName, userPassword);
                }
                catch (NamingException e) {
                    throw new SQLException("registerPool failed", e);
                }
            }
        }
        try {
            return this.pool.borrowObject(new UserPassKey(userName, userPassword));
        }
        catch (Exception e) {
            throw new SQLException("Could not retrieve connection info from pool", e);
        }
    }

    @Override
    public Reference getReference() throws NamingException {
        Reference ref = new Reference(this.getClass().getName(), SharedPoolDataSourceFactory.class.getName(), null);
        ref.add(new StringRefAddr("instanceKey", this.getInstanceKey()));
        return ref;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            in.defaultReadObject();
            SharedPoolDataSource oldDS = (SharedPoolDataSource)new SharedPoolDataSourceFactory().getObjectInstance((Object)this.getReference(), (Name)null, (Context)null, (Hashtable)null);
            this.pool = oldDS.pool;
        }
        catch (NamingException e) {
            throw new IOException("NamingException: " + e);
        }
    }

    private void registerPool(String userName, String password) throws NamingException, SQLException {
        ConnectionPoolDataSource cpds = this.testCPDS(userName, password);
        this.factory = new KeyedCPDSConnectionFactory(cpds, this.getValidationQuery(), this.getValidationQueryTimeoutDuration(), this.isRollbackAfterValidation());
        this.factory.setMaxConn(this.getMaxConnDuration());
        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
        config.setBlockWhenExhausted(this.getDefaultBlockWhenExhausted());
        config.setEvictionPolicyClassName(this.getDefaultEvictionPolicyClassName());
        config.setLifo(this.getDefaultLifo());
        config.setMaxIdlePerKey(this.getDefaultMaxIdle());
        config.setMaxTotal(this.getMaxTotal());
        config.setMaxTotalPerKey(this.getDefaultMaxTotal());
        config.setMaxWait(this.getDefaultMaxWait());
        config.setMinEvictableIdleDuration(this.getDefaultMinEvictableIdleDuration());
        config.setMinIdlePerKey(this.getDefaultMinIdle());
        config.setNumTestsPerEvictionRun(this.getDefaultNumTestsPerEvictionRun());
        config.setSoftMinEvictableIdleDuration(this.getDefaultSoftMinEvictableIdleDuration());
        config.setTestOnCreate(this.getDefaultTestOnCreate());
        config.setTestOnBorrow(this.getDefaultTestOnBorrow());
        config.setTestOnReturn(this.getDefaultTestOnReturn());
        config.setTestWhileIdle(this.getDefaultTestWhileIdle());
        config.setTimeBetweenEvictionRuns(this.getDefaultDurationBetweenEvictionRuns());
        GenericKeyedObjectPool<UserPassKey, PooledConnectionAndInfo> tmpPool = new GenericKeyedObjectPool<UserPassKey, PooledConnectionAndInfo>(this.factory, config);
        this.factory.setPool(tmpPool);
        this.pool = tmpPool;
    }

    public void setMaxTotal(int maxTotal) {
        this.assertInitializationAllowed();
        this.maxTotal = maxTotal;
    }

    @Override
    protected void setupDefaults(Connection connection, String userName) throws SQLException {
        Boolean defaultReadOnly;
        int defaultTransactionIsolation;
        Boolean defaultAutoCommit = this.isDefaultAutoCommit();
        if (defaultAutoCommit != null && connection.getAutoCommit() != defaultAutoCommit.booleanValue()) {
            connection.setAutoCommit(defaultAutoCommit);
        }
        if ((defaultTransactionIsolation = this.getDefaultTransactionIsolation()) != -1) {
            connection.setTransactionIsolation(defaultTransactionIsolation);
        }
        if ((defaultReadOnly = this.isDefaultReadOnly()) != null && connection.isReadOnly() != defaultReadOnly.booleanValue()) {
            connection.setReadOnly(defaultReadOnly);
        }
    }

    @Override
    protected void toStringFields(StringBuilder builder) {
        super.toStringFields(builder);
        builder.append(", maxTotal=");
        builder.append(this.maxTotal);
    }
}

