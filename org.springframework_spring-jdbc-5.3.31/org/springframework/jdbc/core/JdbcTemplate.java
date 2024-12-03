/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.LinkedCaseInsensitiveMap
 *  org.springframework.util.StringUtils
 */
package org.springframework.jdbc.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.BatchUpdateException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.SQLWarningException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.ArgumentTypePreparedStatementSetter;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.InterruptibleBatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.ResultSetSupportingSqlParameter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.core.SqlReturnType;
import org.springframework.jdbc.core.SqlReturnUpdateCount;
import org.springframework.jdbc.core.SqlRowSetResultSetExtractor;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.datasource.ConnectionProxy;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcAccessor;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;

public class JdbcTemplate
extends JdbcAccessor
implements JdbcOperations {
    private static final String RETURN_RESULT_SET_PREFIX = "#result-set-";
    private static final String RETURN_UPDATE_COUNT_PREFIX = "#update-count-";
    private boolean ignoreWarnings = true;
    private int fetchSize = -1;
    private int maxRows = -1;
    private int queryTimeout = -1;
    private boolean skipResultsProcessing = false;
    private boolean skipUndeclaredResults = false;
    private boolean resultsMapCaseInsensitive = false;

    public JdbcTemplate() {
    }

    public JdbcTemplate(DataSource dataSource) {
        this.setDataSource(dataSource);
        this.afterPropertiesSet();
    }

    public JdbcTemplate(DataSource dataSource, boolean lazyInit) {
        this.setDataSource(dataSource);
        this.setLazyInit(lazyInit);
        this.afterPropertiesSet();
    }

    public void setIgnoreWarnings(boolean ignoreWarnings) {
        this.ignoreWarnings = ignoreWarnings;
    }

    public boolean isIgnoreWarnings() {
        return this.ignoreWarnings;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public int getFetchSize() {
        return this.fetchSize;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public int getMaxRows() {
        return this.maxRows;
    }

    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public int getQueryTimeout() {
        return this.queryTimeout;
    }

    public void setSkipResultsProcessing(boolean skipResultsProcessing) {
        this.skipResultsProcessing = skipResultsProcessing;
    }

    public boolean isSkipResultsProcessing() {
        return this.skipResultsProcessing;
    }

    public void setSkipUndeclaredResults(boolean skipUndeclaredResults) {
        this.skipUndeclaredResults = skipUndeclaredResults;
    }

    public boolean isSkipUndeclaredResults() {
        return this.skipUndeclaredResults;
    }

    public void setResultsMapCaseInsensitive(boolean resultsMapCaseInsensitive) {
        this.resultsMapCaseInsensitive = resultsMapCaseInsensitive;
    }

    public boolean isResultsMapCaseInsensitive() {
        return this.resultsMapCaseInsensitive;
    }

    @Override
    @Nullable
    public <T> T execute(ConnectionCallback<T> action) throws DataAccessException {
        Assert.notNull(action, (String)"Callback object must not be null");
        Connection con = DataSourceUtils.getConnection(this.obtainDataSource());
        try {
            Connection conToUse = this.createConnectionProxy(con);
            T t = action.doInConnection(conToUse);
            return t;
        }
        catch (SQLException ex) {
            String sql = JdbcTemplate.getSql(action);
            DataSourceUtils.releaseConnection(con, this.getDataSource());
            con = null;
            throw this.translateException("ConnectionCallback", sql, ex);
        }
        finally {
            DataSourceUtils.releaseConnection(con, this.getDataSource());
        }
    }

    protected Connection createConnectionProxy(Connection con) {
        return (Connection)Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(), new Class[]{ConnectionProxy.class}, (InvocationHandler)new CloseSuppressingInvocationHandler(con));
    }

    @Nullable
    private <T> T execute(StatementCallback<T> action, boolean closeResources) throws DataAccessException {
        Assert.notNull(action, (String)"Callback object must not be null");
        Connection con = DataSourceUtils.getConnection(this.obtainDataSource());
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            this.applyStatementSettings(stmt);
            T result = action.doInStatement(stmt);
            this.handleWarnings(stmt);
            T t = result;
            return t;
        }
        catch (SQLException ex) {
            if (stmt != null) {
                this.handleWarnings(stmt, ex);
            }
            String sql = JdbcTemplate.getSql(action);
            JdbcUtils.closeStatement(stmt);
            stmt = null;
            DataSourceUtils.releaseConnection(con, this.getDataSource());
            con = null;
            throw this.translateException("StatementCallback", sql, ex);
        }
        finally {
            if (closeResources) {
                JdbcUtils.closeStatement(stmt);
                DataSourceUtils.releaseConnection(con, this.getDataSource());
            }
        }
    }

    @Override
    @Nullable
    public <T> T execute(StatementCallback<T> action) throws DataAccessException {
        return this.execute(action, true);
    }

    @Override
    public void execute(final String sql) throws DataAccessException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Executing SQL statement [" + sql + "]"));
        }
        class ExecuteStatementCallback
        implements StatementCallback<Object>,
        SqlProvider {
            ExecuteStatementCallback() {
            }

            @Override
            @Nullable
            public Object doInStatement(Statement stmt) throws SQLException {
                stmt.execute(sql);
                return null;
            }

            @Override
            public String getSql() {
                return sql;
            }
        }
        this.execute(new ExecuteStatementCallback(), true);
    }

    @Override
    @Nullable
    public <T> T query(final String sql, final ResultSetExtractor<T> rse) throws DataAccessException {
        Assert.notNull((Object)sql, (String)"SQL must not be null");
        Assert.notNull(rse, (String)"ResultSetExtractor must not be null");
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Executing SQL query [" + sql + "]"));
        }
        class QueryStatementCallback
        implements StatementCallback<T>,
        SqlProvider {
            QueryStatementCallback() {
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            @Nullable
            public T doInStatement(Statement stmt) throws SQLException {
                Object t;
                ResultSet rs = null;
                try {
                    rs = stmt.executeQuery(sql);
                    t = rse.extractData(rs);
                }
                catch (Throwable throwable) {
                    JdbcUtils.closeResultSet(rs);
                    throw throwable;
                }
                JdbcUtils.closeResultSet(rs);
                return t;
            }

            @Override
            public String getSql() {
                return sql;
            }
        }
        return this.execute(new QueryStatementCallback(), true);
    }

    @Override
    public void query(String sql, RowCallbackHandler rch) throws DataAccessException {
        this.query(sql, new RowCallbackHandlerResultSetExtractor(rch));
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        return (List)JdbcTemplate.result(this.query(sql, new RowMapperResultSetExtractor<T>(rowMapper)));
    }

    @Override
    public <T> Stream<T> queryForStream(final String sql, final RowMapper<T> rowMapper) throws DataAccessException {
        class StreamStatementCallback
        implements StatementCallback<Stream<T>>,
        SqlProvider {
            StreamStatementCallback() {
            }

            @Override
            public Stream<T> doInStatement(Statement stmt) throws SQLException {
                ResultSet rs = stmt.executeQuery(sql);
                Connection con = stmt.getConnection();
                return (Stream)new ResultSetSpliterator(rs, rowMapper).stream().onClose(() -> {
                    JdbcUtils.closeResultSet(rs);
                    JdbcUtils.closeStatement(stmt);
                    DataSourceUtils.releaseConnection(con, JdbcTemplate.this.getDataSource());
                });
            }

            @Override
            public String getSql() {
                return sql;
            }
        }
        return (Stream)JdbcTemplate.result(this.execute(new StreamStatementCallback(), false));
    }

    @Override
    public Map<String, Object> queryForMap(String sql) throws DataAccessException {
        return JdbcTemplate.result(this.queryForObject(sql, this.getColumnMapRowMapper()));
    }

    @Override
    @Nullable
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        List<T> results = this.query(sql, rowMapper);
        return (T)DataAccessUtils.nullableSingleResult(results);
    }

    @Override
    @Nullable
    public <T> T queryForObject(String sql, Class<T> requiredType) throws DataAccessException {
        return this.queryForObject(sql, this.getSingleColumnRowMapper(requiredType));
    }

    @Override
    public <T> List<T> queryForList(String sql, Class<T> elementType) throws DataAccessException {
        return this.query(sql, this.getSingleColumnRowMapper(elementType));
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql) throws DataAccessException {
        return this.query(sql, this.getColumnMapRowMapper());
    }

    @Override
    public SqlRowSet queryForRowSet(String sql) throws DataAccessException {
        return JdbcTemplate.result(this.query(sql, new SqlRowSetResultSetExtractor()));
    }

    @Override
    public int update(final String sql) throws DataAccessException {
        Assert.notNull((Object)sql, (String)"SQL must not be null");
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Executing SQL update [" + sql + "]"));
        }
        class UpdateStatementCallback
        implements StatementCallback<Integer>,
        SqlProvider {
            UpdateStatementCallback() {
            }

            @Override
            public Integer doInStatement(Statement stmt) throws SQLException {
                int rows = stmt.executeUpdate(sql);
                if (JdbcTemplate.this.logger.isTraceEnabled()) {
                    JdbcTemplate.this.logger.trace((Object)("SQL update affected " + rows + " rows"));
                }
                return rows;
            }

            @Override
            public String getSql() {
                return sql;
            }
        }
        return JdbcTemplate.updateCount(this.execute(new UpdateStatementCallback(), true));
    }

    @Override
    public int[] batchUpdate(final String ... sql) throws DataAccessException {
        int[] result;
        Assert.notEmpty((Object[])sql, (String)"SQL array must not be empty");
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Executing SQL batch update of " + sql.length + " statements"));
        }
        class BatchUpdateStatementCallback
        implements StatementCallback<int[]>,
        SqlProvider {
            @Nullable
            private String currSql;

            BatchUpdateStatementCallback() {
            }

            @Override
            public int[] doInStatement(Statement stmt) throws SQLException, DataAccessException {
                int[] rowsAffected = new int[sql.length];
                if (JdbcUtils.supportsBatchUpdates(stmt.getConnection())) {
                    for (String sqlStmt : sql) {
                        this.currSql = this.appendSql(this.currSql, sqlStmt);
                        stmt.addBatch(sqlStmt);
                    }
                    try {
                        rowsAffected = stmt.executeBatch();
                    }
                    catch (BatchUpdateException ex) {
                        String batchExceptionSql = null;
                        for (int i = 0; i < ex.getUpdateCounts().length; ++i) {
                            if (ex.getUpdateCounts()[i] != -3) continue;
                            batchExceptionSql = this.appendSql(batchExceptionSql, sql[i]);
                        }
                        if (StringUtils.hasLength(batchExceptionSql)) {
                            this.currSql = batchExceptionSql;
                        }
                        throw ex;
                    }
                } else {
                    for (int i = 0; i < sql.length; ++i) {
                        this.currSql = sql[i];
                        if (stmt.execute(sql[i])) {
                            throw new InvalidDataAccessApiUsageException("Invalid batch SQL statement: " + sql[i]);
                        }
                        rowsAffected[i] = stmt.getUpdateCount();
                    }
                }
                return rowsAffected;
            }

            private String appendSql(@Nullable String sql2, String statement) {
                return StringUtils.hasLength((String)sql2) ? sql2 + "; " + statement : statement;
            }

            @Override
            @Nullable
            public String getSql() {
                return this.currSql;
            }
        }
        Assert.state(((result = this.execute(new BatchUpdateStatementCallback(), true)) != null ? 1 : 0) != 0, (String)"No update counts");
        return result;
    }

    @Nullable
    private <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action, boolean closeResources) throws DataAccessException {
        Assert.notNull((Object)psc, (String)"PreparedStatementCreator must not be null");
        Assert.notNull(action, (String)"Callback object must not be null");
        if (this.logger.isDebugEnabled()) {
            String sql = JdbcTemplate.getSql(psc);
            this.logger.debug((Object)("Executing prepared SQL statement" + (sql != null ? " [" + sql + "]" : "")));
        }
        Connection con = DataSourceUtils.getConnection(this.obtainDataSource());
        PreparedStatement ps = null;
        try {
            ps = psc.createPreparedStatement(con);
            this.applyStatementSettings(ps);
            T result = action.doInPreparedStatement(ps);
            this.handleWarnings(ps);
            T t = result;
            return t;
        }
        catch (SQLException ex) {
            if (psc instanceof ParameterDisposer) {
                ((ParameterDisposer)((Object)psc)).cleanupParameters();
            }
            if (ps != null) {
                this.handleWarnings(ps, ex);
            }
            String sql = JdbcTemplate.getSql(psc);
            psc = null;
            JdbcUtils.closeStatement(ps);
            ps = null;
            DataSourceUtils.releaseConnection(con, this.getDataSource());
            con = null;
            throw this.translateException("PreparedStatementCallback", sql, ex);
        }
        finally {
            if (closeResources) {
                if (psc instanceof ParameterDisposer) {
                    ((ParameterDisposer)((Object)psc)).cleanupParameters();
                }
                JdbcUtils.closeStatement(ps);
                DataSourceUtils.releaseConnection(con, this.getDataSource());
            }
        }
    }

    @Override
    @Nullable
    public <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action) throws DataAccessException {
        return this.execute(psc, action, true);
    }

    @Override
    @Nullable
    public <T> T execute(String sql, PreparedStatementCallback<T> action) throws DataAccessException {
        return this.execute(new SimplePreparedStatementCreator(sql), action, true);
    }

    @Nullable
    public <T> T query(PreparedStatementCreator psc, final @Nullable PreparedStatementSetter pss, final ResultSetExtractor<T> rse) throws DataAccessException {
        Assert.notNull(rse, (String)"ResultSetExtractor must not be null");
        this.logger.debug((Object)"Executing prepared SQL query");
        return this.execute(psc, new PreparedStatementCallback<T>(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            @Nullable
            public T doInPreparedStatement(PreparedStatement ps) throws SQLException {
                Object t;
                ResultSet rs = null;
                try {
                    if (pss != null) {
                        pss.setValues(ps);
                    }
                    rs = ps.executeQuery();
                    t = rse.extractData(rs);
                }
                catch (Throwable throwable) {
                    JdbcUtils.closeResultSet(rs);
                    if (pss instanceof ParameterDisposer) {
                        ((ParameterDisposer)((Object)pss)).cleanupParameters();
                    }
                    throw throwable;
                }
                JdbcUtils.closeResultSet(rs);
                if (pss instanceof ParameterDisposer) {
                    ((ParameterDisposer)((Object)pss)).cleanupParameters();
                }
                return t;
            }
        }, true);
    }

    @Override
    @Nullable
    public <T> T query(PreparedStatementCreator psc, ResultSetExtractor<T> rse) throws DataAccessException {
        return this.query(psc, null, rse);
    }

    @Override
    @Nullable
    public <T> T query(String sql, @Nullable PreparedStatementSetter pss, ResultSetExtractor<T> rse) throws DataAccessException {
        return this.query(new SimplePreparedStatementCreator(sql), pss, rse);
    }

    @Override
    @Nullable
    public <T> T query(String sql, Object[] args, int[] argTypes, ResultSetExtractor<T> rse) throws DataAccessException {
        return this.query(sql, this.newArgTypePreparedStatementSetter(args, argTypes), rse);
    }

    @Override
    @Deprecated
    @Nullable
    public <T> T query(String sql, @Nullable Object[] args, ResultSetExtractor<T> rse) throws DataAccessException {
        return this.query(sql, this.newArgPreparedStatementSetter(args), rse);
    }

    @Override
    @Nullable
    public <T> T query(String sql, ResultSetExtractor<T> rse, Object ... args) throws DataAccessException {
        return this.query(sql, this.newArgPreparedStatementSetter(args), rse);
    }

    @Override
    public void query(PreparedStatementCreator psc, RowCallbackHandler rch) throws DataAccessException {
        this.query(psc, new RowCallbackHandlerResultSetExtractor(rch));
    }

    @Override
    public void query(String sql, @Nullable PreparedStatementSetter pss, RowCallbackHandler rch) throws DataAccessException {
        this.query(sql, pss, new RowCallbackHandlerResultSetExtractor(rch));
    }

    @Override
    public void query(String sql, Object[] args, int[] argTypes, RowCallbackHandler rch) throws DataAccessException {
        this.query(sql, this.newArgTypePreparedStatementSetter(args, argTypes), rch);
    }

    @Override
    @Deprecated
    public void query(String sql, @Nullable Object[] args, RowCallbackHandler rch) throws DataAccessException {
        this.query(sql, this.newArgPreparedStatementSetter(args), rch);
    }

    @Override
    public void query(String sql, RowCallbackHandler rch, Object ... args) throws DataAccessException {
        this.query(sql, this.newArgPreparedStatementSetter(args), rch);
    }

    @Override
    public <T> List<T> query(PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException {
        return (List)JdbcTemplate.result(this.query(psc, new RowMapperResultSetExtractor<T>(rowMapper)));
    }

    @Override
    public <T> List<T> query(String sql, @Nullable PreparedStatementSetter pss, RowMapper<T> rowMapper) throws DataAccessException {
        return (List)JdbcTemplate.result(this.query(sql, pss, new RowMapperResultSetExtractor<T>(rowMapper)));
    }

    @Override
    public <T> List<T> query(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException {
        return (List)JdbcTemplate.result(this.query(sql, args, argTypes, new RowMapperResultSetExtractor<T>(rowMapper)));
    }

    @Override
    @Deprecated
    public <T> List<T> query(String sql, @Nullable Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        return (List)JdbcTemplate.result(this.query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper)));
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object ... args) throws DataAccessException {
        return (List)JdbcTemplate.result(this.query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper)));
    }

    public <T> Stream<T> queryForStream(PreparedStatementCreator psc, @Nullable PreparedStatementSetter pss, RowMapper<T> rowMapper) throws DataAccessException {
        return JdbcTemplate.result(this.execute(psc, ps -> {
            if (pss != null) {
                pss.setValues(ps);
            }
            ResultSet rs = ps.executeQuery();
            Connection con = ps.getConnection();
            return (Stream)new ResultSetSpliterator(rs, rowMapper).stream().onClose(() -> {
                JdbcUtils.closeResultSet(rs);
                if (pss instanceof ParameterDisposer) {
                    ((ParameterDisposer)((Object)pss)).cleanupParameters();
                }
                JdbcUtils.closeStatement(ps);
                DataSourceUtils.releaseConnection(con, this.getDataSource());
            });
        }, false));
    }

    @Override
    public <T> Stream<T> queryForStream(PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException {
        return this.queryForStream(psc, null, rowMapper);
    }

    @Override
    public <T> Stream<T> queryForStream(String sql, @Nullable PreparedStatementSetter pss, RowMapper<T> rowMapper) throws DataAccessException {
        return this.queryForStream(new SimplePreparedStatementCreator(sql), pss, rowMapper);
    }

    @Override
    public <T> Stream<T> queryForStream(String sql, RowMapper<T> rowMapper, Object ... args) throws DataAccessException {
        return this.queryForStream(new SimplePreparedStatementCreator(sql), this.newArgPreparedStatementSetter(args), rowMapper);
    }

    @Override
    @Nullable
    public <T> T queryForObject(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException {
        List results = (List)this.query(sql, args, argTypes, new RowMapperResultSetExtractor<T>(rowMapper, 1));
        return (T)DataAccessUtils.nullableSingleResult((Collection)results);
    }

    @Override
    @Deprecated
    @Nullable
    public <T> T queryForObject(String sql, @Nullable Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        List results = (List)this.query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper, 1));
        return (T)DataAccessUtils.nullableSingleResult((Collection)results);
    }

    @Override
    @Nullable
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object ... args) throws DataAccessException {
        List results = (List)this.query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper, 1));
        return (T)DataAccessUtils.nullableSingleResult((Collection)results);
    }

    @Override
    @Nullable
    public <T> T queryForObject(String sql, Object[] args, int[] argTypes, Class<T> requiredType) throws DataAccessException {
        return this.queryForObject(sql, args, argTypes, this.getSingleColumnRowMapper(requiredType));
    }

    @Override
    @Deprecated
    public <T> T queryForObject(String sql, @Nullable Object[] args, Class<T> requiredType) throws DataAccessException {
        return this.queryForObject(sql, args, this.getSingleColumnRowMapper(requiredType));
    }

    @Override
    public <T> T queryForObject(String sql, Class<T> requiredType, Object ... args) throws DataAccessException {
        return this.queryForObject(sql, args, this.getSingleColumnRowMapper(requiredType));
    }

    @Override
    public Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        return JdbcTemplate.result(this.queryForObject(sql, args, argTypes, this.getColumnMapRowMapper()));
    }

    @Override
    public Map<String, Object> queryForMap(String sql, Object ... args) throws DataAccessException {
        return JdbcTemplate.result(this.queryForObject(sql, args, this.getColumnMapRowMapper()));
    }

    @Override
    public <T> List<T> queryForList(String sql, Object[] args, int[] argTypes, Class<T> elementType) throws DataAccessException {
        return this.query(sql, args, argTypes, this.getSingleColumnRowMapper(elementType));
    }

    @Override
    @Deprecated
    public <T> List<T> queryForList(String sql, @Nullable Object[] args, Class<T> elementType) throws DataAccessException {
        return this.query(sql, args, this.getSingleColumnRowMapper(elementType));
    }

    @Override
    public <T> List<T> queryForList(String sql, Class<T> elementType, Object ... args) throws DataAccessException {
        return this.query(sql, args, this.getSingleColumnRowMapper(elementType));
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        return this.query(sql, args, argTypes, this.getColumnMapRowMapper());
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql, Object ... args) throws DataAccessException {
        return this.query(sql, args, this.getColumnMapRowMapper());
    }

    @Override
    public SqlRowSet queryForRowSet(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        return JdbcTemplate.result(this.query(sql, args, argTypes, new SqlRowSetResultSetExtractor()));
    }

    @Override
    public SqlRowSet queryForRowSet(String sql, Object ... args) throws DataAccessException {
        return JdbcTemplate.result(this.query(sql, args, new SqlRowSetResultSetExtractor()));
    }

    protected int update(PreparedStatementCreator psc, @Nullable PreparedStatementSetter pss) throws DataAccessException {
        this.logger.debug((Object)"Executing prepared SQL update");
        return JdbcTemplate.updateCount(this.execute(psc, ps -> {
            try {
                if (pss != null) {
                    pss.setValues(ps);
                }
                int rows = ps.executeUpdate();
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("SQL update affected " + rows + " rows"));
                }
                Integer n = rows;
                return n;
            }
            finally {
                if (pss instanceof ParameterDisposer) {
                    ((ParameterDisposer)((Object)pss)).cleanupParameters();
                }
            }
        }, true));
    }

    @Override
    public int update(PreparedStatementCreator psc) throws DataAccessException {
        return this.update(psc, (PreparedStatementSetter)null);
    }

    @Override
    public int update(PreparedStatementCreator psc, KeyHolder generatedKeyHolder) throws DataAccessException {
        Assert.notNull((Object)generatedKeyHolder, (String)"KeyHolder must not be null");
        this.logger.debug((Object)"Executing SQL update and returning generated keys");
        return JdbcTemplate.updateCount(this.execute(psc, ps -> {
            int rows = ps.executeUpdate();
            List<Map<String, Object>> generatedKeys = generatedKeyHolder.getKeyList();
            generatedKeys.clear();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys != null) {
                try {
                    RowMapperResultSetExtractor<Map<String, Object>> rse = new RowMapperResultSetExtractor<Map<String, Object>>(this.getColumnMapRowMapper(), 1);
                    generatedKeys.addAll((Collection)JdbcTemplate.result(rse.extractData(keys)));
                }
                finally {
                    JdbcUtils.closeResultSet(keys);
                }
            }
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("SQL update affected " + rows + " rows and returned " + generatedKeys.size() + " keys"));
            }
            return rows;
        }, true));
    }

    @Override
    public int update(String sql, @Nullable PreparedStatementSetter pss) throws DataAccessException {
        return this.update((PreparedStatementCreator)new SimplePreparedStatementCreator(sql), pss);
    }

    @Override
    public int update(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        return this.update(sql, this.newArgTypePreparedStatementSetter(args, argTypes));
    }

    @Override
    public int update(String sql, Object ... args) throws DataAccessException {
        return this.update(sql, this.newArgPreparedStatementSetter(args));
    }

    @Override
    public int[] batchUpdate(String sql, BatchPreparedStatementSetter pss) throws DataAccessException {
        int[] result;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Executing SQL batch update [" + sql + "]"));
        }
        Assert.state(((result = this.execute(sql, (PreparedStatement ps) -> {
            try {
                InterruptibleBatchPreparedStatementSetter ipss;
                int batchSize = pss.getBatchSize();
                InterruptibleBatchPreparedStatementSetter interruptibleBatchPreparedStatementSetter = ipss = pss instanceof InterruptibleBatchPreparedStatementSetter ? (InterruptibleBatchPreparedStatementSetter)pss : null;
                if (JdbcUtils.supportsBatchUpdates(ps.getConnection())) {
                    for (int i = 0; i < batchSize; ++i) {
                        pss.setValues(ps, i);
                        if (ipss != null && ipss.isBatchExhausted(i)) break;
                        ps.addBatch();
                    }
                    int[] i = ps.executeBatch();
                    return i;
                }
                ArrayList<Integer> rowsAffected = new ArrayList<Integer>();
                for (int i = 0; i < batchSize; ++i) {
                    pss.setValues(ps, i);
                    if (ipss != null && ipss.isBatchExhausted(i)) break;
                    rowsAffected.add(ps.executeUpdate());
                }
                int[] rowsAffectedArray = new int[rowsAffected.size()];
                for (int i = 0; i < rowsAffectedArray.length; ++i) {
                    rowsAffectedArray[i] = (Integer)rowsAffected.get(i);
                }
                int[] nArray = rowsAffectedArray;
                return nArray;
            }
            finally {
                if (pss instanceof ParameterDisposer) {
                    ((ParameterDisposer)((Object)pss)).cleanupParameters();
                }
            }
        })) != null ? 1 : 0) != 0, (String)"No result array");
        return result;
    }

    @Override
    public int[] batchUpdate(String sql, List<Object[]> batchArgs) throws DataAccessException {
        return this.batchUpdate(sql, batchArgs, new int[0]);
    }

    @Override
    public int[] batchUpdate(String sql, final List<Object[]> batchArgs, final int[] argTypes) throws DataAccessException {
        if (batchArgs.isEmpty()) {
            return new int[0];
        }
        return this.batchUpdate(sql, new BatchPreparedStatementSetter(){

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Object[] values = (Object[])batchArgs.get(i);
                int colIndex = 0;
                for (Object value : values) {
                    ++colIndex;
                    if (value instanceof SqlParameterValue) {
                        SqlParameterValue paramValue = (SqlParameterValue)value;
                        StatementCreatorUtils.setParameterValue(ps, colIndex, paramValue, paramValue.getValue());
                        continue;
                    }
                    int colType = argTypes.length < colIndex ? Integer.MIN_VALUE : argTypes[colIndex - 1];
                    StatementCreatorUtils.setParameterValue(ps, colIndex, colType, value);
                }
            }

            @Override
            public int getBatchSize() {
                return batchArgs.size();
            }
        });
    }

    @Override
    public <T> int[][] batchUpdate(String sql, Collection<T> batchArgs, int batchSize, ParameterizedPreparedStatementSetter<T> pss) throws DataAccessException {
        int[][] result;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Executing SQL batch update [" + sql + "] with a batch size of " + batchSize));
        }
        Assert.state(((result = this.execute(sql, (PreparedStatement ps) -> {
            ArrayList<int[]> rowsAffected = new ArrayList<int[]>();
            try {
                boolean batchSupported = JdbcUtils.supportsBatchUpdates(ps.getConnection());
                int n = 0;
                for (Object obj : batchArgs) {
                    pss.setValues(ps, obj);
                    ++n;
                    if (batchSupported) {
                        ps.addBatch();
                        if (n % batchSize != 0 && n != batchArgs.size()) continue;
                        if (this.logger.isTraceEnabled()) {
                            int batchIdx = n % batchSize == 0 ? n / batchSize : n / batchSize + 1;
                            int items = n - (n % batchSize == 0 ? n / batchSize - 1 : n / batchSize) * batchSize;
                            this.logger.trace((Object)("Sending SQL batch update #" + batchIdx + " with " + items + " items"));
                        }
                        rowsAffected.add(ps.executeBatch());
                        continue;
                    }
                    int i = ps.executeUpdate();
                    rowsAffected.add(new int[]{i});
                }
                int[][] result1 = new int[rowsAffected.size()][];
                for (int i = 0; i < result1.length; ++i) {
                    result1[i] = (int[])rowsAffected.get(i);
                }
                int[][] nArrayArray = result1;
                return nArrayArray;
            }
            finally {
                if (pss instanceof ParameterDisposer) {
                    ((ParameterDisposer)((Object)pss)).cleanupParameters();
                }
            }
        })) != null ? 1 : 0) != 0, (String)"No result array");
        return result;
    }

    @Override
    @Nullable
    public <T> T execute(CallableStatementCreator csc, CallableStatementCallback<T> action) throws DataAccessException {
        Assert.notNull((Object)csc, (String)"CallableStatementCreator must not be null");
        Assert.notNull(action, (String)"Callback object must not be null");
        if (this.logger.isDebugEnabled()) {
            String sql = JdbcTemplate.getSql(csc);
            this.logger.debug((Object)("Calling stored procedure" + (sql != null ? " [" + sql + "]" : "")));
        }
        Connection con = DataSourceUtils.getConnection(this.obtainDataSource());
        CallableStatement cs = null;
        try {
            cs = csc.createCallableStatement(con);
            this.applyStatementSettings(cs);
            T result = action.doInCallableStatement(cs);
            this.handleWarnings(cs);
            T t = result;
            return t;
        }
        catch (SQLException ex) {
            if (csc instanceof ParameterDisposer) {
                ((ParameterDisposer)((Object)csc)).cleanupParameters();
            }
            if (cs != null) {
                this.handleWarnings(cs, ex);
            }
            String sql = JdbcTemplate.getSql(csc);
            csc = null;
            JdbcUtils.closeStatement(cs);
            cs = null;
            DataSourceUtils.releaseConnection(con, this.getDataSource());
            con = null;
            throw this.translateException("CallableStatementCallback", sql, ex);
        }
        finally {
            if (csc instanceof ParameterDisposer) {
                ((ParameterDisposer)((Object)csc)).cleanupParameters();
            }
            JdbcUtils.closeStatement(cs);
            DataSourceUtils.releaseConnection(con, this.getDataSource());
        }
    }

    @Override
    @Nullable
    public <T> T execute(String callString, CallableStatementCallback<T> action) throws DataAccessException {
        return this.execute(new SimpleCallableStatementCreator(callString), action);
    }

    @Override
    public Map<String, Object> call(CallableStatementCreator csc, List<SqlParameter> declaredParameters) throws DataAccessException {
        ArrayList<SqlParameter> updateCountParameters = new ArrayList<SqlParameter>();
        ArrayList<SqlParameter> resultSetParameters = new ArrayList<SqlParameter>();
        ArrayList<SqlParameter> callParameters = new ArrayList<SqlParameter>();
        for (SqlParameter parameter : declaredParameters) {
            if (parameter.isResultsParameter()) {
                if (parameter instanceof SqlReturnResultSet) {
                    resultSetParameters.add(parameter);
                    continue;
                }
                updateCountParameters.add(parameter);
                continue;
            }
            callParameters.add(parameter);
        }
        Map result = this.execute(csc, (CallableStatement cs) -> {
            boolean retVal = cs.execute();
            int updateCount = cs.getUpdateCount();
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("CallableStatement.execute() returned '" + retVal + "'"));
                this.logger.trace((Object)("CallableStatement.getUpdateCount() returned " + updateCount));
            }
            Map<String, Object> resultsMap = this.createResultsMap();
            if (retVal || updateCount != -1) {
                resultsMap.putAll(this.extractReturnedResults(cs, updateCountParameters, resultSetParameters, updateCount));
            }
            resultsMap.putAll(this.extractOutputParameters(cs, callParameters));
            return resultsMap;
        });
        Assert.state((result != null ? 1 : 0) != 0, (String)"No result map");
        return result;
    }

    protected Map<String, Object> extractReturnedResults(CallableStatement cs, @Nullable List<SqlParameter> updateCountParameters, @Nullable List<SqlParameter> resultSetParameters, int updateCount) throws SQLException {
        LinkedHashMap<String, Object> results = new LinkedHashMap<String, Object>(4);
        int rsIndex = 0;
        int updateIndex = 0;
        if (!this.skipResultsProcessing) {
            boolean moreResults;
            do {
                if (updateCount == -1) {
                    if (resultSetParameters != null && resultSetParameters.size() > rsIndex) {
                        SqlReturnResultSet declaredRsParam = (SqlReturnResultSet)resultSetParameters.get(rsIndex);
                        results.putAll(this.processResultSet(cs.getResultSet(), declaredRsParam));
                        ++rsIndex;
                    } else if (!this.skipUndeclaredResults) {
                        String rsName = RETURN_RESULT_SET_PREFIX + (rsIndex + 1);
                        SqlReturnResultSet undeclaredRsParam = new SqlReturnResultSet(rsName, this.getColumnMapRowMapper());
                        if (this.logger.isTraceEnabled()) {
                            this.logger.trace((Object)("Added default SqlReturnResultSet parameter named '" + rsName + "'"));
                        }
                        results.putAll(this.processResultSet(cs.getResultSet(), undeclaredRsParam));
                        ++rsIndex;
                    }
                } else if (updateCountParameters != null && updateCountParameters.size() > updateIndex) {
                    SqlReturnUpdateCount ucParam = (SqlReturnUpdateCount)updateCountParameters.get(updateIndex);
                    String declaredUcName = ucParam.getName();
                    results.put(declaredUcName, updateCount);
                    ++updateIndex;
                } else if (!this.skipUndeclaredResults) {
                    String undeclaredName = RETURN_UPDATE_COUNT_PREFIX + (updateIndex + 1);
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace((Object)("Added default SqlReturnUpdateCount parameter named '" + undeclaredName + "'"));
                    }
                    results.put(undeclaredName, updateCount);
                    ++updateIndex;
                }
                moreResults = cs.getMoreResults();
                updateCount = cs.getUpdateCount();
                if (!this.logger.isTraceEnabled()) continue;
                this.logger.trace((Object)("CallableStatement.getUpdateCount() returned " + updateCount));
            } while (moreResults || updateCount != -1);
        }
        return results;
    }

    protected Map<String, Object> extractOutputParameters(CallableStatement cs, List<SqlParameter> parameters) throws SQLException {
        LinkedHashMap results = CollectionUtils.newLinkedHashMap((int)parameters.size());
        int sqlColIndex = 1;
        for (SqlParameter param : parameters) {
            if (param instanceof SqlOutParameter) {
                Object out;
                SqlOutParameter outParam = (SqlOutParameter)param;
                Assert.state((outParam.getName() != null ? 1 : 0) != 0, (String)"Anonymous parameters not allowed");
                SqlReturnType returnType = outParam.getSqlReturnType();
                if (returnType != null) {
                    out = returnType.getTypeValue(cs, sqlColIndex, outParam.getSqlType(), outParam.getTypeName());
                    results.put(outParam.getName(), out);
                } else {
                    out = cs.getObject(sqlColIndex);
                    if (out instanceof ResultSet) {
                        if (outParam.isResultSetSupported()) {
                            results.putAll(this.processResultSet((ResultSet)out, outParam));
                        } else {
                            String rsName = outParam.getName();
                            SqlReturnResultSet rsParam = new SqlReturnResultSet(rsName, this.getColumnMapRowMapper());
                            results.putAll(this.processResultSet((ResultSet)out, rsParam));
                            if (this.logger.isTraceEnabled()) {
                                this.logger.trace((Object)("Added default SqlReturnResultSet parameter named '" + rsName + "'"));
                            }
                        }
                    } else {
                        results.put(outParam.getName(), out);
                    }
                }
            }
            if (param.isResultsParameter()) continue;
            ++sqlColIndex;
        }
        return results;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Map<String, Object> processResultSet(@Nullable ResultSet rs, ResultSetSupportingSqlParameter param) throws SQLException {
        if (rs != null) {
            try {
                if (param.getRowMapper() != null) {
                    RowMapper<?> rowMapper = param.getRowMapper();
                    Object data = new RowMapperResultSetExtractor(rowMapper).extractData(rs);
                    Map<String, Object> map = Collections.singletonMap(param.getName(), data);
                    return map;
                }
                if (param.getRowCallbackHandler() != null) {
                    RowCallbackHandler rch = param.getRowCallbackHandler();
                    new RowCallbackHandlerResultSetExtractor(rch).extractData(rs);
                    Map<String, Object> map = Collections.singletonMap(param.getName(), "ResultSet returned from stored procedure was processed");
                    return map;
                }
                if (param.getResultSetExtractor() != null) {
                    Object data = param.getResultSetExtractor().extractData(rs);
                    Map<String, Object> map = Collections.singletonMap(param.getName(), data);
                    return map;
                }
            }
            finally {
                JdbcUtils.closeResultSet(rs);
            }
        }
        return Collections.emptyMap();
    }

    protected RowMapper<Map<String, Object>> getColumnMapRowMapper() {
        return new ColumnMapRowMapper();
    }

    protected <T> RowMapper<T> getSingleColumnRowMapper(Class<T> requiredType) {
        return new SingleColumnRowMapper<T>(requiredType);
    }

    protected Map<String, Object> createResultsMap() {
        if (this.isResultsMapCaseInsensitive()) {
            return new LinkedCaseInsensitiveMap();
        }
        return new LinkedHashMap<String, Object>();
    }

    protected void applyStatementSettings(Statement stmt) throws SQLException {
        int maxRows;
        int fetchSize = this.getFetchSize();
        if (fetchSize != -1) {
            stmt.setFetchSize(fetchSize);
        }
        if ((maxRows = this.getMaxRows()) != -1) {
            stmt.setMaxRows(maxRows);
        }
        DataSourceUtils.applyTimeout(stmt, this.getDataSource(), this.getQueryTimeout());
    }

    protected PreparedStatementSetter newArgPreparedStatementSetter(@Nullable Object[] args) {
        return new ArgumentPreparedStatementSetter(args);
    }

    protected PreparedStatementSetter newArgTypePreparedStatementSetter(Object[] args, int[] argTypes) {
        return new ArgumentTypePreparedStatementSetter(args, argTypes);
    }

    protected void handleWarnings(Statement stmt, SQLException ex) {
        try {
            this.handleWarnings(stmt);
        }
        catch (SQLWarningException nonIgnoredWarning) {
            ex.setNextException(nonIgnoredWarning.getSQLWarning());
        }
        catch (SQLException warningsEx) {
            this.logger.debug((Object)"Failed to retrieve warnings", (Throwable)warningsEx);
        }
        catch (Throwable warningsEx) {
            this.logger.debug((Object)"Failed to process warnings", warningsEx);
        }
    }

    protected void handleWarnings(Statement stmt) throws SQLException, SQLWarningException {
        if (this.isIgnoreWarnings()) {
            if (this.logger.isDebugEnabled()) {
                for (SQLWarning warningToLog = stmt.getWarnings(); warningToLog != null; warningToLog = warningToLog.getNextWarning()) {
                    this.logger.debug((Object)("SQLWarning ignored: SQL state '" + warningToLog.getSQLState() + "', error code '" + warningToLog.getErrorCode() + "', message [" + warningToLog.getMessage() + "]"));
                }
            }
        } else {
            this.handleWarnings(stmt.getWarnings());
        }
    }

    protected void handleWarnings(@Nullable SQLWarning warning) throws SQLWarningException {
        if (warning != null) {
            throw new SQLWarningException("Warning not ignored", warning);
        }
    }

    protected DataAccessException translateException(String task, @Nullable String sql, SQLException ex) {
        DataAccessException dae = this.getExceptionTranslator().translate(task, sql, ex);
        return dae != null ? dae : new UncategorizedSQLException(task, sql, ex);
    }

    @Nullable
    private static String getSql(Object sqlProvider) {
        if (sqlProvider instanceof SqlProvider) {
            return ((SqlProvider)sqlProvider).getSql();
        }
        return null;
    }

    private static <T> T result(@Nullable T result) {
        Assert.state((result != null ? 1 : 0) != 0, (String)"No result");
        return result;
    }

    private static int updateCount(@Nullable Integer result) {
        Assert.state((result != null ? 1 : 0) != 0, (String)"No update count");
        return result;
    }

    private static class ResultSetSpliterator<T>
    implements Spliterator<T> {
        private final ResultSet rs;
        private final RowMapper<T> rowMapper;
        private int rowNum = 0;

        public ResultSetSpliterator(ResultSet rs, RowMapper<T> rowMapper) {
            this.rs = rs;
            this.rowMapper = rowMapper;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            try {
                if (this.rs.next()) {
                    action.accept(this.rowMapper.mapRow(this.rs, this.rowNum++));
                    return true;
                }
                return false;
            }
            catch (SQLException ex) {
                throw new InvalidResultSetAccessException(ex);
            }
        }

        @Override
        @Nullable
        public Spliterator<T> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            return 16;
        }

        public Stream<T> stream() {
            return StreamSupport.stream(this, false);
        }
    }

    private static class RowCallbackHandlerResultSetExtractor
    implements ResultSetExtractor<Object> {
        private final RowCallbackHandler rch;

        public RowCallbackHandlerResultSetExtractor(RowCallbackHandler rch) {
            this.rch = rch;
        }

        @Override
        @Nullable
        public Object extractData(ResultSet rs) throws SQLException {
            while (rs.next()) {
                this.rch.processRow(rs);
            }
            return null;
        }
    }

    private static class SimpleCallableStatementCreator
    implements CallableStatementCreator,
    SqlProvider {
        private final String callString;

        public SimpleCallableStatementCreator(String callString) {
            Assert.notNull((Object)callString, (String)"Call string must not be null");
            this.callString = callString;
        }

        @Override
        public CallableStatement createCallableStatement(Connection con) throws SQLException {
            return con.prepareCall(this.callString);
        }

        @Override
        public String getSql() {
            return this.callString;
        }
    }

    private static class SimplePreparedStatementCreator
    implements PreparedStatementCreator,
    SqlProvider {
        private final String sql;

        public SimplePreparedStatementCreator(String sql) {
            Assert.notNull((Object)sql, (String)"SQL must not be null");
            this.sql = sql;
        }

        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            return con.prepareStatement(this.sql);
        }

        @Override
        public String getSql() {
            return this.sql;
        }
    }

    private class CloseSuppressingInvocationHandler
    implements InvocationHandler {
        private final Connection target;

        public CloseSuppressingInvocationHandler(Connection target) {
            this.target = target;
        }

        @Override
        @Nullable
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "equals": {
                    return proxy == args[0];
                }
                case "hashCode": {
                    return System.identityHashCode(proxy);
                }
                case "close": {
                    return null;
                }
                case "isClosed": {
                    return false;
                }
                case "getTargetConnection": {
                    return this.target;
                }
                case "unwrap": {
                    return ((Class)args[0]).isInstance(proxy) ? proxy : this.target.unwrap((Class)args[0]);
                }
                case "isWrapperFor": {
                    return ((Class)args[0]).isInstance(proxy) || this.target.isWrapperFor((Class)args[0]);
                }
            }
            try {
                Object retVal = method.invoke((Object)this.target, args);
                if (retVal instanceof Statement) {
                    JdbcTemplate.this.applyStatementSettings((Statement)retVal);
                }
                return retVal;
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
}

