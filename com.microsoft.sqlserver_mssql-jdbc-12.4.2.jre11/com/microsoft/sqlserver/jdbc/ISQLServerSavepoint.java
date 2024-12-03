/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.Serializable;
import java.sql.Savepoint;

public interface ISQLServerSavepoint
extends Savepoint,
Serializable {
    @Override
    public String getSavepointName() throws SQLServerException;

    public String getLabel();

    public boolean isNamed();
}

