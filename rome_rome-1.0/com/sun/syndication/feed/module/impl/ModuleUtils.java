/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.module.impl;

import com.sun.syndication.feed.module.Module;
import java.util.ArrayList;
import java.util.List;

public class ModuleUtils {
    public static List cloneModules(List modules) {
        ArrayList<Object> cModules = null;
        if (modules != null) {
            cModules = new ArrayList<Object>();
            for (int i = 0; i < modules.size(); ++i) {
                Module module = (Module)modules.get(i);
                try {
                    Object c = module.clone();
                    cModules.add(c);
                    continue;
                }
                catch (Exception ex) {
                    throw new RuntimeException("Cloning modules", ex);
                }
            }
        }
        return cModules;
    }

    public static Module getModule(List modules, String uri) {
        Module module = null;
        for (int i = 0; module == null && modules != null && i < modules.size(); ++i) {
            module = (Module)modules.get(i);
            if (module.getUri().equals(uri)) continue;
            module = null;
        }
        return module;
    }
}

