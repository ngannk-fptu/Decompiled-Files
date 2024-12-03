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
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.procedure.spi.CallableStatementSupport;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;
import org.hibernate.procedure.spi.ParameterStrategy;

public class PostgresCallableStatementSupport
implements CallableStatementSupport {
    public static final PostgresCallableStatementSupport INSTANCE = new PostgresCallableStatementSupport();

    @Override
    public String renderCallableStatement(String procedureName, ParameterStrategy parameterStrategy, List<ParameterRegistrationImplementor<?>> parameterRegistrations, SharedSessionContractImplementor session) {
        int startIndex;
        boolean firstParamIsRefCursor;
        boolean bl = firstParamIsRefCursor = !parameterRegistrations.isEmpty() && parameterRegistrations.get(0).getMode() == ParameterMode.REF_CURSOR;
        if (firstParamIsRefCursor && parameterStrategy == ParameterStrategy.NAMED) {
            throw new HibernateException("Cannot mix named parameters and REF_CURSOR parameter on PostgreSQL");
        }
        StringBuilder buffer = firstParamIsRefCursor ? new StringBuilder().append("{? = call ") : new StringBuilder().append("{call ");
        buffer.append(procedureName).append("(");
        String sep = "";
        for (int i = startIndex = firstParamIsRefCursor ? 1 : 0; i < parameterRegistrations.size(); ++i) {
            ParameterRegistrationImplementor<?> parameter = parameterRegistrations.get(i);
            if (parameter.getMode() == ParameterMode.REF_CURSOR) {
                throw new HibernateException("PostgreSQL supports only one REF_CURSOR parameter, but multiple were registered");
            }
            for (int ignored : parameter.getSqlTypes()) {
                buffer.append(sep).append("?");
                sep = ",";
            }
        }
        return buffer.append(")}").toString();
    }

    @Override
    public void registerParameters(String procedureName, CallableStatement statement, ParameterStrategy parameterStrategy, List<ParameterRegistrationImplementor<?>> parameterRegistrations, SharedSessionContractImplementor session) {
        int i = 1;
        try {
            for (ParameterRegistrationImplementor<?> parameter : parameterRegistrations) {
                if (parameter.getMode() == ParameterMode.REF_CURSOR) {
                    statement.registerOutParameter(i, 1111);
                    ++i;
                    continue;
                }
                parameter.prepare(statement, i);
                i += parameter.getSqlTypes().length;
            }
        }
        catch (SQLException e) {
            throw session.getJdbcServices().getSqlExceptionHelper().convert(e, "Error registering CallableStatement parameters", procedureName);
        }
    }
}

