/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.model.WebLabel
 *  com.atlassian.plugin.web.model.WebLink
 *  com.atlassian.plugins.navlink.producer.contentlinks.customcontentlink.CustomContentLink
 *  com.google.common.base.Function
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugins.navlink.producer.contentlinks.rest;

import com.atlassian.plugin.web.model.WebLabel;
import com.atlassian.plugin.web.model.WebLink;
import com.atlassian.plugins.navlink.producer.contentlinks.customcontentlink.CustomContentLink;
import com.atlassian.plugins.navlink.producer.contentlinks.plugin.ContentLinkModuleDescriptor;
import com.atlassian.plugins.navlink.producer.contentlinks.rest.ContentLinkEntity;
import com.atlassian.plugins.navlink.util.url.UrlFactory;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

public class ContentLinkEntityFactory {
    private UrlFactory urlFactory;
    private Map<String, Object> context;
    private HttpServletRequest request;

    public ContentLinkEntityFactory(HttpServletRequest request, Map<String, Object> context, UrlFactory urlFactory) {
        this.urlFactory = urlFactory;
        this.request = request;
        this.context = context;
    }

    public List<ContentLinkEntity> create(List<ContentLinkModuleDescriptor> moduleDescriptors) {
        return Lists.transform(moduleDescriptors, (Function)new Function<ContentLinkModuleDescriptor, ContentLinkEntity>(){

            public ContentLinkEntity apply(@Nullable ContentLinkModuleDescriptor input) {
                return ContentLinkEntityFactory.this.create(input);
            }
        });
    }

    public ContentLinkEntity create(ContentLinkModuleDescriptor contentLinkModuleDescriptor) {
        if (contentLinkModuleDescriptor != null) {
            String label = "";
            WebLabel webLabel = contentLinkModuleDescriptor.getWebLabel();
            if (webLabel != null) {
                label = webLabel.getDisplayableLabel(this.request, this.context);
            }
            String absoluteUrlLink = "";
            WebLink webLink = contentLinkModuleDescriptor.getLink();
            if (webLink != null) {
                absoluteUrlLink = this.urlFactory.toAbsoluteUrl(webLink.getRenderedUrl(this.context));
            }
            String tooltip = "";
            WebLabel tooltipLabel = contentLinkModuleDescriptor.getTooltip();
            if (tooltipLabel != null) {
                tooltip = tooltipLabel.getDisplayableLabel(this.request, this.context);
            }
            return new ContentLinkEntity(absoluteUrlLink, label, tooltip, false);
        }
        return null;
    }

    public List<ContentLinkEntity> createFromCustomContentLinks(List<CustomContentLink> customContentLinks, final boolean convertToAbsolute) {
        return Lists.transform(customContentLinks, (Function)new Function<CustomContentLink, ContentLinkEntity>(){

            public ContentLinkEntity apply(@Nullable CustomContentLink input) {
                return ContentLinkEntityFactory.this.createFromCustomContentLink(input, convertToAbsolute);
            }
        });
    }

    public ContentLinkEntity createFromCustomContentLink(CustomContentLink entity, boolean convertToAbsolute) {
        if (convertToAbsolute) {
            return new ContentLinkEntity(this.urlFactory.toAbsoluteUrl(entity.getLinkUrl()), entity.getLinkLabel(), "", true);
        }
        return new ContentLinkEntity(this.urlFactory.toRelativeUrlWithContextPath(entity.getLinkUrl()), entity.getLinkLabel(), "", true);
    }
}

