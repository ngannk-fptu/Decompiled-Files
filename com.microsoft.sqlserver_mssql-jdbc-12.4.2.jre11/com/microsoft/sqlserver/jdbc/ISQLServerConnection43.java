/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ISQLServerConnection;
import java.sql.SQLException;

public interface ISQLServerConnection43
extends ISQLServerConnection {
    @Override
    public void beginRequest() throws SQLException;

    @Override
    public void endRequest() throws SQLException;
}

