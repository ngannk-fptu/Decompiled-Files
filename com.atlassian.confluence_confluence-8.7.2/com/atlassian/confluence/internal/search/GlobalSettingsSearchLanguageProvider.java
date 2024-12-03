/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.search;

import com.atlassian.confluence.internal.search.SearchLanguageProvider;
import com.atlassian.confluence.search.SearchLanguage;
import com.atlassian.confluence.setup.settings.SettingsManager;

class GlobalSettingsSearchLanguageProvider
implements SearchLanguageProvider {
    private final SearchLanguageProvider setting = () -> SearchLanguage.fromString(settingsManager.getGlobalSettings().getIndexingLanguage());

    public GlobalSettingsSearchLanguageProvider(SettingsManager settingsManager) {
    }

    @Override
    public SearchLanguage get() {
        return this.setting.get();
    }
}

