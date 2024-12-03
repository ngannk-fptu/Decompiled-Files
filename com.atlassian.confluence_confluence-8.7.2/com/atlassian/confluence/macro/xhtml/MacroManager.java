/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.misc.LazyReferenceAdapter
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.util.concurrent.LazyReference
 *  io.atlassian.util.concurrent.LazyReference
 */
package com.atlassian.confluence.macro.xhtml;

import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.util.misc.LazyReferenceAdapter;
import com.atlassian.plugin.ModuleDescriptor;
import io.atlassian.util.concurrent.LazyReference;

public interface MacroManager {
    public Macro getMacroByName(String var1);

    public void registerMacro(String var1, Macro var2);

    public void unregisterMacro(String var1);

    @Deprecated
    public com.atlassian.util.concurrent.LazyReference<Macro> createLazyMacroReference(ModuleDescriptor<?> var1);

    default public LazyReference<Macro> newLazyMacroReference(ModuleDescriptor<?> moduleDescriptor) {
        return new LazyReferenceAdapter(this.createLazyMacroReference(moduleDescriptor));
    }
}

