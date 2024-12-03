/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.hibernate.boot.registry.classloading.spi.ClassLoaderService
 *  org.hibernate.boot.registry.classloading.spi.ClassLoaderService$Work
 *  org.hibernate.boot.registry.classloading.spi.ClassLoadingException
 */
package com.atlassian.migration.agent.store.jpa.impl;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;

@ParametersAreNonnullByDefault
public class DefaultClassLoaderService
implements ClassLoaderService {
    private final transient ClassLoader classLoader = DefaultClassLoaderService.class.getClassLoader();

    DefaultClassLoaderService() {
    }

    @Nonnull
    public <T> Class<T> classForName(String className) {
        Objects.requireNonNull(className);
        try {
            return this.classLoader.loadClass(className);
        }
        catch (Exception | LinkageError e) {
            throw new ClassLoadingException("Unable to load class [" + className + "]", e);
        }
    }

    @Nullable
    public URL locateResource(String name) {
        Objects.requireNonNull(name);
        try {
            return new URL(name);
        }
        catch (Exception exception) {
            try {
                URL url = this.classLoader.getResource(name);
                if (url != null) {
                    return url;
                }
            }
            catch (Exception url) {
                // empty catch block
            }
            if (name.startsWith("/")) {
                String trimmedName = name.substring(1);
                try {
                    URL url = this.classLoader.getResource(trimmedName);
                    if (url != null) {
                        return url;
                    }
                }
                catch (Exception exception2) {
                    // empty catch block
                }
            }
            return null;
        }
    }

    @Nullable
    public InputStream locateResourceStream(String name) {
        Objects.requireNonNull(name);
        try {
            return new URL(name).openStream();
        }
        catch (Exception exception) {
            String stripped;
            try {
                InputStream stream = this.classLoader.getResourceAsStream(name);
                if (stream != null) {
                    return stream;
                }
            }
            catch (Exception stream) {
                // empty catch block
            }
            String string = stripped = name.startsWith("/") ? name.substring(1) : null;
            if (stripped != null) {
                try {
                    return new URL(stripped).openStream();
                }
                catch (Exception exception2) {
                    try {
                        InputStream stream = this.classLoader.getResourceAsStream(stripped);
                        if (stream != null) {
                            return stream;
                        }
                    }
                    catch (Exception exception3) {
                        // empty catch block
                    }
                }
            }
            return null;
        }
    }

    @Nonnull
    public List<URL> locateResources(String name) {
        Objects.requireNonNull(name);
        ArrayList<URL> urls = new ArrayList<URL>();
        try {
            Enumeration<URL> urlEnumeration = this.classLoader.getResources(name);
            if (urlEnumeration != null) {
                while (urlEnumeration.hasMoreElements()) {
                    urls.add(urlEnumeration.nextElement());
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return urls;
    }

    @Nonnull
    public <S> Collection<S> loadJavaServices(Class<S> serviceContract) {
        Objects.requireNonNull(serviceContract);
        return Collections.emptyList();
    }

    @Nonnull
    public <T> T generateProxy(InvocationHandler handler, Class ... interfaces) {
        Objects.requireNonNull(handler);
        return (T)Proxy.newProxyInstance(this.classLoader, interfaces, handler);
    }

    public Package packageForNameOrNull(String packageName) {
        try {
            Class<?> aClass = Class.forName(packageName + ".package-info", false, this.classLoader);
            return aClass.getPackage();
        }
        catch (ClassNotFoundException | LinkageError e) {
            return null;
        }
    }

    public <T> T workWithClassLoader(ClassLoaderService.Work<T> work) {
        Objects.requireNonNull(work);
        return (T)work.doWork(this.classLoader);
    }

    public void stop() {
    }
}

