/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.ParameterMapper;
import org.springframework.jdbc.core.ResultSetSupportingSqlParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.lang.Nullable;

public class CallableStatementCreatorFactory {
    private final String callString;
    private final List<SqlParameter> declaredParameters;
    private int resultSetType = 1003;
    private boolean updatableResults = false;

    public CallableStatementCreatorFactory(String callString) {
        this.callString = callString;
        this.declaredParameters = new ArrayList<SqlParameter>();
    }

    public CallableStatementCreatorFactory(String callString, List<SqlParameter> declaredParameters) {
        this.callString = callString;
        this.declaredParameters = declaredParameters;
    }

    public final String getCallString() {
        return this.callString;
    }

    public void addParameter(SqlParameter param) {
        this.declaredParameters.add(param);
    }

    public void setResultSetType(int resultSetType) {
        this.resultSetType = resultSetType;
    }

    public void setUpdatableResults(boolean updatableResults) {
        this.updatableResults = updatableResults;
    }

    public CallableStatementCreator newCallableStatementCreator(@Nullable Map<String, ?> params) {
        return new CallableStatementCreatorImpl(params != null ? params : new HashMap());
    }

    public CallableStatementCreator newCallableStatementCreator(ParameterMapper inParamMapper) {
        return new CallableStatementCreatorImpl(inParamMapper);
    }

    private class CallableStatementCreatorImpl
    implements CallableStatementCreator,
    SqlProvider,
    ParameterDisposer {
        @Nullable
        private ParameterMapper inParameterMapper;
        @Nullable
        private Map<String, ?> inParameters;

        public CallableStatementCreatorImpl(ParameterMapper inParamMapper) {
            this.inParameterMapper = inParamMapper;
        }

        public CallableStatementCreatorImpl(Map<String, ?> inParams) {
            this.inParameters = inParams;
        }

        @Override
        public CallableStatement createCallableStatement(Connection con) throws SQLException {
            if (this.inParameterMapper != null) {
                this.inParameters = this.inParameterMapper.createMap(con);
            } else if (this.inParameters == null) {
                throw new InvalidDataAccessApiUsageException("A ParameterMapper or a Map of parameters must be provided");
            }
            CallableStatement cs = null;
            cs = CallableStatementCreatorFactory.this.resultSetType == 1003 && !CallableStatementCreatorFactory.this.updatableResults ? con.prepareCall(CallableStatementCreatorFactory.this.callString) : con.prepareCall(CallableStatementCreatorFactory.this.callString, CallableStatementCreatorFactory.this.resultSetType, CallableStatementCreatorFactory.this.updatableResults ? 1008 : 1007);
            int sqlColIndx = 1;
            for (SqlParameter declaredParam : CallableStatementCreatorFactory.this.declaredParameters) {
                if (declaredParam.isResultsParameter()) continue;
                Object inValue = this.inParameters.get(declaredParam.getName());
                if (declaredParam instanceof ResultSetSupportingSqlParameter) {
                    if (declaredParam instanceof SqlOutParameter) {
                        if (declaredParam.getTypeName() != null) {
                            cs.registerOutParameter(sqlColIndx, declaredParam.getSqlType(), declaredParam.getTypeName());
                        } else if (declaredParam.getScale() != null) {
                            cs.registerOutParameter(sqlColIndx, declaredParam.getSqlType(), (int)declaredParam.getScale());
                        } else {
                            cs.registerOutParameter(sqlColIndx, declaredParam.getSqlType());
                        }
                        if (declaredParam.isInputValueProvided()) {
                            StatementCreatorUtils.setParameterValue((PreparedStatement)cs, sqlColIndx, declaredParam, inValue);
                        }
                    }
                } else {
                    if (!this.inParameters.containsKey(declaredParam.getName())) {
                        throw new InvalidDataAccessApiUsageException("Required input parameter '" + declaredParam.getName() + "' is missing");
                    }
                    StatementCreatorUtils.setParameterValue((PreparedStatement)cs, sqlColIndx, declaredParam, inValue);
                }
                ++sqlColIndx;
            }
            return cs;
        }

        @Override
        public String getSql() {
            return CallableStatementCreatorFactory.this.callString;
        }

        @Override
        public void cleanupParameters() {
            if (this.inParameters != null) {
                StatementCreatorUtils.cleanupParameters(this.inParameters.values());
            }
        }

        public String toString() {
            return "CallableStatementCreator: sql=[" + CallableStatementCreatorFactory.this.callString + "]; parameters=" + this.inParameters;
        }
    }
}

