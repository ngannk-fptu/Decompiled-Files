/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.bind.support;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.NativeWebRequest;

public class DefaultDataBinderFactory
implements WebDataBinderFactory {
    @Nullable
    private final WebBindingInitializer initializer;

    public DefaultDataBinderFactory(@Nullable WebBindingInitializer initializer) {
        this.initializer = initializer;
    }

    @Override
    public final WebDataBinder createBinder(NativeWebRequest webRequest, @Nullable Object target, String objectName) throws Exception {
        WebDataBinder dataBinder = this.createBinderInstance(target, objectName, webRequest);
        if (this.initializer != null) {
            this.initializer.initBinder(dataBinder, webRequest);
        }
        this.initBinder(dataBinder, webRequest);
        return dataBinder;
    }

    protected WebDataBinder createBinderInstance(@Nullable Object target, String objectName, NativeWebRequest webRequest) throws Exception {
        return new WebRequestDataBinder(target, objectName);
    }

    protected void initBinder(WebDataBinder dataBinder, NativeWebRequest webRequest) throws Exception {
    }
}

