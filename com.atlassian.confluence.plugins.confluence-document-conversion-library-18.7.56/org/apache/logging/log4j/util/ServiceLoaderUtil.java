/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.util;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.stream.Stream;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;

public final class ServiceLoaderUtil {
    private ServiceLoaderUtil() {
    }

    public static <T> Stream<T> loadServices(Class<T> serviceType, MethodHandles.Lookup lookup) {
        return ServiceLoaderUtil.loadServices(serviceType, lookup, false);
    }

    public static <T> Stream<T> loadServices(Class<T> serviceType, MethodHandles.Lookup lookup, boolean useTccl) {
        return ServiceLoaderUtil.loadServices(serviceType, lookup, useTccl, true);
    }

    static <T> Stream<T> loadServices(Class<T> serviceType, MethodHandles.Lookup lookup, boolean useTccl, boolean verbose) {
        ClassLoader contextClassLoader;
        ClassLoader classLoader = lookup.lookupClass().getClassLoader();
        Stream<Object> services = ServiceLoaderUtil.loadClassloaderServices(serviceType, lookup, classLoader, verbose);
        if (useTccl && (contextClassLoader = LoaderUtil.getThreadContextClassLoader()) != classLoader) {
            services = Stream.concat(services, ServiceLoaderUtil.loadClassloaderServices(serviceType, lookup, contextClassLoader, verbose));
        }
        HashSet classes = new HashSet();
        return services.filter(service -> classes.add(service.getClass()));
    }

    static <T> Stream<T> loadClassloaderServices(Class<T> serviceType, MethodHandles.Lookup lookup, ClassLoader classLoader, boolean verbose) {
        try {
            ServiceLoader serviceLoader;
            MethodHandle loadHandle = lookup.findStatic(ServiceLoader.class, "load", MethodType.methodType(ServiceLoader.class, Class.class, ClassLoader.class));
            CallSite callSite = LambdaMetafactory.metafactory(lookup, "run", MethodType.methodType(PrivilegedAction.class, Class.class, ClassLoader.class), MethodType.methodType(Object.class), loadHandle, MethodType.methodType(ServiceLoader.class));
            PrivilegedAction action = callSite.getTarget().bindTo(serviceType).bindTo(classLoader).invoke();
            if (System.getSecurityManager() == null) {
                serviceLoader = (ServiceLoader)action.run();
            } else {
                MethodHandle privilegedHandle = lookup.findStatic(AccessController.class, "doPrivileged", MethodType.methodType(Object.class, PrivilegedAction.class));
                serviceLoader = privilegedHandle.invoke(action);
            }
            return serviceLoader.stream().map(provider -> {
                try {
                    return provider.get();
                }
                catch (ServiceConfigurationError e) {
                    if (verbose) {
                        StatusLogger.getLogger().warn("Unable to load service class for service {}", (Object)serviceType.getClass(), (Object)e);
                    }
                    return null;
                }
            }).filter(Objects::nonNull);
        }
        catch (Throwable e) {
            if (verbose) {
                StatusLogger.getLogger().error("Unable to load services for service {}", (Object)serviceType, (Object)e);
            }
            return Stream.empty();
        }
    }
}

