/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 *  com.mchange.v2.sql.SqlUtils
 */
package com.mchange.v2.c3p0.impl;

import com.mchange.v2.c3p0.C3P0ProxyStatement;
import com.mchange.v2.c3p0.impl.NewPooledConnection;
import com.mchange.v2.c3p0.impl.NewProxyConnection;
import com.mchange.v2.c3p0.impl.NewProxyResultSet;
import com.mchange.v2.c3p0.impl.ProxyResultSetDetachable;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.sql.SqlUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;

public final class NewProxyStatement
implements Statement,
C3P0ProxyStatement,
ProxyResultSetDetachable {
    protected Statement inner;
    private static final MLogger logger = MLog.getLogger((String)"com.mchange.v2.c3p0.impl.NewProxyStatement");
    volatile NewPooledConnection parentPooledConnection;
    ConnectionEventListener cel = new ConnectionEventListener(){

        @Override
        public void connectionErrorOccurred(ConnectionEvent evt) {
        }

        @Override
        public void connectionClosed(ConnectionEvent evt) {
            NewProxyStatement.this.detach();
        }
    };
    boolean is_cached;
    NewProxyConnection creatorProxy;
    HashSet myProxyResultSets = new HashSet();

    private void __setInner(Statement inner) {
        this.inner = inner;
    }

    NewProxyStatement(Statement inner) {
        this.__setInner(inner);
    }

    @Override
    public final boolean execute(String a, String[] b) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.execute(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final boolean execute(String a) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.execute(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final boolean execute(String a, int b) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.execute(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final boolean execute(String a, int[] b) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.execute(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void close() throws SQLException {
        block17: {
            try {
                this.maybeDirtyTransaction();
                if (this.isDetached()) break block17;
                HashSet hashSet = this.myProxyResultSets;
                synchronized (hashSet) {
                    Iterator ii = this.myProxyResultSets.iterator();
                    while (ii.hasNext()) {
                        ResultSet closeMe;
                        block18: {
                            closeMe = (ResultSet)ii.next();
                            ii.remove();
                            try {
                                closeMe.close();
                            }
                            catch (SQLException e) {
                                if (!logger.isLoggable(MLevel.WARNING)) break block18;
                                logger.log(MLevel.WARNING, "Exception on close of apparently orphaned ResultSet.", (Throwable)e);
                            }
                        }
                        if (!logger.isLoggable(MLevel.FINE)) continue;
                        logger.log(MLevel.FINE, this + " closed orphaned ResultSet: " + closeMe);
                    }
                }
                if (this.is_cached) {
                    this.parentPooledConnection.checkinStatement(this.inner);
                } else {
                    this.parentPooledConnection.markInactiveUncachedStatement(this.inner);
                    try {
                        this.inner.close();
                    }
                    catch (Exception e) {
                        if (logger.isLoggable(MLevel.WARNING)) {
                            logger.log(MLevel.WARNING, "Exception on close of inner statement.", (Throwable)e);
                        }
                        SQLException sqle = SqlUtils.toSQLException((Throwable)e);
                        throw sqle;
                    }
                }
                this.detach();
                this.inner = null;
                this.creatorProxy = null;
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
    }

    @Override
    public final SQLWarning getWarnings() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.getWarnings();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final void clearWarnings() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            this.inner.clearWarnings();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final boolean isClosed() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.isDetached();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final Connection getConnection() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            if (!this.isDetached()) {
                return this.creatorProxy;
            }
            throw new SQLException("You cannot operate on a closed Statement!");
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final ResultSet executeQuery(String a) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            ResultSet innerResultSet = this.inner.executeQuery(a);
            if (innerResultSet == null) {
                return null;
            }
            this.parentPooledConnection.markActiveResultSetForStatement(this.inner, innerResultSet);
            NewProxyResultSet out = new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
            HashSet hashSet = this.myProxyResultSets;
            synchronized (hashSet) {
                this.myProxyResultSets.add(out);
            }
            return out;
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final int executeUpdate(String a, int[] b) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.executeUpdate(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final int executeUpdate(String a, int b) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.executeUpdate(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final int executeUpdate(String a) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.executeUpdate(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final int executeUpdate(String a, String[] b) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.executeUpdate(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final int getMaxFieldSize() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.getMaxFieldSize();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final void setMaxFieldSize(int a) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            this.inner.setMaxFieldSize(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final int getMaxRows() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.getMaxRows();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final void setMaxRows(int a) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            this.inner.setMaxRows(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final void setEscapeProcessing(boolean a) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            this.inner.setEscapeProcessing(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final int getQueryTimeout() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.getQueryTimeout();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final void setQueryTimeout(int a) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            this.inner.setQueryTimeout(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final void cancel() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            this.inner.cancel();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final void setCursorName(String a) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            this.inner.setCursorName(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final ResultSet getResultSet() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            ResultSet innerResultSet = this.inner.getResultSet();
            if (innerResultSet == null) {
                return null;
            }
            this.parentPooledConnection.markActiveResultSetForStatement(this.inner, innerResultSet);
            NewProxyResultSet out = new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
            HashSet hashSet = this.myProxyResultSets;
            synchronized (hashSet) {
                this.myProxyResultSets.add(out);
            }
            return out;
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final int getUpdateCount() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.getUpdateCount();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final boolean getMoreResults(int a) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.getMoreResults(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final boolean getMoreResults() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.getMoreResults();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final void setFetchDirection(int a) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            this.inner.setFetchDirection(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final int getFetchDirection() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.getFetchDirection();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final void setFetchSize(int a) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            this.inner.setFetchSize(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final int getFetchSize() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.getFetchSize();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final int getResultSetConcurrency() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.getResultSetConcurrency();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final int getResultSetType() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.getResultSetType();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final void addBatch(String a) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            this.inner.addBatch(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final void clearBatch() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            this.inner.clearBatch();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final int[] executeBatch() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.executeBatch();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final ResultSet getGeneratedKeys() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            ResultSet innerResultSet = this.inner.getGeneratedKeys();
            if (innerResultSet == null) {
                return null;
            }
            this.parentPooledConnection.markActiveResultSetForStatement(this.inner, innerResultSet);
            NewProxyResultSet out = new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
            HashSet hashSet = this.myProxyResultSets;
            synchronized (hashSet) {
                this.myProxyResultSets.add(out);
            }
            return out;
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final int getResultSetHoldability() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.getResultSetHoldability();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final void setPoolable(boolean a) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            this.inner.setPoolable(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final boolean isPoolable() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.isPoolable();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final void closeOnCompletion() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            this.inner.closeOnCompletion();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final boolean isCloseOnCompletion() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.isCloseOnCompletion();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final long getLargeUpdateCount() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.getLargeUpdateCount();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final void setLargeMaxRows(long a) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            this.inner.setLargeMaxRows(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final long getLargeMaxRows() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.getLargeMaxRows();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final long[] executeLargeBatch() throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.executeLargeBatch();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final long executeLargeUpdate(String a, String[] b) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.executeLargeUpdate(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final long executeLargeUpdate(String a, int[] b) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.executeLargeUpdate(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final long executeLargeUpdate(String a, int b) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.executeLargeUpdate(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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
    public final long executeLargeUpdate(String a) throws SQLException {
        try {
            this.maybeDirtyTransaction();
            return this.inner.executeLargeUpdate(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed Statement!!!", (Throwable)exc);
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

    public final Object unwrap(Class a) throws SQLException {
        if (this.isWrapperForInner(a)) {
            return this.inner.unwrap(a);
        }
        if (this.isWrapperForThis(a)) {
            return this;
        }
        throw new SQLException(this + " is not a wrapper for or implementation of " + a.getName());
    }

    public final boolean isWrapperFor(Class a) throws SQLException {
        return this.isWrapperForInner(a) || this.isWrapperForThis(a);
    }

    void attach(NewPooledConnection parentPooledConnection) {
        this.parentPooledConnection = parentPooledConnection;
        parentPooledConnection.addConnectionEventListener(this.cel);
    }

    private void detach() {
        this.parentPooledConnection.removeConnectionEventListener(this.cel);
        this.parentPooledConnection = null;
    }

    NewProxyStatement(Statement inner, NewPooledConnection parentPooledConnection) {
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
        return Statement.class == intfcl || intfcl.isAssignableFrom(this.inner.getClass());
    }

    private boolean isWrapperForThis(Class intfcl) {
        return intfcl.isAssignableFrom(this.getClass());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void detachProxyResultSet(ResultSet prs) {
        HashSet hashSet = this.myProxyResultSets;
        synchronized (hashSet) {
            this.myProxyResultSets.remove(prs);
        }
    }

    NewProxyStatement(Statement inner, NewPooledConnection parentPooledConnection, boolean cached, NewProxyConnection cProxy) {
        this(inner, parentPooledConnection);
        this.is_cached = cached;
        this.creatorProxy = cProxy;
    }

    @Override
    public Object rawStatementOperation(Method m, Object target, Object[] args) throws IllegalAccessException, InvocationTargetException, SQLException {
        this.maybeDirtyTransaction();
        if (target == C3P0ProxyStatement.RAW_STATEMENT) {
            target = this.inner;
        }
        int len = args.length;
        for (int i = 0; i < len; ++i) {
            if (args[i] != C3P0ProxyStatement.RAW_STATEMENT) continue;
            args[i] = this.inner;
        }
        Object out = m.invoke(target, args);
        if (out instanceof ResultSet) {
            ResultSet innerResultSet = (ResultSet)out;
            this.parentPooledConnection.markActiveResultSetForStatement(this.inner, innerResultSet);
            out = new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        return out;
    }

    void maybeDirtyTransaction() {
        if (this.creatorProxy != null) {
            this.creatorProxy.maybeDirtyTransaction();
        }
    }
}

