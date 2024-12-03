/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface ReturningWork<T> {
    public T execute(Connection var1) throws SQLException;
}

