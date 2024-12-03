/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.Preconditions;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public final class ServiceLoader {
    private static final boolean URLDEFINITION_COMPAT = Boolean.getBoolean("hazelcast.compat.classloading.urldefinition");
    private static final boolean USE_CLASSLOADING_FALLBACK = Boolean.getBoolean("hazelcast.compat.classloading.hooks.fallback");
    private static final ILogger LOGGER = Logger.getLogger(ServiceLoader.class);
    private static final String IGNORED_GLASSFISH_MAGIC_CLASSLOADER = "com.sun.enterprise.v3.server.APIClassLoaderServiceImpl$APIClassLoader";

    private ServiceLoader() {
    }

    public static <T> T load(Class<T> clazz, String factoryId, ClassLoader classLoader) throws Exception {
        Iterator<T> iterator = ServiceLoader.iterator(clazz, factoryId, classLoader);
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    public static <T> Iterator<T> iterator(Class<T> expectedType, String factoryId, ClassLoader classLoader) throws Exception {
        Iterator<Class<T>> classIterator = ServiceLoader.classIterator(expectedType, factoryId, classLoader);
        return new NewInstanceIterator<T>(classIterator);
    }

    public static <T> Iterator<Class<T>> classIterator(Class<T> expectedType, String factoryId, ClassLoader classLoader) throws Exception {
        Set<ServiceDefinition> serviceDefinitions = ServiceLoader.getServiceDefinitions(factoryId, classLoader);
        return new ClassIterator<T>(serviceDefinitions, expectedType);
    }

    private static Set<ServiceDefinition> getServiceDefinitions(String factoryId, ClassLoader classLoader) {
        List<ClassLoader> classLoaders = ServiceLoader.selectClassLoaders(classLoader);
        HashSet<URLDefinition> factoryUrls = new HashSet<URLDefinition>();
        for (ClassLoader selectedClassLoader : classLoaders) {
            factoryUrls.addAll(ServiceLoader.collectFactoryUrls(factoryId, selectedClassLoader));
        }
        HashSet<ServiceDefinition> serviceDefinitions = new HashSet<ServiceDefinition>();
        for (URLDefinition urlDefinition : factoryUrls) {
            serviceDefinitions.addAll(ServiceLoader.parse(urlDefinition));
        }
        if (serviceDefinitions.isEmpty()) {
            Logger.getLogger(ServiceLoader.class).finest("Service loader could not load 'META-INF/services/" + factoryId + "'. It may be empty or does not exist.");
        }
        return serviceDefinitions;
    }

    private static Set<URLDefinition> collectFactoryUrls(String factoryId, ClassLoader classLoader) {
        String resourceName = "META-INF/services/" + factoryId;
        try {
            Enumeration<URL> configs = classLoader.getResources(resourceName);
            HashSet<URLDefinition> urlDefinitions = new HashSet<URLDefinition>();
            while (configs.hasMoreElements()) {
                URL url = configs.nextElement();
                String externalForm = url.toExternalForm().replace(" ", "%20").replace("^", "%5e");
                URI uri = new URI(externalForm);
                if (classLoader.getClass().getName().equals(IGNORED_GLASSFISH_MAGIC_CLASSLOADER)) continue;
                urlDefinitions.add(new URLDefinition(uri, classLoader));
            }
            return urlDefinitions;
        }
        catch (Exception e) {
            LOGGER.severe(e);
            return Collections.emptySet();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Set<ServiceDefinition> parse(URLDefinition urlDefinition) {
        try {
            HashSet<ServiceDefinition> names = new HashSet<ServiceDefinition>();
            BufferedReader r = null;
            try {
                String line;
                URL url = urlDefinition.uri.toURL();
                r = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                while ((line = r.readLine()) != null) {
                    String name;
                    int comment = line.indexOf(35);
                    if (comment >= 0) {
                        line = line.substring(0, comment);
                    }
                    if ((name = line.trim()).length() == 0) continue;
                    names.add(new ServiceDefinition(name, urlDefinition.classLoader));
                }
            }
            catch (Throwable throwable) {
                IOUtil.closeResource(r);
                throw throwable;
            }
            IOUtil.closeResource(r);
            return names;
        }
        catch (Exception e) {
            LOGGER.severe(e);
            return Collections.emptySet();
        }
    }

    static List<ClassLoader> selectClassLoaders(ClassLoader classLoader) {
        ClassLoader coreClassLoader;
        ClassLoader tccl;
        ArrayList<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
        if (classLoader != null) {
            classLoaders.add(classLoader);
        }
        if ((tccl = Thread.currentThread().getContextClassLoader()) != null && tccl != classLoader) {
            classLoaders.add(tccl);
        }
        if ((coreClassLoader = ServiceLoader.class.getClassLoader()) != classLoader && coreClassLoader != tccl) {
            classLoaders.add(coreClassLoader);
        }
        try {
            Class<?> hzClientClass = Class.forName("com.hazelcast.client.HazelcastClient");
            ClassLoader clientClassLoader = hzClientClass.getClassLoader();
            if (clientClassLoader != classLoader && clientClassLoader != tccl && clientClassLoader != coreClassLoader) {
                classLoaders.add(clientClassLoader);
            }
        }
        catch (ClassNotFoundException ignore) {
            EmptyStatement.ignore(ignore);
        }
        return classLoaders;
    }

    static class ClassIterator<T>
    implements Iterator<Class<T>> {
        private final Iterator<ServiceDefinition> iterator;
        private final Class<T> expectedType;
        private Class<T> nextClass;
        private Set<Class<?>> alreadyProvidedClasses = new HashSet();

        ClassIterator(Set<ServiceDefinition> serviceDefinitions, Class<T> expectedType) {
            this.iterator = serviceDefinitions.iterator();
            this.expectedType = expectedType;
        }

        @Override
        public boolean hasNext() {
            if (this.nextClass != null) {
                return true;
            }
            return this.advance();
        }

        private boolean advance() {
            while (this.iterator.hasNext()) {
                ServiceDefinition definition = this.iterator.next();
                String className = definition.className;
                ClassLoader classLoader = definition.classLoader;
                try {
                    Class<?> candidate = this.loadClass(className, classLoader);
                    if (this.expectedType.isAssignableFrom(candidate)) {
                        if (this.isDuplicate(candidate)) continue;
                        this.nextClass = candidate;
                        return true;
                    }
                    this.onNonAssignableClass(className, candidate);
                }
                catch (ClassNotFoundException e) {
                    this.onClassNotFoundException(className, classLoader, e);
                }
            }
            return false;
        }

        private boolean isDuplicate(Class<?> candidate) {
            return !this.alreadyProvidedClasses.add(candidate);
        }

        private Class<?> loadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
            Class<?> candidate = USE_CLASSLOADING_FALLBACK ? ClassLoaderUtil.loadClass(classLoader, className) : classLoader.loadClass(className);
            return candidate;
        }

        private void onClassNotFoundException(String className, ClassLoader classLoader, ClassNotFoundException e) {
            if (!className.startsWith("com.hazelcast")) {
                throw new HazelcastException(e);
            }
            LOGGER.fine("Failed to load " + className + " by " + classLoader + ". This indicates a classloading issue. It can happen in a runtime with a complicated classloading model. (OSGi, Java EE, etc);");
        }

        private void onNonAssignableClass(String className, Class candidate) {
            if (this.expectedType.isInterface()) {
                if (ClassLoaderUtil.implementsInterfaceWithSameName(candidate, this.expectedType)) {
                    LOGGER.fine("There appears to be a classloading conflict. Class " + className + " loaded by " + candidate.getClassLoader() + " implements " + this.expectedType.getName() + " from its own class loader, but it does not implement " + this.expectedType.getName() + " loaded by " + this.expectedType.getClassLoader());
                } else {
                    LOGGER.fine("There appears to be a classloading conflict. Class " + className + " loaded by " + candidate.getClassLoader() + " does not implement an interface with name " + this.expectedType.getName() + " in both class loaders.the interface currently loaded by " + this.expectedType.getClassLoader());
                }
            }
        }

        @Override
        public Class<T> next() {
            if (this.nextClass == null) {
                this.advance();
            }
            if (this.nextClass == null) {
                throw new NoSuchElementException();
            }
            Class<T> classToReturn = this.nextClass;
            this.nextClass = null;
            return classToReturn;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    static class NewInstanceIterator<T>
    implements Iterator<T> {
        private final Iterator<Class<T>> classIterator;

        NewInstanceIterator(Iterator<Class<T>> classIterator) {
            this.classIterator = classIterator;
        }

        @Override
        public boolean hasNext() {
            return this.classIterator.hasNext();
        }

        @Override
        public T next() {
            Class<T> clazz = this.classIterator.next();
            try {
                Constructor<T> constructor = clazz.getDeclaredConstructor(new Class[0]);
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }
                return constructor.newInstance(new Object[0]);
            }
            catch (InstantiationException e) {
                throw new HazelcastException(e);
            }
            catch (IllegalAccessException e) {
                throw new HazelcastException(e);
            }
            catch (NoSuchMethodException e) {
                throw new HazelcastException(e);
            }
            catch (InvocationTargetException e) {
                throw new HazelcastException(e);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class URLDefinition {
        private final URI uri;
        private final ClassLoader classLoader;

        private URLDefinition(URI url, ClassLoader classLoader) {
            this.uri = url;
            this.classLoader = classLoader;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            URLDefinition that = (URLDefinition)o;
            if (this.uri != null ? !this.uri.equals(that.uri) : that.uri != null) {
                return false;
            }
            if (URLDEFINITION_COMPAT) {
                return true;
            }
            return this.classLoader != null ? this.classLoader.equals(that.classLoader) : that.classLoader == null;
        }

        public int hashCode() {
            return this.uri == null ? 0 : this.uri.hashCode();
        }
    }

    static final class ServiceDefinition {
        private final String className;
        private final ClassLoader classLoader;

        public ServiceDefinition(String className, ClassLoader classLoader) {
            this.className = Preconditions.isNotNull(className, "className");
            this.classLoader = Preconditions.isNotNull(classLoader, "classLoader");
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ServiceDefinition that = (ServiceDefinition)o;
            if (!this.classLoader.equals(that.classLoader)) {
                return false;
            }
            return this.className.equals(that.className);
        }

        public int hashCode() {
            int result = this.className.hashCode();
            result = 31 * result + this.classLoader.hashCode();
            return result;
        }
    }
}

