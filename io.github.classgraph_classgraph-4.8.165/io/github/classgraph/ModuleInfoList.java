/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.MappableInfoList;
import io.github.classgraph.ModuleInfo;
import java.util.Collection;

public class ModuleInfoList
extends MappableInfoList<ModuleInfo> {
    private static final long serialVersionUID = 1L;

    ModuleInfoList() {
    }

    ModuleInfoList(int sizeHint) {
        super(sizeHint);
    }

    ModuleInfoList(Collection<ModuleInfo> moduleInfoCollection) {
        super(moduleInfoCollection);
    }

    public ModuleInfoList filter(ModuleInfoFilter filter) {
        ModuleInfoList moduleInfoFiltered = new ModuleInfoList();
        for (ModuleInfo resource : this) {
            if (!filter.accept(resource)) continue;
            moduleInfoFiltered.add(resource);
        }
        return moduleInfoFiltered;
    }

    @FunctionalInterface
    public static interface ModuleInfoFilter {
        public boolean accept(ModuleInfo var1);
    }
}

