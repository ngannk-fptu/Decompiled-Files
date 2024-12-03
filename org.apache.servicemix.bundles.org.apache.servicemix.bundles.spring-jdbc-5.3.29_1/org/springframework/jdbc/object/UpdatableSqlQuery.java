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

public abstract class UpdatableSqlQuery<T>
extends SqlQuery<T> {
    public UpdatableSqlQuery() {
        this.setUpdatableResults(true);
    }

    public UpdatableSqlQuery(DataSource ds, String sql) {
        super(ds, sql);
        this.setUpdatableResults(true);
    }

    @Override
    protected RowMapper<T> newRowMapper(@Nullable Object[] parameters, @Nullable Map<?, ?> context) {
        return new RowMapperImpl(context);
    }

    protected abstract T updateRow(ResultSet var1, int var2, @Nullable Map<?, ?> var3) throws SQLException;

    protected class RowMapperImpl
    implements RowMapper<T> {
        @Nullable
        private final Map<?, ?> context;

        public RowMapperImpl(Map<?, ?> context) {
            this.context = context;
        }

        @Override
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            Object result = UpdatableSqlQuery.this.updateRow(rs, rowNum, this.context);
            rs.updateRow();
            return result;
        }
    }
}

