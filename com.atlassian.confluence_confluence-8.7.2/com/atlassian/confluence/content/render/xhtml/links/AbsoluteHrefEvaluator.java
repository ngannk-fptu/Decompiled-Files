/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.links;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.setup.settings.SettingsManager;

public class AbsoluteHrefEvaluator
implements HrefEvaluator {
    private final HrefEvaluator defaulHrefEvaluator;
    private final SettingsManager settingsManager;
    private final ContextPathHolder contextPathHolder;

    public AbsoluteHrefEvaluator(HrefEvaluator defaulHrefEvaluator, SettingsManager settingsManager, ContextPathHolder contextPathHolder) {
        this.defaulHrefEvaluator = defaulHrefEvaluator;
        this.settingsManager = settingsManager;
        this.contextPathHolder = contextPathHolder;
    }

    @Override
    public String createHref(ConversionContext context, Object object, String anchor) {
        String defaultHref = this.defaulHrefEvaluator.createHref(context, object, anchor);
        if (defaultHref != null && !defaultHref.startsWith("/")) {
            return defaultHref;
        }
        return this.getBaseUrl() + defaultHref;
    }

    private String getBaseUrl() {
        String contextPath;
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        if (baseUrl.endsWith(contextPath = this.contextPathHolder.getContextPath())) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - contextPath.length());
        }
        return baseUrl;
    }
}

