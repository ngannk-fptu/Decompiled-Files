/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.datasource;

import java.sql.Connection;

@FunctionalInterface
public interface ConnectionHandle {
    public Connection getConnection();

    default public void releaseConnection(Connection con) {
    }
}

