/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.function.ExceptionTranslators
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.group.ImmutableMembership
 *  com.atlassian.crowd.model.group.Membership
 *  com.atlassian.crowd.model.group.Membership$MembershipIterationException
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.google.common.collect.Iterators
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.AzureAdDirectory;
import com.atlassian.crowd.directory.query.FetchMode;
import com.atlassian.crowd.directory.query.GraphQuery;
import com.atlassian.crowd.directory.query.MicrosoftGraphQueryTranslator;
import com.atlassian.crowd.directory.query.ODataFilter;
import com.atlassian.crowd.directory.query.ODataSelect;
import com.atlassian.crowd.directory.query.ODataTop;
import com.atlassian.crowd.directory.rest.AzureAdPagingWrapper;
import com.atlassian.crowd.directory.rest.AzureAdRestClient;
import com.atlassian.crowd.directory.rest.entity.group.GraphGroup;
import com.atlassian.crowd.directory.rest.entity.membership.DirectoryObject;
import com.atlassian.crowd.directory.rest.mapper.AzureAdRestEntityMapper;
import com.atlassian.crowd.directory.rest.util.MembershipFilterUtil;
import com.atlassian.crowd.directory.rest.util.ThrowingMapMergeOperatorUtil;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.function.ExceptionTranslators;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.ImmutableMembership;
import com.atlassian.crowd.model.group.Membership;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.search.EntityDescriptor;
import com.google.common.collect.Iterators;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

public class AzureMembershipHelper {
    private AzureAdRestClient restClient;
    private AzureAdPagingWrapper pagingWrapper;
    private MicrosoftGraphQueryTranslator queryTranslator;
    private AzureAdRestEntityMapper restEntityMapper;
    private AzureAdDirectory directory;

    public AzureMembershipHelper(AzureAdRestClient restClient, AzureAdPagingWrapper pagingWrapper, MicrosoftGraphQueryTranslator queryTranslator, AzureAdRestEntityMapper restEntityMapper, AzureAdDirectory directory) {
        this.restClient = restClient;
        this.pagingWrapper = pagingWrapper;
        this.queryTranslator = queryTranslator;
        this.restEntityMapper = restEntityMapper;
        this.directory = directory;
    }

    public Iterator<Membership> membershipIterator() throws OperationFailedException {
        ODataSelect select = this.queryTranslator.resolveAzureAdColumnsForSingleEntityTypeQuery(EntityDescriptor.group(), FetchMode.NAME_AND_ID);
        GraphQuery query = new GraphQuery(ODataFilter.EMPTY, select, 0, ODataTop.FULL_PAGE);
        List groups = this.pagingWrapper.fetchAllResults(this.restClient.searchGroups(query));
        Map<String, String> groupNamesToIds = ThrowingMapMergeOperatorUtil.mapUniqueNamesToIds(groups, GraphGroup::getDisplayName, GraphGroup::getId, "group");
        Function lookUpMembers = ExceptionTranslators.toRuntimeException(entry -> this.getMembership((String)entry.getKey(), (String)entry.getValue()), Membership.MembershipIterationException::new);
        return Iterators.transform(groupNamesToIds.entrySet().iterator(), lookUpMembers::apply);
    }

    public Pair<List<UserWithAttributes>, List<GroupWithAttributes>> getDirectChildren(String groupId) throws OperationFailedException {
        ODataSelect select = this.queryTranslator.translateColumnsForUsersAndGroupsQuery(FetchMode.FULL);
        Pair<List<DirectoryObject>, List<DirectoryObject>> children = this.getChildrenUsersAndGroups(groupId, select);
        return Pair.of(this.mapTo((List)children.getLeft(), UserWithAttributes.class), this.mapTo((List)children.getRight(), GroupWithAttributes.class));
    }

    private Pair<List<DirectoryObject>, List<DirectoryObject>> getChildrenUsersAndGroups(String groupId, ODataSelect select) throws OperationFailedException {
        List children = this.pagingWrapper.fetchAllResults(this.restClient.getDirectChildrenOfGroup(groupId, select));
        return Pair.of(children.stream().filter(MembershipFilterUtil::isUser).collect(Collectors.toList()), !this.directory.supportsNestedGroups() ? Collections.emptyList() : children.stream().filter(MembershipFilterUtil::isGroup).collect(Collectors.toList()));
    }

    private Membership getMembership(String groupName, String groupId) throws OperationFailedException {
        Pair<List<DirectoryObject>, List<DirectoryObject>> children = this.getChildrenUsersAndGroups(groupId, this.queryTranslator.translateColumnsForUsersAndGroupsQuery(FetchMode.NAME));
        return new ImmutableMembership(groupName, this.mapTo((List)children.getLeft(), String.class), this.mapTo((List)children.getRight(), String.class));
    }

    private <T> List<T> mapTo(List<DirectoryObject> list, Class<? extends T> cls) {
        return list.stream().map(this.mapTo(cls)).collect(Collectors.toList());
    }

    private <T> Function<DirectoryObject, T> mapTo(Class<? extends T> cls) {
        String alternateUsernameAttribute = this.directory.getAlternativeUsernameAttribute();
        return o -> this.restEntityMapper.mapDirectoryObject((DirectoryObject)o, cls, this.directory.getDirectoryId(), alternateUsernameAttribute);
    }
}

