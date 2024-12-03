/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.Label$Prefix
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.reference.Collapsed
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.model.relations.CollaboratorRelationDescriptor
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.content.ContentBodyConversionService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ConflictException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.user.User
 *  com.google.common.base.Strings
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.content.apisupport;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.Label;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.reference.Collapsed;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.relations.CollaboratorRelationDescriptor;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.content.ContentBodyConversionService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ConflictException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.content.apisupport.DraftAttributesCopier;
import com.atlassian.confluence.content.service.page.CreateContextProvider;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.internal.ContentDraftManagerInternal;
import com.atlassian.confluence.internal.ContentEntityManagerInternal;
import com.atlassian.confluence.internal.labels.LabelManagerInternal;
import com.atlassian.confluence.internal.pages.PageManagerInternal;
import com.atlassian.confluence.internal.relations.RelatableEntity;
import com.atlassian.confluence.internal.relations.RelationManager;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DuplicateDataRuntimeException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.user.User;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

@ExperimentalSpi
public class ContentCreator {
    private final SpaceManager spaceManager;
    private final ContentBodyConversionService contentBodyConversionService;
    private final PermissionManager permissionManager;
    private final PageManagerInternal pageManager;
    private final CustomContentManager customContentEntityManager;
    private final EventPublisher eventPublisher;
    private final DraftAttributesCopier draftAttributesCopier;
    private final LabelManagerInternal labelManager;
    private final RelationManager relationManager;
    private final ContentEntityManagerInternal contentEntityManager;
    private static final int TITLE_MAX_LENGTH = 255;

    public ContentCreator(SpaceManager spaceManager, ContentBodyConversionService contentBodyConversionService, PermissionManager permissionManager, PageManagerInternal pageManager, CustomContentManager customContentEntityManager, EventPublisher eventPublisher, DraftAttributesCopier draftAttributesCopier, LabelManagerInternal labelManager, RelationManager relationManager, ContentEntityManagerInternal contentEntityManager) {
        this.spaceManager = spaceManager;
        this.contentBodyConversionService = contentBodyConversionService;
        this.permissionManager = permissionManager;
        this.pageManager = pageManager;
        this.customContentEntityManager = customContentEntityManager;
        this.eventPublisher = eventPublisher;
        this.draftAttributesCopier = draftAttributesCopier;
        this.labelManager = labelManager;
        this.relationManager = relationManager;
        this.contentEntityManager = contentEntityManager;
    }

    public void setCommonPropertiesForCreate(Content newContent, ContentEntityObject entity, User authenticatedUser) {
        if (entity instanceof SpaceContentEntityObject) {
            this.setSpaceForCreate(newContent, (SpaceContentEntityObject)entity, authenticatedUser);
        }
        this.setTitleOnEntity(newContent, entity);
        this.setContentBodyOnEntity(newContent, entity);
        this.setContentStatusOnEntity(newContent, entity);
        this.setSyncRevOnEntity(newContent, entity);
        this.setOriginalVersionOnEntity(newContent, entity);
    }

    public ValidationResult validateUpdate(ConfluenceUser user, Content updatedContent, ContentEntityObject entity) {
        SimpleValidationResult.Builder builder = SimpleValidationResult.builder();
        Reference updatedSpace = updatedContent.getSpaceRef();
        if (entity instanceof SpaceContentEntityObject && updatedSpace.exists() && !((Spaced)((Object)entity)).getSpace().getKey().equals(((com.atlassian.confluence.api.model.content.Space)updatedSpace.get()).getKey()) && !entity.isDraft()) {
            builder.addError("You can't change an existing page's space.", new Object[0]).withExceptionSupplier(ServiceExceptionSupplier.badRequestExceptionSupplier());
        }
        if (StringUtils.isBlank((CharSequence)updatedContent.getTitle())) {
            builder.addError("You need to include the title to update a page.", new Object[0]).withExceptionSupplier(ServiceExceptionSupplier.badRequestExceptionSupplier());
        } else if (updatedContent.getTitle().length() > 255) {
            builder.addError("Title cannot be longer than 255 characters.", new Object[0]).withExceptionSupplier(ServiceExceptionSupplier.badRequestExceptionSupplier());
        }
        return builder.authorized(this.permissionManager.hasPermission((User)user, Permission.EDIT, entity)).build();
    }

