/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql;

import com.mchange.v1.util.ClosableResource;
import java.sql.Connection;
import java.sql.PreparedStatement;

public interface ConnectionBundle
extends ClosableResource {
    public Connection getConnection();

    public PreparedStatement getStatement(String var1);

    public void putStatement(String var1, PreparedStatement var2);
}

