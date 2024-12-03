/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.MultiValuesQueriesSupport
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.model.NameComparator
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.QueryBuilder$PartialEntityQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.search.util.QuerySplitter
 *  com.atlassian.crowd.search.util.SearchResultsUtil
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableListMultimap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ListMultimap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.SetMultimap
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.crowd.directory.MultiValuesQueriesSupport;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.directory.nestedgroups.NestedGroupsCacheProvider;
import com.atlassian.crowd.manager.directory.nestedgroups.NestedGroupsIterator;
import com.atlassian.crowd.manager.directory.nestedgroups.NestedGroupsProvider;
import com.atlassian.crowd.manager.directory.nestedgroups.NestedGroupsProviderBuilder;
import com.atlassian.crowd.model.NameComparator;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.search.util.QuerySplitter;
import com.atlassian.crowd.search.util.SearchResultsUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

class RemoteDirectorySearcher {
    private final RemoteDirectory remoteDirectory;
    private final Optional<NestedGroupsCacheProvider> cacheProvider;
    private final int batchSizeForInternalDir = Integer.getInteger("crowd.query.in.batch.size", 1000);

    RemoteDirectorySearcher(@Nonnull RemoteDirectory remoteDirectory, Optional<NestedGroupsCacheProvider> cacheProvider) {
        this.remoteDirectory = (RemoteDirectory)Preconditions.checkNotNull((Object)remoteDirectory, (Object)"remoteDirectory");
        this.cacheProvider = cacheProvider;
    }

    boolean isUserDirectGroupMember(String userName, String groupName) throws OperationFailedException {
        return this.remoteDirectory.isUserDirectGroupMember(userName, groupName);
    }

    boolean isGroupDirectGroupMember(String childGroup, String parentGroup) throws OperationFailedException {
        return !childGroup.equals(parentGroup) && this.remoteDirectory.supportsNestedGroups() && this.remoteDirectory.isGroupDirectGroupMember(childGroup, parentGroup);
    }

    <T> List<T> searchDirectGroupRelationships(MembershipQuery<T> query) throws OperationFailedException {
        if (!this.isQuerySupported(query)) {
            return ImmutableList.of();
        }
        return QuerySplitter.batchNamesToMatchIfNeeded(query, arg_0 -> ((RemoteDirectory)this.remoteDirectory).searchGroupRelationships(arg_0), (int)this.getBatchSize());
    }

    <T> ListMultimap<String, T> searchDirectGroupRelationshipsGroupedByName(MembershipQuery<T> query) throws OperationFailedException {
        if (query.getEntityNamesToMatch().size() > 1) {
            Preconditions.checkArgument((query.getStartIndex() == 0 ? 1 : 0) != 0);
            Preconditions.checkArgument((query.getMaxResults() == -1 ? 1 : 0) != 0);
        }
        if (!this.isQuerySupported(query)) {
            return ImmutableListMultimap.of();
        }
        if (this.remoteDirectory instanceof MultiValuesQueriesSupport) {
            MultiValuesQueriesSupport multiValuesSupport = (MultiValuesQueriesSupport)this.remoteDirectory;
            int batchSize = this.getBatchSize();
            if (query.getEntityNamesToMatch().size() > batchSize) {
                ArrayListMultimap result = ArrayListMultimap.create();
                for (MembershipQuery currentQuery : query.splitEntityNamesToMatch(batchSize)) {
                    result.putAll((Multimap)multiValuesSupport.searchGroupRelationshipsGroupedByName(currentQuery));
                }
                return result;
            }
            return multiValuesSupport.searchGroupRelationshipsGroupedByName(query);
        }
        ArrayListMultimap resultMap = ArrayListMultimap.create();
        for (String entityToMatch : query.getEntityNamesToMatch()) {
            resultMap.putAll((Object)entityToMatch, (Iterable)this.remoteDirectory.searchGroupRelationships(query.withEntityNames(new String[]{entityToMatch})));
        }
        return resultMap;
    }

