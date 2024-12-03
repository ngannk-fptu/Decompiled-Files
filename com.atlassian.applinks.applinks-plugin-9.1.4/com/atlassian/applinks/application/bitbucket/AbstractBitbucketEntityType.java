/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.application.bitbucket;

import com.atlassian.applinks.application.IconizedIdentifiableType;
import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.internal.feature.ApplinksFeatureService;
import com.atlassian.applinks.internal.feature.ApplinksFeatures;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import javax.annotation.Nonnull;

public abstract class AbstractBitbucketEntityType
extends IconizedIdentifiableType {
    private static final String I18N_KEY_TEMPLATE = "applinks.%s.project";
    private static final String I18N_PLURALIZED_KEY_TEMPLATE = "applinks.%s.project.plural";
    private static final String I18N__SHORT_KEY_TEMPLATE = "applinks.%s.project.short";
    private static final String BITBUCKET = "bitbucket";
    private static final String STASH = "stash";
    private final ApplinksFeatureService applinksFeatureService;

    public AbstractBitbucketEntityType(AppLinkPluginUtil pluginUtil, WebResourceUrlProvider webResourceUrlProvider, ApplinksFeatureService applinksFeatureService) {
        super(pluginUtil, webResourceUrlProvider);
        this.applinksFeatureService = applinksFeatureService;
    }

    public final String getI18nKey() {
        return String.format(I18N_KEY_TEMPLATE, this.getKey());
    }

    public final String getPluralizedI18nKey() {
        return String.format(I18N_PLURALIZED_KEY_TEMPLATE, this.getKey());
    }

    public final String getShortenedI18nKey() {
        return String.format(I18N__SHORT_KEY_TEMPLATE, this.getKey());
    }

    @Override
    @Nonnull
    protected String getIconKey() {
        return super.getIconKey();
    }

    private String getKey() {
        return this.isRebrandEnabled() ? BITBUCKET : STASH;
    }

    private boolean isRebrandEnabled() {
        return this.applinksFeatureService.isEnabled(ApplinksFeatures.BITBUCKET_REBRAND);
    }
}

