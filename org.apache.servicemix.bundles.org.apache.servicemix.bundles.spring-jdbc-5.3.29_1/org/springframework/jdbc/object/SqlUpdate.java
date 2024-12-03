/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 */
package org.springframework.jdbc.object;

import java.util.Map;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.object.SqlOperation;
import org.springframework.jdbc.support.KeyHolder;

public class SqlUpdate
extends SqlOperation {
    private int maxRowsAffected = 0;
    private int requiredRowsAffected = 0;

    public SqlUpdate() {
    }

    public SqlUpdate(DataSource ds, String sql) {
        this.setDataSource(ds);
        this.setSql(sql);
    }

    public SqlUpdate(DataSource ds, String sql, int[] types) {
        this.setDataSource(ds);
        this.setSql(sql);
        this.setTypes(types);
    }

    public SqlUpdate(DataSource ds, String sql, int[] types, int maxRowsAffected) {
        this.setDataSource(ds);
        this.setSql(sql);
        this.setTypes(types);
        this.maxRowsAffected = maxRowsAffected;
    }

    public void setMaxRowsAffected(int maxRowsAffected) {
        this.maxRowsAffected = maxRowsAffected;
    }

    public void setRequiredRowsAffected(int requiredRowsAffected) {
        this.requiredRowsAffected = requiredRowsAffected;
    }

    protected void checkRowsAffected(int rowsAffected) throws JdbcUpdateAffectedIncorrectNumberOfRowsException {
        if (this.maxRowsAffected > 0 && rowsAffected > this.maxRowsAffected) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(this.resolveSql(), this.maxRowsAffected, rowsAffected);
        }
        if (this.requiredRowsAffected > 0 && rowsAffected != this.requiredRowsAffected) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(this.resolveSql(), this.requiredRowsAffected, rowsAffected);
        }
    }

    public int update(Object ... params) throws DataAccessException {
        this.validateParameters(params);
        int rowsAffected = this.getJdbcTemplate().update(this.newPreparedStatementCreator(params));
        this.checkRowsAffected(rowsAffected);
        return rowsAffected;
    }

    public int update(Object[] params, KeyHolder generatedKeyHolder) throws DataAccessException {
        if (!this.isReturnGeneratedKeys() && this.getGeneratedKeysColumnNames() == null) {
            throw new InvalidDataAccessApiUsageException("The update method taking a KeyHolder should only be used when generated keys have been configured by calling either 'setReturnGeneratedKeys' or 'setGeneratedKeysColumnNames'.");
        }
        this.validateParameters(params);
        int rowsAffected = this.getJdbcTemplate().update(this.newPreparedStatementCreator(params), generatedKeyHolder);
        this.checkRowsAffected(rowsAffected);
        return rowsAffected;
    }

    public int update() throws DataAccessException {
        return this.update(new Object[0]);
    }

    public int update(int p1) throws DataAccessException {
        return this.update(new Object[]{p1});
    }

    public int update(int p1, int p2) throws DataAccessException {
        return this.update(new Object[]{p1, p2});
    }

    public int update(long p1) throws DataAccessException {
        return this.update(new Object[]{p1});
    }

    public int update(long p1, long p2) throws DataAccessException {
        return this.update(new Object[]{p1, p2});
    }

    public int update(String p) throws DataAccessException {
        return this.update(new Object[]{p});
    }

    public int update(String p1, String p2) throws DataAccessException {
        return this.update(new Object[]{p1, p2});
    }

    public int updateByNamedParam(Map<String, ?> paramMap) throws DataAccessException {
        this.validateNamedParameters(paramMap);
        ParsedSql parsedSql = this.getParsedSql();
        MapSqlParameterSource paramSource = new MapSqlParameterSource(paramMap);
        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, (SqlParameterSource)paramSource);
        Object[] params = NamedParameterUtils.buildValueArray(parsedSql, paramSource, this.getDeclaredParameters());
        int rowsAffected = this.getJdbcTemplate().update(this.newPreparedStatementCreator(sqlToUse, params));
        this.checkRowsAffected(rowsAffected);
        return rowsAffected;
    }

    public int updateByNamedParam(Map<String, ?> paramMap, KeyHolder generatedKeyHolder) throws DataAccessException {
        this.validateNamedParameters(paramMap);
        ParsedSql parsedSql = this.getParsedSql();
        MapSqlParameterSource paramSource = new MapSqlParameterSource(paramMap);
        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, (SqlParameterSource)paramSource);
        Object[] params = NamedParameterUtils.buildValueArray(parsedSql, paramSource, this.getDeclaredParameters());
        int rowsAffected = this.getJdbcTemplate().update(this.newPreparedStatementCreator(sqlToUse, params), generatedKeyHolder);
        this.checkRowsAffected(rowsAffected);
        return rowsAffected;
    }
}

