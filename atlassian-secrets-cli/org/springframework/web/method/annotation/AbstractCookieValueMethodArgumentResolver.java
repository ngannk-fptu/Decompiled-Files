/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.method.annotation;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;

public abstract class AbstractCookieValueMethodArgumentResolver
extends AbstractNamedValueMethodArgumentResolver {
    public AbstractCookieValueMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CookieValue.class);
    }

    @Override
    protected AbstractNamedValueMethodArgumentResolver.NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        CookieValue annotation = parameter.getParameterAnnotation(CookieValue.class);
        Assert.state(annotation != null, "No CookieValue annotation");
        return new CookieValueNamedValueInfo(annotation);
    }

    @Override
    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletRequestBindingException {
        throw new ServletRequestBindingException("Missing cookie '" + name + "' for method parameter of type " + parameter.getNestedParameterType().getSimpleName());
    }

    private static class CookieValueNamedValueInfo
    extends AbstractNamedValueMethodArgumentResolver.NamedValueInfo {
        private CookieValueNamedValueInfo(CookieValue annotation) {
            super(annotation.name(), annotation.required(), annotation.defaultValue());
        }
    }
}

