/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.component.ioc;

import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessor;

public interface IoCComponentProcessorFactory {
    public ComponentScope getScope(Class var1);

    public IoCComponentProcessor get(Class var1, ComponentScope var2);
}

