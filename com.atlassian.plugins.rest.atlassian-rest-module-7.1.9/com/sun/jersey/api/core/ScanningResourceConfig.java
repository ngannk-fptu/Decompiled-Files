/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.core;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.core.spi.scanning.Scanner;
import com.sun.jersey.spi.container.ReloadListener;
import com.sun.jersey.spi.scanning.PathProviderScannerListener;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

public class ScanningResourceConfig
extends DefaultResourceConfig
implements ReloadListener {
    private static final Logger LOGGER = Logger.getLogger(ScanningResourceConfig.class.getName());
    private Scanner scanner;
    private final Set<Class<?>> cachedClasses = new HashSet();

    public void init(Scanner scanner) {
        this.scanner = scanner;
        PathProviderScannerListener asl = new PathProviderScannerListener();
        scanner.scan(asl);
        this.getClasses().addAll(asl.getAnnotatedClasses());
        if (LOGGER.isLoggable(Level.INFO) && !this.getClasses().isEmpty()) {
            Set<Class> rootResourceClasses = this.get(Path.class);
            if (rootResourceClasses.isEmpty()) {
                LOGGER.log(Level.INFO, "No root resource classes found.");
            } else {
                this.logClasses("Root resource classes found:", rootResourceClasses);
            }
            Set<Class> providerClasses = this.get(Provider.class);
            if (providerClasses.isEmpty()) {
                LOGGER.log(Level.INFO, "No provider classes found.");
            } else {
                this.logClasses("Provider classes found:", providerClasses);
            }
        }
        this.cachedClasses.clear();
        this.cachedClasses.addAll(this.getClasses());
    }

    @Deprecated
    public void reload() {
        this.onReload();
    }

    @Override
    public void onReload() {
        HashSet classesToRemove = new HashSet();
        HashSet classesToAdd = new HashSet();
        for (Class<?> c : this.getClasses()) {
            if (this.cachedClasses.contains(c)) continue;
            classesToAdd.add(c);
        }
        for (Class<?> c : this.cachedClasses) {
            if (this.getClasses().contains(c)) continue;
            classesToRemove.add(c);
        }
        this.getClasses().clear();
        this.init(this.scanner);
        this.getClasses().addAll(classesToAdd);
        this.getClasses().removeAll(classesToRemove);
    }

    private Set<Class> get(Class<? extends Annotation> ac) {
        HashSet<Class> s = new HashSet<Class>();
        for (Class<?> c : this.getClasses()) {
            if (!c.isAnnotationPresent(ac)) continue;
            s.add(c);
        }
        return s;
    }

    private void logClasses(String s, Set<Class> classes) {
        StringBuilder b = new StringBuilder();
        b.append(s);
        for (Class c : classes) {
            b.append('\n').append("  ").append(c);
        }
        LOGGER.log(Level.INFO, b.toString());
    }
}

