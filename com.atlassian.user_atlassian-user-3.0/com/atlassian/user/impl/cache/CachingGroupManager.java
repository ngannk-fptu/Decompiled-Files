/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  org.apache.log4j.Category
 */
package com.atlassian.user.impl.cache;

import com.atlassian.cache.CacheFactory;
import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.atlassian.user.impl.cache.EntityRepositoryCache;
import com.atlassian.user.impl.cache.GroupCache;
import com.atlassian.user.impl.cache.GroupsForUserCache;
import com.atlassian.user.impl.cache.MembershipCache;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.page.DefaultPager;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerUtils;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Category;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CachingGroupManager
implements GroupManager {
    private static final Category log = Category.getInstance(CachingGroupManager.class);
    protected final GroupManager underlyingGroupManager;
    protected final CacheFactory cacheFactory;
    protected GroupCache groupCache = null;
    protected MembershipCache membershipCache = null;
    protected GroupsForUserCache groupsForUserCache = null;
    protected EntityRepositoryCache entityRepositoryCache = null;

    public CachingGroupManager(GroupManager underlyingGroupManager, CacheFactory cacheFactory) {
        this.underlyingGroupManager = underlyingGroupManager;
        this.cacheFactory = cacheFactory;
        this.initialiseCaches();
    }

    @Override
    public Pager<Group> getGroups(User user) throws EntityException {
        List<String> groupNames;
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (log.isInfoEnabled()) {
            log.info((Object)("Retrieving groups for user [" + user.getName() + "]"));
        }
        if ((groupNames = this.groupsForUserCache.get(user)) != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Cache hit. Returning pager with " + groupNames.size() + " items."));
            }
            LinkedList<Group> groups = new LinkedList<Group>();
            for (String groupName1 : groupNames) {
                groups.add(this.getGroup(groupName1));
            }
            return new DefaultPager<Group>(groups);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Cache miss. Retrieving groups from underlying group manager.");
        }
        LinkedList<Group> groups = new LinkedList<Group>();
        groupNames = new LinkedList<String>();
        for (Group group : this.underlyingGroupManager.getGroups(user)) {
            groups.add(group);
            this.groupCache.put(group.getName(), group);
            groupNames.add(group.getName());
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Retrieved " + groupNames.size() + " groups for user [" + user + "], putting in cache."));
        }
        this.groupsForUserCache.put(user, groupNames);
        return new DefaultPager<Group>(groups);
    }

    @Override
    public List<Group> getWritableGroups() {
        return this.underlyingGroupManager.getWritableGroups();
    }

    @Override
    public Group getGroup(String groupName) throws EntityException {
        Group cachedGroup = this.groupCache.get(groupName);
        if (cachedGroup != null) {
            return GroupCache.NULL_GROUP.equals(cachedGroup) ? null : cachedGroup;
        }
        Group group = this.underlyingGroupManager.getGroup(groupName);
        this.groupCache.put(groupName, group);
        return group;
    }

    @Override
    public Group createGroup(String groupName) throws EntityException {
        Group createdGroup = this.underlyingGroupManager.createGroup(groupName);
        if (createdGroup != null) {
            this.groupCache.put(createdGroup.getName(), createdGroup);
        }
        return createdGroup;
    }

    @Override
    public void removeGroup(Group group) throws EntityException {
        List<String> memberNames = PagerUtils.toList(this.getMemberNames(group));
        this.underlyingGroupManager.removeGroup(group);
        this.groupCache.remove(group.getName());
        this.groupsForUserCache.remove(memberNames);
        this.membershipCache.remove(memberNames, group);
        this.entityRepositoryCache.remove(group);
    }

    @Override
    public void addMembership(Group group, User user) throws EntityException {
        this.underlyingGroupManager.addMembership(group, user);
        this.membershipCache.put(user, group, true);
        this.groupsForUserCache.remove(user);
    }

    @Override
    public boolean hasMembership(Group group, User user) throws EntityException {
        if (group == null) {
            return false;
        }
        Boolean membershipCheckElement = this.membershipCache.get(user, group);
        if (membershipCheckElement != null) {
            return membershipCheckElement;
        }
        boolean isMember = this.underlyingGroupManager.hasMembership(group, user);
        this.membershipCache.put(user, group, isMember);
        return isMember;
    }

    @Override
    public void removeMembership(Group group, User user) throws EntityException {
        this.underlyingGroupManager.removeMembership(group, user);
        this.membershipCache.remove(user, group);
        this.groupsForUserCache.remove(user);
    }

    @Override
    public RepositoryIdentifier getRepository(Entity entity) throws EntityException {
        RepositoryIdentifier cachedRepository = this.entityRepositoryCache.get(entity);
        if (cachedRepository != null) {
            return cachedRepository;
        }
        RepositoryIdentifier repository = this.underlyingGroupManager.getRepository(entity);
        this.entityRepositoryCache.put(entity, repository);
        return repository;
    }

    @Override
    public Pager<Group> getGroups() throws EntityException {
        return this.underlyingGroupManager.getGroups();
    }

    @Override
    public Pager<String> getMemberNames(Group group) throws EntityException {
        return this.underlyingGroupManager.getMemberNames(group);
    }

    @Override
    public Pager<String> getLocalMemberNames(Group group) throws EntityException {
        return this.underlyingGroupManager.getLocalMemberNames(group);
    }

    @Override
    public Pager<String> getExternalMemberNames(Group group) throws EntityException {
        return this.underlyingGroupManager.getExternalMemberNames(group);
    }

    @Override
    public boolean supportsExternalMembership() throws EntityException {
        return this.underlyingGroupManager.supportsExternalMembership();
    }

    @Override
    public boolean isReadOnly(Group group) throws EntityException {
        return this.underlyingGroupManager.isReadOnly(group);
    }

    @Override
    public RepositoryIdentifier getIdentifier() {
        return this.underlyingGroupManager.getIdentifier();
    }

    @Override
    public boolean isCreative() {
        return this.underlyingGroupManager.isCreative();
    }

    private void initialiseCaches() {
        this.entityRepositoryCache = new EntityRepositoryCache(this.cacheFactory, this.getCacheKey("repositories"));
        this.groupCache = new GroupCache(this.cacheFactory, this.getCacheKey("groups"));
        this.membershipCache = new MembershipCache(this.cacheFactory, this.getCacheKey("groups_hasMembership"));
        this.groupsForUserCache = new GroupsForUserCache(this.cacheFactory, this.getCacheKey("groups_getGroupsForUser"));
    }

    private String getCacheKey(String cacheName) {
        String className = this.underlyingGroupManager.getClass().getName();
        String repositoryKey = this.underlyingGroupManager.getIdentifier().getKey();
        return className + "." + repositoryKey + "." + cacheName;
    }
}

