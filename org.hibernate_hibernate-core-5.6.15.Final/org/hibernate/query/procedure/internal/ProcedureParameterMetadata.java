/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Parameter
 */
package org.hibernate.query.procedure.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.persistence.Parameter;
import org.hibernate.procedure.internal.ProcedureCallImpl;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;
import org.hibernate.procedure.spi.ParameterStrategy;
import org.hibernate.query.ParameterMetadata;
import org.hibernate.query.QueryParameter;
import org.hibernate.query.procedure.ProcedureParameter;
import org.hibernate.query.procedure.spi.ProcedureParameterImplementor;

public class ProcedureParameterMetadata
implements ParameterMetadata {
    private final ProcedureCallImpl procedureCall;
    private ParameterStrategy parameterStrategy = ParameterStrategy.UNKNOWN;
    private List<ProcedureParameterImplementor> parameters = new ArrayList<ProcedureParameterImplementor>();

    public ProcedureParameterMetadata(ProcedureCallImpl procedureCall) {
        this.procedureCall = procedureCall;
    }

    public void registerParameter(ProcedureParameterImplementor parameter) {
        if (parameter.getName() != null) {
            if (this.parameterStrategy == ParameterStrategy.POSITIONAL) {
                throw new IllegalArgumentException("Cannot mix named parameter with positional parameter registrations");
            }
            this.parameterStrategy = ParameterStrategy.NAMED;
        } else if (parameter.getPosition() != null) {
            if (this.parameterStrategy == ParameterStrategy.NAMED) {
                throw new IllegalArgumentException("Cannot mix positional parameter with named parameter registrations");
            }
            this.parameterStrategy = ParameterStrategy.POSITIONAL;
        } else {
            throw new IllegalArgumentException("Unrecognized parameter type : " + parameter);
        }
        if (this.parameters == null) {
            this.parameters = new ArrayList<ProcedureParameterImplementor>();
        }
        this.parameters.add(parameter);
    }

    @Override
    public boolean hasNamedParameters() {
        return this.parameterStrategy == ParameterStrategy.NAMED;
    }

    @Override
    public boolean hasPositionalParameters() {
        return this.parameterStrategy == ParameterStrategy.POSITIONAL;
    }

    @Override
    public Set<QueryParameter<?>> collectAllParameters() {
        LinkedHashSet rtn = new LinkedHashSet();
        for (ProcedureParameter procedureParameter : this.parameters) {
            rtn.add(procedureParameter);
        }
        return rtn;
    }

    @Override
    public Set<Parameter<?>> collectAllParametersJpa() {
        LinkedHashSet rtn = new LinkedHashSet();
        for (ProcedureParameter procedureParameter : this.parameters) {
            rtn.add(procedureParameter);
        }
        return rtn;
    }

    @Override
    public Set<String> getNamedParameterNames() {
        if (!this.hasNamedParameters()) {
            return Collections.emptySet();
        }
        HashSet<String> rtn = new HashSet<String>();
        for (ProcedureParameter procedureParameter : this.parameters) {
            if (procedureParameter.getName() == null) continue;
            rtn.add(procedureParameter.getName());
        }
        return rtn;
    }

    @Override
    public int getPositionalParameterCount() {
        return this.hasPositionalParameters() ? this.parameters.size() : 0;
    }

    public <T> ParameterRegistrationImplementor<T> getQueryParameter(String name) {
        assert (name != null);
        if (this.hasNamedParameters()) {
            for (ParameterRegistrationImplementor parameterRegistrationImplementor : this.parameters) {
                if (!name.equals(parameterRegistrationImplementor.getName())) continue;
                return parameterRegistrationImplementor;
            }
        }
        throw new IllegalArgumentException("Named parameter [" + name + "] is not registered with this procedure call");
    }

    public <T> ParameterRegistrationImplementor<T> getQueryParameter(Integer position) {
        assert (position != null);
        if (this.hasPositionalParameters()) {
            for (ParameterRegistrationImplementor parameterRegistrationImplementor : this.parameters) {
                if (parameterRegistrationImplementor.getPosition() == null || position.intValue() != parameterRegistrationImplementor.getPosition().intValue()) continue;
                return parameterRegistrationImplementor;
            }
        }
        throw new IllegalArgumentException("Positional parameter [" + position + "] is not registered with this procedure call");
    }

    public <T> ProcedureParameterImplementor<T> resolve(Parameter<T> param) {
        if (ProcedureParameterImplementor.class.isInstance(param)) {
            for (ProcedureParameterImplementor parameter : this.parameters) {
                if (parameter != param) continue;
                return parameter;
            }
        }
        throw new IllegalArgumentException("Could not resolve javax.persistence.Parameter to org.hibernate.query.QueryParameter");
    }

    @Override
    public Collection<QueryParameter> getPositionalParameters() {
        return this.parameters.stream().filter(p -> p.getPosition() != null).collect(Collectors.toList());
    }

    @Override
    public Collection<QueryParameter> getNamedParameters() {
        return this.parameters.stream().filter(p -> p.getPosition() == null).collect(Collectors.toList());
    }

    @Override
    public int getParameterCount() {
        return this.parameters.size();
    }

    @Override
    public boolean containsReference(QueryParameter parameter) {
        return this.parameters.contains(parameter);
    }

    public ParameterStrategy getParameterStrategy() {
        return this.parameterStrategy;
    }

    @Override
    public void visitRegistrations(Consumer<QueryParameter> action) {
        for (ProcedureParameterImplementor parameter : this.parameters) {
            action.accept(parameter);
        }
    }
}

