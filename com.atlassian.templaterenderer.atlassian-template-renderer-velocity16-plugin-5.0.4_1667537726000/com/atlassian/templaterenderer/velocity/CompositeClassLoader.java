/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.templaterenderer.velocity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompositeClassLoader
extends ClassLoader {
    private static final Logger log = LoggerFactory.getLogger(CompositeClassLoader.class);
    private final Set<ClassLoader> classLoaders;

    public CompositeClassLoader(ClassLoader ... classLoaders) {
        super(null);
        if (classLoaders == null || classLoaders.length == 0) {
            throw new IllegalArgumentException("At least one classLoader must be supplied!");
        }
        this.classLoaders = new LinkedHashSet<ClassLoader>(Arrays.asList(classLoaders));
    }

    @Override
    public void clearAssertionStatus() {
        for (ClassLoader classLoader : this.classLoaders) {
            classLoader.clearAssertionStatus();
        }
    }

    @Override
    public URL getResource(String name) {
        for (ClassLoader classLoader : this.classLoaders) {
            URL resource = classLoader.getResource(name);
            if (resource == null) continue;
            return resource;
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        for (ClassLoader classLoader : this.classLoaders) {
            InputStream resource = classLoader.getResourceAsStream(name);
            if (resource == null) continue;
            return resource;
        }
        return null;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        IOException ioe = null;
        for (ClassLoader classLoader : this.classLoaders) {
            try {
                Enumeration<URL> resources = classLoader.getResources(name);
                if (resources == null || !resources.hasMoreElements()) continue;
                return resources;
            }
            catch (IOException e) {
                log.debug("Underlying classloader '" + classLoader + "' threw IOException", (Throwable)e);
                ioe = e;
            }
        }
        if (ioe != null) {
            throw ioe;
        }
        return null;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        for (ClassLoader classLoader : this.classLoaders) {
            try {
                Class<?> aClass = classLoader.loadClass(name);
                if (aClass == null) continue;
                return aClass;
            }
            catch (ClassNotFoundException e) {
                log.debug("Underlying classloader '" + classLoader + "' couldn't find class: " + e.getMessage());
            }
        }
        throw new ClassNotFoundException("Class '" + name + "' could not be found!");
    }

    @Override
    public void setClassAssertionStatus(String className, boolean enabled) {
        for (ClassLoader classLoader : this.classLoaders) {
            classLoader.setClassAssertionStatus(className, enabled);
        }
    }

    @Override
    public void setDefaultAssertionStatus(boolean enabled) {
        for (ClassLoader classLoader : this.classLoaders) {
            classLoader.setDefaultAssertionStatus(enabled);
        }
    }

    @Override
    public void setPackageAssertionStatus(String packageName, boolean enabled) {
        for (ClassLoader classLoader : this.classLoaders) {
            classLoader.setPackageAssertionStatus(packageName, enabled);
        }
    }
}

