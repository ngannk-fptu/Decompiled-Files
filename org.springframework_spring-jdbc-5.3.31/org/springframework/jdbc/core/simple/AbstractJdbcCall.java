/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.jdbc.core.simple;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.CallableStatementCreatorFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.metadata.CallMetaDataContext;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class AbstractJdbcCall {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final JdbcTemplate jdbcTemplate;
    private final CallMetaDataContext callMetaDataContext = new CallMetaDataContext();
    private final List<SqlParameter> declaredParameters = new ArrayList<SqlParameter>();
    private final Map<String, RowMapper<?>> declaredRowMappers = new LinkedHashMap();
    private volatile boolean compiled;
    @Nullable
    private String callString;
    @Nullable
    private CallableStatementCreatorFactory callableStatementFactory;

    protected AbstractJdbcCall(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    protected AbstractJdbcCall(JdbcTemplate jdbcTemplate) {
        Assert.notNull((Object)jdbcTemplate, (String)"JdbcTemplate must not be null");
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    public void setProcedureName(@Nullable String procedureName) {
        this.callMetaDataContext.setProcedureName(procedureName);
    }

    @Nullable
    public String getProcedureName() {
        return this.callMetaDataContext.getProcedureName();
    }

    public void setInParameterNames(Set<String> inParameterNames) {
        this.callMetaDataContext.setLimitedInParameterNames(inParameterNames);
    }

    public Set<String> getInParameterNames() {
        return this.callMetaDataContext.getLimitedInParameterNames();
    }

    public void setCatalogName(@Nullable String catalogName) {
        this.callMetaDataContext.setCatalogName(catalogName);
    }

    @Nullable
    public String getCatalogName() {
        return this.callMetaDataContext.getCatalogName();
    }

    public void setSchemaName(@Nullable String schemaName) {
        this.callMetaDataContext.setSchemaName(schemaName);
    }

    @Nullable
    public String getSchemaName() {
        return this.callMetaDataContext.getSchemaName();
    }

    public void setFunction(boolean function) {
        this.callMetaDataContext.setFunction(function);
    }

    public boolean isFunction() {
        return this.callMetaDataContext.isFunction();
    }

    public void setReturnValueRequired(boolean returnValueRequired) {
        this.callMetaDataContext.setReturnValueRequired(returnValueRequired);
    }

    public boolean isReturnValueRequired() {
        return this.callMetaDataContext.isReturnValueRequired();
    }

    public void setNamedBinding(boolean namedBinding) {
        this.callMetaDataContext.setNamedBinding(namedBinding);
    }

    public boolean isNamedBinding() {
        return this.callMetaDataContext.isNamedBinding();
    }

    public void setAccessCallParameterMetaData(boolean accessCallParameterMetaData) {
        this.callMetaDataContext.setAccessCallParameterMetaData(accessCallParameterMetaData);
    }

    @Nullable
    public String getCallString() {
        return this.callString;
    }

    protected CallableStatementCreatorFactory getCallableStatementFactory() {
        Assert.state((this.callableStatementFactory != null ? 1 : 0) != 0, (String)"No CallableStatementCreatorFactory available");
        return this.callableStatementFactory;
    }

    public void addDeclaredParameter(SqlParameter parameter) {
        Assert.notNull((Object)parameter, (String)"The supplied parameter must not be null");
        if (!StringUtils.hasText((String)parameter.getName())) {
            throw new InvalidDataAccessApiUsageException("You must specify a parameter name when declaring parameters for \"" + this.getProcedureName() + "\"");
        }
        this.declaredParameters.add(parameter);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Added declared parameter for [" + this.getProcedureName() + "]: " + parameter.getName()));
        }
    }

    public void addDeclaredRowMapper(String parameterName, RowMapper<?> rowMapper) {
        this.declaredRowMappers.put(parameterName, rowMapper);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Added row mapper for [" + this.getProcedureName() + "]: " + parameterName));
        }
    }

    public final synchronized void compile() throws InvalidDataAccessApiUsageException {
        if (!this.isCompiled()) {
            if (this.getProcedureName() == null) {
                throw new InvalidDataAccessApiUsageException("Procedure or Function name is required");
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
                this.logger.debug((Object)("SqlCall for " + (this.isFunction() ? "function" : "procedure") + " [" + this.getProcedureName() + "] compiled"));
            }
        }
    }

    protected void compileInternal() {
        DataSource dataSource = this.getJdbcTemplate().getDataSource();
        Assert.state((dataSource != null ? 1 : 0) != 0, (String)"No DataSource set");
        this.callMetaDataContext.initializeMetaData(dataSource);
        this.declaredRowMappers.forEach((key, value) -> this.declaredParameters.add(this.callMetaDataContext.createReturnResultSetParameter((String)key, (RowMapper<?>)value)));
        this.callMetaDataContext.processParameters(this.declaredParameters);
        this.callString = this.callMetaDataContext.createCallString();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Compiled stored procedure. Call string is [" + this.callString + "]"));
        }
        this.callableStatementFactory = new CallableStatementCreatorFactory(this.callString, this.callMetaDataContext.getCallParameters());
        this.onCompileInternal();
    }

    protected void onCompileInternal() {
    }

    public boolean isCompiled() {
        return this.compiled;
    }

    protected void checkCompiled() {
        if (!this.isCompiled()) {
            this.logger.debug((Object)"JdbcCall call not compiled before execution - invoking compile");
            this.compile();
        }
    }

    protected Map<String, Object> doExecute(SqlParameterSource parameterSource) {
        this.checkCompiled();
        Map<String, Object> params = this.matchInParameterValuesWithCallParameters(parameterSource);
        return this.executeCallInternal(params);
    }

    protected Map<String, Object> doExecute(Object ... args) {
        this.checkCompiled();
        Map<String, ?> params = this.matchInParameterValuesWithCallParameters(args);
        return this.executeCallInternal(params);
    }

    protected Map<String, Object> doExecute(Map<String, ?> args) {
        this.checkCompiled();
        Map<String, ?> params = this.matchInParameterValuesWithCallParameters(args);
        return this.executeCallInternal(params);
    }

    private Map<String, Object> executeCallInternal(Map<String, ?> args) {
        CallableStatementCreator csc = this.getCallableStatementFactory().newCallableStatementCreator(args);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("The following parameters are used for call " + this.getCallString() + " with " + args));
            int i = 1;
            for (SqlParameter param : this.getCallParameters()) {
                this.logger.debug((Object)(i + ": " + param.getName() + ", SQL type " + param.getSqlType() + ", type name " + param.getTypeName() + ", parameter class [" + param.getClass().getName() + "]"));
                ++i;
            }
        }
        return this.getJdbcTemplate().call(csc, this.getCallParameters());
    }

    @Nullable
    protected String getScalarOutParameterName() {
        return this.callMetaDataContext.getScalarOutParameterName();
    }

    protected List<SqlParameter> getCallParameters() {
        return this.callMetaDataContext.getCallParameters();
    }

    protected Map<String, Object> matchInParameterValuesWithCallParameters(SqlParameterSource parameterSource) {
        return this.callMetaDataContext.matchInParameterValuesWithCallParameters(parameterSource);
    }

    private Map<String, ?> matchInParameterValuesWithCallParameters(Object[] args) {
        return this.callMetaDataContext.matchInParameterValuesWithCallParameters(args);
    }

    protected Map<String, ?> matchInParameterValuesWithCallParameters(Map<String, ?> args) {
        return this.callMetaDataContext.matchInParameterValuesWithCallParameters(args);
    }
}

