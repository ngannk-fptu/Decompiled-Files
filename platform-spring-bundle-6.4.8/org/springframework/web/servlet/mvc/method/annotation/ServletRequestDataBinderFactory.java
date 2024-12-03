/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.InitBinderDataBinderFactory;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

public class ServletRequestDataBinderFactory
extends InitBinderDataBinderFactory {
    public ServletRequestDataBinderFactory(@Nullable List<InvocableHandlerMethod> binderMethods, @Nullable WebBindingInitializer initializer) {
        super(binderMethods, initializer);
    }

    @Override
    protected ServletRequestDataBinder createBinderInstance(@Nullable Object target, String objectName, NativeWebRequest request) throws Exception {
        return new ExtendedServletRequestDataBinder(target, objectName);
    }
}

