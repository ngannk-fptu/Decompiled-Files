/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.rometools.rome.feed.module.impl;

import com.rometools.rome.feed.module.Module;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModuleUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ModuleUtils.class);

    private ModuleUtils() {
    }

    public static List<Module> cloneModules(List<Module> modules) {
        ArrayList<Module> cModules = null;
        if (modules != null) {
            cModules = new ArrayList<Module>();
            for (Module module : modules) {
                try {
                    Module c = (Module)module.clone();
                    cModules.add(c);
                }
                catch (Exception e) {
                    String moduleUri = module.getUri();
                    LOG.error("Error while cloning module " + moduleUri, (Throwable)e);
                    throw new RuntimeException("Cloning modules " + moduleUri, e);
                }
            }
        }
        return cModules;
    }

    public static Module getModule(List<Module> modules, String uri) {
        Module searchedModule = null;
        if (modules != null) {
            for (Module module : modules) {
                if (!module.getUri().equals(uri)) continue;
                searchedModule = module;
                break;
            }
        }
        return searchedModule;
    }
}

