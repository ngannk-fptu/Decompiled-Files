/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  io.atlassian.util.concurrent.ResettableLazyReference
 */
package com.atlassian.mail;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.mail.Settings;
import com.atlassian.mail.config.ConfigLoader;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.auth.AuthenticationContextFactory;
import io.atlassian.util.concurrent.ResettableLazyReference;

public class MailFactory {
    @Deprecated
    public static final String MAIL_DISABLED_KEY = "atlassian.mail.senddisabled";
    @TenantAware(value=TenancyScope.TENANTLESS, comment="This stores a value loaded from a file on disk that will always be the same in Vertigo")
    private static final ResettableLazyReference<ConfigLoader> configLoader = new ResettableLazyReference<ConfigLoader>(){

        protected ConfigLoader create() throws Exception {
            return ConfigLoader.getImmutableConfigurationLoader();
        }
    };

    public static void refresh() {
        configLoader.reset();
    }

    public static MailServerManager getServerManager() {
        return ((ConfigLoader)configLoader.get()).getLoadedManager();
    }

    public static Settings getSettings() {
        return ((ConfigLoader)configLoader.get()).getLoadedSettings();
    }

    public static AuthenticationContextFactory getAuthenticationContextFactory() {
        return ((ConfigLoader)configLoader.get()).getLoadedAuthContextFactory();
    }

    @Deprecated
    public static boolean isSendingDisabled() {
        return MailFactory.getSettings().isSendingDisabled();
    }
}

