/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v1.db.sql.ConnectionUtils
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 */
package com.mchange.v2.c3p0;

import com.mchange.v1.db.sql.ConnectionUtils;
import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.ConnectionCustomizer;
import com.mchange.v2.c3p0.ConnectionTester;
import com.mchange.v2.c3p0.cfg.C3P0Config;
import com.mchange.v2.c3p0.cfg.C3P0ConfigUtils;
import com.mchange.v2.c3p0.impl.C3P0ImplUtils;
import com.mchange.v2.c3p0.impl.C3P0PooledConnection;
import com.mchange.v2.c3p0.impl.NewPooledConnection;
import com.mchange.v2.c3p0.impl.WrapperConnectionPoolDataSourceBase;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;

public final class WrapperConnectionPoolDataSource
extends WrapperConnectionPoolDataSourceBase
implements ConnectionPoolDataSource {
    static final MLogger logger = MLog.getLogger(WrapperConnectionPoolDataSource.class);
    ConnectionTester connectionTester;
    Map userOverrides;

    public WrapperConnectionPoolDataSource(boolean autoregister) {
        block2: {
            super(autoregister);
            this.connectionTester = C3P0Registry.getDefaultConnectionTester();
            this.setUpPropertyListeners();
            try {
                this.userOverrides = C3P0ImplUtils.parseUserOverridesAsString(this.getUserOverridesAsString());
            }
            catch (Exception e) {
                if (!logger.isLoggable(MLevel.WARNING)) break block2;
                logger.log(MLevel.WARNING, "Failed to parse stringified userOverrides. " + this.getUserOverridesAsString(), (Throwable)e);
            }
        }
    }

    public WrapperConnectionPoolDataSource() {
        this(true);
    }

    private void setUpPropertyListeners() {
        VetoableChangeListener setConnectionTesterListener = new VetoableChangeListener(){

            @Override
            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                String propName = evt.getPropertyName();
                Object val = evt.getNewValue();
                if ("connectionTesterClassName".equals(propName)) {
                    try {
                        WrapperConnectionPoolDataSource.this.recreateConnectionTester((String)val);
                    }
                    catch (Exception e) {
                        if (logger.isLoggable(MLevel.WARNING)) {
                            logger.log(MLevel.WARNING, "Failed to create ConnectionTester of class " + val, (Throwable)e);
                        }
                        throw new PropertyVetoException("Could not instantiate connection tester class with name '" + val + "'.", evt);
                    }
                }
                if ("userOverridesAsString".equals(propName)) {
                    try {
                        WrapperConnectionPoolDataSource.this.userOverrides = C3P0ImplUtils.parseUserOverridesAsString((String)val);
                    }
                    catch (Exception e) {
                        if (logger.isLoggable(MLevel.WARNING)) {
                            logger.log(MLevel.WARNING, "Failed to parse stringified userOverrides. " + val, (Throwable)e);
                        }
                        throw new PropertyVetoException("Failed to parse stringified userOverrides. " + val, evt);
                    }
                }
            }
        };
        this.addVetoableChangeListener(setConnectionTesterListener);
    }

    public WrapperConnectionPoolDataSource(String configName) {
        block3: {
            this();
            try {
                if (configName != null) {
                    C3P0Config.bindNamedConfigToBean(this, configName, true);
                }
            }
            catch (Exception e) {
                if (!logger.isLoggable(MLevel.WARNING)) break block3;
                logger.log(MLevel.WARNING, "Error binding WrapperConnectionPoolDataSource to named-config '" + configName + "'. Some default-config values may be used.", (Throwable)e);
            }
        }
    }

    @Override
    public PooledConnection getPooledConnection() throws SQLException {
        return this.getPooledConnection((ConnectionCustomizer)null, null);
    }

    @Override
    protected PooledConnection getPooledConnection(ConnectionCustomizer cc, String pdsIdt) throws SQLException {
        DataSource nds = this.getNestedDataSource();
        if (nds == null) {
            throw new SQLException("No standard DataSource has been set beneath this wrapper! [ nestedDataSource == null ]");
        }
        Connection conn = null;
        try {
            conn = nds.getConnection();
            if (conn == null) {
                throw new SQLException("An (unpooled) DataSource returned null from its getConnection() method! DataSource: " + this.getNestedDataSource());
            }
            if (this.isUsesTraditionalReflectiveProxies(this.getUser())) {
                return new C3P0PooledConnection(conn, this.connectionTester, this.isAutoCommitOnClose(this.getUser()), this.isForceIgnoreUnresolvedTransactions(this.getUser()), cc, pdsIdt);
            }
            return new NewPooledConnection(conn, this.connectionTester, this.isAutoCommitOnClose(this.getUser()), this.isForceIgnoreUnresolvedTransactions(this.getUser()), this.getPreferredTestQuery(this.getUser()), cc, pdsIdt);
        }
        catch (SQLException e) {
            ConnectionUtils.attemptClose((Connection)conn);
            throw e;
        }
        catch (RuntimeException re) {
            ConnectionUtils.attemptClose((Connection)conn);
            throw re;
        }
    }

    @Override
    public PooledConnection getPooledConnection(String user, String password) throws SQLException {
        return this.getPooledConnection(user, password, null, null);
    }

    @Override
    protected PooledConnection getPooledConnection(String user, String password, ConnectionCustomizer cc, String pdsIdt) throws SQLException {
        DataSource nds = this.getNestedDataSource();
        if (nds == null) {
            throw new SQLException("No standard DataSource has been set beneath this wrapper! [ nestedDataSource == null ]");
        }
        Connection conn = null;
        try {
            conn = nds.getConnection(user, password);
            if (conn == null) {
                throw new SQLException("An (unpooled) DataSource returned null from its getConnection() method! DataSource: " + this.getNestedDataSource());
            }
            if (this.isUsesTraditionalReflectiveProxies(user)) {
                return new C3P0PooledConnection(conn, this.connectionTester, this.isAutoCommitOnClose(user), this.isForceIgnoreUnresolvedTransactions(user), cc, pdsIdt);
            }
            return new NewPooledConnection(conn, this.connectionTester, this.isAutoCommitOnClose(user), this.isForceIgnoreUnresolvedTransactions(user), this.getPreferredTestQuery(user), cc, pdsIdt);
        }
        catch (SQLException e) {
            ConnectionUtils.attemptClose(conn);
            throw e;
        }
        catch (RuntimeException re) {
            ConnectionUtils.attemptClose(conn);
            throw re;
        }
    }

    private boolean isAutoCommitOnClose(String userName) {
        if (userName == null) {
            return this.isAutoCommitOnClose();
        }
        Boolean override = C3P0ConfigUtils.extractBooleanOverride("autoCommitOnClose", userName, this.userOverrides);
        return override == null ? this.isAutoCommitOnClose() : override.booleanValue();
    }

    private boolean isForceIgnoreUnresolvedTransactions(String userName) {
        if (userName == null) {
            return this.isForceIgnoreUnresolvedTransactions();
        }
        Boolean override = C3P0ConfigUtils.extractBooleanOverride("forceIgnoreUnresolvedTransactions", userName, this.userOverrides);
        return override == null ? this.isForceIgnoreUnresolvedTransactions() : override.booleanValue();
    }

    private boolean isUsesTraditionalReflectiveProxies(String userName) {
        if (userName == null) {
            return this.isUsesTraditionalReflectiveProxies();
        }
        Boolean override = C3P0ConfigUtils.extractBooleanOverride("usesTraditionalReflectiveProxies", userName, this.userOverrides);
        return override == null ? this.isUsesTraditionalReflectiveProxies() : override.booleanValue();
    }

    private String getPreferredTestQuery(String userName) {
        if (userName == null) {
            return this.getPreferredTestQuery();
        }
        String override = (String)C3P0ConfigUtils.extractUserOverride("preferredTestQuery", userName, this.userOverrides);
        return override == null ? this.getPreferredTestQuery() : override;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.getNestedDataSource().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.getNestedDataSource().setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.getNestedDataSource().setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.getNestedDataSource().getLoginTimeout();
    }

    public String getUser() {
        try {
            return C3P0ImplUtils.findAuth(this.getNestedDataSource()).getUser();
        }
        catch (SQLException e) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.log(MLevel.WARNING, "An Exception occurred while trying to find the 'user' property from our nested DataSource. Defaulting to no specified username.", (Throwable)e);
            }
            return null;
        }
    }

    public String getPassword() {
        try {
            return C3P0ImplUtils.findAuth(this.getNestedDataSource()).getPassword();
        }
        catch (SQLException e) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.log(MLevel.WARNING, "An Exception occurred while trying to find the 'password' property from our nested DataSource. Defaulting to no specified password.", (Throwable)e);
            }
            return null;
        }
    }

    public Map getUserOverrides() {
        return this.userOverrides;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        return sb.toString();
    }

    @Override
    protected String extraToStringInfo() {
        if (this.userOverrides != null) {
            return "; userOverrides: " + this.userOverrides.toString();
        }
        return null;
    }

    private synchronized void recreateConnectionTester(String className) throws Exception {
        ConnectionTester ct;
        this.connectionTester = className != null ? (ct = (ConnectionTester)Class.forName(className).newInstance()) : C3P0Registry.getDefaultConnectionTester();
    }
}

