/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core.namedparam;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.lang.Nullable;

public interface NamedParameterJdbcOperations {
    public JdbcOperations getJdbcOperations();

    @Nullable
    public <T> T execute(String var1, SqlParameterSource var2, PreparedStatementCallback<T> var3) throws DataAccessException;

    @Nullable
    public <T> T execute(String var1, Map<String, ?> var2, PreparedStatementCallback<T> var3) throws DataAccessException;

    @Nullable
    public <T> T execute(String var1, PreparedStatementCallback<T> var2) throws DataAccessException;

    @Nullable
    public <T> T query(String var1, SqlParameterSource var2, ResultSetExtractor<T> var3) throws DataAccessException;

    @Nullable
    public <T> T query(String var1, Map<String, ?> var2, ResultSetExtractor<T> var3) throws DataAccessException;

    @Nullable
    public <T> T query(String var1, ResultSetExtractor<T> var2) throws DataAccessException;

    public void query(String var1, SqlParameterSource var2, RowCallbackHandler var3) throws DataAccessException;

    public void query(String var1, Map<String, ?> var2, RowCallbackHandler var3) throws DataAccessException;

    public void query(String var1, RowCallbackHandler var2) throws DataAccessException;

    public <T> List<T> query(String var1, SqlParameterSource var2, RowMapper<T> var3) throws DataAccessException;

    public <T> List<T> query(String var1, Map<String, ?> var2, RowMapper<T> var3) throws DataAccessException;

    public <T> List<T> query(String var1, RowMapper<T> var2) throws DataAccessException;

    public <T> Stream<T> queryForStream(String var1, SqlParameterSource var2, RowMapper<T> var3) throws DataAccessException;

    public <T> Stream<T> queryForStream(String var1, Map<String, ?> var2, RowMapper<T> var3) throws DataAccessException;

    @Nullable
    public <T> T queryForObject(String var1, SqlParameterSource var2, RowMapper<T> var3) throws DataAccessException;

    @Nullable
    public <T> T queryForObject(String var1, Map<String, ?> var2, RowMapper<T> var3) throws DataAccessException;

    @Nullable
    public <T> T queryForObject(String var1, SqlParameterSource var2, Class<T> var3) throws DataAccessException;

    @Nullable
    public <T> T queryForObject(String var1, Map<String, ?> var2, Class<T> var3) throws DataAccessException;

    public Map<String, Object> queryForMap(String var1, SqlParameterSource var2) throws DataAccessException;

    public Map<String, Object> queryForMap(String var1, Map<String, ?> var2) throws DataAccessException;

    public <T> List<T> queryForList(String var1, SqlParameterSource var2, Class<T> var3) throws DataAccessException;

    public <T> List<T> queryForList(String var1, Map<String, ?> var2, Class<T> var3) throws DataAccessException;

    public List<Map<String, Object>> queryForList(String var1, SqlParameterSource var2) throws DataAccessException;

    public List<Map<String, Object>> queryForList(String var1, Map<String, ?> var2) throws DataAccessException;

    public SqlRowSet queryForRowSet(String var1, SqlParameterSource var2) throws DataAccessException;

    public SqlRowSet queryForRowSet(String var1, Map<String, ?> var2) throws DataAccessException;

    public int update(String var1, SqlParameterSource var2) throws DataAccessException;

    public int update(String var1, Map<String, ?> var2) throws DataAccessException;

    public int update(String var1, SqlParameterSource var2, KeyHolder var3) throws DataAccessException;

    public int update(String var1, SqlParameterSource var2, KeyHolder var3, String[] var4) throws DataAccessException;

    public int[] batchUpdate(String var1, SqlParameterSource[] var2);

    public int[] batchUpdate(String var1, Map<String, ?>[] var2);
}

