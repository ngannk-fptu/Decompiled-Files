/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components;

import com.atlassian.renderer.v2.components.MacroTag;

public interface WikiContentHandler {
    public void handleMacro(StringBuffer var1, MacroTag var2, String var3);

    public void handleText(StringBuffer var1, String var2);
}

