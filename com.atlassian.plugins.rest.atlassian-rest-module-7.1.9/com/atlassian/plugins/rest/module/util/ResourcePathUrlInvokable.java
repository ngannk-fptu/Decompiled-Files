/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.module.util;

import com.atlassian.plugins.rest.module.util.GeneratedURIResponse;
import com.atlassian.plugins.rest.module.util.ResourceInvokable;
import java.lang.reflect.Method;
import java.net.URI;

class ResourcePathUrlInvokable
extends ResourceInvokable {
    ResourcePathUrlInvokable(Class<?> resourceClass, URI baseUri) {
        super(resourceClass, baseUri);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return new GeneratedURIResponse(this.getURI(method, args));
    }
}

