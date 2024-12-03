/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.factory;

import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ProviderServices;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InjectableProviderFactory
implements InjectableProviderContext {
    private final Map<Class<? extends Annotation>, LinkedList<MetaInjectableProvider>> ipm = new HashMap<Class<? extends Annotation>, LinkedList<MetaInjectableProvider>>();

    public final void update(InjectableProviderFactory ipf) {
        for (Map.Entry<Class<? extends Annotation>, LinkedList<MetaInjectableProvider>> e : ipf.ipm.entrySet()) {
            this.getList(e.getKey()).addAll((Collection<MetaInjectableProvider>)e.getValue());
        }
    }

    public final void add(InjectableProvider ip) {
        Type[] args = this.getMetaArguments(ip.getClass());
        if (args != null) {
            MetaInjectableProvider mip = new MetaInjectableProvider(ip, (Class)args[0], (Class)args[1]);
            this.getList(mip.ac).add(mip);
        }
    }

    public final void configure(ProviderServices providerServices) {
        providerServices.getProvidersAndServices(InjectableProvider.class, new ProviderServices.ProviderListener<InjectableProvider>(){

            @Override
            public void onAdd(InjectableProvider ip) {
                InjectableProviderFactory.this.add(ip);
            }
        });
    }

    public final void configureProviders(ProviderServices providerServices) {
        providerServices.getProviders(InjectableProvider.class, new ProviderServices.ProviderListener<InjectableProvider>(){

            @Override
            public void onAdd(InjectableProvider ip) {
                InjectableProviderFactory.this.add(ip);
            }
        });
    }

    private LinkedList<MetaInjectableProvider> getList(Class<? extends Annotation> c) {
        LinkedList<MetaInjectableProvider> l = this.ipm.get(c);
        if (l == null) {
            l = new LinkedList();
            this.ipm.put(c, l);
        }
        return l;
    }

    private Type[] getMetaArguments(Class<? extends InjectableProvider> c) {
        for (Class<? extends InjectableProvider> _c = c; _c != Object.class; _c = _c.getSuperclass()) {
            Type[] ts;
            for (Type t : ts = _c.getGenericInterfaces()) {
                ParameterizedType pt;
                if (!(t instanceof ParameterizedType) || (pt = (ParameterizedType)t).getRawType() != InjectableProvider.class) continue;
                Type[] args = pt.getActualTypeArguments();
                for (int i = 0; i < args.length; ++i) {
                    args[i] = this.getResolvedType(args[i], c, _c);
                }
                if (!(args[0] instanceof Class) || !(args[1] instanceof Class)) continue;
                return args;
            }
        }
        return null;
    }

    private Type getResolvedType(Type t, Class c, Class dc) {
        if (t instanceof Class) {
            return t;
        }
        if (t instanceof TypeVariable) {
            ReflectionHelper.ClassTypePair ct = ReflectionHelper.resolveTypeVariable(c, dc, (TypeVariable)t);
            if (ct != null) {
                return ct.c;
            }
            return t;
        }
        if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)t;
            return pt.getRawType();
        }
        return t;
    }

    private List<MetaInjectableProvider> findInjectableProviders(Class<? extends Annotation> ac, Class<?> cc, ComponentScope s) {
        ArrayList<MetaInjectableProvider> subips = new ArrayList<MetaInjectableProvider>();
        for (MetaInjectableProvider i : this.getList(ac)) {
            if (s != i.ip.getScope() || !i.cc.isAssignableFrom(cc)) continue;
            subips.add(i);
        }
        return subips;
    }

    @Override
    public boolean isAnnotationRegistered(Class<? extends Annotation> ac, Class<?> cc) {
        for (MetaInjectableProvider i : this.getList(ac)) {
            if (!i.cc.isAssignableFrom(cc)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isInjectableProviderRegistered(Class<? extends Annotation> ac, Class<?> cc, ComponentScope s) {
        return !this.findInjectableProviders(ac, cc, s).isEmpty();
    }

    @Override
    public final <A extends Annotation, C> Injectable getInjectable(Class<? extends Annotation> ac, ComponentContext ic, A a, C c, ComponentScope s) {
        for (MetaInjectableProvider mip : this.findInjectableProviders(ac, c.getClass(), s)) {
            Injectable i = mip.ip.getInjectable(ic, a, c);
            if (i == null) continue;
            return i;
        }
        return null;
    }

    @Override
    public final <A extends Annotation, C> Injectable getInjectable(Class<? extends Annotation> ac, ComponentContext ic, A a, C c, List<ComponentScope> ls) {
        for (ComponentScope s : ls) {
            Injectable i = this.getInjectable(ac, ic, a, c, s);
            if (i == null) continue;
            return i;
        }
        return null;
    }

    @Override
    public <A extends Annotation, C> InjectableProviderContext.InjectableScopePair getInjectableWithScope(Class<? extends Annotation> ac, ComponentContext ic, A a, C c, List<ComponentScope> ls) {
        for (ComponentScope s : ls) {
            Injectable i = this.getInjectable(ac, ic, a, c, s);
            if (i == null) continue;
            return new InjectableProviderContext.InjectableScopePair(i, s);
        }
        return null;
    }

    private static final class MetaInjectableProvider {
        final InjectableProvider ip;
        final Class<? extends Annotation> ac;
        final Class<?> cc;

        MetaInjectableProvider(InjectableProvider ip, Class<? extends Annotation> ac, Class<?> cc) {
            this.ip = ip;
            this.ac = ac;
            this.cc = cc;
        }
    }
}

