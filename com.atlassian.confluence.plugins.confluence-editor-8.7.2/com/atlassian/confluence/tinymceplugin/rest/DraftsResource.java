/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.people.KnownUser
 *  com.atlassian.confluence.api.model.relations.CollaboratorRelationDescriptor
 *  com.atlassian.confluence.api.model.relations.Relatable
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.atlassian.confluence.api.model.relations.RelationInstance
 *  com.atlassian.confluence.api.service.relations.RelationService
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.editor.EditorConverter
 *  com.atlassian.confluence.content.service.DraftService
 *  com.atlassian.confluence.content.service.DraftService$DraftType
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.core.datetime.FriendlyDateFormatter
 *  com.atlassian.confluence.core.datetime.RequestTimeThreadLocal
 *  com.atlassian.confluence.core.service.NotAuthorizedException
 *  com.atlassian.confluence.core.service.NotValidException
 *  com.atlassian.confluence.event.events.analytics.SharedDraftUpdatedEvent
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.pages.DraftManager
 *  com.atlassian.confluence.pages.DraftsTransitionHelper
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.relations.touch.TouchRelationSupport
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.diffs.MergeResult
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.util.i18n.Message
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.user.User
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.tinymceplugin.rest;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.model.relations.CollaboratorRelationDescriptor;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.model.relations.RelationInstance;
import com.atlassian.confluence.api.service.relations.RelationService;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.editor.EditorConverter;
import com.atlassian.confluence.content.service.DraftService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.core.datetime.RequestTimeThreadLocal;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.core.service.NotValidException;
import com.atlassian.confluence.event.events.analytics.SharedDraftUpdatedEvent;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.relations.touch.TouchRelationSupport;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.tinymceplugin.rest.DraftChangeResult;
import com.atlassian.confluence.tinymceplugin.rest.entities.DraftData;
import com.atlassian.confluence.tinymceplugin.rest.entities.DraftMessage;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.diffs.MergeResult;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.user.User;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/drafts")
public class DraftsResource {
    private static final Logger log = LoggerFactory.getLogger(DraftsResource.class);
    private static final long DRAFTS_LOCK_TIME_MS = Long.getLong("atlassian.confluence.drafts.shared.lock.time.ms", 0L);
    private static final long DRAFTS_LOCK_LIVE_TIME_SEC = Long.getLong("atlassian.confluence.drafts.shared.cache.ttl.sec", 600L);
    private final UserAccessor userAccessor;
    private final DraftService draftService;
    private final FormatSettingsManager formatSettingsManager;
    private final LocaleManager localeManager;
    private final PermissionManager permissionManager;
    private final DraftManager draftManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final PageManager pageManager;
    private final DraftsTransitionHelper draftsTransitionHelper;
    private final EditorConverter editConverter;
    private final EventPublisher eventPublisher;
    private final RelationService relationService;
    private final DarkFeatureManager darkFeatureManager;
    private final TouchRelationSupport touchRelationSupport;
    private final LoadingCache<Long, ReentrantLock> draftsUpdateLock = CacheBuilder.newBuilder().expireAfterAccess(DRAFTS_LOCK_LIVE_TIME_SEC, TimeUnit.SECONDS).build(CacheLoader.from(ReentrantLock::new));

    public DraftsResource(@ComponentImport UserAccessor userAccessor, @ComponentImport DraftService draftService, @ComponentImport FormatSettingsManager formatSettingsManager, @ComponentImport LocaleManager localeManager, @ComponentImport PermissionManager permissionManager, @ComponentImport DraftManager draftManager, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport PageManager pageManager, @ComponentImport DraftsTransitionHelper draftsTransitionHelper, @ComponentImport EditorConverter editConverter, @ComponentImport EventPublisher eventPublisher, @ComponentImport RelationService relationService, @ComponentImport DarkFeatureManager darkFeatureManager, @ComponentImport TouchRelationSupport touchRelationSupport) {
        this.userAccessor = userAccessor;
        this.draftService = draftService;
        this.formatSettingsManager = formatSettingsManager;
        this.localeManager = localeManager;
        this.permissionManager = permissionManager;
        this.draftManager = draftManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.pageManager = pageManager;
        this.draftsTransitionHelper = draftsTransitionHelper;
        this.editConverter = editConverter;
        this.eventPublisher = eventPublisher;
        this.relationService = relationService;
        this.darkFeatureManager = darkFeatureManager;
        this.touchRelationSupport = touchRelationSupport;
    }

