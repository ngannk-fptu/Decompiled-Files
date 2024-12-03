/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.query.InExpressionBuilder
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.embedded.spi.MembershipDao
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.MembershipAlreadyExistsException
 *  com.atlassian.crowd.exception.MembershipNotFoundException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.InternalEntity
 *  com.atlassian.crowd.model.group.InternalGroup
 *  com.atlassian.crowd.model.user.InternalUser
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.util.BatchResult
 *  com.atlassian.crowd.util.BoundedCount
 *  com.atlassian.crowd.util.persistence.hibernate.batch.BatchProcessor
 *  com.atlassian.crowd.util.persistence.hibernate.batch.HibernateOperation
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ListMultimap
 *  com.google.common.collect.Lists
 *  io.atlassian.fugue.Pair
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.dialect.Dialect
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.impl.user.crowd.hibernate;

import com.atlassian.confluence.core.persistence.schema.hibernate.HibernateSchemaInformationService;
import com.atlassian.confluence.impl.hibernate.query.InExpressionBuilder;
import com.atlassian.confluence.impl.user.crowd.hibernate.GroupingHibernateSearch;
import com.atlassian.confluence.impl.user.crowd.hibernate.HibernateSearch;
import com.atlassian.confluence.impl.user.crowd.hibernate.InternalGroupDao;
import com.atlassian.confluence.impl.user.crowd.hibernate.InternalUserDao;
import com.atlassian.confluence.impl.user.crowd.hibernate.batch.operation.RemoveOperation;
import com.atlassian.confluence.impl.user.crowd.hibernate.batch.operation.SaveOrUpdateOperation;
import com.atlassian.crowd.embedded.hibernate2.HibernateMembership;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.embedded.spi.MembershipDao;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.MembershipAlreadyExistsException;
import com.atlassian.crowd.exception.MembershipNotFoundException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.InternalEntity;
import com.atlassian.crowd.model.group.InternalGroup;
import com.atlassian.crowd.model.user.InternalUser;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.util.BatchResult;
import com.atlassian.crowd.util.BoundedCount;
import com.atlassian.crowd.util.persistence.hibernate.batch.BatchProcessor;
import com.atlassian.crowd.util.persistence.hibernate.batch.HibernateOperation;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import io.atlassian.fugue.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class HibernateMembershipDao
implements MembershipDao {
    private static final Logger log = LoggerFactory.getLogger(HibernateMembershipDao.class);
    private static final String DIRECTORY_ID = "directoryId";
    private static final String PARENT_NAME = "parentName";
    private static final String CHILD_NAME = "childName";
    private static final String CHILD_NAMES = "childNames";
    private static final int QUERY_PARTITION_BUFFER_SIZE = 100;
    private static final Function<InternalUser, String> GET_INTERNAL_USER_LOWER_NAME = InternalUser::getLowerName;
    private final HibernateTemplate hibernateTemplate;
    private final InternalUserDao userDao;
    private final InternalGroupDao groupDao;
    private final BatchProcessor batchProcessor;
    private final HibernateSchemaInformationService hibernateSchemaInformationService;

    public HibernateMembershipDao(SessionFactory sessionFactory, InternalUserDao userDao, InternalGroupDao groupDao, BatchProcessor batchProcessor, HibernateSchemaInformationService hibernateSchemaInformationService) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
        this.userDao = userDao;
        this.groupDao = groupDao;
        this.batchProcessor = batchProcessor;
        this.hibernateSchemaInformationService = hibernateSchemaInformationService;
    }

    public BatchResult<String> addAllUsersToGroup(long directoryId, Collection<String> userNames, String groupName) throws GroupNotFoundException {
        Collection<InternalUser> internalUsers = this.userDao.findByNames(directoryId, userNames);
        InternalGroup group = this.groupDao.internalFindByName(directoryId, groupName);
        HashSet<HibernateMembership> memberships = new HashSet<HibernateMembership>();
        for (InternalUser internalUser : internalUsers) {
            memberships.add(HibernateMembership.groupUserMembership(group, internalUser));
        }
        BatchResult<HibernateMembership> daoResult = this.addAll(memberships);
        if (!daoResult.getFailedEntities().isEmpty()) {
            log.error("The following group memberships could not be processed:");
            for (Iterator entity : daoResult.getFailedEntities()) {
                String userName = ((HibernateMembership)((Object)entity)).getUserMember().getName();
                log.error("{} into {}", (Object)userName, (Object)groupName);
            }
            log.error("Please try to resolve any errors with these users and groups, and try again.");
        }
        BatchResult batchResult = new BatchResult(daoResult.getTotalSuccessful());
        for (HibernateMembership internalMembership : daoResult.getSuccessfulEntities()) {
            batchResult.addSuccess((Object)internalMembership.getUserMember().getName());
        }
        for (HibernateMembership internalMembership : daoResult.getFailedEntities()) {
            batchResult.addFailure((Object)internalMembership.getUserMember().getName());
        }
        Set namesFound = internalUsers.stream().map(GET_INTERNAL_USER_LOWER_NAME).collect(Collectors.toSet());
        for (String userName : userNames) {
            if (namesFound.contains(IdentifierUtils.toLowerCase((String)userName))) continue;
            batchResult.addFailure((Object)userName);
        }
        return batchResult;
    }

    public boolean isUserDirectMember(long directoryId, String userName, String groupName) {
        return this.internalFindUserMembership(directoryId, userName, groupName) != null;
    }

    public boolean isGroupDirectMember(long directoryId, String childGroupName, String parentGroupName) {
        return this.internalFindGroupMembership(directoryId, childGroupName, parentGroupName) != null;
    }

    public void addUserToGroup(long directoryId, String userName, String groupName) throws UserNotFoundException, GroupNotFoundException, MembershipAlreadyExistsException {
        InternalUser internalUser = this.userDao.internalFindByName(directoryId, userName);
        InternalGroup internalGroup = this.groupDao.internalFindByName(directoryId, groupName);
        if (this.internalFindUserMembership(directoryId, userName, groupName) != null) {
            throw new MembershipAlreadyExistsException(userName, groupName);
        }
        this.hibernateTemplate.save((Object)HibernateMembership.groupUserMembership(internalGroup, internalUser));
    }

    public BatchResult<String> addUserToGroups(long directoryId, String username, Set<String> groupNames) throws UserNotFoundException {
        InternalUser user = this.userDao.internalFindByName(directoryId, username);
        BatchResult batchResult = new BatchResult(groupNames.size());
        LinkedHashMap<String, HibernateMembership> memberships = new LinkedHashMap<String, HibernateMembership>();
        for (String groupName : groupNames) {
            try {
                InternalGroup group = this.groupDao.internalFindByName(directoryId, groupName);
                memberships.put(groupName, HibernateMembership.groupUserMembership(group, user));
            }
            catch (GroupNotFoundException exception) {
                log.warn("Group {} does not exist.", (Object)groupName);
                batchResult.addFailure((Object)groupName);
            }
        }
        return this.persistMemberships(memberships, batchResult);
    }

    private <T> BatchResult<T> persistMemberships(Map<T, HibernateMembership> memberships, BatchResult<T> batchResult) {
        memberships.forEach((groupName, membership) -> {
            try {
                this.hibernateTemplate.save(membership);
                batchResult.addSuccess(groupName);
            }
            catch (DataAccessException ex) {
                batchResult.addFailure(groupName);
            }
        });
        return batchResult;
    }

    public void addGroupToGroup(long directoryId, String childGroup, String parentGroup) throws GroupNotFoundException, MembershipAlreadyExistsException {
        InternalGroup internalChildGroup = this.groupDao.internalFindByName(directoryId, childGroup);
        InternalGroup internalParentGroup = this.groupDao.internalFindByName(directoryId, parentGroup);
        if (this.internalFindGroupMembership(directoryId, childGroup, parentGroup) != null) {
            throw new MembershipAlreadyExistsException(childGroup, parentGroup);
        }
        this.hibernateTemplate.save((Object)HibernateMembership.groupGroupMembership(internalParentGroup, internalChildGroup));
    }

    public BatchResult<String> addAllGroupsToGroup(long directoryId, Collection<String> childGroupNames, String parentGroupName) throws GroupNotFoundException {
        List<InternalGroup> childGroups = this.findByNames(directoryId, childGroupNames);
        InternalGroup parentGroup = this.groupDao.internalFindByName(directoryId, parentGroupName);
        Pair<Set<HibernateMembership>, Collection<String>> newMembershipValidationResult = this.validateNewMemberships(directoryId, childGroups, parentGroup, this.fetchExistingGroupMemberships(directoryId, childGroupNames, parentGroupName));
        Set membershipsToAdd = (Set)newMembershipValidationResult.left();
        Collection invalidMemberships = (Collection)newMembershipValidationResult.right();
        return this.addAllMemberships(membershipsToAdd, this.nonExistingEntities(childGroupNames, childGroups), invalidMemberships);
    }

    private List<InternalGroup> findByNames(long directoryId, Collection<String> names) {
        return (List)this.hibernateTemplate.execute(session -> {
            ArrayList<String> groupNames = new ArrayList<String>(IdentifierUtils.toLowerCase((Collection)names));
            int inExpressionLimit = this.hibernateSchemaInformationService.getDialect().getInExpressionCountLimit();
            if (this.isPartitioningNeeded(inExpressionLimit, groupNames.size())) {
                return Lists.partition(groupNames, (int)(inExpressionLimit - 100)).stream().map(partitionedGroupNames -> this.executeGroupNameQuery(directoryId, (List<String>)partitionedGroupNames, session)).flatMap(Collection::stream).collect(Collectors.toList());
            }
            return this.executeGroupNameQuery(directoryId, groupNames, session);
        });
    }

    private boolean isPartitioningNeeded(int limit, int collectionSize) {
        return limit > 0 && collectionSize > limit;
    }

    private List<InternalGroup> executeGroupNameQuery(long directoryId, List<String> groupNames, Session session) {
        InExpressionBuilder inExpressionBuilder = InExpressionBuilder.getInExpressionBuilderDefaultLimit((String)"lowerName", (String)"names", groupNames, (Dialect)this.hibernateSchemaInformationService.getDialect());
        String queryString = "from InternalGroup grp where grp.directory.id = :directoryId and " + inExpressionBuilder.buildInExpressionString();
        Query queryObject = session.createQuery(queryString, InternalGroup.class).setParameter(DIRECTORY_ID, (Object)directoryId);
        inExpressionBuilder.substituteInExpressionParameters(queryObject);
        return queryObject.list();
    }

    private Pair<Set<HibernateMembership>, Collection<String>> validateNewMemberships(long directoryId, Collection<InternalGroup> childGroups, InternalGroup parentGroup, List<HibernateMembership> existingMemberships) {
        PartitioningByResult<InternalGroup> compatibleGroupTypes = PartitioningByResult.partitionBy(childGroups, this.compatibleGroupTypes(directoryId, parentGroup));
        PartitioningByResult<InternalGroup> newMemberships = PartitioningByResult.partitionBy(compatibleGroupTypes.success, this.nonExistingMemberships(directoryId, parentGroup, existingMemberships));
        Collection failuresAsGroupNames = Stream.concat(compatibleGroupTypes.failures.stream(), newMemberships.failures.stream()).map(InternalEntity::getName).collect(Collectors.toSet());
        Set newMembershipsToCreate = newMemberships.success.stream().map(childGroup -> HibernateMembership.groupGroupMembership(parentGroup, childGroup)).collect(Collectors.toSet());
        return Pair.pair(newMembershipsToCreate, (Object)failuresAsGroupNames);
    }

    private Predicate<InternalGroup> compatibleGroupTypes(long directoryId, InternalGroup parentGroup) {
        return childGroup -> {
            if (this.isChildGroupTypeEqualToParentType((InternalGroup)childGroup, parentGroup)) {
                return true;
            }
            log.info("Incompatible membership detected in directory [{}] for group member [{}] of parent group [{}]", new Object[]{directoryId, childGroup.getName(), parentGroup.getName()});
            return false;
        };
    }

    private boolean isChildGroupTypeEqualToParentType(InternalGroup childGroup, InternalGroup parentGroup) {
        return childGroup.getType().equals((Object)parentGroup.getType());
    }

    private Predicate<InternalGroup> nonExistingMemberships(long directoryId, InternalGroup parentGroup, Collection<HibernateMembership> existingMemberships) {
        Set lowerCaseGroupMembers = existingMemberships.stream().map(HibernateMembership::getGroupMember).map(InternalGroup::getLowerName).collect(Collectors.toSet());
        return childGroup -> {
            if (!lowerCaseGroupMembers.contains(childGroup.getLowerName())) {
                return true;
            }
            log.info("Duplicate membership detected in directory [{}] for group member [{}] of parent group [{}]", new Object[]{directoryId, childGroup.getName(), parentGroup.getName()});
            return false;
        };
    }

    private <T extends DirectoryEntity> Collection<String> nonExistingEntities(Collection<String> entityNames, Collection<T> foundEntities) {
        Set foundEntityNames = foundEntities.stream().map(entity -> IdentifierUtils.toLowerCase((String)entity.getName())).collect(Collectors.toSet());
        return entityNames.stream().filter(entityName -> !foundEntityNames.contains(IdentifierUtils.toLowerCase((String)entityName))).collect(Collectors.toList());
    }

    private BatchResult<String> addAllMemberships(Set<HibernateMembership> membershipsToAdd, Collection<String> nonExistingGroups, Collection<String> invalidMemberships) {
        BatchResult results = BatchResult.transform(this.addAll(membershipsToAdd), membership -> membership.getGroupMember().getName());
        results.addFailures(nonExistingGroups);
        results.addFailures(invalidMemberships);
        return results;
    }

    private BatchResult<HibernateMembership> addAll(Collection<HibernateMembership> memberships) {
        return this.batchProcessor.execute((HibernateOperation)new SaveOrUpdateOperation(), memberships);
    }

    public void removeUserFromGroup(long directoryId, String userName, String groupName) throws UserNotFoundException, GroupNotFoundException, MembershipNotFoundException {
        List<HibernateMembership> internalMemberships = this.internalFindAllUserMemberships(directoryId, userName, groupName);
        if (internalMemberships.isEmpty()) {
            throw new MembershipNotFoundException(userName, groupName);
        }
        this.hibernateTemplate.deleteAll(internalMemberships);
    }

    public BatchResult<String> removeUsersFromGroup(long directoryId, Collection<String> userNames, String parentGroupName) throws GroupNotFoundException {
        InternalGroup parentGroup = this.groupDao.internalFindByName(directoryId, parentGroupName);
        List<HibernateMembership> existingMemberships = this.fetchExistingUserMemberships(directoryId, userNames, parentGroup.getName());
        BatchResult result = BatchResult.transform(this.removeAll(existingMemberships), membership -> membership.getUserMember().getName());
        result.addFailures((Collection)this.findFailedEntities(userNames, result.getSuccessfulEntities()).stream().filter(failure -> !result.getFailedEntities().contains(failure)).collect(Collectors.toSet()));
        return result;
    }

    private List<HibernateMembership> fetchExistingUserMemberships(long directoryId, Collection<String> childGroupNames, String parentGroupName) {
        return (List)this.hibernateTemplate.execute(session -> {
            ArrayList<String> childGroupNamesAsList = new ArrayList<String>(IdentifierUtils.toLowerCase((Collection)childGroupNames));
            int inExpressionLimit = this.hibernateSchemaInformationService.getDialect().getInExpressionCountLimit();
            if (this.isPartitioningNeeded(inExpressionLimit, childGroupNamesAsList.size())) {
                return Lists.partition(childGroupNamesAsList, (int)(inExpressionLimit - 100)).stream().map(partitionedGroupNames -> this.executeUserGroupMembershipQuery(directoryId, (List<String>)partitionedGroupNames, parentGroupName, session)).flatMap(Collection::stream).collect(Collectors.toList());
            }
            return this.executeUserGroupMembershipQuery(directoryId, childGroupNamesAsList, parentGroupName, session);
        });
    }

    private List<HibernateMembership> executeUserGroupMembershipQuery(long directoryId, List<String> childGroupNames, String parentGroupName, Session session) {
        InExpressionBuilder inExpressionBuilder = InExpressionBuilder.getInExpressionBuilderDefaultLimit((String)"userMember.lowerName", (String)CHILD_NAMES, childGroupNames, (Dialect)this.hibernateSchemaInformationService.getDialect());
        String queryString = "from HibernateMembership mem join fetch mem.userMember userMember where mem.parentGroup.directory.id = :directoryId and mem.parentGroup.lowerName = :parentName and " + inExpressionBuilder.buildInExpressionString();
        Query queryObject = session.createQuery(queryString, HibernateMembership.class).setParameter(DIRECTORY_ID, (Object)directoryId).setParameter(PARENT_NAME, (Object)IdentifierUtils.toLowerCase((String)parentGroupName));
        inExpressionBuilder.substituteInExpressionParameters(queryObject);
        return queryObject.list();
    }

    public void removeGroupFromGroup(long directoryId, String childGroupName, String parentGroupName) throws GroupNotFoundException, MembershipNotFoundException {
        List<HibernateMembership> internalMemberships = this.internalFindAllGroupMemberships(directoryId, childGroupName, parentGroupName);
        if (internalMemberships.isEmpty()) {
            throw new MembershipNotFoundException(childGroupName, parentGroupName);
        }
        this.hibernateTemplate.deleteAll(internalMemberships);
    }

    public BatchResult<String> removeGroupsFromGroup(long directoryId, Collection<String> childGroupNames, String parentGroupName) throws GroupNotFoundException {
        InternalGroup parentGroup = this.groupDao.internalFindByName(directoryId, parentGroupName);
        List<HibernateMembership> existingMemberships = this.fetchExistingGroupMemberships(directoryId, childGroupNames, parentGroup.getName());
        BatchResult result = BatchResult.transform(this.removeAll(existingMemberships), membership -> membership.getGroupMember().getName());
        result.addFailures((Collection)this.findFailedEntities(childGroupNames, result.getSuccessfulEntities()).stream().filter(failure -> !result.getFailedEntities().contains(failure)).collect(Collectors.toSet()));
        return result;
    }

    private List<HibernateMembership> fetchExistingGroupMemberships(long directoryId, Collection<String> childGroupNames, String parentGroupName) {
        return (List)this.hibernateTemplate.execute(session -> {
            ArrayList<String> childGroupNamesAsList = new ArrayList<String>(IdentifierUtils.toLowerCase((Collection)childGroupNames));
            int inExpressionLimit = this.hibernateSchemaInformationService.getDialect().getInExpressionCountLimit();
            if (this.isPartitioningNeeded(inExpressionLimit, childGroupNamesAsList.size())) {
                return Lists.partition(childGroupNamesAsList, (int)(inExpressionLimit - 100)).stream().map(partitionedGroupNames -> this.executeGroupMembershipQuery(directoryId, (List<String>)partitionedGroupNames, parentGroupName, session)).flatMap(Collection::stream).collect(Collectors.toList());
            }
            return this.executeGroupMembershipQuery(directoryId, childGroupNamesAsList, parentGroupName, session);
        });
    }

    private List<HibernateMembership> executeGroupMembershipQuery(long directoryId, List<String> childGroupNames, String parentGroupName, Session session) {
        InExpressionBuilder inExpressionBuilder = InExpressionBuilder.getInExpressionBuilderDefaultLimit((String)"groupMember.lowerName", (String)CHILD_NAMES, childGroupNames, (Dialect)this.hibernateSchemaInformationService.getDialect());
        String queryString = "from HibernateMembership mem join fetch mem.parentGroup parentGroup join fetch mem.groupMember groupMember where mem.parentGroup.directory.id = :directoryId and mem.parentGroup.lowerName = :parentName and " + inExpressionBuilder.buildInExpressionString();
        Query queryObject = session.createQuery(queryString, HibernateMembership.class).setParameter(DIRECTORY_ID, (Object)directoryId).setParameter(PARENT_NAME, (Object)IdentifierUtils.toLowerCase((String)parentGroupName));
        inExpressionBuilder.substituteInExpressionParameters(queryObject);
        return queryObject.list();
    }

    private BatchResult<HibernateMembership> removeAll(Collection<HibernateMembership> memberships) {
        return this.batchProcessor.execute((HibernateOperation)new RemoveOperation(), memberships);
    }

    private Set<String> findFailedEntities(Collection<String> entities, Collection<String> successfulEntities) {
        return entities.stream().filter(IdentifierUtils.containsIdentifierPredicate(successfulEntities).negate()).collect(Collectors.toSet());
    }

    public BoundedCount countDirectMembersOfGroup(long directoryId, String groupName, int potentialMaxCount) {
        long count = DataAccessUtils.longResult((Collection)((Collection)this.hibernateTemplate.execute(session -> session.getNamedQuery("countMembersOfGroup").setParameter(DIRECTORY_ID, (Object)directoryId).setParameter("groupName", (Object)IdentifierUtils.toLowerCase((String)groupName)).list())));
        return BoundedCount.exactly((long)count);
    }

    public <T> List<T> search(long directoryId, MembershipQuery<T> query) {
        return (List)this.hibernateTemplate.executeWithNativeSession(HibernateSearch.forMemberships(directoryId, query)::doInHibernate);
    }

    public <T> ListMultimap<String, T> searchGroupedByName(long directoryId, MembershipQuery<T> membershipQuery) {
        return (ListMultimap)this.hibernateTemplate.executeWithNativeSession(GroupingHibernateSearch.forMembershipsGroupedByName(directoryId, membershipQuery)::doInHibernate);
    }

    private HibernateMembership internalFindUserMembership(long directoryId, String childName, String parentName) {
        List<HibernateMembership> results = this.internalFindAllUserMemberships(directoryId, childName, parentName);
        if (results.isEmpty()) {
            return null;
        }
        if (results.size() > 1 && log.isWarnEnabled()) {
            log.info("Duplicate membership detected in directory [{}] for user member [{}] of group [{}]", new Object[]{directoryId, childName, parentName});
        }
        return results.get(0);
    }

    private List<HibernateMembership> internalFindAllUserMemberships(long directoryId, String childName, String parentName) {
        return (List)this.hibernateTemplate.execute(session -> session.createQuery("from HibernateMembership mem where mem.parentGroup.directory.id = :directoryId and mem.parentGroup.lowerName = :parentName and mem.userMember.lowerName = :childName").setParameter(DIRECTORY_ID, (Object)directoryId).setParameter(PARENT_NAME, (Object)IdentifierUtils.toLowerCase((String)parentName)).setParameter(CHILD_NAME, (Object)IdentifierUtils.toLowerCase((String)childName)).list());
    }

    private HibernateMembership internalFindGroupMembership(long directoryId, String childName, String parentName) {
        List<HibernateMembership> results = this.internalFindAllGroupMemberships(directoryId, childName, parentName);
        if (results.isEmpty()) {
            return null;
        }
        if (results.size() > 1 && log.isWarnEnabled()) {
            log.info("Duplicate membership detected in directory [{}] for group member [{}] of parent group [{}]", new Object[]{directoryId, childName, parentName});
        }
        return results.get(0);
    }

    private List<HibernateMembership> internalFindAllGroupMemberships(long directoryId, String childName, String parentName) {
        return (List)this.hibernateTemplate.execute(session -> session.createQuery("from HibernateMembership mem where mem.parentGroup.directory.id = :directoryId and mem.parentGroup.lowerName = :parentName and mem.groupMember.lowerName = :childName").setParameter(DIRECTORY_ID, (Object)directoryId).setParameter(PARENT_NAME, (Object)IdentifierUtils.toLowerCase((String)parentName)).setParameter(CHILD_NAME, (Object)IdentifierUtils.toLowerCase((String)childName)).list());
    }

    private static class PartitioningByResult<T> {
        private final List<T> success;
        private final List<T> failures;

        private PartitioningByResult(Map<Boolean, List<T>> partitioningByResult) {
            this.success = ImmutableList.copyOf((Collection)partitioningByResult.get(true));
            this.failures = ImmutableList.copyOf((Collection)partitioningByResult.get(false));
        }

        private static <T> PartitioningByResult<T> partitionBy(Collection<T> toPartition, Predicate<T> predicate) {
            return new PartitioningByResult<T>(toPartition.stream().collect(Collectors.partitioningBy(predicate)));
        }
    }
}

