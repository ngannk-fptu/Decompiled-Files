/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChainingClassLoader
extends ClassLoader {
    private static final Logger log = LoggerFactory.getLogger(ChainingClassLoader.class);
    private final List<ClassLoader> classLoaders;
    private final Map<String, String> resourceRedirects;

    public ChainingClassLoader(ClassLoader ... classLoaders) {
        this(Collections.emptyMap(), classLoaders);
    }

    public ChainingClassLoader(Map<String, String> resourceRedirects, ClassLoader ... classLoaders) {
        super(null);
        this.resourceRedirects = (Map)Preconditions.checkNotNull(resourceRedirects);
        this.classLoaders = ImmutableList.copyOf((Object[])classLoaders);
        Preconditions.checkState((boolean)this.classLoaders.stream().allMatch(Objects::nonNull), (Object)"ClassLoader arguments cannot be null");
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        for (ClassLoader classloader : this.classLoaders) {
            try {
                return classloader.loadClass(name);
            }
            catch (ClassNotFoundException classNotFoundException) {
            }
        }
        throw new ClassNotFoundException(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return new ResourcesEnumeration(this.getAlternativeResourceName(name), this.classLoaders);
    }

    @Override
    public URL getResource(String name) {
        String realResourceName = this.getAlternativeResourceName(name);
        for (ClassLoader classloader : this.classLoaders) {
            URL url = classloader.getResource(realResourceName);
            if (url == null) continue;
            return url;
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream inputStream;
        String realResourceName = this.getAlternativeResourceName(name);
        for (ClassLoader classloader : this.classLoaders) {
            inputStream = classloader.getResourceAsStream(realResourceName);
            if (inputStream == null) continue;
            return inputStream;
        }
        if (!name.equals(realResourceName)) {
            log.debug("No resource found with alternate resourceName '{}'. Falling back to original name '{}'.", (Object)realResourceName, (Object)name);
            for (ClassLoader classloader : this.classLoaders) {
                inputStream = classloader.getResourceAsStream(name);
                if (inputStream == null) continue;
                return inputStream;
            }
        }
        return null;
    }

    private String getAlternativeResourceName(String name) {
        String resultName = name;
        if (this.resourceRedirects.containsKey(name)) {
            String redirectedName = this.resourceRedirects.get(name);
            log.debug("Redirecting resource '{}' to '{}'", (Object)name, (Object)redirectedName);
            resultName = redirectedName;
        }
        return resultName;
    }

    @Override
    public synchronized void setDefaultAssertionStatus(boolean enabled) {
        for (ClassLoader classloader : this.classLoaders) {
            classloader.setDefaultAssertionStatus(enabled);
        }
    }

    @Override
    public synchronized void setPackageAssertionStatus(String packageName, boolean enabled) {
        for (ClassLoader classloader : this.classLoaders) {
            classloader.setPackageAssertionStatus(packageName, enabled);
        }
    }

    @Override
    public synchronized void setClassAssertionStatus(String className, boolean enabled) {
        for (ClassLoader classloader : this.classLoaders) {
            classloader.setClassAssertionStatus(className, enabled);
        }
    }

    @Override
    public synchronized void clearAssertionStatus() {
        for (ClassLoader classloader : this.classLoaders) {
            classloader.clearAssertionStatus();
        }
    }

    private static final class ResourcesEnumeration
    implements Enumeration<URL> {
        private final List<Enumeration<URL>> resources;
        private final String resourceName;

        ResourcesEnumeration(String resourceName, List<ClassLoader> classLoaders) throws IOException {
            this.resourceName = resourceName;
            this.resources = new LinkedList<Enumeration<URL>>();
            for (ClassLoader classLoader : classLoaders) {
                this.resources.add(classLoader.getResources(resourceName));
            }
        }

        @Override
        public boolean hasMoreElements() {
            for (Enumeration<URL> resource : this.resources) {
                if (!resource.hasMoreElements()) continue;
                return true;
            }
            return false;
        }

        @Override
        public URL nextElement() {
            for (Enumeration<URL> resource : this.resources) {
                if (!resource.hasMoreElements()) continue;
                return resource.nextElement();
            }
            throw new NoSuchElementException(this.resourceName);
        }
    }
}

