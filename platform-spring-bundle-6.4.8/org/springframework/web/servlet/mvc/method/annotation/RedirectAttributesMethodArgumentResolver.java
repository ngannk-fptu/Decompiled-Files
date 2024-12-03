/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

public class RedirectAttributesMethodArgumentResolver
implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return RedirectAttributes.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        RedirectAttributesModelMap redirectAttributes;
        Assert.state(mavContainer != null, "RedirectAttributes argument only supported on regular handler methods");
        if (binderFactory != null) {
            WebDataBinder dataBinder = binderFactory.createBinder(webRequest, null, "target");
            redirectAttributes = new RedirectAttributesModelMap(dataBinder);
        } else {
            redirectAttributes = new RedirectAttributesModelMap();
        }
        mavContainer.setRedirectModel(redirectAttributes);
        return redirectAttributes;
    }
}

