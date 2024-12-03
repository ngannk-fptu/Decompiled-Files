/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementSetter {
    public void setValues(PreparedStatement var1) throws SQLException;
}

