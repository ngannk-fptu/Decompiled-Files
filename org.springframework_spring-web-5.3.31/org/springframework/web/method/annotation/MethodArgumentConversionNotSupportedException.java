/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.ConversionNotSupportedException
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.method.annotation;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;

public class MethodArgumentConversionNotSupportedException
extends ConversionNotSupportedException {
    private final String name;
    private final MethodParameter parameter;

    public MethodArgumentConversionNotSupportedException(@Nullable Object value, @Nullable Class<?> requiredType, String name, MethodParameter param, Throwable cause) {
        super(value, requiredType, cause);
        this.name = name;
        this.parameter = param;
    }

    public String getName() {
        return this.name;
    }

    public MethodParameter getParameter() {
        return this.parameter;
    }
}

