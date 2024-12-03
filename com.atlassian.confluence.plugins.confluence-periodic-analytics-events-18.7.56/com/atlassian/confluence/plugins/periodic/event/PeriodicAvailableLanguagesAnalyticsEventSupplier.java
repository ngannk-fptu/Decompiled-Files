/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.Language
 *  com.atlassian.confluence.languages.LanguageManager
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEventSupplier
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.periodic.event;

import com.atlassian.confluence.languages.Language;
import com.atlassian.confluence.languages.LanguageManager;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEventSupplier;
import com.atlassian.confluence.plugins.periodic.event.PeriodicAvailableLanguagesAnalyticsEvent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PeriodicAvailableLanguagesAnalyticsEventSupplier
implements PeriodicEventSupplier {
    private final LanguageManager languageManager;

    @Autowired
    PeriodicAvailableLanguagesAnalyticsEventSupplier(@ComponentImport LanguageManager languageManager) {
        this.languageManager = Objects.requireNonNull(languageManager);
    }

    public PeriodicEvent call() throws Exception {
        Set<String> languages = this.languageManager.getLanguages().stream().map(Language::getName).collect(Collectors.toSet());
        return new PeriodicAvailableLanguagesAnalyticsEvent(languages);
    }
}

