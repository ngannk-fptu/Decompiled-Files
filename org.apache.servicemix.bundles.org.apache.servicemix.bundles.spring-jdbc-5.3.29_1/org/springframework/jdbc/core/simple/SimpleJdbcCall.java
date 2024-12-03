/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core.simple;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.AbstractJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcCallOperations;

public class SimpleJdbcCall
extends AbstractJdbcCall
implements SimpleJdbcCallOperations {
    public SimpleJdbcCall(DataSource dataSource) {
        super(dataSource);
    }

    public SimpleJdbcCall(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public SimpleJdbcCall withProcedureName(String procedureName) {
        this.setProcedureName(procedureName);
        this.setFunction(false);
        return this;
    }

    @Override
    public SimpleJdbcCall withFunctionName(String functionName) {
        this.setProcedureName(functionName);
        this.setFunction(true);
        return this;
    }

    @Override
    public SimpleJdbcCall withSchemaName(String schemaName) {
        this.setSchemaName(schemaName);
        return this;
    }

    @Override
    public SimpleJdbcCall withCatalogName(String catalogName) {
        this.setCatalogName(catalogName);
        return this;
    }

    @Override
    public SimpleJdbcCall withReturnValue() {
        this.setReturnValueRequired(true);
        return this;
    }

    @Override
    public SimpleJdbcCall declareParameters(SqlParameter ... sqlParameters) {
        for (SqlParameter sqlParameter : sqlParameters) {
            if (sqlParameter == null) continue;
            this.addDeclaredParameter(sqlParameter);
        }
        return this;
    }

    @Override
    public SimpleJdbcCall useInParameterNames(String ... inParameterNames) {
        this.setInParameterNames(new LinkedHashSet<String>(Arrays.asList(inParameterNames)));
        return this;
    }

    @Override
    public SimpleJdbcCall returningResultSet(String parameterName, RowMapper<?> rowMapper) {
        this.addDeclaredRowMapper(parameterName, rowMapper);
        return this;
    }

    @Override
    public SimpleJdbcCall withoutProcedureColumnMetaDataAccess() {
        this.setAccessCallParameterMetaData(false);
        return this;
    }

    @Override
    public SimpleJdbcCall withNamedBinding() {
        this.setNamedBinding(true);
        return this;
    }

    @Override
    public <T> T executeFunction(Class<T> returnType, Object ... args) {
        return (T)this.doExecute(args).get(this.getScalarOutParameterName());
    }

    @Override
    public <T> T executeFunction(Class<T> returnType, Map<String, ?> args) {
        return (T)this.doExecute(args).get(this.getScalarOutParameterName());
    }

    @Override
    public <T> T executeFunction(Class<T> returnType, SqlParameterSource args) {
        return (T)this.doExecute(args).get(this.getScalarOutParameterName());
    }

    @Override
    public <T> T executeObject(Class<T> returnType, Object ... args) {
        return (T)this.doExecute(args).get(this.getScalarOutParameterName());
    }

    @Override
    public <T> T executeObject(Class<T> returnType, Map<String, ?> args) {
        return (T)this.doExecute(args).get(this.getScalarOutParameterName());
    }

    @Override
    public <T> T executeObject(Class<T> returnType, SqlParameterSource args) {
        return (T)this.doExecute(args).get(this.getScalarOutParameterName());
    }

    @Override
    public Map<String, Object> execute(Object ... args) {
        return this.doExecute(args);
    }

    @Override
    public Map<String, Object> execute(Map<String, ?> args) {
        return this.doExecute(args);
    }

    @Override
    public Map<String, Object> execute(SqlParameterSource parameterSource) {
        return this.doExecute(parameterSource);
    }
}

