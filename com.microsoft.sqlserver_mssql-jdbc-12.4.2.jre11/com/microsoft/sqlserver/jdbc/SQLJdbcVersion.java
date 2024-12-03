/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;

final class SQLJdbcVersion {
    static final int MAJOR = 12;
    static final int MINOR = 4;
    static final int PATCH = 2;
    static final int BUILD = 0;
    static final String RELEASE_EXT = "";

    private SQLJdbcVersion() {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }
}

