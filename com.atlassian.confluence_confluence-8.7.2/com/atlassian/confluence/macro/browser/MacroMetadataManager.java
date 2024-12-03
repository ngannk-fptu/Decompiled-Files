/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.macro.browser;

import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.browser.beans.MacroParameter;
import com.atlassian.confluence.macro.browser.beans.MacroParameterType;
import com.atlassian.confluence.macro.browser.beans.MacroSummary;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface MacroMetadataManager {
    public @NonNull Set<MacroMetadata> getAllMacroMetadata();

    public @NonNull Set<MacroSummary> getAllMacroSummaries();

    public @Nullable MacroMetadata getMacroMetadataByName(String var1);

    public @Nullable MacroMetadata getMacroMetadataByNameAndId(String var1, String var2);

    public @NonNull Map<String, MacroParameterType> getParameterTypes(String var1);

    public @NonNull Map<String, MacroParameter> getParameters(String var1);
}

