/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.Serializable;
import java.sql.Statement;

public interface ISQLServerStatement
extends Statement,
Serializable {
    public void setResponseBuffering(String var1) throws SQLServerException;

    public String getResponseBuffering() throws SQLServerException;

    public int getCancelQueryTimeout() throws SQLServerException;

    public void setCancelQueryTimeout(int var1) throws SQLServerException;
}

