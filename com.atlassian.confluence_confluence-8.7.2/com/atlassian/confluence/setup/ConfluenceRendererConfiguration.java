/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RendererConfiguration
 */
package com.atlassian.confluence.setup;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.renderer.RendererConfiguration;

public class ConfluenceRendererConfiguration
implements RendererConfiguration {
    private final BootstrapManager bootstrapManager;
    private final SettingsManager settingsManager;

    public ConfluenceRendererConfiguration(BootstrapManager bootstrapManager, SettingsManager settingsManager) {
        this.bootstrapManager = bootstrapManager;
        this.settingsManager = settingsManager;
    }

    public String getWebAppContextPath() {
        return this.bootstrapManager.getWebAppContextPath();
    }

    public boolean isNofollowExternalLinks() {
        return this.settingsManager.getGlobalSettings().isNofollowExternalLinks();
    }

    public boolean isAllowCamelCase() {
        return this.settingsManager.getGlobalSettings().isAllowCamelCase();
    }

    public String getCharacterEncoding() {
        return GeneralUtil.getCharacterEncoding();
    }
}

