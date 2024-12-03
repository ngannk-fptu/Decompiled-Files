/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;

@FunctionalInterface
public interface ResultSetExtractor<T> {
    @Nullable
    public T extractData(ResultSet var1) throws SQLException, DataAccessException;
}

