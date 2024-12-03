/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.impl.rss;

import com.atlassian.confluence.content.render.xhtml.compatibility.BodyTypeAwareRenderer;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.rss.AbstractRenderSupport;
import com.atlassian.confluence.rss.AttachmentRenderSupport;
import com.atlassian.confluence.rss.CommentRenderSupport;
import com.atlassian.confluence.rss.DefaultFeedBuilder;
import com.atlassian.confluence.rss.FeedBuilder;
import com.atlassian.confluence.rss.PageRenderSupport;
import com.atlassian.confluence.rss.RssRenderSupport;
import com.atlassian.confluence.rss.SyndFeedService;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.AvailableToPlugins;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.renderer.WikiStyleRenderer;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class FeedBuilderConfig {
    @Resource
    private SearchManager searchManager;
    @Resource
    private PermissionManager permissionManager;
    @Resource
    private UserAccessor userAccessor;
    @Resource
    private FormatSettingsManager formatSettingsManager;
    @Resource
    private LocaleManager localeManager;
    @Resource
    private PluginAccessor pluginAccessor;
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private SettingsManager settingsManager;
    @Resource
    private WebResourceManager webResourceManager;
    @Resource
    private BodyTypeAwareRenderer viewBodyTypeAwareRenderer;
    @Resource
    private WikiStyleRenderer wikiStyleRenderer;
    @Resource
    private AttachmentManager attachmentManager;

    FeedBuilderConfig() {
    }

    @Bean
    @AvailableToPlugins(interfaces={FeedBuilder.class, SyndFeedService.class})
    public DefaultFeedBuilder feedBuilder() {
        return new DefaultFeedBuilder(this.searchManager, this.settingsManager, this.permissionManager, this.userAccessor, this.formatSettingsManager, this.localeManager, this.renderSupport(), this.pluginAccessor, this.eventPublisher);
    }

    private Map<String, RssRenderSupport> renderSupport() {
        return ImmutableMap.of((Object)Page.class.getName(), (Object)this.pageRenderSupport(), (Object)BlogPost.class.getName(), (Object)this.pageRenderSupport(), (Object)Comment.class.getName(), (Object)this.commentRenderSupport(), (Object)Attachment.class.getName(), (Object)this.attachmentRenderSupport());
    }

    private PageRenderSupport pageRenderSupport() {
        PageRenderSupport support = this.populate(new PageRenderSupport());
        support.setWikiStyleRenderer(this.wikiStyleRenderer);
        return support;
    }

    private CommentRenderSupport commentRenderSupport() {
        CommentRenderSupport support = this.populate(new CommentRenderSupport());
        support.setWikiStyleRenderer(this.wikiStyleRenderer);
        return support;
    }

    private AttachmentRenderSupport attachmentRenderSupport() {
        AttachmentRenderSupport support = this.populate(new AttachmentRenderSupport());
        support.setAttachmentManager(this.attachmentManager);
        return support;
    }

    private <T extends AbstractRenderSupport<?>> T populate(T support) {
        support.setSettingsManager(this.settingsManager);
        support.setUserAccessor(this.userAccessor);
        support.setViewBodyTypeAwareRenderer(this.viewBodyTypeAwareRenderer);
        support.setWebResourceManager(this.webResourceManager);
        return support;
    }
}

