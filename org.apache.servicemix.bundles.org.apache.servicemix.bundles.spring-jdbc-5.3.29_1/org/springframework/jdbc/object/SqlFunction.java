/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.TypeMismatchDataAccessException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.object;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.dao.TypeMismatchDataAccessException;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.lang.Nullable;

public class SqlFunction<T>
extends MappingSqlQuery<T> {
    private final SingleColumnRowMapper<T> rowMapper = new SingleColumnRowMapper();

    public SqlFunction() {
        this.setRowsExpected(1);
    }

    public SqlFunction(DataSource ds, String sql) {
        this.setRowsExpected(1);
        this.setDataSource(ds);
        this.setSql(sql);
    }

    public SqlFunction(DataSource ds, String sql, int[] types) {
        this.setRowsExpected(1);
        this.setDataSource(ds);
        this.setSql(sql);
        this.setTypes(types);
    }

    public SqlFunction(DataSource ds, String sql, int[] types, Class<T> resultType) {
        this.setRowsExpected(1);
        this.setDataSource(ds);
        this.setSql(sql);
        this.setTypes(types);
        this.setResultType(resultType);
    }

    public void setResultType(Class<T> resultType) {
        this.rowMapper.setRequiredType(resultType);
    }

    @Override
    @Nullable
    protected T mapRow(ResultSet rs, int rowNum) throws SQLException {
        return this.rowMapper.mapRow(rs, rowNum);
    }

    public int run() {
        return this.run(new Object[0]);
    }

    public int run(int parameter) {
        return this.run(new Object[]{parameter});
    }

    public int run(Object ... parameters) {
        Object obj = super.findObject(parameters);
        if (!(obj instanceof Number)) {
            throw new TypeMismatchDataAccessException("Could not convert result object [" + obj + "] to int");
        }
        return ((Number)obj).intValue();
    }

    @Nullable
    public Object runGeneric() {
        return this.findObject((Object[])null, null);
    }

    @Nullable
    public Object runGeneric(int parameter) {
        return this.findObject(parameter);
    }

    @Nullable
    public Object runGeneric(Object[] parameters) {
        return this.findObject(parameters);
    }
}

