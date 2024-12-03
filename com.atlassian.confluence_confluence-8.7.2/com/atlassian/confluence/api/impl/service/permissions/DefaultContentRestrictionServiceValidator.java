/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.people.Anonymous
 *  com.atlassian.confluence.api.model.people.Group
 *  com.atlassian.confluence.api.model.people.Subject
 *  com.atlassian.confluence.api.model.people.SubjectType
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.permissions.ContentRestriction
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.permissions.ContentRestrictionService$Validator
 *  com.atlassian.fugue.Option
 *  com.atlassian.user.GroupManager
 *  com.atlassian.user.User
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.api.impl.service.permissions;

import com.atlassian.confluence.api.impl.service.permissions.ContentRestrictionFactory;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.people.Anonymous;
import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.model.people.Subject;
import com.atlassian.confluence.api.model.people.SubjectType;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.permissions.ContentRestriction;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.permissions.ContentRestrictionService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.ContentEntityManagerInternal;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.fugue.Option;
import com.atlassian.user.GroupManager;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DefaultContentRestrictionServiceValidator
implements ContentRestrictionService.Validator {
    private final ContentEntityManagerInternal contentEntityManager;
    private final PermissionManager permissionManager;
    private final ConfluenceUserResolver confluenceUserResolver;
    private final GroupManager groupManager;

    public DefaultContentRestrictionServiceValidator(ContentEntityManagerInternal contentEntityManager, PermissionManager permissionManager, ConfluenceUserResolver confluenceUserResolver, GroupManager groupManager) {
        this.contentEntityManager = contentEntityManager;
        this.permissionManager = permissionManager;
        this.confluenceUserResolver = confluenceUserResolver;
        this.groupManager = groupManager;
    }

    ValidationResult validateContentExistsAndAccessibleByCurrentUser(ContentId contentId) {
        ContentEntityObject ceo = this.contentEntityManager.getById(contentId);
        if (ceo != null) {
            return this.validateUserCanViewContent(AuthenticatedUserThreadLocal.get(), ceo);
        }
        return SimpleValidationResult.builder().addMessage((Message)SimpleMessage.withTranslation((String)String.format("No content with id <%s> can be found", contentId))).withExceptionSupplier(ServiceExceptionSupplier.notFoundExceptionSupplier()).build();
    }

    ValidationResult validateUserCanViewContent(@Nullable ConfluenceUser user, ContentEntityObject ceo) {
        if (!this.permissionManager.hasPermission((com.atlassian.user.User)user, Permission.VIEW, ceo)) {
            return SimpleValidationResult.builder().addMessage((Message)SimpleMessage.withTranslation((String)String.format("No content with id <%s> can be found", ceo.getContentId()))).withExceptionSupplier(ServiceExceptionSupplier.notFoundExceptionSupplier()).build();
        }
        return SimpleValidationResult.VALID;
    }

    ValidationResult validateInputContentRestrictionsBeforeUpdate(ContentId contentId, Collection<? extends ContentRestriction> contentRestrictions) {
        SimpleValidationResult.Builder validationResultBuilder = SimpleValidationResult.builder().authorized(true);
        if (contentRestrictions == null || contentRestrictions.isEmpty()) {
            return validationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)"No ContentRestrictions provided. Must pass proper restrictions in order to set them")).build();
        }
        HashSet<String> permissionTypesUsedInCurrentServiceCall = new HashSet<String>();
        for (ContentRestriction contentRestriction : contentRestrictions) {
            ContentId idFromJson;
            if (contentRestriction == null) {
                return validationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)"null-length/empty ContentRestrictions are not allowed")).build();
            }
            ValidationResult opKeyValidationResult = this.validateOperationKey(contentRestriction.getOperation());
            if (opKeyValidationResult.isNotSuccessful()) {
                return opKeyValidationResult;
            }
            Option<String> permissionType = ContentRestrictionFactory.determinePermissionType(contentRestriction.getOperation());
            ValidationResult permTypeValidationResult = this.validatePermissionType(permissionType);
            if (permTypeValidationResult.isNotSuccessful()) {
                return permTypeValidationResult;
            }
            if (permissionTypesUsedInCurrentServiceCall.contains(permissionType.get())) {
                String message = String.format("duplicate operation: <%s>. Please provide exactly 1 (one) ContentRestriction object for each \"operation\"", contentRestriction.getOperation());
                return validationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)message)).build();
            }
            permissionTypesUsedInCurrentServiceCall.add((String)permissionType.get());
            if (contentRestriction.getContent() != null && contentRestriction.getContent().existsAndExpanded() && !contentId.equals((Object)(idFromJson = ((Content)contentRestriction.getContent().get()).getId()))) {
                return validationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)String.format("Attempt to change restrictions for contentId %1$s using service call for for contentId %2$s", idFromJson, contentId))).build();
            }
            ValidationResult restrictionsMapValidationResult = this.validateRestrictionsMap(contentRestriction.getRestrictions());
            if (!restrictionsMapValidationResult.isNotSuccessful()) continue;
            return restrictionsMapValidationResult;
        }
        return SimpleValidationResult.VALID;
    }

    ValidationResult validateOperationKey(OperationKey opKey) {
        SimpleValidationResult.Builder validationResultBuilder = SimpleValidationResult.builder().authorized(true);
        if (opKey == null || StringUtils.isBlank((CharSequence)opKey.getValue())) {
            return validationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)"null-length/empty \"operation\" fields are not allowed")).build();
        }
        if (!ContentRestrictionFactory.getSupportedOperationKeys().contains((Object)opKey)) {
            String message = String.format("unsupported operation type: <%1$s>. Please use one of: %2$s", opKey, ContentRestrictionFactory.getSupportedOperationKeys());
            return validationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)message)).build();
        }
        return SimpleValidationResult.VALID;
    }

    ValidationResult validatePermissionType(Option<String> permissionType) {
        if (permissionType == null || permissionType.isEmpty() || StringUtils.isBlank((CharSequence)((CharSequence)permissionType.get()))) {
            return SimpleValidationResult.builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)"Could not identify proper permission type for the restriction.")).build();
        }
        return SimpleValidationResult.VALID;
    }

    ValidationResult validateRestrictionsMap(@Nullable Map<SubjectType, PageResponse<Subject>> restrictionsMap) {
        SimpleValidationResult.Builder validationResultBuilder = SimpleValidationResult.builder().authorized(true);
        if (restrictionsMap == null || restrictionsMap.isEmpty()) {
            return validationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)"ContentRestriction must have valid non empty map of subject types to restriction subjects specified")).build();
        }
        Set unsupportedSubjectTypes = restrictionsMap.keySet().stream().filter(subjectType -> !SubjectType.VALUES.contains(subjectType)).collect(Collectors.toSet());
        if (!unsupportedSubjectTypes.isEmpty()) {
            String message = String.format("Unsupported restriction SubjectType(s): <%1$s>. Please use one of: %2$s", unsupportedSubjectTypes, SubjectType.VALUES);
            return validationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)message)).build();
        }
        if (restrictionsMap.keySet().size() - unsupportedSubjectTypes.size() < 1) {
            String message = String.format("None of the supported restriction SubjectType(s) found. Please use one of: %s", SubjectType.VALUES);
            return validationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)message)).build();
        }
        for (Map.Entry<SubjectType, PageResponse<Subject>> entry : restrictionsMap.entrySet()) {
            PageResponse<Subject> subjectsPageResponse = entry.getValue();
            if (subjectsPageResponse == null || subjectsPageResponse.getResults() == null) {
                String message = String.format("Please provide valid PageResponse/List of Subjects for the SubjectType: <%s>", entry.getKey());
                return validationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)message)).build();
            }
            ValidationResult subjectsValidationResult = this.validateSubjectsExistStrictType(entry.getKey(), entry.getValue().getResults());
            if (!subjectsValidationResult.isNotSuccessful()) continue;
            return subjectsValidationResult;
        }
        return SimpleValidationResult.VALID;
    }

    private ValidationResult validateSubjectsExistStrictType(SubjectType subjectType, Collection<? extends Subject> subjects) {
        if (SubjectType.USER.equals((Object)subjectType)) {
            ValidationResult usersExistenceValidationResult = this.validateUsersExist(subjects);
            if (usersExistenceValidationResult.isNotSuccessful()) {
                return usersExistenceValidationResult;
            }
        } else if (SubjectType.GROUP.equals((Object)subjectType)) {
            ValidationResult groupsExistenceValidationResult = this.validateGroupsExist(subjects);
            if (groupsExistenceValidationResult.isNotSuccessful()) {
                return groupsExistenceValidationResult;
            }
        } else {
            return SimpleValidationResult.builder().addMessage((Message)SimpleMessage.withTranslation((String)("Checking restrictions only supported for User(s) and Group(s), whereas <" + subjectType + "> was provided"))).build();
        }
        return SimpleValidationResult.VALID;
    }

    private ValidationResult validateSubjectsExist(Collection<? extends Subject> subjects) {
        Map<SubjectType, List<Subject>> asMapByType = ((Collection)Optional.ofNullable(subjects).orElse(Collections.emptySet())).stream().distinct().collect(Collectors.groupingBy(Subject::getSubjectType));
        for (Map.Entry<SubjectType, List<Subject>> entry : asMapByType.entrySet()) {
            ValidationResult r = this.validateSubjectsExistStrictType(entry.getKey(), (Collection<? extends Subject>)entry.getValue());
            if (!r.isNotSuccessful()) continue;
            return r;
        }
        return SimpleValidationResult.VALID;
    }

    private ValidationResult validateUsersExist(@NonNull Collection<? extends Subject> subjects) {
        for (Subject subject : subjects) {
            if (subject == null) {
                return SimpleValidationResult.builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)"User cannot be <null>/empty/unspecified")).build();
            }
            if (subject instanceof Anonymous) {
                return SimpleValidationResult.builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)"Anonymous user is not allowed in ContentRestrictions API")).build();
            }
            if (!(subject instanceof User)) {
                String message = String.format("Subject <%s> is not a User. Please specify valid users under the \"user\" mapping part", subject);
                return SimpleValidationResult.builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)message)).build();
            }
            User user = (User)subject;
            Optional<Object> maybeExistingUser = Optional.empty();
            Object secondMessage = "";
            try {
                maybeExistingUser = this.confluenceUserResolver.getExistingByApiUser(user);
            }
            catch (Exception e) {
                secondMessage = (String)secondMessage + e.getMessage();
            }
            if (maybeExistingUser.isPresent()) continue;
            String message = String.format("Subject <%s> is not a valid existing user", user);
            SimpleValidationResult.Builder validationBuilder = SimpleValidationResult.builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)message));
            if (StringUtils.isNotBlank((CharSequence)secondMessage)) {
                validationBuilder.addMessage((Message)SimpleMessage.withTranslation((String)secondMessage));
            }
            return validationBuilder.build();
        }
        return SimpleValidationResult.VALID;
    }

    private ValidationResult validateGroupsExist(@NonNull Collection<? extends Subject> subjects) {
        for (Subject subject : subjects) {
            if (subject == null) {
                return SimpleValidationResult.builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)"Group cannot be <null>/empty/unspecified")).build();
            }
            if (!(subject instanceof Group)) {
                String message = String.format("Subject <%s> is not a Group. Please specify valid groups under the \"groups\" mapping part", subject);
                return SimpleValidationResult.builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)message)).build();
            }
            Group group = (Group)subject;
            Option maybeExistingGroup = Option.none();
            Object secondMessage = "";
            try {
                maybeExistingGroup = Option.option((Object)this.groupManager.getGroup(group.getName()));
            }
            catch (Exception e) {
                secondMessage = (String)secondMessage + e.getMessage();
            }
            if (!maybeExistingGroup.isEmpty() && maybeExistingGroup.getOrNull() != null) continue;
            String message = String.format("Subject <%s> is not a valid existing group", group);
            SimpleValidationResult.Builder validationBuilder = SimpleValidationResult.builder().authorized(true).addMessage((Message)SimpleMessage.withTranslation((String)message));
            if (StringUtils.isNotBlank((CharSequence)secondMessage)) {
                validationBuilder.addMessage((Message)SimpleMessage.withTranslation((String)secondMessage));
            }
            return validationBuilder.build();
        }
        return SimpleValidationResult.VALID;
    }

    ValidationResult validateUserCanAlterRestrictions(ConfluenceUser user, ContentEntityObject ceo) {
        ValidationResult versionValidationResult = this.validateContentVersionIsOkForRestrictionsOperations(ceo);
        if (versionValidationResult.isNotSuccessful()) {
            return versionValidationResult;
        }
        ValidationResult allowedValidationResult = this.validateUserCanAlterRestrictionsOnLatestVersion(user, ceo);
        if (allowedValidationResult.isNotSuccessful()) {
            return allowedValidationResult;
        }
        return SimpleValidationResult.VALID;
    }

    private ValidationResult validateUserCanAlterRestrictionsOnLatestVersion(ConfluenceUser user, ContentEntityObject ceo) {
        boolean canAlterRestrictionsOnLatestVersion;
        boolean bl = canAlterRestrictionsOnLatestVersion = this.permissionManager.hasPermission((com.atlassian.user.User)user, Permission.SET_PERMISSIONS, ceo) && this.permissionManager.hasPermission((com.atlassian.user.User)user, Permission.EDIT, ceo);
        if (canAlterRestrictionsOnLatestVersion) {
            return SimpleValidationResult.VALID;
        }
        return SimpleValidationResult.builder().authorized(false).addMessage((Message)SimpleMessage.withTranslation((String)String.format("Not enough permissions to alter ContentRestrictions on a content <%s>", ceo))).build();
    }

    ValidationResult validateContentVersionIsOkForRestrictionsOperations(@Nullable ContentEntityObject ceo) {
        boolean isVersionSuitable;
        boolean bl = isVersionSuitable = ceo != null && (ceo.isLatestVersion() && ceo.isCurrent() || ceo.isUnpublished());
        if (!isVersionSuitable) {
            String message = String.format("Cannot find content <%s>. Outdated version/old_draft/trashed? Please provide valid ContentId.", ceo);
            return SimpleValidationResult.builder().addMessage((Message)SimpleMessage.withTranslation((String)message)).withExceptionSupplier(ServiceExceptionSupplier.notFoundExceptionSupplier()).build();
        }
        return SimpleValidationResult.VALID;
    }

    ValidationResult validateContentExistsAndCanViewAndCanEditAndCanEditRestrictions(ContentId contentId) {
        ContentEntityObject ceo;
        ValidationResult canGetValidationResult = this.validateGetRestrictions(contentId);
        if (canGetValidationResult.isNotSuccessful()) {
            return canGetValidationResult;
        }
        ConfluenceUser currentlyLoggedInUser = AuthenticatedUserThreadLocal.get();
        ValidationResult canAlterValidationResult = this.validateUserCanAlterRestrictions(currentlyLoggedInUser, ceo = this.contentEntityManager.getById(contentId));
        if (canAlterValidationResult.isNotSuccessful()) {
            return canAlterValidationResult;
        }
        return SimpleValidationResult.VALID;
    }

    public ValidationResult validateUpdateRestrictions(ContentId contentId, Collection<? extends ContentRestriction> contentRestrictions) {
        ValidationResult existsAndCanDo = this.validateContentExistsAndCanViewAndCanEditAndCanEditRestrictions(contentId);
        if (existsAndCanDo.isNotSuccessful()) {
            return existsAndCanDo;
        }
        ValidationResult inputValidationResult = this.validateInputContentRestrictionsBeforeUpdate(contentId, contentRestrictions);
        if (inputValidationResult.isNotSuccessful()) {
            return inputValidationResult;
        }
        return SimpleValidationResult.VALID;
    }

    public ValidationResult validateAddRestrictions(ContentId contentId, Collection<? extends ContentRestriction> contentRestrictions) {
        return this.validateUpdateRestrictions(contentId, contentRestrictions);
    }

    public ValidationResult validateDeleteAllDirectRestrictions(ContentId contentId) {
        ValidationResult existsAndCanDo = this.validateContentExistsAndCanViewAndCanEditAndCanEditRestrictions(contentId);
        if (existsAndCanDo.isNotSuccessful()) {
            return existsAndCanDo;
        }
        return SimpleValidationResult.VALID;
    }

    public ValidationResult validateHasDirectRestrictionsForSubject(ContentId contentId, OperationKey operationKey, Subject subject) {
        ValidationResult contentAndOpKeyValidationResult = this.validateGetRestrictionsForOperation(contentId, operationKey);
        if (contentAndOpKeyValidationResult.isNotSuccessful()) {
            return contentAndOpKeyValidationResult;
        }
        if (!(subject instanceof User) && !(subject instanceof Group)) {
            if (subject instanceof Anonymous) {
                return SimpleValidationResult.builder().addMessage((Message)SimpleMessage.withTranslation((String)"Operations on ContentRestrictions for <Anonymous> user are not supported.")).build();
            }
            return SimpleValidationResult.builder().addMessage((Message)SimpleMessage.withTranslation((String)("Checking restrictions only supported for User(s) and Group(s), whereas <" + subject + "> was provided"))).build();
        }
        return SimpleValidationResult.VALID;
    }

    public ValidationResult validateDeleteDirectRestrictionForSubject(ContentId contentId, OperationKey operationKey, Subject subject) {
        ValidationResult restrictionExistenceValidationResult = this.validateHasDirectRestrictionsForSubject(contentId, operationKey, subject);
        if (restrictionExistenceValidationResult.isNotSuccessful()) {
            return restrictionExistenceValidationResult;
        }
        ValidationResult canDoDeleteValidationResult = this.validateDeleteAllDirectRestrictions(contentId);
        if (canDoDeleteValidationResult.isNotSuccessful()) {
            return canDoDeleteValidationResult;
        }
        return SimpleValidationResult.VALID;
    }

    public ValidationResult validateAddDirectRestrictionForSubject(ContentId contentId, OperationKey operationKey, Subject subject) {
        ValidationResult canDoValitationResult = this.validateContentExistsAndCanViewAndCanEditAndCanEditRestrictions(contentId);
        if (canDoValitationResult.isNotSuccessful()) {
            return canDoValitationResult;
        }
        ValidationResult subjAndOpValidatoinResult = this.validateHasDirectRestrictionsForSubject(contentId, operationKey, subject);
        if (subjAndOpValidatoinResult.isNotSuccessful()) {
            return subjAndOpValidatoinResult;
        }
        ValidationResult realSubjectValidation = this.validateSubjectsExist(Collections.singleton(subject));
        if (realSubjectValidation.isNotSuccessful()) {
            return realSubjectValidation;
        }
        return SimpleValidationResult.VALID;
    }

    public ValidationResult validateGetRestrictions(ContentId contentId) {
        ValidationResult existsAndAccessibleValidationResult = this.validateContentExistsAndAccessibleByCurrentUser(contentId);
        if (existsAndAccessibleValidationResult.isNotSuccessful()) {
            return existsAndAccessibleValidationResult;
        }
        ValidationResult versionCheckResult = this.validateContentVersionIsOkForRestrictionsOperations(this.contentEntityManager.getById(contentId));
        if (versionCheckResult.isNotSuccessful()) {
            return versionCheckResult;
        }
        return SimpleValidationResult.VALID;
    }

    public ValidationResult validateGetRestrictionsForOperation(ContentId contentId, OperationKey opKey) {
        ValidationResult opKeyValidationResult = this.validateOperationKey(opKey);
        if (opKeyValidationResult.isNotSuccessful()) {
            return SimpleValidationResult.builder().authorized(opKeyValidationResult.isAuthorized()).addErrors((List)Lists.newArrayList((Iterable)opKeyValidationResult.getErrors())).withExceptionSupplier(ServiceExceptionSupplier.notFoundExceptionSupplier()).build();
        }
        ValidationResult canGetValidationResult = this.validateGetRestrictions(contentId);
        if (canGetValidationResult.isNotSuccessful()) {
            return canGetValidationResult;
        }
        return SimpleValidationResult.VALID;
    }
}

