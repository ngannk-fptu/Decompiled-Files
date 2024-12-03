/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.lang.Nullable;

public class ResultSetSupportingSqlParameter
extends SqlParameter {
    @Nullable
    private ResultSetExtractor<?> resultSetExtractor;
    @Nullable
    private RowCallbackHandler rowCallbackHandler;
    @Nullable
    private RowMapper<?> rowMapper;

    public ResultSetSupportingSqlParameter(String name, int sqlType) {
        super(name, sqlType);
    }

    public ResultSetSupportingSqlParameter(String name, int sqlType, int scale) {
        super(name, sqlType, scale);
    }

    public ResultSetSupportingSqlParameter(String name, int sqlType, @Nullable String typeName) {
        super(name, sqlType, typeName);
    }

    public ResultSetSupportingSqlParameter(String name, int sqlType, ResultSetExtractor<?> rse) {
        super(name, sqlType);
        this.resultSetExtractor = rse;
    }

    public ResultSetSupportingSqlParameter(String name, int sqlType, RowCallbackHandler rch) {
        super(name, sqlType);
        this.rowCallbackHandler = rch;
    }

    public ResultSetSupportingSqlParameter(String name, int sqlType, RowMapper<?> rm) {
        super(name, sqlType);
        this.rowMapper = rm;
    }

    public boolean isResultSetSupported() {
        return this.resultSetExtractor != null || this.rowCallbackHandler != null || this.rowMapper != null;
    }

    @Nullable
    public ResultSetExtractor<?> getResultSetExtractor() {
        return this.resultSetExtractor;
    }

    @Nullable
    public RowCallbackHandler getRowCallbackHandler() {
        return this.rowCallbackHandler;
    }

    @Nullable
    public RowMapper<?> getRowMapper() {
        return this.rowMapper;
    }

    @Override
    public boolean isInputValueProvided() {
        return false;
    }
}

