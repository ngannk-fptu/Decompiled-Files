/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.method;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.server.impl.model.method.ResourceMethod;

public final class ResourceHeadWrapperMethod
extends ResourceMethod {
    private final ResourceMethod m;

    public ResourceHeadWrapperMethod(ResourceMethod m) {
        super("HEAD", m.getTemplate(), m.getConsumes(), m.getProduces(), m.isProducesDeclared(), m.getDispatcher(), m.getRequestFilters(), m.getResponseFilters());
        if (!m.getHttpMethod().equals("GET")) {
            throw new ContainerException("");
        }
        this.m = m;
    }

    public String toString() {
        return this.m.toString();
    }
}

