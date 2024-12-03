/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.spi;

import java.util.Map;
import org.hibernate.Incubating;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.query.QueryParameter;
import org.hibernate.query.spi.QueryParameterBinding;
import org.hibernate.query.spi.QueryParameterListBinding;
import org.hibernate.type.Type;

@Incubating
public interface QueryParameterBindings {
    public boolean isBound(QueryParameter var1);

    public <T> QueryParameterBinding<T> getBinding(QueryParameter<T> var1);

    public <T> QueryParameterBinding<T> getBinding(String var1);

    public <T> QueryParameterBinding<T> getBinding(int var1);

    public void verifyParametersBound(boolean var1);

    public String expandListValuedParameters(String var1, SharedSessionContractImplementor var2);

    public <T> QueryParameterListBinding<T> getQueryParameterListBinding(QueryParameter<T> var1);

    public <T> QueryParameterListBinding<T> getQueryParameterListBinding(String var1);

    public <T> QueryParameterListBinding<T> getQueryParameterListBinding(int var1);

    public Type[] collectPositionalBindTypes();

    public Object[] collectPositionalBindValues();

    public Map<String, TypedValue> collectNamedParameterBindings();

    @Deprecated
    default public boolean isMultiValuedBinding(QueryParameter parameter) {
        return false;
    }
}

