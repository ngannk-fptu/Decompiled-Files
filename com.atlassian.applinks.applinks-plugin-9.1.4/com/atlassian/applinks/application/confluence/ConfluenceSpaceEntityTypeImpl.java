/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType
 *  com.atlassian.applinks.api.application.confluence.ConfluenceSpaceEntityType
 *  com.atlassian.applinks.spi.application.NonAppLinksEntityType
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.plugin.util.Assertions
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.application.confluence;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType;
import com.atlassian.applinks.api.application.confluence.ConfluenceSpaceEntityType;
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

public class ConfluenceSpaceEntityTypeImpl
extends HiResIconizedIdentifiableType
implements ConfluenceSpaceEntityType,
NonAppLinksEntityType,
BuiltinApplinksType {
    private static final TypeId TYPE_ID = new TypeId("confluence.space");

    public ConfluenceSpaceEntityTypeImpl(AppLinkPluginUtil pluginUtil, WebResourceUrlProvider webResourceUrlProvider) {
        super(pluginUtil, webResourceUrlProvider);
    }

    @Nonnull
    public TypeId getId() {
        return TYPE_ID;
    }

    public Class<? extends ApplicationType> getApplicationType() {
        return ConfluenceApplicationType.class;
    }

    public String getI18nKey() {
        return "applinks.confluence.space";
    }

    public String getPluralizedI18nKey() {
        return "applinks.confluence.space.plural";
    }

    public String getShortenedI18nKey() {
        return "applinks.confluence.space.short";
    }

    public URI getDisplayUrl(ApplicationLink link, String space) {
        Assertions.isTrue((String)String.format("Application link %s is not of type %s", link.getId(), this.getApplicationType().getName()), (boolean)(link.getType() instanceof ConfluenceApplicationType));
        return URIUtil.uncheckedConcatenate(link.getDisplayUrl(), "display", space);
    }
}

