/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.Provider
 *  javax.xml.ws.WebServiceContext
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.InstanceResolverAnnotation;
import com.sun.xml.ws.api.server.Invoker;
import com.sun.xml.ws.api.server.MethodUtil;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WSWebServiceContext;
import com.sun.xml.ws.resources.ServerMessages;
import com.sun.xml.ws.resources.WsservletMessages;
import com.sun.xml.ws.server.ServerRtException;
import com.sun.xml.ws.server.SingletonResolver;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;

public abstract class InstanceResolver<T> {
    private static final Logger logger = Logger.getLogger("com.sun.xml.ws.server");

    @NotNull
    public abstract T resolve(@NotNull Packet var1);

    public void postInvoke(@NotNull Packet request, @NotNull T servant) {
    }

    public void start(@NotNull WSWebServiceContext wsc, @NotNull WSEndpoint endpoint) {
        this.start(wsc);
    }

    public void start(@NotNull WebServiceContext wsc) {
    }

    public void dispose() {
    }

    public static <T> InstanceResolver<T> createSingleton(T singleton) {
        assert (singleton != null);
        InstanceResolver<?> ir = InstanceResolver.createFromInstanceResolverAnnotation(singleton.getClass());
        if (ir == null) {
            ir = new SingletonResolver(singleton);
        }
        return ir;
    }

    public static <T> InstanceResolver<T> createDefault(@NotNull Class<T> clazz, boolean bool) {
        return InstanceResolver.createDefault(clazz);
    }

    public static <T> InstanceResolver<T> createDefault(@NotNull Class<T> clazz) {
        InstanceResolver<T> ir = InstanceResolver.createFromInstanceResolverAnnotation(clazz);
        if (ir == null) {
            ir = new SingletonResolver<T>(InstanceResolver.createNewInstance(clazz));
        }
        return ir;
    }

    public static <T> InstanceResolver<T> createFromInstanceResolverAnnotation(@NotNull Class<T> clazz) {
        for (Annotation a : clazz.getAnnotations()) {
            InstanceResolverAnnotation ira = a.annotationType().getAnnotation(InstanceResolverAnnotation.class);
            if (ira == null) continue;
            Class<? extends InstanceResolver> ir = ira.value();
            try {
                return ir.getConstructor(Class.class).newInstance(clazz);
            }
            catch (InstantiationException e) {
                throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(ir.getName(), a.annotationType(), clazz.getName()));
            }
            catch (IllegalAccessException e) {
                throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(ir.getName(), a.annotationType(), clazz.getName()));
            }
            catch (InvocationTargetException e) {
                throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(ir.getName(), a.annotationType(), clazz.getName()));
            }
            catch (NoSuchMethodException e) {
                throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(ir.getName(), a.annotationType(), clazz.getName()));
            }
        }
        return null;
    }

    protected static <T> T createNewInstance(Class<T> cl) {
        try {
            return cl.newInstance();
        }
        catch (InstantiationException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ServerRtException(WsservletMessages.ERROR_IMPLEMENTOR_FACTORY_NEW_INSTANCE_FAILED(cl), new Object[0]);
        }
        catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ServerRtException(WsservletMessages.ERROR_IMPLEMENTOR_FACTORY_NEW_INSTANCE_FAILED(cl), new Object[0]);
        }
    }

    @NotNull
    public Invoker createInvoker() {
        return new Invoker(){

            @Override
            public void start(@NotNull WSWebServiceContext wsc, @NotNull WSEndpoint endpoint) {
                InstanceResolver.this.start(wsc, endpoint);
            }

            @Override
            public void dispose() {
                InstanceResolver.this.dispose();
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public Object invoke(Packet p, Method m, Object ... args) throws InvocationTargetException, IllegalAccessException {
                Object t = InstanceResolver.this.resolve(p);
                try {
                    Object object = MethodUtil.invoke(t, m, args);
                    return object;
                }
                finally {
                    InstanceResolver.this.postInvoke(p, t);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public <U> U invokeProvider(@NotNull Packet p, U arg) {
                Object t = InstanceResolver.this.resolve(p);
                try {
                    Object object = ((Provider)t).invoke(arg);
                    return (U)object;
                }
                finally {
                    InstanceResolver.this.postInvoke(p, t);
                }
            }

            public String toString() {
                return "Default Invoker over " + InstanceResolver.this.toString();
            }
        };
    }
}

