/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.procedure.spi;

import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;
import org.hibernate.procedure.spi.ParameterStrategy;
import org.hibernate.query.procedure.internal.ProcedureParamBindings;
import org.hibernate.query.procedure.internal.ProcedureParameterMetadata;

public interface CallableStatementSupport {
    default public String renderCallableStatement(String name, ParameterStrategy parameterStrategy, List<ParameterRegistrationImplementor<?>> parameterRegistrations, SharedSessionContractImplementor session) {
        throw new UnsupportedOperationException("Legacy #renderCallableStatement called but implementation does not support that call.");
    }

    default public String renderCallableStatement(String procedureName, ProcedureParameterMetadata parameterMetadata, ProcedureParamBindings paramBindings, SharedSessionContractImplementor session) {
        return this.renderCallableStatement(procedureName, parameterMetadata.getParameterStrategy(), new ArrayList(parameterMetadata.collectAllParameters()), session);
    }

    public void registerParameters(String var1, CallableStatement var2, ParameterStrategy var3, List<ParameterRegistrationImplementor<?>> var4, SharedSessionContractImplementor var5);
}

