/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 */
package org.springframework.data.repository.query;

import java.lang.reflect.Method;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;

public final class DefaultParameters
extends Parameters<DefaultParameters, Parameter> {
    public DefaultParameters(Method method) {
        super(method);
    }

    private DefaultParameters(List<Parameter> parameters) {
        super(parameters);
    }

    @Override
    protected Parameter createParameter(MethodParameter parameter) {
        return new Parameter(parameter);
    }

    @Override
    protected DefaultParameters createFrom(List<Parameter> parameters) {
        return new DefaultParameters(parameters);
    }
}

