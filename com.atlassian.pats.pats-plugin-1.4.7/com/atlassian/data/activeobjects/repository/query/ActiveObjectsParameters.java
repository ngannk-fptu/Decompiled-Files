/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 */
package com.atlassian.data.activeobjects.repository.query;

import java.lang.reflect.Method;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;

public class ActiveObjectsParameters
extends Parameters<ActiveObjectsParameters, ActiveObjectsParameter> {
    public ActiveObjectsParameters(Method method) {
        super(method);
    }

    private ActiveObjectsParameters(List<ActiveObjectsParameter> parameters) {
        super(parameters);
    }

    @Override
    protected ActiveObjectsParameter createParameter(MethodParameter parameter) {
        return new ActiveObjectsParameter(parameter);
    }

    @Override
    protected ActiveObjectsParameters createFrom(List<ActiveObjectsParameter> parameters) {
        return new ActiveObjectsParameters(parameters);
    }

    static class ActiveObjectsParameter
    extends Parameter {
        protected ActiveObjectsParameter(MethodParameter parameter) {
            super(parameter);
        }
    }
}

