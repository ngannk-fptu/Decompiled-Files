/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.component;

import com.sun.jersey.core.reflection.AnnotatedMethod;
import com.sun.jersey.core.reflection.MethodList;
import com.sun.jersey.core.spi.component.AnnotatedContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;

public class ComponentInjector<T> {
    protected final InjectableProviderContext ipc;
    protected final Class<T> c;

    public ComponentInjector(InjectableProviderContext ipc, Class<T> c) {
        this.ipc = ipc;
        this.c = c;
    }

    public void inject(T t) {
        Annotation[] as;
        AnnotatedContext aoc = new AnnotatedContext();
        for (Class<T> oClass = this.c; oClass != Object.class; oClass = oClass.getSuperclass()) {
            for (Field f : oClass.getDeclaredFields()) {
                aoc.setAccessibleObject(f);
                as = f.getAnnotations();
                aoc.setAnnotations(as);
                boolean missingDependency = false;
                for (Annotation a : as) {
                    Injectable i = this.ipc.getInjectable(a.annotationType(), (ComponentContext)aoc, a, f.getGenericType(), ComponentScope.UNDEFINED_SINGLETON);
                    if (i != null) {
                        missingDependency = false;
                        this.setFieldValue(t, f, i.getValue());
                        break;
                    }
                    if (!this.ipc.isAnnotationRegistered(a.annotationType(), f.getGenericType().getClass())) continue;
                    missingDependency = true;
                }
                if (!missingDependency) continue;
                Errors.missingDependency(f);
            }
        }
        MethodList ml = new MethodList(this.c.getMethods());
        int methodIndex = 0;
        for (AnnotatedMethod m : ml.hasNotMetaAnnotation(HttpMethod.class).hasNotAnnotation(Path.class).hasNumParams(1).hasReturnType(Void.TYPE).nameStartsWith("set")) {
            as = m.getAnnotations();
            aoc.setAccessibleObject(m.getMethod());
            aoc.setAnnotations(as);
            Type gpt = m.getGenericParameterTypes()[0];
            boolean missingDependency = false;
            for (Annotation a : as) {
                Injectable i = this.ipc.getInjectable(a.annotationType(), (ComponentContext)aoc, a, gpt, ComponentScope.UNDEFINED_SINGLETON);
                if (i != null) {
                    missingDependency = false;
                    this.setMethodValue(t, m, i.getValue());
                    break;
                }
                if (!this.ipc.isAnnotationRegistered(a.annotationType(), gpt.getClass())) continue;
                missingDependency = true;
            }
            if (missingDependency) {
                Errors.missingDependency(m.getMethod(), methodIndex);
            }
            ++methodIndex;
        }
    }

    private void setFieldValue(final Object resource, final Field f, final Object value) {
        AccessController.doPrivileged(new PrivilegedAction<Object>(){

            @Override
            public Object run() {
                try {
                    if (!f.isAccessible()) {
                        f.setAccessible(true);
                    }
                    f.set(resource, value);
                    return null;
                }
                catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void setMethodValue(Object o, AnnotatedMethod m, Object value) {
        try {
            m.getMethod().invoke(o, value);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

