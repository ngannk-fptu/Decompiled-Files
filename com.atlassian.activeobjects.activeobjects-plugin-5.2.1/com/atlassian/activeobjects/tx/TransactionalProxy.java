/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.tx;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

public final class TransactionalProxy
implements InvocationHandler {
    private static final Class<? extends Annotation> ANNOTATION_CLASS = Transactional.class;
    private final ActiveObjects ao;
    private final Object obj;

    public TransactionalProxy(ActiveObjects ao, Object obj) {
        this.ao = ao;
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (TransactionalProxy.isAnnotated(method)) {
            return this.invokeInTransaction(method, args);
        }
        return this.invoke(method, args);
    }

    private Object invokeInTransaction(Method method, Object[] args) throws Throwable {
        try {
            return this.executeInTransaction(method, args);
        }
        catch (TransactionalException e) {
            throw e.getThrowable();
        }
    }

    private Object executeInTransaction(Method method, Object[] args) {
        return this.ao.executeInTransaction(() -> {
            try {
                return this.invoke(method, args);
            }
            catch (IllegalAccessException e) {
                throw new TransactionalException(e);
            }
            catch (InvocationTargetException e) {
                throw new TransactionalException(e);
            }
        });
    }

    private Object invoke(Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
        return method.invoke(this.obj, args);
    }

    public static Object transactional(ActiveObjects ao, Object o) {
        Objects.requireNonNull(o);
        Class<?> c = o.getClass();
        return Proxy.newProxyInstance(c.getClassLoader(), c.getInterfaces(), (InvocationHandler)new TransactionalProxy(ao, o));
    }

    static boolean isAnnotated(Method method) {
        return method != null && (TransactionalProxy.isAnnotationPresent(method) || TransactionalProxy.isAnnotationPresent(method.getDeclaringClass()));
    }

    public static boolean isAnnotated(Class<?> c) {
        if (c != null) {
            if (c.isInterface()) {
                if (TransactionalProxy.isAnnotationPresent(c)) {
                    return true;
                }
                for (GenericDeclaration genericDeclaration : c.getMethods()) {
                    if (!TransactionalProxy.isAnnotated((Method)genericDeclaration)) continue;
                    return true;
                }
            }
            for (GenericDeclaration genericDeclaration : c.getInterfaces()) {
                if (!TransactionalProxy.isAnnotated(genericDeclaration)) continue;
                return true;
            }
        }
        return false;
    }

    private static boolean isAnnotationPresent(AnnotatedElement e) {
        return e.isAnnotationPresent(ANNOTATION_CLASS);
    }

    private static final class TransactionalException
    extends RuntimeException {
        public TransactionalException(Throwable cause) {
            super(cause);
        }

        public Throwable getThrowable() {
            Throwable cause = this.getCause();
            if (cause instanceof InvocationTargetException) {
                return cause.getCause();
            }
            return cause;
        }
    }
}

