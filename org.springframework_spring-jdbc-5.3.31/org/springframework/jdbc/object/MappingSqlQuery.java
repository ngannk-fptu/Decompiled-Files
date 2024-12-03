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
import org.springframework.jdbc.object.MappingSqlQueryWithParameters;
import org.springframework.lang.Nullable;

public abstract class MappingSqlQuery<T>
extends MappingSqlQueryWithParameters<T> {
    public MappingSqlQuery() {
    }

    public MappingSqlQuery(DataSource ds, String sql) {
        super(ds, sql);
    }

    @Override
    @Nullable
    protected final T mapRow(ResultSet rs, int rowNum, @Nullable Object[] parameters, @Nullable Map<?, ?> context) throws SQLException {
        return this.mapRow(rs, rowNum);
    }

    @Nullable
    protected abstract T mapRow(ResultSet var1, int var2) throws SQLException;
}

