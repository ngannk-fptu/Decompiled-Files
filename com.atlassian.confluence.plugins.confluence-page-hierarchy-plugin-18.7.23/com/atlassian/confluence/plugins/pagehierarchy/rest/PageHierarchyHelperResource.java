/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.people.Anonymous
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.permissions.Operation
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.api.model.permissions.Target
 *  com.atlassian.confluence.api.model.permissions.Target$IdTarget
 *  com.atlassian.confluence.api.model.permissions.Target$ModelObjectTarget
 *  com.atlassian.confluence.api.model.permissions.TargetType
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.confluence.api.service.permissions.OperationService
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.links.LinkManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.pages.actions.beans.PageIncomingLinks
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.pagehierarchy.rest;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.people.Anonymous;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.permissions.Operation;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.permissions.Target;
import com.atlassian.confluence.api.model.permissions.TargetType;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.api.service.permissions.OperationService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.links.LinkManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.beans.PageIncomingLinks;
import com.atlassian.confluence.plugins.pagehierarchy.validation.DeletePageHierarchyValidator;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@ExperimentalApi
@AnonymousAllowed
@Path(value="/internal")
@Consumes(value={"application/json"})
@Produces(value={"application/json;charset=UTF-8"})
@Internal
public final class PageHierarchyHelperResource {
    private final SpaceManager spaceManager;
    private final SpacePermissionManager spacePermissionManager;
    private final PermissionManager permissionManager;
    private final LinkManager linkManager;
    private final PageManager pageManager;
    private final PersonService personService;
    private final OperationService operationService;
    private final AccessModeService accessModeService;
    private final AttachmentManager attachmentManager;

    public PageHierarchyHelperResource(@ConfluenceImport SpaceManager spaceManager, @ConfluenceImport SpacePermissionManager spacePermissionManager, @ConfluenceImport PermissionManager permissionManager, @ConfluenceImport LinkManager linkManager, @ConfluenceImport PageManager pageManager, @ConfluenceImport PersonService personService, @ConfluenceImport OperationService operationService, @ConfluenceImport AccessModeService accessModeService, @ConfluenceImport AttachmentManager attachmentManager) {
        this.spaceManager = Objects.requireNonNull(spaceManager);
        this.spacePermissionManager = Objects.requireNonNull(spacePermissionManager);
        this.permissionManager = Objects.requireNonNull(permissionManager);
        this.linkManager = Objects.requireNonNull(linkManager);
        this.pageManager = Objects.requireNonNull(pageManager);
        this.personService = personService;
        this.operationService = operationService;
        this.accessModeService = Objects.requireNonNull(accessModeService);
        this.attachmentManager = attachmentManager;
    }

    @GET
    @Path(value="/incomingLinkCount/{pageId}")
    public Response getIncomingLinkCount(@PathParam(value="pageId") long pageId) {
        Page page = this.pageManager.getPage(pageId);
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        this.validate((AbstractPage)page, user);
        return Response.ok((Object)ImmutableMap.builder().put((Object)"count", (Object)new PageIncomingLinks(this.linkManager, this.permissionManager).getIncomingLinks((AbstractPage)page, (User)user).size()).build()).build();
    }

    @GET
    @Path(value="/attachmentCount/{pageId}")
    public Response getAttachmentCount(@PathParam(value="pageId") long pageId) {
        Page page = this.pageManager.getPage(pageId);
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        this.validate((AbstractPage)page, user);
        return Response.ok((Object)ImmutableMap.builder().put((Object)"count", (Object)this.attachmentManager.countLatestVersionsOfAttachments((ContentEntityObject)page)).build()).build();
    }

