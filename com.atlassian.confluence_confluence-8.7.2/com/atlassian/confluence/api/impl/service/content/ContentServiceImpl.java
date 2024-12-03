/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.content.ContentService$ContentFinder
 *  com.atlassian.confluence.api.service.content.ContentService$Validator
 *  com.atlassian.confluence.api.service.content.ContentTrashService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ConflictException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.user.User
 *  com.google.common.base.Supplier
 */
package com.atlassian.confluence.api.impl.service.content;

import com.atlassian.confluence.api.impl.service.content.ContentTrashServiceImpl;
import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.impl.service.content.finder.ContentFinderFactory;
import com.atlassian.confluence.api.impl.service.content.finder.FinderPredicates;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.content.ContentTrashService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ConflictException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.content.apisupport.ApiSupportProvider;
import com.atlassian.confluence.content.apisupport.ContentTypeApiSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.ContentEntityManagerInternal;
import com.atlassian.confluence.internal.audit.event.RestrictedPageViewNotPermittedEvent;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.relations.touch.TouchRelationSupport;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.user.User;
import com.google.common.base.Supplier;
import java.util.List;
import java.util.stream.Collectors;

public class ContentServiceImpl
implements ContentService {
    private final ContentFactory contentFactory;
    private final ContentFinderFactory contentFinderFactory;
    private final ContentTrashService trashService;
    private final ApiSupportProvider apiSupportProvider;
    private final ContentEntityManagerInternal contentEntityManager;
    private final UserChecker userChecker;
    private final LicenseService licenseService;
    private final Supplier<TouchRelationSupport> touchRelationSupportSupplier;
    private final EventPublisher eventPublisher;
    private PermissionManager permissionManager;

    public ContentServiceImpl(PermissionManager permissionManager, ContentFactory contentFactory, ContentFinderFactory contentFinderFactory, ContentTrashService trashService, ApiSupportProvider apiSupportProvider, ContentEntityManagerInternal contentEntityManager, UserChecker userChecker, Supplier<TouchRelationSupport> touchRelationSupportSupplier, EventPublisher eventPublisher, LicenseService licenseService) {
        this.permissionManager = permissionManager;
        this.contentFactory = contentFactory;
        this.contentFinderFactory = contentFinderFactory;
        this.trashService = trashService;
        this.apiSupportProvider = apiSupportProvider;
        this.contentEntityManager = contentEntityManager;
        this.userChecker = userChecker;
        this.touchRelationSupportSupplier = touchRelationSupportSupplier;
        this.eventPublisher = eventPublisher;
        this.licenseService = licenseService;
    }

    public ContentService.ContentFinder find(Expansion ... expansions) {
        return this.contentFinderFactory.createContentFinder(this, expansions);
    }

    private Expansions getDefaultUpdateExpansions() {
        return new Expansions(ExpansionsParser.parse((String)"body.storage,history,space,container.history,container.version,version,ancestors"));
    }

    public Content create(Content newContent) throws ServiceException {
        return this.create(newContent, this.getDefaultUpdateExpansions().toArray());
    }

    public Content create(Content newContent, Expansion ... expansions) throws ServiceException {
        this.validator().validateCreate(newContent).throwIfNotSuccessful("Could not create content with type " + newContent.getType());
        Option<Content> created = this.createUsingApiSupport(newContent, new Expansions(expansions));
        if (created.isDefined()) {
            Content createdContent = (Content)created.get();
            ((TouchRelationSupport)this.touchRelationSupportSupplier.get()).handleTouchRelations(createdContent);
            return createdContent;
        }
        throw new NotImplementedServiceException("Could not create " + newContent.getType());
    }

    private Option<Content> createUsingApiSupport(Content newContent, Expansions expansions) {
        ContentTypeApiSupport apiSupport = this.apiSupportProvider.getForType(newContent.getType());
        if (!newContent.getType().equals((Object)ContentType.ATTACHMENT)) {
            ContentEntityObject newCEO = (ContentEntityObject)apiSupport.create(newContent);
            return Option.some((Object)this.buildContentWithoutChecks(newCEO, expansions));
        }
        return Option.none();
    }

    private Option<Content> updateUsingApiSupport(Content contentToUpdate, ContentConvertible ceo) {
        ContentTypeApiSupport apiSupport = this.apiSupportProvider.getForType(ceo.getContentTypeObject());
        ContentEntityObject updatedCEO = (ContentEntityObject)((Object)apiSupport.update(contentToUpdate, ceo));
        return Option.option((Object)this.contentFactory.buildFrom(updatedCEO, this.getDefaultUpdateExpansions()));
    }

    public Content update(Content updatedContent) throws ServiceException {
        if (updatedContent == null) {
            throw new BadRequestException("No content supplied to update");
        }
        ContentId contentId = updatedContent.getId();
        ContentType type = updatedContent.getType();
        ContentEntityObject ceo = ContentStatus.DRAFT.equals((Object)updatedContent.getStatus()) ? this.contentEntityManager.findDraftFor(contentId.asLong()) : this.contentEntityManager.getById(contentId);
        if (ceo == null) {
            throw new NotFoundException("Could not find Content for update with id " + contentId);
        }
        if (!(ceo instanceof ContentConvertible)) {
            throw new NotImplementedServiceException("This content type is not currently supported: " + type);
        }
        if (ceo.isDeleted()) {
            return this.trashService.restore(updatedContent);
        }
        this.validator().validateUpdate(updatedContent, ceo).throwIfNotSuccessful(String.format("Could not update Content of type : %s with id %s", ceo.getClass(), updatedContent.getId().serialise()));
        ((TouchRelationSupport)this.touchRelationSupportSupplier.get()).handleTouchRelations(updatedContent);
        Option<Content> result = this.updateUsingApiSupport(updatedContent, (ContentConvertible)((Object)ceo));
        return (Content)result.get();
    }

    public void delete(Content content) throws ServiceException {
        if (content == null) {
            throw new BadRequestException("Missing content");
        }
        if (ContentStatus.DRAFT.equals((Object)content.getStatus())) {
            throw new NotImplementedServiceException("Draft deletion via the ContentService is not supported. Use the ContentDraftService instead.");
        }
        Content existingContent = (Content)this.find(new Expansion[0]).withId(content.getId()).fetch().orElseThrow(ServiceExceptionSupplier.notFound((String)String.format("Cannot delete content with id '%s': not found.", content.getId())));
        if (ContentTrashServiceImpl.TRASHABLE_CONTENT_TYPES.contains((Object)existingContent.getType())) {
            this.trashService.trash(existingContent);
        } else {
            this.trashService.purge(existingContent);
        }
    }

    public ValidatorImpl validator() {
        return new ValidatorImpl();
    }

    public Content buildContent(ContentEntityObject entity, List<ContentStatus> statuses, Expansion ... expansions) {
        if (entity == null) {
            return null;
        }
        if (!statuses.isEmpty() && !FinderPredicates.statusPredicate(statuses).test(entity)) {
            return null;
        }
        if (!this.canView(entity)) {
            if (entity instanceof AbstractPage) {
                this.eventPublisher.publish((Object)new RestrictedPageViewNotPermittedEvent((AbstractPage)entity));
            }
            return null;
        }
        return this.buildContentWithoutChecks(entity, new Expansions(expansions));
    }

    private Content buildContentWithoutChecks(ContentEntityObject entity, Expansions expansions) {
        return this.contentFactory.buildFrom(entity, expansions);
    }

    private boolean canView(ContentEntityObject entity) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return this.permissionManager.hasPermission((User)user, Permission.VIEW, entity);
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public class ValidatorImpl
    implements ContentService.Validator {
        public ValidationResult validateDelete(Content content) {
            if (content == null) {
                return new SimpleValidationResult.Builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)"Missing content")).build();
            }
            if (content.getType() == null) {
                ContentId contentId = content.getId();
                content = (Content)ContentServiceImpl.this.find(new Expansion[0]).withId(contentId).fetchOrNull();
                if (content == null) {
                    return new SimpleValidationResult.Builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)String.format("Cannot delete content with id '%s': not found", contentId))).build();
                }
            }
            if (ContentTrashServiceImpl.TRASHABLE_CONTENT_TYPES.contains((Object)content.getType())) {
                return ContentServiceImpl.this.trashService.validator().validateTrash(content);
            }
            return ContentServiceImpl.this.trashService.validator().validatePurge(content);
        }

        public ValidationResult validateCreate(Content newContent) throws ServiceException {
            this.validateLicense().throwIfNotSuccessful();
            if (newContent == null) {
                return new SimpleValidationResult.Builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)"Missing new content")).build();
            }
            ValidationResult result = this.validateBodyCount(newContent);
            if (!result.isValid()) {
                return result;
            }
            return this.validateCreateUsingApiSupport(newContent);
        }

        private ValidationResult validateCreateUsingApiSupport(Content newContent) {
            if (newContent.getType() == null) {
                return SimpleValidationResult.builder().authorized(true).addError("type is required to create content", new Object[0]).build();
            }
            ContentTypeApiSupport apiSupport = ContentServiceImpl.this.apiSupportProvider.getForType(newContent.getType());
            return apiSupport.validateCreate(newContent);
        }

        private ValidationResult validateUpdateUsingApiSupport(Content newContent, ContentConvertible existingEntity) {
            SimpleValidationResult.Builder builder = SimpleValidationResult.builder();
            if (!ContentServiceImpl.this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, existingEntity)) {
                builder.authorized(false).addError("You do not have permission to edit this content", new Object[0]);
            } else {
                builder.authorized(true);
            }
            ContentType contentType = newContent.getType();
            if (contentType == null || !contentType.equals((Object)existingEntity.getContentTypeObject())) {
                return builder.addError(String.format("Type is required and must match existing entity type of %s, but received %s", existingEntity.getContentTypeObject(), contentType), new Object[0]).build();
            }
            ContentTypeApiSupport apiSupport = ContentServiceImpl.this.apiSupportProvider.getForType(contentType);
            return apiSupport.validateUpdate(newContent, existingEntity);
        }

        protected ValidationResult validateUpdate(Content updatedContent, ContentEntityObject currentCeo) {
            ValidationResult result;
            this.validateLicense().throwIfNotSuccessful();
            if (updatedContent == null) {
                throw new BadRequestException("Missing updated content.");
            }
            if (currentCeo == null) {
                throw new BadRequestException("Missing current content entity object.");
            }
            Reference updatedVersionRef = updatedContent.getVersionRef();
            if (!updatedVersionRef.isExpanded() || !updatedVersionRef.exists()) {
                throw new BadRequestException("Must supply an incremented version when updating Content. No version supplied.");
            }
            int currentVersion = currentCeo.getVersion();
            int updatedVersion = ((Version)updatedVersionRef.get()).getNumber();
            if (currentCeo.isDraft()) {
                if (updatedVersion != currentVersion) {
                    throw new ConflictException("Draft versioning is not supported. Current version is : " + currentVersion);
                }
            } else if (updatedVersion != currentVersion + 1) {
                throw new ConflictException("Version must be incremented on update. Current version is: " + currentVersion);
            }
            if (!(result = this.validateBodyCount(updatedContent)).isValid()) {
                return result;
            }
            return this.validateUpdateUsingApiSupport(updatedContent, (ContentConvertible)((Object)currentCeo));
        }

        private ValidationResult validateBodyCount(Content content) {
            int bodyCount = content.getBody().size();
            if (bodyCount > 1) {
                List bodyTypes = content.getBody().keySet().stream().map(ContentRepresentation::getRepresentation).collect(Collectors.toList());
                String message = String.format("Maximum number of permitted body entries is 1 but found %d: %s", bodyCount, String.join((CharSequence)", ", bodyTypes));
                return SimpleValidationResult.builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)message)).build();
            }
            return SimpleValidationResult.VALID;
        }

        private ValidationResult validateLicense() {
            if (ContentServiceImpl.this.licenseService.retrieve().isExpired() || ContentServiceImpl.this.userChecker != null && ContentServiceImpl.this.userChecker.hasTooManyUsers()) {
                return SimpleValidationResult.FORBIDDEN;
            }
            return SimpleValidationResult.VALID;
        }
    }
}

