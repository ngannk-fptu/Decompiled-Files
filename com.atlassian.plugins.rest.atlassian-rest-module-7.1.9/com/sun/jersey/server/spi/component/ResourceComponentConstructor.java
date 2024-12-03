/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.spi.component;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceConstructor;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import com.sun.jersey.server.spi.component.ResourceComponentInjector;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.spi.inject.Injectable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import javax.ws.rs.WebApplicationException;

public class ResourceComponentConstructor {
    private final Class clazz;
    private final ResourceComponentInjector resourceComponentInjector;
    private final Constructor constructor;
    private final Constructor nonPublicConstructor;
    private final List<Method> postConstructs = new ArrayList<Method>();
    private final List<AbstractHttpContextInjectable> injectables;

    public ResourceComponentConstructor(ServerInjectableProviderContext serverInjectableProviderCtx, ComponentScope scope, AbstractResource abstractResource) {
        this.clazz = abstractResource.getResourceClass();
        int modifiers = this.clazz.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            Errors.nonPublicClass(this.clazz);
        }
        if (Modifier.isAbstract(modifiers)) {
            if (Modifier.isInterface(modifiers)) {
                Errors.interfaceClass(this.clazz);
            } else {
                Errors.abstractClass(this.clazz);
            }
        }
        if (this.clazz.getEnclosingClass() != null && !Modifier.isStatic(modifiers)) {
            Errors.innerClass(this.clazz);
        }
        if (Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers) && this.clazz.getConstructors().length == 0) {
            Errors.nonPublicConstructor(this.clazz);
        }
        this.resourceComponentInjector = new ResourceComponentInjector(serverInjectableProviderCtx, scope, abstractResource);
        this.postConstructs.addAll(abstractResource.getPostConstructMethods());
        ConstructorInjectablePair ctorInjectablePair = this.getConstructor(serverInjectableProviderCtx, scope, abstractResource);
        if (ctorInjectablePair == null) {
            this.constructor = null;
            this.nonPublicConstructor = this.getNonPublicConstructor();
            this.injectables = null;
        } else if (ctorInjectablePair.injectables.isEmpty()) {
            this.constructor = ctorInjectablePair.constructor;
            this.nonPublicConstructor = null;
            this.injectables = null;
        } else {
            if (ctorInjectablePair.injectables.contains(null)) {
                for (int i = 0; i < ctorInjectablePair.injectables.size(); ++i) {
                    if (ctorInjectablePair.injectables.get(i) != null) continue;
                    Errors.missingDependency(ctorInjectablePair.constructor, i);
                }
            }
            this.constructor = ctorInjectablePair.constructor;
            this.injectables = AbstractHttpContextInjectable.transform(ctorInjectablePair.injectables);
            if (this.constructor != null) {
                this.setAccessible(this.constructor);
                this.nonPublicConstructor = null;
            } else {
                this.nonPublicConstructor = this.injectables == null ? this.getNonPublicConstructor() : null;
            }
        }
    }

    private Constructor getNonPublicConstructor() {
        try {
            Constructor result = AccessController.doPrivileged(new PrivilegedExceptionAction<Constructor>(){

                @Override
                public Constructor run() throws NoSuchMethodException {
                    return ResourceComponentConstructor.this.clazz.getDeclaredConstructor(new Class[0]);
                }
            });
            this.setAccessible(result);
            return result;
        }
        catch (PrivilegedActionException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof NoSuchMethodException) {
                return null;
            }
            throw new WebApplicationException(cause);
        }
    }

    private void setAccessible(final Constructor constructor) {
        AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                constructor.setAccessible(true);
                return null;
            }
        });
    }

    public Class getResourceClass() {
        return this.clazz;
    }

    public Object construct(HttpContext hc) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object o = this._construct(hc);
        this.resourceComponentInjector.inject(hc, o);
        for (Method postConstruct : this.postConstructs) {
            postConstruct.invoke(o, new Object[0]);
        }
        return o;
    }

    private Object _construct(HttpContext httpContext) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (this.injectables == null) {
            return this.constructor != null ? this.constructor.newInstance(new Object[0]) : (this.nonPublicConstructor != null ? this.nonPublicConstructor.newInstance(new Object[0]) : this.clazz.newInstance());
        }
        Object[] params = new Object[this.injectables.size()];
        int i = 0;
        for (AbstractHttpContextInjectable injectable : this.injectables) {
            params[i++] = injectable != null ? injectable.getValue(httpContext) : null;
        }
        return this.constructor.newInstance(params);
    }

    private <T> ConstructorInjectablePair getConstructor(ServerInjectableProviderContext sipc, ComponentScope scope, AbstractResource ar) {
        if (ar.getConstructors().isEmpty()) {
            return null;
        }
        TreeSet<ConstructorInjectablePair> cs = new TreeSet<ConstructorInjectablePair>(new ConstructorComparator());
        for (AbstractResourceConstructor arc : ar.getConstructors()) {
            List<Injectable> is = sipc.getInjectable((AccessibleObject)arc.getCtor(), arc.getParameters(), scope);
            cs.add(new ConstructorInjectablePair(arc.getCtor(), is));
        }
        return (ConstructorInjectablePair)cs.first();
    }

    private static class ConstructorComparator<T>
    implements Comparator<ConstructorInjectablePair> {
        private ConstructorComparator() {
        }

        @Override
        public int compare(ConstructorInjectablePair o1, ConstructorInjectablePair o2) {
            int p = Collections.frequency(o1.injectables, null) - Collections.frequency(o2.injectables, null);
            if (p != 0) {
                return p;
            }
            return o2.constructor.getParameterTypes().length - o1.constructor.getParameterTypes().length;
        }
    }

    private static class ConstructorInjectablePair {
        private final Constructor constructor;
        private final List<Injectable> injectables;

        private ConstructorInjectablePair(Constructor constructor, List<Injectable> injectables) {
            this.constructor = constructor;
            this.injectables = injectables;
        }
    }
}

