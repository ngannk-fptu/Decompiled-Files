/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.fortuna.ical4j.util;

import java.io.InputStream;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceLoader {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceLoader.class);

    public static URL getResource(String name) {
        URL resource = null;
        try {
            if (Thread.currentThread().getContextClassLoader() != null) {
                resource = Thread.currentThread().getContextClassLoader().getResource(name);
            }
        }
        catch (SecurityException e) {
            LOG.info("Unable to access context classloader, using default. " + e.getMessage());
        }
        if (resource == null) {
            resource = ResourceLoader.class.getResource("/" + name);
        }
        return resource;
    }

    public static InputStream getResourceAsStream(String name) {
        InputStream stream = null;
        try {
            stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        }
        catch (SecurityException e) {
            LOG.info("Unable to access context classloader, using default. " + e.getMessage());
        }
        if (stream == null) {
            stream = ResourceLoader.class.getResourceAsStream("/" + name);
        }
        return stream;
    }
}

