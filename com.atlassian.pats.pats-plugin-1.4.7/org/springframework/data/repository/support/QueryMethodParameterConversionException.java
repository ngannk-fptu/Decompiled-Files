/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.convert.ConversionException
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.support;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionException;
import org.springframework.util.Assert;

public class QueryMethodParameterConversionException
extends RuntimeException {
    private static final long serialVersionUID = -5818002272039533066L;
    private final Object source;
    private final MethodParameter parameter;

    public QueryMethodParameterConversionException(Object source, MethodParameter parameter, ConversionException cause) {
        super(String.format("Failed to convert %s into %s!", source, parameter.getParameterType().getName()), (Throwable)cause);
        Assert.notNull((Object)parameter, (String)"Method parameter must not be null!");
        Assert.notNull((Object)cause, (String)"ConversionException must not be null!");
        this.parameter = parameter;
        this.source = source;
    }

    public Object getSource() {
        return this.source;
    }

    public MethodParameter getParameter() {
        return this.parameter;
    }
}

