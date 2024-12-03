/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.object;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.object.SqlOperation;
import org.springframework.lang.Nullable;

public abstract class SqlQuery<T>
extends SqlOperation {
    private int rowsExpected = 0;

    public SqlQuery() {
    }

    public SqlQuery(DataSource ds, String sql) {
        this.setDataSource(ds);
        this.setSql(sql);
    }

    public void setRowsExpected(int rowsExpected) {
        this.rowsExpected = rowsExpected;
    }

    public int getRowsExpected() {
        return this.rowsExpected;
    }

    public List<T> execute(@Nullable Object[] params, @Nullable Map<?, ?> context) throws DataAccessException {
        this.validateParameters(params);
        RowMapper<T> rowMapper = this.newRowMapper(params, context);
        return this.getJdbcTemplate().query(this.newPreparedStatementCreator(params), rowMapper);
    }

    public List<T> execute(Object ... params) throws DataAccessException {
        return this.execute(params, null);
    }

    public List<T> execute(Map<?, ?> context) throws DataAccessException {
        return this.execute((Object[])null, context);
    }

    public List<T> execute() throws DataAccessException {
        return this.execute((Object[])null, null);
    }

    public List<T> execute(int p1, @Nullable Map<?, ?> context) throws DataAccessException {
        return this.execute(new Object[]{p1}, context);
    }

    public List<T> execute(int p1) throws DataAccessException {
        return this.execute(p1, (Map<?, ?>)null);
    }

    public List<T> execute(int p1, int p2, @Nullable Map<?, ?> context) throws DataAccessException {
        return this.execute(new Object[]{p1, p2}, context);
    }

    public List<T> execute(int p1, int p2) throws DataAccessException {
        return this.execute(p1, p2, null);
    }

    public List<T> execute(long p1, @Nullable Map<?, ?> context) throws DataAccessException {
        return this.execute(new Object[]{p1}, context);
    }

    public List<T> execute(long p1) throws DataAccessException {
        return this.execute(p1, null);
    }

    public List<T> execute(String p1, @Nullable Map<?, ?> context) throws DataAccessException {
        return this.execute(new Object[]{p1}, context);
    }

    public List<T> execute(String p1) throws DataAccessException {
        return this.execute(p1, null);
    }

    public List<T> executeByNamedParam(Map<String, ?> paramMap, @Nullable Map<?, ?> context) throws DataAccessException {
        this.validateNamedParameters(paramMap);
        ParsedSql parsedSql = this.getParsedSql();
        MapSqlParameterSource paramSource = new MapSqlParameterSource(paramMap);
        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, (SqlParameterSource)paramSource);
        Object[] params = NamedParameterUtils.buildValueArray(parsedSql, paramSource, this.getDeclaredParameters());
        RowMapper<T> rowMapper = this.newRowMapper(params, context);
        return this.getJdbcTemplate().query(this.newPreparedStatementCreator(sqlToUse, params), rowMapper);
    }

    public List<T> executeByNamedParam(Map<String, ?> paramMap) throws DataAccessException {
        return this.executeByNamedParam(paramMap, null);
    }

    @Nullable
    public T findObject(@Nullable Object[] params, @Nullable Map<?, ?> context) throws DataAccessException {
        List<T> results = this.execute(params, context);
        return (T)DataAccessUtils.singleResult(results);
    }

    @Nullable
    public T findObject(Object ... params) throws DataAccessException {
        return this.findObject(params, null);
    }

    @Nullable
    public T findObject(int p1, @Nullable Map<?, ?> context) throws DataAccessException {
        return this.findObject(new Object[]{p1}, context);
    }

    @Nullable
    public T findObject(int p1) throws DataAccessException {
        return this.findObject(p1, (Map<?, ?>)null);
    }

    @Nullable
    public T findObject(int p1, int p2, @Nullable Map<?, ?> context) throws DataAccessException {
        return this.findObject(new Object[]{p1, p2}, context);
    }

    @Nullable
    public T findObject(int p1, int p2) throws DataAccessException {
        return this.findObject(p1, p2, null);
    }

    @Nullable
    public T findObject(long p1, @Nullable Map<?, ?> context) throws DataAccessException {
        return this.findObject(new Object[]{p1}, context);
    }

    @Nullable
    public T findObject(long p1) throws DataAccessException {
        return this.findObject(p1, null);
    }

    @Nullable
    public T findObject(String p1, @Nullable Map<?, ?> context) throws DataAccessException {
        return this.findObject(new Object[]{p1}, context);
    }

    @Nullable
    public T findObject(String p1) throws DataAccessException {
        return this.findObject(p1, null);
    }

    @Nullable
    public T findObjectByNamedParam(Map<String, ?> paramMap, @Nullable Map<?, ?> context) throws DataAccessException {
        List<T> results = this.executeByNamedParam(paramMap, context);
        return (T)DataAccessUtils.singleResult(results);
    }

    @Nullable
    public T findObjectByNamedParam(Map<String, ?> paramMap) throws DataAccessException {
        return this.findObjectByNamedParam(paramMap, null);
    }

    protected abstract RowMapper<T> newRowMapper(@Nullable Object[] var1, @Nullable Map<?, ?> var2);
}

