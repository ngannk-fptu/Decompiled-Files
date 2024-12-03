/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.ui;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.content.ui.ApiContentUiSupport;
import com.atlassian.confluence.content.ui.AttachmentApiContentUiSupport;
import com.atlassian.confluence.content.ui.DefaultContentUiSupport;
import com.atlassian.confluence.content.ui.SimpleApiContentUiSupport;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultApiContentUiSupport
implements ApiContentUiSupport<Content> {
    private static final Logger log = LoggerFactory.getLogger(DefaultContentUiSupport.class);
    private final Map<String, ApiContentUiSupport> delegates;
    private final ApiContentUiSupport defaultDelegate;

    public DefaultApiContentUiSupport(WebResourceUrlProvider webResourceUrlProvider) {
        HashMap<String, ApiContentUiSupport> delegates = new HashMap<String, ApiContentUiSupport>();
        this.addDelegate(delegates, webResourceUrlProvider, "page", "/images/icons/contenttypes/page_16.png", "aui-icon content-type-page", "page.word");
        this.addDelegate(delegates, webResourceUrlProvider, "comment", "/images/icons/contenttypes/comment_16.png", "aui-icon content-type-comment", "comment.name");
        this.addDelegate(delegates, webResourceUrlProvider, "spacedesc", "/images/icons/contenttypes/space_16.png", "aui-icon content-type-space", "space.name");
        this.addDelegate(delegates, webResourceUrlProvider, "personalspacedesc", "/images/icons/contenttypes/personal_space_16.png", "aui-icon content-type-personal-space", "personal.space");
        this.addDelegate(delegates, webResourceUrlProvider, "userinfo", "/images/icons/classic/16/28.png", "aui-icon content-type-profile", "profile.name");
        this.addDelegate(delegates, webResourceUrlProvider, "blogpost", "/images/icons/contenttypes/blog_post_16.png", "aui-icon content-type-blogpost", "news.name");
        this.addDelegate(delegates, webResourceUrlProvider, "status", "/images/icons/contenttypes/status_16.png", "aui-icon aui-icon-small aui-iconfont-user-status", "status.name");
        delegates.put("attachment", new AttachmentApiContentUiSupport(webResourceUrlProvider));
        this.delegates = delegates;
        this.defaultDelegate = SimpleApiContentUiSupport.getUnknown(webResourceUrlProvider);
    }

    @Override
    public String getIconFilePath(Content content, int size) {
        return this.getDelegate(content).getIconFilePath(content, size);
    }

    @Override
    public String getIconPath(Content content, int size) {
        return this.getDelegate(content).getIconPath(content, size);
    }

    @Override
    public String getLegacyIconPath(String contentType, int size) {
        return this.getDelegate(contentType).getLegacyIconPath(contentType, size);
    }

    @Override
    public String getIconCssClass(Content content) {
        return this.getDelegate(content).getIconCssClass(content);
    }

    @Override
    public String getContentCssClass(String contentType, String contentPluginKey) {
        throw new UnsupportedOperationException("Getting css class by plugin key is not supported as content plugin key is not exposed in api content model");
    }

    @Override
    public String getContentCssClass(Content content) {
        return this.getDelegate(content).getContentCssClass(content);
    }

    @Override
    public String getIconCssClass(SearchResult result) {
        return this.getDelegate(result).getIconCssClass(result);
    }

    @Override
    public String getContentTypeI18NKey(SearchResult result) {
        return this.getDelegate(result).getContentTypeI18NKey(result);
    }

    @Override
    public String getContentTypeI18NKey(Content content) {
        return this.getDelegate(content).getContentTypeI18NKey(content);
    }

    private ApiContentUiSupport getDelegate(String contentType, String contentPluginKey) {
        if (contentPluginKey != null) {
            throw new UnsupportedOperationException("Getting css class by plugin key is not supported as content plugin key is not exposed in api content model");
        }
        return this.getDelegate(contentType);
    }

    private ApiContentUiSupport getDelegate(SearchResult result) {
        return this.getDelegate(result.getType(), result.getExtraFields().get(SearchFieldNames.CONTENT_PLUGIN_KEY));
    }

    private ApiContentUiSupport getDelegate(Content object) {
        return this.getDelegate(object.getType().getType());
    }

    private ApiContentUiSupport getDelegate(String contentType) {
        if (this.delegates.containsKey(contentType)) {
            return this.delegates.get(contentType);
        }
        log.info("No UI delegate found for content type {}. Falling back on default.", (Object)contentType);
        return this.defaultDelegate;
    }

    private void addDelegate(Map<String, ApiContentUiSupport> delegates, WebResourceUrlProvider webResourceUrlProvider, String contentType, String iconPath, String iconCssClass, String i18NKey) {
        delegates.put(contentType, new SimpleApiContentUiSupport(webResourceUrlProvider, iconPath, iconCssClass, "content-type-" + contentType, i18NKey));
    }
}

