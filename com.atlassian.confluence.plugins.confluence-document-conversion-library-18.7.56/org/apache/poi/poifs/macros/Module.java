/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.macros;

public interface Module {
    public String getContent();

    public ModuleType geModuleType();

    public static enum ModuleType {
        Document,
        Module,
        Class;

    }
}

