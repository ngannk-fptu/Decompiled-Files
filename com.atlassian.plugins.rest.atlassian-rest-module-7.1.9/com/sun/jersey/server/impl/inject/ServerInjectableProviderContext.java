/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.inject;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import java.lang.reflect.AccessibleObject;
import java.util.List;

public interface ServerInjectableProviderContext
extends InjectableProviderContext {
    public boolean isParameterTypeRegistered(Parameter var1);

    public Injectable getInjectable(Parameter var1, ComponentScope var2);

    public Injectable getInjectable(AccessibleObject var1, Parameter var2, ComponentScope var3);

    public InjectableProviderContext.InjectableScopePair getInjectableiWithScope(Parameter var1, ComponentScope var2);

    public InjectableProviderContext.InjectableScopePair getInjectableiWithScope(AccessibleObject var1, Parameter var2, ComponentScope var3);

    public List<Injectable> getInjectable(List<Parameter> var1, ComponentScope var2);

    public List<Injectable> getInjectable(AccessibleObject var1, List<Parameter> var2, ComponentScope var3);
}

