/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.custom_apps;

import com.atlassian.plugins.custom_apps.CustomAppStore;
import com.atlassian.plugins.custom_apps.api.CustomApp;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginSettingsBasedCustomAppStore
implements CustomAppStore {
    private static final Logger log = LoggerFactory.getLogger(PluginSettingsBasedCustomAppStore.class);
    private static final String KEY = "com.atlassian.plugins.custom_apps.";
    private static final String CUSTOM_APPS_AS_JSON = "customAppsAsJSON";
    private static final String HAS_CUSTOM_ORDER = "hasCustomOrder";
    private final PluginSettingsFactory pluginSettingsFactory;

    public PluginSettingsBasedCustomAppStore(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public List<CustomApp> getAll() {
        return this.jsonStringToList((String)this.settings().get(CUSTOM_APPS_AS_JSON));
    }

    @Override
    public void storeAll(List<CustomApp> apps) {
        this.settings().put(CUSTOM_APPS_AS_JSON, (Object)this.listToJsonString(apps));
    }

    @Override
    public boolean isCustomOrder() {
        return "true".equals(this.settings().get(HAS_CUSTOM_ORDER));
    }

    @Override
    public void setCustomOrder() {
        this.settings().put(HAS_CUSTOM_ORDER, (Object)"true");
    }

    private PluginSettings settings() {
        return new PluginSettings(){
            private PluginSettings pluginSettings;
            {
                this.pluginSettings = PluginSettingsBasedCustomAppStore.this.pluginSettingsFactory.createGlobalSettings();
            }

            public Object get(String key) {
                return this.pluginSettings.get(PluginSettingsBasedCustomAppStore.KEY + key);
            }

            public Object put(String key, Object value) {
                return this.pluginSettings.put(PluginSettingsBasedCustomAppStore.KEY + key, value);
            }

            public Object remove(String key) {
                return this.pluginSettings.remove(PluginSettingsBasedCustomAppStore.KEY + key);
            }
        };
    }

    private List<CustomApp> jsonStringToList(String jsonString) {
        ArrayList<CustomApp> customApps = new ArrayList<CustomApp>();
        if (jsonString != null) {
            try {
                JSONArray json = new JSONArray(jsonString);
                for (int i = 0; i < json.length(); ++i) {
                    CustomApp c = this.jsonObjectToCustomApp(json.getJSONObject(i));
                    if (c == null) continue;
                    customApps.add(c);
                }
            }
            catch (JSONException e) {
                log.error("Error decoding custom apps JSON representation: '" + jsonString + "'.", (Throwable)e);
            }
        }
        return customApps;
    }

    private CustomApp jsonObjectToCustomApp(JSONObject o) throws JSONException {
        String id = o.getString("id");
        String displayName = o.getString("displayName");
        String url = o.getString("url");
        String sourceApplicationUrl = this.getString(o, "baseUrl");
        String sourceApplicationName = this.getString(o, "applicationName");
        String sourceApplicationType = this.getString(o, "applicationType");
        boolean hide = this.getBoolean(o, "hide");
        boolean editable = this.getBoolean(o, "editable");
        List<String> allowedGroups = this.getStringArray(o, "allowedGroups");
        boolean self = this.getBoolean(o, "self");
        if (id != null && displayName != null && url != null) {
            return new CustomApp(id, displayName, url, sourceApplicationUrl, sourceApplicationName, sourceApplicationType, hide, allowedGroups, editable, self);
        }
        return null;
    }

    private String getString(JSONObject o, String property) throws JSONException {
        if (o.has(property)) {
            return o.getString(property);
        }
        return null;
    }

    private boolean getBoolean(JSONObject o, String property) throws JSONException {
        if (o.has(property)) {
            return o.getBoolean(property);
        }
        return false;
    }

    private List<String> getStringArray(JSONObject o, String property) throws JSONException {
        ImmutableList.Builder builder = ImmutableList.builder();
        if (o.has(property)) {
            JSONArray a = o.getJSONArray(property);
            for (int i = 0; i < a.length(); ++i) {
                builder.add((Object)a.getString(i));
            }
        }
        return builder.build();
    }

    private String listToJsonString(List<CustomApp> apps) {
        if (apps == null) {
            return null;
        }
        JSONArray json = new JSONArray();
        for (CustomApp app : apps) {
            JSONObject o = new JSONObject();
            try {
                o.put("id", (Object)app.getId());
                o.put("displayName", (Object)app.getDisplayName());
                o.put("url", (Object)app.getUrl());
                o.put("baseUrl", (Object)app.getSourceApplicationUrl());
                o.put("applicationName", (Object)app.getSourceApplicationName());
                o.put("applicationType", (Object)app.getSourceApplicationType());
                o.put("hide", app.getHide());
                o.put("allowedGroups", app.getAllowedGroups());
                o.put("editable", app.getEditable());
                o.put("self", app.isSelf());
                json.put((Object)o);
            }
            catch (JSONException e) {
                log.error("Error encoding custom app " + app, (Throwable)e);
            }
        }
        return json.toString();
    }
}

