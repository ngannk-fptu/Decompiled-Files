/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.registry.classloading.internal;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.hibernate.boot.registry.classloading.internal.AggregatedClassLoader;
import org.hibernate.boot.registry.classloading.internal.AggregatedServiceLoader;
import org.hibernate.boot.registry.classloading.internal.TcclLookupPrecedence;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

public class ClassLoaderServiceImpl
implements ClassLoaderService {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(ClassLoaderServiceImpl.class);
    private static final String CLASS_PATH_SCHEME = "classpath://";
    private final ConcurrentMap<Class, AggregatedServiceLoader<?>> serviceLoaders = new ConcurrentHashMap();
    private volatile AggregatedClassLoader aggregatedClassLoader;

    public ClassLoaderServiceImpl() {
        this(ClassLoaderServiceImpl.class.getClassLoader());
    }

    public ClassLoaderServiceImpl(ClassLoader classLoader) {
        this(Collections.singletonList(classLoader), TcclLookupPrecedence.AFTER);
    }

    public ClassLoaderServiceImpl(Collection<ClassLoader> providedClassLoaders, final TcclLookupPrecedence lookupPrecedence) {
        final LinkedHashSet<ClassLoader> orderedClassLoaderSet = new LinkedHashSet<ClassLoader>();
        if (providedClassLoaders != null) {
            for (ClassLoader classLoader : providedClassLoaders) {
                if (classLoader == null) continue;
                orderedClassLoaderSet.add(classLoader);
            }
        }
        orderedClassLoaderSet.add(ClassLoaderServiceImpl.class.getClassLoader());
        this.aggregatedClassLoader = AccessController.doPrivileged(new PrivilegedAction<AggregatedClassLoader>(){

            @Override
            public AggregatedClassLoader run() {
                return new AggregatedClassLoader(orderedClassLoaderSet, lookupPrecedence);
            }
        });
    }

    @Deprecated
    public static ClassLoaderServiceImpl fromConfigSettings(Map configValues) {
        ArrayList<ClassLoader> providedClassLoaders = new ArrayList<ClassLoader>();
        Collection classLoaders = (Collection)configValues.get("hibernate.classLoaders");
        if (classLoaders != null) {
            providedClassLoaders.addAll(classLoaders);
        }
        ClassLoaderServiceImpl.addIfSet(providedClassLoaders, "hibernate.classLoader.application", configValues);
        ClassLoaderServiceImpl.addIfSet(providedClassLoaders, "hibernate.classLoader.resources", configValues);
        ClassLoaderServiceImpl.addIfSet(providedClassLoaders, "hibernate.classLoader.hibernate", configValues);
        ClassLoaderServiceImpl.addIfSet(providedClassLoaders, "hibernate.classLoader.environment", configValues);
        return new ClassLoaderServiceImpl(providedClassLoaders, TcclLookupPrecedence.AFTER);
    }

    private static void addIfSet(List<ClassLoader> providedClassLoaders, String name, Map configVales) {
        ClassLoader providedClassLoader = (ClassLoader)configVales.get(name);
        if (providedClassLoader != null) {
            providedClassLoaders.add(providedClassLoader);
        }
    }

    @Override
    public <T> Class<T> classForName(String className) {
        try {
            return Class.forName(className, true, this.getAggregatedClassLoader());
        }
        catch (Exception e) {
            throw new ClassLoadingException("Unable to load class [" + className + "]", e);
        }
        catch (LinkageError e) {
            throw new ClassLoadingException("Unable to load class [" + className + "]", e);
        }
    }

    @Override
    public URL locateResource(String name) {
        try {
            return new URL(name);
        }
        catch (Exception exception) {
            URL url2;
            name = this.stripClasspathScheme(name);
            try {
                url2 = this.getAggregatedClassLoader().getResource(name);
                if (url2 != null) {
                    return url2;
                }
            }
            catch (Exception url2) {
                // empty catch block
            }
            if (name.startsWith("/")) {
                name = name.substring(1);
                try {
                    url2 = this.getAggregatedClassLoader().getResource(name);
                    if (url2 != null) {
                        return url2;
                    }
                }
                catch (Exception exception2) {
                    // empty catch block
                }
            }
            return null;
        }
    }

    @Override
    public InputStream locateResourceStream(String name) {
        try {
            log.tracef("trying via [new URL(\"%s\")]", name);
            return new URL(name).openStream();
        }
        catch (Exception exception) {
            String stripped;
            name = this.stripClasspathScheme(name);
            try {
                log.tracef("trying via [ClassLoader.getResourceAsStream(\"%s\")]", name);
                InputStream stream = this.getAggregatedClassLoader().getResourceAsStream(name);
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
                    log.tracef("trying via [new URL(\"%s\")]", stripped);
                    return new URL(stripped).openStream();
                }
                catch (Exception exception2) {
                    try {
                        log.tracef("trying via [ClassLoader.getResourceAsStream(\"%s\")]", stripped);
                        InputStream stream = this.getAggregatedClassLoader().getResourceAsStream(stripped);
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

    @Override
    public List<URL> locateResources(String name) {
        ArrayList<URL> urls = new ArrayList<URL>();
        try {
            Enumeration<URL> urlEnumeration = this.getAggregatedClassLoader().getResources(name);
            if (urlEnumeration != null && urlEnumeration.hasMoreElements()) {
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

    @Override
    public <S> Collection<S> loadJavaServices(Class<S> serviceContract) {
        AggregatedServiceLoader<S> serviceLoader = (AggregatedServiceLoader<S>)this.serviceLoaders.get(serviceContract);
        if (serviceLoader == null) {
            serviceLoader = AggregatedServiceLoader.create(this.getAggregatedClassLoader(), serviceContract);
            this.serviceLoaders.put(serviceContract, serviceLoader);
        }
        return serviceLoader.getAll();
    }

    @Override
    public <T> T generateProxy(InvocationHandler handler, Class ... interfaces) {
        return (T)Proxy.newProxyInstance(this.getAggregatedClassLoader(), interfaces, handler);
    }

    @Override
    public Package packageForNameOrNull(String packageName) {
        try {
            Class<?> aClass = Class.forName(packageName + ".package-info", true, this.getAggregatedClassLoader());
            return aClass == null ? null : aClass.getPackage();
        }
        catch (ClassNotFoundException e) {
            log.packageNotFound(packageName);
            return null;
        }
        catch (LinkageError e) {
            log.warn("LinkageError while attempting to load Package named " + packageName, e);
            return null;
        }
    }

    @Override
    public <T> T workWithClassLoader(ClassLoaderService.Work<T> work) {
        return work.doWork(this.getAggregatedClassLoader());
    }

    private AggregatedClassLoader getAggregatedClassLoader() {
        AggregatedClassLoader aggregated = this.aggregatedClassLoader;
        if (aggregated == null) {
            throw log.usingStoppedClassLoaderService();
        }
        return aggregated;
    }

    private String stripClasspathScheme(String name) {
        if (name == null) {
            return null;
        }
        if (name.startsWith(CLASS_PATH_SCHEME)) {
            return name.substring(CLASS_PATH_SCHEME.length());
        }
        return name;
    }

    @Override
    public void stop() {
        for (AggregatedServiceLoader serviceLoader : this.serviceLoaders.values()) {
            serviceLoader.close();
        }
        this.serviceLoaders.clear();
        this.aggregatedClassLoader = null;
    }
}

