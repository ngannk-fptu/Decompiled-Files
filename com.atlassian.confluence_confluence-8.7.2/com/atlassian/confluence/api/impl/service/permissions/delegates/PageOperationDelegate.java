/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.History
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.people.Anonymous
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.api.model.permissions.Target
 *  com.atlassian.confluence.api.model.permissions.Target$IdTarget
 *  com.atlassian.confluence.api.model.permissions.TargetType
 *  com.atlassian.confluence.api.model.permissions.spi.OperationCheck
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResults
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.api.impl.service.permissions.delegates;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.api.impl.service.content.factory.PersonFactory;
import com.atlassian.confluence.api.impl.service.permissions.delegates.AbstractOperationDelegate;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.History;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.people.Anonymous;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.permissions.Target;
import com.atlassian.confluence.api.model.permissions.TargetType;
import com.atlassian.confluence.api.model.permissions.spi.OperationCheck;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.SimpleValidationResults;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.ContentPermissionSummary;
import com.atlassian.confluence.internal.ContentPermissionManagerInternal;
import com.atlassian.confluence.internal.pages.PageManagerInternal;
import com.atlassian.confluence.internal.permissions.TargetResolver;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.delegate.PagePermissionsDelegate;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class PageOperationDelegate
extends AbstractOperationDelegate {
    private final PagePermissionsDelegate permissionDelegate;
    private final SpacePermissionManager spacePermissionManager;
    private final ContentPermissionManager contentPermissionManager;
    private final ConfluenceUserResolver confluenceUserResolver;
    private final PersonFactory personFactory;
    private final PageManagerInternal pageManagerInternal;
    private final Logger log = LoggerFactory.getLogger(PageOperationDelegate.class);

    public PageOperationDelegate(PagePermissionsDelegate permissionDelegate, ConfluenceUserResolver confluenceUserResolver, TargetResolver targetResolver, SpacePermissionManager spacePermissionManager, ContentPermissionManager contentPermissionManager, PersonFactory personFactory, PageManagerInternal pageManagerInternal) {
        super(confluenceUserResolver, targetResolver);
        this.confluenceUserResolver = confluenceUserResolver;
        this.permissionDelegate = (PagePermissionsDelegate)Preconditions.checkNotNull((Object)permissionDelegate);
        this.spacePermissionManager = (SpacePermissionManager)Preconditions.checkNotNull((Object)spacePermissionManager);
        this.contentPermissionManager = contentPermissionManager;
        this.personFactory = personFactory;
        this.pageManagerInternal = pageManagerInternal;
    }

    @Override
    protected List<OperationCheck> makeOperations() {
        return ImmutableList.builder().add((Object)new ReadPageOperationCheck()).add((Object)new UpdatePageOperationCheck()).add((Object)new CreatePageOperationCheck()).add((Object)new DeletePageOperationCheck()).build();
    }

    private boolean canViewPageUnderSpace(ConfluenceUser user, Space hibernateContainer) {
        return this.spacePermissionManager.hasPermissionNoExemptions("VIEWSPACE", hibernateContainer, user);
    }

    private boolean canUpdatePageUnderSpace(ConfluenceUser user, Space hibernateContainer) {
        return this.spacePermissionManager.hasPermissionNoExemptions("EDITSPACE", hibernateContainer, user);
    }

    private boolean canDeletePageUnderSpace(ConfluenceUser user, Space hibernateContainer) {
        return this.spacePermissionManager.hasPermissionNoExemptions("REMOVEPAGE", hibernateContainer, user);
    }

    private class DeletePageOperationCheck
    extends PageOperationCheck {
        DeletePageOperationCheck() {
            super(OperationKey.DELETE);
        }

        @Override
        protected final ValidationResult canPerform(ConfluenceUser user, Target target) {
            if (PageOperationDelegate.this.targetResolver.isContainerTarget(target)) {
                Option<Space> hibernateContainer = PageOperationDelegate.this.targetResolver.resolveContainerHibernateObject(target, Space.class);
                if (!hibernateContainer.isDefined()) {
                    PageOperationDelegate.this.log.debug(PageOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Space does not exist.", target, user, PageOperationDelegate.this.log));
                    return SimpleValidationResults.notFoundResult((String)"Space does not exist", (Object[])new Object[0]);
                }
                if (!PageOperationDelegate.this.canViewPageUnderSpace(user, (Space)hibernateContainer.get())) {
                    PageOperationDelegate.this.log.debug(PageOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing view under space permission.", target, user, PageOperationDelegate.this.log));
                    return SimpleValidationResult.FORBIDDEN;
                }
                if (PageOperationDelegate.this.canDeletePageUnderSpace(user, (Space)hibernateContainer.get())) {
                    return SimpleValidationResult.VALID;
                }
                PageOperationDelegate.this.log.debug(PageOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing delete under space permission.", target, user, PageOperationDelegate.this.log));
                return SimpleValidationResult.FORBIDDEN;
            }
            Option<Page> hibernatePage = PageOperationDelegate.this.targetResolver.resolveHibernateObject(target, Page.class);
            if (!hibernatePage.isDefined()) {
                PageOperationDelegate.this.log.debug(PageOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Page does not exist.", target, user, PageOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Page does not exist", (Object[])new Object[0]);
            }
            Page page = (Page)hibernatePage.get();
            if (PageOperationDelegate.this.permissionDelegate.canRemove((User)user, page)) {
                return SimpleValidationResult.VALID;
            }
            PageOperationDelegate.this.log.debug(PageOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing delete permission. (Or view or edit permission)", target, user, PageOperationDelegate.this.log));
            return SimpleValidationResult.FORBIDDEN;
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }

        @Override
        protected Map<Target, ValidationResult> canPerformOnPages(ConfluenceUser user, Space space, List<Content> pageTargets) {
            boolean hasRemovePageSpacePermission = PageOperationDelegate.this.spacePermissionManager.hasPermissionNoExemptions("REMOVEPAGE", space, user);
            boolean hasRemoveOwnSpacePermission = PageOperationDelegate.this.spacePermissionManager.hasPermissionNoExemptions("REMOVEOWNCONTENT", space, user);
            boolean hasSpaceAdminSpacePermission = PageOperationDelegate.this.spacePermissionManager.hasPermissionNoExemptions("SETSPACEPERMISSIONS", space, user);
            Set<Long> targetIdList = pageTargets.stream().map(pageTarget -> pageTarget.getId().asLong()).collect(Collectors.toSet());
            Map<Long, ValidationResult> validationViewResultMap = this.hasContentLevelPermission(user, "View", targetIdList);
            Map<Long, ValidationResult> validationEditResultMap = this.hasContentLevelPermission(user, "Edit", targetIdList);
            Map<Target, ValidationResult> finalValidationResultMap = this.createDefaultValidationMap(pageTargets, SimpleValidationResult.FORBIDDEN);
            for (Map.Entry<Target, ValidationResult> entry : finalValidationResultMap.entrySet()) {
                boolean hasRemoveOwnPermission;
                Target currentTarget = entry.getKey();
                Content currentContent = PageOperationDelegate.this.targetResolver.resolveModelObject(currentTarget, Content.class);
                ValidationResult hasRemovePagePermission = validationViewResultMap.get(currentContent.getId().asLong());
                ValidationResult hasEditPagePermission = validationEditResultMap.get(currentContent.getId().asLong());
                Person creator = currentContent.getHistory().getCreatedBy();
                UserKey creatorUserKey = creator.optionalUserKey().orElse(null);
                if (creator instanceof Anonymous) {
                    hasRemoveOwnPermission = hasRemoveOwnSpacePermission;
                } else {
                    boolean bl = hasRemoveOwnPermission = user.getKey().equals((Object)creatorUserKey) && hasRemoveOwnSpacePermission;
                }
                if (!hasRemovePageSpacePermission && !hasRemoveOwnPermission) {
                    finalValidationResultMap.put(entry.getKey(), SimpleValidationResult.FORBIDDEN);
                    continue;
                }
                if (hasRemovePagePermission.isSuccessful() && !hasSpaceAdminSpacePermission && !hasEditPagePermission.isSuccessful()) {
                    hasRemovePagePermission = SimpleValidationResult.FORBIDDEN;
                }
                finalValidationResultMap.put(entry.getKey(), hasRemovePagePermission);
            }
            return finalValidationResultMap;
        }
    }

    private class CreatePageOperationCheck
    extends PageOperationCheck {
        CreatePageOperationCheck() {
            super(OperationKey.CREATE);
        }

        @Override
        protected ValidationResult canPerform(ConfluenceUser user, Target target) {
            Option<Space> spaceOption = PageOperationDelegate.this.targetResolver.resolveContainerHibernateObject(target, Space.class);
            if (!spaceOption.isDefined()) {
                PageOperationDelegate.this.log.debug(PageOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Space does not exist.", target, user, PageOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Space does not exist", (Object[])new Object[0]);
            }
            Space containerHibernateObject = (Space)spaceOption.get();
            if (PageOperationDelegate.this.permissionDelegate.canCreate(user, containerHibernateObject)) {
                Option<Page> pageOption;
                if (!PageOperationDelegate.this.targetResolver.isContainerTarget(target) && (pageOption = PageOperationDelegate.this.targetResolver.resolveHibernateObject(target, Page.class)).isDefined()) {
                    PageOperationDelegate.this.log.debug(PageOperationDelegate.this.getDebugString(this.getOperationKey(), "Conflict. Page already exists.", target, user, PageOperationDelegate.this.log));
                    return SimpleValidationResults.conflictResult((String)"Page already exists.", (Object[])new Object[0]);
                }
                return SimpleValidationResult.VALID;
            }
            PageOperationDelegate.this.log.debug(PageOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing create in space permission. (or edit or view permission)", target, user, PageOperationDelegate.this.log));
            return SimpleValidationResult.FORBIDDEN;
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }

        @Override
        protected Map<Target, ValidationResult> canPerformOnPages(ConfluenceUser user, Space space, List<Content> pageTargets) {
            boolean hasSpacePermission;
            boolean bl = hasSpacePermission = PageOperationDelegate.this.spacePermissionManager.hasPermissionNoExemptions("VIEWSPACE", space, user) && PageOperationDelegate.this.spacePermissionManager.hasPermissionNoExemptions("EDITSPACE", space, user);
            if (!hasSpacePermission) {
                PageOperationDelegate.this.log.debug("Do not have space permission. Return FORBIDDEN for all list of Target");
                return this.createDefaultValidationMap(pageTargets, SimpleValidationResult.FORBIDDEN);
            }
            return this.createDefaultValidationMap(pageTargets, SimpleValidationResult.VALID);
        }
    }

    private class UpdatePageOperationCheck
    extends PageOperationCheck {
        UpdatePageOperationCheck() {
            super(OperationKey.UPDATE);
        }

        @Override
        protected final ValidationResult canPerform(ConfluenceUser user, Target target) {
            if (PageOperationDelegate.this.targetResolver.isContainerTarget(target)) {
                Option<Space> hibernateContainer = PageOperationDelegate.this.targetResolver.resolveContainerHibernateObject(target, Space.class);
                if (!hibernateContainer.isDefined()) {
                    PageOperationDelegate.this.log.debug(PageOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Space does not exist.", target, user, PageOperationDelegate.this.log));
                    return SimpleValidationResults.notFoundResult((String)"Space does not exist", (Object[])new Object[0]);
                }
                if (!PageOperationDelegate.this.canViewPageUnderSpace(user, (Space)hibernateContainer.get())) {
                    PageOperationDelegate.this.log.debug(PageOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing view under space permission.", target, user, PageOperationDelegate.this.log));
                    return SimpleValidationResult.FORBIDDEN;
                }
                if (PageOperationDelegate.this.canUpdatePageUnderSpace(user, (Space)hibernateContainer.get())) {
                    return SimpleValidationResult.VALID;
                }
                PageOperationDelegate.this.log.debug(PageOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing update under space permission.", target, user, PageOperationDelegate.this.log));
                return SimpleValidationResult.FORBIDDEN;
            }
            Option<Page> hibernatePage = PageOperationDelegate.this.targetResolver.resolveHibernateObject(target, Page.class);
            if (!hibernatePage.isDefined()) {
                PageOperationDelegate.this.log.debug(PageOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Page does not exist.", target, user, PageOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Page does not exist", (Object[])new Object[0]);
            }
            Page page = (Page)hibernatePage.get();
            if (PageOperationDelegate.this.permissionDelegate.canEdit((User)user, page)) {
                return SimpleValidationResult.VALID;
            }
            PageOperationDelegate.this.log.debug(PageOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing edit permission. (Or view permission)", target, user, PageOperationDelegate.this.log));
            return SimpleValidationResult.FORBIDDEN;
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }

        @Override
        protected Map<Target, ValidationResult> canPerformOnPages(ConfluenceUser user, Space space, List<Content> pageTargets) {
            boolean hasSpacePermission;
            boolean bl = hasSpacePermission = PageOperationDelegate.this.spacePermissionManager.hasPermissionNoExemptions("VIEWSPACE", space, user) && PageOperationDelegate.this.spacePermissionManager.hasPermissionNoExemptions("EDITSPACE", space, user);
            if (!hasSpacePermission) {
                PageOperationDelegate.this.log.debug("Do not have space permission. Return FORBIDDEN for all list of Target");
                return this.createDefaultValidationMap(pageTargets, SimpleValidationResult.FORBIDDEN);
            }
            Set<Long> targetIdList = pageTargets.stream().map(pageTarget -> pageTarget.getId().asLong()).collect(Collectors.toSet());
            Map<Long, ValidationResult> validationEditResultMap = this.hasContentLevelPermission(user, "Edit", targetIdList);
            Map<Long, ValidationResult> validationViewResultMap = this.hasContentLevelPermission(user, "View", targetIdList);
            validationViewResultMap.entrySet().stream().forEach(viewValidation -> {
                ValidationResult viewValidationResult = (ValidationResult)viewValidation.getValue();
                if (!viewValidationResult.isSuccessful()) {
                    validationEditResultMap.put((Long)viewValidation.getKey(), viewValidationResult);
                }
            });
            return this.transformValidationResultMap(pageTargets, validationEditResultMap);
        }
    }

    private class ReadPageOperationCheck
    extends PageOperationCheck {
        ReadPageOperationCheck() {
            super(OperationKey.READ);
        }

        @Override
        protected ValidationResult canPerform(ConfluenceUser user, Target target) {
            if (PageOperationDelegate.this.targetResolver.isContainerTarget(target)) {
                Option<Space> hibernateContainer = PageOperationDelegate.this.targetResolver.resolveContainerHibernateObject(target, Space.class);
                if (!hibernateContainer.isDefined()) {
                    PageOperationDelegate.this.log.debug(PageOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Space does not exist.", target, user, PageOperationDelegate.this.log));
                    return SimpleValidationResults.notFoundResult((String)"Space does not exist", (Object[])new Object[0]);
                }
                if (PageOperationDelegate.this.canViewPageUnderSpace(user, (Space)hibernateContainer.get())) {
                    return SimpleValidationResult.VALID;
                }
                PageOperationDelegate.this.log.debug(PageOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing view under space permission.", target, user, PageOperationDelegate.this.log));
                return SimpleValidationResult.FORBIDDEN;
            }
            Option<Page> hibernatePage = PageOperationDelegate.this.targetResolver.resolveHibernateObject(target, Page.class);
            if (!hibernatePage.isDefined()) {
                PageOperationDelegate.this.log.debug(PageOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Page does not exist.", target, user, PageOperationDelegate.this.log));
                return SimpleValidationResults.notFoundResult((String)"Page does not exist", (Object[])new Object[0]);
            }
            if (PageOperationDelegate.this.permissionDelegate.canView((User)user, (Page)hibernatePage.get())) {
                return SimpleValidationResult.VALID;
            }
            PageOperationDelegate.this.log.debug(PageOperationDelegate.this.getDebugString(this.getOperationKey(), "Forbidden. Missing view permission.", target, user, PageOperationDelegate.this.log));
            return SimpleValidationResult.FORBIDDEN;
        }

        @Override
        protected ValidationResult canPerformAccordingToState(ConfluenceUser user, Target target) {
            return SimpleValidationResult.VALID;
        }

        @Override
        protected Map<Target, ValidationResult> canPerformOnPages(ConfluenceUser user, Space space, List<Content> pageTargets) {
            boolean hasSpacePermission = PageOperationDelegate.this.spacePermissionManager.hasPermissionNoExemptions("VIEWSPACE", space, user);
            if (!hasSpacePermission) {
                PageOperationDelegate.this.log.debug("Do not have space permission. Return FORBIDDEN for all list of Target");
                return this.createDefaultValidationMap(pageTargets, SimpleValidationResult.FORBIDDEN);
            }
            Set<Long> targetIdList = pageTargets.stream().map(pageTarget -> pageTarget.getId().asLong()).collect(Collectors.toSet());
            Map<Long, ValidationResult> validationResultMap = this.hasContentLevelPermission(user, "View", targetIdList);
            return this.transformValidationResultMap(pageTargets, validationResultMap);
        }
    }

    private abstract class PageOperationCheck
    extends AbstractOperationDelegate.ConfluenceUserBaseOperationCheck {
        protected PageOperationCheck(OperationKey operationKey) {
            super(PageOperationDelegate.this, operationKey, TargetType.PAGE);
        }

        @Override
        protected final Map<Target, ValidationResult> canPerformImpl(Person person, Iterable<Target> targets) {
            Preconditions.checkNotNull(targets);
            int targetSize = Iterables.size(targets);
            Preconditions.checkArgument((targetSize > 0 ? 1 : 0) != 0);
            if (StreamSupport.stream(targets.spliterator(), false).allMatch(target -> target instanceof Target.IdTarget)) {
                List<Target.IdTarget> idTargets = StreamSupport.stream(targets.spliterator(), false).map(target -> (Target.IdTarget)target).collect(Collectors.toList());
                targets = this.findTargetByIds(idTargets);
            }
            String unknownSpaceKey = "??unknown??";
            ConfluenceUser user = PageOperationDelegate.this.confluenceUserResolver.getExistingUserByPerson(person);
            HashMap validationResultMap = Maps.newHashMap();
            ArrayList<Target> pageTargets = new ArrayList<Target>();
            boolean hasContainerTargets = false;
            for (Target target2 : targets) {
                if (PageOperationDelegate.this.targetResolver.isContainerTarget(target2)) {
                    hasContainerTargets = true;
                    validationResultMap.put(target2, this.canPerform(user, target2));
                    continue;
                }
                pageTargets.add(target2);
            }
            if (hasContainerTargets && !pageTargets.isEmpty()) {
                throw new BadRequestException("Only support single type of Content for checking permission");
            }
            HashMap contentToTargetMap = Maps.newHashMap();
            pageTargets.stream().forEach(target -> contentToTargetMap.put(PageOperationDelegate.this.targetResolver.resolveModelObject((Target)target, Content.class), target));
            if (contentToTargetMap.size() != targetSize) {
                ArrayList foundTargets = Lists.newArrayList(contentToTargetMap.values());
                pageTargets.stream().filter(pageTarget -> !foundTargets.contains(pageTarget)).forEach(notFoundTarget -> validationResultMap.put(notFoundTarget, SimpleValidationResults.notFoundResult((String)"Could not resolve Content object for Target object", (Object[])new Object[0])));
            }
            Map<String, List<Content>> groupingBySpace = contentToTargetMap.keySet().stream().collect(Collectors.groupingBy(content -> {
                if (content.getSpaceRef() != null && content.getSpaceRef().exists()) {
                    return com.atlassian.confluence.api.model.content.Space.getSpaceKey((Reference)content.getSpaceRef());
                }
                com.atlassian.confluence.api.model.content.Space currentSpace = content.getSpace();
                return currentSpace == null ? "??unknown??" : currentSpace.getKey();
            }));
            groupingBySpace.entrySet().stream().forEach(entry -> {
                String spaceKey = (String)entry.getKey();
                List contents = (List)entry.getValue();
                if (spaceKey.equals("??unknown??") && contents.size() > 1) {
                    contents.stream().forEach(content -> validationResultMap.put((Target)contentToTargetMap.get(content), SimpleValidationResult.builder().addError("Space does not exist", new Object[0]).build()));
                } else {
                    validationResultMap.putAll(this.canPerformOnSameSpaceTargets(contentToTargetMap, user, (List)entry.getValue()));
                }
            });
            return validationResultMap;
        }

        protected Iterable<Target> findTargetByIds(Iterable<Target.IdTarget> idTargets) {
            List<Long> ids = StreamSupport.stream(idTargets.spliterator(), false).map(idTarget -> idTarget.getId().asLong()).collect(Collectors.toList());
            List<ContentPermissionSummary> contentPermissionSummaries = PageOperationDelegate.this.pageManagerInternal.findContentPermissionSummaryByIds(ids);
            return contentPermissionSummaries.stream().map(contentPermissionSummary -> {
                Space space = contentPermissionSummary.getSpace();
                ConfluenceUser creator = contentPermissionSummary.getCreator();
                com.atlassian.confluence.api.model.content.Space spaceApiModel = com.atlassian.confluence.api.model.content.Space.builder().id(space.getId()).key(space.getKey()).build();
                Target targetForPermissionCheck = Target.forModelObject((Object)Content.builder((ContentType)ContentType.PAGE).id(ContentId.of((ContentType)ContentType.PAGE, (long)contentPermissionSummary.getId())).history(History.builder().createdBy(PageOperationDelegate.this.personFactory.forUser(creator)).build()).space(spaceApiModel).container((Container)spaceApiModel).build());
                return targetForPermissionCheck;
            }).collect(Collectors.toList());
        }

        protected abstract Map<Target, ValidationResult> canPerformOnPages(ConfluenceUser var1, Space var2, List<Content> var3);

        protected Map<Long, ValidationResult> hasContentLevelPermission(ConfluenceUser user, String permissionType, Set<Long> contentIds) {
            return ((ContentPermissionManagerInternal)PageOperationDelegate.this.contentPermissionManager).hasContentLevelPermission(user, permissionType, contentIds);
        }

        protected Map<Target, ValidationResult> createDefaultValidationMap(List<Content> pageTargets, ValidationResult defaultValue) {
            HashMap resultMap = Maps.newHashMap();
            for (Content content : pageTargets) {
                resultMap.put(Target.forModelObject((Object)content), defaultValue);
            }
            return resultMap;
        }

        protected Map<Target, ValidationResult> transformValidationResultMap(List<Content> pageTargets, Map<Long, ValidationResult> validationResultMap) {
            Map<Target, ValidationResult> finalValidationResultMap = this.createDefaultValidationMap(pageTargets, SimpleValidationResult.FORBIDDEN);
            for (Map.Entry<Target, ValidationResult> entry : finalValidationResultMap.entrySet()) {
                Target currentTarget = entry.getKey();
                Content currentContent = PageOperationDelegate.this.targetResolver.resolveModelObject(currentTarget, Content.class);
                finalValidationResultMap.put(entry.getKey(), validationResultMap.get(currentContent.getId().asLong()));
            }
            return finalValidationResultMap;
        }

        private Map<Target, ValidationResult> canPerformOnSameSpaceTargets(Map<Content, Target> contentToTargetMap, ConfluenceUser user, List<Content> contentTargets) {
            HashMap validationResultMap = Maps.newHashMap();
            Target firstPageTarget = contentToTargetMap.get(contentTargets.get(0));
            if (contentTargets.size() == 1) {
                validationResultMap.put(firstPageTarget, this.canPerform(user, firstPageTarget));
                return validationResultMap;
            }
            Option<Space> hibernateContainer = PageOperationDelegate.this.targetResolver.resolveContainerHibernateObject(firstPageTarget, Space.class);
            if (!hibernateContainer.isDefined()) {
                PageOperationDelegate.this.log.debug(PageOperationDelegate.this.getDebugString(this.getOperationKey(), "Not Found. Space does not exist.", firstPageTarget, user, PageOperationDelegate.this.log));
                validationResultMap.putAll(this.createDefaultValidationMap(contentTargets, SimpleValidationResults.notFoundResult((String)"Space does not exist", (Object[])new Object[0])));
                return validationResultMap;
            }
            Map<Target, ValidationResult> validationResultMapForPages = this.canPerformOnPages(user, (Space)hibernateContainer.get(), contentTargets);
            if (validationResultMapForPages != null) {
                validationResultMap.putAll(validationResultMapForPages);
            }
            return validationResultMap;
        }
    }
}

