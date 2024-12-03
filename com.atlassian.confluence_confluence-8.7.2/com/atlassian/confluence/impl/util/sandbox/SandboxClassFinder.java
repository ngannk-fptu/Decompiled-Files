/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.ByteStreams
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SandboxClassFinder {
    private static final Logger logger = LoggerFactory.getLogger(SandboxClassFinder.class);
    private final List<ClassLoader> classLoaders;

    SandboxClassFinder(List<ClassLoader> classLoaders) {
        this.classLoaders = Objects.requireNonNull(classLoaders);
    }

    Class<?> loadClass(String name) throws ClassNotFoundException {
        logger.debug("Attempt to load class " + name);
        for (ClassLoader loader : this.classLoaders) {
            try {
                return loader.loadClass(name);
            }
            catch (ClassNotFoundException e) {
                logger.debug("Classloader {} was not able to find class {}", (Object)loader, (Object)name);
            }
        }
        throw new ClassNotFoundException("Class finder was not able to find " + name);
    }

    byte[] findClass(String name) {
        logger.debug("Attempt to load class " + name);
        byte[] payload = new byte[]{};
        String resourcePath = name.replace('.', '/') + ".class";
        for (ClassLoader loader : this.classLoaders) {
            try {
                InputStream resourceAsStream = loader.getResourceAsStream(resourcePath);
                try {
                    if (resourceAsStream == null) continue;
                    payload = ByteStreams.toByteArray((InputStream)resourceAsStream);
                    break;
                }
                finally {
                    if (resourceAsStream == null) continue;
                    resourceAsStream.close();
                }
            }
            catch (IOException iOException) {}
        }
        if (payload.length == 0) {
            logger.debug("Can't find class " + name);
        }
        return payload;
    }

    byte[] findResource(String name) {
        logger.debug("Attempt to load resource " + name);
        byte[] payload = new byte[]{};
        for (ClassLoader loader : this.classLoaders) {
            try {
                InputStream resourceAsStream = loader.getResourceAsStream(name);
                try {
                    if (resourceAsStream == null) continue;
                    payload = ByteStreams.toByteArray((InputStream)resourceAsStream);
                    break;
                }
                finally {
                    if (resourceAsStream == null) continue;
                    resourceAsStream.close();
                }
            }
            catch (IOException iOException) {}
        }
        if (payload.length == 0) {
            logger.debug("Can't find resource " + name);
        }
        return payload;
    }

    List<byte[]> findResources(String name) {
        logger.debug("Attempt to load resources " + name);
        ArrayList<byte[]> payload = new ArrayList<byte[]>();
        for (ClassLoader loader : this.classLoaders) {
            try {
                Enumeration<URL> resources = loader.getResources(name);
                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    InputStream resourceAsStream = url.openStream();
                    try {
                        if (resourceAsStream == null) continue;
                        payload.add(ByteStreams.toByteArray((InputStream)resourceAsStream));
                    }
                    finally {
                        if (resourceAsStream == null) continue;
                        resourceAsStream.close();
                    }
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            if (payload.size() <= 0) continue;
            break;
        }
        if (payload.size() == 0) {
            logger.debug("Can't find resources " + name);
        }
        return payload;
    }
}

