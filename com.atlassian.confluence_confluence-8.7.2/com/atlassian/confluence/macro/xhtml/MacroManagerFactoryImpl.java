/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.xhtml;

import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.macro.xhtml.MacroManagerFactory;

public class MacroManagerFactoryImpl
implements MacroManagerFactory {
    private final MacroManager defaultMacroManager;
    private final MacroManager xhtmlMacroManager;
    private final MacroManager userMacroManager;

    public MacroManagerFactoryImpl(MacroManager defaultMacroManager, MacroManager xhtmlMacroManager, MacroManager userMacroManager) {
        this.defaultMacroManager = defaultMacroManager;
        this.xhtmlMacroManager = xhtmlMacroManager;
        this.userMacroManager = userMacroManager;
    }

    @Override
    public MacroManager getDefaultMacroManager() {
        return this.defaultMacroManager;
    }

    @Override
    public MacroManager getXhtmlMacroManager() {
        return this.xhtmlMacroManager;
    }

    @Override
    public MacroManager getUserMacroMacroManager() {
        return this.userMacroManager;
    }
}

