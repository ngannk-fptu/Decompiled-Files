/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.macro.browser;

import com.atlassian.confluence.macro.browser.MacroMetadataProvider;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.browser.beans.MacroSummary;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class DelegatingMacroMetadataProvider
implements MacroMetadataProvider {
    private List<MacroMetadataProvider> macroMetadataProviders;

    @Override
    public Collection<MacroMetadata> getData() {
        HashMap<String, MacroMetadata> macroMetadataMap = new HashMap<String, MacroMetadata>();
        for (MacroMetadataProvider macroMetadataProvider : this.macroMetadataProviders) {
            for (MacroMetadata macroMetadata : macroMetadataProvider.getData()) {
                String name = macroMetadata.getMacroName();
                if (macroMetadataMap.get(name) != null) continue;
                macroMetadataMap.put(name, macroMetadata);
            }
        }
        return macroMetadataMap.values();
    }

    @Override
    public Collection<MacroSummary> getSummaries() {
        HashMap map = Maps.newHashMap();
        for (MacroMetadataProvider macroMetadataProvider : this.macroMetadataProviders) {
            for (MacroSummary summary : macroMetadataProvider.getSummaries()) {
                String name = summary.getMacroName();
                if (map.get(name) != null) continue;
                map.put(name, summary);
            }
        }
        return map.values();
    }

    @Override
    public MacroMetadata getByMacroName(String macroName) {
        return this.getByMacroNameAndId(macroName, null);
    }

    @Override
    public MacroMetadata getByMacroNameAndId(String macroName, String alternateId) {
        for (MacroMetadataProvider macroMetadataProvider : this.macroMetadataProviders) {
            MacroMetadata metadata = macroMetadataProvider.getByMacroNameAndId(macroName, alternateId);
            if (metadata == null) continue;
            return metadata;
        }
        return null;
    }

    public void setMacroMetadataProviders(List<MacroMetadataProvider> macroMetadataProviders) {
        this.macroMetadataProviders = macroMetadataProviders;
    }
}

