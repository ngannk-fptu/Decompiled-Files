/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.PostConstruct
 *  javax.xml.ws.WebServiceContext
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.spi.Invoker
 */
package com.sun.xml.ws.transport.httpspi.servlet;

import com.sun.xml.ws.util.InjectionPlan;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.annotation.PostConstruct;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.spi.Invoker;

class InvokerImpl
extends Invoker {
    private final Class implType;
    private final Object impl;
    private final Method postConstructMethod;

    InvokerImpl(Class implType) {
        this.implType = implType;
        this.postConstructMethod = InvokerImpl.findAnnotatedMethod(implType, PostConstruct.class);
        try {
            this.impl = implType.newInstance();
        }
        catch (InstantiationException e) {
            throw new WebServiceException((Throwable)e);
        }
        catch (IllegalAccessException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    private static void invokeMethod(final Method method, final Object instance, final Object ... args) {
        if (method == null) {
            return;
        }
        AccessController.doPrivileged(new PrivilegedAction<Void>(){

            @Override
            public Void run() {
                try {
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    method.invoke(instance, args);
                }
                catch (IllegalAccessException e) {
                    throw new WebServiceException((Throwable)e);
                }
                catch (InvocationTargetException e) {
                    throw new WebServiceException((Throwable)e);
                }
                return null;
            }
        });
    }

    public void inject(WebServiceContext webServiceContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        InjectionPlan.buildInjectionPlan(this.implType, WebServiceContext.class, false).inject(this.impl, webServiceContext);
        InvokerImpl.invokeMethod(this.postConstructMethod, this.impl, new Object[0]);
    }

    public Object invoke(Method m, Object ... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return m.invoke(this.impl, args);
    }

    private static Method findAnnotatedMethod(Class clazz, Class<? extends Annotation> annType) {
        boolean once = false;
        Method r = null;
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getAnnotation(annType) == null) continue;
            if (once) {
                throw new WebServiceException("Only one method should have the annotation" + annType);
            }
            if (method.getParameterTypes().length != 0) {
                throw new WebServiceException("Method" + method + "shouldn't have any arguments");
            }
            r = method;
            once = true;
        }
        return r;
    }
}

