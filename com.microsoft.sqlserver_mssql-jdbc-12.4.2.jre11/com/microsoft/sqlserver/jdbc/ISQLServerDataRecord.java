/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerMetaData;

public interface ISQLServerDataRecord {
    public SQLServerMetaData getColumnMetaData(int var1);

    public int getColumnCount();

    public Object[] getRowData();

    public boolean next();
}

