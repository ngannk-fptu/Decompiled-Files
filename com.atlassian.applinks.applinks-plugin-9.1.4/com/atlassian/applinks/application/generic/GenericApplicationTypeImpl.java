/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationTypeVisitor
 *  com.atlassian.applinks.api.application.generic.GenericApplicationType
 *  com.atlassian.applinks.spi.application.NonAppLinksApplicationType
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.application.generic;

import com.atlassian.applinks.api.ApplicationTypeVisitor;
import com.atlassian.applinks.api.application.generic.GenericApplicationType;
import com.atlassian.applinks.application.BuiltinApplinksType;
import com.atlassian.applinks.application.HiResIconizedIdentifiableType;
import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.spi.application.NonAppLinksApplicationType;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GenericApplicationTypeImpl
extends HiResIconizedIdentifiableType
implements GenericApplicationType,
NonAppLinksApplicationType,
BuiltinApplinksType {
    public static final TypeId TYPE_ID = new TypeId("generic");

    public GenericApplicationTypeImpl(AppLinkPluginUtil pluginUtil, WebResourceUrlProvider webResourceUrlProvider) {
        super(pluginUtil, webResourceUrlProvider);
    }

    @Nonnull
    public TypeId getId() {
        return TYPE_ID;
    }

    @Nonnull
    public String getI18nKey() {
        return "applinks.generic";
    }

    @Nullable
    public <T> T accept(@Nonnull ApplicationTypeVisitor<T> visitor) {
        return (T)visitor.visit((GenericApplicationType)this);
    }
}

