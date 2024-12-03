/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.migration.LinkResolver
 *  com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink
 *  com.atlassian.confluence.content.render.xhtml.model.links.NotPermittedLink
 *  com.atlassian.confluence.content.render.xhtml.model.links.UnresolvedLink
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifierResolver
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifierResolver
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.service.NotAuthorizedException
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.xhtml.api.Link
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.migration.LinkResolver;
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.links.NotPermittedLink;
import com.atlassian.confluence.content.render.xhtml.model.links.UnresolvedLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.plugins.macros.advanced.PageProvider;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.user.User;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

public class DefaultPageProvider
implements PageProvider {
    private LinkResolver linkResolver;
    private PermissionManager permissionManager;
    private I18NBeanFactory i18NBeanFactory;
    private BlogPostResourceIdentifierResolver blogPostResourceIdentifierResolver;
    private PageResourceIdentifierResolver pageResourceIdentifierResolver;

    @Override
    public ContentEntityObject resolve(String location, ConversionContext context) throws NotAuthorizedException {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        if (StringUtils.isEmpty((CharSequence)location)) {
            throw new IllegalArgumentException(i18NBean.getText("confluence.macros.advanced.include.error.no.location"));
        }
        Link link = this.linkResolver.resolve(location, context.getPageContext());
        if (link == null) {
            throw new IllegalArgumentException(i18NBean.getText("confluence.macros.advanced.include.error.no.link", (Object[])new String[]{location}));
        }
        return this.resolve(link, context);
    }

    @Override
    public ContentEntityObject resolve(Link link, ConversionContext context) {
        ContentEntityObject currentPage = context.getEntity();
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        ResourceIdentifier resourceIdentifier = link.getDestinationResourceIdentifier();
        String location = this.getLocation(resourceIdentifier);
        if (link instanceof NotPermittedLink) {
            throw new NotAuthorizedException(i18NBean.getText("confluence.macros.advanced.include.error.user.not.authorized", (Object[])new String[]{AuthenticatedUserThreadLocal.getUsername()}));
        }
        if (link instanceof UnresolvedLink || !(link instanceof DefaultLink)) {
            return null;
        }
        if (this.linkingToSelf(link, context) && this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)currentPage)) {
            return currentPage;
        }
        AbstractPage page = this.resolveResourceIdentifier(resourceIdentifier, context);
        if (page == null) {
            throw new IllegalArgumentException(i18NBean.getText("confluence.macros.advanced.include.error.no.link", (Object[])new String[]{location}));
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)page)) {
            throw new NotAuthorizedException(i18NBean.getText("confluence.macros.advanced.include.error.user.not.authorized", (Object[])new String[]{AuthenticatedUserThreadLocal.getUsername()}));
        }
        return page;
    }

    private boolean linkingToSelf(Link link, ConversionContext conversionContext) {
        if (conversionContext == null) {
            return true;
        }
        if (this.isGlobalObject(conversionContext)) {
            return false;
        }
        ResourceIdentifier resourceIdentifier = link.getDestinationResourceIdentifier();
        if (resourceIdentifier == null) {
            if (conversionContext.getProperty("macroDefinition") != null) {
                String linkedPageName = ((MacroDefinition)conversionContext.getProperty("macroDefinition")).getDefaultParameterValue();
                return this.isSamePageTitle(conversionContext, linkedPageName);
            }
            return true;
        }
        return this.isSameContentType(conversionContext, resourceIdentifier) && this.isSamePageTitle(conversionContext, resourceIdentifier) && this.isSameSpace(conversionContext, resourceIdentifier) && this.isSamePostingDateForBlog(conversionContext, resourceIdentifier);
    }

    private boolean isGlobalObject(ConversionContext conversionContext) {
        return conversionContext.getEntity() == null;
    }

    private boolean isSameContentType(ConversionContext conversionContext, ResourceIdentifier resourceIdentifier) {
        return ContentTypeEnum.PAGE.equals((Object)conversionContext.getEntity().getTypeEnum()) && resourceIdentifier instanceof PageResourceIdentifier || ContentTypeEnum.BLOG.equals((Object)conversionContext.getEntity().getTypeEnum()) && resourceIdentifier instanceof BlogPostResourceIdentifier;
    }

    private boolean isSamePageTitle(ConversionContext conversionContext, ResourceIdentifier resourceIdentifier) {
        return this.isSamePageTitle(conversionContext, this.getTitle(resourceIdentifier));
    }

    private boolean isSamePageTitle(ConversionContext conversionContext, String linkedPageTitle) {
        return StringUtils.isNotEmpty((CharSequence)conversionContext.getEntity().getTitle()) && conversionContext.getEntity().getTitle().equals(linkedPageTitle);
    }

    private boolean isSamePostingDateForBlog(ConversionContext conversionContext, ResourceIdentifier resourceIdentifier) {
        return !ContentTypeEnum.BLOG.equals((Object)conversionContext.getEntity().getTypeEnum()) || ((BlogPost)conversionContext.getEntity()).getPostingCalendarDate().equals(((BlogPostResourceIdentifier)resourceIdentifier).getPostingDay());
    }

    private String getTitle(ResourceIdentifier resourceIdentifier) {
        if (resourceIdentifier instanceof PageResourceIdentifier) {
            return ((PageResourceIdentifier)resourceIdentifier).getTitle();
        }
        if (resourceIdentifier instanceof BlogPostResourceIdentifier) {
            return ((BlogPostResourceIdentifier)resourceIdentifier).getTitle();
        }
        throw new IllegalArgumentException(this.i18NBeanFactory.getI18NBean().getText("confluence.macros.advanced.include.error.invalid.content-entity"));
    }

    private boolean isSameSpace(ConversionContext conversionContext, ResourceIdentifier resourceIdentifier) {
        return resourceIdentifier instanceof PageResourceIdentifier && ((PageResourceIdentifier)resourceIdentifier).getSpaceKey().equals(conversionContext.getSpaceKey()) || resourceIdentifier instanceof BlogPostResourceIdentifier && ((BlogPostResourceIdentifier)resourceIdentifier).getSpaceKey().equals(conversionContext.getSpaceKey());
    }

    private String getLocation(ResourceIdentifier resourceIdentifier) {
        if (resourceIdentifier instanceof BlogPostResourceIdentifier) {
            BlogPostResourceIdentifier blog = (BlogPostResourceIdentifier)resourceIdentifier;
            return (String)(blog.getSpaceKey() != null ? blog.getSpaceKey() + ":" : "") + "/" + BlogPost.toDatePath((Date)blog.getPostingDay().getTime()) + "/" + blog.getTitle();
        }
        if (resourceIdentifier instanceof PageResourceIdentifier) {
            PageResourceIdentifier page = (PageResourceIdentifier)resourceIdentifier;
            return (String)(page.getSpaceKey() != null ? page.getSpaceKey() + ":" : "") + page.getTitle();
        }
        return resourceIdentifier == null ? "" : resourceIdentifier.toString();
    }

    private AbstractPage resolveResourceIdentifier(ResourceIdentifier destination, ConversionContext context) {
        if (!(destination instanceof PageResourceIdentifier) && !(destination instanceof BlogPostResourceIdentifier)) {
            throw new IllegalArgumentException(this.i18NBeanFactory.getI18NBean().getText("confluence.macros.advanced.include.error.invalid.content-entity"));
        }
        try {
            if (destination instanceof BlogPostResourceIdentifier) {
                return this.blogPostResourceIdentifierResolver.resolve((BlogPostResourceIdentifier)destination, context);
            }
            return this.pageResourceIdentifierResolver.resolve((PageResourceIdentifier)destination, context);
        }
        catch (CannotResolveResourceIdentifierException e) {
            return null;
        }
    }

    public void setXhtmlMigrationLinkResolver(LinkResolver linkResolver) {
        this.linkResolver = linkResolver;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    public void setBlogPostResourceIdentifierResolver(BlogPostResourceIdentifierResolver blogPostResourceIdentifierResolver) {
        this.blogPostResourceIdentifierResolver = blogPostResourceIdentifierResolver;
    }

    public void setPageResourceIdentifierResolver(PageResourceIdentifierResolver pageResourceIdentifierResolver) {
        this.pageResourceIdentifierResolver = pageResourceIdentifierResolver;
    }
}

