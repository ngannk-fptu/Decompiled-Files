/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.core.util.XMLUtils
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.sharelinks;

import com.atlassian.confluence.plugins.BusinessBlueprintsContextProviderHelper;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.plugins.sharelinks.LinkMetaData;
import com.atlassian.confluence.plugins.sharelinks.LinkMetaDataExtractor;
import com.atlassian.confluence.plugins.sharelinks.widgetconnector.WidgetConnectorSupport;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.core.util.XMLUtils;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.google.common.collect.Maps;
import java.net.URISyntaxException;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;

public class SharelinksContextProvider
extends AbstractBlueprintContextProvider {
    private final LinkMetaDataExtractor linkMetaDataExtractor;
    private final BusinessBlueprintsContextProviderHelper helper;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final WidgetConnectorSupport widgetConnectorSupport;

    public SharelinksContextProvider(LinkMetaDataExtractor linkMetaDataExtractor, BusinessBlueprintsContextProviderHelper helper, WebResourceUrlProvider webResourceUrlProvider, WidgetConnectorSupport widgetConnectorSupport) {
        this.linkMetaDataExtractor = linkMetaDataExtractor;
        this.helper = helper;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.widgetConnectorSupport = widgetConnectorSupport;
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        String url = (String)context.get("url");
        LinkMetaData meta = new LinkMetaData(url);
        try {
            meta = this.linkMetaDataExtractor.parseMetaData(url, false);
        }
        catch (URISyntaxException e) {
            meta.setDomain(url);
        }
        String title = meta.getTitle();
        if (StringUtils.isEmpty((CharSequence)title)) {
            title = meta.getSourceURL();
        }
        context.setTitle(title);
        String faviconURL = meta.getFaviconURL();
        if (StringUtils.isEmpty((CharSequence)faviconURL)) {
            faviconURL = this.getDefaultSharedLinksFavicon();
        }
        String faviconImg = String.format("<img class=\"confluence-embedded-image confluence-external-resource\" src=\"%s\" height=\"16px\"/>", XMLUtils.escape((String)faviconURL));
        context.put("faviconImg", (Object)faviconImg);
        String htmlLink = String.format("<a href=\"%s\">%s</a>", XMLUtils.escape((String)meta.getSourceURL()), XMLUtils.escape((String)meta.getExcerptedURL()));
        context.put("htmlLink", (Object)htmlLink);
        context.put("createdDate", (Object)this.helper.getFormattedLocalDate(null));
        this.addLinkMetaDataToContextMap(meta, context);
        return context;
    }

    private void addLinkMetaDataToContextMap(LinkMetaData linkMetaData, BlueprintContext context) {
        String soyTemplateName;
        HashMap soyLinkMetaDataContext = Maps.newHashMap();
        if (this.isTwitterCardUrl(linkMetaData)) {
            soyTemplateName = "Confluence.Blueprints.Sharelinks.twitterMetaDataHtml.soy";
        } else if (StringUtils.isNotEmpty((CharSequence)linkMetaData.getVideoURL())) {
            soyTemplateName = "Confluence.Blueprints.Sharelinks.videoMetaDataHtml.soy";
            boolean isSupportedMediaDomain = this.widgetConnectorSupport.isSupported(linkMetaData.getDomain());
            soyLinkMetaDataContext.put("isSupportedMediaDomain", isSupportedMediaDomain);
        } else {
            soyTemplateName = "Confluence.Blueprints.Sharelinks.metaDataHtml.soy";
        }
        soyLinkMetaDataContext.put("linkMetaData", linkMetaData);
        String faviconURL = linkMetaData.getFaviconURL();
        if (StringUtils.isEmpty((CharSequence)faviconURL)) {
            faviconURL = this.getDefaultSharedLinksFavicon();
        }
        soyLinkMetaDataContext.put("faviconURL", faviconURL);
        I18NBean i18nBean = this.helper.getI18nBean();
        String descriptionMessage = linkMetaData.getDescription();
        if (StringUtils.isEmpty((CharSequence)descriptionMessage)) {
            descriptionMessage = StringUtils.isEmpty((CharSequence)linkMetaData.getImageURL()) ? i18nBean.getText("sharelinks.blueprint.page.preview.unavailable") : i18nBean.getText("sharelinks.blueprint.page.description.unavailable");
        }
        soyLinkMetaDataContext.put("descriptionMessage", descriptionMessage);
        String soyLinkMetaDataHtml = this.helper.renderFromSoy("com.atlassian.confluence.plugins.confluence-business-blueprints:sharelinks-resources", soyTemplateName, soyLinkMetaDataContext);
        context.put("linkMetaDataHtml", (Object)soyLinkMetaDataHtml);
    }

    private boolean isTwitterCardUrl(LinkMetaData linkMetaData) {
        String domain = linkMetaData.getDomain();
        return domain != null && domain.contains("twitter.com") && (linkMetaData.getSourceURL().contains("/status/") || linkMetaData.getSourceURL().contains("/statuses/"));
    }

    private String getDefaultSharedLinksFavicon() {
        return this.webResourceUrlProvider.getStaticPluginResourceUrl("com.atlassian.confluence.plugins.confluence-business-blueprints:sharelinks-resources", "default-sharelinks-favicon-16.png", UrlMode.ABSOLUTE);
    }
}

