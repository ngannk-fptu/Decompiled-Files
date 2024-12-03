/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.FormatConverter
 *  com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.service.CommentService
 *  com.atlassian.confluence.content.service.comment.CommentCommand
 *  com.atlassian.confluence.content.service.comment.CreateCommentCommand
 *  com.atlassian.confluence.content.service.comment.EditCommentCommand
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.HeartbeatManager
 *  com.atlassian.confluence.core.OperationContext
 *  com.atlassian.confluence.core.service.NotAuthorizedException
 *  com.atlassian.confluence.core.service.NotValidException
 *  com.atlassian.confluence.diff.Differ
 *  com.atlassian.confluence.event.events.content.comment.CommentCreateEvent
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.DraftsTransitionHelper
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.security.CaptchaManager
 *  com.atlassian.confluence.security.ContentPermissionSet
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.CollaborativeEditingHelper
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.UserChecker
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.plugins.rest.common.security.RequiresXsrfCheck
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.user.User
 *  com.atlassian.xwork.XsrfTokenGenerator
 *  com.google.common.base.Preconditions
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.FormParam
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.tinymceplugin.rest;

import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.service.CommentService;
import com.atlassian.confluence.content.service.comment.CommentCommand;
import com.atlassian.confluence.content.service.comment.CreateCommentCommand;
import com.atlassian.confluence.content.service.comment.EditCommentCommand;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.HeartbeatManager;
import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.core.service.NotValidException;
import com.atlassian.confluence.diff.Differ;
import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.confluence.tinymceplugin.rest.captcha.CaptchaCheckFailedException;
import com.atlassian.confluence.tinymceplugin.rest.captcha.CaptchaResourceFilter;
import com.atlassian.confluence.tinymceplugin.service.CommentRenderService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.plugins.rest.common.security.RequiresXsrfCheck;
import com.atlassian.renderer.RenderContext;
import com.atlassian.user.User;
import com.atlassian.xwork.XsrfTokenGenerator;
import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/content/{id}")
public class PageResource {
    private static final Logger log = LoggerFactory.getLogger(PageResource.class);
    private final CommentService commentService;
    private final NotificationManager notificationManager;
    private final CommentRenderService commentRenderService;
    private final CaptchaManager captchaManager;
    private final FormatConverter editConverter;
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final XsrfTokenGenerator tokenGenerator;
    private final DraftsTransitionHelper draftsTransitionHelper;
    private final HeartbeatManager heartbeatManager;
    private final Differ differ;
    private final UserChecker userChecker;
    private final CollaborativeEditingHelper collaborativeEditingHelper;
    private final EventPublisher eventPublisher;
    private final StorageFormatCleaner storageFormatCleaner;
    private final LicenseService licenseService;

    public PageResource(@ComponentImport CommentService commentService, @ComponentImport NotificationManager notificationManager, CommentRenderService commentRenderService, @ComponentImport CaptchaManager captchaManager, @ComponentImport FormatConverter converter, @ComponentImport PageManager pageManager, @ComponentImport PermissionManager permissionManager, @ComponentImport XsrfTokenGenerator tokenGenerator, @ComponentImport DraftsTransitionHelper draftsTransitionHelper, @ComponentImport HeartbeatManager heartbeatManager, @ComponentImport Differ differ, @ComponentImport UserChecker userChecker, @ComponentImport CollaborativeEditingHelper collaborativeEditingHelper, @ComponentImport EventPublisher eventPublisher, @ComponentImport StorageFormatCleaner storageFormatCleaner, @ComponentImport LicenseService licenseService) {
        this.commentService = Objects.requireNonNull(commentService);
        this.notificationManager = Objects.requireNonNull(notificationManager);
        this.commentRenderService = Objects.requireNonNull(commentRenderService);
        this.captchaManager = Objects.requireNonNull(captchaManager);
        this.editConverter = Objects.requireNonNull(converter);
        this.pageManager = Objects.requireNonNull(pageManager);
        this.permissionManager = Objects.requireNonNull(permissionManager);
        this.tokenGenerator = Objects.requireNonNull(tokenGenerator);
        this.draftsTransitionHelper = Objects.requireNonNull(draftsTransitionHelper);
        this.heartbeatManager = Objects.requireNonNull(heartbeatManager);
        this.differ = Objects.requireNonNull(differ);
        this.userChecker = Objects.requireNonNull(userChecker);
        this.collaborativeEditingHelper = Objects.requireNonNull(collaborativeEditingHelper);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.storageFormatCleaner = Objects.requireNonNull(storageFormatCleaner);
        this.licenseService = Objects.requireNonNull(licenseService);
    }

