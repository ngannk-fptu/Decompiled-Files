/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.util.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public interface SingleConnectionProvider {
    public Connection getConnection(Properties var1) throws SQLException;
}

