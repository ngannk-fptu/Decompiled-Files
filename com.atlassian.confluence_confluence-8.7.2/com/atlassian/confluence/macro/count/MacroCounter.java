/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.macro.count;

import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.count.MacroCount;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.util.Collection;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface MacroCounter {
    public void addMacroUsage(MacroDefinition var1, @Nullable Macro var2);

    public Collection<MacroCount> getUsages();
}

