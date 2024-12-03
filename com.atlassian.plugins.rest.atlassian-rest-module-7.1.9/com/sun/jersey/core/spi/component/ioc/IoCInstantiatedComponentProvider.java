/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.component.ioc;

import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;

public interface IoCInstantiatedComponentProvider
extends IoCComponentProvider {
    public Object getInjectableInstance(Object var1);
}