    private <T> boolean isQuerySupported(MembershipQuery<T> query) {
        if (RemoteDirectorySearcher.isNestedGroupQuery(query) && !this.remoteDirectory.supportsNestedGroups()) {
            return false;
        }
        return !RemoteDirectorySearcher.isLegacyQuery(query);
    }

    private static <T> boolean isNestedGroupQuery(MembershipQuery<T> query) {
        return query.getEntityToMatch().getEntityType() == Entity.GROUP && query.getEntityToReturn().getEntityType() == Entity.GROUP;
    }

    private static <T> boolean isLegacyQuery(MembershipQuery<T> query) {
        return RemoteDirectorySearcher.isLegacyRole(query.getEntityToMatch()) || RemoteDirectorySearcher.isLegacyRole(query.getEntityToReturn());
    }

    private static boolean isLegacyRole(EntityDescriptor entity) {
        return entity.getEntityType() == Entity.GROUP && entity.getGroupType() == GroupType.LEGACY_ROLE;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    boolean isUserNestedGroupMember(String username, String groupName) throws OperationFailedException {
        List<String> directGroups = this.searchDirectGroupRelationships(this.createUserParentsQuery(String.class, GroupType.GROUP, (Set<String>)ImmutableSet.of((Object)username)));
        if (directGroups.isEmpty()) return false;
        if (!this.allSubGroupsIterator((Collection<String>)ImmutableList.of((Object)groupName), GroupType.GROUP).anyMatch(IdentifierUtils.containsIdentifierPredicate(directGroups)::test)) return false;
        return true;
    }

    public Set<String> filterNestedUserMembersOfGroups(Set<String> userNames, Set<String> groupNames) throws OperationFailedException {
        SetMultimap<String, String> parentsByUsername = this.findAllDirectParentsOfTheUsers(userNames);
        HashMultimap usersByGroupname = HashMultimap.create();
        parentsByUsername.entries().forEach(arg_0 -> RemoteDirectorySearcher.lambda$filterNestedUserMembersOfGroups$0((SetMultimap)usersByGroupname, arg_0));
        HashSet<String> result = new HashSet<String>();
        NestedGroupsIterator<String> groupsIterator = this.allSubGroupsIterator(groupNames, GroupType.GROUP);
        while (!parentsByUsername.isEmpty() && groupsIterator.hasNext()) {
            Set users = usersByGroupname.get((Object)IdentifierUtils.toLowerCase((String)groupsIterator.next()));
            result.addAll(users);
            parentsByUsername.keySet().removeAll(users);
        }
        return this.filterNames(userNames, result);
    }

    private Set<String> filterNames(Collection<String> input, Collection<String> filter) {
        return input.stream().filter(IdentifierUtils.containsIdentifierPredicate(filter)).collect(Collectors.toSet());
    }

    private SetMultimap<String, String> findAllDirectParentsOfTheUsers(Set<String> userNames) throws OperationFailedException {
        MembershipQuery<String> query = this.createUserParentsQuery(String.class, GroupType.GROUP, userNames);
        return HashMultimap.create(this.searchDirectGroupRelationshipsGroupedByName(query));
    }

    boolean isGroupNestedGroupMember(String childGroup, String parentGroup) throws OperationFailedException {
        return this.remoteDirectory.supportsNestedGroups() && !IdentifierUtils.equalsInLowerCase((String)childGroup, (String)parentGroup) && this.allSubGroupsIterator((Collection<String>)ImmutableList.of((Object)parentGroup), GroupType.GROUP).anyMatch(g -> IdentifierUtils.equalsInLowerCase((String)g, (String)childGroup));
    }

    <T> List<T> searchNestedGroupRelationships(MembershipQuery<T> query) throws OperationFailedException {
        if (!this.remoteDirectory.supportsNestedGroups()) {
            return this.searchDirectGroupRelationships(query);
        }
        if (query.isFindChildren() && query.getEntityToReturn().getEntityType() == Entity.USER) {
            List<String> allGroups = this.allSubGroupsIterator(query.getEntityNamesToMatch(), query.getEntityToMatch().getGroupType()).toList();
            return this.searchDirectGroupRelationships(query.withEntityNames(allGroups));
        }
        if (query.getReturnType() == String.class) {
            List<T> groups = this.searchNestedGroups(query.withReturnType(Group.class));
            return SearchResultsUtil.convertEntitiesToNames(groups);
        }
        return this.searchNestedGroups(query);
    }

    private <T extends Group> List<T> searchNestedGroups(MembershipQuery<T> query) throws OperationFailedException {
        NestedGroupsIterator<Group> iterator;
        Preconditions.checkArgument((query.getEntityToReturn().getEntityType() == Entity.GROUP ? 1 : 0) != 0, (Object)"You can only find the GROUP memberships of USER or GROUP");
        if (query.getEntityToMatch().getEntityType() == Entity.USER) {
            List<T> directParents = this.searchDirectGroupRelationships(query.withAllResults());
            iterator = NestedGroupsIterator.groupsIterator(directParents, true, this.provider(query));
        } else {
            iterator = NestedGroupsIterator.groupsIterator(query.getEntityNamesToMatch(), this.provider(query));
        }
        List<Group> allResults = iterator.toList();
        allResults.sort(NameComparator.directoryEntityComparator());
        return SearchResultsUtil.constrainResults(allResults, (int)query.getStartIndex(), (int)query.getMaxResults());
    }

    private <T> MembershipQuery<T> createUserParentsQuery(Class<T> returnedType, GroupType groupType, Set<String> usernames) {
        return QueryBuilder.queryFor(returnedType, (EntityDescriptor)EntityDescriptor.group((GroupType)groupType)).parentsOf(EntityDescriptor.user()).withNames(usernames).returningAtMost(-1);
    }

    private NestedGroupsIterator<String> allSubGroupsIterator(Collection<String> groups, GroupType groupType) {
        return NestedGroupsIterator.namesIterator(groups, true, this.nestedGroupsProvider(Group.class, true, groupType));
    }

    private NestedGroupsProvider nestedGroupsProvider(Class<? extends Group> cls, boolean isChildrenQuery, GroupType groupType) {
        NestedGroupsProviderBuilder builder = NestedGroupsProviderBuilder.create().useGroupName();
        if (!this.remoteDirectory.supportsNestedGroups()) {
            return builder.setSingleGroupProvider(name -> ImmutableList.of()).build();
        }
        builder.setProvider(names -> ImmutableListMultimap.copyOf(this.searchDirectGroupRelationshipsGroupedByName(this.createQuery(cls, isChildrenQuery, groupType, names)))).setBatchSize(this.getBatchSize());
        if (this.cacheProvider.isPresent() && cls.equals(Group.class)) {
            builder.useCache(this.cacheProvider.get(), this.remoteDirectory.getDirectoryId(), isChildrenQuery, groupType);
        }
        return builder.build();
    }

    private MembershipQuery<? extends Group> createQuery(Class<? extends Group> returnClass, boolean isChildrenQuery, GroupType groupType, Collection<String> names) {
        EntityDescriptor groupDescriptor = EntityDescriptor.group((GroupType)groupType);
        QueryBuilder.PartialEntityQuery queryBuilder = QueryBuilder.queryFor(returnClass, (EntityDescriptor)groupDescriptor);
        return (isChildrenQuery ? queryBuilder.childrenOf(groupDescriptor) : queryBuilder.parentsOf(groupDescriptor)).withNames(names).returningAtMost(-1);
    }

    private NestedGroupsProvider provider(MembershipQuery<? extends Group> query) {
        Preconditions.checkArgument((boolean)Group.class.isAssignableFrom(query.getReturnType()));
        return this.nestedGroupsProvider(query.getReturnType(), query.isFindChildren(), query.isFindChildren() ? query.getEntityToMatch().getGroupType() : query.getEntityToReturn().getGroupType());
    }

    private int getBatchSize() {
        return this.remoteDirectory instanceof MultiValuesQueriesSupport ? this.batchSizeForInternalDir : 1;
    }

    private static /* synthetic */ void lambda$filterNestedUserMembersOfGroups$0(SetMultimap usersByGroupname, Map.Entry entry) {
        usersByGroupname.put((Object)IdentifierUtils.toLowerCase((String)((String)entry.getValue())), entry.getKey());
    }
}

