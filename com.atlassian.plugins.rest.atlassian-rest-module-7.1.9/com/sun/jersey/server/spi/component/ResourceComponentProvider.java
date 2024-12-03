/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.spi.component;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.core.spi.component.ComponentProvider;
import com.sun.jersey.core.spi.component.ComponentScope;

public interface ResourceComponentProvider
extends ComponentProvider {
    public void init(AbstractResource var1);

    public ComponentScope getScope();

    public Object getInstance(HttpContext var1);

    public void destroy();
}

