/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.macro.browser;

import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.browser.beans.MacroSummary;
import java.util.Collection;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface MacroMetadataProvider {
    public Collection<MacroMetadata> getData();

    public Collection<MacroSummary> getSummaries();

    public @Nullable MacroMetadata getByMacroName(String var1);

    public @Nullable MacroMetadata getByMacroNameAndId(String var1, String var2);
}

