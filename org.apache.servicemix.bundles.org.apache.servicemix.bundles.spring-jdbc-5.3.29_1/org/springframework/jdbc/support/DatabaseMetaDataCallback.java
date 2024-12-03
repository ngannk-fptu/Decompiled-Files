/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.support;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.springframework.jdbc.support.MetaDataAccessException;

@FunctionalInterface
public interface DatabaseMetaDataCallback<T> {
    public T processMetaData(DatabaseMetaData var1) throws SQLException, MetaDataAccessException;
}

