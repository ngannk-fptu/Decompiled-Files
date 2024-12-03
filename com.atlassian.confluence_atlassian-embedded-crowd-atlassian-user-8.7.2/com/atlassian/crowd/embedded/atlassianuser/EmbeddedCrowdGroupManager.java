/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Group
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.exception.OperationNotPermittedException
 *  com.atlassian.crowd.exception.embedded.InvalidGroupException
 *  com.atlassian.crowd.exception.runtime.CrowdRuntimeException
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.user.Entity
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.GroupManager
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.EntityValidationException
 *  com.atlassian.user.repository.RepositoryIdentifier
 *  com.atlassian.user.search.page.DefaultPager
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.user.search.page.Pagers
 *  com.atlassian.user.util.Assert
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 */
package com.atlassian.crowd.embedded.atlassianuser;

import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.atlassianuser.Conversions;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.crowd.exception.embedded.InvalidGroupException;
import com.atlassian.crowd.exception.runtime.CrowdRuntimeException;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.GroupManager;
import com.atlassian.user.impl.EntityValidationException;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.page.DefaultPager;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.Pagers;
import com.atlassian.user.util.Assert;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;

@Deprecated
public class EmbeddedCrowdGroupManager
implements GroupManager {
    private final RepositoryIdentifier repositoryIdentifier;
    private final CrowdService crowdService;

    public EmbeddedCrowdGroupManager(RepositoryIdentifier repositoryIdentifier, CrowdService crowdService) {
        this.repositoryIdentifier = (RepositoryIdentifier)Preconditions.checkNotNull((Object)repositoryIdentifier);
        this.crowdService = (CrowdService)Preconditions.checkNotNull((Object)crowdService);
    }

    public RepositoryIdentifier getIdentifier() {
        return this.repositoryIdentifier;
    }

    public RepositoryIdentifier getRepository(Entity entity) throws EntityException {
        if (this.getGroup(entity.getName()) != null) {
            return this.repositoryIdentifier;
        }
        return null;
    }

    public boolean isCreative() {
        return true;
    }

    public Pager<com.atlassian.user.Group> getGroups() throws EntityException {
        Iterable allGroups = this.crowdService.search(Queries.ALL_GROUPS);
        return Pagers.newDefaultPager((Iterable)Iterables.transform((Iterable)allGroups, Conversions.TO_ATLASSIAN_GROUP));
    }

    public List<com.atlassian.user.Group> getWritableGroups() {
        Iterable allGroups = this.crowdService.search(Queries.ALL_GROUPS);
        return Lists.newArrayList((Iterable)Iterables.transform((Iterable)allGroups, Conversions.TO_ATLASSIAN_GROUP));
    }

    public Pager<com.atlassian.user.Group> getGroups(com.atlassian.user.User user) throws EntityException {
        Iterable groupsForUser = this.crowdService.search(Queries.groupsForUser(user));
        return Pagers.newDefaultPager((Iterable)Iterables.transform((Iterable)groupsForUser, Conversions.TO_ATLASSIAN_GROUP));
    }

    public Pager<String> getMemberNames(com.atlassian.user.Group group) throws EntityException {
        Iterable members = this.crowdService.search(Queries.groupMemberNames(group));
        return Pagers.newDefaultPager((Iterable)members);
    }

    public Pager<String> getLocalMemberNames(com.atlassian.user.Group group) throws EntityException {
        return this.getMemberNames(group);
    }

    public Pager<String> getExternalMemberNames(com.atlassian.user.Group group) throws EntityException {
        return DefaultPager.emptyPager();
    }

    public com.atlassian.user.Group getGroup(String groupName) throws EntityException {
        if (groupName == null) {
            throw new IllegalArgumentException("Input (groupname) is null.");
        }
        return (com.atlassian.user.Group)Conversions.TO_ATLASSIAN_GROUP.apply((Object)this.crowdService.getGroup(groupName));
    }

    public com.atlassian.user.Group createGroup(final String groupName) throws EntityException {
        Group group;
        Group template = new Group(){

            public String getName() {
                return groupName;
            }

            public int compareTo(Group group) {
                return this.getName().compareToIgnoreCase(group.getName());
            }

            public int hashCode() {
                return Objects.hash(this.getName());
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || this.getClass() != o.getClass()) {
                    return false;
                }
                return Objects.equals(this.getName(), ((com.atlassian.user.Group)o).getName());
            }
        };
        try {
            group = this.crowdService.addGroup(template);
        }
        catch (InvalidGroupException e) {
            throw new EntityValidationException((Throwable)e);
        }
        catch (OperationNotPermittedException e) {
            throw new EntityException((Throwable)e);
        }
        return (com.atlassian.user.Group)Conversions.TO_ATLASSIAN_GROUP.apply((Object)group);
    }

    public void removeGroup(com.atlassian.user.Group group) throws EntityException, IllegalArgumentException {
        try {
            this.crowdService.removeGroup(this.getCrowdGroup(group));
        }
        catch (OperationNotPermittedException e) {
            throw new EntityException((Throwable)e);
        }
    }

    public void addMembership(com.atlassian.user.Group group, com.atlassian.user.User user) throws EntityException, IllegalArgumentException {
        Group crowdGroup = this.getCrowdGroup(group);
        if (crowdGroup == null) {
            throw new EntityException("This repository [" + this.getIdentifier().getName() + "] does not handle memberships of the group [" + group + "]");
        }
        User crowdUser = this.getCrowdUser(user);
        if (crowdUser == null) {
            throw new EntityException("This repository [" + this.getIdentifier().getName() + "] does not handle memberships of the user [" + user + "]");
        }
        try {
            this.crowdService.addUserToGroup(crowdUser, crowdGroup);
        }
        catch (OperationNotPermittedException e) {
            throw new EntityException((Throwable)e);
        }
    }

    public boolean hasMembership(com.atlassian.user.Group group, com.atlassian.user.User user) throws EntityException {
        Group crowdGroup = this.getCrowdGroup(group);
        if (crowdGroup == null) {
            return false;
        }
        User crowdUser = this.getCrowdUser(user);
        if (crowdUser == null) {
            return false;
        }
        return this.crowdService.isUserMemberOfGroup(crowdUser, crowdGroup);
    }

    public void removeMembership(com.atlassian.user.Group group, com.atlassian.user.User user) throws EntityException, IllegalArgumentException {
        Group crowdGroup = this.getCrowdGroup(group);
        if (crowdGroup == null) {
            throw new EntityException("This repository [" + this.getIdentifier().getName() + "] does not handle memberships of the group [" + group + "]");
        }
        User crowdUser = this.getCrowdUser(user);
        if (crowdUser == null) {
            throw new EntityException("This repository [" + this.getIdentifier().getName() + "] does not handle memberships of the user [" + user + "]");
        }
        try {
            this.crowdService.removeUserFromGroup(crowdUser, crowdGroup);
        }
        catch (OperationNotPermittedException e) {
            throw new EntityException((Throwable)e);
        }
        catch (CrowdRuntimeException e) {
            throw new EntityException((Throwable)e);
        }
    }

    private User getCrowdUser(com.atlassian.user.User user) {
        Assert.notNull((Object)user, (String)"User should not be null");
        if (user instanceof com.atlassian.crowd.model.user.User) {
            return (com.atlassian.crowd.model.user.User)user;
        }
        return this.crowdService.getUser(user.getName());
    }

    private Group getCrowdGroup(com.atlassian.user.Group group) {
        Assert.notNull((Object)group, (String)"Group should not be null");
        if (group instanceof Group) {
            return (Group)group;
        }
        return this.crowdService.getGroup(group.getName());
    }

    public boolean supportsExternalMembership() throws EntityException {
        return false;
    }

    public boolean isReadOnly(com.atlassian.user.Group group) throws EntityException {
        return false;
    }

    private static class Queries {
        private static final EntityQuery<Group> ALL_GROUPS = QueryBuilder.queryFor(Group.class, (EntityDescriptor)EntityDescriptor.group()).returningAtMost(-1);

        private Queries() {
        }

        private static MembershipQuery<Group> groupsForUser(com.atlassian.user.User user) {
            return QueryBuilder.queryFor(Group.class, (EntityDescriptor)EntityDescriptor.group()).parentsOf(EntityDescriptor.user()).withName(user.getName()).returningAtMost(-1);
        }

        private static MembershipQuery<String> groupMemberNames(com.atlassian.user.Group group) {
            return QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user()).childrenOf(EntityDescriptor.group()).withName(group.getName()).returningAtMost(-1);
        }
    }
}

