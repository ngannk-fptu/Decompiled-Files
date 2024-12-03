/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationTypeVisitor
 *  com.atlassian.applinks.api.application.bitbucket.BitbucketApplicationType
 *  com.atlassian.applinks.api.application.stash.StashApplicationType
 *  com.atlassian.applinks.spi.application.NonAppLinksApplicationType
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.application.bitbucket;

import com.atlassian.applinks.api.ApplicationTypeVisitor;
import com.atlassian.applinks.api.application.bitbucket.BitbucketApplicationType;
import com.atlassian.applinks.api.application.stash.StashApplicationType;
import com.atlassian.applinks.application.BuiltinApplinksType;
import com.atlassian.applinks.application.HiResIconizedIdentifiableType;
import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.internal.feature.ApplinksFeatureService;
import com.atlassian.applinks.internal.feature.ApplinksFeatures;
import com.atlassian.applinks.spi.application.NonAppLinksApplicationType;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BitbucketApplicationTypeImpl
extends HiResIconizedIdentifiableType
implements BitbucketApplicationType,
StashApplicationType,
NonAppLinksApplicationType,
BuiltinApplinksType {
    static final TypeId TYPE_ID = new TypeId("stash");
    private final ApplinksFeatureService applinksFeatureService;

    public BitbucketApplicationTypeImpl(AppLinkPluginUtil pluginUtil, WebResourceUrlProvider webResourceUrlProvider, ApplinksFeatureService applinksFeatureService) {
        super(pluginUtil, webResourceUrlProvider);
        this.applinksFeatureService = applinksFeatureService;
    }

    @Nonnull
    public String getI18nKey() {
        return this.isRebrandEnabled() ? (this.isV4Enabled() ? "applinks.bitbucket.v4" : "applinks.bitbucket") : "applinks.stash";
    }

    @Nullable
    public <T> T accept(@Nonnull ApplicationTypeVisitor<T> visitor) {
        return (T)visitor.visit((BitbucketApplicationType)this);
    }

    @Nonnull
    public TypeId getId() {
        return TYPE_ID;
    }

    @Override
    @Nonnull
    protected String getIconKey() {
        return this.isRebrandEnabled() ? "bitbucket" : this.getId().get();
    }

    private boolean isRebrandEnabled() {
        return this.applinksFeatureService.isEnabled(ApplinksFeatures.BITBUCKET_REBRAND);
    }

    private boolean isV4Enabled() {
        return this.applinksFeatureService.isEnabled(ApplinksFeatures.V4_UI);
    }
}

