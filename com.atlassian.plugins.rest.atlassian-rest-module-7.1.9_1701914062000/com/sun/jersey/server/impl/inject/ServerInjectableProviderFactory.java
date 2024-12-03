/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.inject;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.AnnotatedContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.factory.InjectableProviderFactory;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.List;

public final class ServerInjectableProviderFactory
extends InjectableProviderFactory
implements ServerInjectableProviderContext {
    @Override
    public boolean isParameterTypeRegistered(Parameter p) {
        if (p.getAnnotation() == null) {
            return false;
        }
        if (this.isAnnotationRegistered(p.getAnnotation().annotationType(), p.getClass())) {
            return true;
        }
        return this.isAnnotationRegistered(p.getAnnotation().annotationType(), p.getParameterType().getClass());
    }

    @Override
    public InjectableProviderContext.InjectableScopePair getInjectableiWithScope(Parameter p, ComponentScope s) {
        return this.getInjectableiWithScope(null, p, s);
    }

    @Override
    public InjectableProviderContext.InjectableScopePair getInjectableiWithScope(AccessibleObject ao, Parameter p, ComponentScope s) {
        if (p.getAnnotation() == null) {
            return null;
        }
        AnnotatedContext ic = new AnnotatedContext(ao, p.getAnnotations());
        if (s == ComponentScope.PerRequest) {
            Injectable i = this.getInjectable(p.getAnnotation().annotationType(), (ComponentContext)ic, p.getAnnotation(), p, ComponentScope.PerRequest);
            if (i != null) {
                return new InjectableProviderContext.InjectableScopePair(i, ComponentScope.PerRequest);
            }
            return this.getInjectableWithScope(p.getAnnotation().annotationType(), ic, p.getAnnotation(), p.getParameterType(), ComponentScope.PERREQUEST_UNDEFINED_SINGLETON);
        }
        return this.getInjectableWithScope(p.getAnnotation().annotationType(), ic, p.getAnnotation(), p.getParameterType(), ComponentScope.UNDEFINED_SINGLETON);
    }

    @Override
    public Injectable getInjectable(Parameter p, ComponentScope s) {
        return this.getInjectable(null, p, s);
    }

    @Override
    public Injectable getInjectable(AccessibleObject ao, Parameter p, ComponentScope s) {
        InjectableProviderContext.InjectableScopePair isp = this.getInjectableiWithScope(ao, p, s);
        if (isp == null) {
            return null;
        }
        return isp.i;
    }

    @Override
    public List<Injectable> getInjectable(List<Parameter> ps, ComponentScope s) {
        return this.getInjectable(null, ps, s);
    }

    @Override
    public List<Injectable> getInjectable(AccessibleObject ao, List<Parameter> ps, ComponentScope s) {
        ArrayList<Injectable> is = new ArrayList<Injectable>();
        for (Parameter p : ps) {
            is.add(this.getInjectable(ao, p, s));
        }
        return is;
    }
}

