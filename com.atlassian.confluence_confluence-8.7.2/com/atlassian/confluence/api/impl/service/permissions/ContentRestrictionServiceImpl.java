/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.people.Group
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.Subject
 *  com.atlassian.confluence.api.model.people.SubjectType
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.permissions.ContentRestriction
 *  com.atlassian.confluence.api.model.permissions.ContentRestrictionsPageResponse
 *  com.atlassian.confluence.api.model.permissions.ContentRestrictionsPageResponse$ContentRestrictionPageResponseBuilder
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.api.model.reference.ModelMapBuilder
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.nav.Navigation$ContentRestrictionByOperationNav
 *  com.atlassian.confluence.api.nav.NavigationService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.InternalServerException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.confluence.api.service.permissions.ContentRestrictionService
 *  com.atlassian.confluence.api.service.permissions.ContentRestrictionService$Validator
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 *  com.atlassian.fugue.Maybe
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.api.impl.service.permissions;

import com.atlassian.confluence.api.impl.service.content.factory.Fauxpansions;
import com.atlassian.confluence.api.impl.service.permissions.ContentRestrictionFactory;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.people.Subject;
import com.atlassian.confluence.api.model.people.SubjectType;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.permissions.ContentRestriction;
import com.atlassian.confluence.api.model.permissions.ContentRestrictionsPageResponse;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.InternalServerException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.api.service.permissions.ContentRestrictionService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.internal.ContentEntityManagerInternal;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.security.PermissionUtils;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.fugue.Maybe;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentRestrictionServiceImpl
implements ContentRestrictionService {
    private static final Logger log = LoggerFactory.getLogger(ContentRestrictionServiceImpl.class);
    private static final SimplePageRequest DEFAULT_RESTRICTIONS_PAGE_REQUEST = new SimplePageRequest(0, PaginationLimits.restrictionSubjects());
    private final ContentEntityManagerInternal contentEntityManager;
    private final ContentPermissionManager contentPermissionManager;
    private final ContentRestrictionFactory contentRestrictionFactory;
    private final NavigationService navigationService;
    private final ConfluenceUserResolver confluenceUserResolver;
    private final ContentRestrictionService.Validator contentRestrictionServiceValidator;

    public ContentRestrictionServiceImpl(ContentEntityManagerInternal contentEntityManager, ContentPermissionManager contentPermissionManager, ContentRestrictionFactory contentRestrictionFactory, ConfluenceUserResolver confluenceUserResolver, NavigationService navigationService, ContentRestrictionService.Validator contentRestrictionServiceValidator) {
        this.contentEntityManager = contentEntityManager;
        this.contentPermissionManager = contentPermissionManager;
        this.contentRestrictionFactory = contentRestrictionFactory;
        this.confluenceUserResolver = confluenceUserResolver;
        this.navigationService = navigationService;
        this.contentRestrictionServiceValidator = contentRestrictionServiceValidator;
    }

    public ContentRestrictionService.Validator validator() {
        return this.contentRestrictionServiceValidator;
    }

    public Map<OperationKey, ContentRestriction> getRestrictionsGroupByOperation(ContentId target, Expansion ... allExpansions) throws ServiceException {
        this.contentRestrictionServiceValidator.validateGetRestrictions(target).throwIfNotSuccessful();
        Expansions expansions = new Expansions(allExpansions);
        ModelMapBuilder restrictions = ModelMapBuilder.newInstance();
        for (OperationKey opKey : ContentRestrictionFactory.getSupportedOperationKeys()) {
            Fauxpansions operationExpansions = Fauxpansions.fauxpansions(expansions, opKey.toString());
            if (!operationExpansions.canExpand()) {
                restrictions.addCollapsedEntry((Object)opKey);
                continue;
            }
            ContentRestriction restriction = this.doGetRestrictionsForOperation(target, opKey, (PageRequest)DEFAULT_RESTRICTIONS_PAGE_REQUEST, operationExpansions.getSubExpansions().toArray());
            restrictions.put((Object)opKey, (Object)restriction);
        }
        Navigation.ContentRestrictionByOperationNav navBuilder = this.navigationService.createNavigation().content(Content.buildReference((ContentSelector)ContentSelector.from((Content)Content.builder().id(target).build()))).restrictionByOperation();
        return restrictions.navigable((Navigation.Builder)navBuilder).build();
    }

    public ContentRestriction getRestrictionsForOperation(ContentId target, OperationKey operationKey, PageRequest pageRequest, Expansion ... allExpansions) throws ServiceException {
        this.contentRestrictionServiceValidator.validateGetRestrictionsForOperation(target, operationKey).throwIfNotSuccessful();
        return this.doGetRestrictionsForOperation(target, operationKey, pageRequest, allExpansions);
    }

    ContentRestriction doGetRestrictionsForOperation(ContentId target, OperationKey operationKey, PageRequest pageRequest, Expansion[] allExpansions) {
        ContentPermissionSet permissionSet;
        Expansions expansions = new Expansions(allExpansions);
        LimitedRequest limitedReq = LimitedRequestImpl.create((PageRequest)pageRequest, (int)PaginationLimits.restrictionSubjects());
        String permissionType = (String)ContentRestrictionFactory.determinePermissionType(operationKey).get();
        ContentEntityObject ceo = this.contentEntityManager.getById(target);
        ContentPermissionSet contentPermissionSet = permissionSet = ceo != null ? ceo.getContentPermissionSet(permissionType) : null;
        if (permissionSet == null) {
            permissionSet = new ContentPermissionSet(permissionType, ceo);
        }
        return this.contentRestrictionFactory.buildFrom(permissionSet, limitedReq, expansions);
    }

    public ContentRestrictionsPageResponse getRestrictions(ContentId target, PageRequest pageRequest, Expansion ... allExpansions) throws ServiceException {
        this.contentRestrictionServiceValidator.validateGetRestrictions(target).throwIfNotSuccessful();
        List restrictionsList = ContentRestrictionFactory.getSupportedOperationKeys().stream().map(opKey -> this.doGetRestrictionsForOperation(target, (OperationKey)opKey, pageRequest, allExpansions)).collect(Collectors.toList());
        Link byOperation = new Link(new LinkType("byOperation"), this.navigationService.createNavigation().content(ContentSelector.from((Content)Content.builder().id(target).build())).restrictionByOperation().buildAbsolute());
        ContentEntityObject ceo = Objects.requireNonNull(this.contentEntityManager.getById(target));
        return ((ContentRestrictionsPageResponse.ContentRestrictionPageResponseBuilder)((ContentRestrictionsPageResponse.ContentRestrictionPageResponseBuilder)((ContentRestrictionsPageResponse.ContentRestrictionPageResponseBuilder)ContentRestrictionsPageResponse.builder().withContentId(target).addAll(restrictionsList)).withRestrictionsHash(PermissionUtils.getRestrictionsHash(ceo)).pageRequest(pageRequest)).hasMore(false)).addLink(byOperation).build();
    }

    public ContentRestrictionsPageResponse updateRestrictions(ContentId target, Collection<? extends ContentRestriction> contentRestrictions, Expansion ... expansions) throws ServiceException {
        this.contentRestrictionServiceValidator.validateUpdateRestrictions(target, contentRestrictions).throwIfNotSuccessful();
        ContentEntityObject ceo = Objects.requireNonNull(this.contentEntityManager.getById(target));
        Map<String, Collection<ContentPermission>> wannabeNewContentPermissions = this.getMergedContentPermissions(ceo, contentRestrictions, MergeMode.REPLACE);
        this.validateSelfAccessRetained(wannabeNewContentPermissions).throwIfNotSuccessful();
        this.contentPermissionManager.setContentPermissions(wannabeNewContentPermissions, ceo);
        return this.getRestrictions(target, (PageRequest)DEFAULT_RESTRICTIONS_PAGE_REQUEST, expansions);
    }

    public ContentRestrictionsPageResponse addRestrictions(ContentId target, Collection<? extends ContentRestriction> contentRestrictions, Expansion ... expansions) throws ServiceException {
        this.contentRestrictionServiceValidator.validateAddRestrictions(target, contentRestrictions).throwIfNotSuccessful();
        ContentEntityObject ceo = Objects.requireNonNull(this.contentEntityManager.getById(target));
        Map<String, Collection<ContentPermission>> wannabeNewContentPermissions = this.getMergedContentPermissions(ceo, contentRestrictions, MergeMode.ADD);
        this.validateSelfAccessRetained(wannabeNewContentPermissions).throwIfNotSuccessful();
        this.contentPermissionManager.setContentPermissions(wannabeNewContentPermissions, ceo);
        return this.getRestrictions(target, (PageRequest)DEFAULT_RESTRICTIONS_PAGE_REQUEST, expansions);
    }

    public ContentRestrictionsPageResponse deleteAllDirectRestrictions(ContentId target, Expansion ... expansions) throws ServiceException {
        this.contentRestrictionServiceValidator.validateDeleteAllDirectRestrictions(target).throwIfNotSuccessful();
        ContentEntityObject ceo = this.contentEntityManager.getById(target);
        Map<String, Collection<ContentPermission>> noPermissions = ContentRestrictionFactory.getSupportedOperationKeys().stream().map(ContentRestrictionFactory::determinePermissionType).collect(Collectors.toMap(Maybe::get, x -> Collections.emptyList()));
        this.contentPermissionManager.setContentPermissions(noPermissions, ceo);
        return this.getRestrictions(target, (PageRequest)DEFAULT_RESTRICTIONS_PAGE_REQUEST, expansions);
    }

    public boolean hasDirectRestrictionForSubject(ContentId contentId, OperationKey operationKey, Subject subject) throws ServiceException {
        this.validator().validateHasDirectRestrictionsForSubject(contentId, operationKey, subject).throwIfNotSuccessful();
        ContentEntityObject ceo = this.contentEntityManager.getById(contentId);
        String permissionType = (String)ContentRestrictionFactory.determinePermissionType(operationKey).get();
        Optional<ContentPermission> maybeTargetContentPermission = this.getPreExistingContentPermissionForSubject(ceo, permissionType, subject);
        return maybeTargetContentPermission.isPresent();
    }

    public void deleteDirectRestrictionForSubject(ContentId contentId, OperationKey operationKey, Subject subject) throws ServiceException {
        this.validator().validateDeleteDirectRestrictionForSubject(contentId, operationKey, subject).throwIfNotSuccessful();
        String permissionType = (String)ContentRestrictionFactory.determinePermissionType(operationKey).get();
        ContentEntityObject ceo = Objects.requireNonNull(this.contentEntityManager.getById(contentId));
        ContentPermission targetContentPermission = this.getPreExistingContentPermissionForSubject(ceo, permissionType, subject).orElseThrow(() -> new NotFoundException("Cannot delete the restriction specified since it doesn't exist."));
        ContentRestriction restrictionToDelete = ContentRestriction.builder().operation(operationKey).restrictions(Collections.singletonMap(subject.getSubjectType(), PageResponseImpl.fromSingle((Object)subject, (boolean)false).build())).build();
        Map<String, Collection<ContentPermission>> wannabeNewContentPermissions = this.getMergedContentPermissions(ceo, Collections.singleton(restrictionToDelete), MergeMode.SUBTRACT);
        this.validateSelfAccessRetained(wannabeNewContentPermissions).throwIfNotSuccessful();
        this.contentPermissionManager.removeContentPermission(targetContentPermission);
    }

    public void addDirectRestrictionForSubject(ContentId contentId, OperationKey operationKey, Subject subject) throws ServiceException {
        this.validator().validateAddDirectRestrictionForSubject(contentId, operationKey, subject).throwIfNotSuccessful();
        ContentEntityObject ceo = Objects.requireNonNull(this.contentEntityManager.getById(contentId));
        ContentRestriction restrictionToAdd = ContentRestriction.builder().operation(operationKey).restrictions((Map)ImmutableMap.of((Object)subject.getSubjectType(), (Object)PageResponseImpl.fromSingle((Object)subject, (boolean)false).build())).build();
        Map<String, Collection<ContentPermission>> wannabeNewContentPermissions = this.getMergedContentPermissions(ceo, Collections.singleton(restrictionToAdd), MergeMode.ADD);
        this.validateSelfAccessRetained(wannabeNewContentPermissions).throwIfNotSuccessful();
        this.contentPermissionManager.setContentPermissions(wannabeNewContentPermissions, ceo);
    }

    protected @NonNull Map<String, Collection<ContentPermission>> getMergedContentPermissions(@NonNull ContentEntityObject ceo, @NonNull Collection<? extends ContentRestriction> givenContentRestrictions, @NonNull MergeMode mergeMode) {
        HashMap<String, Collection> results = new HashMap<String, Collection>();
        for (ContentRestriction contentRestriction : givenContentRestrictions) {
            String contentPermissionType = (String)ContentRestrictionFactory.determinePermissionType(contentRestriction.getOperation()).get();
            Map<SubjectType, Optional<Collection<ContentPermission>>> asMapOfContentPermissions = SubjectType.VALUES.stream().collect(Collectors.toMap(subjectType -> subjectType, subjectType -> Optional.ofNullable((PageResponse)givenRestriction.getRestrictions().get(subjectType)).map(PageResponse::getResults).map(subjects -> subjects.stream().map(subj -> this.toContentPermissionStrict(contentPermissionType, (SubjectType)subjectType, (Subject)subj)).collect(Collectors.toSet()))));
            Collection mergedContentPermissionsOfThisType = results.computeIfAbsent(contentPermissionType, x -> new HashSet());
            Set<ContentPermission> set = this.doMergeContentPermissions(ceo, contentPermissionType, mergeMode, asMapOfContentPermissions);
            mergedContentPermissionsOfThisType.addAll(set);
        }
        return ImmutableMap.copyOf(results);
    }

    private @NonNull Set<ContentPermission> doMergeContentPermissions(@NonNull ContentEntityObject ceo, @NonNull String contentPermissionType, @NonNull MergeMode mergeMode, @NonNull Map<SubjectType, Optional<Collection<ContentPermission>>> permissionsMap) {
        HashSet<ContentPermission> result = new HashSet<ContentPermission>();
        switch (mergeMode) {
            case REPLACE: {
                SubjectType.VALUES.forEach(subjectType -> {
                    Optional<Set<ContentPermission>> maybeContentPermissions = permissionsMap.getOrDefault(subjectType, Optional.empty());
                    result.addAll((Collection)maybeContentPermissions.orElse(this.getPreExistingContentPermissionsForSubjectType(ceo, contentPermissionType, (SubjectType)subjectType)));
                });
                break;
            }
            case ADD: {
                Set<ContentPermission> preExistingContentPermissions = this.getPreExistingContentPermissions(ceo, contentPermissionType);
                Set newPermissions = permissionsMap.values().stream().flatMap(c -> ((Collection)c.orElse(Collections.emptySet())).stream()).collect(Collectors.toSet());
                result.addAll(preExistingContentPermissions);
                result.addAll(newPermissions);
                break;
            }
            case SUBTRACT: {
                Set<ContentPermission> preExistingContentPermissions = this.getPreExistingContentPermissions(ceo, contentPermissionType);
                Set newPermissions = permissionsMap.values().stream().flatMap(c -> ((Collection)c.orElse(Collections.emptySet())).stream()).collect(Collectors.toSet());
                result.addAll(preExistingContentPermissions);
                result.removeAll(newPermissions);
                break;
            }
            default: {
                String message = "Unsupported MergeMode. Should never end up like that!";
                log.error("Unsupported MergeMode. Should never end up like that!");
                throw new InternalServerException("Exception while modifying ContentRestrictions", (Throwable)new IllegalStateException("Unsupported MergeMode. Should never end up like that!"));
            }
        }
        return result;
    }

    private ContentPermission toContentPermissionStrict(String contentPermissionType, SubjectType subjectType, Subject subject) {
        if (SubjectType.USER.equals((Object)subjectType)) {
            return ContentPermission.createUserPermission(contentPermissionType, this.confluenceUserResolver.getExistingUserByPerson((Person)((User)subject)));
        }
        if (SubjectType.GROUP.equals((Object)subjectType)) {
            return ContentPermission.createGroupPermission(contentPermissionType, ((Group)subject).getName());
        }
        throw this.throwableUnsupportedSubjectType(subjectType);
    }

    protected @NonNull NotImplementedServiceException throwableUnsupportedSubjectType(@Nullable Object something) {
        String message = String.format("Restrictions operations on SubjectType <%1$s> are not implemented yet. Please use one of <%2$s>", something, SubjectType.VALUES);
        log.error(message);
        return new NotImplementedServiceException(message, (Exception)new UnsupportedOperationException(message));
    }

    protected @NonNull Set<ContentPermission> getPreExistingContentPermissions(@Nullable ContentEntityObject ceo, @Nullable String permissionType, @Nullable Predicate<ContentPermission> filterBy) {
        return this.getPreExistingContentPermissions(ceo, permissionType).stream().filter(filterBy != null ? filterBy : x -> true).collect(Collectors.toSet());
    }

    protected @NonNull Set<ContentPermission> getPreExistingContentPermissions(@Nullable ContentEntityObject ceo, @Nullable String permissionType) {
        if (ceo == null || permissionType == null || ceo.getContentPermissionSet(permissionType) == null) {
            return Collections.emptySet();
        }
        return new HashSet<ContentPermission>(ceo.getContentPermissionSet(permissionType).contentPermissionsCopy());
    }

    protected @NonNull Set<ContentPermission> getPreExistingContentPermissionsForSubjectType(@Nullable ContentEntityObject ceo, @Nullable String permissionType, @Nullable SubjectType subjectType) {
        if (SubjectType.USER.equals((Object)subjectType)) {
            return this.getPreExistingContentPermissions(ceo, permissionType, ContentPermission::isUserPermission);
        }
        if (SubjectType.GROUP.equals((Object)subjectType)) {
            return this.getPreExistingContentPermissions(ceo, permissionType, ContentPermission::isGroupPermission);
        }
        throw this.throwableUnsupportedSubjectType(subjectType);
    }

    protected @NonNull Optional<ContentPermission> getPreExistingContentPermissionForSubject(@Nullable ContentEntityObject ceo, @Nullable String permissionType, @Nullable Subject subject) {
        if (subject instanceof User) {
            return this.getPreExistingContentPermissionForUser(ceo, permissionType, (User)subject);
        }
        if (subject instanceof Group) {
            return this.getPreExistingContentPermissionForGroup(ceo, permissionType, (Group)subject);
        }
        throw new BadRequestException("ContentRestrictions only support User(s) and Group(s)");
    }

    protected @NonNull Optional<ContentPermission> getPreExistingContentPermissionForUser(@Nullable ContentEntityObject ceo, @Nullable String permissionType, @NonNull User user) {
        Optional<ConfluenceUser> maybeUser = this.confluenceUserResolver.getExistingByApiUser(user);
        if (!maybeUser.isPresent()) {
            throw new NotFoundException("Specified User does not exist");
        }
        return this.getPreExistingContentPermissions(ceo, permissionType, ContentPermission::isUserPermission).stream().filter(cp -> Objects.equals(maybeUser.get(), cp.getUserSubject())).findFirst();
    }

    protected @NonNull Optional<ContentPermission> getPreExistingContentPermissionForGroup(@Nullable ContentEntityObject ceo, @Nullable String permissionType, @Nullable Group group) {
        if (group == null || group.getName() == null) {
            throw new NotFoundException("Specified Group does not exist");
        }
        return this.getPreExistingContentPermissions(ceo, permissionType, ContentPermission::isGroupPermission).stream().filter(cp -> Objects.equals(group.getName(), cp.getGroupName())).findFirst();
    }

    protected @NonNull ValidationResult validateSelfAccessRetained(@NonNull Map<String, Collection<ContentPermission>> contentPermissionByPermissionTypeMap) {
        ArrayList<OperationKey> faultyOpKeys = new ArrayList<OperationKey>();
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        for (Map.Entry<String, Collection<ContentPermission>> permissionsEntry : contentPermissionByPermissionTypeMap.entrySet()) {
            boolean selfAccessRetainedForOperation = permissionsEntry.getValue().isEmpty() || permissionsEntry.getValue().stream().filter(ContentPermission::isUserPermission).map(ContentPermission::getUserSubject).anyMatch(user -> Objects.equals(currentUser, user));
            if (selfAccessRetainedForOperation) continue;
            faultyOpKeys.add(ContentRestrictionFactory.determineOpKey(permissionsEntry.getKey()));
        }
        if (!faultyOpKeys.isEmpty()) {
            String confluenceSuicideMessage = String.format("Provided ContentRestrictions evicts current user (you) from: %s. Must include yourself in \"user\" sections for READ and/or UPDATE when restricting those operations. Must not provide restrictions which when applied result in current situation.", faultyOpKeys);
            return SimpleValidationResult.builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)confluenceSuicideMessage)).build();
        }
        return SimpleValidationResult.VALID;
    }

    public static enum MergeMode {
        ADD,
        REPLACE,
        SUBTRACT;

    }
}

