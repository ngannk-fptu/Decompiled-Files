/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaManager
 */
package com.atlassian.confluence.security;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.security.DefaultSpacePermissionDefaultsStore;
import com.atlassian.confluence.security.SpacePermissionDefaultsStore;
import com.atlassian.confluence.security.SpacePermissionDefaultsStoreFactory;
import com.atlassian.confluence.setup.settings.SettingsManager;

public class DefaultSpacePermissionDefaultsStoreFactory
implements SpacePermissionDefaultsStoreFactory {
    private final BandanaManager bandanaManager;
    private final SettingsManager settingsManager;

    public DefaultSpacePermissionDefaultsStoreFactory(BandanaManager bandanaManager, SettingsManager settingsManager) {
        this.bandanaManager = bandanaManager;
        this.settingsManager = settingsManager;
    }

    @Override
    public SpacePermissionDefaultsStore createStore() {
        return new DefaultSpacePermissionDefaultsStore(this.settingsManager, this.bandanaManager);
    }
}

