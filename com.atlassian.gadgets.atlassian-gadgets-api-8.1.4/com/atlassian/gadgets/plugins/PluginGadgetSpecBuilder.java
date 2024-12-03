/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.gadgets.plugins;

import com.atlassian.gadgets.plugins.PluginGadgetSpec;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class PluginGadgetSpecBuilder {
    private Condition enabledCondition;
    private Condition localCondition;
    private String location;
    private String moduleKey;
    private Map<String, String> params;
    private Plugin plugin;
    private String publishLocation;

    public PluginGadgetSpec build() {
        return new PluginGadgetSpec(this.enabledCondition, this.localCondition, this.location, this.moduleKey, this.params, this.plugin, this.publishLocation);
    }

    public PluginGadgetSpecBuilder enabledCondition(Condition condition) {
        this.enabledCondition = condition;
        return this;
    }

    public PluginGadgetSpecBuilder localCondition(Condition condition) {
        this.localCondition = condition;
        return this;
    }

    public PluginGadgetSpecBuilder location(String location) {
        this.location = location;
        return this;
    }

    public PluginGadgetSpecBuilder moduleKey(String moduleKey) {
        this.moduleKey = moduleKey;
        return this;
    }

    public PluginGadgetSpecBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public PluginGadgetSpecBuilder plugin(Plugin plugin) {
        this.plugin = plugin;
        return this;
    }

    public PluginGadgetSpecBuilder publishLocation(String publishPath) {
        this.publishLocation = publishPath;
        return this;
    }
}

