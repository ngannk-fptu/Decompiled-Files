/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ISQLServerConnection43;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.sql.SQLException;
import java.sql.ShardingKey;

public class SQLServerConnection43
extends SQLServerConnection
implements ISQLServerConnection43 {
    private static final long serialVersionUID = -6904163521498951547L;

    SQLServerConnection43(String parentInfo) throws SQLServerException {
        super(parentInfo);
    }

    @Override
    public void beginRequest() throws SQLException {
        this.beginRequestInternal();
    }

    @Override
    public void endRequest() throws SQLException {
        this.endRequestInternal();
    }

    @Override
    public void setShardingKey(ShardingKey shardingKey) throws SQLException {
        SQLServerException.throwFeatureNotSupportedException();
    }

    @Override
    public void setShardingKey(ShardingKey shardingKey, ShardingKey superShardingKey) throws SQLException {
        SQLServerException.throwFeatureNotSupportedException();
    }

    @Override
    public boolean setShardingKeyIfValid(ShardingKey shardingKey, int timeout) throws SQLException {
        SQLServerException.throwFeatureNotSupportedException();
        return false;
    }

    @Override
    public boolean setShardingKeyIfValid(ShardingKey shardingKey, ShardingKey superShardingKey, int timeout) throws SQLException {
        SQLServerException.throwFeatureNotSupportedException();
        return false;
    }
}

