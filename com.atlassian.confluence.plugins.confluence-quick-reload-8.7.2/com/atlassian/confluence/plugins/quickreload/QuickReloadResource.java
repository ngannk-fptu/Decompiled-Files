/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.setup.settings.Settings
 *  com.atlassian.confluence.tinymceplugin.rest.entities.CommentResult
 *  com.atlassian.confluence.tinymceplugin.rest.entities.CommentResultWithActions$CommentResultWithActionsBuilder
 *  com.atlassian.confluence.tinymceplugin.service.CommentRenderService
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UnknownUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.actions.ProfilePictureInfo
 *  com.atlassian.confluence.util.RequestCacheThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.user.User
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.quickreload;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.quickreload.CommentAndUserResult;
import com.atlassian.confluence.plugins.quickreload.Commenter;
import com.atlassian.confluence.plugins.quickreload.PageResult;
import com.atlassian.confluence.plugins.quickreload.QuickReloadCaches;
import com.atlassian.confluence.plugins.quickreload.ReadOnlyModeResult;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.tinymceplugin.rest.entities.CommentResult;
import com.atlassian.confluence.tinymceplugin.rest.entities.CommentResultWithActions;
import com.atlassian.confluence.tinymceplugin.service.CommentRenderService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UnknownUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.user.User;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.xml.stream.XMLStreamException;

@Path(value="/{pageId}")
@Produces(value={"application/json"})
public class QuickReloadResource {
    private final CommentManager commentManager;
    private final CommentRenderService commentRenderer;
    private final UserAccessor userAccessor;
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final QuickReloadCaches caches;
    private final AccessModeService accessModeService;
    private final GlobalSettingsManager settingsManager;
    private final LicenseService licenseService;
    private final AttachmentManager attachmentManager;
    private static final int MAX_MESSAGE_LENGTH = 30;
    private static final Commenter user = new Commenter("joesmith", "Joe Smith", "/images/avatar.png");
    private static final ImmutableMap<String, Object> SAMPLE = ImmutableMap.of((Object)"comments", (Object)new CommentAndUserResult(user, (CommentResult)new CommentResultWithActions.CommentResultWithActionsBuilder(1234L, "example comment", 5678L, 1357L, true).build()), (Object)"page", (Object)new PageResult(user));

    public QuickReloadResource(@ComponentImport CommentManager commentManager, @ComponentImport CommentRenderService commentRenderer, @ComponentImport UserAccessor userAccessor, @ComponentImport PageManager pageManager, @ComponentImport PermissionManager permissionManager, @ComponentImport I18NBeanFactory i18NBeanFactory, QuickReloadCaches caches, @ComponentImport AccessModeService accessModeService, @ComponentImport GlobalSettingsManager settingsManager, @ComponentImport LicenseService licenseService, @ComponentImport AttachmentManager attachmentManager) {
        this.commentManager = commentManager;
        this.commentRenderer = commentRenderer;
        this.userAccessor = userAccessor;
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.caches = caches;
        this.accessModeService = accessModeService;
        this.settingsManager = settingsManager;
        this.licenseService = licenseService;
        this.attachmentManager = attachmentManager;
    }

    @GET
    @AnonymousAllowed
    public Response getAll(@Context HttpServletRequest request, @PathParam(value="pageId") Long pageId, @QueryParam(value="since") Long since) {
        if (since == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        if (pageId == 0L) {
            return Response.ok((Object)ImmutableMap.of((Object)"readOnlyMode", (Object)this.getReadOnlyMode(), (Object)"time", (Object)System.currentTimeMillis())).build();
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        AbstractPage page = this.pageManager.getAbstractPage(pageId.longValue());
        if (page == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)page)) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        String username = user != null ? user.getName() : "";
        int attachments = this.attachmentManager.countLatestVersionsOfAttachmentsOnPageSince((ContentEntityObject)page, new Date(since));
        return Response.ok((Object)ImmutableMap.of((Object)"readOnlyMode", (Object)this.getReadOnlyMode(), (Object)"comments", this.getComments(request, pageId, since), (Object)"page", (Object)MoreObjects.firstNonNull((Object)this.getPage(page, since, username), (Object)""), (Object)"attachmentsCount", (Object)attachments, (Object)"time", (Object)System.currentTimeMillis())).build();
    }

    private ReadOnlyModeResult getReadOnlyMode() {
        Settings globalSettings = this.settingsManager.getGlobalSettings();
        return new ReadOnlyModeResult(this.accessModeService.isReadOnlyAccessModeEnabled(), globalSettings.isMaintenanceBannerMessageOn() && this.licenseService.isLicensedForDataCenterOrExempt(), globalSettings.getMaintenanceBannerMessage());
    }

    private PageResult getPage(AbstractPage page, Long since, String username) {
        if (page != null) {
            if (Boolean.valueOf(page.getProperties().getStringProperty("SUPPRESS_NOTIFICATION_" + page.getVersion())).booleanValue()) {
                return null;
            }
            this.caches.updateLastUpdate(page.getId(), page.getLastModificationDate().getTime());
            ConfluenceUser lastModifier = page.getLastModifier();
            String lastModifierName = lastModifier != null ? lastModifier.getName() : null;
            return page.getLastModificationDate().getTime() > since && !username.equals(lastModifierName) ? new PageResult(this.getCommenter(lastModifier)) : null;
        }
        return null;
    }

    private List<CommentAndUserResult> getComments(HttpServletRequest request, Long pageId, Long since) {
        List comments = this.commentManager.getPageComments(pageId.longValue(), new Date(since));
        long lastUpdate = Long.MIN_VALUE;
        for (Comment comment2 : comments) {
            long commentModified = comment2.getLastModificationDate().getTime();
            if (lastUpdate >= commentModified) continue;
            lastUpdate = commentModified;
        }
        this.caches.updateLastUpdate(pageId, lastUpdate);
        return Lists.transform((List)comments, comment -> {
            try {
                int maxMessageLength = comment.isInlineComment() ? 30 : 0;
                ConfluenceUser creator = comment.getCreator();
                boolean asPlainText = comment.isInlineComment();
                return new CommentAndUserResult(this.getCommenter(creator), this.commentRenderer.render(comment, true, request, maxMessageLength, asPlainText));
            }
            catch (XhtmlException | XMLStreamException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Commenter getCommenter(ConfluenceUser user) {
        String name;
        String string = name = user != null ? user.getName() : null;
        if (name != null) {
            ConfluenceUser accessedUser = this.userAccessor.getUserByName(name);
            if (accessedUser == null) {
                accessedUser = UnknownUser.unknownUser((ConfluenceUser)user, (I18NBean)this.i18NBeanFactory.getI18NBean());
            }
            ProfilePictureInfo pictureInfo = this.userAccessor.getUserProfilePicture((User)accessedUser);
            return new Commenter(accessedUser.getName(), accessedUser.getFullName(), pictureInfo.getUriReference());
        }
        return new Commenter("", "Anonymous", RequestCacheThreadLocal.getContextPath() + "/images/icons/profilepics/anonymous.svg");
    }
}

