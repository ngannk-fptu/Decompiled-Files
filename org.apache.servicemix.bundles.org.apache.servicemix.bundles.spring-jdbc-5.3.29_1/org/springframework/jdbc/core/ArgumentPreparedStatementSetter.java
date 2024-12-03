/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.lang.Nullable;

public class ArgumentPreparedStatementSetter
implements PreparedStatementSetter,
ParameterDisposer {
    @Nullable
    private final Object[] args;

    public ArgumentPreparedStatementSetter(@Nullable Object[] args) {
        this.args = args;
    }

    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        if (this.args != null) {
            for (int i = 0; i < this.args.length; ++i) {
                Object arg = this.args[i];
                this.doSetValue(ps, i + 1, arg);
            }
        }
    }

    protected void doSetValue(PreparedStatement ps, int parameterPosition, Object argValue) throws SQLException {
        if (argValue instanceof SqlParameterValue) {
            SqlParameterValue paramValue = (SqlParameterValue)argValue;
            StatementCreatorUtils.setParameterValue(ps, parameterPosition, paramValue, paramValue.getValue());
        } else {
            StatementCreatorUtils.setParameterValue(ps, parameterPosition, Integer.MIN_VALUE, argValue);
        }
    }

    @Override
    public void cleanupParameters() {
        StatementCreatorUtils.cleanupParameters(this.args);
    }
}

