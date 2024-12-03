/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.application.refapp.RefAppApplicationType
 *  com.atlassian.applinks.api.application.refapp.RefAppCharlieEntityType
 *  com.atlassian.applinks.spi.application.NonAppLinksEntityType
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.plugin.util.Assertions
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.application.refapp;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.application.refapp.RefAppApplicationType;
import com.atlassian.applinks.api.application.refapp.RefAppCharlieEntityType;
import com.atlassian.applinks.application.BuiltinApplinksType;
import com.atlassian.applinks.application.HiResIconizedIdentifiableType;
import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.spi.application.NonAppLinksEntityType;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugin.util.Assertions;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import java.net.URI;
import javax.annotation.Nonnull;

public class RefAppCharlieEntityTypeImpl
extends HiResIconizedIdentifiableType
implements RefAppCharlieEntityType,
NonAppLinksEntityType,
BuiltinApplinksType {
    private static final TypeId TYPE_ID = new TypeId("refapp.charlie");

    public RefAppCharlieEntityTypeImpl(AppLinkPluginUtil pluginUtil, WebResourceUrlProvider webResourceUrlProvider) {
        super(pluginUtil, webResourceUrlProvider);
    }

    @Nonnull
    public TypeId getId() {
        return TYPE_ID;
    }

    public Class<? extends ApplicationType> getApplicationType() {
        return RefAppApplicationType.class;
    }

    public String getI18nKey() {
        return "applinks.refapp.charlie";
    }

    public String getPluralizedI18nKey() {
        return "applinks.refapp.charlie.plural";
    }

    public String getShortenedI18nKey() {
        return "applinks.refapp.charlie.short";
    }

    public URI getDisplayUrl(ApplicationLink link, String charlie) {
        Assertions.isTrue((String)String.format("Application link %s is not of type %s", link.getId(), this.getApplicationType().getName()), (boolean)(link.getType() instanceof RefAppApplicationType));
        return URIUtil.uncheckedConcatenate(link.getDisplayUrl(), "plugins", "servlet", "charlie", charlie);
    }
}

