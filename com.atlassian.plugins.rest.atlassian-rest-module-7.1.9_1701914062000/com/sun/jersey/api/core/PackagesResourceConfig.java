/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.core;

import com.sun.jersey.api.core.ScanningResourceConfig;
import com.sun.jersey.core.spi.scanning.PackageNamesScanner;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PackagesResourceConfig
extends ScanningResourceConfig {
    public static final String PROPERTY_PACKAGES = "com.sun.jersey.config.property.packages";
    private static final Logger LOGGER = Logger.getLogger(PackagesResourceConfig.class.getName());

    public PackagesResourceConfig(String ... packages) {
        if (packages == null || packages.length == 0) {
            throw new IllegalArgumentException("Array of packages must not be null or empty");
        }
        this.init((String[])packages.clone());
    }

    public PackagesResourceConfig(Map<String, Object> props) {
        this(PackagesResourceConfig.getPackages(props));
        this.setPropertiesAndFeatures(props);
    }

    private void init(String[] packages) {
        if (LOGGER.isLoggable(Level.INFO)) {
            StringBuilder b = new StringBuilder();
            b.append("Scanning for root resource and provider classes in the packages:");
            for (String p : packages) {
                b.append('\n').append("  ").append(p);
            }
            LOGGER.log(Level.INFO, b.toString());
        }
        this.init(new PackageNamesScanner(packages));
    }

    private static String[] getPackages(Map<String, Object> props) {
        Object v = props.get(PROPERTY_PACKAGES);
        if (v == null) {
            throw new IllegalArgumentException("com.sun.jersey.config.property.packages property is missing");
        }
        String[] packages = PackagesResourceConfig.getPackages(v);
        if (packages.length == 0) {
            throw new IllegalArgumentException("com.sun.jersey.config.property.packages contains no packages");
        }
        return packages;
    }

    private static String[] getPackages(Object param) {
        if (param instanceof String) {
            return PackagesResourceConfig.getElements(new String[]{(String)param}, " ,;\n");
        }
        if (param instanceof String[]) {
            return PackagesResourceConfig.getElements((String[])param, " ,;\n");
        }
        throw new IllegalArgumentException("com.sun.jersey.config.property.packages must have a property value of type String or String[]");
    }
}

