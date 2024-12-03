/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.parameter.multivalued;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.server.impl.model.parameter.multivalued.ExtractorContainerException;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;

final class PrimitiveValueOfExtractor
implements MultivaluedParameterExtractor {
    final Method valueOf;
    final String parameter;
    final String defaultStringValue;
    final Object defaultValue;
    final Object defaultDefaultValue;

    public PrimitiveValueOfExtractor(Method valueOf, String parameter, String defaultStringValue, Object defaultDefaultValue) throws IllegalAccessException, InvocationTargetException {
        this.valueOf = valueOf;
        this.parameter = parameter;
        this.defaultStringValue = defaultStringValue;
        this.defaultValue = defaultStringValue != null ? this.getValue(defaultStringValue) : null;
        this.defaultDefaultValue = defaultDefaultValue;
    }

    @Override
    public String getName() {
        return this.parameter;
    }

    @Override
    public String getDefaultStringValue() {
        return this.defaultStringValue;
    }

    private Object getValue(String v) {
        try {
            return this.valueOf.invoke(null, v);
        }
        catch (InvocationTargetException ex) {
            Throwable target = ex.getTargetException();
            if (target instanceof WebApplicationException) {
                throw (WebApplicationException)target;
            }
            throw new ExtractorContainerException(target);
        }
        catch (RuntimeException ex) {
            throw new ContainerException(ex);
        }
        catch (Exception ex) {
            throw new ContainerException(ex);
        }
    }

    @Override
    public Object extract(MultivaluedMap<String, String> parameters) {
        String v = parameters.getFirst(this.parameter);
        if (v != null && !v.trim().isEmpty()) {
            return this.getValue(v);
        }
        if (this.defaultValue != null) {
            return this.defaultValue;
        }
        return this.defaultDefaultValue;
    }
}

