/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.link;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator;
import com.atlassian.confluence.content.render.xhtml.model.links.CreatePageLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageTemplateResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.view.link.CommonLinkAttributesWriter;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewBlogPostLinkMarshaller;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewCreatePageLinkMarshaller;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewLinkMarshallerFactory;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewPageLinkMarshaller;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewPageTemplateLinkMarshaller;
import com.atlassian.confluence.content.render.xhtml.view.link.ViewUserLinkMarshaller;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.xhtml.api.Link;
import java.util.Objects;

public class ViewLinkMarshallerFactoryImpl
implements ViewLinkMarshallerFactory {
    private final ResourceIdentifierResolver<PageResourceIdentifier, Page> pageResourceIdentifierResolver;
    private final ResourceIdentifierResolver<BlogPostResourceIdentifier, BlogPost> blogPostResourceIdentifierResolver;
    private final ResourceIdentifierResolver<PageTemplateResourceIdentifier, PageTemplate> pageTemplateResourceIdentifierResolver;
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private final CommonLinkAttributesWriter commonLinkAttributesWriter;
    private final Marshaller<Link> linkBodyMarshaller;
    private final HrefEvaluator hrefEvaluator;
    private final Marshaller<Link> unresolvedLinkMarshaller;
    private final ConfluenceUserResolver confluenceUserResolver;
    private final PersonalInformationManager personalInformationManager;
    private final GlobalSettingsManager settingsManager;
    private final Marshaller<CreatePageLink> createPageLinkMarshaller;

    public ViewLinkMarshallerFactoryImpl(ResourceIdentifierResolver<PageResourceIdentifier, Page> pageResourceIdentifierResolver, ResourceIdentifierResolver<BlogPostResourceIdentifier, BlogPost> blogPostResourceIdentifierResolver, ResourceIdentifierResolver<PageTemplateResourceIdentifier, PageTemplate> pageTemplateResourceIdentifierResolver, SpaceManager spaceManager, PermissionManager permissionManager, XmlStreamWriterTemplate xmlStreamWriterTemplate, Marshaller<Link> linkBodyMarshaller, HrefEvaluator hrefEvaluator, Marshaller<Link> unresolvedLinkMarshaller, ConfluenceUserResolver confluenceUserResolver, PersonalInformationManager personalInformationManager, GlobalSettingsManager settingsManager, CommonLinkAttributesWriter commonLinkAttributesWriter) {
        this.pageResourceIdentifierResolver = Objects.requireNonNull(pageResourceIdentifierResolver);
        this.blogPostResourceIdentifierResolver = Objects.requireNonNull(blogPostResourceIdentifierResolver);
        this.pageTemplateResourceIdentifierResolver = pageTemplateResourceIdentifierResolver;
        this.xmlStreamWriterTemplate = Objects.requireNonNull(xmlStreamWriterTemplate);
        this.linkBodyMarshaller = Objects.requireNonNull(linkBodyMarshaller);
        this.hrefEvaluator = Objects.requireNonNull(hrefEvaluator);
        this.unresolvedLinkMarshaller = Objects.requireNonNull(unresolvedLinkMarshaller);
        this.confluenceUserResolver = Objects.requireNonNull(confluenceUserResolver);
        this.personalInformationManager = Objects.requireNonNull(personalInformationManager);
        this.settingsManager = Objects.requireNonNull(settingsManager);
        this.commonLinkAttributesWriter = Objects.requireNonNull(commonLinkAttributesWriter);
        this.createPageLinkMarshaller = new ViewCreatePageLinkMarshaller(xmlStreamWriterTemplate, permissionManager, unresolvedLinkMarshaller, commonLinkAttributesWriter, linkBodyMarshaller, null, spaceManager);
    }

    @Override
    public Marshaller<Link> newPageLinkMarshaller() {
        return new ViewPageLinkMarshaller(this.pageResourceIdentifierResolver, this.xmlStreamWriterTemplate, this.createPageLinkMarshaller, this.commonLinkAttributesWriter, this.linkBodyMarshaller, this.hrefEvaluator, null);
    }

    @Override
    public Marshaller<Link> newPageLinkMarshaller(Marshaller<CreatePageLink> createPageLinkMarshaller, HrefEvaluator hrefEvaluator, Marshaller<Link> unresolvedLinkMarshaller) {
        if (createPageLinkMarshaller == null) {
            createPageLinkMarshaller = this.createPageLinkMarshaller;
        }
        if (hrefEvaluator == null) {
            hrefEvaluator = this.hrefEvaluator;
        }
        return new ViewPageLinkMarshaller(this.pageResourceIdentifierResolver, this.xmlStreamWriterTemplate, createPageLinkMarshaller, this.commonLinkAttributesWriter, this.linkBodyMarshaller, hrefEvaluator, null);
    }

    @Override
    public Marshaller<Link> newBlogPostLinkMarshaller() {
        return new ViewBlogPostLinkMarshaller(this.blogPostResourceIdentifierResolver, this.xmlStreamWriterTemplate, this.unresolvedLinkMarshaller, this.commonLinkAttributesWriter, this.linkBodyMarshaller, this.hrefEvaluator, null);
    }

    @Override
    public Marshaller<Link> newBlogPostLinkMarshaller(HrefEvaluator hrefEvaluator, Marshaller<Link> unresolvedLinkMarshaller) {
        if (hrefEvaluator == null) {
            hrefEvaluator = this.hrefEvaluator;
        }
        if (unresolvedLinkMarshaller == null) {
            unresolvedLinkMarshaller = this.unresolvedLinkMarshaller;
        }
        return new ViewBlogPostLinkMarshaller(this.blogPostResourceIdentifierResolver, this.xmlStreamWriterTemplate, unresolvedLinkMarshaller, this.commonLinkAttributesWriter, this.linkBodyMarshaller, hrefEvaluator, null);
    }

    @Override
    public Marshaller<Link> newUserLinkMarshaller() {
        return new ViewUserLinkMarshaller(this.confluenceUserResolver, this.xmlStreamWriterTemplate, this.commonLinkAttributesWriter, this.linkBodyMarshaller, this.unresolvedLinkMarshaller, null, null, this.personalInformationManager, this.hrefEvaluator, this.settingsManager);
    }

    @Override
    public Marshaller<Link> newUserLinkMarshaller(HrefEvaluator hrefEvaluator, Marshaller<Link> unresolvedLinkMarshaller) {
        return new ViewUserLinkMarshaller(this.confluenceUserResolver, this.xmlStreamWriterTemplate, this.commonLinkAttributesWriter, this.linkBodyMarshaller, unresolvedLinkMarshaller, null, null, this.personalInformationManager, hrefEvaluator, this.settingsManager);
    }

    @Override
    public Marshaller<Link> newPageTemplateLinkMarshaller() {
        return new ViewPageTemplateLinkMarshaller(this.pageTemplateResourceIdentifierResolver, this.xmlStreamWriterTemplate, this.linkBodyMarshaller, this.commonLinkAttributesWriter, this.hrefEvaluator, null);
    }
}

