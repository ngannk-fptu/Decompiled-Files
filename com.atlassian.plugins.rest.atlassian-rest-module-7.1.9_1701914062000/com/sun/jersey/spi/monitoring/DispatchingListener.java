/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.monitoring;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.AbstractSubResourceLocator;

public interface DispatchingListener {
    public void onSubResource(long var1, Class var3);

    public void onSubResourceLocator(long var1, AbstractSubResourceLocator var3);

    public void onResourceMethod(long var1, AbstractResourceMethod var3);
}

