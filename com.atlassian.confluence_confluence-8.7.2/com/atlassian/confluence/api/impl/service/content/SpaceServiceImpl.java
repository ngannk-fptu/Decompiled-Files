/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.FormattedBody
 *  com.atlassian.confluence.api.model.content.Label
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.SpaceStatus
 *  com.atlassian.confluence.api.model.content.SpaceType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.longtasks.LongTaskId
 *  com.atlassian.confluence.api.model.longtasks.LongTaskSubmission
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.PaginationBatch
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.reference.Collapsed
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.api.service.content.SpaceService$SpaceContentFinder
 *  com.atlassian.confluence.api.service.content.SpaceService$SpaceFinder
 *  com.atlassian.confluence.api.service.content.SpaceService$Validator
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.api.impl.service.content;

import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.impl.service.content.factory.SpaceFactory;
import com.atlassian.confluence.api.impl.service.content.finder.AbstractFinder;
import com.atlassian.confluence.api.impl.service.content.finder.FinderProxyFactory;
import com.atlassian.confluence.api.impl.service.longtasks.LongTaskFactory;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.FormattedBody;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.longtasks.LongTaskId;
import com.atlassian.confluence.api.model.longtasks.LongTaskSubmission;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.pagination.PaginationBatch;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.reference.Collapsed;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.impl.spaces.SpaceRemovalLongRunningTask;
import com.atlassian.confluence.internal.pages.PageManagerInternal;
import com.atlassian.confluence.internal.spaces.SpaceManagerInternal;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.LongRunningTaskManager;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceServiceImpl
implements SpaceService {
    private static final Logger log = LoggerFactory.getLogger(SpaceServiceImpl.class);
    private static final Expansions DEFAULT_CREATE_UPDATE_EXPANSIONS = new Expansions(ExpansionsParser.parse((String)("homepage,description." + ContentRepresentation.PLAIN)));
    private static final String SPACE_NAME_EXCEEDS_MAX_LENGTH_MESSAGE = "Page name cannot exceed 255 characters";
    private static final int MAX_SPACE_NAME_LENGTH = 255;
    private final SpaceManagerInternal spaceManager;
    private final PageManagerInternal pageManager;
    private final SpaceFactory spaceFactory;
    private final ContentFactory contentFactory;
    private final PaginationService paginationService;
    private final PermissionManager permissionManager;
    private final SpacePermissionManager spacePermissionManager;
    private final ContentService contentService;
    private final LongRunningTaskManager longRunningTaskManager;
    private final LongTaskFactory longTaskFactory;
    private final I18NBeanFactory i18NBeanFactory;
    private final FinderProxyFactory finderProxyFactory;
    private final LabelManager labelManager;
    private final UserChecker userChecker;
    private final AccessModeService accessModeService;
    private final LicenseService licenseService;
    private Predicate<Space> canView = new Predicate<Space>(){

        @Override
        public boolean test(@Nullable Space space) {
            return SpaceServiceImpl.this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, space);
        }

        public String toString() {
            return AuthenticatedUserThreadLocal.get() + " CAN VIEW";
        }
    };

    public SpaceServiceImpl(SpaceManagerInternal spaceManager, SpaceFactory spaceFactory, PageManagerInternal pageManager, ContentFactory contentFactory, PaginationService paginationService, PermissionManager permissionManager, ContentService contentService, SpacePermissionManager spacePermissionManager, LongRunningTaskManager longRunningTaskManager, LongTaskFactory longTaskFactory, I18NBeanFactory i18NBeanFactory, FinderProxyFactory finderProxyFactory, LabelManager labelManager, UserChecker userChecker, AccessModeService accessModeService, LicenseService licenseService) {
        this.spaceManager = spaceManager;
        this.spaceFactory = spaceFactory;
        this.pageManager = pageManager;
        this.contentFactory = contentFactory;
        this.paginationService = paginationService;
        this.permissionManager = permissionManager;
        this.contentService = contentService;
        this.spacePermissionManager = spacePermissionManager;
        this.longRunningTaskManager = longRunningTaskManager;
        this.longTaskFactory = longTaskFactory;
        this.i18NBeanFactory = i18NBeanFactory;
        this.finderProxyFactory = finderProxyFactory;
        this.labelManager = labelManager;
        this.userChecker = userChecker;
        this.accessModeService = accessModeService;
        this.licenseService = licenseService;
    }

    public com.atlassian.confluence.api.model.content.Space create(com.atlassian.confluence.api.model.content.Space space, boolean isPrivate) throws ServiceException {
        this.validator().validateCreate(space, isPrivate).throwIfNotSuccessful("Cannot create Space");
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        String key = space.getKey();
        String name = space.getName();
        String description = this.getDescription(space);
        Space newSpace = isPrivate ? this.spaceManager.createPrivateSpace(key, name, description, user) : this.spaceManager.createSpace(key, name, description, user);
        return this.spaceFactory.buildFrom(newSpace, DEFAULT_CREATE_UPDATE_EXPANSIONS);
    }

    public LongTaskSubmission delete(com.atlassian.confluence.api.model.content.Space spaceToDelete) throws ServiceException {
        this.validator().validateDelete(spaceToDelete).throwIfNotSuccessful("Cannot delete Space");
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        SpaceRemovalLongRunningTask spaceRemovalLongRunningTask = new SpaceRemovalLongRunningTask(spaceToDelete.getKey(), this.spaceManager, user, this.i18NBeanFactory.getI18NBean());
        LongRunningTaskId longRunningTaskId = this.longRunningTaskManager.startLongRunningTask(user, (LongRunningTask)spaceRemovalLongRunningTask);
        LongTaskId taskId = longRunningTaskId.asLongTaskId();
        return this.longTaskFactory.buildSubmission(taskId);
    }

    private String getDescription(com.atlassian.confluence.api.model.content.Space space) {
        Map descriptionMap = space.getDescription();
        if (descriptionMap == null || descriptionMap instanceof Collapsed) {
            return null;
        }
        FormattedBody description = (FormattedBody)descriptionMap.get(SpaceFactory.DEFAULT_DESCRIPTION_REPRESENTATION);
        if (description == null) {
            log.debug("Space description map supplied, but no entry for: " + SpaceFactory.DEFAULT_DESCRIPTION_REPRESENTATION);
            return null;
        }
        return description.getValue();
    }

    public SpaceService.SpaceFinder find(Expansion ... expansions) {
        SpaceFinderImpl finder = new SpaceFinderImpl(expansions);
        return this.finderProxyFactory.createProxy(finder, SpaceService.SpaceFinder.class);
    }

    public com.atlassian.confluence.api.model.content.Space update(com.atlassian.confluence.api.model.content.Space space) throws ServiceException {
        Reference homepageRef;
        Space originalSpace;
        this.validator().validateUpdate(space).throwIfNotSuccessful("Cannot update Space");
        Space updatedSpace = Objects.requireNonNull(this.spaceManager.getSpace(space.getKey()));
        try {
            originalSpace = (Space)updatedSpace.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new ServiceException("Unable to create clone while updating space with key: " + space.getKey(), (Throwable)e);
        }
        updatedSpace.setName(space.getName());
        String description = this.getDescription(space);
        if (description != null) {
            updatedSpace.getDescription().setBodyAsString(description);
        }
        if ((homepageRef = space.getHomepageRef()).exists() && homepageRef.isExpanded()) {
            ContentId homepageId = ((Content)homepageRef.get()).getId();
            updatedSpace.setHomePage(this.pageManager.getPage(homepageId.asLong()));
        }
        this.spaceManager.saveSpace(updatedSpace, originalSpace);
        return this.spaceFactory.buildFrom(updatedSpace, DEFAULT_CREATE_UPDATE_EXPANSIONS);
    }

    public ValidatorImpl validator() {
        return new ValidatorImpl();
    }

    public SpaceService.SpaceContentFinder findContent(com.atlassian.confluence.api.model.content.Space space, Expansion ... expansions) throws NotFoundException {
        SpaceContentImpl finder = new SpaceContentImpl(space, expansions);
        return this.finderProxyFactory.createProxy(finder, SpaceService.SpaceContentFinder.class);
    }

    private Space getInternalSpace(com.atlassian.confluence.api.model.content.Space space) {
        String key = space.getKey();
        Space internalSpace = this.spaceManager.getSpace(key);
        if (!this.checkCanView(internalSpace)) {
            internalSpace = null;
        }
        return internalSpace;
    }

    private Space getInternalSpaceOrThrowNotFound(com.atlassian.confluence.api.model.content.Space space) throws NotFoundException {
        Space internalSpace = this.getInternalSpace(space);
        if (internalSpace == null) {
            throw new NotFoundException("A space with key " + space.getKey() + " does not exist or you do not have permission to view it");
        }
        return internalSpace;
    }

    private boolean checkCanView(Space space) {
        return this.canView.test(space);
    }

    public class SpaceContentImpl
    implements SpaceService.SpaceContentFinder {
        private final com.atlassian.confluence.api.model.content.Space space;
        private final Expansions contentExpansions;
        private List<ContentType> contentTypeFilter = Arrays.asList(ContentType.PAGE, ContentType.BLOG_POST);
        private Depth depth = Depth.ALL;

        SpaceContentImpl(com.atlassian.confluence.api.model.content.Space space, Expansion ... expansions) throws NotFoundException {
            this.space = (com.atlassian.confluence.api.model.content.Space)Preconditions.checkNotNull((Object)space);
            this.contentExpansions = new Expansions((Expansion[])Preconditions.checkNotNull((Object)expansions));
        }

        public SpaceService.SpaceContentFinder withDepth(Depth depth) {
            this.depth = (Depth)Preconditions.checkNotNull((Object)depth);
            return this;
        }

        public Map<ContentType, PageResponse<Content>> fetchMappedByType(PageRequest pageRequest) throws BadRequestException {
            ImmutableMap.Builder contentByType = ImmutableMap.builder();
            for (ContentType type : this.contentTypeFilter) {
                contentByType.put((Object)type, this.fetchMany(type, pageRequest));
            }
            return contentByType.build();
        }

        /*
         * WARNING - void declaration
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public PageResponse<Content> fetchMany(ContentType type, PageRequest pageRequest) throws BadRequestException {
            void var4_7;
            final Space internalSpace = SpaceServiceImpl.this.getInternalSpaceOrThrowNotFound(this.space);
            if (ContentType.PAGE.equals((Object)type)) {
                if (Depth.ALL.equals(this.depth)) {
                    PaginationBatch<Page> paginationBatch = new PaginationBatch<Page>(){

                        public PageResponse<Page> apply(@NonNull LimitedRequest nextRequest) {
                            return SpaceServiceImpl.this.pageManager.getFilteredPages(internalSpace, nextRequest, new Predicate[0]);
                        }
                    };
                    return this.fetchPageOfContent(pageRequest, (PaginationBatch<? extends AbstractPage>)var4_7);
                } else {
                    if (!Depth.ROOT.equals(this.depth)) throw new BadRequestException("Unrecognized depth :" + this.depth);
                    PaginationBatch<Page> paginationBatch = new PaginationBatch<Page>(){

                        public PageResponse<Page> apply(@NonNull LimitedRequest nextRequest) {
                            return SpaceServiceImpl.this.pageManager.getTopLevelPages(internalSpace, nextRequest);
                        }
                    };
                }
                return this.fetchPageOfContent(pageRequest, (PaginationBatch<? extends AbstractPage>)var4_7);
            } else {
                if (!ContentType.BLOG_POST.equals((Object)type)) return PageResponseImpl.empty((boolean)false);
                PaginationBatch<BlogPost> paginationBatch = new PaginationBatch<BlogPost>(){

                    public PageResponse<BlogPost> apply(@NonNull LimitedRequest nextRequest) {
                        return SpaceServiceImpl.this.pageManager.getFilteredBlogPosts(internalSpace, nextRequest, new Predicate[0]);
                    }
                };
            }
            return this.fetchPageOfContent(pageRequest, (PaginationBatch<? extends AbstractPage>)var4_7);
        }

        private PageResponse<Content> fetchPageOfContent(PageRequest pageRequest, PaginationBatch<? extends AbstractPage> batchFetcher) {
            LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)pageRequest, (int)PaginationLimits.content((Expansions)this.contentExpansions));
            return SpaceServiceImpl.this.paginationService.performPaginationListRequest(limitedRequest, batchFetcher, items -> SpaceServiceImpl.this.contentFactory.buildFrom(items, this.contentExpansions));
        }
    }

    public class SpaceFinderImpl
    extends AbstractFinder<com.atlassian.confluence.api.model.content.Space>
    implements SpaceService.SpaceFinder {
        private SpacesQuery.Builder queryBuilder;

        SpaceFinderImpl(Expansion ... expansions) {
            super(expansions);
            this.queryBuilder = SpacesQuery.newQuery();
        }

        public SpaceService.SpaceFinder withKeys(String ... keys) {
            this.queryBuilder.withSpaceKeys(Arrays.asList(keys));
            return this;
        }

        public SpaceService.SpaceFinder withType(com.atlassian.confluence.api.model.content.SpaceType type) {
            this.queryBuilder.withSpaceType(SpaceType.getSpaceType(type.getType()));
            return this;
        }

        public SpaceService.SpaceFinder withStatus(com.atlassian.confluence.api.model.content.SpaceStatus status) {
            try {
                this.queryBuilder.withSpaceStatus(SpaceStatus.valueOf(status.getStatus().toUpperCase()));
            }
            catch (IllegalArgumentException e) {
                throw new BadRequestException("Could not fetch spaces with unrecognized status " + status.getValue());
            }
            return this;
        }

        public SpaceService.SpaceFinder withLabels(com.atlassian.confluence.api.model.content.Label ... labels) {
            this.queryBuilder.withLabels(Arrays.stream(labels).map(label -> {
                Label internalLabel = SpaceServiceImpl.this.labelManager.getLabel(label.getLabel(), Namespace.getNamespace(label.getPrefix()));
                if (internalLabel == null) {
                    throw new BadRequestException("Could not fetch spaces with unrecognized label " + label.getLabel());
                }
                return internalLabel;
            }).collect(Collectors.toList()));
            return this;
        }

        public SpaceService.SpaceFinder withIsFavourited(boolean isFavourited) {
            this.queryBuilder.withIsFavourited(isFavourited);
            return this;
        }

        public SpaceService.SpaceFinder withHasRetentionPolicy(boolean hasRetentionPolicy) {
            this.queryBuilder.withHasRetentionPolicy(hasRetentionPolicy);
            return this;
        }

        public PageResponse<com.atlassian.confluence.api.model.content.Space> fetchMany(PageRequest request) {
            Expansions parsedExpansions = new Expansions(this.expansions);
            return SpaceServiceImpl.this.paginationService.performPaginationListRequest(LimitedRequestImpl.create((PageRequest)request, (int)PaginationLimits.spaces((Expansions)parsedExpansions)), input -> SpaceServiceImpl.this.spaceManager.getSpaces(this.queryBuilder.build(), (LimitedRequest)input, (Predicate<? super Space>)SpaceServiceImpl.this.canView), items -> SpaceServiceImpl.this.spaceFactory.buildFrom(items, parsedExpansions));
        }

        public Optional<com.atlassian.confluence.api.model.content.Space> fetch() {
            List spaces = this.fetchMany((PageRequest)new SimplePageRequest(0, 1)).getResults();
            return spaces.isEmpty() ? Optional.empty() : Optional.of((com.atlassian.confluence.api.model.content.Space)spaces.get(0));
        }
    }

    public class ValidatorImpl
    implements SpaceService.Validator {
        public ValidationResult validateCreate(com.atlassian.confluence.api.model.content.Space space, boolean isPrivate) {
            if (SpaceServiceImpl.this.accessModeService.shouldEnforceReadOnlyAccess()) {
                return SimpleValidationResult.NOT_ALLOWED_IN_READ_ONLY_MODE;
            }
            if (this.validateLicense().isNotSuccessful()) {
                return SimpleValidationResult.FORBIDDEN;
            }
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            if (!SpaceServiceImpl.this.permissionManager.hasCreatePermission((User)user, PermissionManager.TARGET_APPLICATION, Space.class)) {
                return SimpleValidationResult.FORBIDDEN;
            }
            String key = space.getKey();
            if (!Space.isValidGlobalSpaceKey(key)) {
                return new SimpleValidationResult.Builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)("Invalid space key: " + key))).build();
            }
            if (SpaceServiceImpl.this.spaceManager.getSpace(key) != null) {
                return new SimpleValidationResult.Builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)("A space already exists with key " + key))).build();
            }
            String name = space.getName();
            if (StringUtils.isBlank((CharSequence)name)) {
                return new SimpleValidationResult.Builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)"Name is blank. Cannot create space with no name.")).build();
            }
            if (name.length() > 255) {
                return new SimpleValidationResult.Builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)SpaceServiceImpl.SPACE_NAME_EXCEEDS_MAX_LENGTH_MESSAGE)).build();
            }
            ValidationResult descriptionResult = this.validateDescription(space);
            if (!descriptionResult.isValid()) {
                return descriptionResult;
            }
            return SimpleValidationResult.VALID;
        }

        public ValidationResult validateUpdate(com.atlassian.confluence.api.model.content.Space space) {
            if (SpaceServiceImpl.this.accessModeService.shouldEnforceReadOnlyAccess()) {
                return SimpleValidationResult.NOT_ALLOWED_IN_READ_ONLY_MODE;
            }
            if (this.validateLicense().isNotSuccessful()) {
                return SimpleValidationResult.FORBIDDEN;
            }
            String spaceKey = space.getKey();
            Space internalSpace = SpaceServiceImpl.this.spaceManager.getSpace(spaceKey);
            if (internalSpace == null) {
                return new SimpleValidationResult.Builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)("Unknown space key: " + spaceKey))).build();
            }
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            if (!SpaceServiceImpl.this.permissionManager.hasPermission((User)user, Permission.VIEW, internalSpace) || !SpaceServiceImpl.this.permissionManager.hasPermission((User)user, Permission.ADMINISTER, internalSpace)) {
                return SimpleValidationResult.FORBIDDEN;
            }
            String name = space.getName();
            if (StringUtils.isBlank((CharSequence)name)) {
                return new SimpleValidationResult.Builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)"Name is blank. Cannot update space to have no name.")).build();
            }
            if (name.length() > 255) {
                return new SimpleValidationResult.Builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)SpaceServiceImpl.SPACE_NAME_EXCEEDS_MAX_LENGTH_MESSAGE)).build();
            }
            ValidationResult descriptionResult = this.validateDescription(space);
            if (!descriptionResult.isValid()) {
                return descriptionResult;
            }
            Reference homepageRef = space.getHomepageRef();
            if (homepageRef.exists() && homepageRef.isExpanded()) {
                ContentId homepageId = ((Content)homepageRef.get()).getId();
                Content newHomepage = (Content)SpaceServiceImpl.this.contentService.find(new Expansion[]{new Expansion("space")}).withId(homepageId).fetchOrNull();
                if (newHomepage == null) {
                    return new SimpleValidationResult.Builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)("Cannot update space homepage to unknown content with id: " + homepageId))).build();
                }
                if (!spaceKey.equals(newHomepage.getSpace().getKey())) {
                    return new SimpleValidationResult.Builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)("Cannot update space homepage to page in space with key: " + newHomepage.getSpace().getKey()))).build();
                }
            }
            return SimpleValidationResult.VALID;
        }

        private ValidationResult validateDescription(com.atlassian.confluence.api.model.content.Space space) {
            Map description = space.getDescription();
            if (!(description == null || description instanceof Collapsed || description.isEmpty() || description.containsKey(ContentRepresentation.PLAIN))) {
                return new SimpleValidationResult.Builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)"For now, only a space description with PLAIN representation is valid.")).build();
            }
            return SimpleValidationResult.VALID;
        }

        public ValidationResult validateDelete(com.atlassian.confluence.api.model.content.Space space) {
            if (SpaceServiceImpl.this.accessModeService.shouldEnforceReadOnlyAccess()) {
                return SimpleValidationResult.NOT_ALLOWED_IN_READ_ONLY_MODE;
            }
            String key = space.getKey();
            Space internalSpace = SpaceServiceImpl.this.spaceManager.getSpace(key);
            if (internalSpace == null) {
                return new SimpleValidationResult.Builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)("Cannot find space with key: " + key))).withExceptionSupplier(ServiceExceptionSupplier.notFoundExceptionSupplier()).build();
            }
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            List<String> permissionTypes = Arrays.asList("VIEWSPACE", "SETSPACEPERMISSIONS");
            if (!SpaceServiceImpl.this.spacePermissionManager.hasAllPermissions(permissionTypes, internalSpace, user)) {
                return new SimpleValidationResult.Builder().addMessage((Message)SimpleMessage.withTranslation((String)("Not permitted to delete space with key: " + key))).build();
            }
            return SimpleValidationResult.VALID;
        }

        private ValidationResult validateLicense() {
            if (SpaceServiceImpl.this.licenseService.retrieve().isExpired() || SpaceServiceImpl.this.userChecker != null && SpaceServiceImpl.this.userChecker.hasTooManyUsers()) {
                return SimpleValidationResult.FORBIDDEN;
            }
            return SimpleValidationResult.VALID;
        }
    }
}

