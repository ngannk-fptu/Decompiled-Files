/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql;

import com.mchange.v1.db.sql.ConnectionBundle;
import com.mchange.v1.db.sql.ConnectionBundleImpl;
import com.mchange.v1.db.sql.ConnectionBundlePool;
import com.mchange.v1.util.AbstractResourcePool;
import com.mchange.v1.util.BrokenObjectException;
import com.mchange.v1.util.UnexpectedException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class ConnectionBundlePoolImpl
extends AbstractResourcePool
implements ConnectionBundlePool {
    String jdbcUrl;
    String username;
    String pwd;

    public ConnectionBundlePoolImpl(String string, String string2, String string3, int n, int n2, int n3) throws SQLException {
        super(n, n2, n3);
        this.init(string, string2, string3);
    }

    protected ConnectionBundlePoolImpl(int n, int n2, int n3) {
        super(n, n2, n3);
    }

    protected void init(String string, String string2, String string3) throws SQLException {
        this.jdbcUrl = string;
        this.username = string2;
        this.pwd = string3;
        try {
            this.init();
        }
        catch (SQLException sQLException) {
            throw sQLException;
        }
        catch (Exception exception) {
            throw new UnexpectedException(exception, "Unexpected exception while initializing ConnectionBundlePool");
        }
    }

    @Override
    public ConnectionBundle checkoutBundle() throws SQLException, BrokenObjectException, InterruptedException {
        try {
            return (ConnectionBundle)this.checkoutResource();
        }
        catch (BrokenObjectException brokenObjectException) {
            throw brokenObjectException;
        }
        catch (InterruptedException interruptedException) {
            throw interruptedException;
        }
        catch (SQLException sQLException) {
            throw sQLException;
        }
        catch (Exception exception) {
            throw new UnexpectedException(exception, "Unexpected exception while checking out ConnectionBundle");
        }
    }

    @Override
    public void checkinBundle(ConnectionBundle connectionBundle) throws BrokenObjectException {
        this.checkinResource(connectionBundle);
    }

    @Override
    public void close() throws SQLException {
        try {
            super.close();
        }
        catch (SQLException sQLException) {
            throw sQLException;
        }
        catch (Exception exception) {
            throw new UnexpectedException(exception, "Unexpected exception while closing pool.");
        }
    }

    @Override
    protected Object acquireResource() throws Exception {
        Connection connection = DriverManager.getConnection(this.jdbcUrl, this.username, this.pwd);
        this.setConnectionOptions(connection);
        return new ConnectionBundleImpl(connection);
    }

    @Override
    protected void refurbishResource(Object object) throws BrokenObjectException {
        boolean bl;
        try {
            Connection connection = ((ConnectionBundle)object).getConnection();
            connection.rollback();
            bl = connection.isClosed();
            this.setConnectionOptions(connection);
        }
        catch (SQLException sQLException) {
            bl = true;
        }
        if (bl) {
            throw new BrokenObjectException(object);
        }
    }

    @Override
    protected void destroyResource(Object object) throws Exception {
        ((ConnectionBundle)object).close();
    }

    protected abstract void setConnectionOptions(Connection var1) throws SQLException;
}

