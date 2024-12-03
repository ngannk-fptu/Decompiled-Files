/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationTypeVisitor
 *  com.atlassian.applinks.api.application.crowd.CrowdApplicationType
 *  com.atlassian.applinks.spi.application.NonAppLinksApplicationType
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.application.crowd;

import com.atlassian.applinks.api.ApplicationTypeVisitor;
import com.atlassian.applinks.api.application.crowd.CrowdApplicationType;
import com.atlassian.applinks.application.BuiltinApplinksType;
import com.atlassian.applinks.application.HiResIconizedIdentifiableType;
import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.spi.application.NonAppLinksApplicationType;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CrowdApplicationTypeImpl
extends HiResIconizedIdentifiableType
implements CrowdApplicationType,
NonAppLinksApplicationType,
BuiltinApplinksType {
    public static final TypeId TYPE_ID = new TypeId("crowd");

    public CrowdApplicationTypeImpl(AppLinkPluginUtil pluginUtil, WebResourceUrlProvider webResourceUrlProvider) {
        super(pluginUtil, webResourceUrlProvider);
    }

    @Nonnull
    public String getI18nKey() {
        return "applinks.crowd";
    }

    @Nullable
    public <T> T accept(@Nonnull ApplicationTypeVisitor<T> visitor) {
        return (T)visitor.visit((CrowdApplicationType)this);
    }

    @Nonnull
    public TypeId getId() {
        return TYPE_ID;
    }
}

