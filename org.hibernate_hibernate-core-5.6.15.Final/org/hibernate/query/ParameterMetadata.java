/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Parameter
 */
package org.hibernate.query;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import javax.persistence.Parameter;
import org.hibernate.query.QueryParameter;

public interface ParameterMetadata {
    public boolean hasNamedParameters();

    public boolean hasPositionalParameters();

    public Set<QueryParameter<?>> collectAllParameters();

    public Set<Parameter<?>> collectAllParametersJpa();

    public Set<String> getNamedParameterNames();

    public int getPositionalParameterCount();

    public <T> QueryParameter<T> getQueryParameter(String var1);

    public <T> QueryParameter<T> getQueryParameter(Integer var1);

    public <T> QueryParameter<T> resolve(Parameter<T> var1);

    public Collection<QueryParameter> getPositionalParameters();

    public Collection<QueryParameter> getNamedParameters();

    public int getParameterCount();

    public boolean containsReference(QueryParameter var1);

    public void visitRegistrations(Consumer<QueryParameter> var1);
}

