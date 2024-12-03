/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.web.method.annotation;

import java.util.Collections;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.InvocableHandlerMethod;

public class InitBinderDataBinderFactory
extends DefaultDataBinderFactory {
    private final List<InvocableHandlerMethod> binderMethods;

    public InitBinderDataBinderFactory(@Nullable List<InvocableHandlerMethod> binderMethods, @Nullable WebBindingInitializer initializer) {
        super(initializer);
        this.binderMethods = binderMethods != null ? binderMethods : Collections.emptyList();
    }

    @Override
    public void initBinder(WebDataBinder dataBinder, NativeWebRequest request) throws Exception {
        for (InvocableHandlerMethod binderMethod : this.binderMethods) {
            Object returnValue;
            if (!this.isBinderMethodApplicable(binderMethod, dataBinder) || (returnValue = binderMethod.invokeForRequest(request, null, new Object[]{dataBinder})) == null) continue;
            throw new IllegalStateException("@InitBinder methods must not return a value (should be void): " + binderMethod);
        }
    }

    protected boolean isBinderMethodApplicable(HandlerMethod initBinderMethod, WebDataBinder dataBinder) {
        InitBinder ann = initBinderMethod.getMethodAnnotation(InitBinder.class);
        Assert.state((ann != null ? 1 : 0) != 0, (String)"No InitBinder annotation");
        Object[] names = ann.value();
        return ObjectUtils.isEmpty((Object[])names) || ObjectUtils.containsElement((Object[])names, (Object)dataBinder.getObjectName());
    }
}

