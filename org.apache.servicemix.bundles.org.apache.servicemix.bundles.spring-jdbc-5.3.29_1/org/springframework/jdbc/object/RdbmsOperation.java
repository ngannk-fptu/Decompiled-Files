/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.object;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class RdbmsOperation
implements InitializingBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private JdbcTemplate jdbcTemplate = new JdbcTemplate();
    private int resultSetType = 1003;
    private boolean updatableResults = false;
    private boolean returnGeneratedKeys = false;
    @Nullable
    private String[] generatedKeysColumnNames;
    @Nullable
    private String sql;
    private final List<SqlParameter> declaredParameters = new ArrayList<SqlParameter>();
    private volatile boolean compiled;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate.setDataSource(dataSource);
    }

    public void setFetchSize(int fetchSize) {
        this.jdbcTemplate.setFetchSize(fetchSize);
    }

    public void setMaxRows(int maxRows) {
        this.jdbcTemplate.setMaxRows(maxRows);
    }

    public void setQueryTimeout(int queryTimeout) {
        this.jdbcTemplate.setQueryTimeout(queryTimeout);
    }

    public void setResultSetType(int resultSetType) {
        this.resultSetType = resultSetType;
    }

    public int getResultSetType() {
        return this.resultSetType;
    }

    public void setUpdatableResults(boolean updatableResults) {
        if (this.isCompiled()) {
            throw new InvalidDataAccessApiUsageException("The updateableResults flag must be set before the operation is compiled");
        }
        this.updatableResults = updatableResults;
    }

    public boolean isUpdatableResults() {
        return this.updatableResults;
    }

    public void setReturnGeneratedKeys(boolean returnGeneratedKeys) {
        if (this.isCompiled()) {
            throw new InvalidDataAccessApiUsageException("The returnGeneratedKeys flag must be set before the operation is compiled");
        }
        this.returnGeneratedKeys = returnGeneratedKeys;
    }

    public boolean isReturnGeneratedKeys() {
        return this.returnGeneratedKeys;
    }

    public void setGeneratedKeysColumnNames(String ... names) {
        if (this.isCompiled()) {
            throw new InvalidDataAccessApiUsageException("The column names for the generated keys must be set before the operation is compiled");
        }
        this.generatedKeysColumnNames = names;
    }

    @Nullable
    public String[] getGeneratedKeysColumnNames() {
        return this.generatedKeysColumnNames;
    }

    public void setSql(@Nullable String sql) {
        this.sql = sql;
    }

    @Nullable
    public String getSql() {
        return this.sql;
    }

    protected String resolveSql() {
        String sql = this.getSql();
        Assert.state((sql != null ? 1 : 0) != 0, (String)"No SQL set");
        return sql;
    }

    public void setTypes(@Nullable int[] types) throws InvalidDataAccessApiUsageException {
        if (this.isCompiled()) {
            throw new InvalidDataAccessApiUsageException("Cannot add parameters once query is compiled");
        }
        if (types != null) {
            for (int type : types) {
                this.declareParameter(new SqlParameter(type));
            }
        }
    }

    public void declareParameter(SqlParameter param) throws InvalidDataAccessApiUsageException {
        if (this.isCompiled()) {
            throw new InvalidDataAccessApiUsageException("Cannot add parameters once the query is compiled");
        }
        this.declaredParameters.add(param);
    }

    public void setParameters(SqlParameter ... parameters) {
        if (this.isCompiled()) {
            throw new InvalidDataAccessApiUsageException("Cannot add parameters once the query is compiled");
        }
        for (int i = 0; i < parameters.length; ++i) {
            if (parameters[i] == null) {
                throw new InvalidDataAccessApiUsageException("Cannot add parameter at index " + i + " from " + Arrays.asList(parameters) + " since it is 'null'");
            }
            this.declaredParameters.add(parameters[i]);
        }
    }

    protected List<SqlParameter> getDeclaredParameters() {
        return this.declaredParameters;
    }

    public void afterPropertiesSet() {
        this.compile();
    }

    public final void compile() throws InvalidDataAccessApiUsageException {
        if (!this.isCompiled()) {
            if (this.getSql() == null) {
                throw new InvalidDataAccessApiUsageException("Property 'sql' is required");
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
                this.logger.debug((Object)("RdbmsOperation with SQL [" + this.getSql() + "] compiled"));
            }
        }
    }

    public boolean isCompiled() {
        return this.compiled;
    }

    protected void checkCompiled() {
        if (!this.isCompiled()) {
            this.logger.debug((Object)"SQL operation not compiled before execution - invoking compile");
            this.compile();
        }
    }

    protected void validateParameters(@Nullable Object[] parameters) throws InvalidDataAccessApiUsageException {
        this.checkCompiled();
        int declaredInParameters = 0;
        for (SqlParameter param : this.declaredParameters) {
            if (!param.isInputValueProvided()) continue;
            if (!(this.supportsLobParameters() || param.getSqlType() != 2004 && param.getSqlType() != 2005)) {
                throw new InvalidDataAccessApiUsageException("BLOB or CLOB parameters are not allowed for this kind of operation");
            }
            ++declaredInParameters;
        }
        this.validateParameterCount(parameters != null ? parameters.length : 0, declaredInParameters);
    }

    protected void validateNamedParameters(@Nullable Map<String, ?> parameters) throws InvalidDataAccessApiUsageException {
        this.checkCompiled();
        Map<String, Object> paramsToUse = parameters != null ? parameters : Collections.emptyMap();
        int declaredInParameters = 0;
        for (SqlParameter param : this.declaredParameters) {
            if (!param.isInputValueProvided()) continue;
            if (!(this.supportsLobParameters() || param.getSqlType() != 2004 && param.getSqlType() != 2005)) {
                throw new InvalidDataAccessApiUsageException("BLOB or CLOB parameters are not allowed for this kind of operation");
            }
            if (param.getName() != null && !paramsToUse.containsKey(param.getName())) {
                throw new InvalidDataAccessApiUsageException("The parameter named '" + param.getName() + "' was not among the parameters supplied: " + paramsToUse.keySet());
            }
            ++declaredInParameters;
        }
        this.validateParameterCount(paramsToUse.size(), declaredInParameters);
    }

    private void validateParameterCount(int suppliedParamCount, int declaredInParamCount) {
        if (suppliedParamCount < declaredInParamCount) {
            throw new InvalidDataAccessApiUsageException(suppliedParamCount + " parameters were supplied, but " + declaredInParamCount + " in parameters were declared in class [" + this.getClass().getName() + "]");
        }
        if (suppliedParamCount > this.declaredParameters.size() && !this.allowsUnusedParameters()) {
            throw new InvalidDataAccessApiUsageException(suppliedParamCount + " parameters were supplied, but " + declaredInParamCount + " parameters were declared in class [" + this.getClass().getName() + "]");
        }
    }

    protected abstract void compileInternal() throws InvalidDataAccessApiUsageException;

    protected boolean supportsLobParameters() {
        return true;
    }

    protected boolean allowsUnusedParameters() {
        return false;
    }
}

