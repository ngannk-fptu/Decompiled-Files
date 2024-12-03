/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.confluence.impl.health.web;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.google.common.annotations.VisibleForTesting;
import java.util.Arrays;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class JohnsonPageI18NProvider {
    @VisibleForTesting
    static final String[] I18N_KEYS = new String[]{"johnson.check.acknowledge.warnings.cta", "johnson.check.events.event.kblink", "johnson.check.events.show.all", "johnson.check.events.show.less", "johnson.legacy.columnheading.description", "johnson.legacy.columnheading.exception", "johnson.legacy.columnheading.level", "johnson.legacy.columnheading.time", "johnson.page.detail.restricted.intro", "johnson.page.detail.restricted.footer", "johnson.page.footer", "johnson.page.headline.title.error", "johnson.page.headline.description.error", "johnson.page.headline.neutral", "johnson.page.headline.title.warning", "johnson.page.headline.description.warning", "johnson.page.headline.description.dismissible", "johnson.page.progress", "johnson.page.subheadline.error", "johnson.page.subheadline.neutral", "johnson.warnings.acknowledge.error.no.perm", "johnson.warnings.ignore.cli", "system.error.progress.completed"};
    private final I18NBeanFactory i18NBeanFactory;

    public JohnsonPageI18NProvider(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = Objects.requireNonNull(i18NBeanFactory);
    }

    public Json getTranslations() {
        JsonObject translations = new JsonObject();
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        Arrays.stream(I18N_KEYS).forEach(key -> translations.setProperty((String)key, i18NBean.getText((String)key)));
        return translations;
    }
}

