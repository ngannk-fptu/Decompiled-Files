/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.DraftResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.Spaced
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.PersonalInformation
 *  com.atlassian.confluence.xhtml.api.EditorFormatService
 *  com.atlassian.confluence.xhtml.api.Link
 *  com.atlassian.confluence.xhtml.api.LinkBody
 *  com.atlassian.confluence.xhtml.api.PlainTextLinkBody
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.tinymceplugin.rest;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.DraftResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.xhtml.api.EditorFormatService;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.LinkBody;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.Calendar;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="/link")
@AnonymousAllowed
public class LinkResource {
    private static final Logger log = LoggerFactory.getLogger(LinkResource.class);
    private final EditorFormatService editorFormatService;
    private final ResourceIdentifierResolver<IdAndTypeResourceIdentifier, Object> idresourceIdentifierResolver;
    private final PermissionManager permissionManager;

    public LinkResource(@ComponentImport EditorFormatService editorFormatService, @ComponentImport(value="idAndTypeResourceIdentifierResolver") @Qualifier(value="idAndTypeResourceIdentifierResolver") ResourceIdentifierResolver<IdAndTypeResourceIdentifier, Object> resourceIdentifierResolver, @ComponentImport PermissionManager permissionManager) {
        this.editorFormatService = editorFormatService;
        this.idresourceIdentifierResolver = resourceIdentifierResolver;
        this.permissionManager = permissionManager;
    }

    @GET
    @Path(value="/placeholder")
    @Consumes(value={"application/json"})
    @Produces(value={"text/plain"})
    public Response generatePlaceHolder(@QueryParam(value="resourceId") String resourceId, @QueryParam(value="resourceType") String resourceType, @QueryParam(value="alias") String alias) {
        String linkHtml;
        block15: {
            Object resource;
            ContentTypeEnum contentType;
            linkHtml = "";
            PlainTextLinkBody plainLinkBody = null;
            if (StringUtils.isNotBlank((CharSequence)alias)) {
                plainLinkBody = new PlainTextLinkBody(alias);
            }
            if ("user".equals(resourceType)) {
                resourceType = "userinfo";
            }
            if ((contentType = ContentTypeEnum.getByRepresentation((String)resourceType)) == null) {
                log.debug("Unsupported resource type: " + resourceType.replaceAll("[\r\n]", ""));
                return Response.serverError().build();
            }
            try {
                resource = this.idresourceIdentifierResolver.resolve((ResourceIdentifier)new IdAndTypeResourceIdentifier(Long.parseLong(resourceId), contentType), null);
            }
            catch (CannotResolveResourceIdentifierException e) {
                return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
            }
            try {
                PageContext pageContext = new PageContext();
                pageContext.setOutputType(ConversionContextOutputType.DISPLAY.value());
                DefaultConversionContext context = new DefaultConversionContext((RenderContext)pageContext);
                if (resource instanceof Attachment) {
                    Attachment attachment = (Attachment)resource;
                    if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)attachment.getSpace())) {
                        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
                    }
                    if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)attachment)) {
                        return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
                    }
                    AttachmentContainerResourceIdentifier containerResourceIdentifier = (AttachmentContainerResourceIdentifier)this.getResourceIdentifier(attachment.getContainer());
                    AttachmentResourceIdentifier resourceIdentifier = new AttachmentResourceIdentifier(containerResourceIdentifier, attachment.getFileName());
                    linkHtml = this.editorFormatService.convertLinkToEdit((Link)new DefaultLink((ResourceIdentifier)resourceIdentifier, (LinkBody)plainLinkBody), (ConversionContext)context);
                    break block15;
                }
                if (resource instanceof ContentEntityObject) {
                    ContentEntityObject contentEntityObject = (ContentEntityObject)resource;
                    if (contentEntityObject instanceof Spaced && !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)((Spaced)contentEntityObject).getSpace())) {
                        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
                    }
                    if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)contentEntityObject)) {
                        return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
                    }
                    linkHtml = this.editorFormatService.convertLinkToEdit((Link)new DefaultLink(this.getResourceIdentifier(contentEntityObject), (LinkBody)plainLinkBody), (ConversionContext)context);
                    break block15;
                }
                if (resource instanceof Space) {
                    Space space = (Space)resource;
                    if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)space)) {
                        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
                    }
                    linkHtml = this.editorFormatService.convertLinkToEdit((Link)new DefaultLink((ResourceIdentifier)new SpaceResourceIdentifier(space.getKey()), (LinkBody)plainLinkBody), (ConversionContext)context);
                    break block15;
                }
                log.error("Unsupported resource: " + resource);
                return Response.serverError().build();
            }
            catch (XhtmlException e) {
                log.error("Error occurred rendering resource", (Throwable)e);
                return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
        return Response.ok((Object)linkHtml).build();
    }

    private ResourceIdentifier getResourceIdentifier(ContentEntityObject content) {
        PageResourceIdentifier result = null;
        if (content instanceof Page) {
            result = new PageResourceIdentifier(((Page)content).getSpaceKey(), content.getTitle());
        } else if (content instanceof BlogPost) {
            BlogPost blogPost = (BlogPost)content;
            Calendar postingDay = Calendar.getInstance();
            postingDay.setTime(blogPost.getPostingDate());
            result = new BlogPostResourceIdentifier(blogPost.getSpaceKey(), blogPost.getTitle(), postingDay);
        } else if (content instanceof Draft) {
            result = new DraftResourceIdentifier(content.getId());
        } else if (content instanceof PersonalInformation) {
            ConfluenceUser user = ((PersonalInformation)content).getUser();
            result = UserResourceIdentifier.create((UserKey)user.getKey());
        }
        return result;
    }
}

