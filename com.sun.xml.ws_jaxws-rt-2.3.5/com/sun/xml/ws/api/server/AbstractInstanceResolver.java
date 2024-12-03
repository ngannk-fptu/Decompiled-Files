/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.server.InstanceResolver;
import com.sun.xml.ws.api.server.MethodUtil;
import com.sun.xml.ws.api.server.ResourceInjector;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.resources.ServerMessages;
import com.sun.xml.ws.server.ServerRtException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

public abstract class AbstractInstanceResolver<T>
extends InstanceResolver<T> {
    protected static ResourceInjector getResourceInjector(WSEndpoint endpoint) {
        ResourceInjector ri = endpoint.getContainer().getSPI(ResourceInjector.class);
        if (ri == null) {
            ri = ResourceInjector.STANDALONE;
        }
        return ri;
    }

    protected static void invokeMethod(final @Nullable Method method, final Object instance, final Object ... args) {
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
                    MethodUtil.invoke(instance, method, args);
                }
                catch (IllegalAccessException e) {
                    throw new ServerRtException("server.rt.err", e);
                }
                catch (InvocationTargetException e) {
                    throw new ServerRtException("server.rt.err", e);
                }
                return null;
            }
        });
    }

    @Nullable
    protected final Method findAnnotatedMethod(Class clazz, Class<? extends Annotation> annType) {
        boolean once = false;
        Method r = null;
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getAnnotation(annType) == null) continue;
            if (once) {
                throw new ServerRtException(ServerMessages.ANNOTATION_ONLY_ONCE(annType), new Object[0]);
            }
            if (method.getParameterTypes().length != 0) {
                throw new ServerRtException(ServerMessages.NOT_ZERO_PARAMETERS(method), new Object[0]);
            }
            r = method;
            once = true;
        }
        return r;
    }
}

