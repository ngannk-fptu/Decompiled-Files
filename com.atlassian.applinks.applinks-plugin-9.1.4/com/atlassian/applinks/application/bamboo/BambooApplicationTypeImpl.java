/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationTypeVisitor
 *  com.atlassian.applinks.api.application.bamboo.BambooApplicationType
 *  com.atlassian.applinks.spi.application.NonAppLinksApplicationType
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.application.bamboo;

import com.atlassian.applinks.api.ApplicationTypeVisitor;
import com.atlassian.applinks.api.application.bamboo.BambooApplicationType;
import com.atlassian.applinks.application.BuiltinApplinksType;
import com.atlassian.applinks.application.HiResIconizedIdentifiableType;
import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.spi.application.NonAppLinksApplicationType;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BambooApplicationTypeImpl
extends HiResIconizedIdentifiableType
implements BambooApplicationType,
NonAppLinksApplicationType,
BuiltinApplinksType {
    static final TypeId TYPE_ID = new TypeId("bamboo");

    public BambooApplicationTypeImpl(AppLinkPluginUtil pluginUtil, WebResourceUrlProvider webResourceUrlProvider) {
        super(pluginUtil, webResourceUrlProvider);
    }

    @Nonnull
    public TypeId getId() {
        return TYPE_ID;
    }

    @Nonnull
    public String getI18nKey() {
        return "applinks.bamboo";
    }

    @Nullable
    public <T> T accept(@Nonnull ApplicationTypeVisitor<T> visitor) {
        return (T)visitor.visit((BambooApplicationType)this);
    }
}