    @GET
    @Path(value="/draft/diff")
    @AnonymousAllowed
    public Response getDraftDiff(@PathParam(value="id") Long contentId) {
        AbstractPage currentPage = this.pageManager.getAbstractPage(contentId.longValue());
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.EDIT, (Object)currentPage)) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        ContentEntityObject draftPage = this.draftsTransitionHelper.getDraftForPage(currentPage);
        String bodyDiff = this.differ.diff((ContentEntityObject)currentPage, draftPage);
        return Response.status((Response.Status)Response.Status.OK).entity((Object)this.replacePlaceholders(bodyDiff)).build();
    }

    @GET
    @AnonymousAllowed
    public Response getEditorContent(@PathParam(value="id") Long contentId) {
        List usersForActivity;
        AbstractPage page;
        AbstractPage content = page = this.pageManager.getAbstractPage(contentId.longValue());
        if (page == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.EDIT, (Object)page) || this.hasInvalidLicense()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        String activityId = contentId + page.getType();
        try {
            this.heartbeatManager.startActivity(activityId, (User)user);
        }
        catch (RuntimeException e) {
            log.error("Failed to start activity for key {}", (Object)activityId);
            return Response.serverError().build();
        }
        try {
            usersForActivity = this.heartbeatManager.getUsersForActivity(activityId);
        }
        catch (RuntimeException e) {
            log.error("Failed to get users for activity with key {}", (Object)activityId);
            return Response.serverError().build();
        }
        if (this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(page.getSpaceKey()) && this.collaborativeEditingHelper.isOverLimit(usersForActivity.size())) {
            try {
                this.heartbeatManager.stopActivity(activityId, (User)user);
            }
            catch (Exception e) {
                log.error("Error stopping heartbeat activity", (Throwable)e);
            }
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        PageContext context = content.toPageContext();
        String editFormat = this.editConverter.convertToEditorFormat(this.storageFormatCleaner.cleanQuietly(content.getBodyAsString()), (RenderContext)context);
        HashMap<String, Object> returnVal = new HashMap<String, Object>();
        returnVal.put("title", content.getTitle());
        returnVal.put("editorContent", editFormat);
        returnVal.put("pageVersion", String.valueOf(page.getVersion()));
        returnVal.put("atlToken", this.tokenGenerator.generateToken(ServletContextThreadLocal.getRequest()));
        String syncRev = content.getSynchronyRevision();
        returnVal.put("syncRev", syncRev != null ? syncRev : "dummy-sync-rev");
        returnVal.put("confRev", page.getConfluenceRevision());
        returnVal.put("syncRevSource", page.getSynchronyRevisionSource());
        returnVal.put("editMode", this.draftsTransitionHelper.getEditMode(page.getSpaceKey()));
        HashMap<String, String> permissions = new HashMap<String, String>();
        String separator = ",";
        ContentPermissionSet readPermissions = page.getContentPermissionSet("View");
        String viewPermissionsUsers = "";
        String viewPermissionsGroups = "";
        if (readPermissions != null) {
            viewPermissionsUsers = StringUtils.join((Iterable)readPermissions.getUserNames(), (String)",");
            viewPermissionsGroups = StringUtils.join((Iterable)readPermissions.getGroupNames(), (String)",");
        }
        permissions.put("viewPermissionsUsers", viewPermissionsUsers);
        permissions.put("viewPermissionsGroups", viewPermissionsGroups);
        ContentPermissionSet editPermissions = page.getContentPermissionSet("Edit");
        String editPermissionsUsers = "";
        String editPermissionsGroups = "";
        if (editPermissions != null) {
            editPermissionsUsers = StringUtils.join((Iterable)editPermissions.getUserNames(), (String)",");
            editPermissionsGroups = StringUtils.join((Iterable)editPermissions.getGroupNames(), (String)",");
        }
        permissions.put("editPermissionsUsers", editPermissionsUsers);
        permissions.put("editPermissionsGroups", editPermissionsGroups);
        returnVal.put("permissions", permissions);
        return Response.ok(returnVal).build();
    }

    @POST
    @Path(value="/comment")
    @AnonymousAllowed
    @RequiresXsrfCheck
    public Response add(@PathParam(value="id") Long contentId, @FormParam(value="html") String html, @FormParam(value="watch") boolean watch, @QueryParam(value="actions") @DefaultValue(value="false") boolean actions, @FormParam(value="uuid") @DefaultValue(value="") String uuid, @Context HttpServletRequest req) {
        return this.add(contentId, 0L, html, watch, actions, uuid, req);
    }

    @POST
    @Path(value="/comments/{parentCommentId}/comment")
    @AnonymousAllowed
    @RequiresXsrfCheck
    public Response add(@PathParam(value="id") Long contentId, @PathParam(value="parentCommentId") Long parentCommentId, @FormParam(value="html") String html, @FormParam(value="watch") boolean watch, @QueryParam(value="actions") @DefaultValue(value="false") boolean actions, @FormParam(value="uuid") @DefaultValue(value="") String uuid, @Context HttpServletRequest req) {
        UUID commentUuid;
        if (this.hasInvalidLicense()) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        try {
            this.checkCaptcha(req);
        }
        catch (CaptchaCheckFailedException ex) {
            return ex.getResponse();
        }
        try {
            commentUuid = UUID.fromString(uuid);
        }
        catch (IllegalArgumentException e) {
            log.debug("Invalid comment UUID received, generating random UUID.");
            commentUuid = UUID.randomUUID();
        }
        if (contentId == null || parentCommentId == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        CreateCommentCommand command = this.commentService.newCreateCommentFromEditorCommand(contentId.longValue(), parentCommentId.longValue(), html, commentUuid);
        Response response = this.createOrEditAndRender((CommentCommand)command, watch, actions, req);
        this.eventPublisher.publish((Object)new CommentCreateEvent((Object)this, command.getComment(), (OperationContext)DefaultSaveContext.DEFAULT));
        return response;
    }

    @POST
    @Path(value="/comments/{commentId}")
    @AnonymousAllowed
    @RequiresXsrfCheck
    public Response edit(@PathParam(value="commentId") Long commentId, @FormParam(value="html") String html, @FormParam(value="watch") boolean watch, @QueryParam(value="actions") @DefaultValue(value="false") boolean actions, @Context HttpServletRequest req) {
        if (this.hasInvalidLicense()) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        try {
            this.checkCaptcha(req);
        }
        catch (CaptchaCheckFailedException ex) {
            return ex.getResponse();
        }
        EditCommentCommand command = this.commentService.newEditCommentFromEditorCommand(commentId.longValue(), html);
        return this.createOrEditAndRender((CommentCommand)command, watch, actions, req);
    }

    private boolean hasInvalidLicense() {
        return this.licenseService.retrieve().isExpired() || this.userChecker.hasTooManyUsers();
    }

    private void checkCaptcha(HttpServletRequest req) throws CaptchaCheckFailedException {
        new CaptchaResourceFilter(this.captchaManager).filter(req);
    }

    private Response createOrEditAndRender(CommentCommand command, boolean watch, boolean actions, @Context HttpServletRequest req) {
        Comment comment;
        try {
            if (!command.isAuthorized()) {
                throw new NotAuthorizedException(null);
            }
            if (!command.isValid()) {
                throw new NotValidException();
            }
            command.execute();
            comment = command.getComment();
        }
        catch (NotAuthorizedException ex) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        catch (NotValidException ex) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        ContentEntityObject container = comment.getContainer();
        Preconditions.checkState((container != null ? 1 : 0) != 0, (Object)"Comments must have a container");
        if (user != null && watch) {
            this.notificationManager.addContentNotification((User)user, container);
        }
        try {
            return Response.ok((Object)this.commentRenderService.render(comment, actions, req)).build();
        }
        catch (XMLStreamException ex) {
            String message = "Failed to read the comment storage format for comment id=" + comment.getId() + " on content id=" + container.getId();
            if (log.isDebugEnabled()) {
                log.debug(message + " for storage content=" + comment.getBodyAsString());
            } else {
                log.warn(message);
            }
        }
        catch (XhtmlException ex) {
            String message = "Exception while rendering the comment id=" + comment.getId() + " on content id=" + container.getId();
            if (log.isDebugEnabled()) {
                log.debug(message + " for storage content=" + comment.getBodyAsString(), (Throwable)ex);
            }
            log.warn(message, (Throwable)ex);
        }
        return Response.serverError().build();
    }

    private String replacePlaceholders(String diff) {
        return diff.replaceAll("<p class=\"diff-context-placeholder\">\\.\\.\\.</p>", "<p class=\"diff-context-placeholder\"></p>");
    }
}

