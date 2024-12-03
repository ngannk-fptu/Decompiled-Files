/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.data.activeobjects.repository.query;

import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.ParametersParameterAccessor;

public class ActiveObjectsParametersParameterAccessor
extends ParametersParameterAccessor {
    ActiveObjectsParametersParameterAccessor(Parameters<?, ?> parameters, Object[] values) {
        super(parameters, values);
    }

    public <T> T getValue(Parameter parameter) {
        return super.getValue(parameter.getIndex());
    }
}

