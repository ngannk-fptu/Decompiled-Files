/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core.simple;

import java.util.Arrays;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.AbstractJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.jdbc.support.KeyHolder;

public class SimpleJdbcInsert
extends AbstractJdbcInsert
implements SimpleJdbcInsertOperations {
    public SimpleJdbcInsert(DataSource dataSource) {
        super(dataSource);
    }

    public SimpleJdbcInsert(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public SimpleJdbcInsert withTableName(String tableName) {
        this.setTableName(tableName);
        return this;
    }

    @Override
    public SimpleJdbcInsert withSchemaName(String schemaName) {
        this.setSchemaName(schemaName);
        return this;
    }

    @Override
    public SimpleJdbcInsert withCatalogName(String catalogName) {
        this.setCatalogName(catalogName);
        return this;
    }

    @Override
    public SimpleJdbcInsert usingColumns(String ... columnNames) {
        this.setColumnNames(Arrays.asList(columnNames));
        return this;
    }

    @Override
    public SimpleJdbcInsert usingGeneratedKeyColumns(String ... columnNames) {
        this.setGeneratedKeyNames(columnNames);
        return this;
    }

    @Override
    public SimpleJdbcInsertOperations withoutTableColumnMetaDataAccess() {
        this.setAccessTableColumnMetaData(false);
        return this;
    }

    @Override
    public SimpleJdbcInsertOperations includeSynonymsForTableColumnMetaData() {
        this.setOverrideIncludeSynonymsDefault(true);
        return this;
    }

    @Override
    public int execute(Map<String, ?> args) {
        return this.doExecute(args);
    }

    @Override
    public int execute(SqlParameterSource parameterSource) {
        return this.doExecute(parameterSource);
    }

    @Override
    public Number executeAndReturnKey(Map<String, ?> args) {
        return this.doExecuteAndReturnKey(args);
    }

    @Override
    public Number executeAndReturnKey(SqlParameterSource parameterSource) {
        return this.doExecuteAndReturnKey(parameterSource);
    }

    @Override
    public KeyHolder executeAndReturnKeyHolder(Map<String, ?> args) {
        return this.doExecuteAndReturnKeyHolder(args);
    }

    @Override
    public KeyHolder executeAndReturnKeyHolder(SqlParameterSource parameterSource) {
        return this.doExecuteAndReturnKeyHolder(parameterSource);
    }

    @Override
    public int[] executeBatch(Map<String, ?> ... batch) {
        return this.doExecuteBatch(batch);
    }

    @Override
    public int[] executeBatch(SqlParameterSource ... batch) {
        return this.doExecuteBatch(batch);
    }
}

