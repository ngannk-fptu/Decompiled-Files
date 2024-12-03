/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 *  com.mchange.v2.sql.SqlUtils
 *  com.mchange.v2.util.ResourceClosedException
 */
package com.mchange.v2.c3p0.impl;

import com.mchange.v2.c3p0.C3P0ProxyConnection;
import com.mchange.v2.c3p0.impl.NewPooledConnection;
import com.mchange.v2.c3p0.impl.NewProxyCallableStatement;
import com.mchange.v2.c3p0.impl.NewProxyDatabaseMetaData;
import com.mchange.v2.c3p0.impl.NewProxyPreparedStatement;
import com.mchange.v2.c3p0.impl.NewProxyResultSet;
import com.mchange.v2.c3p0.impl.NewProxyStatement;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.sql.SqlUtils;
import com.mchange.v2.util.ResourceClosedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;

public final class NewProxyConnection
implements Connection,
C3P0ProxyConnection {
    protected Connection inner;
    boolean txn_known_resolved = true;
    DatabaseMetaData metaData = null;
    private static final MLogger logger = MLog.getLogger((String)"com.mchange.v2.c3p0.impl.NewProxyConnection");
    volatile NewPooledConnection parentPooledConnection;
    ConnectionEventListener cel = new ConnectionEventListener(){

        @Override
        public void connectionErrorOccurred(ConnectionEvent evt) {
        }

        @Override
        public void connectionClosed(ConnectionEvent evt) {
            NewProxyConnection.this.detach();
        }
    };

    private void __setInner(Connection inner) {
        this.inner = inner;
    }

    NewProxyConnection(Connection inner) {
        this.__setInner(inner);
    }

    @Override
    public synchronized void setReadOnly(boolean a) throws SQLException {
        try {
            this.inner.setReadOnly(a);
            this.parentPooledConnection.markNewReadOnly(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized void close() throws SQLException {
        try {
            if (!this.isDetached()) {
                NewPooledConnection npc = this.parentPooledConnection;
                this.detach();
                npc.markClosedProxyConnection(this, this.txn_known_resolved);
                this.inner = null;
            } else if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, this + ": close() called after already close()ed or abort()ed.");
            }
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                if (logger.isLoggable(MLevel.FINE)) {
                    logger.log(MLevel.FINE, this + ": close() called more than once.");
                }
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized boolean isReadOnly() throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.isReadOnly();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized Statement createStatement(int a, int b) throws SQLException {
        try {
            this.txn_known_resolved = false;
            Statement innerStmt = this.inner.createStatement(a, b);
            this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
            return new NewProxyStatement(innerStmt, this.parentPooledConnection, false, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized Statement createStatement() throws SQLException {
        try {
            this.txn_known_resolved = false;
            Statement innerStmt = this.inner.createStatement();
            this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
            return new NewProxyStatement(innerStmt, this.parentPooledConnection, false, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized Statement createStatement(int a, int b, int c) throws SQLException {
        try {
            this.txn_known_resolved = false;
            Statement innerStmt = this.inner.createStatement(a, b, c);
            this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
            return new NewProxyStatement(innerStmt, this.parentPooledConnection, false, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized PreparedStatement prepareStatement(String a, int b) throws SQLException {
        try {
            this.txn_known_resolved = false;
            if (this.parentPooledConnection.isStatementCaching()) {
                try {
                    Class[] argTypes = new Class[]{String.class, Integer.TYPE};
                    Method method = Connection.class.getMethod("prepareStatement", argTypes);
                    Object[] args = new Object[]{a, new Integer(b)};
                    PreparedStatement innerStmt = (PreparedStatement)this.parentPooledConnection.checkoutStatement(method, args);
                    return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, true, this);
                }
                catch (ResourceClosedException e) {
                    if (logger.isLoggable(MLevel.FINE)) {
                        logger.log(MLevel.FINE, "A Connection tried to prepare a Statement via a Statement cache that is already closed. This can happen -- rarely -- if a DataSource is closed or reset() while Connections are checked-out and in use.", (Throwable)e);
                    }
                    PreparedStatement innerStmt = this.inner.prepareStatement(a, b);
                    this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
                    return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
                }
            }
            PreparedStatement innerStmt = this.inner.prepareStatement(a, b);
            this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
            return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized PreparedStatement prepareStatement(String a, int[] b) throws SQLException {
        try {
            this.txn_known_resolved = false;
            if (this.parentPooledConnection.isStatementCaching()) {
                try {
                    Class[] argTypes = new Class[]{String.class, int[].class};
                    Method method = Connection.class.getMethod("prepareStatement", argTypes);
                    Object[] args = new Object[]{a, b};
                    PreparedStatement innerStmt = (PreparedStatement)this.parentPooledConnection.checkoutStatement(method, args);
                    return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, true, this);
                }
                catch (ResourceClosedException e) {
                    if (logger.isLoggable(MLevel.FINE)) {
                        logger.log(MLevel.FINE, "A Connection tried to prepare a Statement via a Statement cache that is already closed. This can happen -- rarely -- if a DataSource is closed or reset() while Connections are checked-out and in use.", (Throwable)e);
                    }
                    PreparedStatement innerStmt = this.inner.prepareStatement(a, b);
                    this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
                    return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
                }
            }
            PreparedStatement innerStmt = this.inner.prepareStatement(a, b);
            this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
            return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized PreparedStatement prepareStatement(String a, int b, int c) throws SQLException {
        try {
            this.txn_known_resolved = false;
            if (this.parentPooledConnection.isStatementCaching()) {
                try {
                    Class[] argTypes = new Class[]{String.class, Integer.TYPE, Integer.TYPE};
                    Method method = Connection.class.getMethod("prepareStatement", argTypes);
                    Object[] args = new Object[]{a, new Integer(b), new Integer(c)};
                    PreparedStatement innerStmt = (PreparedStatement)this.parentPooledConnection.checkoutStatement(method, args);
                    return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, true, this);
                }
                catch (ResourceClosedException e) {
                    if (logger.isLoggable(MLevel.FINE)) {
                        logger.log(MLevel.FINE, "A Connection tried to prepare a Statement via a Statement cache that is already closed. This can happen -- rarely -- if a DataSource is closed or reset() while Connections are checked-out and in use.", (Throwable)e);
                    }
                    PreparedStatement innerStmt = this.inner.prepareStatement(a, b, c);
                    this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
                    return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
                }
            }
            PreparedStatement innerStmt = this.inner.prepareStatement(a, b, c);
            this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
            return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized PreparedStatement prepareStatement(String a, String[] b) throws SQLException {
        try {
            this.txn_known_resolved = false;
            if (this.parentPooledConnection.isStatementCaching()) {
                try {
                    Class[] argTypes = new Class[]{String.class, String[].class};
                    Method method = Connection.class.getMethod("prepareStatement", argTypes);
                    Object[] args = new Object[]{a, b};
                    PreparedStatement innerStmt = (PreparedStatement)this.parentPooledConnection.checkoutStatement(method, args);
                    return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, true, this);
                }
                catch (ResourceClosedException e) {
                    if (logger.isLoggable(MLevel.FINE)) {
                        logger.log(MLevel.FINE, "A Connection tried to prepare a Statement via a Statement cache that is already closed. This can happen -- rarely -- if a DataSource is closed or reset() while Connections are checked-out and in use.", (Throwable)e);
                    }
                    PreparedStatement innerStmt = this.inner.prepareStatement(a, b);
                    this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
                    return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
                }
            }
            PreparedStatement innerStmt = this.inner.prepareStatement(a, b);
            this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
            return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized PreparedStatement prepareStatement(String a, int b, int c, int d) throws SQLException {
        try {
            this.txn_known_resolved = false;
            if (this.parentPooledConnection.isStatementCaching()) {
                try {
                    Class[] argTypes = new Class[]{String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE};
                    Method method = Connection.class.getMethod("prepareStatement", argTypes);
                    Object[] args = new Object[]{a, new Integer(b), new Integer(c), new Integer(d)};
                    PreparedStatement innerStmt = (PreparedStatement)this.parentPooledConnection.checkoutStatement(method, args);
                    return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, true, this);
                }
                catch (ResourceClosedException e) {
                    if (logger.isLoggable(MLevel.FINE)) {
                        logger.log(MLevel.FINE, "A Connection tried to prepare a Statement via a Statement cache that is already closed. This can happen -- rarely -- if a DataSource is closed or reset() while Connections are checked-out and in use.", (Throwable)e);
                    }
                    PreparedStatement innerStmt = this.inner.prepareStatement(a, b, c, d);
                    this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
                    return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
                }
            }
            PreparedStatement innerStmt = this.inner.prepareStatement(a, b, c, d);
            this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
            return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized PreparedStatement prepareStatement(String a) throws SQLException {
        try {
            this.txn_known_resolved = false;
            if (this.parentPooledConnection.isStatementCaching()) {
                try {
                    Class[] argTypes = new Class[]{String.class};
                    Method method = Connection.class.getMethod("prepareStatement", argTypes);
                    Object[] args = new Object[]{a};
                    PreparedStatement innerStmt = (PreparedStatement)this.parentPooledConnection.checkoutStatement(method, args);
                    return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, true, this);
                }
                catch (ResourceClosedException e) {
                    if (logger.isLoggable(MLevel.FINE)) {
                        logger.log(MLevel.FINE, "A Connection tried to prepare a Statement via a Statement cache that is already closed. This can happen -- rarely -- if a DataSource is closed or reset() while Connections are checked-out and in use.", (Throwable)e);
                    }
                    PreparedStatement innerStmt = this.inner.prepareStatement(a);
                    this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
                    return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
                }
            }
            PreparedStatement innerStmt = this.inner.prepareStatement(a);
            this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
            return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized CallableStatement prepareCall(String a) throws SQLException {
        try {
            this.txn_known_resolved = false;
            if (this.parentPooledConnection.isStatementCaching()) {
                try {
                    Class[] argTypes = new Class[]{String.class};
                    Method method = Connection.class.getMethod("prepareCall", argTypes);
                    Object[] args = new Object[]{a};
                    CallableStatement innerStmt = (CallableStatement)this.parentPooledConnection.checkoutStatement(method, args);
                    return new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, true, this);
                }
                catch (ResourceClosedException e) {
                    if (logger.isLoggable(MLevel.FINE)) {
                        logger.log(MLevel.FINE, "A Connection tried to prepare a CallableStatement via a Statement cache that is already closed. This can happen -- rarely -- if a DataSource is closed or reset() while Connections are checked-out and in use.", (Throwable)e);
                    }
                    CallableStatement innerStmt = this.inner.prepareCall(a);
                    this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
                    return new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, false, this);
                }
            }
            CallableStatement innerStmt = this.inner.prepareCall(a);
            this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
            return new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, false, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized CallableStatement prepareCall(String a, int b, int c, int d) throws SQLException {
        try {
            this.txn_known_resolved = false;
            if (this.parentPooledConnection.isStatementCaching()) {
                try {
                    Class[] argTypes = new Class[]{String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE};
                    Method method = Connection.class.getMethod("prepareCall", argTypes);
                    Object[] args = new Object[]{a, new Integer(b), new Integer(c), new Integer(d)};
                    CallableStatement innerStmt = (CallableStatement)this.parentPooledConnection.checkoutStatement(method, args);
                    return new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, true, this);
                }
                catch (ResourceClosedException e) {
                    if (logger.isLoggable(MLevel.FINE)) {
                        logger.log(MLevel.FINE, "A Connection tried to prepare a CallableStatement via a Statement cache that is already closed. This can happen -- rarely -- if a DataSource is closed or reset() while Connections are checked-out and in use.", (Throwable)e);
                    }
                    CallableStatement innerStmt = this.inner.prepareCall(a, b, c, d);
                    this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
                    return new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, false, this);
                }
            }
            CallableStatement innerStmt = this.inner.prepareCall(a, b, c, d);
            this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
            return new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, false, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized CallableStatement prepareCall(String a, int b, int c) throws SQLException {
        try {
            this.txn_known_resolved = false;
            if (this.parentPooledConnection.isStatementCaching()) {
                try {
                    Class[] argTypes = new Class[]{String.class, Integer.TYPE, Integer.TYPE};
                    Method method = Connection.class.getMethod("prepareCall", argTypes);
                    Object[] args = new Object[]{a, new Integer(b), new Integer(c)};
                    CallableStatement innerStmt = (CallableStatement)this.parentPooledConnection.checkoutStatement(method, args);
                    return new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, true, this);
                }
                catch (ResourceClosedException e) {
                    if (logger.isLoggable(MLevel.FINE)) {
                        logger.log(MLevel.FINE, "A Connection tried to prepare a CallableStatement via a Statement cache that is already closed. This can happen -- rarely -- if a DataSource is closed or reset() while Connections are checked-out and in use.", (Throwable)e);
                    }
                    CallableStatement innerStmt = this.inner.prepareCall(a, b, c);
                    this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
                    return new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, false, this);
                }
            }
            CallableStatement innerStmt = this.inner.prepareCall(a, b, c);
            this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
            return new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, false, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized DatabaseMetaData getMetaData() throws SQLException {
        try {
            this.txn_known_resolved = false;
            if (this.metaData == null) {
                DatabaseMetaData innerMetaData = this.inner.getMetaData();
                this.metaData = new NewProxyDatabaseMetaData(innerMetaData, this.parentPooledConnection, this);
            }
            return this.metaData;
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized void setTransactionIsolation(int a) throws SQLException {
        try {
            this.inner.setTransactionIsolation(a);
            this.parentPooledConnection.markNewTxnIsolation(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized void setCatalog(String a) throws SQLException {
        try {
            this.inner.setCatalog(a);
            this.parentPooledConnection.markNewCatalog(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized void setHoldability(int a) throws SQLException {
        try {
            this.inner.setHoldability(a);
            this.parentPooledConnection.markNewHoldability(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    public synchronized void setTypeMap(Map a) throws SQLException {
        try {
            this.inner.setTypeMap(a);
            this.parentPooledConnection.markNewTypeMap(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized SQLWarning getWarnings() throws SQLException {
        try {
            return this.inner.getWarnings();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized void clearWarnings() throws SQLException {
        try {
            this.inner.clearWarnings();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized boolean isClosed() throws SQLException {
        try {
            return this.isDetached();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized void commit() throws SQLException {
        try {
            this.inner.commit();
            this.txn_known_resolved = true;
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized void rollback(Savepoint a) throws SQLException {
        try {
            this.inner.rollback(a);
            this.txn_known_resolved = true;
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized void rollback() throws SQLException {
        try {
            this.inner.rollback();
            this.txn_known_resolved = true;
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized void setAutoCommit(boolean a) throws SQLException {
        try {
            this.inner.setAutoCommit(a);
            this.txn_known_resolved = true;
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized void setClientInfo(String a, String b) throws SQLClientInfoException {
        try {
            try {
                this.txn_known_resolved = false;
                this.inner.setClientInfo(a, b);
            }
            catch (NullPointerException exc) {
                if (this.isDetached()) {
                    throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
                }
                throw exc;
            }
            catch (Exception exc) {
                if (!this.isDetached()) {
                    throw this.parentPooledConnection.handleThrowable(exc);
                }
                throw SqlUtils.toSQLException((Throwable)exc);
            }
        }
        catch (Exception e) {
            throw SqlUtils.toSQLClientInfoException((Throwable)e);
        }
    }

    @Override
    public synchronized void setClientInfo(Properties a) throws SQLClientInfoException {
        try {
            try {
                this.txn_known_resolved = false;
                this.inner.setClientInfo(a);
            }
            catch (NullPointerException exc) {
                if (this.isDetached()) {
                    throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
                }
                throw exc;
            }
            catch (Exception exc) {
                if (!this.isDetached()) {
                    throw this.parentPooledConnection.handleThrowable(exc);
                }
                throw SqlUtils.toSQLException((Throwable)exc);
            }
        }
        catch (Exception e) {
            throw SqlUtils.toSQLClientInfoException((Throwable)e);
        }
    }

    @Override
    public synchronized void abort(Executor a) throws SQLException {
        try {
            if (!this.isDetached()) {
                final NewPooledConnection npc = this.parentPooledConnection;
                Executor exec = a;
                this.detach();
                this.inner = null;
                Runnable r = new Runnable(){

                    @Override
                    public void run() {
                        block2: {
                            try {
                                npc.closeMaybeCheckedOut(true);
                            }
                            catch (SQLException e) {
                                if (!logger.isLoggable(MLevel.WARNING)) break block2;
                                logger.log(MLevel.WARNING, "An Exception occurred while attempting to destroy NewPooledConnection and underlying physical Connection on abort().", (Throwable)e);
                            }
                        }
                    }
                };
                exec.execute(r);
            } else if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, this + ": abort() after already close()ed or abort()ed.");
            }
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized String nativeSQL(String a) throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.nativeSQL(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized boolean getAutoCommit() throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.getAutoCommit();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized String getCatalog() throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.getCatalog();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized int getTransactionIsolation() throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.getTransactionIsolation();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    public synchronized Map getTypeMap() throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.getTypeMap();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized int getHoldability() throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.getHoldability();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized Savepoint setSavepoint() throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.setSavepoint();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized Savepoint setSavepoint(String a) throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.setSavepoint(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized void releaseSavepoint(Savepoint a) throws SQLException {
        try {
            this.txn_known_resolved = false;
            this.inner.releaseSavepoint(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized Clob createClob() throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.createClob();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized Blob createBlob() throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.createBlob();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized NClob createNClob() throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.createNClob();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized SQLXML createSQLXML() throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.createSQLXML();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized Properties getClientInfo() throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.getClientInfo();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized String getClientInfo(String a) throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.getClientInfo(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized Array createArrayOf(String a, Object[] b) throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.createArrayOf(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized Struct createStruct(String a, Object[] b) throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.createStruct(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized void setSchema(String a) throws SQLException {
        try {
            this.txn_known_resolved = false;
            this.inner.setSchema(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized String getSchema() throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.getSchema();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized void setNetworkTimeout(Executor a, int b) throws SQLException {
        try {
            this.txn_known_resolved = false;
            this.inner.setNetworkTimeout(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized int getNetworkTimeout() throws SQLException {
        try {
            this.txn_known_resolved = false;
            return this.inner.getNetworkTimeout();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public synchronized boolean isValid(int a) throws SQLException {
        try {
            if (this.isDetached()) {
                return false;
            }
            return this.inner.isValid(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Connection!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    public synchronized Object unwrap(Class a) throws SQLException {
        if (this.isWrapperForInner(a)) {
            return this.inner.unwrap(a);
        }
        if (this.isWrapperForThis(a)) {
            return this;
        }
        throw new SQLException(this + " is not a wrapper for or implementation of " + a.getName());
    }

    public synchronized boolean isWrapperFor(Class a) throws SQLException {
        return this.isWrapperForInner(a) || this.isWrapperForThis(a);
    }

    @Override
    public Object rawConnectionOperation(Method m, Object target, Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
        this.maybeDirtyTransaction();
        if (this.inner == null) {
            throw new SQLException("You cannot operate on a closed Connection!");
        }
        if (target == C3P0ProxyConnection.RAW_CONNECTION) {
            target = this.inner;
        }
        int len = args.length;
        for (int i = 0; i < len; ++i) {
            if (args[i] != C3P0ProxyConnection.RAW_CONNECTION) continue;
            args[i] = this.inner;
        }
        Object out = m.invoke(target, args);
        if (out instanceof CallableStatement) {
            CallableStatement innerStmt = (CallableStatement)out;
            this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
            out = new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, false, this);
        } else if (out instanceof PreparedStatement) {
            PreparedStatement innerStmt = (PreparedStatement)out;
            this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
            out = new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
        } else if (out instanceof Statement) {
            Statement innerStmt = (Statement)out;
            this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
            out = new NewProxyStatement(innerStmt, this.parentPooledConnection, false, this);
        } else if (out instanceof ResultSet) {
            ResultSet innerRs = (ResultSet)out;
            this.parentPooledConnection.markActiveRawConnectionResultSet(innerRs);
            out = new NewProxyResultSet(innerRs, this.parentPooledConnection, this.inner, this);
        } else if (out instanceof DatabaseMetaData) {
            out = new NewProxyDatabaseMetaData((DatabaseMetaData)out, this.parentPooledConnection);
        }
        return out;
    }

    synchronized void maybeDirtyTransaction() {
        this.txn_known_resolved = false;
    }

    void attach(NewPooledConnection parentPooledConnection) {
        this.parentPooledConnection = parentPooledConnection;
        parentPooledConnection.addConnectionEventListener(this.cel);
    }

    private void detach() {
        this.parentPooledConnection.removeConnectionEventListener(this.cel);
        this.parentPooledConnection = null;
    }

    NewProxyConnection(Connection inner, NewPooledConnection parentPooledConnection) {
        this(inner);
        this.attach(parentPooledConnection);
    }

    boolean isDetached() {
        return this.parentPooledConnection == null;
    }

    public String toString() {
        return super.toString() + " [wrapping: " + this.inner + "]";
    }

    private boolean isWrapperForInner(Class intfcl) {
        return Connection.class == intfcl || intfcl.isAssignableFrom(this.inner.getClass());
    }

    private boolean isWrapperForThis(Class intfcl) {
        return intfcl.isAssignableFrom(this.getClass());
    }
}

