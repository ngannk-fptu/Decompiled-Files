/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.method.annotation;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.context.request.NativeWebRequest;
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
        CookieValue annotation = (CookieValue)parameter.getParameterAnnotation(CookieValue.class);
        Assert.state((annotation != null ? 1 : 0) != 0, (String)"No CookieValue annotation");
        return new CookieValueNamedValueInfo(annotation);
    }

    @Override
    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletRequestBindingException {
        throw new MissingRequestCookieException(name, parameter);
    }

    @Override
    protected void handleMissingValueAfterConversion(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        throw new MissingRequestCookieException(name, parameter, true);
    }

    private static final class CookieValueNamedValueInfo
    extends AbstractNamedValueMethodArgumentResolver.NamedValueInfo {
        private CookieValueNamedValueInfo(CookieValue annotation) {
            super(annotation.name(), annotation.required(), annotation.defaultValue());
        }
    }
}

