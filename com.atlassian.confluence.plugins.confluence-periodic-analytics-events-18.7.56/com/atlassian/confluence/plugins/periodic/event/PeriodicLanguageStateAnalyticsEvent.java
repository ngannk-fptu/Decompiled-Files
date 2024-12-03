/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.languages.Language
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent
 */
package com.atlassian.confluence.plugins.periodic.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.languages.Language;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent;
import java.util.Objects;

@EventName(value="confluence.periodic.analytics.language.state")
public class PeriodicLanguageStateAnalyticsEvent
implements PeriodicEvent {
    private final Language globalDefaultLanguage;
    private final boolean browserLanguageEnabled;

    PeriodicLanguageStateAnalyticsEvent(Language globalDefaultLanguage, boolean browserLanguageEnabled) {
        this.globalDefaultLanguage = Objects.requireNonNull(globalDefaultLanguage);
        this.browserLanguageEnabled = Objects.requireNonNull(browserLanguageEnabled);
    }

    public boolean getBrowserLanguageEnabled() {
        return this.browserLanguageEnabled;
    }

    public String getGlobalDefaultLanguage() {
        return this.globalDefaultLanguage.getLanguage();
    }
}

