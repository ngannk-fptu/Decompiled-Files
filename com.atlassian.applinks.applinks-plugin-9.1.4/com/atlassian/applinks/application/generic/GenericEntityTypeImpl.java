/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.application.generic.GenericApplicationType
 *  com.atlassian.applinks.spi.application.NonAppLinksEntityType
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.plugin.util.Assertions
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.application.generic;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.application.generic.GenericApplicationType;
import com.atlassian.applinks.application.BuiltinApplinksType;
import com.atlassian.applinks.application.IconizedIdentifiableType;
import com.atlassian.applinks.application.generic.GenericApplicationTypeImpl;
import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.spi.application.NonAppLinksEntityType;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.plugin.util.Assertions;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import java.net.URI;
import javax.annotation.Nonnull;

public class GenericEntityTypeImpl
extends IconizedIdentifiableType
implements NonAppLinksEntityType,
BuiltinApplinksType {
    private static final TypeId TYPE_ID = new TypeId("generic.entity");

    public GenericEntityTypeImpl(AppLinkPluginUtil pluginUtil, WebResourceUrlProvider webResourceUrlProvider) {
        super(pluginUtil, webResourceUrlProvider);
    }

    @Nonnull
    public TypeId getId() {
        return TYPE_ID;
    }

    public Class<? extends ApplicationType> getApplicationType() {
        return GenericApplicationType.class;
    }

    public String getI18nKey() {
        return "applinks.generic.entity";
    }

    public String getPluralizedI18nKey() {
        return "applinks.generic.entity.plural";
    }

    public String getShortenedI18nKey() {
        return "applinks.generic.entity.short";
    }

    public URI getDisplayUrl(ApplicationLink link, String entity) {
        Assertions.isTrue((String)String.format("Application link %s is not of type %s", link.getId(), this.getApplicationType().getName()), (boolean)(link.getType() instanceof GenericApplicationTypeImpl));
        return null;
    }
}

