/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core;

import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;

@FunctionalInterface
public interface StatementCallback<T> {
    @Nullable
    public T doInStatement(Statement var1) throws SQLException, DataAccessException;
}

