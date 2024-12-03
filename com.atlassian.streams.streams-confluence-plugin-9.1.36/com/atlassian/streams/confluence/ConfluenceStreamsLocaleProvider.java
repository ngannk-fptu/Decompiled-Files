/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.languages.LocaleParser
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.streams.spi.StreamsLocaleProvider
 *  com.google.common.base.Preconditions
 */
package com.atlassian.streams.confluence;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.languages.LocaleParser;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.streams.spi.StreamsLocaleProvider;
import com.google.common.base.Preconditions;
import java.util.Locale;

public class ConfluenceStreamsLocaleProvider
implements StreamsLocaleProvider {
    private final LocaleManager localeManager;
    private final SettingsManager settingsManager;

    public ConfluenceStreamsLocaleProvider(LocaleManager localeManager, SettingsManager settingsManager) {
        this.localeManager = (LocaleManager)Preconditions.checkNotNull((Object)localeManager, (Object)"localeManager");
        this.settingsManager = (SettingsManager)Preconditions.checkNotNull((Object)settingsManager, (Object)"settingsManager");
    }

    public Locale getApplicationLocale() {
        return LocaleParser.toLocale((String)this.settingsManager.getGlobalSettings().getGlobalDefaultLocale());
    }

    public Locale getUserLocale() {
        return this.localeManager.getLocale(AuthenticatedUserThreadLocal.getUser());
    }
}

