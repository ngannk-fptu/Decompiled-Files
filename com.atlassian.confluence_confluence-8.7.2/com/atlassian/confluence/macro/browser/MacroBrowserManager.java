/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.browser;

import com.atlassian.confluence.macro.browser.beans.MacroCategory;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.browser.beans.MacroSummary;
import java.util.Collection;
import java.util.Set;

public interface MacroBrowserManager {
    public Set<MacroCategory> getMacroCategories();

    public Set<MacroMetadata> getMacroMetadata();

    public Set<MacroMetadata> getMacroMetadata(Collection<String> var1);

    public Set<MacroSummary> getMacroSummaries();
}

