/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.Serializable;
import java.sql.ResultSetMetaData;

public interface ISQLServerResultSetMetaData
extends ResultSetMetaData,
Serializable {
    public boolean isSparseColumnSet(int var1) throws SQLServerException;
}