    public ValidationResult validateCreate(ConfluenceUser confluenceUser, Content newContent, Class<? extends ContentEntityObject> entityClass) {
        SimpleValidationResult.Builder resultBuilder = SimpleValidationResult.builder();
        if (!newContent.getSpaceRef().exists()) {
            resultBuilder.addError("You must specify a space for new content.", new Object[0]);
        }
        if (StringUtils.isBlank((CharSequence)newContent.getTitle())) {
            resultBuilder.addError("You must specify a title for new content.", new Object[0]);
        } else if (newContent.getTitle().length() > 255) {
            resultBuilder.addError("Title cannot be longer than 255 characters.", new Object[0]);
        }
        if (resultBuilder.hasErrors()) {
            return resultBuilder.authorized(true).build();
        }
        Space space = this.spaceManager.getSpace(newContent.getSpace().getKey());
        return SimpleValidationResult.builder().authorized(this.permissionManager.hasCreatePermission((User)confluenceUser, (Object)space, entityClass)).build();
    }

    private void checkSpacePermissions(User user, Space space, Class<? extends ContentEntityObject> typeToCreate) {
        if (!this.permissionManager.hasPermission(user, Permission.VIEW, space)) {
            throw new PermissionException("You're not allowed to view that space, or it does not exist.");
        }
        if (!this.permissionManager.hasCreatePermission(user, (Object)space, typeToCreate)) {
            throw new PermissionException("You're not allowed to create content in that space.");
        }
    }

    public boolean setCommonPropertiesForUpdate(Content updatedContent, ContentEntityObject entity) {
        boolean storeRequired = this.setContentBodyOnEntity(updatedContent, entity);
        storeRequired |= this.setTitleOnEntity(updatedContent, entity);
        storeRequired |= this.setContentStatusOnEntity(updatedContent, entity);
        return storeRequired |= this.setSyncRevOnEntity(updatedContent, entity);
    }

    public boolean setLabelsMetadataOnEntity(Content updatedContent, ContentEntityObject entity) {
        if (entity instanceof Comment) {
            return false;
        }
        Map metadata = updatedContent.getMetadata();
        if (metadata != null && !(metadata instanceof Collapsed)) {
            boolean performedUpdate = false;
            Object metadataLabels = metadata.get("labels");
            if (metadataLabels == null) {
                return false;
            }
            if (!(metadataLabels instanceof PageResponse)) {
                throw new BadRequestException("Incorrect format of labels in metadata. Labels field should be a PageResponse or List, instead of " + metadataLabels.getClass());
            }
            List newLabels = ((PageResponse)metadata.get("labels")).getResults();
            HashMap unvisitedLabels = Maps.newHashMap();
            for (Label label : entity.getLabels()) {
                unvisitedLabels.put(this.getLabelIdentifier(label.getNamespace().getPrefix(), label.getName()), label);
            }
            for (Label label : newLabels) {
                String prefix = label.getPrefix();
                if (StringUtils.isEmpty((CharSequence)prefix)) {
                    prefix = Label.Prefix.global.toString();
                }
                String labelName = label.getLabel();
                String labelIdentifier = this.getLabelIdentifier(prefix, labelName);
                if (StringUtils.isEmpty((CharSequence)labelName)) {
                    throw new BadRequestException("Label " + label.toString() + " must contains a name");
                }
                Label newLabel = new Label(labelName, prefix);
                if (unvisitedLabels.containsKey(labelIdentifier)) {
                    Label target = (Label)unvisitedLabels.get(labelIdentifier);
                    if (!target.getNamespace().getPrefix().equals(prefix) || !target.getName().equals(labelName)) {
                        this.labelManager.removeLabel(entity, target);
                        this.labelManager.addLabel(entity, newLabel);
                        performedUpdate = true;
                    }
                    unvisitedLabels.remove(labelIdentifier);
                    continue;
                }
                this.labelManager.addLabel(entity, newLabel);
                performedUpdate = true;
            }
            for (Label label : unvisitedLabels.values()) {
                this.labelManager.removeLabel(entity, label);
                performedUpdate = true;
            }
            return performedUpdate;
        }
        return false;
    }

    private String getLabelIdentifier(String prefix, String labelName) {
        return prefix + ":" + labelName;
    }

    public boolean setCommonMetadata(Content updatedContent, ContentEntityObject entity) {
        boolean updated = this.setLabelsMetadataOnEntity(updatedContent, entity);
        return updated;
    }

    private boolean setSyncRevOnEntity(Content updatedContent, ContentEntityObject entity) {
        String entitySyncRev = entity.getSynchronyRevision();
        String updatedSyncRev = null;
        Reference versionRef = updatedContent.getVersionRef();
        if (versionRef.exists()) {
            updatedSyncRev = updatedContent.getVersion().getSyncRev();
        }
        if (StringUtils.isNotBlank(updatedSyncRev) && !updatedSyncRev.equals(entitySyncRev)) {
            entity.setSynchronyRevision(updatedSyncRev);
            return true;
        }
        return false;
    }

