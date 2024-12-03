/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.efi.store;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.efi.store.GlobalStorageService;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GlobalStorageServiceImpl
implements GlobalStorageService {
    public static final String NPS_KEY = "com.atlassian.nps.plugin.status.nps_enabled";
    private static final BandanaContext GLOBAL_DATA_CONTEXT = new ConfluenceBandanaContext(GlobalStorageServiceImpl.class.getName());
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalStorageServiceImpl.class);
    private final BandanaManager bandanaManager;
    private final PluginSettingsFactory pluginSettingsFactory;

    @Autowired
    public GlobalStorageServiceImpl(@ComponentImport BandanaManager bandanaManager, @ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this.bandanaManager = bandanaManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public String get(String key) {
        return (String)this.bandanaManager.getValue(GLOBAL_DATA_CONTEXT, "efi.store.onboarding." + key);
    }

    @Override
    public boolean set(String key, String value) {
        this.bandanaManager.setValue(GLOBAL_DATA_CONTEXT, "efi.store.onboarding." + key, (Object)value);
        return true;
    }

    @Override
    public void remove(String key) {
        this.bandanaManager.removeValue(GLOBAL_DATA_CONTEXT, "efi.store.onboarding." + key);
    }

    @Override
    public String getNpsEnabledSetting() {
        try {
            return (String)this.pluginSettingsFactory.createGlobalSettings().get(NPS_KEY);
        }
        catch (RuntimeException e) {
            LOGGER.warn("Couldn't check the NPS status. This can safely be ignored during plugin shutdown. Detail: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Set<String> getSet(String key) {
        return (Set)this.bandanaManager.getValue(GLOBAL_DATA_CONTEXT, "efi.store.onboarding." + key);
    }

    @Override
    public void set(String key, Set<String> value) {
        this.bandanaManager.setValue(GLOBAL_DATA_CONTEXT, "efi.store.onboarding." + key, value);
    }
}