    @GET
    @AnonymousAllowed
    @Produces(value={"application/json"})
    @Deprecated
    public Response getDrafts(int limit, int offset) {
        try {
            List drafts = this.draftService.findDrafts(limit, offset);
            ArrayList<DraftData> draftDatum = new ArrayList<DraftData>();
            for (Draft draft : drafts) {
                draftDatum.add(DraftData.create(draft));
            }
            return Response.ok(draftDatum).build();
        }
        catch (NotAuthorizedException ex) {
            return Response.status((int)403).build();
        }
        catch (NotValidException ex) {
            if (ex.getCause() != null) {
                log.warn(ex.getMessage(), ex.getCause());
            }
            HashMap<String, Integer> obj = new HashMap<String, Integer>();
            obj.put("limit", limit);
            obj.put("offset", offset);
            return Response.status((int)422).entity(obj).build();
        }
    }

    @GET
    @Path(value="/message")
    @AnonymousAllowed
    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    public Response getDraftMessage(@QueryParam(value="existingDraftId") long existingDraftId, @QueryParam(value="pageId") long pageId, @QueryParam(value="type") String type, @QueryParam(value="spaceKey") String spaceKey) {
        Draft draft = existingDraftId != 0L ? this.draftManager.getDraft(existingDraftId) : this.draftManager.findDraft(Long.valueOf(pageId), AuthenticatedUserThreadLocal.getUsername(), type, spaceKey);
        if (draft != null) {
            boolean conflictFound = false;
            boolean mergeRequired = false;
            if (this.draftManager.isMergeRequired(draft)) {
                MergeResult mergeResult = this.draftManager.mergeContent(draft);
                conflictFound = mergeResult.hasConflicts();
                mergeRequired = !conflictFound;
            }
            DraftData draftData = DraftData.create(draft);
            draftData.setDate(this.formatFriendlyDate(draft.getLastModificationDate()));
            return Response.ok((Object)new DraftMessage(draftData, draft.isNewPage(), conflictFound, mergeRequired)).build();
        }
        return Response.ok().build();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @POST
    @AnonymousAllowed
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response save(DraftData draftData, @DefaultValue(value="false") @QueryParam(value="ignoreRelations") boolean ignoreRelations) {
        String saveTime = this.getEventDate();
        try {
            Long draftId = draftData.getDraftId();
            Long pageId = draftData.getPageId();
            Long contentId = pageId != null && pageId != 0L ? pageId : draftId;
            ContentEntityObject existingDraft = this.draftsTransitionHelper.getDraft(contentId.longValue());
            Optional<ConfluenceUser> confluenceUser = Optional.ofNullable(AuthenticatedUserThreadLocal.get());
            if (existingDraft != null && !this.permissionManager.hasPermissionNoExemptions((User)confluenceUser.orElse(null), Permission.EDIT, (Object)existingDraft)) {
                return this.getNotAuthorizedResponse(draftData, saveTime);
            }
            if (this.draftsTransitionHelper.isSharedDraftsFeatureEnabled(draftData.getSpaceKey())) {
                if (existingDraft == null) {
                    return Response.status((int)400).entity((Object)new DraftChangeResult(draftData.getDraftId(), draftData.getPageId(), saveTime, "Legacy drafts deprecated")).build();
                }
                draftId = existingDraft.getId();
                Supplier<Void> supplier = () -> {
                    try {
                        existingDraft.setTitle(draftData.getTitle());
                        existingDraft.setBodyAsString(this.editConverter.convert(draftData.getContent(), (ConversionContext)new DefaultConversionContext((RenderContext)existingDraft.toPageContext())));
                        if (!"dummy-sync-rev".equals(draftData.getSyncRev())) {
                            existingDraft.setSynchronyRevision(draftData.getSyncRev());
                        }
                        this.pageManager.saveContentEntity(existingDraft, DefaultSaveContext.DRAFT);
                        this.eventPublisher.publish((Object)new SharedDraftUpdatedEvent());
                    }
                    catch (XhtmlException ex) {
                        throw new NotValidException("The supplied editor content could not be converted to storage format.", (Throwable)ex);
                    }
                    return null;
                };
                Lock lock = (Lock)this.draftsUpdateLock.get((Object)contentId);
                if (lock.tryLock(DRAFTS_LOCK_TIME_MS, TimeUnit.MILLISECONDS)) {
                    try {
                        supplier.get();
                    }
                    finally {
                        lock.unlock();
                    }
                } else {
                    log.debug("Lock {} for draft {} is held by another thread", (Object)contentId, (Object)draftId);
                    return Response.status((Response.Status)Response.Status.NOT_MODIFIED).entity((Object)new DraftChangeResult(draftData.getDraftId(), draftData.getPageId(), saveTime)).build();
                }
                if (!ignoreRelations) {
                    confluenceUser.ifPresent(user -> this.updateRelations(contentId, existingDraft, (ConfluenceUser)user));
                }
            } else {
                draftId = this.draftService.saveDraftFromEditor(draftData.getDraftId(), draftData.getParentPageId(), draftData.getTitle(), DraftService.DraftType.getByRepresentation((String)draftData.getType()), draftData.getContent(), draftData.getPageId(), draftData.getSpaceKey(), draftData.getPageVersion()).getId();
            }
            return Response.ok((Object)new DraftChangeResult(draftId, draftData.getPageId(), saveTime)).build();
        }
        catch (NotAuthorizedException ex) {
            return this.getNotAuthorizedResponse(draftData, saveTime);
        }
        catch (NotValidException ex) {
            if (ex.getCause() != null) {
                log.warn(ex.getMessage(), ex.getCause());
            }
            DraftChangeResult result = new DraftChangeResult(draftData.getDraftId(), draftData.getPageId(), saveTime, ex.getMessage());
            return Response.status((int)422).entity((Object)result).build();
        }
        catch (InterruptedException | ExecutionException e) {
            log.warn("Error acquiring the lock: {}", (Object)e.toString());
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)new DraftChangeResult(draftData.getDraftId(), draftData.getPageId(), saveTime)).build();
        }
    }

    private void updateRelations(Long contentId, ContentEntityObject existingDraft, ConfluenceUser authenticatedUser) {
        KnownUser currentUser = KnownUser.builder().userKey(authenticatedUser.getKey()).username(authenticatedUser.getName()).displayName(authenticatedUser.getFullName()).build();
        Content draftContent = Content.builder().id(ContentId.deserialise((String)contentId.toString())).type(ContentType.valueOf((String)existingDraft.getType())).status(ContentStatus.DRAFT).build();
        this.touchRelationSupport.handleTouchRelations(draftContent);
        this.relationService.create(RelationInstance.builder((Relatable)currentUser, (RelationDescriptor)CollaboratorRelationDescriptor.COLLABORATOR, (Relatable)draftContent).build());
    }

    @DELETE
    @Path(value="/discard")
    @AnonymousAllowed
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response discard(DraftData draftData) {
        String saveTime = this.getEventDate();
        try {
            Long removedDraftId = this.draftService.removeDraft(draftData.getPageId().longValue(), draftData.getDraftId().longValue());
            if (removedDraftId != null) {
                return Response.ok((Object)new DraftChangeResult(removedDraftId, draftData.getPageId(), saveTime)).build();
            }
            return Response.status((int)404).entity((Object)new DraftChangeResult(draftData.getDraftId(), draftData.getPageId(), saveTime)).build();
        }
        catch (NotAuthorizedException ex) {
            return this.getNotAuthorizedResponse(draftData, saveTime);
        }
        catch (NotValidException ex) {
            DraftChangeResult result = null;
            if (ex.getCause() != null) {
                log.warn(ex.getMessage(), ex.getCause());
            }
            result = new DraftChangeResult(draftData.getDraftId(), draftData.getPageId(), saveTime, ex.getMessage());
            return Response.status((int)422).entity((Object)result).build();
        }
    }

    private Response getNotAuthorizedResponse(DraftData draftData, String saveTime) {
        return Response.status((int)403).entity((Object)new DraftChangeResult(draftData.getDraftId(), draftData.getPageId(), saveTime)).build();
    }

    private String getEventDate() {
        return this.getDateFormatter().formatTime(new Date());
    }

    private DateFormatter getDateFormatter() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return this.userAccessor.getConfluenceUserPreferences((User)user).getDateFormatter(this.formatSettingsManager, this.localeManager);
    }

    private String formatFriendlyDate(Date date) {
        Message message = this.getFriendlyDateFormatter().getFormatMessage(date);
        return this.getText(message.getKey(), message.getArguments());
    }

    private FriendlyDateFormatter getFriendlyDateFormatter() {
        return new FriendlyDateFormatter(RequestTimeThreadLocal.getTimeOrNow(), this.getDateFormatter());
    }

    private String getText(String key, Object ... args) {
        return this.i18NBeanFactory.getI18NBean().getText(key, args);
    }
}

