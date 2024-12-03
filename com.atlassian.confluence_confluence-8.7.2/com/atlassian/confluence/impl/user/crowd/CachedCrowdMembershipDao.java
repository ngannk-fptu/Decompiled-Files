/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.spi.MembershipDao
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.MembershipAlreadyExistsException
 *  com.atlassian.crowd.exception.MembershipNotFoundException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.QueryBuilder$PartialEntityQuery
 *  com.atlassian.crowd.search.builder.QueryBuilder$PartialMembershipQueryWithEntityToMatch
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestriction
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.util.BatchResult
 *  com.atlassian.crowd.util.BoundedCount
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ListMultimap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.confluence.impl.user.crowd.GroupMembershipCache;
import com.atlassian.confluence.impl.user.crowd.MembershipCache;
import com.atlassian.crowd.embedded.spi.MembershipDao;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.MembershipAlreadyExistsException;
import com.atlassian.crowd.exception.MembershipNotFoundException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.util.BatchResult;
import com.atlassian.crowd.util.BoundedCount;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CachedCrowdMembershipDao
implements MembershipDao {
    private static final Logger log = LoggerFactory.getLogger(CachedCrowdMembershipDao.class);
    private final MembershipDao delegate;
    private final MembershipCache membershipCache;
    private final GroupMembershipCache parentGroupCache;
    private final GroupMembershipCache childGroupCache;

    public CachedCrowdMembershipDao(MembershipDao delegate, MembershipCache membershipCache, GroupMembershipCache parentGroupCache, GroupMembershipCache childGroupCache) {
        this.delegate = delegate;
        this.membershipCache = membershipCache;
        this.parentGroupCache = parentGroupCache;
        this.childGroupCache = childGroupCache;
    }

    public boolean isUserDirectMember(long directoryId, String userName, String groupName) {
        log.debug("checking direct membership for user [ {} ] and group [ {} ]", (Object)userName, (Object)groupName);
        return this.membershipCache.isUserDirectMember(directoryId, userName, groupName, () -> this.findParentGroupNames(directoryId, userName, EntityDescriptor.user()));
    }

    public boolean isGroupDirectMember(long directoryId, String childGroup, String parentGroupName) {
        log.debug("checking direct membership for child group [ {} ] and parent group [ {} ]", (Object)childGroup, (Object)parentGroupName);
        return this.membershipCache.isGroupDirectMember(directoryId, childGroup, parentGroupName, () -> this.findParentGroupNames(directoryId, childGroup, EntityDescriptor.group()));
    }

    private Iterable<String> findParentGroupNames(long directoryId, String childName, EntityDescriptor entityDescriptor) {
        MembershipQuery query = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).parentsOf(entityDescriptor).withName(childName).returningAtMost(-1);
        return this.delegate.search(directoryId, query);
    }

    public void addUserToGroup(long directoryId, String userName, String groupName) throws UserNotFoundException, GroupNotFoundException, MembershipAlreadyExistsException {
        log.debug("adding user [ {} ] to group [ {} ]", (Object)userName, (Object)groupName);
        this.membershipCache.removeUserGroupMemberships(directoryId, userName);
        this.delegate.addUserToGroup(directoryId, userName, groupName);
    }

    public BatchResult<String> addUserToGroups(long directoryId, String username, Set<String> groupNames) throws UserNotFoundException {
        this.membershipCache.removeUserGroupMemberships(directoryId, username);
        return this.delegate.addUserToGroups(directoryId, username, groupNames);
    }

    public BatchResult<String> addAllUsersToGroup(long directoryId, Collection<String> userNames, String groupName) throws GroupNotFoundException {
        if (log.isDebugEnabled()) {
            log.debug("adding [ {} ] users to [ {} ]", (Object)userNames.size(), (Object)groupName);
        }
        for (String userName : userNames) {
            this.membershipCache.removeUserGroupMemberships(directoryId, userName);
        }
        return this.delegate.addAllUsersToGroup(directoryId, userNames, groupName);
    }

    public void addGroupToGroup(long directoryId, String childGroupName, String parentGroupName) throws GroupNotFoundException, MembershipAlreadyExistsException {
        log.debug("adding child group [ {} ] to group [ {} ]", (Object)childGroupName, (Object)parentGroupName);
        this.clearChildMembershipFromCache(directoryId, childGroupName);
        this.childGroupCache.removeGroupGroupMemberships(directoryId, parentGroupName);
        this.delegate.addGroupToGroup(directoryId, childGroupName, parentGroupName);
    }

    public BatchResult<String> addAllGroupsToGroup(long directoryId, Collection<String> childGroupNames, String parentGroupName) throws GroupNotFoundException {
        BatchResult allResults = new BatchResult(childGroupNames.size());
        BatchResult result = this.delegate.addAllGroupsToGroup(directoryId, childGroupNames, parentGroupName);
        if (!result.getSuccessfulEntities().isEmpty()) {
            result.getSuccessfulEntities().forEach(childGroupName -> this.clearChildMembershipFromCache(directoryId, (String)childGroupName));
            this.childGroupCache.removeGroupGroupMemberships(directoryId, parentGroupName);
        }
        return allResults;
    }

    public void removeUserFromGroup(long directoryId, String userName, String groupName) throws UserNotFoundException, GroupNotFoundException, MembershipNotFoundException {
        this.membershipCache.removeUserGroupMemberships(directoryId, userName);
        this.delegate.removeUserFromGroup(directoryId, userName, groupName);
    }

    public BatchResult<String> removeUsersFromGroup(long directoryId, Collection<String> userNames, String parentGroupName) throws GroupNotFoundException {
        BatchResult allResults = new BatchResult(userNames.size());
        BatchResult result = this.delegate.removeUsersFromGroup(directoryId, userNames, parentGroupName);
        result.getSuccessfulEntities().forEach(userName -> this.membershipCache.removeUserGroupMemberships(directoryId, (String)userName));
        return allResults;
    }

    public void removeGroupFromGroup(long directoryId, String childGroupName, String parentGroupName) throws GroupNotFoundException, MembershipNotFoundException {
        this.clearChildMembershipFromCache(directoryId, childGroupName);
        this.childGroupCache.removeGroupGroupMemberships(directoryId, parentGroupName);
        this.delegate.removeGroupFromGroup(directoryId, childGroupName, parentGroupName);
    }

    public BatchResult<String> removeGroupsFromGroup(long directoryId, Collection<String> childGroupNames, String parentGroupName) throws GroupNotFoundException {
        BatchResult allResults = new BatchResult(childGroupNames.size());
        BatchResult result = this.delegate.removeGroupsFromGroup(directoryId, childGroupNames, parentGroupName);
        if (!result.getSuccessfulEntities().isEmpty()) {
            result.getSuccessfulEntities().forEach(childGroupName -> this.clearChildMembershipFromCache(directoryId, (String)childGroupName));
            this.childGroupCache.removeGroupGroupMemberships(directoryId, parentGroupName);
        }
        return allResults;
    }

    private void clearChildMembershipFromCache(long directoryId, String childGroupName) {
        this.membershipCache.removeGroupGroupMemberships(directoryId, childGroupName);
        this.parentGroupCache.removeGroupGroupMemberships(directoryId, childGroupName);
    }

    public BoundedCount countDirectMembersOfGroup(long directoryId, String groupName, int potentialMaxCount) {
        return this.delegate.countDirectMembersOfGroup(directoryId, groupName, potentialMaxCount);
    }

    public <T> List<T> search(long directoryId, MembershipQuery<T> query) {
        if (CachedCrowdMembershipDao.isStringCacheable(query)) {
            MembershipQuery<T> stringQuery = query;
            return this.parentStringSearch(directoryId, stringQuery);
        }
        if (CachedCrowdMembershipDao.isParentGroupCacheable(query)) {
            MembershipQuery<T> groupQuery = query;
            return this.parentGroupSearch(directoryId, groupQuery);
        }
        if (CachedCrowdMembershipDao.isChildGroupCacheable(query)) {
            MembershipQuery<T> groupQuery = query;
            return this.childGroupSearch(directoryId, groupQuery);
        }
        log.debug("searching for [ {} ] (no cache)", query);
        return this.delegate.search(directoryId, query);
    }

    public <T> ListMultimap<String, T> searchGroupedByName(long directoryId, MembershipQuery<T> query) {
        if (query.getReturnType().isAssignableFrom(String.class) && !query.isFindChildren() && query.getEntityToMatch().equals((Object)EntityDescriptor.group()) && !CachedCrowdMembershipDao.queryHasRestrictions(query)) {
            return this.searchGroupedByNameGroupsForStringEntities(directoryId, query);
        }
        if (query.getEntityToMatch().getEntityType().equals((Object)Entity.GROUP) && query.getEntityToMatch().getGroupType().equals((Object)GroupType.GROUP) && query.getReturnType().isAssignableFrom(InternalDirectoryGroup.class) && !CachedCrowdMembershipDao.queryHasRestrictions(query)) {
            return this.searchGroupedByNameForInternalDirectoryGroupEntities(directoryId, query);
        }
        log.debug("searchGroupedByName for [ {} ] (no cache)", query);
        return this.delegate.searchGroupedByName(directoryId, query);
    }

    private static <T> boolean queryHasRestrictions(MembershipQuery<?> query) {
        return query.getSearchRestriction() != null && !NullRestriction.class.isAssignableFrom(query.getSearchRestriction().getClass());
    }

    private ListMultimap<String, String> searchGroupedByNameGroupsForStringEntities(long directoryId, MembershipQuery<String> query) {
        if (!query.getEntityToMatch().equals((Object)EntityDescriptor.group())) {
            throw this.unsupportedGroupMembersSearchException(query.getEntityToMatch());
        }
        if (!query.getEntityToReturn().equals((Object)EntityDescriptor.group())) {
            throw new IllegalArgumentException("Group members search does not support returning " + query.getEntityToMatch());
        }
        if (query.isFindChildren()) {
            throw new IllegalArgumentException("Parent group members search does not support finding children");
        }
        ArrayListMultimap groupToGroupsResultMap = ArrayListMultimap.create();
        HashSet missingGroupNames = new HashSet();
        query.getEntityNamesToMatch().forEach(arg_0 -> this.lambda$searchGroupedByNameGroupsForStringEntities$6(directoryId, query, (ListMultimap)groupToGroupsResultMap, missingGroupNames, arg_0));
        if (missingGroupNames.isEmpty()) {
            log.debug("All the child groups have been found in cache.");
            return groupToGroupsResultMap;
        }
        MembershipQuery queryToGetMissingGroups = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).parentsOf(EntityDescriptor.group()).withNames(missingGroupNames).returningAtMost(-1);
        ListMultimap groupToGroupsMapWithoutLimitation = this.delegate.searchGroupedByName(directoryId, queryToGetMissingGroups);
        missingGroupNames.forEach(arg_0 -> this.lambda$searchGroupedByNameGroupsForStringEntities$9(groupToGroupsMapWithoutLimitation, directoryId, query, (ListMultimap)groupToGroupsResultMap, arg_0));
        return groupToGroupsResultMap;
    }

    private ListMultimap<String, InternalDirectoryGroup> searchGroupedByNameForInternalDirectoryGroupEntities(long directoryId, MembershipQuery<InternalDirectoryGroup> query) {
        if (!query.getEntityToMatch().equals((Object)EntityDescriptor.group())) {
            throw this.unsupportedGroupMembersSearchException(query.getEntityToMatch());
        }
        GroupMembershipCache cache = query.isFindChildren() ? this.childGroupCache : this.parentGroupCache;
        ArrayListMultimap groupToGroupsResultMap = ArrayListMultimap.create();
        HashSet<String> missingGroupNames = new HashSet<String>();
        query.getEntityNamesToMatch().forEach(arg_0 -> CachedCrowdMembershipDao.lambda$searchGroupedByNameForInternalDirectoryGroupEntities$11(cache, directoryId, query, (ListMultimap)groupToGroupsResultMap, missingGroupNames, arg_0));
        if (missingGroupNames.isEmpty()) {
            log.debug("All the groups have been found in cache.");
            return groupToGroupsResultMap;
        }
        MembershipQuery<InternalDirectoryGroup> queryToGetMissingGroups = this.buildMultiGroupQuery(query, missingGroupNames);
        ListMultimap groupToGroupsMapWithoutLimitation = this.delegate.searchGroupedByName(directoryId, queryToGetMissingGroups);
        missingGroupNames.forEach(arg_0 -> CachedCrowdMembershipDao.lambda$searchGroupedByNameForInternalDirectoryGroupEntities$14(groupToGroupsMapWithoutLimitation, cache, directoryId, query, (ListMultimap)groupToGroupsResultMap, arg_0));
        return groupToGroupsResultMap;
    }

    private MembershipQuery<InternalDirectoryGroup> buildMultiGroupQuery(MembershipQuery<InternalDirectoryGroup> query, Set<String> groupNames) {
        QueryBuilder.PartialEntityQuery partialEntityQueryBuilder = QueryBuilder.queryFor(InternalDirectoryGroup.class, (EntityDescriptor)EntityDescriptor.group());
        QueryBuilder.PartialMembershipQueryWithEntityToMatch partialMembershipQueryBuilder = query.isFindChildren() ? partialEntityQueryBuilder.childrenOf(EntityDescriptor.group()) : partialEntityQueryBuilder.parentsOf(EntityDescriptor.group());
        return partialMembershipQueryBuilder.withNames(groupNames).returningAtMost(-1);
    }

    private List<InternalDirectoryGroup> childGroupSearch(long directoryId, MembershipQuery<InternalDirectoryGroup> query) {
        if (!query.getEntityToMatch().equals((Object)EntityDescriptor.group())) {
            throw this.unsupportedGroupMembersSearchException(query.getEntityToMatch());
        }
        String entityName = query.getEntityNameToMatch();
        log.debug("searching for all child group objects for group [ {} ]", (Object)entityName);
        return CachedCrowdMembershipDao.limit(query, this.childGroupCache.getGroupsForGroup(directoryId, entityName, () -> this.delegate.search(directoryId, CachedCrowdMembershipDao.childGroupSearchQuery(query, entityName))));
    }

    private IllegalArgumentException unsupportedGroupMembersSearchException(EntityDescriptor entityToMatch) {
        return new IllegalArgumentException("Group members search does not support matching on " + entityToMatch);
    }

    private List<InternalDirectoryGroup> parentGroupSearch(long directoryId, MembershipQuery<InternalDirectoryGroup> query) {
        if (!query.getEntityToMatch().equals((Object)EntityDescriptor.group())) {
            throw new IllegalArgumentException("Group parents search does not support matching on " + query.getEntityToMatch());
        }
        String entityName = query.getEntityNameToMatch();
        log.debug("searching for all parent groups objects for group [ {} ]", (Object)entityName);
        return CachedCrowdMembershipDao.limit(query, this.parentGroupCache.getGroupsForGroup(directoryId, entityName, () -> this.delegate.search(directoryId, CachedCrowdMembershipDao.parentGroupSearchQuery(query, entityName))));
    }

    private static MembershipQuery<InternalDirectoryGroup> childGroupSearchQuery(MembershipQuery<InternalDirectoryGroup> query, String entityName) {
        return QueryBuilder.queryFor((Class)query.getReturnType(), (EntityDescriptor)EntityDescriptor.group()).childrenOf(EntityDescriptor.group()).withName(entityName).returningAtMost(-1);
    }

    private static MembershipQuery<InternalDirectoryGroup> parentGroupSearchQuery(MembershipQuery<InternalDirectoryGroup> query, String entityName) {
        return QueryBuilder.queryFor((Class)query.getReturnType(), (EntityDescriptor)EntityDescriptor.group()).parentsOf(EntityDescriptor.group()).withName(entityName).returningAtMost(-1);
    }

    private List<String> parentStringSearch(long directoryId, MembershipQuery<String> query) {
        List<String> groupNames;
        String entityName = query.getEntityNameToMatch();
        if (query.getEntityToMatch().equals((Object)EntityDescriptor.user())) {
            log.debug("searching for all groups for user [ {} ]", (Object)entityName);
            groupNames = this.getGroupNamesByUser(directoryId, entityName);
        } else {
            log.debug("searching for all parent groups for group [ {} ]", (Object)entityName);
            groupNames = this.getGroupNamesByChildGroup(directoryId, entityName);
        }
        return CachedCrowdMembershipDao.constrainResults(groupNames, query.getStartIndex(), query.getMaxResults());
    }

    private List<String> getGroupNamesByChildGroup(long directoryId, String childGroupName) {
        return this.membershipCache.getGroupsForGroup(directoryId, childGroupName, () -> {
            MembershipQuery completeQuery = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).parentsOf(EntityDescriptor.group()).withName(childGroupName).returningAtMost(-1);
            return this.delegate.search(directoryId, completeQuery);
        });
    }

    private List<String> getGroupNamesByUser(long directoryId, String username) {
        return this.membershipCache.getGroupsForUser(directoryId, username, () -> {
            MembershipQuery completeQuery = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).parentsOf(EntityDescriptor.user()).withName(username).returningAtMost(-1);
            return this.delegate.search(directoryId, completeQuery);
        });
    }

    private static boolean isChildGroupCacheable(MembershipQuery<?> query) {
        return query.getReturnType().isAssignableFrom(InternalDirectoryGroup.class) && query.isFindChildren() && query.getEntityToMatch().getEntityType().equals((Object)Entity.GROUP) && query.getEntityToMatch().getGroupType().equals((Object)GroupType.GROUP) && !CachedCrowdMembershipDao.queryHasRestrictions(query);
    }

    private static boolean isParentGroupCacheable(MembershipQuery<?> query) {
        return query.getReturnType().isAssignableFrom(InternalDirectoryGroup.class) && !query.isFindChildren() && query.getEntityToMatch().getEntityType().equals((Object)Entity.GROUP) && query.getEntityToMatch().getGroupType().equals((Object)GroupType.GROUP) && !CachedCrowdMembershipDao.queryHasRestrictions(query);
    }

    private static boolean isStringCacheable(MembershipQuery<?> query) {
        return query.getReturnType().isAssignableFrom(String.class) && !query.isFindChildren() && !CachedCrowdMembershipDao.queryHasRestrictions(query);
    }

    private static <T> List<T> limit(MembershipQuery<?> query, Collection<T> results) {
        return CachedCrowdMembershipDao.constrainResults(results, query.getStartIndex(), query.getMaxResults());
    }

    public static <T> List<T> constrainResults(Collection<T> results, int startIndex, int maxResults) {
        return results.stream().skip(startIndex).limit(maxResults == -1 ? Integer.MAX_VALUE : (long)maxResults).collect(Collectors.toList());
    }

    private static /* synthetic */ void lambda$searchGroupedByNameForInternalDirectoryGroupEntities$14(ListMultimap groupToGroupsMapWithoutLimitation, GroupMembershipCache cache, long directoryId, MembershipQuery query, ListMultimap groupToGroupsResultMap, String groupName) {
        List childOrParentGroups = groupToGroupsMapWithoutLimitation.get((Object)groupName);
        cache.getGroupsForGroup(directoryId, groupName, () -> childOrParentGroups != null ? childOrParentGroups : Collections.emptyList());
        CachedCrowdMembershipDao.limit(query, childOrParentGroups).forEach(child -> groupToGroupsResultMap.put((Object)groupName, child));
    }

    private static /* synthetic */ void lambda$searchGroupedByNameForInternalDirectoryGroupEntities$11(GroupMembershipCache cache, long directoryId, MembershipQuery query, ListMultimap groupToGroupsResultMap, Set missingGroupNames, String entityName) {
        List<InternalDirectoryGroup> parentOrChildGroupList = cache.getGroupsForGroup(directoryId, entityName);
        if (parentOrChildGroupList != null) {
            CachedCrowdMembershipDao.limit(query, parentOrChildGroupList).forEach(parentOrChild -> groupToGroupsResultMap.put((Object)entityName, parentOrChild));
        } else {
            missingGroupNames.add(entityName);
        }
    }

    private /* synthetic */ void lambda$searchGroupedByNameGroupsForStringEntities$9(ListMultimap groupToGroupsMapWithoutLimitation, long directoryId, MembershipQuery query, ListMultimap groupToGroupsResultMap, String groupName) {
        List parentGroups = groupToGroupsMapWithoutLimitation.get((Object)groupName);
        this.membershipCache.getGroupsForGroup(directoryId, groupName, () -> parentGroups != null ? parentGroups : Collections.emptyList());
        CachedCrowdMembershipDao.limit(query, parentGroups).forEach(child -> groupToGroupsResultMap.put((Object)groupName, child));
    }

    private /* synthetic */ void lambda$searchGroupedByNameGroupsForStringEntities$6(long directoryId, MembershipQuery query, ListMultimap groupToGroupsResultMap, Set missingGroupNames, String childGroupName) {
        List<String> parentGroupList = this.membershipCache.getGroupsForGroup(directoryId, childGroupName);
        if (parentGroupList != null) {
            CachedCrowdMembershipDao.limit(query, parentGroupList).forEach(parentGroup -> groupToGroupsResultMap.put((Object)childGroupName, parentGroup));
        } else {
            missingGroupNames.add(childGroupName);
        }
    }
}

