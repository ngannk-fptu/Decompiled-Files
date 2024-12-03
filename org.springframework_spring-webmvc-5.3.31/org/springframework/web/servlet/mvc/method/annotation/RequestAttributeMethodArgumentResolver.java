/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.bind.ServletRequestBindingException
 *  org.springframework.web.bind.annotation.RequestAttribute
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
 *  org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver$NamedValueInfo
 */
package org.springframework.web.servlet.mvc.method.annotation;

import javax.servlet.ServletException;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;

public class RequestAttributeMethodArgumentResolver
extends AbstractNamedValueMethodArgumentResolver {
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestAttribute.class);
    }

    protected AbstractNamedValueMethodArgumentResolver.NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        RequestAttribute ann = (RequestAttribute)parameter.getParameterAnnotation(RequestAttribute.class);
        Assert.state((ann != null ? 1 : 0) != 0, (String)"No RequestAttribute annotation");
        return new AbstractNamedValueMethodArgumentResolver.NamedValueInfo(ann.name(), ann.required(), "\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n");
    }

    @Nullable
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) {
        return request.getAttribute(name, 0);
    }

    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletException {
        throw new ServletRequestBindingException("Missing request attribute '" + name + "' of type " + parameter.getNestedParameterType().getSimpleName());
    }
}

