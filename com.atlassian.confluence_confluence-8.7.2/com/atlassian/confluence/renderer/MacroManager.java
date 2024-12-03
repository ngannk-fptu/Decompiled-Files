/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.v2.macro.Macro
 *  com.atlassian.renderer.v2.macro.MacroManager
 */
package com.atlassian.confluence.renderer;

import com.atlassian.renderer.v2.macro.Macro;
import java.util.Map;

public interface MacroManager
extends com.atlassian.renderer.v2.macro.MacroManager {
    public Map<String, Macro> getMacros();

    public void registerMacro(String var1, Macro var2);

    public void unregisterMacro(String var1);
}

