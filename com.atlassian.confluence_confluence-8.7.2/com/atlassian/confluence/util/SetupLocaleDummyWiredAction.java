/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 */
package com.atlassian.confluence.util;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.languages.LocaleParser;
import com.atlassian.confluence.setup.SetupContext;
import java.util.Locale;

final class SetupLocaleDummyWiredAction
extends ConfluenceActionSupport {
    SetupLocaleDummyWiredAction() {
    }

    @Override
    public Locale getLocale() {
        ApplicationConfiguration applicationConfig = (ApplicationConfiguration)SetupContext.get().getBean(ApplicationConfiguration.class);
        String localeString = (String)applicationConfig.getProperty((Object)"confluence.setup.locale");
        if (localeString != null) {
            return LocaleParser.toLocale(localeString);
        }
        return LocaleManager.DEFAULT_LOCALE;
    }
}

