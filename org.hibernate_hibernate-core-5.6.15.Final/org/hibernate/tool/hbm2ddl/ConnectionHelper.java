/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

import java.sql.Connection;
import java.sql.SQLException;

@Deprecated
public interface ConnectionHelper {
    public void prepare(boolean var1) throws SQLException;

    public Connection getConnection() throws SQLException;

    public void release() throws SQLException;
}

