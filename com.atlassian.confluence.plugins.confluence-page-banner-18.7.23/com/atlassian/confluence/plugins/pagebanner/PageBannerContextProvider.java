/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.actions.AbstractPageAction
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.plugin.web.model.WebIcon
 *  com.atlassian.plugin.web.model.WebLabel
 *  com.atlassian.plugin.web.model.WebLink
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.web.context.HttpContext
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.plugins.pagebanner;

import com.atlassian.confluence.pages.actions.AbstractPageAction;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugins.pagebanner.BannerItem;
import com.atlassian.confluence.plugins.pagebanner.IconItem;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.model.WebIcon;
import com.atlassian.plugin.web.model.WebLabel;
import com.atlassian.plugin.web.model.WebLink;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.sal.api.web.context.HttpContext;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;

public class PageBannerContextProvider
implements ContextProvider {
    private static final String SYSTEM_CONTENT_METADATA_LOCATION = "system.content.metadata";
    private static final String PAGE_METADATA_BANNER_LOCATION = "page.metadata.banner";
    private final WebInterfaceManager webInterfaceManager;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final HttpContext httpContext;

    public PageBannerContextProvider(WebInterfaceManager webInterfaceManager, WebResourceUrlProvider webResourceUrlProvider, HttpContext httpContext) {
        this.webInterfaceManager = Objects.requireNonNull(webInterfaceManager);
        this.webResourceUrlProvider = Objects.requireNonNull(webResourceUrlProvider);
        this.httpContext = Objects.requireNonNull(httpContext);
    }

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> params) {
        Object action = params.get("action");
        if (action instanceof AbstractPageAction) {
            WebInterfaceContext webInterfaceContext = ((AbstractPageAction)action).getWebInterfaceContext();
            return ImmutableMap.builder().put((Object)"systemContentItems", this.getBannerItems(SYSTEM_CONTENT_METADATA_LOCATION, webInterfaceContext)).put((Object)"pageBannerItems", this.getBannerItems(PAGE_METADATA_BANNER_LOCATION, webInterfaceContext)).build();
        }
        return Collections.emptyMap();
    }

    private List<BannerItem> getBannerItems(String section, WebInterfaceContext webInterfaceContext) {
        ArrayList items = Lists.newArrayList();
        HttpServletRequest request = (HttpServletRequest)Preconditions.checkNotNull((Object)this.httpContext.getRequest(), (Object)"HttpServletRequest not present");
        Map context = webInterfaceContext.toMap();
        for (WebItemModuleDescriptor webItem : this.webInterfaceManager.getDisplayableItems(section, context)) {
            items.add(this.createBannerItem(webItem, request, context));
        }
        return items;
    }

    private BannerItem createBannerItem(WebItemModuleDescriptor item, HttpServletRequest request, Map<String, Object> context) {
        WebLink link = item.getLink();
        WebLabel label = item.getWebLabel();
        WebLabel tooltip = item.getTooltip();
        WebIcon icon = item.getIcon();
        Map params = item.getParams();
        IconItem iconItem = icon == null ? null : this.getIconItem(context, icon);
        return new BannerItem(label != null ? label.getDisplayableLabel(request, context) : "", link != null ? link.getDisplayableUrl(request, context) : "#", tooltip != null ? tooltip.getDisplayableLabel(request, context) : "", link != null ? link.getId() : "", item.getStyleClass(), iconItem, params != null && Boolean.parseBoolean((String)params.get("suppressStyle")));
    }

    private IconItem getIconItem(Map<String, Object> context, WebIcon icon) {
        try {
            URI uri = this.getUri(context, icon);
            return new IconItem(icon.getHeight(), icon.getWidth(), uri.toString());
        }
        catch (URISyntaxException e) {
            return null;
        }
    }

    private URI getUri(Map<String, Object> context, WebIcon icon) throws URISyntaxException {
        URI uri = new URI(icon.getUrl().getRenderedUrl(context));
        if (!uri.isAbsolute()) {
            return URI.create(this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.RELATIVE) + uri.toString());
        }
        return uri;
    }
}

