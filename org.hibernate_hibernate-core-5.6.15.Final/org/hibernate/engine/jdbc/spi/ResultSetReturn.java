/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.spi;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public interface ResultSetReturn {
    public ResultSet extract(PreparedStatement var1);

    public ResultSet extract(CallableStatement var1);

    public ResultSet extract(Statement var1, String var2);

    public ResultSet execute(PreparedStatement var1);

    public ResultSet execute(Statement var1, String var2);

    public int executeUpdate(PreparedStatement var1);

    public int executeUpdate(Statement var1, String var2);
}

