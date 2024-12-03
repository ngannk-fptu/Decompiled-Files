/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.impl.delegation;

import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.atlassian.user.impl.delegation.repository.DelegatingRepository;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.page.DefaultPager;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerFactory;
import com.atlassian.user.util.Assert;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DelegatingGroupManager
implements GroupManager {
    private static final Logger log = Logger.getLogger(DelegatingGroupManager.class);
    private final List<GroupManager> groupManagers = new ArrayList<GroupManager>();

    public DelegatingGroupManager(List<GroupManager> groupManagers) {
        this.groupManagers.addAll(groupManagers);
    }

    public List<GroupManager> getGroupManagers() {
        return Collections.unmodifiableList(this.groupManagers);
    }

    private GroupManager getMatchingGroupManager(String groupName) throws EntityException {
        GroupManager lastFoundGroupManager = null;
        Iterator<GroupManager> i$ = this.groupManagers.iterator();
        while (i$.hasNext()) {
            GroupManager groupManager;
            lastFoundGroupManager = groupManager = i$.next();
            if (groupManager.getGroup(groupName) == null) continue;
            return groupManager;
        }
        return lastFoundGroupManager;
    }

    @Override
    public Pager<Group> getGroups() throws EntityException {
        ArrayList groups = new ArrayList();
        for (GroupManager groupManager : this.groupManagers) {
            try {
                groups.add(groupManager.getGroups());
            }
            catch (EntityException e) {
                log.error((Object)("Failed to retrieve groups from group manager in delegation: " + groupManager.getClass().toString() + ". Continuing with remaining managers."), (Throwable)e);
            }
        }
        return PagerFactory.getPager(groups);
    }

    @Override
    public Pager<Group> getGroups(User user) throws EntityException {
        Assert.notNull(user, "User must not be null");
        LinkedList pagers = new LinkedList();
        for (GroupManager groupManager : this.groupManagers) {
            try {
                Pager<Group> groupPager = groupManager.getGroups(user);
                pagers.add(groupPager);
            }
            catch (EntityException e) {
                log.error((Object)("Failed to retrieve groups for user [" + user + "] from group manager: " + groupManager.getClass().toString() + ". Continuing with remaining managers."), (Throwable)e);
            }
        }
        return PagerFactory.getPager(pagers);
    }

    @Override
    public List<Group> getWritableGroups() {
        ArrayList<Group> groups = new ArrayList<Group>();
        for (GroupManager groupManager : this.groupManagers) {
            groups.addAll(groupManager.getWritableGroups());
        }
        return groups;
    }

    @Override
    public Pager<String> getMemberNames(Group group) throws EntityException {
        GroupManager groupManager = this.getMatchingGroupManager(group.getName());
        if (groupManager == null) {
            return DefaultPager.emptyPager();
        }
        return groupManager.getMemberNames(group);
    }

    @Override
    public Pager<String> getLocalMemberNames(Group group) throws EntityException {
        GroupManager groupManager = this.getMatchingGroupManager(group.getName());
        if (groupManager == null) {
            return DefaultPager.emptyPager();
        }
        return groupManager.getLocalMemberNames(group);
    }

    @Override
    public Pager<String> getExternalMemberNames(Group group) throws EntityException {
        GroupManager groupManager = this.getMatchingGroupManager(group.getName());
        if (groupManager == null) {
            return DefaultPager.emptyPager();
        }
        return groupManager.getExternalMemberNames(group);
    }

    @Override
    public Group getGroup(String groupName) throws EntityException {
        for (GroupManager groupManager : this.groupManagers) {
            Group foundGroup = groupManager.getGroup(groupName);
            if (foundGroup == null) continue;
            return foundGroup;
        }
        return null;
    }

    @Override
    public Group createGroup(String groupName) throws EntityException {
        Iterator<GroupManager> iter = this.groupManagers.iterator();
        Group createdGroup = null;
        while (iter.hasNext()) {
            GroupManager groupManager = iter.next();
            if (groupManager.isCreative()) {
                createdGroup = groupManager.createGroup(groupName);
            }
            if (createdGroup == null) continue;
            return createdGroup;
        }
        return null;
    }

    @Override
    public void removeGroup(Group group) throws EntityException {
        for (GroupManager groupManager : this.groupManagers) {
            if (groupManager.getGroup(group.getName()) == null || groupManager.isReadOnly(group)) continue;
            groupManager.removeGroup(group);
            break;
        }
    }

    @Override
    public void addMembership(Group group, User user) throws EntityException {
        if (group == null) {
            throw new IllegalArgumentException("Can't add membership for null group");
        }
        GroupManager groupManager = this.getMatchingGroupManager(group.getName());
        groupManager.addMembership(group, user);
    }

    @Override
    public boolean hasMembership(Group group, User user) throws EntityException {
        if (group == null) {
            return false;
        }
        GroupManager groupManager = this.getMatchingGroupManager(group.getName());
        return groupManager.hasMembership(group, user);
    }

    @Override
    public void removeMembership(Group group, User user) throws EntityException {
        if (group == null) {
            throw new IllegalArgumentException("Can't remove membership for null group");
        }
        GroupManager groupManager = this.getMatchingGroupManager(group.getName());
        groupManager.removeMembership(group, user);
    }

    @Override
    public boolean supportsExternalMembership() throws EntityException {
        for (GroupManager groupManager : this.groupManagers) {
            if (!groupManager.supportsExternalMembership()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isReadOnly(Group group) throws EntityException {
        for (GroupManager groupManager : this.groupManagers) {
            if (groupManager.getGroup(group.getName()) == null) continue;
            return groupManager.isReadOnly(group);
        }
        return false;
    }

    @Override
    public RepositoryIdentifier getIdentifier() {
        ArrayList<RepositoryIdentifier> repositories = new ArrayList<RepositoryIdentifier>();
        for (GroupManager groupManager : this.groupManagers) {
            repositories.add(groupManager.getIdentifier());
        }
        return new DelegatingRepository(repositories);
    }

    @Override
    public RepositoryIdentifier getRepository(Entity entity) throws EntityException {
        if (!(entity instanceof Group)) {
            return null;
        }
        GroupManager groupManager = this.getMatchingGroupManager(entity.getName());
        return groupManager.getIdentifier();
    }

    @Override
    public boolean isCreative() {
        for (GroupManager groupManager : this.groupManagers) {
            if (!groupManager.isCreative()) continue;
            return true;
        }
        return false;
    }
}

