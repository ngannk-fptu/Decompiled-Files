/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.google.common.base.Preconditions;
import java.util.Map;

public class IsPluginEnabledCondition
implements Condition {
    private PluginAccessor pluginAccessor;
    private String requiredPlugin;

    public void init(Map<String, String> params) throws PluginParseException {
        this.requiredPlugin = params.get("plugin-key");
        Preconditions.checkNotNull((Object)this.requiredPlugin);
    }

    public boolean shouldDisplay(Map<String, Object> stringObjectMap) {
        return this.pluginAccessor.isPluginEnabled(this.requiredPlugin);
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }
}