    public boolean setTitleOnEntity(Content updatedContent, ContentEntityObject entity) {
        if (Strings.isNullOrEmpty((String)updatedContent.getTitle())) {
            return false;
        }
        String updatedTitle = updatedContent.getTitle();
        String string = updatedTitle = updatedTitle != null ? updatedTitle.trim() : "";
        if (!updatedTitle.equals(entity.getTitle())) {
            entity.setTitle(updatedTitle);
            return true;
        }
        return false;
    }

    public boolean setContentBodyOnEntity(Content updatedContent, ContentEntityObject entity) {
        String oldContentBody;
        Optional bodyToConvert = this.contentBodyConversionService.selectBodyForRepresentation(updatedContent, ContentRepresentation.STORAGE);
        String contentBody = bodyToConvert.map(b -> this.contentBodyConversionService.convert(b, ContentRepresentation.STORAGE).getValue()).orElseGet(() -> entity.getBodyContent().getBody());
        String updatedContentBody = StringUtils.stripToEmpty((String)contentBody);
        if (!updatedContentBody.equals(oldContentBody = StringUtils.stripToEmpty((String)entity.getBodyAsString()))) {
            entity.setBodyAsString(updatedContentBody);
            return true;
        }
        return false;
    }

    public boolean setContentStatusOnEntity(Content updatedContent, ContentEntityObject entity) {
        ContentStatus newStatus = updatedContent.getStatus();
        if (newStatus.equals((Object)entity.getContentStatusObject())) {
            return false;
        }
        if (ContentStatus.CURRENT.equals((Object)updatedContent.getStatus())) {
            entity.setContentStatus("current");
            return true;
        }
        if (ContentStatus.TRASHED.equals((Object)updatedContent.getStatus())) {
            entity.setContentStatus("deleted");
            return true;
        }
        if (ContentStatus.DRAFT.equals((Object)updatedContent.getStatus())) {
            entity.setContentStatus("draft");
            return true;
        }
        return false;
    }

    boolean setOriginalVersionOnEntity(Content updatedContent, ContentEntityObject entity) {
        if (updatedContent.getId() == null || !ContentStatus.DRAFT.equals((Object)updatedContent.getStatus())) {
            return false;
        }
        ContentEntityObject existingEntity = this.contentEntityManager.getById(updatedContent.getId());
        if (existingEntity == null) {
            return false;
        }
        if (!existingEntity.isLatestVersion()) {
            throw new NotFoundException(String.format("Cannot find content with contentId '%s'", updatedContent.getId()));
        }
        if (!ContentStatus.CURRENT.equals((Object)existingEntity.getContentStatusObject())) {
            throw new BadRequestException(String.format("Cannot create draft for content with contentId '%s' and status '%s'", updatedContent.getId(), existingEntity.getContentStatusObject()));
        }
        ContentEntityObject existingDraft = this.contentEntityManager.findDraftFor(existingEntity);
        if (existingDraft != null) {
            throw new ConflictException(String.format("A draft with contentId '%s' already exists", updatedContent.getId()));
        }
        entity.setOriginalVersion(existingEntity);
        return true;
    }

    public void setSpaceForCreate(Content newContent, SpaceContentEntityObject spacedEntity, User authenticatedUser) {
        Space space = null;
        if (newContent.getSpaceRef().exists()) {
            space = this.spaceManager.getSpace(newContent.getSpace().getKey());
            this.checkSpacePermissions(authenticatedUser, space, spacedEntity.getClass());
        }
        spacedEntity.setSpace(space);
    }

    public <T extends ContentEntityObject> T saveNewVersion(T ceo, T originalCEO, Version version) {
        DefaultSaveContext saveContext = ((DefaultSaveContext.Builder)((DefaultSaveContext.Builder)DefaultSaveContext.builder().updateLastModifier(true).suppressNotifications(version.isMinorEdit() || version.isHidden())).updateTrigger(this.getPageUpdateTrigger(ceo))).build();
        ceo.setVersionComment(version.getMessage());
        if (ceo instanceof AbstractPage) {
            try {
                this.pageManager.saveContentEntity(ceo, originalCEO, saveContext);
                long id = ceo.getId();
                ContentEntityObject updateCeo = Objects.requireNonNull(this.pageManager.getById(id));
                ContentEntityObject previousVersion = this.pageManager.getPreviousVersion(updateCeo);
                if (previousVersion != null) {
                    this.relationManager.moveRelationsToContent(updateCeo, previousVersion, (RelationDescriptor)CollaboratorRelationDescriptor.COLLABORATOR);
                }
                if (!AuthenticatedUserThreadLocal.isAnonymousUser()) {
                    this.relationManager.addRelation(AuthenticatedUserThreadLocal.get(), updateCeo, (RelationDescriptor)CollaboratorRelationDescriptor.COLLABORATOR);
                }
                return (T)updateCeo;
            }
            catch (DuplicateDataRuntimeException exception) {
                throw new BadRequestException("A page with this title already exists: " + exception.getMessage(), (Throwable)exception);
            }
        }
        if (ceo instanceof CustomContentEntityObject) {
            this.customContentEntityManager.saveContentEntity(ceo, originalCEO, saveContext);
            long id = ceo.getId();
            return (T)this.customContentEntityManager.getById(id);
        }
        throw new NotImplementedServiceException(String.format("Updating of content with type %s is not supported ", ceo.getClass()));
    }

