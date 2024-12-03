/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.managed;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.tomcat.dbcp.dbcp2.ConnectionFactory;
import org.apache.tomcat.dbcp.dbcp2.managed.TransactionRegistry;

public interface XAConnectionFactory
extends ConnectionFactory {
    @Override
    public Connection createConnection() throws SQLException;

    public TransactionRegistry getTransactionRegistry();
}

