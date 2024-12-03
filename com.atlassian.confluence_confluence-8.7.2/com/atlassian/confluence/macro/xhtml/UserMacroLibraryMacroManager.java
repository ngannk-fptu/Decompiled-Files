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
import com.atlassian.confluence.renderer.UserMacroConfig;
import com.atlassian.confluence.renderer.UserMacroLibrary;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.util.concurrent.LazyReference;

public class UserMacroLibraryMacroManager
implements MacroManager {
    private UserMacroLibrary userMacroLibrary;

    public UserMacroLibraryMacroManager(UserMacroLibrary userMacroLibrary) {
        this.userMacroLibrary = userMacroLibrary;
    }

    @Override
    public Macro getMacroByName(String macroName) {
        UserMacroConfig config = this.userMacroLibrary.getMacro(macroName);
        if (config == null) {
            return null;
        }
        return config.toMacro();
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
        throw new UnsupportedOperationException("User Macros from the UserMacroLibrary do not come from the plugin subsystem so don't need to be lazy loaded.");
    }
}

