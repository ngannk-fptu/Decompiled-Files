/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface BatchPreparedStatementSetter {
    public void setValues(PreparedStatement var1, int var2) throws SQLException;

    public int getBatchSize();
}

