/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql;

import com.mchange.v1.db.sql.ConnectionBundle;
import com.mchange.v1.db.sql.ConnectionBundlePool;
import com.mchange.v1.db.sql.ConnectionBundlePoolImpl;
import com.mchange.v1.util.BrokenObjectException;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionBundlePoolBean
implements ConnectionBundlePool {
    ConnectionBundlePool inner;

    public void init(String string, String string2, String string3, String string4, int n, int n2, int n3) throws SQLException, ClassNotFoundException {
        Class.forName(string);
        this.init(string2, string3, string4, n, n2, n3);
    }

    public void init(String string, String string2, String string3, int n, int n2, int n3) throws SQLException {
        this.inner = new InnerPool(string, string2, string3, n, n2, n3);
    }

    @Override
    public ConnectionBundle checkoutBundle() throws SQLException, InterruptedException, BrokenObjectException {
        return this.inner.checkoutBundle();
    }

    @Override
    public void checkinBundle(ConnectionBundle connectionBundle) throws SQLException, BrokenObjectException {
        this.inner.checkinBundle(connectionBundle);
    }

    @Override
    public void close() throws SQLException {
        this.inner.close();
    }

    protected void setConnectionOptions(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
    }

    class InnerPool
    extends ConnectionBundlePoolImpl {
        InnerPool(String string, String string2, String string3, int n, int n2, int n3) throws SQLException {
            super(n, n2, n3);
            this.init(string, string2, string3);
        }

        @Override
        protected void setConnectionOptions(Connection connection) throws SQLException {
            ConnectionBundlePoolBean.this.setConnectionOptions(connection);
        }
    }
}

