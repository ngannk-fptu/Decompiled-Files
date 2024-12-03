/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.component.ioc;

import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;

public interface IoCProxiedComponentProvider
extends IoCComponentProvider {
    @Override
    public Object getInstance();

    public Object proxy(Object var1);
}

