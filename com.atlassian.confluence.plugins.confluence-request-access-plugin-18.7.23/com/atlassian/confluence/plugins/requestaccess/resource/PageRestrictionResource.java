/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContentPermissionManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.ContentPermission
 *  com.atlassian.confluence.security.ContentPermissionSet
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.requestaccess.resource;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.requestaccess.entity.PermissionCheck;
import com.atlassian.confluence.plugins.requestaccess.entity.UserEntities;
import com.atlassian.confluence.plugins.requestaccess.entity.UserEntity;
import com.atlassian.confluence.plugins.requestaccess.events.AccessGrantedEvent;
import com.atlassian.confluence.plugins.requestaccess.events.AccessRequestedEvent;
import com.atlassian.confluence.plugins.requestaccess.service.UserNotificationService;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path(value="/page/restriction")
@Component
public class PageRestrictionResource {
    private static final Logger log = LoggerFactory.getLogger(PageRestrictionResource.class);
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final UserAccessor userAccessor;
    private final ContentPermissionManager contentPermissionManager;
    private final UserNotificationService recipientNotificationService;
    private final EventPublisher eventPublisher;
    private final MailServerManager mailServerManager;

    @Autowired
    public PageRestrictionResource(@ComponentImport PageManager pageManager, @ComponentImport PermissionManager permissionManager, @ComponentImport UserAccessor userAccessor, @ComponentImport ContentPermissionManager contentPermissionManager, UserNotificationService recipientNotificationService, @ComponentImport EventPublisher eventPublisher, @ComponentImport MailServerManager mailServerManager) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.userAccessor = userAccessor;
        this.contentPermissionManager = contentPermissionManager;
        this.recipientNotificationService = recipientNotificationService;
        this.eventPublisher = eventPublisher;
        this.mailServerManager = mailServerManager;
    }

    @GET
    @Path(value="/{pageId}/check/{type:view|edit}")
    @Produces(value={"application/json"})
    public Response checkPermission(@PathParam(value="pageId") long pageId, @PathParam(value="type") String accessTypeString, @QueryParam(value="username") String username) {
        ConfluenceUser authenticatedUser = AuthenticatedUserThreadLocal.get();
        log.info("Check the user {} permission to the pageId {}", (Object)authenticatedUser, (Object)pageId);
        if (!this.permissionManager.hasPermission((User)authenticatedUser, Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((int)403).build();
        }
        AbstractPage page = this.pageManager.getAbstractPage(pageId);
        if (page == null || page.isDeleted()) {
            return Response.status((int)404).build();
        }
        if (!this.permissionManager.hasPermission((User)authenticatedUser, Permission.VIEW, (Object)page)) {
            return Response.status((int)403).build();
        }
        ConfluenceUser targetUser = this.userAccessor.getUserByName(username);
        AccessType accessType = AccessType.valueOf(accessTypeString.toUpperCase());
        boolean hasPermission = this.permissionManager.hasPermission((User)targetUser, accessType.getPermission(), (Object)page);
        return Response.ok((Object)new PermissionCheck(hasPermission)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @POST
    @Path(value="/{pageId}/request/{type:view|edit}")
    @Produces(value={"application/json"})
    public Response request(@PathParam(value="pageId") long pageId, @PathParam(value="type") String accessTypeString) {
        ConfluenceUser authenticatedUser = AuthenticatedUserThreadLocal.get();
        log.info("User {} has requested access to pageId {}", (Object)authenticatedUser, (Object)pageId);
        if (!this.permissionManager.hasPermission((User)authenticatedUser, Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((int)403).build();
        }
        AbstractPage page = this.pageManager.getAbstractPage(pageId);
        if (page == null || page.isDeleted()) {
            return Response.status((int)404).build();
        }
        if (!this.mailServerManager.isDefaultSMTPMailServerDefined()) {
            return Response.status((int)412).build();
        }
        LinkedHashSet<ConfluenceUser> notificationRecipients = this.recipientNotificationService.findRequestAccessRecipient(page);
        if (notificationRecipients.isEmpty()) {
            log.warn("No recipient for the request access notification was found", (Object)authenticatedUser, (Object)pageId);
            return Response.status((int)412).build();
        }
        AccessType accessType = AccessType.valueOf(accessTypeString.toUpperCase());
        notificationRecipients.stream().map(recipient -> new AccessRequestedEvent(authenticatedUser, (ConfluenceUser)recipient, (ContentEntityObject)page, accessType, page.getSpaceKey())).forEach(arg_0 -> ((EventPublisher)this.eventPublisher).publish(arg_0));
        List<UserEntity> entities = notificationRecipients.stream().map(UserEntity::new).collect(Collectors.toList());
        return Response.ok((Object)new UserEntities(entities)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @POST
    @Path(value="/{pageId}/grant/{type:view|edit}")
    @Consumes(value={"application/json"})
    @Produces(value={"text/html"})
    public Response grant(@PathParam(value="pageId") long pageId, @PathParam(value="type") String accessTypeString, String username) {
        ConfluenceUser authenticatedUser = AuthenticatedUserThreadLocal.get();
        log.info("User {} is requesting access for user {} to page {}", new Object[]{authenticatedUser, username, pageId});
        if (StringUtils.isBlank((CharSequence)username)) {
            return Response.status((int)400).build();
        }
        AbstractPage page = this.pageManager.getAbstractPage(pageId);
        if (page == null) {
            return Response.status((int)404).build();
        }
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        if (user == null) {
            return Response.status((int)404).build();
        }
        if (!this.permissionManager.hasPermission((User)authenticatedUser, Permission.SET_PERMISSIONS, (Object)page)) {
            return Response.status((int)403).build();
        }
        AccessType accessType = AccessType.valueOf(accessTypeString.toUpperCase());
        try {
            ContentPermissionSet contentPermissionSet = page.getContentPermissionSet(accessType.getPermissionName());
            if (contentPermissionSet != null && contentPermissionSet.isPermitted((User)user)) {
                return Response.status((int)202).build();
            }
            this.addPageRestriction(page, user, accessType);
            if (!this.mailServerManager.isDefaultSMTPMailServerDefined()) {
                return Response.status((int)412).build();
            }
            this.eventPublisher.publish((Object)new AccessGrantedEvent(authenticatedUser, user, (ContentEntityObject)page, accessType, page.getSpaceKey()));
        }
        catch (Exception e) {
            log.error("Error when adding permissions to user {} for page {}", new Object[]{authenticatedUser, pageId, e});
            return Response.status((int)500).build();
        }
        return Response.status((int)200).build();
    }

    private void addPageRestriction(AbstractPage page, ConfluenceUser user, AccessType accessType) {
        ContentPermission viewContentPermission = ContentPermission.createUserPermission((String)accessType.getPermissionName(), (ConfluenceUser)user);
        this.contentPermissionManager.addContentPermission(viewContentPermission, (ContentEntityObject)page);
    }

    public static enum AccessType {
        VIEW(Permission.VIEW, "View"),
        EDIT(Permission.EDIT, "Edit");

        private final Permission permission;
        private final String permissionName;

        private AccessType(Permission permission, String permissionName) {
            this.permissionName = permissionName;
            this.permission = permission;
        }

        public Permission getPermission() {
            return this.permission;
        }

        public String getPermissionName() {
            return this.permissionName;
        }
    }
}

