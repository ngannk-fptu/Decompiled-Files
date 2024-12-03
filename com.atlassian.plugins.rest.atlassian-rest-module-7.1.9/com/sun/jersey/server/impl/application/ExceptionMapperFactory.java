/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.application;

import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.spi.component.ProviderServices;
import com.sun.jersey.spi.container.ExceptionMapperContext;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ext.ExceptionMapper;

public class ExceptionMapperFactory
implements ExceptionMapperContext {
    private Set<ExceptionMapperType> emts = new HashSet<ExceptionMapperType>();

    public void init(ProviderServices providerServices) {
        for (ExceptionMapper em : providerServices.getProviders(ExceptionMapper.class)) {
            Class<? extends Throwable> c = this.getExceptionType(em.getClass());
            if (c == null) continue;
            this.emts.add(new ExceptionMapperType(em, c));
        }
    }

    @Override
    public ExceptionMapper find(Class<? extends Throwable> c) {
        int distance = Integer.MAX_VALUE;
        ExceptionMapper selectedEm = null;
        for (ExceptionMapperType emt : this.emts) {
            int d = this.distance(c, emt.c);
            if (d >= distance) continue;
            distance = d;
            selectedEm = emt.em;
            if (distance != 0) continue;
            break;
        }
        return selectedEm;
    }

    private int distance(Class<?> c, Class<?> emtc) {
        int distance = 0;
        if (!emtc.isAssignableFrom(c)) {
            return Integer.MAX_VALUE;
        }
        while (c != emtc) {
            c = c.getSuperclass();
            ++distance;
        }
        return distance;
    }

    private Class<? extends Throwable> getExceptionType(Class<? extends ExceptionMapper> c) {
        Class t = this.getType(c);
        if (Throwable.class.isAssignableFrom(t)) {
            return t;
        }
        return null;
    }

    private Class getType(Class<? extends ExceptionMapper> c) {
        for (Class<? extends ExceptionMapper> _c = c; _c != Object.class; _c = _c.getSuperclass()) {
            Type[] ts;
            for (Type t : ts = _c.getGenericInterfaces()) {
                ParameterizedType pt;
                if (!(t instanceof ParameterizedType) || (pt = (ParameterizedType)t).getRawType() != ExceptionMapper.class) continue;
                return this.getResolvedType(pt.getActualTypeArguments()[0], c, _c);
            }
        }
        return null;
    }

    private Class getResolvedType(Type t, Class c, Class dc) {
        if (t instanceof Class) {
            return (Class)t;
        }
        if (t instanceof TypeVariable) {
            ReflectionHelper.ClassTypePair ct = ReflectionHelper.resolveTypeVariable(c, dc, (TypeVariable)t);
            if (ct != null) {
                return ct.c;
            }
            return null;
        }
        if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)t;
            return (Class)pt.getRawType();
        }
        return null;
    }

    private static class ExceptionMapperType {
        ExceptionMapper em;
        Class<? extends Throwable> c;

        public ExceptionMapperType(ExceptionMapper em, Class<? extends Throwable> c) {
            this.em = em;
            this.c = c;
        }
    }
}

