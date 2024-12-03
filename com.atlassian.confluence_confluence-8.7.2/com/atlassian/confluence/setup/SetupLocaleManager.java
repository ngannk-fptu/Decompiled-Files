/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.languages.LocaleParser;
import com.atlassian.user.User;
import java.util.Locale;

public class SetupLocaleManager
implements LocaleManager {
    private ApplicationConfiguration applicationConfiguration;

    public SetupLocaleManager(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    @Override
    public Locale getSiteDefaultLocale() {
        String localeString = (String)this.applicationConfiguration.getProperty((Object)"confluence.setup.locale");
        if (localeString == null) {
            return LocaleManager.DEFAULT_LOCALE;
        }
        return LocaleParser.toLocale(localeString);
    }

    @Override
    public Locale getLocale(User user) {
        return this.getSiteDefaultLocale();
    }

    @Override
    public void setRequestLanguages(String languageString) {
        throw new UnsupportedOperationException("Setting languages is not supported in the setup context.");
    }

    @Override
    public void setLanguage(String selectedLanguage) {
        throw new UnsupportedOperationException("Setting languages is not supported in the setup context.");
    }
}

