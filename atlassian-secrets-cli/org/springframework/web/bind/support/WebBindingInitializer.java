/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.bind.support;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.WebRequest;

public interface WebBindingInitializer {
    public void initBinder(WebDataBinder var1);

    @Deprecated
    default public void initBinder(WebDataBinder binder, WebRequest request) {
        this.initBinder(binder);
    }
}

