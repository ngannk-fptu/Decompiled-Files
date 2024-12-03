/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.ResultSetSupportingSqlParameter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

public class SqlReturnResultSet
extends ResultSetSupportingSqlParameter {
    public SqlReturnResultSet(String name, ResultSetExtractor<?> extractor) {
        super(name, 0, extractor);
    }

    public SqlReturnResultSet(String name, RowCallbackHandler handler) {
        super(name, 0, handler);
    }

    public SqlReturnResultSet(String name, RowMapper<?> mapper) {
        super(name, 0, mapper);
    }

    @Override
    public boolean isResultsParameter() {
        return true;
    }
}

