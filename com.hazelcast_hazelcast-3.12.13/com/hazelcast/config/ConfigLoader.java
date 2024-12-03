/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.UrlXmlConfig;
import com.hazelcast.util.EmptyStatement;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public final class ConfigLoader {
    private static final String CLASSPATH_PREFIX = "classpath:";

    private ConfigLoader() {
    }

    public static Config load(String path) throws IOException {
        URL url = ConfigLoader.locateConfig(path);
        if (url == null) {
            return null;
        }
        return new UrlXmlConfig(url);
    }

    public static URL locateConfig(String path) {
        if (path.isEmpty()) {
            return null;
        }
        URL url = ConfigLoader.asFile(path);
        if (url == null) {
            url = ConfigLoader.asURL(path);
        }
        if (url == null) {
            url = ConfigLoader.asResource(path);
        }
        if (url == null) {
            String extractedPath = ConfigLoader.extractPathOrNull(path);
            if (extractedPath == null) {
                return null;
            }
            url = ConfigLoader.asResource(extractedPath);
        }
        return url;
    }

    private static String extractPathOrNull(String path) {
        if (path.startsWith(CLASSPATH_PREFIX)) {
            return path.substring(CLASSPATH_PREFIX.length());
        }
        return null;
    }

    private static URL asFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                return file.toURI().toURL();
            }
            catch (MalformedURLException ignored) {
                EmptyStatement.ignore(ignored);
            }
        }
        return null;
    }

    private static URL asURL(String path) {
        try {
            return new URL(path);
        }
        catch (MalformedURLException ignored) {
            EmptyStatement.ignore(ignored);
            return null;
        }
    }

    private static URL asResource(String path) {
        URL url = null;
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            url = contextClassLoader.getResource(path);
        }
        if (url == null) {
            url = ConfigLoader.class.getClassLoader().getResource(path);
        }
        if (url == null) {
            url = ClassLoader.getSystemClassLoader().getResource(path);
        }
        return url;
    }
}

