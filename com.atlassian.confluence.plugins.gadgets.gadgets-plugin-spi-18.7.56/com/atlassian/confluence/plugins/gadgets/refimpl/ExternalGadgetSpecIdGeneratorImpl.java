/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 */
package com.atlassian.confluence.plugins.gadgets.refimpl;

import com.atlassian.confluence.plugins.gadgets.refimpl.ExternalGadgetSpecIdGenerator;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class ExternalGadgetSpecIdGeneratorImpl
implements ExternalGadgetSpecIdGenerator {
    private static final String PLUGIN_SETTINGS_KEY = ExternalGadgetSpecIdGenerator.class.getName() + ":";
    private static final String NEXT_GADGET_ID_KEY = PLUGIN_SETTINGS_KEY + "nextId";
    private static final int START_GADGET_ID = 1000;
    private final PluginSettings pluginSettings;

    public ExternalGadgetSpecIdGeneratorImpl(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
    }

    @Override
    public ExternalGadgetSpecId newExternalGadgetSpecId() {
        String value = (String)this.pluginSettings.get(NEXT_GADGET_ID_KEY);
        int nextGadgetSpecId = value == null ? 1000 : Integer.parseInt(value);
        this.pluginSettings.put(NEXT_GADGET_ID_KEY, (Object)Integer.toString(nextGadgetSpecId + 1));
        return ExternalGadgetSpecId.valueOf((String)Integer.toString(nextGadgetSpecId));
    }
}