    private <T extends ContentEntityObject> PageUpdateTrigger getPageUpdateTrigger(T ceo) {
        PageUpdateTrigger trigger = PageUpdateTrigger.UNKNOWN;
        if (StringUtils.isNotBlank((CharSequence)ceo.getSynchronyRevision())) {
            trigger = PageUpdateTrigger.EDIT_PAGE;
        }
        return trigger;
    }

    public <T extends ContentEntityObject> T update(T ceo, T originalCEO, Version version) {
        if (originalCEO.isDraft()) {
            return this.saveNewContent(ceo, version, null);
        }
        return this.saveNewVersion(ceo, originalCEO, version);
    }

    @Deprecated
    public <T extends ContentEntityObject> T saveForCreate(T entity) {
        return this.saveNewContent(entity, null, null);
    }

    @Deprecated
    public <T extends ContentEntityObject> T saveForCreate(T entity, Option<Draft> draft) {
        return this.saveNewContent(entity, null, (Draft)draft.getOrElse((Object)null));
    }

    @Deprecated
    public <T extends ContentEntityObject> T saveForCreate(T entity, @Nullable Version version, Option<Draft> draft) {
        Object savedCeo;
        if (entity instanceof CustomContentEntityObject) {
            this.customContentEntityManager.saveContentEntity(entity, DefaultSaveContext.DEFAULT);
            long id = entity.getId();
            return (T)this.customContentEntityManager.getById(id);
        }
        if (!(entity instanceof AbstractPage)) {
            throw new NotImplementedServiceException(String.format("Saving content with type $%s is not supported", entity.getClass()));
        }
        if (entity.isDraft()) {
            savedCeo = ((ContentDraftManagerInternal)((Object)this.pageManager)).createDraft(entity, DefaultSaveContext.DRAFT);
        } else {
            try {
                Created event;
                DefaultSaveContext saveContext = ((DefaultSaveContext.Builder)((DefaultSaveContext.Builder)((DefaultSaveContext.Builder)DefaultSaveContext.builder().updateLastModifier(true).suppressEvents(true)).updateTrigger(this.getPageUpdateTrigger(entity))).suppressNotifications(version != null && (version.isMinorEdit() || version.isHidden()))).build();
                this.pageManager.saveContentEntity(entity, saveContext);
                if (draft.isDefined()) {
                    entity = this.draftAttributesCopier.copyDraftAttributes(entity, (Draft)draft.get());
                }
                if ((event = this.getCreateEvent((AbstractPage)entity)) != null) {
                    this.eventPublisher.publish((Object)event);
                }
            }
            catch (DuplicateDataRuntimeException exception) {
                throw new BadRequestException("A page with this title already exists: " + exception.getMessage(), (Throwable)exception);
            }
            long id = entity.getId();
            savedCeo = this.pageManager.getById(id);
        }
        if (savedCeo != null && !AuthenticatedUserThreadLocal.isAnonymousUser()) {
            this.relationManager.addRelation(AuthenticatedUserThreadLocal.get(), (RelatableEntity)savedCeo, (RelationDescriptor)CollaboratorRelationDescriptor.COLLABORATOR);
        }
        return savedCeo;
    }

    public <T extends ContentEntityObject> T saveNewContent(T entity, @Nullable Version version, @Nullable Draft draft) {
        return this.saveForCreate(entity, version, (Option<Draft>)Option.option((Object)draft));
    }

    private Created getCreateEvent(AbstractPage abstractPage) {
        if (abstractPage instanceof Page) {
            return new PageCreateEvent((Object)this, (Page)abstractPage, CreateContextProvider.EMPTY_CONTEXT_PROVIDER.getContext());
        }
        if (abstractPage instanceof BlogPost) {
            return new BlogPostCreateEvent((Object)this, (BlogPost)abstractPage, CreateContextProvider.EMPTY_CONTEXT_PROVIDER.getContext());
        }
        return null;
    }

    public <T extends ContentEntityObject> T cloneForUpdate(T ceo) {
        return (T)((ContentEntityObject)ceo.clone());
    }
}

