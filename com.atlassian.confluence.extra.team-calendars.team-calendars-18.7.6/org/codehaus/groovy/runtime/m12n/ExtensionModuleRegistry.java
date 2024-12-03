/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.m12n;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.groovy.runtime.m12n.ExtensionModule;

public class ExtensionModuleRegistry {
    private final List<ExtensionModule> modules = new LinkedList<ExtensionModule>();

    public void addModule(ExtensionModule module) {
        this.modules.add(module);
    }

    public void removeModule(ExtensionModule module) {
        this.modules.remove(module);
    }

    public List<ExtensionModule> getModules() {
        return new ArrayList<ExtensionModule>(this.modules);
    }

    public boolean hasModule(String moduleName) {
        for (ExtensionModule module : this.modules) {
            if (!module.getName().equals(moduleName)) continue;
            return true;
        }
        return false;
    }

    public ExtensionModule getModule(String moduleName) {
        for (ExtensionModule module : this.modules) {
            if (!module.getName().equals(moduleName)) continue;
            return module;
        }
        return null;
    }
}

