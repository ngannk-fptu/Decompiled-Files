/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.ui;

import com.atlassian.confluence.content.ContentTypeManager;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.ui.AttachmentUiSupport;
import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.content.ui.PageUiSupport;
import com.atlassian.confluence.content.ui.SimpleUiSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultContentUiSupport
implements ContentUiSupport<ContentEntityObject> {
    private static final Logger log = LoggerFactory.getLogger(DefaultContentUiSupport.class);
    private final Map<String, ContentUiSupport> delegates;
    private final ContentUiSupport defaultDelegate;
    private final ContentTypeManager contentTypeManager;

    public DefaultContentUiSupport(WebResourceUrlProvider webResourceUrlProvider, ContentTypeManager contentTypeManager) {
        this.contentTypeManager = contentTypeManager;
        HashMap<String, ContentUiSupport> delegates = new HashMap<String, ContentUiSupport>();
        delegates.put("page", new PageUiSupport(webResourceUrlProvider));
        this.addDelegate(delegates, webResourceUrlProvider, "comment", "/images/icons/contenttypes/comment_16.png", "aui-icon content-type-comment", "comment.name");
        this.addDelegate(delegates, webResourceUrlProvider, "spacedesc", "/images/icons/contenttypes/space_16.png", "aui-icon content-type-space", "space.name");
        this.addDelegate(delegates, webResourceUrlProvider, "personalspacedesc", "/images/icons/contenttypes/personal_space_16.png", "aui-icon content-type-personal-space", "personal.space");
        this.addDelegate(delegates, webResourceUrlProvider, "userinfo", "/images/icons/classic/16/28.png", "aui-icon content-type-profile", "profile.name");
        this.addDelegate(delegates, webResourceUrlProvider, "blogpost", "/images/icons/contenttypes/blog_post_16.png", "aui-icon content-type-blogpost", "news.name");
        this.addDelegate(delegates, webResourceUrlProvider, "status", "/images/icons/contenttypes/status_16.png", "aui-icon aui-icon-small aui-iconfont-user-status", "status.name");
        delegates.put("attachment", new AttachmentUiSupport(webResourceUrlProvider));
        this.delegates = delegates;
        this.defaultDelegate = SimpleUiSupport.getUnknown(webResourceUrlProvider);
    }

    @Override
    public String getIconFilePath(ContentEntityObject content, int size) {
        return this.getDelegate(content).getIconFilePath(content, size);
    }

    @Override
    public String getIconPath(ContentEntityObject content, int size) {
        return this.getDelegate(content).getIconPath(content, size);
    }

    @Override
    public String getLegacyIconPath(String contentType, int size) {
        return this.getDelegate(contentType).getLegacyIconPath(contentType, size);
    }

    @Override
    public String getIconCssClass(ContentEntityObject content) {
        return this.getDelegate(content).getIconCssClass(content);
    }

    @Override
    public String getContentCssClass(String contentType, String contentPluginKey) {
        return this.getDelegate(contentType, contentPluginKey).getContentCssClass(contentType, contentPluginKey);
    }

    @Override
    public String getContentCssClass(ContentEntityObject content) {
        return this.getDelegate(content).getContentCssClass(content);
    }

    private ContentUiSupport getDelegate(String contentType, String contentPluginKey) {
        if (contentPluginKey != null) {
            return this.getPluginDelegate(contentPluginKey);
        }
        return this.getDelegate(contentType);
    }

    @Override
    public String getIconCssClass(SearchResult result) {
        return this.getDelegate(result).getIconCssClass(result);
    }

    @Override
    public String getContentTypeI18NKey(SearchResult result) {
        return this.getDelegate(result).getContentTypeI18NKey(result);
    }

    private ContentUiSupport getDelegate(SearchResult result) {
        return this.getDelegate(result.getType(), result.getExtraFields().get(SearchFieldNames.CONTENT_PLUGIN_KEY));
    }

    @Override
    public String getContentTypeI18NKey(ContentEntityObject content) {
        return this.getDelegate(content).getContentTypeI18NKey(content);
    }

    private ContentUiSupport getDelegate(ContentEntityObject object) {
        if (object instanceof CustomContentEntityObject) {
            return this.getPluginDelegate((CustomContentEntityObject)object);
        }
        return this.getDelegate(object.getType());
    }

    private ContentUiSupport getPluginDelegate(CustomContentEntityObject pluginContentEntityObject) {
        return this.getPluginDelegate(pluginContentEntityObject.getPluginModuleKey());
    }

    private ContentUiSupport getPluginDelegate(String contentPluginKey) {
        return this.contentTypeManager.getContentType(contentPluginKey).getContentUiSupport();
    }

    private ContentUiSupport getDelegate(String contentType) {
        if (this.delegates.containsKey(contentType)) {
            return this.delegates.get(contentType);
        }
        log.info("No UI delegate found for content type {}. Falling back on default.", (Object)contentType);
        return this.defaultDelegate;
    }

    private void addDelegate(Map<String, ContentUiSupport> delegates, WebResourceUrlProvider webResourceUrlProvider, String contentType, String iconPath, String iconCssClass, String i18NKey) {
        delegates.put(contentType, new SimpleUiSupport(webResourceUrlProvider, iconPath, iconCssClass, "content-type-" + contentType, i18NKey));
    }
}

