/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.plugins.pageproperties.api.model;

import com.atlassian.confluence.plugins.pageproperties.api.model.PagePropertiesMacroInstance;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PagePropertiesMacroReport {
    private final Map<String, List<PagePropertiesMacroInstance>> macroInstancesMap;

    public PagePropertiesMacroReport(Map<String, List<PagePropertiesMacroInstance>> macroInstancesMap) {
        this.macroInstancesMap = ImmutableMap.copyOf(macroInstancesMap);
    }

    public List<PagePropertiesMacroInstance> getMacroInstancesForId(String pagePropertiesMacroId) {
        return this.macroInstancesMap.get(pagePropertiesMacroId);
    }

    public List<PagePropertiesMacroInstance> getAllMacroInstances() {
        return this.macroInstancesMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }
}

