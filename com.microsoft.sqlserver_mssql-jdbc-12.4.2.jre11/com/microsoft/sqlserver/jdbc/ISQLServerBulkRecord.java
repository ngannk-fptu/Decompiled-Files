/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ISQLServerBulkData;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.time.format.DateTimeFormatter;

@Deprecated(since="8.1.0")
public interface ISQLServerBulkRecord
extends ISQLServerBulkData {
    public boolean isAutoIncrement(int var1);

    public void addColumnMetadata(int var1, String var2, int var3, int var4, int var5, DateTimeFormatter var6) throws SQLServerException;

    public void addColumnMetadata(int var1, String var2, int var3, int var4, int var5) throws SQLServerException;

    public void setTimestampWithTimezoneFormat(String var1);

    public void setTimestampWithTimezoneFormat(DateTimeFormatter var1);

    public void setTimeWithTimezoneFormat(String var1);

    public void setTimeWithTimezoneFormat(DateTimeFormatter var1);

    public DateTimeFormatter getColumnDateTimeFormatter(int var1);
}

