/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.setup.settings.init;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.setup.settings.init.AdminUiProperties;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Set;

public class ConfluenceAdminUiProperties
implements AdminUiProperties {
    public static final String ALLOW_DAILY_BACKUP_KEY = "admin.ui.allow.daily.backup.custom.location";
    public static final String ALLOW_SITE_SUPPORT_EMAIL_KEY = "admin.ui.allow.site.support.email";
    public static final String ALLOW_MANUAL_BACKUP_DOWNLOAD_KEY = "admin.ui.allow.manual.backup.download";
    private ApplicationConfiguration config;
    public static final Set<String> ADMIN_UI_KEYS = ImmutableSet.of((Object)"admin.ui.allow.daily.backup.custom.location", (Object)"admin.ui.allow.site.support.email", (Object)"admin.ui.allow.manual.backup.download");

    public ConfluenceAdminUiProperties(ApplicationConfiguration config) throws IOException {
        this.config = config;
    }

    @Override
    public boolean isAllowed(String key) {
        return this.config.getBooleanProperty((Object)key);
    }

    public static void initAdminUiProperties(ApplicationConfiguration config) {
        for (String key : ADMIN_UI_KEYS) {
            config.setProperty((Object)key, false);
        }
    }
}

