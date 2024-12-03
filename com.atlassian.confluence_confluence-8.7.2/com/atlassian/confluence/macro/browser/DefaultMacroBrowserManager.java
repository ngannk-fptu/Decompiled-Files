/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.macro.browser;

import com.atlassian.confluence.macro.browser.MacroBrowserManager;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.browser.beans.MacroCategory;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.browser.beans.MacroSummary;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class DefaultMacroBrowserManager
implements MacroBrowserManager {
    private final Set<MacroCategory> macroCategories = Collections.unmodifiableSet(EnumSet.allOf(MacroCategory.class));
    private MacroMetadataManager metadataManager;

    public DefaultMacroBrowserManager(MacroMetadataManager metadataManager) {
        this.metadataManager = metadataManager;
    }

    @Override
    public Set<MacroCategory> getMacroCategories() {
        return this.macroCategories;
    }

    @Override
    public Set<MacroMetadata> getMacroMetadata() {
        return this.metadataManager.getAllMacroMetadata();
    }

    @Override
    public Set<MacroMetadata> getMacroMetadata(Collection<String> whitelist) {
        HashSet whitelistSet = Sets.newHashSet(whitelist);
        return Sets.newHashSet((Iterable)Iterables.filter(this.metadataManager.getAllMacroMetadata(), macroMetadata -> whitelistSet.contains(macroMetadata.getMacroName())));
    }

    @Override
    public Set<MacroSummary> getMacroSummaries() {
        return this.metadataManager.getAllMacroSummaries();
    }
}

