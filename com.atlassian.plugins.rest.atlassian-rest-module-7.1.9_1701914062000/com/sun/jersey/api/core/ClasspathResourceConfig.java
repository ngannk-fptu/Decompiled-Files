/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.core;

import com.sun.jersey.api.core.ScanningResourceConfig;
import com.sun.jersey.core.spi.scanning.FilesScanner;
import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClasspathResourceConfig
extends ScanningResourceConfig {
    public static final String PROPERTY_CLASSPATH = "com.sun.jersey.config.property.classpath";
    private static final Logger LOGGER = Logger.getLogger(ClasspathResourceConfig.class.getName());

    public ClasspathResourceConfig() {
        this(ClasspathResourceConfig.getPaths());
    }

    public ClasspathResourceConfig(Map<String, Object> props) {
        this(ClasspathResourceConfig.getPaths(props));
        this.setPropertiesAndFeatures(props);
    }

    public ClasspathResourceConfig(String[] paths) {
        if (paths == null || paths.length == 0) {
            throw new IllegalArgumentException("Array of paths must not be null or empty");
        }
        this.init((String[])paths.clone());
    }

    private void init(String[] paths) {
        File[] files = new File[paths.length];
        for (int i = 0; i < paths.length; ++i) {
            files[i] = new File(paths[i]);
        }
        if (LOGGER.isLoggable(Level.INFO)) {
            StringBuilder b = new StringBuilder();
            b.append("Scanning for root resource and provider classes in the paths:");
            for (String p : paths) {
                b.append('\n').append("  ").append(p);
            }
            LOGGER.log(Level.INFO, b.toString());
        }
        this.init(new FilesScanner(files));
    }

    private static String[] getPaths() {
        String classPath = System.getProperty("java.class.path");
        return classPath.split(File.pathSeparator);
    }

    private static String[] getPaths(Map<String, Object> props) {
        Object v = props.get(PROPERTY_CLASSPATH);
        if (v == null) {
            throw new IllegalArgumentException("com.sun.jersey.config.property.classpath property is missing");
        }
        String[] paths = ClasspathResourceConfig.getPaths(v);
        if (paths.length == 0) {
            throw new IllegalArgumentException("com.sun.jersey.config.property.classpath contains no paths");
        }
        return paths;
    }

    private static String[] getPaths(Object param) {
        if (param instanceof String) {
            return ClasspathResourceConfig.getElements(new String[]{(String)param}, " ,;\n");
        }
        if (param instanceof String[]) {
            return ClasspathResourceConfig.getElements((String[])param, " ,;\n");
        }
        throw new IllegalArgumentException("com.sun.jersey.config.property.classpath must have a property value of type String or String[]");
    }
}

