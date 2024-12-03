/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.container;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.ClasspathResourceConfig;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.spi.container.ContainerListener;
import com.sun.jersey.spi.container.ContainerNotifier;
import com.sun.jersey.spi.container.ContainerProvider;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.WebApplicationFactory;
import com.sun.jersey.spi.service.ServiceFinder;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class ContainerFactory {
    private ContainerFactory() {
    }

    public static <A> A createContainer(Class<A> type, Class<?> ... resourceClasses) throws ContainerException, IllegalArgumentException {
        HashSet resourceClassesSet = new HashSet(Arrays.asList(resourceClasses));
        return ContainerFactory.createContainer(type, new DefaultResourceConfig(resourceClassesSet), null);
    }

    public static <A> A createContainer(Class<A> type, Set<Class<?>> resourceClasses) throws ContainerException, IllegalArgumentException {
        return ContainerFactory.createContainer(type, new DefaultResourceConfig(resourceClasses), null);
    }

    public static <A> A createContainer(Class<A> type, ResourceConfig resourceConfig) throws ContainerException, IllegalArgumentException {
        return ContainerFactory.createContainer(type, resourceConfig, null);
    }

    public static <A> A createContainer(Class<A> type, ResourceConfig resourceConfig, IoCComponentProviderFactory factory) throws ContainerException, IllegalArgumentException {
        WebApplication wa = WebApplicationFactory.createWebApplication();
        LinkedList<ContainerProvider> cps = new LinkedList<ContainerProvider>();
        for (ContainerProvider containerProvider : ServiceFinder.find(ContainerProvider.class, true)) {
            cps.addFirst(containerProvider);
        }
        for (ContainerProvider<Object> containerProvider : cps) {
            Object o;
            Object c = containerProvider.createContainer(type, resourceConfig, wa);
            if (c == null) continue;
            if (!wa.isInitiated()) {
                wa.initiate(resourceConfig, factory);
            }
            if ((o = resourceConfig.getProperties().get("com.sun.jersey.spi.container.ContainerNotifier")) instanceof List) {
                List list = (List)o;
                for (Object elem : list) {
                    if (!(elem instanceof ContainerNotifier) || !(c instanceof ContainerListener)) continue;
                    ContainerNotifier crf = (ContainerNotifier)elem;
                    crf.addListener((ContainerListener)c);
                }
            } else if (o instanceof ContainerNotifier && c instanceof ContainerListener) {
                ContainerNotifier crf = (ContainerNotifier)o;
                crf.addListener((ContainerListener)c);
            }
            return (A)c;
        }
        throw new IllegalArgumentException("No container provider supports the type " + type);
    }

    @Deprecated
    public static <A> A createContainer(Class<A> type, String packageName) throws ContainerException, IllegalArgumentException {
        String resourcesClassName = packageName + ".WebResources";
        try {
            Class<?> resourcesClass = ContainerFactory.class.getClassLoader().loadClass(resourcesClassName);
            ResourceConfig config = (ResourceConfig)resourcesClass.newInstance();
            return ContainerFactory.createContainer(type, config, null);
        }
        catch (ClassNotFoundException e) {
            throw new ContainerException(e);
        }
        catch (InstantiationException e) {
            throw new ContainerException(e);
        }
        catch (IllegalAccessException e) {
            throw new ContainerException(e);
        }
    }

    public static <A> A createContainer(Class<A> type) {
        String classPath = System.getProperty("java.class.path");
        String[] paths = classPath.split(File.pathSeparator);
        return ContainerFactory.createContainer(type, paths);
    }

    public static <A> A createContainer(Class<A> type, String ... paths) {
        ClasspathResourceConfig config = new ClasspathResourceConfig(paths);
        return ContainerFactory.createContainer(type, config, null);
    }
}

