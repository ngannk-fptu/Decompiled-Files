/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.Local
 *  javax.ejb.Remote
 *  javax.ejb.Stateful
 *  javax.ejb.Stateless
 */
package com.sun.jersey.server.impl.ejb;

import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.JavaMethodInvoker;
import com.sun.jersey.spi.container.ResourceMethodCustomInvokerDispatchFactory;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ws.rs.core.Context;

public class EJBRequestDispatcherProvider
implements ResourceMethodDispatchProvider {
    @Context
    ResourceMethodCustomInvokerDispatchFactory rdFactory;

    @Override
    public RequestDispatcher create(AbstractResourceMethod abstractResourceMethod) {
        AbstractResource declaringResource = abstractResourceMethod.getDeclaringResource();
        if (this.isSessionBean(declaringResource)) {
            Class<?> resourceClass = declaringResource.getResourceClass();
            Method javaMethod = abstractResourceMethod.getMethod();
            for (Class iFace : this.remoteAndLocalIfaces(resourceClass)) {
                try {
                    Method iFaceMethod = iFace.getDeclaredMethod(javaMethod.getName(), javaMethod.getParameterTypes());
                    if (iFaceMethod == null) continue;
                    return this.createDispatcher(abstractResourceMethod, iFaceMethod);
                }
                catch (NoSuchMethodException iFaceMethod) {
                }
                catch (SecurityException ex) {
                    Logger.getLogger(EJBRequestDispatcherProvider.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    private List<Class> remoteAndLocalIfaces(Class<?> resourceClass) {
        LinkedList<Class> allLocalOrRemoteIfaces = new LinkedList<Class>();
        if (resourceClass.isAnnotationPresent(Remote.class)) {
            allLocalOrRemoteIfaces.addAll(Arrays.asList(resourceClass.getAnnotation(Remote.class).value()));
        }
        if (resourceClass.isAnnotationPresent(Local.class)) {
            allLocalOrRemoteIfaces.addAll(Arrays.asList(resourceClass.getAnnotation(Local.class).value()));
        }
        for (Class<?> i : resourceClass.getInterfaces()) {
            if (!i.isAnnotationPresent(Remote.class) && !i.isAnnotationPresent(Local.class)) continue;
            allLocalOrRemoteIfaces.add(i);
        }
        return allLocalOrRemoteIfaces;
    }

    private RequestDispatcher createDispatcher(AbstractResourceMethod abstractResourceMethod, final Method iFaceMethod) {
        return this.rdFactory.getDispatcher(abstractResourceMethod, new JavaMethodInvoker(){

            @Override
            public Object invoke(Method m, Object o, Object ... parameters) throws InvocationTargetException, IllegalAccessException {
                return iFaceMethod.invoke(o, parameters);
            }
        });
    }

    private boolean isSessionBean(AbstractResource ar) {
        return ar.isAnnotationPresent(Stateless.class) || ar.isAnnotationPresent(Stateful.class);
    }
}

