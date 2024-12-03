/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql;

import com.mchange.v1.db.sql.ConnectionBundle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionBundleImpl
implements ConnectionBundle {
    Connection con;
    Map map = new HashMap();

    public ConnectionBundleImpl(Connection connection) {
        this.con = connection;
    }

    @Override
    public Connection getConnection() {
        return this.con;
    }

    @Override
    public PreparedStatement getStatement(String string) {
        return (PreparedStatement)this.map.get(string);
    }

    @Override
    public void putStatement(String string, PreparedStatement preparedStatement) {
        this.map.put(string, preparedStatement);
    }

    @Override
    public void close() throws SQLException {
        this.con.close();
    }

    public void finalize() throws Exception {
        if (!this.con.isClosed()) {
            this.close();
        }
    }
}

