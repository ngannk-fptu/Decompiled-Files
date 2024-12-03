/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.util.concurrent.LazyReference
 */
package com.atlassian.confluence.macro.xhtml;

import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.util.concurrent.LazyReference;
import java.util.Iterator;
import java.util.List;

public class DelegatingReadOnlyMacroManager
implements MacroManager {
    List<MacroManager> delegateMacroManagers;

    public DelegatingReadOnlyMacroManager(List<MacroManager> delegateMacroManagers) {
        this.delegateMacroManagers = delegateMacroManagers;
    }

    @Override
    public Macro getMacroByName(String macroName) {
        MacroManager delegate;
        Macro macro = null;
        Iterator<MacroManager> iterator = this.delegateMacroManagers.iterator();
        while (iterator.hasNext() && (macro = (delegate = iterator.next()).getMacroByName(macroName)) == null) {
        }
        return macro;
    }

    @Override
    public void registerMacro(String name, Macro macro) {
        throw new UnsupportedOperationException("You cannot register macros with this manager. It is read only.");
    }

    @Override
    public void unregisterMacro(String name) {
        throw new UnsupportedOperationException("You cannot unregister macros with this manager. It is read only.");
    }

    @Override
    @Deprecated
    public LazyReference<Macro> createLazyMacroReference(ModuleDescriptor<?> moduleDescriptor) {
        throw new UnsupportedOperationException("This DelegatingReadOnlyMacroManager does not directly created Macros so this operation is not supported.");
    }
}

