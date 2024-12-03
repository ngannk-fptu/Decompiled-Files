/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql;

import java.sql.Connection;
import java.sql.SQLException;

public interface Schema {
    public void createSchema(Connection var1) throws SQLException;

    public void dropSchema(Connection var1) throws SQLException;

    public String getStatementText(String var1, String var2);
}

