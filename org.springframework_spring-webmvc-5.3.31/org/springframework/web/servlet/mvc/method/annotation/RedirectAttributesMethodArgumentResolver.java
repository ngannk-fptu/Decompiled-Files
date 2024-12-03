/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 *  org.springframework.ui.ModelMap
 *  org.springframework.util.Assert
 *  org.springframework.validation.DataBinder
 *  org.springframework.web.bind.WebDataBinder
 *  org.springframework.web.bind.support.WebDataBinderFactory
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.support.HandlerMethodArgumentResolver
 *  org.springframework.web.method.support.ModelAndViewContainer
 */
package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

public class RedirectAttributesMethodArgumentResolver
implements HandlerMethodArgumentResolver {
    public boolean supportsParameter(MethodParameter parameter) {
        return RedirectAttributes.class.isAssignableFrom(parameter.getParameterType());
    }

    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        RedirectAttributesModelMap redirectAttributes;
        Assert.state((mavContainer != null ? 1 : 0) != 0, (String)"RedirectAttributes argument only supported on regular handler methods");
        if (binderFactory != null) {
            WebDataBinder dataBinder = binderFactory.createBinder(webRequest, null, "target");
            redirectAttributes = new RedirectAttributesModelMap((DataBinder)dataBinder);
        } else {
            redirectAttributes = new RedirectAttributesModelMap();
        }
        mavContainer.setRedirectModel((ModelMap)redirectAttributes);
        return redirectAttributes;
    }
}

