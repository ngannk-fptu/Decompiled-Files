/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package com.sun.jersey.api.core.servlet;

import com.sun.jersey.api.core.ScanningResourceConfig;
import com.sun.jersey.spi.scanning.servlet.WebAppResourcesScanner;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

public class WebAppResourceConfig
extends ScanningResourceConfig {
    private static final Logger LOGGER = Logger.getLogger(WebAppResourceConfig.class.getName());

    public WebAppResourceConfig(Map<String, Object> props, ServletContext sc) {
        this(WebAppResourceConfig.getPaths(props), sc);
        this.setPropertiesAndFeatures(props);
    }

    public WebAppResourceConfig(String[] paths, ServletContext sc) {
        if (paths == null || paths.length == 0) {
            throw new IllegalArgumentException("Array of paths must not be null or empty");
        }
        this.init(paths, sc);
    }

    private void init(String[] paths, ServletContext sc) {
        if (LOGGER.isLoggable(Level.INFO)) {
            StringBuilder b = new StringBuilder();
            b.append("Scanning for root resource and provider classes in the Web app resource paths:");
            for (String p : paths) {
                b.append('\n').append("  ").append(p);
            }
            LOGGER.log(Level.INFO, b.toString());
        }
        this.init(new WebAppResourcesScanner(paths, sc));
    }

    private static String[] getPaths(Map<String, Object> props) {
        Object v = props.get("com.sun.jersey.config.property.classpath");
        if (v == null) {
            return new String[]{"/WEB-INF/lib", "/WEB-INF/classes"};
        }
        String[] paths = WebAppResourceConfig.getPaths(v);
        if (paths.length == 0) {
            throw new IllegalArgumentException("com.sun.jersey.config.property.classpath contains no paths");
        }
        return paths;
    }

    private static String[] getPaths(Object param) {
        if (param instanceof String) {
            return WebAppResourceConfig.getElements(new String[]{(String)param});
        }
        if (param instanceof String[]) {
            return WebAppResourceConfig.getElements((String[])param);
        }
        throw new IllegalArgumentException("com.sun.jersey.config.property.classpath must have a property value of type String or String[]");
    }
}

