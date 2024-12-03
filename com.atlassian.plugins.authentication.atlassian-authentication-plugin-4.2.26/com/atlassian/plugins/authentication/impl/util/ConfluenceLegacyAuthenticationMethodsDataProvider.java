/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.inject.Inject
 */
package com.atlassian.plugins.authentication.impl.util;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.util.LegacyAuthenticationMethodsDataProvider;
import javax.inject.Inject;

@ConfluenceComponent
public class ConfluenceLegacyAuthenticationMethodsDataProvider
implements LegacyAuthenticationMethodsDataProvider {
    private final SettingsManager settingsManager;

    @Inject
    public ConfluenceLegacyAuthenticationMethodsDataProvider(@ComponentImport SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public boolean hasLegacyAuthenticationMethodsConfigured() {
        return this.settingsManager.getGlobalSettings().isAllowRemoteApi();
    }
}

