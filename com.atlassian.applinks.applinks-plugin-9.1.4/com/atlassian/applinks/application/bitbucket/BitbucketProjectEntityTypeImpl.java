/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.application.bitbucket.BitbucketApplicationType
 *  com.atlassian.applinks.api.application.bitbucket.BitbucketProjectEntityType
 *  com.atlassian.applinks.api.application.stash.StashProjectEntityType
 *  com.atlassian.applinks.spi.application.NonAppLinksEntityType
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.plugin.util.Assertions
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.application.bitbucket;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.application.bitbucket.BitbucketApplicationType;
import com.atlassian.applinks.api.application.bitbucket.BitbucketProjectEntityType;
import com.atlassian.applinks.api.application.stash.StashProjectEntityType;
import com.atlassian.applinks.application.BuiltinApplinksType;
import com.atlassian.applinks.application.HiResIconizedIdentifiableType;
import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.internal.feature.ApplinksFeatureService;
import com.atlassian.applinks.internal.feature.ApplinksFeatures;
import com.atlassian.applinks.spi.application.NonAppLinksEntityType;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugin.util.Assertions;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import java.net.URI;
import javax.annotation.Nonnull;

public final class BitbucketProjectEntityTypeImpl
extends HiResIconizedIdentifiableType
implements BitbucketProjectEntityType,
StashProjectEntityType,
NonAppLinksEntityType,
BuiltinApplinksType {
    private static final String ID_TEMPLATE = "%s.project";
    private static final String I18N_KEY_TEMPLATE = "applinks.%s.project";
    private static final String I18N_PLURALIZED_KEY_TEMPLATE = "applinks.%s.project.plural";
    private static final String I18N__SHORT_KEY_TEMPLATE = "applinks.%s.project.short";
    private static final String TYPE_BITBUCKET = "bitbucket";
    private static final String TYPE_STASH = "stash";
    private final ApplinksFeatureService applinksFeatureService;
    private static final TypeId TYPE_ID = new TypeId(BitbucketProjectEntityTypeImpl.formatId("stash"));

    public BitbucketProjectEntityTypeImpl(AppLinkPluginUtil pluginUtil, WebResourceUrlProvider webResourceUrlProvider, ApplinksFeatureService applinksFeatureService) {
        super(pluginUtil, webResourceUrlProvider);
        this.applinksFeatureService = applinksFeatureService;
    }

    @Nonnull
    public TypeId getId() {
        return TYPE_ID;
    }

    @Nonnull
    public String getI18nKey() {
        return String.format(I18N_KEY_TEMPLATE, this.getType());
    }

    public String getPluralizedI18nKey() {
        return String.format(I18N_PLURALIZED_KEY_TEMPLATE, this.getType());
    }

    public String getShortenedI18nKey() {
        return String.format(I18N__SHORT_KEY_TEMPLATE, this.getType());
    }

    @Override
    @Nonnull
    protected String getIconKey() {
        return BitbucketProjectEntityTypeImpl.formatId(this.getType());
    }

    public Class<? extends ApplicationType> getApplicationType() {
        return BitbucketApplicationType.class;
    }

    public URI getDisplayUrl(ApplicationLink link, String project) {
        Assertions.isTrue((String)String.format("Application link %s is not of type %s", link.getId(), this.getApplicationType().getName()), (boolean)(link.getType() instanceof BitbucketApplicationType));
        return URIUtil.uncheckedConcatenate(link.getDisplayUrl(), "projects", project);
    }

    private static String formatId(String type) {
        return String.format(ID_TEMPLATE, type);
    }

    private String getType() {
        return this.isRebrandEnabled() ? TYPE_BITBUCKET : TYPE_STASH;
    }

    private boolean isRebrandEnabled() {
        return this.applinksFeatureService.isEnabled(ApplinksFeatures.BITBUCKET_REBRAND);
    }
}

