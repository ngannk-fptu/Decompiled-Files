/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.JavaMethodInvoker;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

public interface ResourceMethodCustomInvokerDispatchProvider {
    public RequestDispatcher create(AbstractResourceMethod var1, JavaMethodInvoker var2);
}

