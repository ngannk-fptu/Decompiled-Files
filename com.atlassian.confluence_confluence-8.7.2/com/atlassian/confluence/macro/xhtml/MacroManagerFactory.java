/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.xhtml;

import com.atlassian.confluence.macro.xhtml.MacroManager;

public interface MacroManagerFactory {
    public MacroManager getDefaultMacroManager();

    public MacroManager getXhtmlMacroManager();

    public MacroManager getUserMacroMacroManager();
}

