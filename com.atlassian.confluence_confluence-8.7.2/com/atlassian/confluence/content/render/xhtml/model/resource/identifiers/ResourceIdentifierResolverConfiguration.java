/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.migration.ContentDao;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentOwningContentResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifierOnlyUriResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.DraftResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageTemplateResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifierResolver;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.templates.persistence.dao.PageTemplateDao;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.spring.AvailableToPlugins;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ResourceIdentifierResolverConfiguration {
    @Resource
    private PageManager pageManager;
    @Resource
    private DraftManager draftManager;
    @Resource
    private SpaceManager spaceManager;
    @Resource
    private ContentDao contentDao;
    @Resource
    private PageTemplateDao pageTemplateDao;
    @Resource
    private ContentEntityManager contentEntityManager;
    @Resource
    private AttachmentManager attachmentManager;
    @Resource
    private SettingsManager settingsManager;

    ResourceIdentifierResolverConfiguration() {
    }

    @Bean
    PageResourceIdentifierResolver pageResourceIdentifierResolver() {
        return new PageResourceIdentifierResolver(this.pageManager);
    }

    @Bean
    BlogPostResourceIdentifierResolver blogPostResourceIdentifierResolver() {
        return new BlogPostResourceIdentifierResolver(this.pageManager);
    }

    @Bean
    DraftResourceIdentifierResolver draftResourceIdentifierResolver() {
        return new DraftResourceIdentifierResolver(this.draftManager);
    }

    @Bean
    SpaceResourceIdentifierResolver spaceResourceIdentifierResolver() {
        return new SpaceResourceIdentifierResolver(this.spaceManager);
    }

    @Bean
    ContentEntityResourceIdentifierResolver contentEntityResourceIdentifierResolver() {
        return new ContentEntityResourceIdentifierResolver(this.contentDao);
    }

    @Bean
    PageTemplateResourceIdentifierResolver pageTemplateResourceIdentifierResolver() {
        return new PageTemplateResourceIdentifierResolver(this.pageTemplateDao);
    }

    @Bean
    AttachmentOwningContentResolver attachmentOwningContentResolver() {
        return new AttachmentOwningContentResolver(this.pageResourceIdentifierResolver(), this.blogPostResourceIdentifierResolver(), this.contentEntityResourceIdentifierResolver(), this.draftResourceIdentifierResolver(), this.contentEntityManager, this.attachmentManager);
    }

    @Bean
    AttachmentResourceIdentifierResolver attachmentResourceIdentifierResolver() {
        return new AttachmentResourceIdentifierResolver(this.attachmentManager, this.attachmentOwningContentResolver());
    }

    @Bean
    AttachmentResourceIdentifierOnlyUriResolver attachmentUriResolver() {
        return new AttachmentResourceIdentifierOnlyUriResolver(this.settingsManager);
    }

    @Bean
    @AvailableToPlugins
    IdAndTypeResourceIdentifierResolver idAndTypeResourceIdentifierResolver() {
        return new IdAndTypeResourceIdentifierResolver(this.attachmentManager, this.spaceManager);
    }
}