    private void validate(AbstractPage page, ConfluenceUser user) {
        if (page != null && !this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)page)) {
            throw new PermissionException();
        }
        if (page == null) {
            throw new NotFoundException();
        }
    }

    @GET
    @Path(value="/spacePermissions/{spaceKey}")
    public Response getSpacePermissions(@PathParam(value="spaceKey") String spaceKey) {
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        return Response.ok(this.hasSpacePermissions(currentUser, space)).build();
    }

    private Map<String, Boolean> hasSpacePermissions(@Nullable ConfluenceUser user, @Nonnull Space space) {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        builder.put((Object)"readOnly", (Object)this.accessModeService.isReadOnlyAccessModeEnabled());
        boolean hasCreatePagePermission = this.spacePermissionManager.hasPermission("EDITSPACE", space, (User)user);
        boolean hasCreateAttachmentPermission = this.spacePermissionManager.hasPermission("CREATEATTACHMENT", space, (User)user);
        boolean hasPagePermissionsPermission = this.spacePermissionManager.hasPermission("SETPAGEPERMISSIONS", space, (User)user);
        boolean isSystemAdmin = this.permissionManager.isSystemAdministrator((User)user);
        boolean isSpaceAdmin = this.spacePermissionManager.hasPermission("SETSPACEPERMISSIONS", space, (User)user);
        boolean hasDeleteOwnPermission = this.spacePermissionManager.hasPermission("REMOVEOWNCONTENT", space, (User)user);
        boolean hasDeletePagePermission = this.spacePermissionManager.hasPermission("REMOVEPAGE", space, (User)user);
        builder.put((Object)"pages", (Object)hasCreatePagePermission);
        builder.put((Object)"attachments", (Object)hasCreateAttachmentPermission);
        builder.put((Object)"restrictions", (Object)hasPagePermissionsPermission);
        builder.put((Object)"systemAdmin", (Object)isSystemAdmin);
        builder.put((Object)"spaceAdmin", (Object)isSpaceAdmin);
        builder.put((Object)"deleteOwn", (Object)hasDeleteOwnPermission);
        builder.put((Object)"deletePages", (Object)hasDeletePagePermission);
        return builder.build();
    }

    @GET
    @Path(value="/subtreeCount/{pageId}")
    public Response getSubtreeCount(@PathParam(value="pageId") long pageId, @QueryParam(value="totalCountOnly") boolean totalCountOnly) {
        Page page = this.pageManager.getPage(pageId);
        if (page == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put((Object)"totalCount", (Object)this.pageManager.countPagesInSubtree(page));
        builder.put((Object)"maximum", (Object)DeletePageHierarchyValidator.MAX_PAGES);
        if (totalCountOnly) {
            return Response.ok((Object)builder.build()).build();
        }
        ImmutableSet.Builder targetIds = ImmutableSet.builder();
        int restrictedCount = this.getRestrictedPagesCount(currentUser, page, (ImmutableSet.Builder<Long>)targetIds);
        builder.put((Object)"targetIds", (Object)targetIds.build());
        builder.put((Object)"restrictedCount", (Object)restrictedCount);
        return Response.ok((Object)builder.build()).build();
    }

    private int getRestrictedPagesCount(@Nullable ConfluenceUser user, @Nonnull Page page, ImmutableSet.Builder<Long> accessibleIds) {
        Object checkedPerson = user == null ? new Anonymous(null, null) : (Person)this.personService.find(new Expansion[0]).withUserKey(user.getKey()).fetchOne().getOrThrow(() -> new BadRequestException("Could not get Person object from user name :" + user.getName()));
        ArrayList descendantPageIds = Lists.newArrayList((Iterable)Iterables.concat((Iterable)this.pageManager.getDescendantIds(page), (Iterable)Lists.newArrayList((Object[])new Long[]{page.getId()})));
        List descendantPageContentIds = descendantPageIds.stream().map(id -> ContentId.of((ContentType)ContentType.PAGE, (long)id)).collect(Collectors.toList());
        List targets = descendantPageContentIds.stream().map(contentId -> Target.forContentId((ContentId)contentId, (TargetType)TargetType.PAGE)).collect(Collectors.toList());
        Map result = this.operationService.canPerform((Person)checkedPerson, (Operation)OperationKey.DELETE, targets);
        long count = result.entrySet().stream().filter(entry -> !((ValidationResult)entry.getValue()).isSuccessful()).count();
        result.entrySet().stream().filter(entry -> ((ValidationResult)entry.getValue()).isSuccessful()).forEach(entry -> {
            Object content;
            Target target = (Target)entry.getKey();
            if (target instanceof Target.IdTarget) {
                accessibleIds.add((Object)((Target.IdTarget)entry.getKey()).getId().asLong());
            } else if (target instanceof Target.ModelObjectTarget && (content = ((Target.ModelObjectTarget)target).getModelObject()) instanceof Content) {
                accessibleIds.add((Object)((Content)content).getId().asLong());
            }
        });
        return Math.toIntExact(count);
    }
}

