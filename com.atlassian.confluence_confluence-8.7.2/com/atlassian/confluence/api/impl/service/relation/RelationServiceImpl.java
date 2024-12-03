/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.PaginationBatch
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.relations.CollaboratorRelationDescriptor
 *  com.atlassian.confluence.api.model.relations.Relatable
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.atlassian.confluence.api.model.relations.RelationInstance
 *  com.atlassian.confluence.api.model.relations.ValidatingRelationDescriptor
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ConflictException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.confluence.api.service.relations.RelationService
 *  com.atlassian.confluence.api.service.relations.RelationService$RelatableFinder
 *  com.atlassian.confluence.api.service.relations.RelationService$Validator
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  net.jcip.annotations.ThreadSafe
 *  org.springframework.orm.ObjectOptimisticLockingFailureException
 */
package com.atlassian.confluence.api.impl.service.relation;

import com.atlassian.confluence.api.impl.service.relation.RelatableFactory;
import com.atlassian.confluence.api.impl.service.relation.RelatableResolver;
import com.atlassian.confluence.api.impl.service.relation.RelationInstanceFactory;
import com.atlassian.confluence.api.impl.service.relation.ValidatingRelationDescriptorRegistry;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.pagination.PaginationBatch;
import com.atlassian.confluence.api.model.relations.CollaboratorRelationDescriptor;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.model.relations.RelationInstance;
import com.atlassian.confluence.api.model.relations.ValidatingRelationDescriptor;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ConflictException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.api.service.relations.RelationService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.relations.RelatableEntity;
import com.atlassian.confluence.internal.relations.RelatableEntityTypeEnum;
import com.atlassian.confluence.internal.relations.RelationManager;
import com.atlassian.confluence.internal.relations.dao.RelationEntity;
import com.atlassian.confluence.internal.relations.query.RelationQuery;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.userstatus.FavouriteManager;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.jcip.annotations.ThreadSafe;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

