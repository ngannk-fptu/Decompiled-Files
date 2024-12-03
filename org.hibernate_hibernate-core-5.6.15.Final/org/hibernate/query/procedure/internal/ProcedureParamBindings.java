/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ParameterMode
 */
package org.hibernate.query.procedure.internal;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.ParameterMode;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.procedure.ParameterBind;
import org.hibernate.procedure.internal.ParameterBindImpl;
import org.hibernate.procedure.internal.ProcedureCallImpl;
import org.hibernate.query.QueryParameter;
import org.hibernate.query.procedure.internal.ProcedureParameterMetadata;
import org.hibernate.query.procedure.spi.ProcedureParameterImplementor;
import org.hibernate.query.spi.QueryParameterBinding;
import org.hibernate.query.spi.QueryParameterBindings;
import org.hibernate.query.spi.QueryParameterListBinding;
import org.hibernate.type.Type;

public class ProcedureParamBindings
implements QueryParameterBindings {
    private final ProcedureParameterMetadata parameterMetadata;
    private final ProcedureCallImpl procedureCall;
    private final Map<ProcedureParameterImplementor, ParameterBind> bindingMap = new HashMap<ProcedureParameterImplementor, ParameterBind>();

    public ProcedureParamBindings(ProcedureParameterMetadata parameterMetadata, ProcedureCallImpl procedureCall) {
        this.parameterMetadata = parameterMetadata;
        this.procedureCall = procedureCall;
    }

    public ProcedureParameterMetadata getParameterMetadata() {
        return this.parameterMetadata;
    }

    public ProcedureCallImpl getProcedureCall() {
        return this.procedureCall;
    }

    @Override
    public boolean isBound(QueryParameter parameter) {
        return this.getBinding(parameter).isBound();
    }

    @Override
    public <T> QueryParameterBinding<T> getBinding(QueryParameter<T> parameter) {
        QueryParameter procParam = this.parameterMetadata.resolve(parameter);
        ParameterBindImpl binding = this.bindingMap.get(procParam);
        if (binding == null) {
            if (!this.parameterMetadata.containsReference(parameter)) {
                throw new IllegalArgumentException("Passed parameter is not registered with this query");
            }
            binding = new ParameterBindImpl((ProcedureParameterImplementor)procParam, this);
            this.bindingMap.put((ProcedureParameterImplementor)procParam, binding);
        }
        return binding;
    }

    @Override
    public <T> QueryParameterBinding<T> getBinding(String name) {
        return this.getBinding(this.parameterMetadata.getQueryParameter(name));
    }

    @Override
    public <T> QueryParameterBinding<T> getBinding(int position) {
        return this.getBinding(this.parameterMetadata.getQueryParameter(position));
    }

    @Override
    public void verifyParametersBound(boolean callable) {
        this.parameterMetadata.visitRegistrations(queryParameter -> {
            ProcedureParameterImplementor procParam = (ProcedureParameterImplementor)queryParameter;
            if (procParam.getMode() != ParameterMode.IN && procParam.getMode() != ParameterMode.INOUT || !this.getBinding(procParam).isBound()) {
                // empty if block
            }
        });
    }

    @Override
    public String expandListValuedParameters(String queryString, SharedSessionContractImplementor producer) {
        return queryString;
    }

    @Override
    public <T> QueryParameterListBinding<T> getQueryParameterListBinding(QueryParameter<T> parameter) {
        return null;
    }

    @Override
    public <T> QueryParameterListBinding<T> getQueryParameterListBinding(String name) {
        return null;
    }

    @Override
    public <T> QueryParameterListBinding<T> getQueryParameterListBinding(int position) {
        return null;
    }

    @Override
    public Type[] collectPositionalBindTypes() {
        return new Type[0];
    }

    @Override
    public Object[] collectPositionalBindValues() {
        return new Object[0];
    }

    @Override
    public Map<String, TypedValue> collectNamedParameterBindings() {
        return null;
    }
}

