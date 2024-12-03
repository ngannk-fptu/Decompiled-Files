/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.people.Group
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResults
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.InternalServerException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.people.GroupService
 *  com.atlassian.confluence.api.service.people.GroupService$GroupFinder
 *  com.atlassian.confluence.api.service.people.GroupService$Validator
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.GroupManager
 *  com.atlassian.user.User
 *  com.atlassian.user.search.page.Pager
 *  com.google.common.base.Strings
 *  io.atlassian.fugue.Iterables
 *  io.atlassian.fugue.Option
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.api.impl.service.people;

import com.atlassian.confluence.admin.criteria.DefaultWritableDirectoryForGroupsExistsCriteria;
import com.atlassian.confluence.api.impl.pagination.PagerToPageResponseHelper;
import com.atlassian.confluence.api.impl.service.content.finder.AbstractFinder;
import com.atlassian.confluence.api.impl.service.content.finder.FinderProxyFactory;
import com.atlassian.confluence.api.impl.service.people.GroupFactory;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.SimpleValidationResults;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.InternalServerException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.people.GroupService;
import com.atlassian.confluence.internal.user.UserAccessorInternal;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.EntityException;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.atlassian.user.search.page.Pager;
import com.google.common.base.Strings;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Option;
import java.util.HashSet;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class GroupServiceImpl
implements GroupService {
    private final ConfluenceAccessManager confluenceAccessManager;
    private final GroupManager groupManager;
    private final UserAccessorInternal userAccessor;
    private final GroupFactory groupFactory;
    private final FinderProxyFactory finderProxyFactory;
    private final PermissionManager permissionManager;
    private final DefaultWritableDirectoryForGroupsExistsCriteria writableDirectoryForGroupsExistsCriteria;
    private final SpacePermissionManager spacePermissionManager;

    public GroupServiceImpl(GroupManager groupManager, ConfluenceAccessManager confluenceAccessManager, UserAccessorInternal userAccessor, GroupFactory groupFactory, FinderProxyFactory finderProxyFactory, PermissionManager permissionManager, DefaultWritableDirectoryForGroupsExistsCriteria writableDirectoryForGroupsExistsCriteria, SpacePermissionManager spacePermissionManager) {
        this.groupManager = groupManager;
        this.confluenceAccessManager = confluenceAccessManager;
        this.userAccessor = userAccessor;
        this.groupFactory = groupFactory;
        this.finderProxyFactory = finderProxyFactory;
        this.permissionManager = permissionManager;
        this.writableDirectoryForGroupsExistsCriteria = writableDirectoryForGroupsExistsCriteria;
        this.spacePermissionManager = spacePermissionManager;
    }

    public GroupService.GroupFinder find(Expansion ... expansions) {
        this.validator().validateView().throwIfNotSuccessful("Unable to search for groups");
        return this.finderProxyFactory.createProxy(new GroupFinderImpl(expansions), GroupService.GroupFinder.class);
    }

    public Group createGroup(String groupName) {
        this.validator().validateCreate(groupName).throwIfNotSuccessful("Unable to create groups");
        return this.groupFactory.buildFrom(this.userAccessor.addGroup(groupName), Expansions.EMPTY);
    }

    public void deleteGroup(String groupName) {
        this.validator().validateDelete(groupName).throwIfNotSuccessful("Unable to delete group: " + groupName);
        com.atlassian.user.Group group = this.userAccessor.getGroup(groupName);
        this.userAccessor.removeGroup(group);
    }

    public GroupService.Validator validator() {
        return new GroupValidator();
    }

    private boolean canModifyGroups() {
        return this.permissionManager.isConfluenceAdministrator(this.getAuthenticatedUser()) && this.writableDirectoryForGroupsExistsCriteria.isMet();
    }

    private ConfluenceUser getAuthenticatedUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    public class GroupValidator
    implements GroupService.Validator {
        public ValidationResult validateView() {
            AccessStatus userAccessStatus = GroupServiceImpl.this.confluenceAccessManager.getUserAccessStatus(GroupServiceImpl.this.getAuthenticatedUser());
            return SimpleValidationResult.builder().authorized(userAccessStatus.hasLicensedAccess()).build();
        }

        public ValidationResult validateCreate(String groupName) {
            if (!GroupServiceImpl.this.canModifyGroups()) {
                return SimpleValidationResult.FORBIDDEN;
            }
            if (StringUtils.isBlank((CharSequence)groupName)) {
                return SimpleValidationResult.builder().authorized(true).addError("Group name cannot be empty.", new Object[0]).build();
            }
            if (GroupServiceImpl.this.userAccessor.getGroup(groupName) != null) {
                return SimpleValidationResults.conflictResult((String)"Group name already exists", (Object[])new Object[0]);
            }
            return SimpleValidationResult.VALID;
        }

        public ValidationResult validateDelete(String groupName) {
            com.atlassian.user.Group group = GroupServiceImpl.this.userAccessor.getGroup(groupName);
            if (group == null) {
                return SimpleValidationResults.notFoundResult((String)("Group not found: " + groupName), (Object[])new Object[0]);
            }
            if (!GroupServiceImpl.this.canModifyGroups()) {
                return SimpleValidationResult.FORBIDDEN;
            }
            if (!GroupServiceImpl.this.permissionManager.hasPermission((User)GroupServiceImpl.this.getAuthenticatedUser(), Permission.REMOVE, group)) {
                return SimpleValidationResult.FORBIDDEN;
            }
            HashSet<String> adminGroups = new HashSet<String>();
            for (SpacePermission spacePermission : GroupServiceImpl.this.spacePermissionManager.getGlobalPermissions()) {
                if (!"SYSTEMADMINISTRATOR".equals(spacePermission.getType()) || !spacePermission.isGroupPermission()) continue;
                adminGroups.add(spacePermission.getGroup());
            }
            if (adminGroups.contains(groupName) && adminGroups.size() == 1) {
                return SimpleValidationResult.builder().authorized(true).addError("Last admin group cannot be deleted.", new Object[0]).build();
            }
            return SimpleValidationResult.VALID;
        }
    }

    public class GroupFinderImpl
    extends AbstractFinder<Group>
    implements GroupService.GroupFinder {
        private String groupName;
        private com.atlassian.confluence.api.model.people.User member;

        public GroupFinderImpl(Expansion[] expansions) {
            super(expansions);
        }

        public PageResponse<Group> fetchMany(PageRequest pageRequest) {
            try {
                return this.fetchGroups(LimitedRequestImpl.create((PageRequest)pageRequest, (int)PaginationLimits.groups()));
            }
            catch (EntityException e) {
                throw new InternalServerException("Error fetching groups", (Throwable)e);
            }
        }

        public GroupService.GroupFinder withName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        public GroupService.GroupFinder withMember(com.atlassian.confluence.api.model.people.User person) {
            this.member = person;
            return this;
        }

        private PageResponse<Group> fetchGroups(LimitedRequest limitedRequest) throws EntityException {
            if (!Strings.isNullOrEmpty((String)this.groupName)) {
                return PageResponseImpl.from(this.fetchGroupByName(this.groupName), (boolean)false).build();
            }
            Pager groupPager = this.member == null ? GroupServiceImpl.this.groupManager.getGroups() : this.fetchGroupsByMembership();
            return PagerToPageResponseHelper.createFromPager(groupPager, limitedRequest, group -> GroupServiceImpl.this.groupFactory.buildFrom((com.atlassian.user.Group)group, this.getExpansions()));
        }

        private Pager<com.atlassian.user.Group> fetchGroupsByMembership() {
            ConfluenceUser user = this.fetchMemberUser();
            return GroupServiceImpl.this.userAccessor.getGroups(user);
        }

        private Option<Group> fetchGroupByName(String groupName) throws EntityException {
            com.atlassian.user.Group group = GroupServiceImpl.this.groupManager.getGroup(groupName);
            if (group != null && (this.member == null || GroupServiceImpl.this.userAccessor.hasMembership(group, this.fetchMemberUser()))) {
                return Option.option((Object)GroupServiceImpl.this.groupFactory.buildFrom(group, this.getExpansions()));
            }
            return Option.none();
        }

        private ConfluenceUser fetchMemberUser() {
            if (!this.member.optionalUserKey().isPresent() && !this.member.optionalUsername().isPresent()) {
                throw new BadRequestException("One of username or userkey must be defined on the member");
            }
            return GroupServiceImpl.this.userAccessor.getExistingByApiUser(this.member).orElseThrow(() -> new NotFoundException("Could not find user to check membership of : " + this.member));
        }

        public Optional<Group> fetch() {
            try {
                if (Strings.isNullOrEmpty((String)this.groupName)) {
                    return Iterables.first(this.fetchGroups(LimitedRequestImpl.create((int)1))).toOptional();
                }
                return this.fetchGroupByName(this.groupName).toOptional();
            }
            catch (EntityException ex) {
                throw new InternalServerException("Could not fetch group", (Throwable)ex);
            }
        }
    }
}

