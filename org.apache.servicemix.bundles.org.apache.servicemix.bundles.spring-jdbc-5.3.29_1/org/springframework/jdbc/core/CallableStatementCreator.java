/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface CallableStatementCreator {
    public CallableStatement createCallableStatement(Connection var1) throws SQLException;
}

