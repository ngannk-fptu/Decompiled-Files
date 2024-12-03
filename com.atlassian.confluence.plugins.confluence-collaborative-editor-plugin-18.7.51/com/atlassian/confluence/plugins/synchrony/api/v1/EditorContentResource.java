/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.relations.CollaboratorRelationDescriptor
 *  com.atlassian.confluence.api.model.relations.Relatable
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.atlassian.confluence.api.model.relations.TouchedRelationDescriptor
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ConflictException
 *  com.atlassian.confluence.api.service.relations.RelationService
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.DraftsTransitionHelper
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.fugue.Either
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  com.google.common.base.Strings
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.synchrony.api.v1;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.relations.CollaboratorRelationDescriptor;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.model.relations.TouchedRelationDescriptor;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ConflictException;
import com.atlassian.confluence.api.service.relations.RelationService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.synchrony.model.SynchronyError;
import com.atlassian.confluence.plugins.synchrony.service.SynchronyContentService;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Either;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import com.google.common.base.Strings;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import net.minidev.json.JSONObject;
import org.codehaus.jackson.annotate.JsonCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value="/content")
@Produces(value={"application/json"})
public class EditorContentResource {
    private static final Logger log = LoggerFactory.getLogger(EditorContentResource.class);
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final DraftsTransitionHelper draftsTransitionHelper;
    private final RelationService relationService;
    private final TransactionTemplate transactionTemplate;
    private final SynchronyContentService synchronyContentService;

    @Autowired
    public EditorContentResource(@ComponentImport PageManager pageManager, @ComponentImport PermissionManager permissionManager, @ComponentImport DraftsTransitionHelper draftsTransitionHelper, @ComponentImport RelationService relationService, @ComponentImport TransactionTemplate transactionTemplate, SynchronyContentService synchronyContentService) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.draftsTransitionHelper = draftsTransitionHelper;
        this.relationService = relationService;
        this.transactionTemplate = transactionTemplate;
        this.synchronyContentService = synchronyContentService;
    }

    @Path(value="/{pageId}/changes/unpublished")
    @DELETE
    @AnonymousAllowed
    @Produces(value={"application/json"})
    public Response discardToLastPublishedVersion(@PathParam(value="pageId") Long pageId) {
        AbstractPage content = this.pageManager.getAbstractPage(pageId.longValue());
        if (content != null) {
            if (!this.hasEditPermission(AuthenticatedUserThreadLocal.get(), (ContentEntityObject)content)) {
                return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
            }
            if (content.isLatestVersion()) {
                try {
                    this.transactionTemplate.execute(() -> {
                        Either<SynchronyError, JSONObject> externalChangeResult;
                        Content draftContent = Content.builder().id(content.getContentId()).status(ContentStatus.DRAFT).build();
                        this.relationService.removeAllRelationsFromEntityWithType((RelationDescriptor)CollaboratorRelationDescriptor.COLLABORATOR, (Relatable)draftContent);
                        this.relationService.removeAllRelationsFromEntityWithType((RelationDescriptor)TouchedRelationDescriptor.TOUCHED, (Relatable)draftContent);
                        Page currentPage = this.pageManager.getPage(pageId.longValue());
                        ContentEntityObject existingDraft = this.draftsTransitionHelper.getDraft(pageId.longValue());
                        if (currentPage != null && existingDraft != null) {
                            existingDraft.setBodyAsString(currentPage.getBodyAsString());
                            this.pageManager.saveContentEntity(existingDraft, DefaultSaveContext.DRAFT);
                        }
                        if ((externalChangeResult = this.synchronyContentService.discardUnpublishedChanges(content.getContentId(), AuthenticatedUserThreadLocal.get())).isLeft()) {
                            SynchronyError error = (SynchronyError)externalChangeResult.left().get();
                            throw new RuntimeException(String.format("Rolling back removal of collaborators due to failed discard changes request to Synchrony: %s on revision %s", new Object[]{error.getCode(), error.getConflictingRev()}));
                        }
                        return null;
                    });
                }
                catch (RuntimeException e) {
                    if (e.getClass().getSimpleName().contains("HibernateOptimisticLockingFailureException")) {
                        log.debug("Editor might be busy. Unable to discard to last published version", (Throwable)e);
                        throw new ConflictException("Editor might be busy. Unable to discard to last published version", (Throwable)e);
                    }
                    log.error("Unable to discard to last published version", (Throwable)e);
                    throw new BadRequestException("Unable to discard to last published version", (Throwable)e);
                }
                return Response.status((Response.Status)Response.Status.OK).build();
            }
        }
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Content not found").build();
    }

    @Path(value="/{pageId}/recovery")
    @PUT
    @AnonymousAllowed
    @Produces(value={"application/json"})
    public Response recoverSynchronyAndConfluence(@PathParam(value="pageId") Long pageId, @QueryParam(value="behind") Behind behind, @QueryParam(value="conflictingRev") String conflictingRev) {
        if (!this.hasEditPermission(AuthenticatedUserThreadLocal.get(), (ContentEntityObject)this.pageManager.getAbstractPage(pageId.longValue()))) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        boolean result = behind == Behind.SYNCHRONY ? this.synchronyContentService.synchronyRecovery("synchrony-recovery", pageId, AuthenticatedUserThreadLocal.get()) : this.synchronyContentService.confluenceRecovery(pageId, AuthenticatedUserThreadLocal.get(), conflictingRev);
        if (!result) {
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status((Response.Status)Response.Status.OK).build();
    }

    private boolean hasEditPermission(ConfluenceUser user, ContentEntityObject content) {
        return this.permissionManager.hasPermission((User)user, Permission.EDIT, (Object)content);
    }

    public static enum Behind {
        SYNCHRONY,
        CONFLUENCE;


        @JsonCreator
        public static Behind fromString(String string) {
            if (Strings.isNullOrEmpty((String)string)) {
                return null;
            }
            return Behind.valueOf(string.toUpperCase());
        }
    }
}

