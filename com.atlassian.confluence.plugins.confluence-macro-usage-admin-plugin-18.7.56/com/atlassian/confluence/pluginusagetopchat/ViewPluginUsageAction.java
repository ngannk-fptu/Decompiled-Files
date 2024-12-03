/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.search.CQLSearchService
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.macro.browser.MacroMetadataManager
 *  com.atlassian.confluence.macro.browser.beans.MacroMetadata
 *  com.atlassian.confluence.macro.browser.beans.MacroSummary
 *  com.atlassian.confluence.plugin.descriptor.XhtmlMacroModuleDescriptor
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.xwork.ActionViewDataMappings
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.pluginusagetopchat;

import com.atlassian.confluence.api.service.search.CQLSearchService;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.browser.beans.MacroSummary;
import com.atlassian.confluence.plugin.descriptor.XhtmlMacroModuleDescriptor;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.xwork.ActionViewDataMappings;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ViewPluginUsageAction
extends ConfluenceActionSupport {
    @ComponentImport(value="cqlSearchService")
    private CQLSearchService cqlSearchService;
    @ComponentImport
    private MacroMetadataManager macroMetadataManager;
    @ComponentImport
    private PluginMetadataManager pluginMetadataManager;
    private final Map<String, Object> data = new HashMap<String, Object>();
    private Map<String, String> mapMarketplacePluginsNameToKey = new HashMap<String, String>();
    private Map<String, Map<String, Integer>> mapPluginToMapMacroCount = new HashMap<String, Map<String, Integer>>();
    private Map<String, Integer> mapPluginUsage = new TreeMap<String, Integer>();
    private Map<String, List<String>> mapUnusedPluginToMacroList = new HashMap<String, List<String>>();

    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    @ActionViewDataMappings
    public Map<String, Object> getData() {
        return this.data;
    }

    public String execute() {
        this.getPluginUsageInfo();
        this.buildUnusedMarketplacePluginListAndUsedPluginMap();
        this.data.put("unusedpluginmacromap", this.getUnusedPluginToMapList());
        this.data.put("pluginusagemap", this.getPluginUsageMap());
        this.data.put("pluginmacrolistmap", this.getUsedPluginMacroListMap());
        this.data.put("pluginnamekeymap", this.getPlugNameToKeyMap());
        this.data.put("contextPath", ServletContextThreadLocal.getRequest().getContextPath());
        return "template/pluginusageList";
    }

    public void setCQLSearchService(CQLSearchService cqlSearchService) {
        this.cqlSearchService = cqlSearchService;
    }

    public void setMacroMetadataManager(MacroMetadataManager macroMetadataManager) {
        this.macroMetadataManager = macroMetadataManager;
    }

    public void setPluginMetadataManager(PluginMetadataManager pluginMetadataManager) {
        this.pluginMetadataManager = pluginMetadataManager;
    }

    private Map<String, List<String>> getUnusedPluginToMapList() {
        return this.mapUnusedPluginToMacroList;
    }

    private void getPluginUsageInfo() {
        List descriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(XhtmlMacroModuleDescriptor.class);
        for (MacroMetadata macroData : this.macroMetadataManager.getAllMacroMetadata()) {
            String pluginName;
            MacroSummary macroSummary = macroData.extractMacroSummary();
            String pluginKey = this.resolveOriginalPluginKey(descriptors, macroSummary);
            Plugin plugin = this.pluginAccessor.getPlugin(pluginKey);
            String string = pluginName = plugin == null ? pluginKey : plugin.getName();
            if (plugin == null || !plugin.isBundledPlugin() && !this.pluginMetadataManager.isSystemProvided(plugin)) {
                this.mapMarketplacePluginsNameToKey.put(pluginName, pluginKey);
            }
            String macroName = macroData.getMacroName();
            String cqlQuery = "macro = '" + macroName + "'";
            Integer countOfPageUsage = this.cqlSearchService.countContent(cqlQuery);
            this.appendPluginUsageData(pluginName, macroName, countOfPageUsage);
        }
    }

    private String resolveOriginalPluginKey(List<XhtmlMacroModuleDescriptor> descriptors, MacroSummary macroSummary) {
        if (macroSummary.getMacroName().equals("gadget")) {
            return "com.atlassian.confluence.plugins.gadgets";
        }
        Optional<XhtmlMacroModuleDescriptor> descriptor = descriptors.stream().filter(d -> d.getName().equals(macroSummary.getMacroName())).findFirst();
        if (descriptor.isPresent() && descriptor.get().getPluginKey().equals("com.atlassian.plugins.atlassian-connect-plugin")) {
            String key = descriptor.get().getKey();
            int indexOfKeyDelimiter = key.indexOf("__");
            if (indexOfKeyDelimiter > 0) {
                return key.substring(0, indexOfKeyDelimiter);
            }
            return macroSummary.getPluginKey();
        }
        return macroSummary.getPluginKey();
    }

    private void appendPluginUsageData(String pluginName, String macroName, Integer countOfPageUsage) {
        if (this.mapPluginToMapMacroCount.containsKey(pluginName)) {
            Map<String, Integer> mapMacroCount = this.mapPluginToMapMacroCount.get(pluginName);
            mapMacroCount.put(macroName, countOfPageUsage);
        } else {
            HashMap<String, Integer> mapMacroCount = new HashMap<String, Integer>();
            mapMacroCount.put(macroName, countOfPageUsage);
            this.mapPluginToMapMacroCount.put(pluginName, mapMacroCount);
        }
    }

    private void buildUnusedMarketplacePluginListAndUsedPluginMap() {
        for (Map.Entry<String, Map<String, Integer>> pluginMacroCount : this.mapPluginToMapMacroCount.entrySet()) {
            int usageCountPerPlugin = pluginMacroCount.getValue().values().stream().mapToInt(Integer::intValue).sum();
            if (usageCountPerPlugin == 0) {
                if (!this.mapMarketplacePluginsNameToKey.containsKey(pluginMacroCount.getKey())) continue;
                Set<String> macros = pluginMacroCount.getValue().keySet();
                ArrayList<String> sortedMacros = new ArrayList<String>(macros);
                Collections.sort(sortedMacros);
                this.mapUnusedPluginToMacroList.put(pluginMacroCount.getKey(), sortedMacros);
                continue;
            }
            this.mapPluginUsage.put(pluginMacroCount.getKey(), usageCountPerPlugin);
        }
    }

    private Map<String, Integer> getPluginUsageMap() {
        return this.orderCountsDescending(this.mapPluginUsage, false);
    }

    private Map<String, Map<String, Integer>> getUsedPluginMacroListMap() {
        Map<String, Map<String, Integer>> usedMap = this.mapPluginToMapMacroCount.entrySet().stream().filter(p -> this.mapPluginUsage.containsKey(p.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        usedMap.replaceAll((p, v) -> this.orderCountsDescending((Map)usedMap.get(p), true));
        return usedMap;
    }

    private Map<String, String> getPlugNameToKeyMap() {
        return this.mapMarketplacePluginsNameToKey;
    }

    private Map<String, Integer> orderCountsDescending(Map<String, Integer> macroCounts, boolean removeZero) {
        LinkedHashMap<String, Integer> sortedCounts = new LinkedHashMap<String, Integer>();
        macroCounts.entrySet().stream().filter(m -> !removeZero || (Integer)m.getValue() > 0).sorted((e1, e2) -> ((Integer)e2.getValue()).compareTo((Integer)e1.getValue())).forEach(e -> sortedCounts.put((String)e.getKey(), (Integer)e.getValue()));
        return sortedCounts;
    }
}

