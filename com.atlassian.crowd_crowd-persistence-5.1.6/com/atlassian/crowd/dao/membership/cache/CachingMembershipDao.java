/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.impl.IdentifierMap
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.embedded.spi.MembershipDao
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.MembershipAlreadyExistsException
 *  com.atlassian.crowd.exception.MembershipNotFoundException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.NameComparator
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestriction
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.search.util.SearchResultsUtil
 *  com.atlassian.crowd.util.BatchResult
 *  com.atlassian.crowd.util.BoundedCount
 *  com.google.common.base.Function
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableListMultimap
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.ListMultimap
 */
package com.atlassian.crowd.dao.membership.cache;

import com.atlassian.crowd.dao.membership.InternalMembershipDao;
import com.atlassian.crowd.dao.membership.cache.MembershipCache;
import com.atlassian.crowd.dao.membership.cache.QueryType;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.impl.IdentifierMap;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.embedded.spi.MembershipDao;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.MembershipAlreadyExistsException;
import com.atlassian.crowd.exception.MembershipNotFoundException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.NameComparator;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.search.util.SearchResultsUtil;
import com.atlassian.crowd.util.BatchResult;
import com.atlassian.crowd.util.BoundedCount;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CachingMembershipDao
implements MembershipDao {
    private final InternalMembershipDao delegate;
    protected final MembershipCache membershipCache;

    public CachingMembershipDao(InternalMembershipDao delegate, MembershipCache membershipCache) {
        this.delegate = delegate;
        this.membershipCache = membershipCache;
    }

    public boolean isUserDirectMember(long directoryId, String userName, String groupName) {
        List<String> groupUsers = this.membershipCache.getNames(directoryId, QueryType.GROUP_USERS, groupName);
        if (groupUsers != null) {
            return this.containsLowerCase(groupUsers, userName);
        }
        List<String> userGroups = this.membershipCache.getNames(directoryId, QueryType.USER_GROUPS, userName);
        if (userGroups != null) {
            return this.containsLowerCase(userGroups, groupName);
        }
        return this.delegate.isUserDirectMember(directoryId, userName, groupName);
    }

    public boolean isGroupDirectMember(long directoryId, String childGroup, String parentGroup) {
        List<String> children = this.membershipCache.getNames(directoryId, QueryType.GROUP_SUBGROUPS, parentGroup);
        if (children != null) {
            return this.containsLowerCase(children, childGroup);
        }
        List<String> parents = this.membershipCache.getNames(directoryId, QueryType.GROUP_PARENTS, childGroup);
        if (parents != null) {
            return this.containsLowerCase(parents, parentGroup);
        }
        return this.delegate.isGroupDirectMember(directoryId, childGroup, parentGroup);
    }

    public void addUserToGroup(long directoryId, String userName, String groupName) throws UserNotFoundException, GroupNotFoundException, MembershipAlreadyExistsException {
        this.membershipCache.invalidateCache(directoryId, QueryType.USER_GROUPS, userName);
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_USERS, groupName);
        this.delegate.addUserToGroup(directoryId, userName, groupName);
    }

    public BatchResult<String> addAllUsersToGroup(long directoryId, Collection<String> userNames, String groupName) throws GroupNotFoundException {
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_USERS, groupName);
        userNames.forEach(userName -> this.membershipCache.invalidateCache(directoryId, QueryType.USER_GROUPS, (String)userName));
        return this.delegate.addAllUsersToGroup(directoryId, userNames, groupName);
    }

    public void addGroupToGroup(long directoryId, String childGroup, String parentGroup) throws GroupNotFoundException, MembershipAlreadyExistsException {
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_PARENTS, childGroup);
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_SUBGROUPS, parentGroup);
        this.delegate.addGroupToGroup(directoryId, childGroup, parentGroup);
    }

    public BatchResult<String> addAllGroupsToGroup(long directoryId, Collection<String> childGroupNames, String groupName) throws GroupNotFoundException {
        childGroupNames.forEach(childGroupName -> this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_PARENTS, (String)childGroupName));
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_SUBGROUPS, groupName);
        return this.delegate.addAllGroupsToGroup(directoryId, childGroupNames, groupName);
    }

    public void removeUserFromGroup(long directoryId, String userName, String groupName) throws UserNotFoundException, GroupNotFoundException, MembershipNotFoundException {
        this.membershipCache.invalidateCache(directoryId, QueryType.USER_GROUPS, userName);
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_USERS, groupName);
        this.delegate.removeUserFromGroup(directoryId, userName, groupName);
    }

    public BatchResult<String> removeUsersFromGroup(long directoryId, Collection<String> usernames, String groupName) throws GroupNotFoundException {
        usernames.forEach(username -> this.membershipCache.invalidateCache(directoryId, QueryType.USER_GROUPS, (String)username));
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_USERS, groupName);
        return this.delegate.removeUsersFromGroup(directoryId, usernames, groupName);
    }

    public void removeGroupFromGroup(long directoryId, String childGroup, String parentGroup) throws GroupNotFoundException, MembershipNotFoundException {
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_PARENTS, childGroup);
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_SUBGROUPS, parentGroup);
        this.delegate.removeGroupFromGroup(directoryId, childGroup, parentGroup);
    }

    public BatchResult<String> removeGroupsFromGroup(long directoryId, Collection<String> childGroupNames, String groupName) throws GroupNotFoundException {
        childGroupNames.forEach(childGroupName -> this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_PARENTS, (String)childGroupName));
        this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_SUBGROUPS, groupName);
        return this.delegate.removeGroupsFromGroup(directoryId, childGroupNames, groupName);
    }

    public <T> List<T> search(long directoryId, MembershipQuery<T> query) {
        if (this.shouldCache(query)) {
            ListMultimap<String, T> results = this.searchGroupedByNameCached(directoryId, query.withAllResults());
            if (results.isEmpty()) {
                return ImmutableList.of();
            }
            if (results.keySet().size() == 1) {
                List allResults = results.get(Iterables.getOnlyElement((Iterable)results.keySet()));
                return SearchResultsUtil.constrainResults((List)allResults, (int)query.getStartIndex(), (int)query.getMaxResults());
            }
            return this.indexByName(results.values()).entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).skip(query.getStartIndex()).limit(EntityQuery.allResultsToLongMax((int)query.getMaxResults())).collect(Collectors.toList());
        }
        return this.delegate.search(directoryId, query);
    }

    private <T> Map<String, T> indexByName(Collection<T> values) {
        if (values.isEmpty()) {
            return ImmutableMap.of();
        }
        Function normalizer = NameComparator.normaliserOf(values.iterator().next().getClass());
        HashMap results = new HashMap();
        values.forEach(arg_0 -> CachingMembershipDao.lambda$indexByName$4(results, (java.util.function.Function)normalizer, arg_0));
        return results;
    }

    public <T> ListMultimap<String, T> searchGroupedByName(long directoryId, MembershipQuery<T> query) {
        if (this.shouldCache(query)) {
            return this.searchGroupedByNameCached(directoryId, query);
        }
        return this.delegate.searchGroupedByName(directoryId, query);
    }

    private <T> ListMultimap<String, T> searchGroupedByNameCached(long directoryId, MembershipQuery<T> query) {
        QueryType queryType = this.getQueryType(query);
        ArrayListMultimap resultsByName = ArrayListMultimap.create();
        HashSet<String> missing = new HashSet<String>();
        for (String name : query.getEntityNamesToMatch()) {
            List results = this.membershipCache.get(directoryId, queryType, name, query.getReturnType());
            if (results == null) {
                missing.add(name);
                continue;
            }
            resultsByName.putAll((Object)name, results);
        }
        if (!missing.isEmpty()) {
            resultsByName.putAll(this.executeAndCache(directoryId, query.withEntityNames(missing).withAllResults()));
        }
        return resultsByName;
    }

    private <T> ListMultimap<String, T> executeAndCache(long directoryId, MembershipQuery<T> query) {
        QueryType queryType = this.getQueryType(query);
        ListMultimap<String, T> results = this.doSearchGroupedByName(directoryId, query);
        IdentifierMap identityMap = new IdentifierMap(results.asMap());
        for (String key : query.getEntityNamesToMatch()) {
            ImmutableList values = ImmutableList.copyOf((Collection)((Collection)identityMap.getOrDefault((Object)key, (Object)ImmutableList.of())));
            this.membershipCache.put(directoryId, queryType, key, values);
        }
        return results;
    }

    private <T> ListMultimap<String, T> doSearchGroupedByName(long directoryId, MembershipQuery<T> query) {
        if (query.getEntityNamesToMatch().size() == 1) {
            return ImmutableListMultimap.builder().putAll(Iterables.getOnlyElement((Iterable)query.getEntityNamesToMatch()), (Iterable)this.delegate.search(directoryId, query)).build();
        }
        return this.delegate.searchGroupedByName(directoryId, query);
    }

    public BoundedCount countDirectMembersOfGroup(long directoryId, String groupName, int potentialMaxCount) {
        List<String> cached = this.membershipCache.getNames(directoryId, QueryType.GROUP_USERS, groupName);
        return cached != null ? BoundedCount.exactly((long)cached.size()) : this.delegate.countDirectMembersOfGroup(directoryId, groupName, potentialMaxCount);
    }

    public BatchResult<String> addUserToGroups(long directoryId, String username, Set<String> groupNames) throws UserNotFoundException {
        this.membershipCache.invalidateCache(directoryId, QueryType.USER_GROUPS, username);
        groupNames.forEach(name -> this.membershipCache.invalidateCache(directoryId, QueryType.GROUP_USERS, (String)name));
        return this.delegate.addUserToGroups(directoryId, username, groupNames);
    }

    protected <T> boolean shouldCache(MembershipQuery<T> query) {
        SearchRestriction restriction = query.getSearchRestriction();
        if (restriction != null && !(restriction instanceof NullRestriction)) {
            return false;
        }
        if (!this.membershipCache.supports(query.getReturnType()) && !query.isWithAllResults()) {
            return false;
        }
        if (query.getEntityToMatch().getGroupType() != null && query.getEntityToMatch().getGroupType() != GroupType.GROUP) {
            return false;
        }
        if (query.getEntityToReturn().getGroupType() != null && query.getEntityToReturn().getGroupType() != GroupType.GROUP) {
            return false;
        }
        return this.membershipCache.getCacheableTypes().contains((Object)this.getQueryType(query));
    }

    private <T> QueryType getQueryType(MembershipQuery<T> query) {
        if (query.isFindChildren()) {
            return query.getEntityToReturn().getEntityType() == Entity.USER ? QueryType.GROUP_USERS : QueryType.GROUP_SUBGROUPS;
        }
        return query.getEntityToMatch().getEntityType() == Entity.USER ? QueryType.USER_GROUPS : QueryType.GROUP_PARENTS;
    }

    private boolean containsLowerCase(Collection<String> values, String toMatch) {
        String toMatchLowercase = IdentifierUtils.toLowerCase((String)toMatch);
        return values.stream().anyMatch(v -> IdentifierUtils.toLowerCase((String)v).equals(toMatchLowercase));
    }

    public void clearCache() {
        this.membershipCache.clear();
    }

    private static /* synthetic */ void lambda$indexByName$4(Map results, java.util.function.Function normalizer, Object e) {
        results.putIfAbsent(normalizer.apply(e), e);
    }
}

