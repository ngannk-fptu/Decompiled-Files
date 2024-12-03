/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.dao.DataIntegrityViolationException
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.dao.InvalidDataAccessResourceUsageException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.core.simple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.metadata.TableMetaDataContext;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractJdbcInsert {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final JdbcTemplate jdbcTemplate;
    private final TableMetaDataContext tableMetaDataContext = new TableMetaDataContext();
    private final List<String> declaredColumns = new ArrayList<String>();
    private String[] generatedKeyNames = new String[0];
    private volatile boolean compiled;
    private String insertString = "";
    private int[] insertTypes = new int[0];

    protected AbstractJdbcInsert(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    protected AbstractJdbcInsert(JdbcTemplate jdbcTemplate) {
        Assert.notNull((Object)jdbcTemplate, (String)"JdbcTemplate must not be null");
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    public void setTableName(@Nullable String tableName) {
        this.checkIfConfigurationModificationIsAllowed();
        this.tableMetaDataContext.setTableName(tableName);
    }

    @Nullable
    public String getTableName() {
        return this.tableMetaDataContext.getTableName();
    }

    public void setSchemaName(@Nullable String schemaName) {
        this.checkIfConfigurationModificationIsAllowed();
        this.tableMetaDataContext.setSchemaName(schemaName);
    }

    @Nullable
    public String getSchemaName() {
        return this.tableMetaDataContext.getSchemaName();
    }

    public void setCatalogName(@Nullable String catalogName) {
        this.checkIfConfigurationModificationIsAllowed();
        this.tableMetaDataContext.setCatalogName(catalogName);
    }

    @Nullable
    public String getCatalogName() {
        return this.tableMetaDataContext.getCatalogName();
    }

    public void setColumnNames(List<String> columnNames) {
        this.checkIfConfigurationModificationIsAllowed();
        this.declaredColumns.clear();
        this.declaredColumns.addAll(columnNames);
    }

    public List<String> getColumnNames() {
        return Collections.unmodifiableList(this.declaredColumns);
    }

    public void setGeneratedKeyName(String generatedKeyName) {
        this.checkIfConfigurationModificationIsAllowed();
        this.generatedKeyNames = new String[]{generatedKeyName};
    }

    public void setGeneratedKeyNames(String ... generatedKeyNames) {
        this.checkIfConfigurationModificationIsAllowed();
        this.generatedKeyNames = generatedKeyNames;
    }

    public String[] getGeneratedKeyNames() {
        return this.generatedKeyNames;
    }

    public void setAccessTableColumnMetaData(boolean accessTableColumnMetaData) {
        this.tableMetaDataContext.setAccessTableColumnMetaData(accessTableColumnMetaData);
    }

    public void setOverrideIncludeSynonymsDefault(boolean override) {
        this.tableMetaDataContext.setOverrideIncludeSynonymsDefault(override);
    }

    public String getInsertString() {
        return this.insertString;
    }

    public int[] getInsertTypes() {
        return this.insertTypes;
    }

    public final synchronized void compile() throws InvalidDataAccessApiUsageException {
        if (!this.isCompiled()) {
            if (this.getTableName() == null) {
                throw new InvalidDataAccessApiUsageException("Table name is required");
            }
            try {
                this.jdbcTemplate.afterPropertiesSet();
            }
            catch (IllegalArgumentException ex) {
                throw new InvalidDataAccessApiUsageException(ex.getMessage());
            }
            this.compileInternal();
            this.compiled = true;
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("JdbcInsert for table [" + this.getTableName() + "] compiled"));
            }
        }
    }

    protected void compileInternal() {
        DataSource dataSource = this.getJdbcTemplate().getDataSource();
        Assert.state((dataSource != null ? 1 : 0) != 0, (String)"No DataSource set");
        this.tableMetaDataContext.processMetaData(dataSource, this.getColumnNames(), this.getGeneratedKeyNames());
        this.insertString = this.tableMetaDataContext.createInsertString(this.getGeneratedKeyNames());
        this.insertTypes = this.tableMetaDataContext.createInsertTypes();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Compiled insert object: insert string is [" + this.insertString + "]"));
        }
        this.onCompileInternal();
    }

    protected void onCompileInternal() {
    }

    public boolean isCompiled() {
        return this.compiled;
    }

    protected void checkCompiled() {
        if (!this.isCompiled()) {
            this.logger.debug((Object)"JdbcInsert not compiled before execution - invoking compile");
            this.compile();
        }
    }

    protected void checkIfConfigurationModificationIsAllowed() {
        if (this.isCompiled()) {
            throw new InvalidDataAccessApiUsageException("Configuration cannot be altered once the class has been compiled or used");
        }
    }

    protected int doExecute(Map<String, ?> args) {
        this.checkCompiled();
        List<Object> values = this.matchInParameterValuesWithInsertColumns(args);
        return this.executeInsertInternal(values);
    }

    protected int doExecute(SqlParameterSource parameterSource) {
        this.checkCompiled();
        List<Object> values = this.matchInParameterValuesWithInsertColumns(parameterSource);
        return this.executeInsertInternal(values);
    }

    private int executeInsertInternal(List<?> values) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("The following parameters are used for insert " + this.getInsertString() + " with: " + values));
        }
        return this.getJdbcTemplate().update(this.getInsertString(), values.toArray(), this.getInsertTypes());
    }

    protected Number doExecuteAndReturnKey(Map<String, ?> args) {
        this.checkCompiled();
        List<Object> values = this.matchInParameterValuesWithInsertColumns(args);
        return this.executeInsertAndReturnKeyInternal(values);
    }

    protected Number doExecuteAndReturnKey(SqlParameterSource parameterSource) {
        this.checkCompiled();
        List<Object> values = this.matchInParameterValuesWithInsertColumns(parameterSource);
        return this.executeInsertAndReturnKeyInternal(values);
    }

    protected KeyHolder doExecuteAndReturnKeyHolder(Map<String, ?> args) {
        this.checkCompiled();
        List<Object> values = this.matchInParameterValuesWithInsertColumns(args);
        return this.executeInsertAndReturnKeyHolderInternal(values);
    }

    protected KeyHolder doExecuteAndReturnKeyHolder(SqlParameterSource parameterSource) {
        this.checkCompiled();
        List<Object> values = this.matchInParameterValuesWithInsertColumns(parameterSource);
        return this.executeInsertAndReturnKeyHolderInternal(values);
    }

    private Number executeInsertAndReturnKeyInternal(List<?> values) {
        KeyHolder kh = this.executeInsertAndReturnKeyHolderInternal(values);
        if (kh.getKey() != null) {
            return kh.getKey();
        }
        throw new DataIntegrityViolationException("Unable to retrieve the generated key for the insert: " + this.getInsertString());
    }

    private KeyHolder executeInsertAndReturnKeyHolderInternal(List<?> values) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("The following parameters are used for call " + this.getInsertString() + " with: " + values));
        }
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        if (this.tableMetaDataContext.isGetGeneratedKeysSupported()) {
            this.getJdbcTemplate().update(con -> {
                PreparedStatement ps = this.prepareStatementForGeneratedKeys(con);
                this.setParameterValues(ps, values, this.getInsertTypes());
                return ps;
            }, keyHolder);
        } else {
            if (!this.tableMetaDataContext.isGetGeneratedKeysSimulated()) {
                throw new InvalidDataAccessResourceUsageException("The getGeneratedKeys feature is not supported by this database");
            }
            if (this.getGeneratedKeyNames().length < 1) {
                throw new InvalidDataAccessApiUsageException("Generated Key Name(s) not specified. Using the generated keys features requires specifying the name(s) of the generated column(s)");
            }
            if (this.getGeneratedKeyNames().length > 1) {
                throw new InvalidDataAccessApiUsageException("Current database only supports retrieving the key for a single column. There are " + this.getGeneratedKeyNames().length + " columns specified: " + Arrays.toString(this.getGeneratedKeyNames()));
            }
            Assert.state((this.getTableName() != null ? 1 : 0) != 0, (String)"No table name set");
            String keyQuery = this.tableMetaDataContext.getSimpleQueryForGetGeneratedKey(this.getTableName(), this.getGeneratedKeyNames()[0]);
            Assert.state((keyQuery != null ? 1 : 0) != 0, (String)"Query for simulating get generated keys must not be null");
            if (keyQuery.toUpperCase().startsWith("RETURNING")) {
                Long key = this.getJdbcTemplate().queryForObject(this.getInsertString() + " " + keyQuery, Long.class, values.toArray());
                HashMap<String, Long> keys = new HashMap<String, Long>(2);
                keys.put(this.getGeneratedKeyNames()[0], key);
                keyHolder.getKeyList().add(keys);
            } else {
                this.getJdbcTemplate().execute(con -> {
                    PreparedStatement ps = null;
                    try {
                        ps = con.prepareStatement(this.getInsertString());
                        this.setParameterValues(ps, values, this.getInsertTypes());
                        ps.executeUpdate();
                    }
                    catch (Throwable throwable) {
                        JdbcUtils.closeStatement(ps);
                        throw throwable;
                    }
                    JdbcUtils.closeStatement(ps);
                    Statement keyStmt = null;
                    ResultSet rs = null;
                    try {
                        keyStmt = con.createStatement();
                        rs = keyStmt.executeQuery(keyQuery);
                        if (rs.next()) {
                            long key = rs.getLong(1);
                            HashMap<String, Long> keys = new HashMap<String, Long>(2);
                            keys.put(this.getGeneratedKeyNames()[0], key);
                            keyHolder.getKeyList().add(keys);
                        }
                    }
                    catch (Throwable throwable) {
                        JdbcUtils.closeResultSet(rs);
                        JdbcUtils.closeStatement(keyStmt);
                        throw throwable;
                    }
                    JdbcUtils.closeResultSet(rs);
                    JdbcUtils.closeStatement(keyStmt);
                    return null;
                });
            }
        }
        return keyHolder;
    }

    private PreparedStatement prepareStatementForGeneratedKeys(Connection con) throws SQLException {
        PreparedStatement ps;
        if (this.getGeneratedKeyNames().length < 1) {
            throw new InvalidDataAccessApiUsageException("Generated Key Name(s) not specified. Using the generated keys features requires specifying the name(s) of the generated column(s).");
        }
        if (this.tableMetaDataContext.isGeneratedKeysColumnNameArraySupported()) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)"Using generated keys support with array of column names.");
            }
            ps = con.prepareStatement(this.getInsertString(), this.getGeneratedKeyNames());
        } else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)"Using generated keys support with Statement.RETURN_GENERATED_KEYS.");
            }
            ps = con.prepareStatement(this.getInsertString(), 1);
        }
        return ps;
    }

    protected int[] doExecuteBatch(Map<String, ?> ... batch) {
        this.checkCompiled();
        ArrayList<List<Object>> batchValues = new ArrayList<List<Object>>(batch.length);
        for (Map<String, ?> args : batch) {
            batchValues.add(this.matchInParameterValuesWithInsertColumns(args));
        }
        return this.executeBatchInternal(batchValues);
    }

    protected int[] doExecuteBatch(SqlParameterSource ... batch) {
        this.checkCompiled();
        ArrayList<List<Object>> batchValues = new ArrayList<List<Object>>(batch.length);
        for (SqlParameterSource parameterSource : batch) {
            batchValues.add(this.matchInParameterValuesWithInsertColumns(parameterSource));
        }
        return this.executeBatchInternal(batchValues);
    }

    private int[] executeBatchInternal(final List<List<Object>> batchValues) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Executing statement " + this.getInsertString() + " with batch of size: " + batchValues.size()));
        }
        return this.getJdbcTemplate().batchUpdate(this.getInsertString(), new BatchPreparedStatementSetter(){

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                AbstractJdbcInsert.this.setParameterValues(ps, (List)batchValues.get(i), AbstractJdbcInsert.this.getInsertTypes());
            }

            @Override
            public int getBatchSize() {
                return batchValues.size();
            }
        });
    }

    private void setParameterValues(PreparedStatement preparedStatement, List<?> values, int ... columnTypes) throws SQLException {
        int colIndex = 0;
        for (Object value : values) {
            if (columnTypes == null || ++colIndex > columnTypes.length) {
                StatementCreatorUtils.setParameterValue(preparedStatement, colIndex, Integer.MIN_VALUE, value);
                continue;
            }
            StatementCreatorUtils.setParameterValue(preparedStatement, colIndex, columnTypes[colIndex - 1], value);
        }
    }

    protected List<Object> matchInParameterValuesWithInsertColumns(SqlParameterSource parameterSource) {
        return this.tableMetaDataContext.matchInParameterValuesWithInsertColumns(parameterSource);
    }

    protected List<Object> matchInParameterValuesWithInsertColumns(Map<String, ?> args) {
        return this.tableMetaDataContext.matchInParameterValuesWithInsertColumns(args);
    }
}

