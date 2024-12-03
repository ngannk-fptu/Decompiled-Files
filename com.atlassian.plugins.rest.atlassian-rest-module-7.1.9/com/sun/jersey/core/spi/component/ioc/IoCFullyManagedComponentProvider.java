/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.component.ioc;

import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;

public interface IoCFullyManagedComponentProvider
extends IoCComponentProvider {
    public ComponentScope getScope();
}

