/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 */
package com.atlassian.confluence.plugins.gadgets.refimpl;

import com.atlassian.confluence.plugins.gadgets.refimpl.SubscribedGadgetFeedIdGenerator;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class SubscribedGadgetFeedIdGeneratorImpl
implements SubscribedGadgetFeedIdGenerator {
    private static final String PLUGIN_SETTINGS_KEY = SubscribedGadgetFeedIdGenerator.class.getName() + ":";
    private static final String NEXT_GADGET_ID_KEY = PLUGIN_SETTINGS_KEY + "nextId";
    private static final int START_GADGET_ID = 1000;
    private final PluginSettings pluginSettings;

    public SubscribedGadgetFeedIdGeneratorImpl(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
    }

    @Override
    public String newSubscribedGadgetFeedId() {
        String value = (String)this.pluginSettings.get(NEXT_GADGET_ID_KEY);
        int nextGadgetSpecId = value == null ? 1000 : Integer.parseInt(value);
        this.pluginSettings.put(NEXT_GADGET_ID_KEY, (Object)Integer.toString(nextGadgetSpecId + 1));
        return Integer.toString(nextGadgetSpecId);
    }
}

