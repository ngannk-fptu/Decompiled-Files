/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.api.client;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.WSService;
import com.sun.xml.ws.api.client.ServiceInterceptor;
import com.sun.xml.ws.util.ServiceFinder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class ServiceInterceptorFactory {
    private static ThreadLocal<Set<ServiceInterceptorFactory>> threadLocalFactories = new ThreadLocal<Set<ServiceInterceptorFactory>>(){

        @Override
        protected Set<ServiceInterceptorFactory> initialValue() {
            return new HashSet<ServiceInterceptorFactory>();
        }
    };

    public abstract ServiceInterceptor create(@NotNull WSService var1);

    @NotNull
    public static ServiceInterceptor load(@NotNull WSService service, @Nullable ClassLoader cl) {
        ArrayList<ServiceInterceptor> l = new ArrayList<ServiceInterceptor>();
        for (ServiceInterceptorFactory f : ServiceFinder.find(ServiceInterceptorFactory.class)) {
            l.add(f.create(service));
        }
        for (ServiceInterceptorFactory f : threadLocalFactories.get()) {
            l.add(f.create(service));
        }
        return ServiceInterceptor.aggregate(l.toArray(new ServiceInterceptor[l.size()]));
    }

    public static boolean registerForThread(ServiceInterceptorFactory factory) {
        return threadLocalFactories.get().add(factory);
    }

    public static boolean unregisterForThread(ServiceInterceptorFactory factory) {
        return threadLocalFactories.get().remove(factory);
    }
}

