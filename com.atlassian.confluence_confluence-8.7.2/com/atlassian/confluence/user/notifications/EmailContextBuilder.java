/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.user.notifications;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.ConfluenceRenderUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class EmailContextBuilder {
    private final SettingsManager settingsManager;

    public EmailContextBuilder(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public Map<String, Serializable> getSystemContext() {
        Settings globalSettings = this.settingsManager.getGlobalSettings();
        HashMap<String, Serializable> context = new HashMap<String, Serializable>();
        String domainName = globalSettings.getBaseUrl();
        if (StringUtils.isNotEmpty((CharSequence)domainName) && domainName.endsWith("/")) {
            domainName = domainName.substring(0, domainName.length() - 1);
        }
        context.put("baseurl", (Serializable)((Object)domainName));
        BootstrapManager bootstrapManager = (BootstrapManager)BootstrapUtils.getBootstrapManager();
        Object contextPath = bootstrapManager.getWebAppContextPath();
        if (StringUtils.isNotEmpty((CharSequence)contextPath) && !((String)contextPath).startsWith("/")) {
            contextPath = "/" + (String)contextPath;
        }
        context.put("contextPath", (Serializable)contextPath);
        context.put("stylesheet", (Serializable)((Object)ConfluenceRenderUtils.renderDefaultStylesheet()));
        String siteTitle = globalSettings.getSiteTitle();
        context.put("siteTitle", (Serializable)((Object)siteTitle));
        return context;
    }
}

