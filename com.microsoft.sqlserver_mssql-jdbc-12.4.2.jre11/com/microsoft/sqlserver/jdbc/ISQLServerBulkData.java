/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Set;

public interface ISQLServerBulkData
extends Serializable {
    public Set<Integer> getColumnOrdinals();

    public String getColumnName(int var1);

    public int getColumnType(int var1);

    public int getPrecision(int var1);

    public int getScale(int var1);

    public Object[] getRowData() throws SQLException;

    public boolean next() throws SQLException;
}

