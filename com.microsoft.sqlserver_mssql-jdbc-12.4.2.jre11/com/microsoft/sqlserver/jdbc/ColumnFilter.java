/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SQLServerException;

abstract class ColumnFilter {
    ColumnFilter() {
    }

    abstract Object apply(Object var1, JDBCType var2) throws SQLServerException;
}

