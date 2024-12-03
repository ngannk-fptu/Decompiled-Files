/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.exceptions.ReadOnlyException
 *  com.atlassian.confluence.core.BodyType
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.pages.templates.PageTemplateManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  javax.annotation.Nonnull
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.exceptions.ReadOnlyException;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.plugins.createcontent.ContentTemplateRefManager;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import com.atlassian.confluence.plugins.createcontent.exceptions.ResourceException;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.rest.AbstractRestResource;
import com.atlassian.confluence.plugins.createcontent.rest.PageTemplateForm;
import com.atlassian.confluence.plugins.createcontent.template.PluginPageTemplateHelper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

@Path(value="/templates")
public class PageTemplateResource
extends AbstractRestResource {
    public static final String PARAM_ID = "id";
    public static final String PARAM_CONTENT_TEMPLATE_REF_ID = "contentTemplateRefId";
    private final PageTemplateManager pageTemplateManager;
    private final PluginPageTemplateHelper pageTemplateHelper;
    private final ContentTemplateRefManager contentTemplateRefManager;
    private final SpaceManager spaceManager;

    public PageTemplateResource(@ComponentImport PageTemplateManager pageTemplateManager, PluginPageTemplateHelper pageTemplateHelper, ContentTemplateRefManager contentTemplateRefManager, @ComponentImport SpaceManager spaceManager, @ComponentImport PermissionManager permissionManager, @ComponentImport AccessModeService accessModeService) {
        super(permissionManager, spaceManager, accessModeService);
        this.pageTemplateManager = pageTemplateManager;
        this.pageTemplateHelper = pageTemplateHelper;
        this.contentTemplateRefManager = contentTemplateRefManager;
        this.spaceManager = spaceManager;
    }

    @GET
    public PageTemplateForm get(@QueryParam(value="id") Integer id, @QueryParam(value="contentTemplateRefId") UUID contentTemplateRefId) {
        this.checkIds(id, contentTemplateRefId);
        PageTemplate pageTemplate = null;
        if (id != null) {
            pageTemplate = this.getPageTemplate(id);
        } else if (contentTemplateRefId != null) {
            pageTemplate = this.getPageTemplate(contentTemplateRefId);
        }
        if (pageTemplate != null) {
            this.checkViewPermission(pageTemplate);
            return this.build(pageTemplate);
        }
        throw new ResourceException("Either 'id' or 'contentTemplateRefId' parameter should be specified", Response.Status.BAD_REQUEST, ResourceErrorType.PARAMETER_MISSING);
    }

    @POST
    @Consumes(value={"application/json", "application/xml"})
    public long create(PageTemplateForm pageTemplate) {
        this.checkNullEntity(pageTemplate);
        PageTemplate newPageTemplate = this.build(pageTemplate);
        this.checkCreatePermission(newPageTemplate);
        this.pageTemplateManager.savePageTemplate(newPageTemplate, null);
        return newPageTemplate.getId();
    }

    private PageTemplate build(PageTemplateForm pageTemplate) {
        PageTemplate result = new PageTemplate();
        result.setId(pageTemplate.id);
        result.setName(pageTemplate.name);
        result.setDescription(pageTemplate.description);
        result.setBodyType(BodyType.XHTML);
        if (!StringUtils.isEmpty((CharSequence)pageTemplate.spaceKey)) {
            Space space = this.spaceManager.getSpace(pageTemplate.spaceKey);
            result.setSpace(space);
        }
        result.setVersion(pageTemplate.version);
        result.setContent(pageTemplate.content);
        return result;
    }

    private PageTemplateForm build(PageTemplate pageTemplate) {
        PageTemplateForm result = new PageTemplateForm();
        result.id = pageTemplate.getId();
        result.name = pageTemplate.getName();
        result.description = pageTemplate.getDescription();
        Space space = pageTemplate.getSpace();
        if (space != null) {
            result.spaceKey = space.getKey();
        }
        result.version = pageTemplate.getVersion();
        result.content = pageTemplate.getContent();
        return result;
    }

    private void build(PageTemplateForm pageTemplate, PageTemplate originalPageTemplate) {
        if (!StringUtils.isEmpty((CharSequence)pageTemplate.name)) {
            originalPageTemplate.setName(pageTemplate.name);
        }
        if (!StringUtils.isEmpty((CharSequence)pageTemplate.description)) {
            originalPageTemplate.setDescription(pageTemplate.description);
        }
        if (!StringUtils.isEmpty((CharSequence)pageTemplate.content)) {
            originalPageTemplate.setContent(pageTemplate.content);
        }
    }

    @PUT
    @Consumes(value={"application/json", "application/xml"})
    public long update(PageTemplateForm pageTemplate) throws CloneNotSupportedException {
        PageTemplate originalPageTemplate = this.pageTemplateManager.getPageTemplate(pageTemplate.id);
        if (originalPageTemplate == null) {
            throw new ResourceException("Page template doesn't exist already", Response.Status.NOT_FOUND, ResourceErrorType.NOT_FOUND_PAGE_TEMPLATE, (Object)pageTemplate.id);
        }
        this.checkUpdatePermission(originalPageTemplate);
        PageTemplate newPageTemplate = (PageTemplate)originalPageTemplate.clone();
        this.build(pageTemplate, originalPageTemplate);
        this.pageTemplateManager.savePageTemplate(originalPageTemplate, newPageTemplate);
        return newPageTemplate.getId();
    }

    @DELETE
    public void delete(@QueryParam(value="id") Integer id, @QueryParam(value="contentTemplateRefId") UUID contentTemplateRefId) {
        this.checkIds(id, contentTemplateRefId);
        PageTemplate pageTemplate = null;
        if (id != null) {
            pageTemplate = this.getPageTemplate(id);
        }
        if (contentTemplateRefId != null) {
            pageTemplate = this.getPageTemplate(contentTemplateRefId);
        }
        if (pageTemplate == null) {
            throw new ResourceException("The specified page template was not found", Response.Status.NOT_FOUND, ResourceErrorType.NOT_FOUND_PAGE_TEMPLATE);
        }
        this.checkDeletePermission(pageTemplate);
        this.pageTemplateManager.removePageTemplate(pageTemplate);
    }

    private void checkIds(Integer id, UUID contentTemplateRefId) {
        if (id == null && contentTemplateRefId == null) {
            throw new ResourceException("Either 'id' or 'contentTemplateRefId' parameter should be specified", Response.Status.BAD_REQUEST, ResourceErrorType.PARAMETER_MISSING);
        }
        if (id != null && contentTemplateRefId != null) {
            throw new ResourceException("Only one of 'id' or 'contentTemplateRefId' parameters should be specified", Response.Status.BAD_REQUEST, ResourceErrorType.PARAMETER_TOO_MANY);
        }
    }

    private PageTemplate getPageTemplate(UUID contentTemplateRefId) {
        ContentTemplateRef contentTemplateRef = (ContentTemplateRef)this.contentTemplateRefManager.getById(contentTemplateRefId);
        if (contentTemplateRef == null) {
            throw new ResourceException("The specified ContentTemplateRef was not found", Response.Status.NOT_FOUND, ResourceErrorType.NOT_FOUND_CONTENT_TEMPLATE_REF, (Object)contentTemplateRefId);
        }
        return this.pageTemplateHelper.getPageTemplate(contentTemplateRef);
    }

    private PageTemplate getPageTemplate(Integer id) {
        return this.pageTemplateManager.getPageTemplate((long)id.intValue());
    }

    private void checkDeletePermission(@Nonnull PageTemplate pageTemplate) {
        this.checkPermission(pageTemplate, Permission.ADMINISTER, "You are not permitted to delete page template ");
    }

    private void checkUpdatePermission(@Nonnull PageTemplate pageTemplate) {
        this.checkPermission(pageTemplate, Permission.ADMINISTER, "You are not permitted to update page template ");
    }

    private void checkCreatePermission(@Nonnull PageTemplate pageTemplate) {
        this.checkPermission(pageTemplate, Permission.ADMINISTER, "You are not permitted to create page template ");
    }

    private void checkViewPermission(@Nonnull PageTemplate pageTemplate) {
        this.checkPermission(pageTemplate, Permission.VIEW, "You are not permitted to view page template ");
    }

    private void checkPermission(@Nonnull PageTemplate pageTemplate, Permission permission, String errorMessage) {
        Object target;
        if (this.accessModeService.isReadOnlyAccessModeEnabled() && !permission.equals((Object)Permission.VIEW)) {
            throw new ReadOnlyException();
        }
        Space space = pageTemplate.getSpace();
        Object object = target = space != null ? space : PermissionManager.TARGET_APPLICATION;
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), permission, target)) {
            throw new ResourceException(errorMessage + pageTemplate.getId(), Response.Status.FORBIDDEN, AuthenticatedUserThreadLocal.get() == null ? ResourceErrorType.PERMISSION_ANONYMOUS_CREATE : ResourceErrorType.PERMISSION_USER_CREATE, (Object)(space != null ? space.getKey() : null));
        }
    }
}

