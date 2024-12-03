/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

@Deprecated
public final class ConfigHelper {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(ConfigHelper.class);

    public static URL locateConfig(String path) {
        try {
            return new URL(path);
        }
        catch (MalformedURLException e) {
            return ConfigHelper.findAsResource(path);
        }
    }

    public static URL findAsResource(String path) {
        URL url = null;
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            url = contextClassLoader.getResource(path);
        }
        if (url != null) {
            return url;
        }
        url = ConfigHelper.class.getClassLoader().getResource(path);
        if (url != null) {
            return url;
        }
        url = ClassLoader.getSystemClassLoader().getResource(path);
        return url;
    }

    public static InputStream getConfigStream(String path) throws HibernateException {
        URL url = ConfigHelper.locateConfig(path);
        if (url == null) {
            String msg = LOG.unableToLocateConfigFile(path);
            LOG.error(msg);
            throw new HibernateException(msg);
        }
        try {
            return url.openStream();
        }
        catch (IOException e) {
            throw new HibernateException("Unable to open config file: " + path, e);
        }
    }

    private ConfigHelper() {
    }

    public static InputStream getResourceAsStream(String resource) {
        String stripped = resource.startsWith("/") ? resource.substring(1) : resource;
        InputStream stream = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            stream = classLoader.getResourceAsStream(stripped);
        }
        if (stream == null) {
            stream = Environment.class.getResourceAsStream(resource);
        }
        if (stream == null) {
            stream = Environment.class.getClassLoader().getResourceAsStream(stripped);
        }
        if (stream == null) {
            throw new HibernateException(resource + " not found");
        }
        return stream;
    }

    public static InputStream getUserResourceAsStream(String resource) {
        boolean hasLeadingSlash = resource.startsWith("/");
        String stripped = hasLeadingSlash ? resource.substring(1) : resource;
        InputStream stream = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null && (stream = classLoader.getResourceAsStream(resource)) == null && hasLeadingSlash) {
            stream = classLoader.getResourceAsStream(stripped);
        }
        if (stream == null) {
            stream = Environment.class.getClassLoader().getResourceAsStream(resource);
        }
        if (stream == null && hasLeadingSlash) {
            stream = Environment.class.getClassLoader().getResourceAsStream(stripped);
        }
        if (stream == null) {
            throw new HibernateException(resource + " not found");
        }
        return stream;
    }
}

