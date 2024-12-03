/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.lang.Nullable;

public interface JdbcOperations {
    @Nullable
    public <T> T execute(ConnectionCallback<T> var1) throws DataAccessException;

    @Nullable
    public <T> T execute(StatementCallback<T> var1) throws DataAccessException;

    public void execute(String var1) throws DataAccessException;

    @Nullable
    public <T> T query(String var1, ResultSetExtractor<T> var2) throws DataAccessException;

    public void query(String var1, RowCallbackHandler var2) throws DataAccessException;

    public <T> List<T> query(String var1, RowMapper<T> var2) throws DataAccessException;

    public <T> Stream<T> queryForStream(String var1, RowMapper<T> var2) throws DataAccessException;

    @Nullable
    public <T> T queryForObject(String var1, RowMapper<T> var2) throws DataAccessException;

    @Nullable
    public <T> T queryForObject(String var1, Class<T> var2) throws DataAccessException;

    public Map<String, Object> queryForMap(String var1) throws DataAccessException;

    public <T> List<T> queryForList(String var1, Class<T> var2) throws DataAccessException;

    public List<Map<String, Object>> queryForList(String var1) throws DataAccessException;

    public SqlRowSet queryForRowSet(String var1) throws DataAccessException;

    public int update(String var1) throws DataAccessException;

    public int[] batchUpdate(String ... var1) throws DataAccessException;

    @Nullable
    public <T> T execute(PreparedStatementCreator var1, PreparedStatementCallback<T> var2) throws DataAccessException;

    @Nullable
    public <T> T execute(String var1, PreparedStatementCallback<T> var2) throws DataAccessException;

    @Nullable
    public <T> T query(PreparedStatementCreator var1, ResultSetExtractor<T> var2) throws DataAccessException;

    @Nullable
    public <T> T query(String var1, @Nullable PreparedStatementSetter var2, ResultSetExtractor<T> var3) throws DataAccessException;

    @Nullable
    public <T> T query(String var1, Object[] var2, int[] var3, ResultSetExtractor<T> var4) throws DataAccessException;

    @Deprecated
    @Nullable
    public <T> T query(String var1, @Nullable Object[] var2, ResultSetExtractor<T> var3) throws DataAccessException;

    @Nullable
    public <T> T query(String var1, ResultSetExtractor<T> var2, Object ... var3) throws DataAccessException;

    public void query(PreparedStatementCreator var1, RowCallbackHandler var2) throws DataAccessException;

    public void query(String var1, @Nullable PreparedStatementSetter var2, RowCallbackHandler var3) throws DataAccessException;

    public void query(String var1, Object[] var2, int[] var3, RowCallbackHandler var4) throws DataAccessException;

    @Deprecated
    public void query(String var1, @Nullable Object[] var2, RowCallbackHandler var3) throws DataAccessException;

    public void query(String var1, RowCallbackHandler var2, Object ... var3) throws DataAccessException;

    public <T> List<T> query(PreparedStatementCreator var1, RowMapper<T> var2) throws DataAccessException;

    public <T> List<T> query(String var1, @Nullable PreparedStatementSetter var2, RowMapper<T> var3) throws DataAccessException;

    public <T> List<T> query(String var1, Object[] var2, int[] var3, RowMapper<T> var4) throws DataAccessException;

    @Deprecated
    public <T> List<T> query(String var1, @Nullable Object[] var2, RowMapper<T> var3) throws DataAccessException;

    public <T> List<T> query(String var1, RowMapper<T> var2, Object ... var3) throws DataAccessException;

    public <T> Stream<T> queryForStream(PreparedStatementCreator var1, RowMapper<T> var2) throws DataAccessException;

    public <T> Stream<T> queryForStream(String var1, @Nullable PreparedStatementSetter var2, RowMapper<T> var3) throws DataAccessException;

    public <T> Stream<T> queryForStream(String var1, RowMapper<T> var2, Object ... var3) throws DataAccessException;

    @Nullable
    public <T> T queryForObject(String var1, Object[] var2, int[] var3, RowMapper<T> var4) throws DataAccessException;

    @Deprecated
    @Nullable
    public <T> T queryForObject(String var1, @Nullable Object[] var2, RowMapper<T> var3) throws DataAccessException;

    @Nullable
    public <T> T queryForObject(String var1, RowMapper<T> var2, Object ... var3) throws DataAccessException;

    @Nullable
    public <T> T queryForObject(String var1, Object[] var2, int[] var3, Class<T> var4) throws DataAccessException;

    @Deprecated
    @Nullable
    public <T> T queryForObject(String var1, @Nullable Object[] var2, Class<T> var3) throws DataAccessException;

    @Nullable
    public <T> T queryForObject(String var1, Class<T> var2, Object ... var3) throws DataAccessException;

    public Map<String, Object> queryForMap(String var1, Object[] var2, int[] var3) throws DataAccessException;

    public Map<String, Object> queryForMap(String var1, Object ... var2) throws DataAccessException;

    public <T> List<T> queryForList(String var1, Object[] var2, int[] var3, Class<T> var4) throws DataAccessException;

    @Deprecated
    public <T> List<T> queryForList(String var1, @Nullable Object[] var2, Class<T> var3) throws DataAccessException;

    public <T> List<T> queryForList(String var1, Class<T> var2, Object ... var3) throws DataAccessException;

    public List<Map<String, Object>> queryForList(String var1, Object[] var2, int[] var3) throws DataAccessException;

    public List<Map<String, Object>> queryForList(String var1, Object ... var2) throws DataAccessException;

    public SqlRowSet queryForRowSet(String var1, Object[] var2, int[] var3) throws DataAccessException;

    public SqlRowSet queryForRowSet(String var1, Object ... var2) throws DataAccessException;

    public int update(PreparedStatementCreator var1) throws DataAccessException;

    public int update(PreparedStatementCreator var1, KeyHolder var2) throws DataAccessException;

    public int update(String var1, @Nullable PreparedStatementSetter var2) throws DataAccessException;

    public int update(String var1, Object[] var2, int[] var3) throws DataAccessException;

    public int update(String var1, Object ... var2) throws DataAccessException;

    public int[] batchUpdate(String var1, BatchPreparedStatementSetter var2) throws DataAccessException;

    public int[] batchUpdate(String var1, List<Object[]> var2) throws DataAccessException;

    public int[] batchUpdate(String var1, List<Object[]> var2, int[] var3) throws DataAccessException;

    public <T> int[][] batchUpdate(String var1, Collection<T> var2, int var3, ParameterizedPreparedStatementSetter<T> var4) throws DataAccessException;

    @Nullable
    public <T> T execute(CallableStatementCreator var1, CallableStatementCallback<T> var2) throws DataAccessException;

    @Nullable
    public <T> T execute(String var1, CallableStatementCallback<T> var2) throws DataAccessException;

    public Map<String, Object> call(CallableStatementCreator var1, List<SqlParameter> var2) throws DataAccessException;
}

