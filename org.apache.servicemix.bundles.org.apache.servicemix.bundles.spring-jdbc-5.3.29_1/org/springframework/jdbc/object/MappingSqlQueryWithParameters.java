/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.object;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.object.SqlQuery;
import org.springframework.lang.Nullable;

public abstract class MappingSqlQueryWithParameters<T>
extends SqlQuery<T> {
    public MappingSqlQueryWithParameters() {
    }

    public MappingSqlQueryWithParameters(DataSource ds, String sql) {
        super(ds, sql);
    }

    @Override
    protected RowMapper<T> newRowMapper(@Nullable Object[] parameters, @Nullable Map<?, ?> context) {
        return new RowMapperImpl(parameters, context);
    }

    @Nullable
    protected abstract T mapRow(ResultSet var1, int var2, @Nullable Object[] var3, @Nullable Map<?, ?> var4) throws SQLException;

    protected class RowMapperImpl
    implements RowMapper<T> {
        @Nullable
        private final Object[] params;
        @Nullable
        private final Map<?, ?> context;

        public RowMapperImpl(@Nullable Object[] parameters, Map<?, ?> context) {
            this.params = parameters;
            this.context = context;
        }

        @Override
        @Nullable
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            return MappingSqlQueryWithParameters.this.mapRow(rs, rowNum, this.params, this.context);
        }
    }
}

