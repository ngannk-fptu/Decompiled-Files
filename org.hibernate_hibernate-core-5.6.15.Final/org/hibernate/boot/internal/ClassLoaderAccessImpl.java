/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.internal;

import java.net.URL;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.service.ServiceRegistry;
import org.jboss.logging.Logger;

public class ClassLoaderAccessImpl
implements ClassLoaderAccess {
    private static final Logger log = Logger.getLogger(ClassLoaderAccessImpl.class);
    private final ClassLoaderService classLoaderService;
    private ClassLoader jpaTempClassLoader;

    public ClassLoaderAccessImpl(ClassLoader jpaTempClassLoader, ClassLoaderService classLoaderService) {
        this.jpaTempClassLoader = jpaTempClassLoader;
        this.classLoaderService = classLoaderService;
    }

    public ClassLoaderAccessImpl(ClassLoader tempClassLoader, ServiceRegistry serviceRegistry) {
        this(tempClassLoader, serviceRegistry.getService(ClassLoaderService.class));
    }

    public ClassLoaderAccessImpl(ClassLoaderService classLoaderService) {
        this(null, classLoaderService);
    }

    public void injectTempClassLoader(ClassLoader jpaTempClassLoader) {
        log.debugf("ClassLoaderAccessImpl#injectTempClassLoader(%s) [was %s]", (Object)jpaTempClassLoader, (Object)this.jpaTempClassLoader);
        this.jpaTempClassLoader = jpaTempClassLoader;
    }

    public Class<?> classForName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name of class to load cannot be null");
        }
        if (this.isSafeClass(name)) {
            return this.classLoaderService.classForName(name);
        }
        log.debugf("Not known whether passed class name [%s] is safe", (Object)name);
        if (this.jpaTempClassLoader == null) {
            log.debugf("No temp ClassLoader provided; using live ClassLoader for loading potentially unsafe class : %s", (Object)name);
            return this.classLoaderService.classForName(name);
        }
        log.debugf("Temp ClassLoader was provided, so we will use that : %s", (Object)name);
        try {
            return this.jpaTempClassLoader.loadClass(name);
        }
        catch (ClassNotFoundException e) {
            throw new ClassLoadingException(name);
        }
    }

    private boolean isSafeClass(String name) {
        return name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("jakarta.") || name.startsWith("org.hibernate.");
    }

    public ClassLoader getJpaTempClassLoader() {
        return this.jpaTempClassLoader;
    }

    @Override
    public URL locateResource(String resourceName) {
        return this.classLoaderService.locateResource(resourceName);
    }

    public void release() {
    }
}