public class RelationServiceImpl
implements RelationService {
    private static final EnumSet<RelatableEntityTypeEnum> IMPLEMENTATION_SPACE_TYPES = EnumSet.of(RelatableEntityTypeEnum.SPACE);
    private static final EnumSet<RelatableEntityTypeEnum> IMPLEMENTATION_CONTENT_TYPES = EnumSet.of(RelatableEntityTypeEnum.PAGE, new RelatableEntityTypeEnum[]{RelatableEntityTypeEnum.COMMENT, RelatableEntityTypeEnum.BLOG, RelatableEntityTypeEnum.ATTACHMENT, RelatableEntityTypeEnum.DRAFT, RelatableEntityTypeEnum.CUSTOM});
    private final RelationManager relationManager;
    private final RelationInstanceFactory relationInstanceFactory;
    private final RelatableFactory relatableFactory;
    private final RelatableResolver relatableResolver;
    private final PaginationService paginationService;
    private final FavouriteManager favouriteManager;
    private final LikeManager likeManager;
    private final PermissionManager permissionManager;
    private final UserChecker userChecker;
    private final ValidatingRelationDescriptorRegistry relationDescriptorRegistry;
    private final AccessModeService accessModeService;
    private final LicenseService licenseService;

    public RelationServiceImpl(RelationManager relationManager, RelationInstanceFactory relationInstanceFactory, RelatableFactory relatableFactory, RelatableResolver relatableResolver, PaginationService paginationService, FavouriteManager favouriteManager, LikeManager likeManager, PermissionManager permissionManager, UserChecker userChecker, ValidatingRelationDescriptorRegistry relationDescriptorRegistry, AccessModeService accessModeService, LicenseService licenseService) {
        this.relationManager = relationManager;
        this.relationInstanceFactory = relationInstanceFactory;
        this.relatableFactory = relatableFactory;
        this.relatableResolver = relatableResolver;
        this.paginationService = paginationService;
        this.favouriteManager = favouriteManager;
        this.likeManager = likeManager;
        this.permissionManager = permissionManager;
        this.userChecker = userChecker;
        this.relationDescriptorRegistry = relationDescriptorRegistry;
        this.accessModeService = accessModeService;
        this.licenseService = licenseService;
    }

    public <S extends Relatable, T extends Relatable> RelationInstance<S, T> create(RelationInstance<S, T> relationInstance) throws ServiceException {
        Relatable apiSource = relationInstance.getSource();
        Relatable apiTarget = relationInstance.getTarget();
        RelationDescriptor relationDescriptor = relationInstance.getRelationDescriptor();
        this.validator().validateCreate(apiSource, relationDescriptor, apiTarget).throwIfNotSuccessful("Cannot create relation");
        RelatableEntity sourceEntity = this.relatableResolver.resolve(apiSource);
        RelatableEntity targetEntity = this.relatableResolver.resolve(apiTarget);
        if (relationDescriptor.getRelationName().equals("favourite")) {
            this.createFavourite(sourceEntity, targetEntity);
            return this.relationInstanceFactory.buildFrom(sourceEntity, relationDescriptor.getRelationName(), targetEntity, new Expansions(new Expansion[0]));
        }
        if (relationDescriptor.getRelationName().equals("like")) {
            this.createLike(sourceEntity, targetEntity);
            return this.relationInstanceFactory.buildFrom(sourceEntity, relationDescriptor.getRelationName(), targetEntity, new Expansions(new Expansion[0]));
        }
        RelationEntity relationEntity = this.handlePotentialHibernateException(() -> this.relationManager.addRelation(sourceEntity, targetEntity, relationDescriptor));
        return this.relationInstanceFactory.buildFrom(relationEntity, new Expansions(new Expansion[0]));
    }

    public <S extends Relatable, T extends Relatable> void delete(RelationInstance<S, T> relationInstance) throws ServiceException {
        Relatable apiSource = relationInstance.getSource();
        Relatable apiTarget = relationInstance.getTarget();
        RelationDescriptor relationDescriptor = relationInstance.getRelationDescriptor();
        this.validator().validateDelete(apiSource, relationDescriptor, apiTarget).throwIfNotSuccessful("Could not delete relation");
        RelatableEntity sourceEntity = this.relatableResolver.resolve(apiSource);
        RelatableEntity targetEntity = this.relatableResolver.resolve(apiTarget);
        if (relationDescriptor.getRelationName().equals("favourite")) {
            this.deleteFavourite(sourceEntity, targetEntity);
            return;
        }
        if (relationDescriptor.getRelationName().equals("like")) {
            this.deleteLike(sourceEntity, targetEntity);
            return;
        }
        this.handlePotentialHibernateException(() -> {
            this.relationManager.removeRelation(sourceEntity, targetEntity, relationDescriptor);
            return null;
        });
    }

    private void createLike(RelatableEntity sourceEntity, RelatableEntity targetEntity) {
        User user = (User)sourceEntity;
        ContentEntityObject targetContent = this.castTargetClassForLikes(targetEntity);
        this.likeManager.addLike(targetContent, user);
    }

    private void createFavourite(RelatableEntity sourceEntity, RelatableEntity targetEntity) {
        User user = (User)sourceEntity;
        if (targetEntity instanceof SpaceDescription) {
            this.favouriteManager.addSpaceToFavourites(user, ((SpaceDescription)targetEntity).getSpace());
        } else if (targetEntity instanceof AbstractPage) {
            this.favouriteManager.addPageToFavourites(user, (AbstractPage)targetEntity);
        } else {
            throw new IllegalArgumentException("Invalid targetEntity class: " + targetEntity.getClass());
        }
    }

    private void deleteLike(RelatableEntity sourceEntity, RelatableEntity targetEntity) {
        User user = (User)sourceEntity;
        ContentEntityObject targetContent = this.castTargetClassForLikes(targetEntity);
        this.likeManager.removeLike(targetContent, user);
    }

    private void deleteFavourite(RelatableEntity sourceEntity, RelatableEntity targetEntity) {
        User user = (User)sourceEntity;
        if (targetEntity instanceof SpaceDescription) {
            this.favouriteManager.removeSpaceFromFavourites(user, ((SpaceDescription)targetEntity).getSpace());
        } else if (targetEntity instanceof AbstractPage) {
            this.favouriteManager.removePageFromFavourites(user, (AbstractPage)targetEntity);
        } else {
            throw new IllegalArgumentException("Invalid targetEntity class: " + targetEntity.getClass());
        }
    }

    private ContentEntityObject castTargetClassForLikes(RelatableEntity targetEntity) {
        if (!(targetEntity instanceof ContentEntityObject)) {
            throw new BadRequestException("targetEntity class " + targetEntity.getClass() + " is invalid for like relation");
        }
        return (ContentEntityObject)targetEntity;
    }

    public RelationService.Validator validator() {
        return new ValidatorImpl();
    }

    public <S extends Relatable, T extends Relatable> RelationService.RelatableFinder<T> findTargets(S source, RelationDescriptor<S, T> relationDescriptor) {
        if (relationDescriptor.getRelationName().equals("favourite")) {
            throw new NotImplementedServiceException(RelationService.class.getSimpleName() + ".findTargets not implemented for Favourites");
        }
        return RelatableFinderImpl.targetFinder(source, relationDescriptor, this.relationManager, this.relatableFactory, this.relatableResolver, this.paginationService, this.permissionManager);
    }

    public <S extends Relatable, T extends Relatable> RelationService.RelatableFinder<S> findSources(T target, RelationDescriptor<S, T> relationDescriptor) {
        if (relationDescriptor.getRelationName().equals("favourite")) {
            throw new NotImplementedServiceException(RelationService.class.getSimpleName() + ".findSources not implemented for Favourites");
        }
        return RelatableFinderImpl.sourceFinder(target, relationDescriptor, this.relationManager, this.relatableFactory, this.relatableResolver, this.paginationService, this.permissionManager);
    }

    public <S extends Relatable, T extends Relatable> boolean isRelated(S source, RelationDescriptor<S, T> relationDescriptor, T target) {
        if (relationDescriptor.getRelationName().equals("favourite")) {
            return this.hasFavourite(source, target);
        }
        if (relationDescriptor.getRelationName().equals("like")) {
            return this.likes(source, target);
        }
        this.validator().validateFetch(source, relationDescriptor, target).throwIfNotSuccessful("Could not perform operation");
        return this.relationManager.isRelated(this.relatableResolver.resolve(source), this.relatableResolver.resolve(target), relationDescriptor);
    }

    public <S extends Relatable, T extends Relatable> void removeAllRelationsFromEntityWithType(RelationDescriptor<S, T> relationDescriptor, Relatable relatable) {
        if (relationDescriptor.getRelationName().equals("like")) {
            throw new NotImplementedServiceException(RelationService.class.getSimpleName() + ".removeAllRelationsFromEntityWithType not implemented for Likes");
        }
        if (relationDescriptor.getRelationName().equals("favourite")) {
            throw new NotImplementedServiceException(RelationService.class.getSimpleName() + ".removeAllRelationsFromEntityWithType not implemented for Favourties");
        }
        this.validator().validateDeleteAllWithType(relatable, relationDescriptor).throwIfNotSuccessful();
        this.relationManager.removeAllRelationsFromEntityWithType(relationDescriptor, this.relatableResolver.resolve(relatable));
    }

    private <S extends Relatable, T extends Relatable> boolean likes(S source, T target) {
        if (!(source instanceof com.atlassian.confluence.api.model.people.User)) {
            return false;
        }
        User user = (User)this.relatableResolver.resolve(source);
        RelatableEntity targetEntity = this.relatableResolver.resolve(target);
        if (targetEntity instanceof ContentEntityObject) {
            return this.likeManager.hasLike((ContentEntityObject)targetEntity, user);
        }
        return false;
    }

    private <S extends Relatable, T extends Relatable> boolean hasFavourite(S source, T target) {
        RelatableEntity targetEntity;
        if (!(source instanceof com.atlassian.confluence.api.model.people.User)) {
            return false;
        }
        User user = (User)this.relatableResolver.resolve(source);
        if (!this.hasFavouritesPermission(user, targetEntity = this.relatableResolver.resolve(target))) {
            throw new PermissionException("User not permitted to read favourites for other users or without view permission");
        }
        return this.hasFavourite(user, targetEntity);
    }

    private boolean hasFavourite(User user, RelatableEntity target) {
        if (target instanceof AbstractPage) {
            return this.favouriteManager.isUserFavourite(user, (AbstractPage)target);
        }
        if (target instanceof SpaceDescription) {
            return this.favouriteManager.isUserFavourite(user, ((SpaceDescription)target).getSpace());
        }
        return false;
    }

    private boolean hasFavouritesPermission(User user, Object entity) {
        if (entity instanceof SpaceDescription) {
            return this.favouriteManager.hasPermission(user, ((SpaceDescription)entity).getSpace());
        }
        if (entity instanceof AbstractPage) {
            return this.favouriteManager.hasPermission(user, (AbstractPage)entity);
        }
        return true;
    }

    private <T extends RelationEntity> T handlePotentialHibernateException(Supplier<T> supplier) {
        RelationEntity result;
        try {
            result = (RelationEntity)supplier.get();
        }
        catch (ObjectOptimisticLockingFailureException exception) {
            throw new ConflictException("Attempted to update stale data. Try again.", (Throwable)exception);
        }
        return (T)result;
    }

    private static class RelatableFinderImpl<R extends Relatable>
    implements RelationService.RelatableFinder<R> {
        private static final int DEFAULT_PAGE_SIZE = 200;
        private final FinderMode mode;
        private final Relatable relatable;
        private final RelationDescriptor relationDescriptor;
        private final RelationManager relationManager;
        private final RelatableFactory relatableFactory;
        private final RelatableResolver relatableResolver;
        private final PaginationService paginationService;
        private final PermissionManager permissionManager;

        private RelatableFinderImpl(Relatable relatable, RelationDescriptor relationDescriptor, FinderMode mode, RelationManager relationManager, RelatableFactory relatableFactory, RelatableResolver relatableResolver, PaginationService paginationService, PermissionManager permissionManager) {
            this.relatable = relatable;
            this.relationDescriptor = relationDescriptor;
            this.permissionManager = permissionManager;
            this.mode = (FinderMode)((Object)Preconditions.checkNotNull((Object)((Object)mode)));
            this.relationManager = relationManager;
            this.relatableFactory = relatableFactory;
            this.relatableResolver = relatableResolver;
            this.paginationService = paginationService;
        }

        public static <S extends Relatable, T extends Relatable> RelatableFinderImpl<S> sourceFinder(T target, RelationDescriptor<S, T> relationDescriptor, RelationManager relationManager, RelatableFactory relatableFactory, RelatableResolver relatableResolver, PaginationService paginationService, PermissionManager permissionManager) {
            return new RelatableFinderImpl(target, relationDescriptor, FinderMode.SOURCE, relationManager, relatableFactory, relatableResolver, paginationService, permissionManager);
        }

        public static <S extends Relatable, T extends Relatable> RelatableFinderImpl<T> targetFinder(S source, RelationDescriptor<S, T> relationDescriptor, RelationManager relationManager, RelatableFactory relatableFactory, RelatableResolver relatableResolver, PaginationService paginationService, PermissionManager permissionManager) {
            return new RelatableFinderImpl(source, relationDescriptor, FinderMode.TARGET, relationManager, relatableFactory, relatableResolver, paginationService, permissionManager);
        }

        public PageResponse<R> fetchMany(PageRequest pageRequest, Expansion ... expansions) throws ServiceException {
            RelationQuery query = this.getRelationQuery(this.resolveRelatable(this.relatable));
            PageResponse result = this.paginationService.performPaginationListRequest(LimitedRequestImpl.create((PageRequest)pageRequest, (int)200), this.createPaginationBatch(query), items -> this.relatableFactory.buildFrom(items, new Expansions(expansions)));
            return result;
        }

        private PaginationBatch<RelatableEntity> createPaginationBatch(RelationQuery query) {
            switch (this.mode) {
                case SOURCE: {
                    return input -> this.filterOutNotPermitted(this.relationManager.getSources(query, (LimitedRequest)input));
                }
                case TARGET: {
                    return input -> this.filterOutNotPermitted(this.relationManager.getTargets(query, (LimitedRequest)input));
                }
            }
            throw new IllegalStateException("No such mode : " + String.valueOf((Object)this.mode));
        }

        private PageResponse<RelatableEntity> filterOutNotPermitted(PageResponse<RelatableEntity> results) {
            if (results.getResults().isEmpty() || results.getResults().get(0) instanceof ConfluenceUser) {
                return results;
            }
            ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
            List entities = results.getResults().stream().filter(entity -> this.permissionManager.hasPermission((User)currentUser, Permission.VIEW, entity)).collect(Collectors.toList());
            return PageResponseImpl.from(entities, (boolean)results.hasMore()).build();
        }

        private RelatableEntity resolveRelatable(Relatable relatable) {
            RelatableEntity entity = this.relatableResolver.resolve(relatable);
            if (entity instanceof ContentEntityObject && !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, entity)) {
                throw new PermissionException("No view permissions found for entity in request");
            }
            return entity;
        }

        private RelationQuery getRelationQuery(RelatableEntity entity) {
            RelationQuery.Builder queryBuilder = RelationQuery.create(entity, this.relationDescriptor);
            if (FinderMode.SOURCE.equals((Object)this.mode) && Space.class.isAssignableFrom(this.relationDescriptor.getSourceClass()) || FinderMode.TARGET.equals((Object)this.mode) && Space.class.isAssignableFrom(this.relationDescriptor.getTargetClass())) {
                queryBuilder.contentTypeFilters(IMPLEMENTATION_SPACE_TYPES);
            } else if (FinderMode.SOURCE.equals((Object)this.mode) && Content.class.isAssignableFrom(this.relationDescriptor.getSourceClass()) || FinderMode.TARGET.equals((Object)this.mode) && Content.class.isAssignableFrom(this.relationDescriptor.getTargetClass())) {
                queryBuilder.contentTypeFilters(IMPLEMENTATION_CONTENT_TYPES);
            }
            return queryBuilder.build();
        }

        public int fetchCount() {
            RelationQuery query = this.getRelationQuery(this.relatableResolver.resolve(this.relatable));
            switch (this.mode) {
                case TARGET: {
                    return this.relationManager.getTargetsCount(query);
                }
                case SOURCE: {
                    return this.relationManager.getSourcesCount(query);
                }
            }
            throw new IllegalStateException("No such mode " + String.valueOf((Object)this.mode));
        }

        private static enum FinderMode {
            SOURCE,
            TARGET;

        }
    }

    @ThreadSafe
    private class ValidatorImpl
    implements RelationService.Validator {
        private ValidatorImpl() {
        }

        public <S extends Relatable, T extends Relatable> ValidationResult validateCreate(S source, RelationDescriptor<S, T> relationDescriptor, T target) {
            if (RelationServiceImpl.this.accessModeService.shouldEnforceReadOnlyAccess()) {
                return SimpleValidationResult.NOT_ALLOWED_IN_READ_ONLY_MODE;
            }
            if (this.validateLicense().isNotSuccessful()) {
                return SimpleValidationResult.FORBIDDEN;
            }
            ValidationResult result = this.validateFetch(source, relationDescriptor, target);
            if (!result.isValid()) {
                return result;
            }
            ValidatingRelationDescriptor validatingRelationDescriptor = RelationServiceImpl.this.relationDescriptorRegistry.getValidatingDescriptor(relationDescriptor);
            result = validatingRelationDescriptor.canRelate(source, target);
            if (!result.isSuccessful()) {
                return result;
            }
            result = this.validateCreateDeleteFavourites(relationDescriptor, source, target);
            return result;
        }

        private ValidationResult validatePermissions(Relatable source, Relatable target) {
            ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
            if (currentUser == null) {
                return SimpleValidationResult.builder().authorized(false).addMessage((Message)SimpleMessage.withTranslation((String)"Anonymous is not permitted to perform that operation.")).build();
            }
            if ((source instanceof Content || source instanceof Space) && !RelationServiceImpl.this.permissionManager.hasPermission((User)currentUser, Permission.VIEW, RelationServiceImpl.this.relatableResolver.resolve(source))) {
                return SimpleValidationResult.builder().authorized(false).addMessage((Message)SimpleMessage.withTranslation((String)"Source access denied: no view permissions found.")).build();
            }
            if ((target instanceof Content || target instanceof Space) && !RelationServiceImpl.this.permissionManager.hasPermission((User)currentUser, Permission.VIEW, RelationServiceImpl.this.relatableResolver.resolve(target))) {
                return SimpleValidationResult.builder().authorized(false).addMessage((Message)SimpleMessage.withTranslation((String)"Target access denied: no view permissions found.")).build();
            }
            return SimpleValidationResult.VALID;
        }

        public <S extends Relatable, T extends Relatable> ValidationResult validateDelete(S source, RelationDescriptor<S, T> relationDescriptor, T target) {
            if (RelationServiceImpl.this.accessModeService.shouldEnforceReadOnlyAccess()) {
                return SimpleValidationResult.NOT_ALLOWED_IN_READ_ONLY_MODE;
            }
            if (CollaboratorRelationDescriptor.COLLABORATOR.equals(relationDescriptor)) {
                return SimpleValidationResult.builder().authorized(false).addMessage((Message)SimpleMessage.withTranslation((String)"Deletion of collaborator relations is not allowed")).build();
            }
            this.validateIfNull(source, relationDescriptor, target);
            ValidationResult result = this.validatePermissions(source, target);
            if (!result.isAuthorized()) {
                return result;
            }
            ValidatingRelationDescriptor validatingRelationDescriptor = RelationServiceImpl.this.relationDescriptorRegistry.getValidatingDescriptor(relationDescriptor);
            result = validatingRelationDescriptor.canRelate(source, target);
            if (!result.isSuccessful()) {
                return result;
            }
            result = this.validateCreateDeleteFavourites(relationDescriptor, source, target);
            return result;
        }

        public <S extends Relatable, T extends Relatable> ValidationResult validateFetch(S source, RelationDescriptor<S, T> relationDescriptor, T target) {
            this.validateIfNull(source, relationDescriptor, target);
            ValidationResult result = this.validatePermissions(source, target);
            if (!result.isAuthorized()) {
                return result;
            }
            return this.validateNotNullParameters(source, relationDescriptor, target);
        }

        public <S extends Relatable, T extends Relatable> ValidationResult validateDeleteAllWithType(Relatable relatable, RelationDescriptor<S, T> relationDescriptor) {
            if (RelationServiceImpl.this.accessModeService.shouldEnforceReadOnlyAccess()) {
                return SimpleValidationResult.NOT_ALLOWED_IN_READ_ONLY_MODE;
            }
            ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
            if ((relatable instanceof Content || relatable instanceof Space) && !RelationServiceImpl.this.permissionManager.hasPermission((User)currentUser, Permission.EDIT, RelationServiceImpl.this.relatableResolver.resolve(relatable))) {
                return SimpleValidationResult.builder().authorized(false).addMessage((Message)SimpleMessage.withTranslation((String)"Access denied: no edit permissions found.")).build();
            }
            if (relatable instanceof com.atlassian.confluence.api.model.people.User && !AuthenticatedUserThreadLocal.isAnonymousUser() && !currentUser.getKey().equals(((com.atlassian.confluence.api.model.people.User)relatable).optionalUserKey().orElse(null))) {
                return SimpleValidationResult.builder().authorized(false).addMessage((Message)SimpleMessage.withTranslation((String)"Access denied: no permission to edit relations for this user")).build();
            }
            return SimpleValidationResult.VALID;
        }

        private void validateIfNull(Relatable source, RelationDescriptor relationDescriptor, Relatable target) {
            Preconditions.checkNotNull((Object)source, (Object)"source");
            Preconditions.checkNotNull((Object)relationDescriptor, (Object)"relationDescriptor");
            Preconditions.checkNotNull((Object)target, (Object)"target");
        }

        private ValidationResult validateNotNullParameters(Relatable source, RelationDescriptor relationDescriptor, Relatable target) {
            SimpleValidationResult.Builder resultBuilder = SimpleValidationResult.builder().authorized(true);
            if (AuthenticatedUserThreadLocal.get() == null) {
                resultBuilder.authorized(false).addError("Anonymous user cannot change relations", new Object[0]);
            }
            if (source == null) {
                resultBuilder.addError("Source should not be null", new Object[0]);
            }
            if (target == null) {
                resultBuilder.addError("Target should not be null", new Object[0]);
            }
            if (relationDescriptor == null) {
                resultBuilder.addError("Relation should not be null", new Object[0]);
            }
            if (relationDescriptor != null && relationDescriptor.getRelationName() == null) {
                resultBuilder.addError("Relation name should not be null", new Object[0]);
            }
            if (relationDescriptor != null && (relationDescriptor.getSourceClass() == null || relationDescriptor.getTargetClass() == null)) {
                resultBuilder.addError("Relation classes should not be null", new Object[0]);
            }
            return resultBuilder.build();
        }

        private ValidationResult validateCreateDeleteFavourites(RelationDescriptor relationDescriptor, Relatable source, Relatable target) {
            if (relationDescriptor.getRelationName().equals("favourite")) {
                SimpleValidationResult.Builder resultBuilder = SimpleValidationResult.builder().authorized(true);
                RelatableEntity sourceEntity = RelationServiceImpl.this.relatableResolver.resolve(source);
                RelatableEntity targetEntity = RelationServiceImpl.this.relatableResolver.resolve(target);
                if (!(targetEntity instanceof AbstractPage) && !(targetEntity instanceof SpaceDescription)) {
                    resultBuilder.addError("Expected Favourite relation target to be a Page, Blog Post or Space", new Object[0]);
                }
                if (resultBuilder.hasErrors()) {
                    return resultBuilder.build();
                }
                if (!RelationServiceImpl.this.hasFavouritesPermission((User)sourceEntity, targetEntity)) {
                    resultBuilder.authorized(false).addError("User not permitted to create or delete favourites for other users or without view permission", new Object[0]);
                }
                return resultBuilder.build();
            }
            return SimpleValidationResult.VALID;
        }

        private ValidationResult validateLicense() {
            if (RelationServiceImpl.this.licenseService.retrieve().isExpired() || RelationServiceImpl.this.userChecker != null && RelationServiceImpl.this.userChecker.hasTooManyUsers()) {
                return SimpleValidationResult.FORBIDDEN;
            }
            return SimpleValidationResult.VALID;
        }
    }
}

