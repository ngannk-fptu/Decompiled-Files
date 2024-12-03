/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.PersonalInformation
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.breadcrumbs.AttachmentBreadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.Breadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator
 *  com.atlassian.confluence.util.breadcrumbs.CustomContentBreadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.SpaceBreadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.UserBreadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.spaceia.SpaceBreadcrumb
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.user.User
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.rest.resources;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.rest.resources.AbstractResource;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.breadcrumbs.AttachmentBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator;
import com.atlassian.confluence.util.breadcrumbs.CustomContentBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.UserBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.spaceia.SpaceBreadcrumb;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="/breadcrumb")
@AnonymousAllowed
public class BreadcrumbResource
extends AbstractResource {
    private static final Logger log = LoggerFactory.getLogger(BreadcrumbResource.class);
    private final ResourceIdentifierResolver<IdAndTypeResourceIdentifier, Object> idAndTypeResourceIdentifierResolver;
    private final PermissionManager permissionManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final ContextPathHolder contextPathHolder;
    private final BreadcrumbGenerator breadcrumbGenerator;

    private BreadcrumbResource() {
        this.permissionManager = null;
        this.idAndTypeResourceIdentifierResolver = null;
        this.i18NBeanFactory = null;
        this.contextPathHolder = null;
        this.breadcrumbGenerator = null;
    }

    public BreadcrumbResource(@Qualifier(value="idAndTypeResourceIdentifierResolver") ResourceIdentifierResolver<IdAndTypeResourceIdentifier, Object> idAndTypeResourceIdentifierResolver, @Qualifier(value="userAccessor") UserAccessor userAccessor, @Qualifier(value="i18NBeanFactory") I18NBeanFactory i18NBeanFactory, ContextPathHolder contextPathHolder, PermissionManager permissionManager, SpacePermissionManager spacePermissionManager, BreadcrumbGenerator breadcrumbGenerator) {
        super(userAccessor, spacePermissionManager);
        this.idAndTypeResourceIdentifierResolver = idAndTypeResourceIdentifierResolver;
        this.i18NBeanFactory = i18NBeanFactory;
        this.contextPathHolder = contextPathHolder;
        this.permissionManager = permissionManager;
        this.breadcrumbGenerator = breadcrumbGenerator;
    }

    @GET
    @Produces(value={"application/json"})
    public Response generateBreadcrumbs(@QueryParam(value="id") @DefaultValue(value="-1") long resourceId, @QueryParam(value="type") String resourceType) {
        List<Crumb> breadcrumbs;
        ContentTypeEnum contentType;
        if (resourceId == -1L || StringUtils.isBlank((CharSequence)resourceType)) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        if ("user".equals(resourceType)) {
            resourceType = "userinfo";
        }
        if ((contentType = ContentTypeEnum.getByRepresentation((String)resourceType)) == null) {
            log.debug("Unsupported resource type: " + resourceType);
            return Response.serverError().build();
        }
        Object resource = this.getResource(resourceId, contentType);
        if (resource == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        if (resource instanceof Page || resource instanceof BlogPost) {
            Space space = ((AbstractPage)resource).getLatestVersion().getSpace();
            breadcrumbs = this.getBreadcrumbTrail(space, this.breadcrumbGenerator.getContentBreadcrumb(space, (AbstractPage)resource));
        } else if (resource instanceof CustomContentEntityObject) {
            breadcrumbs = this.getBreadcrumbTrail(null, (Breadcrumb)new CustomContentBreadcrumb((CustomContentEntityObject)resource));
        } else if (resource instanceof Attachment) {
            breadcrumbs = this.getBreadcrumbTrail(((Attachment)resource).getSpace(), (Breadcrumb)new AttachmentBreadcrumb((Attachment)resource));
        } else if (resource instanceof PersonalInformation) {
            ConfluenceUser user = ((PersonalInformation)resource).getUser();
            if (user == null) {
                return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
            }
            breadcrumbs = this.getBreadcrumbTrail(null, (Breadcrumb)new UserBreadcrumb((User)user));
        } else if (resource instanceof Space) {
            breadcrumbs = this.getBreadcrumbTrail((Space)resource, (Breadcrumb)new SpaceBreadcrumb((Space)resource));
        } else {
            log.error("Unsupported resource type: " + resource);
            return Response.serverError().build();
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("type", resourceType);
        map.put("breadcrumbs", breadcrumbs);
        return Response.ok(map).build();
    }

    private Object getResource(long resourceId, ContentTypeEnum contentType) {
        try {
            Object resource = this.idAndTypeResourceIdentifierResolver.resolve((ResourceIdentifier)new IdAndTypeResourceIdentifier(resourceId, contentType), null);
            return this.permissionManager.hasPermission(this.getCurrentUser(), Permission.VIEW, resource) ? resource : null;
        }
        catch (CannotResolveResourceIdentifierException e) {
            return null;
        }
    }

    private List<Crumb> getBreadcrumbTrail(Space space, Breadcrumb breadcrumb) {
        if (space != null) {
            breadcrumb = new com.atlassian.confluence.util.breadcrumbs.SpaceBreadcrumb(space).concatWith(breadcrumb);
        }
        breadcrumb.setFilterTrailingBreadcrumb(false);
        ArrayList<Crumb> crumbs = new ArrayList<Crumb>();
        for (Breadcrumb bc : this.breadcrumbGenerator.getFilteredBreadcrumbTrail(space, breadcrumb)) {
            crumbs.add(new Crumb(this.i18NBeanFactory.getI18NBean().getText(bc.getTitle()), this.contextPathHolder.getContextPath() + bc.getTarget()));
        }
        return crumbs;
    }

    @XmlRootElement
    public static final class Crumb {
        @XmlAttribute
        private final String title;
        @XmlAttribute
        private final String url;

        private Crumb() {
            this("", "");
        }

        public Crumb(String title, String url) {
            this.title = title;
            this.url = url;
        }

        public String getTitle() {
            return this.title;
        }

        public String getUrl() {
            return this.url;
        }
    }
}

