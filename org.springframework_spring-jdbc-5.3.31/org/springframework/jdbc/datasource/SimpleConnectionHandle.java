/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.datasource;

import java.sql.Connection;
import org.springframework.jdbc.datasource.ConnectionHandle;
import org.springframework.util.Assert;

public class SimpleConnectionHandle
implements ConnectionHandle {
    private final Connection connection;

    public SimpleConnectionHandle(Connection connection) {
        Assert.notNull((Object)connection, (String)"Connection must not be null");
        this.connection = connection;
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    public String toString() {
        return "SimpleConnectionHandle: " + this.connection;
    }
}

