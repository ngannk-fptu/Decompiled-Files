/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.sal.api.ApplicationProperties
 */
package com.atlassian.zdu;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.zdu.api.ZduService;
import java.util.Map;

public class GlobalHeaderContext
implements ContextProvider {
    private final ApplicationProperties applicationProperties;
    private final ZduService zduService;

    public GlobalHeaderContext(ApplicationProperties applicationProperties, ZduService zduService) {
        this.applicationProperties = applicationProperties;
        this.zduService = zduService;
    }

    public void init(Map<String, String> map) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> map) {
        map.put("productName", this.applicationProperties.getDisplayName());
        map.put("cluster", this.zduService.getCluster());
        return map;
    }
}

