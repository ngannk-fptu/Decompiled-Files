/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import java.io.Serializable;
import java.sql.SQLException;

abstract class SQLServerLob
implements Serializable {
    private static final long serialVersionUID = -6444654924359581662L;
    boolean delayLoadingLob = true;

    SQLServerLob() {
    }

    abstract void fillFromStream() throws SQLException;

    void setDelayLoadingLob() {
        this.delayLoadingLob = false;
    }
}

