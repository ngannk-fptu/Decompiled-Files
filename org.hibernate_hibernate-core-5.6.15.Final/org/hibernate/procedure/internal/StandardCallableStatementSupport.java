/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ParameterMode
 */
package org.hibernate.procedure.internal;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.ParameterMode;
import org.hibernate.QueryException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.procedure.spi.CallableStatementSupport;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;
import org.hibernate.procedure.spi.ParameterStrategy;

public class StandardCallableStatementSupport
implements CallableStatementSupport {
    public static final StandardCallableStatementSupport NO_REF_CURSOR_INSTANCE = new StandardCallableStatementSupport(false);
    public static final StandardCallableStatementSupport REF_CURSOR_INSTANCE = new StandardCallableStatementSupport(true);
    private final boolean supportsRefCursors;

    public StandardCallableStatementSupport(boolean supportsRefCursors) {
        this.supportsRefCursors = supportsRefCursors;
    }

    @Override
    public String renderCallableStatement(String procedureName, ParameterStrategy parameterStrategy, List<ParameterRegistrationImplementor<?>> parameterRegistrations, SharedSessionContractImplementor session) {
        StringBuilder buffer = new StringBuilder().append("{call ").append(procedureName).append("(");
        String sep = "";
        for (ParameterRegistrationImplementor<?> parameter : parameterRegistrations) {
            if (parameter == null) {
                throw new QueryException("Parameter registrations had gaps");
            }
            if (parameter.getMode() == ParameterMode.REF_CURSOR) {
                this.verifyRefCursorSupport(session.getJdbcServices().getJdbcEnvironment().getDialect());
                buffer.append(sep).append("?");
                sep = ",";
                continue;
            }
            for (int i = 0; i < parameter.getSqlTypes().length; ++i) {
                buffer.append(sep).append("?");
                sep = ",";
            }
        }
        return buffer.append(")}").toString();
    }

    private void verifyRefCursorSupport(Dialect dialect) {
        if (!this.supportsRefCursors) {
            throw new QueryException("Dialect [" + dialect.getClass().getName() + "] not known to support REF_CURSOR parameters");
        }
    }

    @Override
    public void registerParameters(String procedureName, CallableStatement statement, ParameterStrategy parameterStrategy, List<ParameterRegistrationImplementor<?>> parameterRegistrations, SharedSessionContractImplementor session) {
        int i = 1;
        try {
            for (ParameterRegistrationImplementor<?> parameter : parameterRegistrations) {
                parameter.prepare(statement, i);
                if (parameter.getMode() == ParameterMode.REF_CURSOR) {
                    ++i;
                    continue;
                }
                i += parameter.getSqlTypes().length;
            }
        }
        catch (SQLException e) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(e, "Error registering CallableStatement parameters", procedureName);
        }
    }
}

