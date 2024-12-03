/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.spi.component;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractField;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractSetterMethod;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ResourceComponentInjector {
    private Field[] singletonFields;
    private Object[] singletonFieldValues;
    private Field[] perRequestFields;
    private AbstractHttpContextInjectable<?>[] perRequestFieldInjectables;
    private Method[] singletonSetters;
    private Object[] singletonSetterValues;
    private Method[] perRequestSetters;
    private AbstractHttpContextInjectable<?>[] perRequestSetterInjectables;

    public ResourceComponentInjector(ServerInjectableProviderContext ipc, ComponentScope s, AbstractResource resource) {
        this.processFields(ipc, s, resource.getFields());
        this.processSetters(ipc, s, resource.getSetterMethods());
    }

    public boolean hasInjectableArtifacts() {
        return this.singletonFields.length > 0 || this.perRequestFields.length > 0 || this.singletonSetters.length > 0 || this.perRequestSetters.length > 0;
    }

    private void processFields(ServerInjectableProviderContext ipc, ComponentScope s, List<AbstractField> fields) {
        HashMap<Field, Injectable> singletons = new HashMap<Field, Injectable>();
        HashMap<Field, Injectable> perRequest = new HashMap<Field, Injectable>();
        for (AbstractField af : fields) {
            Parameter p = af.getParameters().get(0);
            InjectableProviderContext.InjectableScopePair isp = ipc.getInjectableiWithScope(af.getField(), p, s);
            if (isp != null) {
                this.configureField(af.getField());
                if (s == ComponentScope.PerRequest && isp.cs != ComponentScope.Singleton) {
                    perRequest.put(af.getField(), isp.i);
                    continue;
                }
                singletons.put(af.getField(), isp.i);
                continue;
            }
            if (!ipc.isParameterTypeRegistered(p)) continue;
            Errors.missingDependency(af.getField());
        }
        int size = singletons.entrySet().size();
        this.singletonFields = new Field[size];
        this.singletonFieldValues = new Object[size];
        int i = 0;
        for (Map.Entry e : singletons.entrySet()) {
            this.singletonFields[i] = (Field)e.getKey();
            this.singletonFieldValues[i++] = ((Injectable)e.getValue()).getValue();
        }
        size = perRequest.entrySet().size();
        this.perRequestFields = new Field[size];
        this.perRequestFieldInjectables = new AbstractHttpContextInjectable[size];
        i = 0;
        for (Map.Entry e : perRequest.entrySet()) {
            this.perRequestFields[i] = (Field)e.getKey();
            this.perRequestFieldInjectables[i++] = AbstractHttpContextInjectable.transform((Injectable)e.getValue());
        }
    }

    private void configureField(final Field f) {
        if (!f.isAccessible()) {
            AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    f.setAccessible(true);
                    return null;
                }
            });
        }
    }

    private void processSetters(ServerInjectableProviderContext ipc, ComponentScope s, List<AbstractSetterMethod> setterMethods) {
        HashMap<Method, Injectable> singletons = new HashMap<Method, Injectable>();
        HashMap<Method, Injectable> perRequest = new HashMap<Method, Injectable>();
        int methodIndex = 0;
        for (AbstractSetterMethod sm : setterMethods) {
            Parameter p = sm.getParameters().get(0);
            InjectableProviderContext.InjectableScopePair isp = ipc.getInjectableiWithScope(sm.getMethod(), p, s);
            if (isp != null) {
                if (s == ComponentScope.PerRequest && isp.cs != ComponentScope.Singleton) {
                    perRequest.put(sm.getMethod(), isp.i);
                } else {
                    singletons.put(sm.getMethod(), isp.i);
                }
            } else if (ipc.isParameterTypeRegistered(p)) {
                Errors.missingDependency(sm.getMethod(), methodIndex);
            }
            ++methodIndex;
        }
        int size = singletons.entrySet().size();
        this.singletonSetters = new Method[size];
        this.singletonSetterValues = new Object[size];
        int i = 0;
        for (Map.Entry e : singletons.entrySet()) {
            this.singletonSetters[i] = (Method)e.getKey();
            this.singletonSetterValues[i++] = ((Injectable)e.getValue()).getValue();
        }
        size = perRequest.entrySet().size();
        this.perRequestSetters = new Method[size];
        this.perRequestSetterInjectables = new AbstractHttpContextInjectable[size];
        i = 0;
        for (Map.Entry e : perRequest.entrySet()) {
            this.perRequestSetters[i] = (Method)e.getKey();
            this.perRequestSetterInjectables[i++] = AbstractHttpContextInjectable.transform((Injectable)e.getValue());
        }
    }

    public void inject(HttpContext c, Object o) {
        int i = 0;
        for (Field field : this.singletonFields) {
            try {
                field.set(o, this.singletonFieldValues[i++]);
            }
            catch (IllegalAccessException ex) {
                throw new ContainerException(ex);
            }
        }
        i = 0;
        for (Field field : this.perRequestFields) {
            try {
                field.set(o, this.perRequestFieldInjectables[i++].getValue(c));
            }
            catch (IllegalAccessException ex) {
                throw new ContainerException(ex);
            }
        }
        i = 0;
        for (AccessibleObject accessibleObject : this.singletonSetters) {
            try {
                ((Method)accessibleObject).invoke(o, this.singletonSetterValues[i++]);
            }
            catch (Exception ex) {
                throw new ContainerException(ex);
            }
        }
        i = 0;
        for (AccessibleObject accessibleObject : this.perRequestSetters) {
            try {
                ((Method)accessibleObject).invoke(o, this.perRequestSetterInjectables[i++].getValue(c));
            }
            catch (Exception ex) {
                throw new ContainerException(ex);
            }
        }
    }
}

