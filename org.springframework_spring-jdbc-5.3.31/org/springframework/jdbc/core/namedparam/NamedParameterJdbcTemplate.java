/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ConcurrentLruCache
 */
package org.springframework.jdbc.core.namedparam;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlRowSetResultSetExtractor;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentLruCache;

public class NamedParameterJdbcTemplate
implements NamedParameterJdbcOperations {
    public static final int DEFAULT_CACHE_LIMIT = 256;
    private final JdbcOperations classicJdbcTemplate;
    private volatile ConcurrentLruCache<String, ParsedSql> parsedSqlCache = new ConcurrentLruCache(256, NamedParameterUtils::parseSqlStatement);

    public NamedParameterJdbcTemplate(DataSource dataSource) {
        Assert.notNull((Object)dataSource, (String)"DataSource must not be null");
        this.classicJdbcTemplate = new JdbcTemplate(dataSource);
    }

    public NamedParameterJdbcTemplate(JdbcOperations classicJdbcTemplate) {
        Assert.notNull((Object)classicJdbcTemplate, (String)"JdbcTemplate must not be null");
        this.classicJdbcTemplate = classicJdbcTemplate;
    }

    @Override
    public JdbcOperations getJdbcOperations() {
        return this.classicJdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        Assert.state((boolean)(this.classicJdbcTemplate instanceof JdbcTemplate), (String)"No JdbcTemplate available");
        return (JdbcTemplate)this.classicJdbcTemplate;
    }

    public void setCacheLimit(int cacheLimit) {
        this.parsedSqlCache = new ConcurrentLruCache(cacheLimit, NamedParameterUtils::parseSqlStatement);
    }

    public int getCacheLimit() {
        return this.parsedSqlCache.sizeLimit();
    }

    @Override
    @Nullable
    public <T> T execute(String sql, SqlParameterSource paramSource, PreparedStatementCallback<T> action) throws DataAccessException {
        return this.getJdbcOperations().execute(this.getPreparedStatementCreator(sql, paramSource), action);
    }

    @Override
    @Nullable
    public <T> T execute(String sql, Map<String, ?> paramMap, PreparedStatementCallback<T> action) throws DataAccessException {
        return this.execute(sql, new MapSqlParameterSource(paramMap), action);
    }

    @Override
    @Nullable
    public <T> T execute(String sql, PreparedStatementCallback<T> action) throws DataAccessException {
        return this.execute(sql, EmptySqlParameterSource.INSTANCE, action);
    }

    @Override
    @Nullable
    public <T> T query(String sql, SqlParameterSource paramSource, ResultSetExtractor<T> rse) throws DataAccessException {
        return this.getJdbcOperations().query(this.getPreparedStatementCreator(sql, paramSource), rse);
    }

    @Override
    @Nullable
    public <T> T query(String sql, Map<String, ?> paramMap, ResultSetExtractor<T> rse) throws DataAccessException {
        return this.query(sql, (SqlParameterSource)new MapSqlParameterSource(paramMap), rse);
    }

    @Override
    @Nullable
    public <T> T query(String sql, ResultSetExtractor<T> rse) throws DataAccessException {
        return this.query(sql, (SqlParameterSource)EmptySqlParameterSource.INSTANCE, rse);
    }

    @Override
    public void query(String sql, SqlParameterSource paramSource, RowCallbackHandler rch) throws DataAccessException {
        this.getJdbcOperations().query(this.getPreparedStatementCreator(sql, paramSource), rch);
    }

    @Override
    public void query(String sql, Map<String, ?> paramMap, RowCallbackHandler rch) throws DataAccessException {
        this.query(sql, (SqlParameterSource)new MapSqlParameterSource(paramMap), rch);
    }

    @Override
    public void query(String sql, RowCallbackHandler rch) throws DataAccessException {
        this.query(sql, (SqlParameterSource)EmptySqlParameterSource.INSTANCE, rch);
    }

    @Override
    public <T> List<T> query(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper) throws DataAccessException {
        return this.getJdbcOperations().query(this.getPreparedStatementCreator(sql, paramSource), rowMapper);
    }

    @Override
    public <T> List<T> query(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper) throws DataAccessException {
        return this.query(sql, (SqlParameterSource)new MapSqlParameterSource(paramMap), rowMapper);
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        return this.query(sql, (SqlParameterSource)EmptySqlParameterSource.INSTANCE, rowMapper);
    }

    @Override
    public <T> Stream<T> queryForStream(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper) throws DataAccessException {
        return this.getJdbcOperations().queryForStream(this.getPreparedStatementCreator(sql, paramSource), rowMapper);
    }

    @Override
    public <T> Stream<T> queryForStream(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper) throws DataAccessException {
        return this.queryForStream(sql, new MapSqlParameterSource(paramMap), rowMapper);
    }

    @Override
    @Nullable
    public <T> T queryForObject(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper) throws DataAccessException {
        List<T> results = this.getJdbcOperations().query(this.getPreparedStatementCreator(sql, paramSource), rowMapper);
        return (T)DataAccessUtils.nullableSingleResult(results);
    }

    @Override
    @Nullable
    public <T> T queryForObject(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper) throws DataAccessException {
        return this.queryForObject(sql, (SqlParameterSource)new MapSqlParameterSource(paramMap), rowMapper);
    }

    @Override
    @Nullable
    public <T> T queryForObject(String sql, SqlParameterSource paramSource, Class<T> requiredType) throws DataAccessException {
        return this.queryForObject(sql, paramSource, new SingleColumnRowMapper<T>(requiredType));
    }

    @Override
    @Nullable
    public <T> T queryForObject(String sql, Map<String, ?> paramMap, Class<T> requiredType) throws DataAccessException {
        return this.queryForObject(sql, paramMap, new SingleColumnRowMapper<T>(requiredType));
    }

    @Override
    public Map<String, Object> queryForMap(String sql, SqlParameterSource paramSource) throws DataAccessException {
        Map<String, Object> result = this.queryForObject(sql, paramSource, new ColumnMapRowMapper());
        Assert.state((result != null ? 1 : 0) != 0, (String)"No result map");
        return result;
    }

    @Override
    public Map<String, Object> queryForMap(String sql, Map<String, ?> paramMap) throws DataAccessException {
        Map<String, Object> result = this.queryForObject(sql, paramMap, new ColumnMapRowMapper());
        Assert.state((result != null ? 1 : 0) != 0, (String)"No result map");
        return result;
    }

    @Override
    public <T> List<T> queryForList(String sql, SqlParameterSource paramSource, Class<T> elementType) throws DataAccessException {
        return this.query(sql, paramSource, new SingleColumnRowMapper<T>(elementType));
    }

    @Override
    public <T> List<T> queryForList(String sql, Map<String, ?> paramMap, Class<T> elementType) throws DataAccessException {
        return this.queryForList(sql, new MapSqlParameterSource(paramMap), elementType);
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql, SqlParameterSource paramSource) throws DataAccessException {
        return this.query(sql, paramSource, new ColumnMapRowMapper());
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql, Map<String, ?> paramMap) throws DataAccessException {
        return this.queryForList(sql, new MapSqlParameterSource(paramMap));
    }

    @Override
    public SqlRowSet queryForRowSet(String sql, SqlParameterSource paramSource) throws DataAccessException {
        SqlRowSet result = this.getJdbcOperations().query(this.getPreparedStatementCreator(sql, paramSource), new SqlRowSetResultSetExtractor());
        Assert.state((result != null ? 1 : 0) != 0, (String)"No result");
        return result;
    }

    @Override
    public SqlRowSet queryForRowSet(String sql, Map<String, ?> paramMap) throws DataAccessException {
        return this.queryForRowSet(sql, new MapSqlParameterSource(paramMap));
    }

    @Override
    public int update(String sql, SqlParameterSource paramSource) throws DataAccessException {
        return this.getJdbcOperations().update(this.getPreparedStatementCreator(sql, paramSource));
    }

    @Override
    public int update(String sql, Map<String, ?> paramMap) throws DataAccessException {
        return this.update(sql, new MapSqlParameterSource(paramMap));
    }

    @Override
    public int update(String sql, SqlParameterSource paramSource, KeyHolder generatedKeyHolder) throws DataAccessException {
        return this.update(sql, paramSource, generatedKeyHolder, null);
    }

    @Override
    public int update(String sql, SqlParameterSource paramSource, KeyHolder generatedKeyHolder, @Nullable String[] keyColumnNames) throws DataAccessException {
        PreparedStatementCreator psc = this.getPreparedStatementCreator(sql, paramSource, pscf -> {
            if (keyColumnNames != null) {
                pscf.setGeneratedKeysColumnNames(keyColumnNames);
            } else {
                pscf.setReturnGeneratedKeys(true);
            }
        });
        return this.getJdbcOperations().update(psc, generatedKeyHolder);
    }

    @Override
    public int[] batchUpdate(String sql, final SqlParameterSource[] batchArgs) {
        if (batchArgs.length == 0) {
            return new int[0];
        }
        final ParsedSql parsedSql = this.getParsedSql(sql);
        final PreparedStatementCreatorFactory pscf = this.getPreparedStatementCreatorFactory(parsedSql, batchArgs[0]);
        return this.getJdbcOperations().batchUpdate(pscf.getSql(), new BatchPreparedStatementSetter(){

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Object[] values = NamedParameterUtils.buildValueArray(parsedSql, batchArgs[i], null);
                pscf.newPreparedStatementSetter(values).setValues(ps);
            }

            @Override
            public int getBatchSize() {
                return batchArgs.length;
            }
        });
    }

    @Override
    public int[] batchUpdate(String sql, Map<String, ?>[] batchValues) {
        return this.batchUpdate(sql, SqlParameterSourceUtils.createBatch(batchValues));
    }

    protected PreparedStatementCreator getPreparedStatementCreator(String sql, SqlParameterSource paramSource) {
        return this.getPreparedStatementCreator(sql, paramSource, null);
    }

    protected PreparedStatementCreator getPreparedStatementCreator(String sql, SqlParameterSource paramSource, @Nullable Consumer<PreparedStatementCreatorFactory> customizer) {
        ParsedSql parsedSql = this.getParsedSql(sql);
        PreparedStatementCreatorFactory pscf = this.getPreparedStatementCreatorFactory(parsedSql, paramSource);
        if (customizer != null) {
            customizer.accept(pscf);
        }
        Object[] params = NamedParameterUtils.buildValueArray(parsedSql, paramSource, null);
        return pscf.newPreparedStatementCreator(params);
    }

    protected ParsedSql getParsedSql(String sql) {
        Assert.notNull((Object)sql, (String)"SQL must not be null");
        return (ParsedSql)this.parsedSqlCache.get((Object)sql);
    }

    protected PreparedStatementCreatorFactory getPreparedStatementCreatorFactory(ParsedSql parsedSql, SqlParameterSource paramSource) {
        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramSource);
        List<SqlParameter> declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql, paramSource);
        return new PreparedStatementCreatorFactory(sqlToUse, declaredParameters);
    }
}

