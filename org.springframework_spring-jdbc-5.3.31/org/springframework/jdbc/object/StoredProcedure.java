/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 */
package org.springframework.jdbc.object;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlCall;

public abstract class StoredProcedure
extends SqlCall {
    protected StoredProcedure() {
    }

    protected StoredProcedure(DataSource ds, String name) {
        this.setDataSource(ds);
        this.setSql(name);
    }

    protected StoredProcedure(JdbcTemplate jdbcTemplate, String name) {
        this.setJdbcTemplate(jdbcTemplate);
        this.setSql(name);
    }

    @Override
    protected boolean allowsUnusedParameters() {
        return true;
    }

    @Override
    public void declareParameter(SqlParameter param) throws InvalidDataAccessApiUsageException {
        if (param.getName() == null) {
            throw new InvalidDataAccessApiUsageException("Parameters to stored procedures must have names as well as types");
        }
        super.declareParameter(param);
    }

    public Map<String, Object> execute(Object ... inParams) {
        HashMap<String, Object> paramsToUse = new HashMap<String, Object>();
        this.validateParameters(inParams);
        int i = 0;
        for (SqlParameter sqlParameter : this.getDeclaredParameters()) {
            if (!sqlParameter.isInputValueProvided() || i >= inParams.length) continue;
            paramsToUse.put(sqlParameter.getName(), inParams[i++]);
        }
        return this.getJdbcTemplate().call(this.newCallableStatementCreator(paramsToUse), this.getDeclaredParameters());
    }

    public Map<String, Object> execute(Map<String, ?> inParams) throws DataAccessException {
        this.validateParameters(inParams.values().toArray());
        return this.getJdbcTemplate().call(this.newCallableStatementCreator(inParams), this.getDeclaredParameters());
    }

    public Map<String, Object> execute(ParameterMapper inParamMapper) throws DataAccessException {
        this.checkCompiled();
        return this.getJdbcTemplate().call(this.newCallableStatementCreator(inParamMapper), this.getDeclaredParameters());
    }
}

