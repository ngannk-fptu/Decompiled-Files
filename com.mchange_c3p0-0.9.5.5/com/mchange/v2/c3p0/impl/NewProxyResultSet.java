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

import com.mchange.v2.c3p0.impl.NewPooledConnection;
import com.mchange.v2.c3p0.impl.NewProxyConnection;
import com.mchange.v2.c3p0.impl.ProxyResultSetDetachable;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.sql.SqlUtils;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;

public final class NewProxyResultSet
implements ResultSet {
    protected ResultSet inner;
    private static final MLogger logger = MLog.getLogger((String)"com.mchange.v2.c3p0.impl.NewProxyResultSet");
    volatile NewPooledConnection parentPooledConnection;
    ConnectionEventListener cel = new ConnectionEventListener(){

        @Override
        public void connectionErrorOccurred(ConnectionEvent evt) {
        }

        @Override
        public void connectionClosed(ConnectionEvent evt) {
            NewProxyResultSet.this.detach();
        }
    };
    Object creator;
    Object creatorProxy;
    NewProxyConnection proxyConn;

    private void __setInner(ResultSet inner) {
        this.inner = inner;
    }

    NewProxyResultSet(ResultSet inner) {
        this.__setInner(inner);
    }

    @Override
    public final void updateBytes(String a, byte[] b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateBytes(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateBytes(int a, byte[] b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateBytes(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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

    public final Object getObject(int a, Map b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getObject(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Object getObject(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getObject(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Object getObject(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getObject(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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

    public final Object getObject(String a, Map b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getObject(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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

    public final Object getObject(int a, Class b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getObject(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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

    public final Object getObject(String a, Class b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getObject(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final boolean getBoolean(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getBoolean(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final boolean getBoolean(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getBoolean(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final byte getByte(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getByte(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final byte getByte(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getByte(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final short getShort(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getShort(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final short getShort(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getShort(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final int getInt(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getInt(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final int getInt(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getInt(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final long getLong(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getLong(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final long getLong(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getLong(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final float getFloat(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getFloat(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final float getFloat(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getFloat(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final double getDouble(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getDouble(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final double getDouble(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getDouble(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final byte[] getBytes(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getBytes(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final byte[] getBytes(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getBytes(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final boolean next() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.next();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Array getArray(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getArray(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Array getArray(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getArray(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final URL getURL(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getURL(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final URL getURL(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getURL(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final boolean first() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.first();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void close() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            if (!this.isDetached()) {
                if (this.creator instanceof Statement) {
                    this.parentPooledConnection.markInactiveResultSetForStatement((Statement)this.creator, this.inner);
                } else if (this.creator instanceof DatabaseMetaData) {
                    this.parentPooledConnection.markInactiveMetaDataResultSet(this.inner);
                } else if (this.creator instanceof Connection) {
                    this.parentPooledConnection.markInactiveRawConnectionResultSet(this.inner);
                } else {
                    throw new InternalError("Must be Statement or DatabaseMetaData -- Bad Creator: " + this.creator);
                }
                if (this.creatorProxy instanceof ProxyResultSetDetachable) {
                    ((ProxyResultSetDetachable)this.creatorProxy).detachProxyResultSet(this);
                }
                this.detach();
                this.inner.close();
                this.inner = null;
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
    public final int getType() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getType();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Ref getRef(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getRef(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Ref getRef(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getRef(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final boolean previous() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.previous();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final ResultSetMetaData getMetaData() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getMetaData();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final SQLWarning getWarnings() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getWarnings();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.clearWarnings();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.isDetached();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Statement getStatement() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            if (this.creator instanceof Statement) {
                return (Statement)this.creatorProxy;
            }
            if (this.creator instanceof DatabaseMetaData) {
                return null;
            }
            throw new InternalError("Must be Statement or DatabaseMetaData -- Bad Creator: " + this.creator);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateAsciiStream(String a, InputStream b, int c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateAsciiStream(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateAsciiStream(int a, InputStream b, int c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateAsciiStream(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateAsciiStream(int a, InputStream b, long c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateAsciiStream(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateAsciiStream(String a, InputStream b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateAsciiStream(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateAsciiStream(String a, InputStream b, long c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateAsciiStream(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateAsciiStream(int a, InputStream b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateAsciiStream(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateBinaryStream(int a, InputStream b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateBinaryStream(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateBinaryStream(String a, InputStream b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateBinaryStream(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateBinaryStream(int a, InputStream b, int c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateBinaryStream(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateBinaryStream(String a, InputStream b, long c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateBinaryStream(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateBinaryStream(String a, InputStream b, int c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateBinaryStream(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateBinaryStream(int a, InputStream b, long c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateBinaryStream(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateCharacterStream(int a, Reader b, int c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateCharacterStream(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateCharacterStream(String a, Reader b, int c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateCharacterStream(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateCharacterStream(String a, Reader b, long c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateCharacterStream(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateCharacterStream(int a, Reader b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateCharacterStream(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateCharacterStream(String a, Reader b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateCharacterStream(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateCharacterStream(int a, Reader b, long c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateCharacterStream(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateObject(int a, Object b, SQLType c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateObject(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateObject(String a, Object b, SQLType c, int d) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateObject(a, b, c, d);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateObject(int a, Object b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateObject(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateObject(int a, Object b, int c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateObject(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateObject(String a, Object b, SQLType c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateObject(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateObject(int a, Object b, SQLType c, int d) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateObject(a, b, c, d);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateObject(String a, Object b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateObject(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateObject(String a, Object b, int c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateObject(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void insertRow() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.insertRow();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateRow() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateRow();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void deleteRow() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.deleteRow();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void refreshRow() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.refreshRow();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void cancelRowUpdates() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.cancelRowUpdates();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void moveToInsertRow() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.moveToInsertRow();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void moveToCurrentRow() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.moveToCurrentRow();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateRef(String a, Ref b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateRef(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateRef(int a, Ref b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateRef(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateBlob(String a, InputStream b, long c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateBlob(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateBlob(String a, Blob b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateBlob(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateBlob(int a, Blob b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateBlob(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateBlob(int a, InputStream b, long c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateBlob(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateBlob(int a, InputStream b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateBlob(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateBlob(String a, InputStream b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateBlob(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateClob(String a, Reader b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateClob(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateClob(int a, Reader b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateClob(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateClob(int a, Clob b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateClob(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateClob(String a, Clob b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateClob(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateClob(int a, Reader b, long c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateClob(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateClob(String a, Reader b, long c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateClob(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateArray(int a, Array b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateArray(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateArray(String a, Array b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateArray(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateRowId(int a, RowId b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateRowId(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateRowId(String a, RowId b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateRowId(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateNString(int a, String b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateNString(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateNString(String a, String b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateNString(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateNClob(int a, Reader b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateNClob(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateNClob(String a, Reader b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateNClob(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateNClob(String a, Reader b, long c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateNClob(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateNClob(int a, Reader b, long c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateNClob(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateNClob(int a, NClob b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateNClob(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateNClob(String a, NClob b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateNClob(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateSQLXML(int a, SQLXML b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateSQLXML(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateSQLXML(String a, SQLXML b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateSQLXML(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateNCharacterStream(int a, Reader b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateNCharacterStream(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateNCharacterStream(String a, Reader b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateNCharacterStream(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateNCharacterStream(String a, Reader b, long c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateNCharacterStream(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateNCharacterStream(int a, Reader b, long c) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateNCharacterStream(a, b, c);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final String getString(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getString(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final String getString(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getString(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final int getHoldability() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getHoldability();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final boolean wasNull() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.wasNull();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final BigDecimal getBigDecimal(int a, int b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getBigDecimal(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final BigDecimal getBigDecimal(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getBigDecimal(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final BigDecimal getBigDecimal(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getBigDecimal(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final BigDecimal getBigDecimal(String a, int b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getBigDecimal(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Timestamp getTimestamp(int a, Calendar b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getTimestamp(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Timestamp getTimestamp(String a, Calendar b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getTimestamp(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Timestamp getTimestamp(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getTimestamp(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Timestamp getTimestamp(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getTimestamp(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Blob getBlob(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getBlob(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Blob getBlob(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getBlob(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Clob getClob(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getClob(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Clob getClob(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getClob(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final RowId getRowId(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getRowId(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final RowId getRowId(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getRowId(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final NClob getNClob(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getNClob(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final NClob getNClob(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getNClob(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final SQLXML getSQLXML(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getSQLXML(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final SQLXML getSQLXML(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getSQLXML(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final String getNString(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getNString(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final String getNString(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getNString(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Reader getNCharacterStream(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getNCharacterStream(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Reader getNCharacterStream(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getNCharacterStream(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Reader getCharacterStream(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getCharacterStream(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Reader getCharacterStream(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getCharacterStream(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final InputStream getAsciiStream(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getAsciiStream(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final InputStream getAsciiStream(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getAsciiStream(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final InputStream getBinaryStream(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getBinaryStream(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final InputStream getBinaryStream(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getBinaryStream(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Time getTime(int a, Calendar b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getTime(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Time getTime(String a, Calendar b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getTime(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Time getTime(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getTime(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Time getTime(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getTime(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final boolean last() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.last();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Date getDate(String a, Calendar b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getDate(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Date getDate(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getDate(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Date getDate(int a, Calendar b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getDate(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final Date getDate(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getDate(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.setFetchDirection(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getFetchDirection();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.setFetchSize(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getFetchSize();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final InputStream getUnicodeStream(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getUnicodeStream(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final InputStream getUnicodeStream(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getUnicodeStream(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final String getCursorName() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getCursorName();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final int findColumn(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.findColumn(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final boolean isBeforeFirst() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.isBeforeFirst();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final boolean isAfterLast() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.isAfterLast();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final boolean isFirst() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.isFirst();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final boolean isLast() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.isLast();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void beforeFirst() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.beforeFirst();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void afterLast() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.afterLast();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final int getRow() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getRow();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final boolean absolute(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.absolute(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final boolean relative(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.relative(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final int getConcurrency() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.getConcurrency();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final boolean rowUpdated() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.rowUpdated();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final boolean rowInserted() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.rowInserted();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final boolean rowDeleted() throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            return this.inner.rowDeleted();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateNull(int a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateNull(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateNull(String a) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateNull(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateBoolean(int a, boolean b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateBoolean(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateBoolean(String a, boolean b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateBoolean(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateByte(String a, byte b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateByte(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateByte(int a, byte b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateByte(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateShort(String a, short b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateShort(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateShort(int a, short b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateShort(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateInt(int a, int b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateInt(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateInt(String a, int b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateInt(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateLong(int a, long b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateLong(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateLong(String a, long b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateLong(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateFloat(int a, float b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateFloat(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateFloat(String a, float b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateFloat(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateDouble(int a, double b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateDouble(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateDouble(String a, double b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateDouble(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateBigDecimal(String a, BigDecimal b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateBigDecimal(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateBigDecimal(int a, BigDecimal b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateBigDecimal(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateString(String a, String b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateString(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateString(int a, String b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateString(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateDate(int a, Date b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateDate(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateDate(String a, Date b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateDate(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateTime(String a, Time b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateTime(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateTime(int a, Time b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateTime(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateTimestamp(int a, Timestamp b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateTimestamp(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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
    public final void updateTimestamp(String a, Timestamp b) throws SQLException {
        try {
            if (this.proxyConn != null) {
                this.proxyConn.maybeDirtyTransaction();
            }
            this.inner.updateTimestamp(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed ResultSet!!!", (Throwable)exc);
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

    NewProxyResultSet(ResultSet inner, NewPooledConnection parentPooledConnection) {
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
        return ResultSet.class == intfcl || intfcl.isAssignableFrom(this.inner.getClass());
    }

    private boolean isWrapperForThis(Class intfcl) {
        return intfcl.isAssignableFrom(this.getClass());
    }

    NewProxyResultSet(ResultSet inner, NewPooledConnection parentPooledConnection, Object c, Object cProxy) {
        this(inner, parentPooledConnection);
        this.creator = c;
        this.creatorProxy = cProxy;
        if (this.creatorProxy instanceof NewProxyConnection) {
            this.proxyConn = (NewProxyConnection)cProxy;
        }
    }
}

