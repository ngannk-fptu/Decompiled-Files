/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.jsonorg.JSONException
 *  com.atlassian.json.jsonorg.JSONObject
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.json.marshal.Jsonable$JsonMappingException
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.plugin.StoredPluginState
 *  com.atlassian.plugin.StoredPluginStateAccessor
 *  com.atlassian.plugin.manager.SafeModeManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  javax.inject.Inject
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugins.pulp.wrm;

import com.atlassian.confluence.plugins.pulp.wrm.IsUserInstalledPlugin;
import com.atlassian.json.jsonorg.JSONException;
import com.atlassian.json.jsonorg.JSONObject;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugin.StoredPluginState;
import com.atlassian.plugin.StoredPluginStateAccessor;
import com.atlassian.plugin.manager.SafeModeManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.checkerframework.checker.nullness.qual.NonNull;

public class PluginStatusWrmDataProvider
implements WebResourceDataProvider {
    private static final String DISABLED_USER_PLUGINS_NAMES = "disabledPluginsNames";
    private static final String FAILED_USER_PLUGINS_NAMES = "failedPluginsNames";
    private static final String SAFEMODE_DISABLED_USER_PLUGINS_NAMES = "safeModeDisabledPluginsNames";
    private final IsUserInstalledPlugin isUserInstalledPlugin;
    private final StoredPluginStateAccessor storedPluginStateAccessor;
    private final PluginAccessor pluginAccessor;
    private final SafeModeManager safeModeManager;

    @Inject
    public PluginStatusWrmDataProvider(@ComponentImport PluginAccessor pluginAccessor, @ComponentImport StoredPluginStateAccessor storedPluginStateAccessor, IsUserInstalledPlugin isUserInstalledPlugin, @ComponentImport SafeModeManager safeModeManager) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
        this.storedPluginStateAccessor = Objects.requireNonNull(storedPluginStateAccessor);
        this.isUserInstalledPlugin = Objects.requireNonNull(isUserInstalledPlugin);
        this.safeModeManager = Objects.requireNonNull(safeModeManager);
    }

    public @NonNull Jsonable get() {
        return writer -> {
            try {
                this.getBodyContentJsonObject().write(writer);
            }
            catch (JSONException e) {
                throw new Jsonable.JsonMappingException((Throwable)e);
            }
        };
    }

    private JSONObject getBodyContentJsonObject() {
        Collection userInstalledPlugins = this.pluginAccessor.getPlugins((Predicate)this.isUserInstalledPlugin);
        if (userInstalledPlugins.isEmpty()) {
            return new JSONObject();
        }
        return this.getPluginStatus(userInstalledPlugins);
    }

    private JSONObject getPluginStatus(Collection<Plugin> userInstalledPlugins) {
        ArrayList<Plugin> manuallyDisabledPlugins = new ArrayList<Plugin>();
        ArrayList<Plugin> safeModeDisabledPlugins = new ArrayList<Plugin>();
        ArrayList<Plugin> failedPlugins = new ArrayList<Plugin>();
        for (Plugin p : userInstalledPlugins) {
            if (!this.isNotEnabled(p)) continue;
            if (this.isNotManuallyDisabled(p)) {
                if (this.isPluginDisabledBySafeMode(p)) {
                    safeModeDisabledPlugins.add(p);
                    continue;
                }
                failedPlugins.add(p);
                continue;
            }
            manuallyDisabledPlugins.add(p);
        }
        JSONObject pluginStatus = new JSONObject();
        try {
            int disabledPluginsCount = manuallyDisabledPlugins.size() + safeModeDisabledPlugins.size();
            if (disabledPluginsCount != 0) {
                pluginStatus.put(DISABLED_USER_PLUGINS_NAMES, this.getPluginNames(manuallyDisabledPlugins));
                pluginStatus.put(SAFEMODE_DISABLED_USER_PLUGINS_NAMES, this.getPluginNames(safeModeDisabledPlugins));
            }
            if (userInstalledPlugins.size() - disabledPluginsCount > 0) {
                pluginStatus.put(FAILED_USER_PLUGINS_NAMES, this.getPluginNames(failedPlugins));
            }
            return pluginStatus;
        }
        catch (JSONException e) {
            throw new IllegalStateException(e);
        }
    }

    private boolean isNotEnabled(Plugin plugin) {
        return plugin.getPluginState() != PluginState.ENABLED;
    }

    private List<String> getPluginNames(List<Plugin> plugins) {
        return plugins.stream().map(Plugin::getName).collect(Collectors.toList());
    }

    private boolean isNotManuallyDisabled(Plugin plugin) {
        StoredPluginState storedPluginState = this.storedPluginStateAccessor.get();
        return storedPluginState.isEnabled(plugin);
    }

    private boolean isPluginDisabledBySafeMode(Plugin plugin) {
        return !this.safeModeManager.pluginShouldBeStarted(plugin, Collections.emptyList());
    }
}

