/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementCreator {
    public PreparedStatement createPreparedStatement(Connection var1) throws SQLException;
}

