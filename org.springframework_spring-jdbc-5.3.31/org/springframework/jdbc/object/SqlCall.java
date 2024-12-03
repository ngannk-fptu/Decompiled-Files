/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.object;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.CallableStatementCreatorFactory;
import org.springframework.jdbc.core.ParameterMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.RdbmsOperation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class SqlCall
extends RdbmsOperation {
    private boolean function = false;
    private boolean sqlReadyForUse = false;
    @Nullable
    private String callString;
    @Nullable
    private CallableStatementCreatorFactory callableStatementFactory;

    public SqlCall() {
    }

    public SqlCall(DataSource ds, String sql) {
        this.setDataSource(ds);
        this.setSql(sql);
    }

    public void setFunction(boolean function) {
        this.function = function;
    }

    public boolean isFunction() {
        return this.function;
    }

    public void setSqlReadyForUse(boolean sqlReadyForUse) {
        this.sqlReadyForUse = sqlReadyForUse;
    }

    public boolean isSqlReadyForUse() {
        return this.sqlReadyForUse;
    }

    @Override
    protected final void compileInternal() {
        if (this.isSqlReadyForUse()) {
            this.callString = this.resolveSql();
        } else {
            StringBuilder callString = new StringBuilder(32);
            List<SqlParameter> parameters = this.getDeclaredParameters();
            int parameterCount = 0;
            if (this.isFunction()) {
                callString.append("{? = call ").append(this.resolveSql()).append('(');
                parameterCount = -1;
            } else {
                callString.append("{call ").append(this.resolveSql()).append('(');
            }
            for (SqlParameter parameter : parameters) {
                if (parameter.isResultsParameter()) continue;
                if (parameterCount > 0) {
                    callString.append(", ");
                }
                if (parameterCount >= 0) {
                    callString.append('?');
                }
                ++parameterCount;
            }
            callString.append(")}");
            this.callString = callString.toString();
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Compiled stored procedure. Call string is [" + this.callString + "]"));
        }
        this.callableStatementFactory = new CallableStatementCreatorFactory(this.callString, this.getDeclaredParameters());
        this.callableStatementFactory.setResultSetType(this.getResultSetType());
        this.callableStatementFactory.setUpdatableResults(this.isUpdatableResults());
        this.onCompileInternal();
    }

    protected void onCompileInternal() {
    }

    @Nullable
    public String getCallString() {
        return this.callString;
    }

    protected CallableStatementCreator newCallableStatementCreator(@Nullable Map<String, ?> inParams) {
        Assert.state((this.callableStatementFactory != null ? 1 : 0) != 0, (String)"No CallableStatementFactory available");
        return this.callableStatementFactory.newCallableStatementCreator(inParams);
    }

    protected CallableStatementCreator newCallableStatementCreator(ParameterMapper inParamMapper) {
        Assert.state((this.callableStatementFactory != null ? 1 : 0) != 0, (String)"No CallableStatementFactory available");
        return this.callableStatementFactory.newCallableStatementCreator(inParamMapper);
    }
}

