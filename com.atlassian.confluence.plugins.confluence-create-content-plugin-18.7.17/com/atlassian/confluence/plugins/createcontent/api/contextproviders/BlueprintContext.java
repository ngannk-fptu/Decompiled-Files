/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.plugin.ModuleCompleteKey
 */
package com.atlassian.confluence.plugins.createcontent.api.contextproviders;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContextKeys;
import com.atlassian.plugin.ModuleCompleteKey;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PublicApi
public class BlueprintContext {
    private final Map<String, Object> map;

    BlueprintContext(Map<String, Object> context) {
        this.map = context;
    }

    public BlueprintContext() {
        this(new HashMap<String, Object>());
    }

    public Map<String, Object> getMap() {
        return this.map;
    }

    public ModuleCompleteKey getBlueprintModuleCompleteKey() {
        return (ModuleCompleteKey)this.get(BlueprintContextKeys.BLUEPRINT_MODULE_KEY);
    }

    public UUID getBlueprintId() {
        return (UUID)this.get(BlueprintContextKeys.BLUEPRINT_ID);
    }

    public String getSpaceKey() {
        return (String)this.get(BlueprintContextKeys.SPACE_KEY);
    }

    public String getAnalyticsKey() {
        return (String)this.get(BlueprintContextKeys.ANALYTICS_KEY);
    }

    public String getTemplateLabel() {
        return (String)this.get(BlueprintContextKeys.TEMPLATE_LABEL);
    }

    public String getCreateResult() {
        return (String)this.get(BlueprintContextKeys.CREATE_RESULT);
    }

    public String getCreateFromTemplateLabel() {
        return (String)this.get(BlueprintContextKeys.CREATE_FROM_TEMPLATE_LABEL);
    }

    public Object get(String key) {
        return this.map.get(key);
    }

    public void put(String key, Object value) {
        this.map.put(key, value);
    }

    public void setTitle(String title) {
        this.put(BlueprintContextKeys.CONTENT_PAGE_TITLE, (Object)title);
    }

    public void setCreateFromTemplateLabel(String label) {
        this.put(BlueprintContextKeys.CREATE_FROM_TEMPLATE_LABEL, (Object)label);
        this.put("pageFromTemplateTitle", (Object)label);
    }

    private Object get(BlueprintContextKeys contextKey) {
        return this.map.get(contextKey.key());
    }

    private void put(BlueprintContextKeys contextKey, Object value) {
        this.map.put(contextKey.key(), value);
    }

    public void setTemplateLabel(String label) {
        this.put(BlueprintContextKeys.TEMPLATE_LABEL, (Object)label);
    }

    public void setAnalyticsKey(String key) {
        this.put(BlueprintContextKeys.ANALYTICS_KEY, (Object)key);
    }

    public void setSpaceKey(String spaceKey) {
        this.put(BlueprintContextKeys.SPACE_KEY, (Object)spaceKey);
    }

    public void setBlueprintModuleCompleteKey(ModuleCompleteKey key) {
        this.put(BlueprintContextKeys.BLUEPRINT_MODULE_KEY, (Object)key);
    }

    public void setBlueprintId(UUID blueprintId) {
        this.put(BlueprintContextKeys.BLUEPRINT_ID, (Object)blueprintId);
    }
}

