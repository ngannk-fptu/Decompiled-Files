/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.lang.Nullable;

public class PreparedStatementCreatorFactory {
    private final String sql;
    private final List<SqlParameter> declaredParameters;
    private int resultSetType = 1003;
    private boolean updatableResults = false;
    private boolean returnGeneratedKeys = false;
    @Nullable
    private String[] generatedKeysColumnNames;

    public PreparedStatementCreatorFactory(String sql) {
        this.sql = sql;
        this.declaredParameters = new ArrayList<SqlParameter>();
    }

    public PreparedStatementCreatorFactory(String sql, int ... types) {
        this.sql = sql;
        this.declaredParameters = SqlParameter.sqlTypesToAnonymousParameterList(types);
    }

    public PreparedStatementCreatorFactory(String sql, List<SqlParameter> declaredParameters) {
        this.sql = sql;
        this.declaredParameters = declaredParameters;
    }

    public final String getSql() {
        return this.sql;
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

    public void setReturnGeneratedKeys(boolean returnGeneratedKeys) {
        this.returnGeneratedKeys = returnGeneratedKeys;
    }

    public void setGeneratedKeysColumnNames(String ... names) {
        this.generatedKeysColumnNames = names;
    }

    public PreparedStatementSetter newPreparedStatementSetter(@Nullable List<?> params) {
        return new PreparedStatementCreatorImpl(params != null ? params : Collections.emptyList());
    }

    public PreparedStatementSetter newPreparedStatementSetter(@Nullable Object[] params) {
        return new PreparedStatementCreatorImpl(params != null ? Arrays.asList(params) : Collections.emptyList());
    }

    public PreparedStatementCreator newPreparedStatementCreator(@Nullable List<?> params) {
        return new PreparedStatementCreatorImpl(params != null ? params : Collections.emptyList());
    }

    public PreparedStatementCreator newPreparedStatementCreator(@Nullable Object[] params) {
        return new PreparedStatementCreatorImpl(params != null ? Arrays.asList(params) : Collections.emptyList());
    }

    public PreparedStatementCreator newPreparedStatementCreator(String sqlToUse, @Nullable Object[] params) {
        return new PreparedStatementCreatorImpl(sqlToUse, params != null ? Arrays.asList(params) : Collections.emptyList());
    }

    private class PreparedStatementCreatorImpl
    implements PreparedStatementCreator,
    PreparedStatementSetter,
    SqlProvider,
    ParameterDisposer {
        private final String actualSql;
        private final List<?> parameters;

        public PreparedStatementCreatorImpl(List<?> parameters) {
            this(preparedStatementCreatorFactory.sql, parameters);
        }

        public PreparedStatementCreatorImpl(String actualSql, List<?> parameters) {
            this.actualSql = actualSql;
            this.parameters = parameters;
            if (parameters.size() != PreparedStatementCreatorFactory.this.declaredParameters.size()) {
                HashSet<String> names = new HashSet<String>();
                for (int i = 0; i < parameters.size(); ++i) {
                    Object param = parameters.get(i);
                    if (param instanceof SqlParameterValue) {
                        names.add(((SqlParameterValue)param).getName());
                        continue;
                    }
                    names.add("Parameter #" + i);
                }
                if (names.size() != PreparedStatementCreatorFactory.this.declaredParameters.size()) {
                    throw new InvalidDataAccessApiUsageException("SQL [" + PreparedStatementCreatorFactory.this.sql + "]: given " + names.size() + " parameters but expected " + PreparedStatementCreatorFactory.this.declaredParameters.size());
                }
            }
        }

        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            PreparedStatement ps = PreparedStatementCreatorFactory.this.generatedKeysColumnNames != null || PreparedStatementCreatorFactory.this.returnGeneratedKeys ? (PreparedStatementCreatorFactory.this.generatedKeysColumnNames != null ? con.prepareStatement(this.actualSql, PreparedStatementCreatorFactory.this.generatedKeysColumnNames) : con.prepareStatement(this.actualSql, 1)) : (PreparedStatementCreatorFactory.this.resultSetType == 1003 && !PreparedStatementCreatorFactory.this.updatableResults ? con.prepareStatement(this.actualSql) : con.prepareStatement(this.actualSql, PreparedStatementCreatorFactory.this.resultSetType, PreparedStatementCreatorFactory.this.updatableResults ? 1008 : 1007));
            this.setValues(ps);
            return ps;
        }

        @Override
        public void setValues(PreparedStatement ps) throws SQLException {
            int sqlColIndx = 1;
            for (int i = 0; i < this.parameters.size(); ++i) {
                SqlParameter declaredParameter;
                Object in = this.parameters.get(i);
                if (in instanceof SqlParameterValue) {
                    SqlParameterValue paramValue = (SqlParameterValue)in;
                    in = paramValue.getValue();
                    declaredParameter = paramValue;
                } else {
                    if (PreparedStatementCreatorFactory.this.declaredParameters.size() <= i) {
                        throw new InvalidDataAccessApiUsageException("SQL [" + PreparedStatementCreatorFactory.this.sql + "]: unable to access parameter number " + (i + 1) + " given only " + PreparedStatementCreatorFactory.this.declaredParameters.size() + " parameters");
                    }
                    declaredParameter = (SqlParameter)PreparedStatementCreatorFactory.this.declaredParameters.get(i);
                }
                if (in instanceof Iterable && declaredParameter.getSqlType() != 2003) {
                    Iterable entries = (Iterable)in;
                    for (Object entry : entries) {
                        if (entry instanceof Object[]) {
                            Object[] valueArray;
                            for (Object argValue : valueArray = (Object[])entry) {
                                StatementCreatorUtils.setParameterValue(ps, sqlColIndx++, declaredParameter, argValue);
                            }
                            continue;
                        }
                        StatementCreatorUtils.setParameterValue(ps, sqlColIndx++, declaredParameter, entry);
                    }
                    continue;
                }
                StatementCreatorUtils.setParameterValue(ps, sqlColIndx++, declaredParameter, in);
            }
        }

        @Override
        public String getSql() {
            return PreparedStatementCreatorFactory.this.sql;
        }

        @Override
        public void cleanupParameters() {
            StatementCreatorUtils.cleanupParameters(this.parameters);
        }

        public String toString() {
            return "PreparedStatementCreator: sql=[" + PreparedStatementCreatorFactory.this.sql + "]; parameters=" + this.parameters;
        }
    }
}

