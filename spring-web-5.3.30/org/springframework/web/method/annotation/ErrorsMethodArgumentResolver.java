/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 *  org.springframework.ui.ModelMap
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.validation.BindingResult
 *  org.springframework.validation.Errors
 */
package org.springframework.web.method.annotation;

import java.util.Set;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class ErrorsMethodArgumentResolver
implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class paramType = parameter.getParameterType();
        return Errors.class.isAssignableFrom(paramType);
    }

    @Override
    @Nullable
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Assert.state((mavContainer != null ? 1 : 0) != 0, (String)"Errors/BindingResult argument only supported on regular handler methods");
        ModelMap model = mavContainer.getModel();
        String lastKey = (String)CollectionUtils.lastElement((Set)model.keySet());
        if (lastKey != null && lastKey.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
            return model.get((Object)lastKey);
        }
        throw new IllegalStateException("An Errors/BindingResult argument is expected to be declared immediately after the model attribute, the @RequestBody or the @RequestPart arguments to which they apply: " + parameter.getMethod());
    }
}

