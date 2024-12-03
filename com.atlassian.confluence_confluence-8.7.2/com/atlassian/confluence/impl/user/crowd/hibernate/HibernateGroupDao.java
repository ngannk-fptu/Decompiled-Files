/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 *  com.atlassian.crowd.model.group.InternalGroup
 *  com.atlassian.crowd.model.group.InternalGroupAttribute
 *  com.atlassian.crowd.model.group.InternalGroupWithAttributes
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.util.BatchResult
 *  com.atlassian.crowd.util.persistence.hibernate.batch.BatchFinder
 *  com.atlassian.crowd.util.persistence.hibernate.batch.BatchProcessor
 *  com.atlassian.crowd.util.persistence.hibernate.batch.HibernateOperation
 *  javax.persistence.criteria.CriteriaBuilder
 *  javax.persistence.criteria.CriteriaQuery
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.criteria.Root
 *  javax.persistence.criteria.Selection
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.type.LongType
 *  org.hibernate.type.StringType
 *  org.hibernate.type.Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.impl.user.crowd.hibernate;

import com.atlassian.confluence.core.persistence.hibernate.SessionHelper;
import com.atlassian.confluence.impl.user.crowd.hibernate.HibernateSearch;
import com.atlassian.confluence.impl.user.crowd.hibernate.InternalGroupDao;
import com.atlassian.confluence.impl.user.crowd.hibernate.InternalMembershipDao;
import com.atlassian.confluence.impl.user.crowd.hibernate.batch.operation.SaveOrUpdateOperation;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import com.atlassian.crowd.model.group.InternalGroup;
import com.atlassian.crowd.model.group.InternalGroupAttribute;
import com.atlassian.crowd.model.group.InternalGroupWithAttributes;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.util.BatchResult;
import com.atlassian.crowd.util.persistence.hibernate.batch.BatchFinder;
import com.atlassian.crowd.util.persistence.hibernate.batch.BatchProcessor;
import com.atlassian.crowd.util.persistence.hibernate.batch.HibernateOperation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class HibernateGroupDao
implements InternalGroupDao<InternalGroup> {
    private static final Logger log = LoggerFactory.getLogger(HibernateGroupDao.class);
    private final HibernateTemplate hibernateTemplate;
    private final Supplier<DirectoryDao> directoryDao;
    private final InternalMembershipDao membershipDao;
    private final BatchProcessor<Session> batchProcessor;
    private final BatchFinder batchFinder;

    public HibernateGroupDao(SessionFactory sessionFactory, Supplier<DirectoryDao> directoryDao, InternalMembershipDao membershipDao, BatchProcessor<Session> batchProcessor, BatchFinder batchFinder) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
        this.directoryDao = directoryDao;
        this.membershipDao = membershipDao;
        this.batchProcessor = batchProcessor;
        this.batchFinder = batchFinder;
    }

    public InternalDirectoryGroup findByName(long directoryId, String name) throws GroupNotFoundException {
        return this.internalFindByName(directoryId, name);
    }

    public BatchResult<String> removeAllGroups(long directoryId, Set<String> groupNames) {
        Collection internalGroups = this.batchFinder.find(directoryId, groupNames, InternalGroup.class);
        HashSet<String> groupsToBeRemoved = new HashSet<String>(groupNames);
        BatchResult result = new BatchResult(internalGroups.size());
        for (InternalGroup internalGroup : internalGroups) {
            try {
                this.remove((Group)internalGroup);
                groupsToBeRemoved.remove(internalGroup.getName());
                result.addSuccess((Object)internalGroup.getName());
            }
            catch (GroupNotFoundException groupNotFoundException) {}
        }
        result.addFailures(groupsToBeRemoved);
        return result;
    }

    public Set<String> getAllExternalIds(long directoryId) throws DirectoryNotFoundException {
        Directory directory = this.directoryDao.get().findById(directoryId);
        List result = (List)this.hibernateTemplate.execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(String.class);
            Root root = query.from(InternalGroup.class);
            query.select((Selection)root.get("externalId")).where((Expression)builder.and(new Predicate[]{builder.equal((Expression)root.get("directory"), (Object)directory), builder.isFalse((Expression)root.get("local")), builder.isNotNull((Expression)root.get("externalId"))}));
            return session.createQuery(query).getResultList();
        });
        if (result == null || result.isEmpty()) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(new HashSet(result));
    }

    public long getGroupCount(long directoryId) throws DirectoryNotFoundException {
        Directory directory = this.directoryDao.get().findById(directoryId);
        return DataAccessUtils.longResult((Collection)((Collection)this.hibernateTemplate.execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(Long.class);
            Root root = query.from(InternalGroup.class);
            query.select((Selection)builder.count((Expression)root)).where((Expression)builder.equal((Expression)root.get("directory"), (Object)directory));
            return session.createQuery(query).getResultList();
        })));
    }

    public Set<String> getLocalGroupNames(long directoryId) throws DirectoryNotFoundException {
        Directory directory = this.directoryDao.get().findById(directoryId);
        List result = (List)this.hibernateTemplate.execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(String.class);
            Root root = query.from(InternalGroup.class);
            query.select((Selection)root.get("name")).where((Expression)builder.and((Expression)builder.equal((Expression)root.get("directory"), (Object)directory), (Expression)builder.isTrue((Expression)root.get("local"))));
            return session.createQuery(query).getResultList();
        });
        if (result == null || result.isEmpty()) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(new HashSet(result));
    }

    public Map<String, String> findByExternalIds(long directoryId, Set<String> externalIds) {
        return (Map)this.hibernateTemplate.execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery query = builder.createTupleQuery();
            Root group = query.from(InternalGroup.class);
            query.multiselect(new Selection[]{group.get("externalId").alias("externalId"), group.get("name").alias("name")});
            query.where((Expression)builder.and((Expression)builder.equal((Expression)group.get("directory").get("id"), (Object)directoryId), (Expression)group.get("externalId").in((Collection)externalIds)));
            try (Stream stream = session.createQuery(query).stream();){
                Map<String, String> map = stream.collect(Collectors.toMap(t -> (String)t.get("externalId", String.class), t -> (String)t.get("name", String.class)));
                return map;
            }
        });
    }

    public Map<String, String> findExternalIdsByNames(long directoryId, Set<String> groupNames) {
        return (Map)this.hibernateTemplate.execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery query = builder.createTupleQuery();
            Root group = query.from(InternalGroup.class);
            query.multiselect(new Selection[]{group.get("externalId").alias("externalId"), group.get("name").alias("name")});
            query.where((Expression)builder.and((Expression)builder.equal((Expression)group.get("directory").get("id"), (Object)directoryId), (Expression)group.get("name").in((Collection)groupNames)));
            try (Stream stream = session.createQuery(query).stream();){
                Map<String, String> map = stream.collect(Collectors.toMap(t -> (String)t.get("name", String.class), t -> (String)t.get("externalId", String.class)));
                return map;
            }
        });
    }

    public long getExternalGroupCount(long directoryId) throws DirectoryNotFoundException {
        Directory directory = this.directoryDao.get().findById(directoryId);
        return DataAccessUtils.longResult((Collection)((Collection)this.hibernateTemplate.execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(Long.class);
            Root group = query.from(InternalGroup.class);
            query.select((Selection)builder.count((Expression)group));
            query.where((Expression)builder.and((Expression)builder.equal((Expression)group.get("directory"), (Object)directory), (Expression)builder.isFalse((Expression)group.get("local"))));
            return session.createQuery(query).getResultList();
        })));
    }

    public GroupWithAttributes findByNameWithAttributes(long directoryId, String name) throws GroupNotFoundException {
        InternalGroup group = this.internalFindByName(directoryId, name);
        return this.populateAttributes(group);
    }

    private Map<String, Set<String>> convertToAttributesMap(Iterable<InternalGroupAttribute> attributesList) {
        HashMap<String, Set<String>> attributesMap = new HashMap<String, Set<String>>();
        for (InternalGroupAttribute internalGroupAttribute : attributesList) {
            if (!attributesMap.containsKey(internalGroupAttribute.getName())) {
                attributesMap.put(internalGroupAttribute.getName(), new HashSet());
            }
            ((Set)attributesMap.get(internalGroupAttribute.getName())).add(internalGroupAttribute.getValue());
        }
        return attributesMap;
    }

    private InternalGroup add(Group group, boolean local) throws DirectoryNotFoundException {
        InternalGroup groupToSave = new InternalGroup(group, this.directoryDao.get().findById(group.getDirectoryId()));
        groupToSave.setLocal(local);
        groupToSave.setCreatedDateToNow();
        groupToSave.setUpdatedDateToNow();
        this.hibernateTemplate.save((Object)groupToSave);
        return groupToSave;
    }

    public BatchResult<Group> addAll(Set<? extends Group> groups) throws DirectoryNotFoundException {
        HashSet<InternalGroup> groupsToAdd = new HashSet<InternalGroup>();
        HashSet<Group> groupsFailedValidation = new HashSet<Group>();
        for (Group group : groups) {
            try {
                InternalGroup groupToAdd = new InternalGroup(group, this.directoryDao.get().findById(group.getDirectoryId()));
                groupToAdd.setCreatedDateToNow();
                groupToAdd.setUpdatedDateToNow();
                groupsToAdd.add(groupToAdd);
            }
            catch (IllegalArgumentException e) {
                log.error("Could not add group <" + group.getName() + ">: ", (Throwable)e);
                groupsFailedValidation.add(group);
            }
        }
        BatchResult daoResult = this.batchProcessor.execute((HibernateOperation)new SaveOrUpdateOperation(), groupsToAdd);
        if (!daoResult.getFailedEntities().isEmpty()) {
            log.info("The following groups could not be processed:");
            for (InternalGroup entity : daoResult.getFailedEntities()) {
                log.info(entity.getName());
            }
            log.info("Please try to resolve any errors with these groups, and try again.");
        }
        BatchResult batchResult = new BatchResult(groups.size());
        batchResult.addSuccesses((Collection)daoResult.getSuccessfulEntities());
        batchResult.addFailures((Collection)daoResult.getFailedEntities());
        batchResult.addFailures(groupsFailedValidation);
        return batchResult;
    }

    @Override
    public InternalGroup add(Group group) throws DirectoryNotFoundException {
        return this.add(group, false);
    }

    @Override
    public InternalGroup addLocal(Group group) throws DirectoryNotFoundException {
        return this.add(group, true);
    }

    public Group update(Group group) throws GroupNotFoundException {
        InternalGroup groupToUpdate = this.internalFindByGroup(group);
        groupToUpdate.updateDetailsFrom(group);
        groupToUpdate.setUpdatedDateToNow();
        this.hibernateTemplate.update((Object)groupToUpdate);
        return groupToUpdate;
    }

    public Group rename(Group group, String newName) throws GroupNotFoundException {
        InternalGroup groupToRename = this.internalFindByGroup(group);
        groupToRename.renameTo(newName);
        groupToRename.setUpdatedDateToNow();
        this.hibernateTemplate.update((Object)groupToRename);
        return groupToRename;
    }

    @Override
    public InternalGroup internalFindByGroup(Group group) throws GroupNotFoundException {
        return this.internalFindByName(group.getDirectoryId(), group.getName());
    }

    @Override
    public void removeAllGroups(long directoryId) {
        this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from InternalGroupAttribute groupAttribute where groupAttribute.directory.id = :directoryId", new Object[]{directoryId}, new Type[]{LongType.INSTANCE}));
        this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from InternalGroup internalGroup where internalGroup.directory.id = :directoryId", new Object[]{directoryId}, new Type[]{LongType.INSTANCE}));
    }

    public void storeAttributes(Group group, Map<String, Set<String>> attributes) throws GroupNotFoundException {
        InternalGroup internalGroup = this.internalFindByGroup(group);
        List<InternalGroupAttribute> attributesList = this.findGroupAttributes(internalGroup.getId());
        HashMap<String, Set> existingAttributesMap = new HashMap<String, Set>();
        for (InternalGroupAttribute internalGroupAttribute : attributesList) {
            Set existingVals = existingAttributesMap.computeIfAbsent(internalGroupAttribute.getName(), k -> new HashSet());
            existingVals.add(internalGroupAttribute);
        }
        for (Map.Entry entry : attributes.entrySet()) {
            if (!existingAttributesMap.containsKey(entry.getKey())) {
                for (String newVal : (Set)entry.getValue()) {
                    internalGroup.getAttributes().add(this.addAttribute(internalGroup, (String)entry.getKey(), newVal));
                }
                continue;
            }
            Set newVals = (Set)entry.getValue();
            Set existingVals = (Set)existingAttributesMap.get(entry.getKey());
            ArrayList<String> valsToAdd = new ArrayList<String>();
            HashSet<InternalGroupAttribute> valsToRemove = new HashSet<InternalGroupAttribute>(existingVals);
            this.resolveNoOps(newVals, existingVals, valsToAdd, valsToRemove);
            for (InternalGroupAttribute existingVal : valsToRemove) {
                if (valsToAdd.isEmpty()) {
                    internalGroup.getAttributes().remove(existingVal);
                    this.hibernateTemplate.delete((Object)existingVal);
                    continue;
                }
                String newVal = (String)valsToAdd.remove(0);
                existingVal.setValue(newVal);
                this.hibernateTemplate.saveOrUpdate((Object)existingVal);
            }
            for (String newVal : valsToAdd) {
                internalGroup.getAttributes().add(this.addAttribute(internalGroup, (String)entry.getKey(), newVal));
            }
        }
    }

    private void resolveNoOps(Set<String> newVals, Set<InternalGroupAttribute> existingVals, List<String> valsToAdd, Set<InternalGroupAttribute> valsToRemove) {
        for (String newVal : newVals) {
            boolean valueAlreadyExists = false;
            for (InternalGroupAttribute existingVal : existingVals) {
                if (!newVal.equals(existingVal.getValue())) continue;
                valsToRemove.remove(existingVal);
                valueAlreadyExists = true;
                break;
            }
            if (valueAlreadyExists) continue;
            valsToAdd.add(newVal);
        }
    }

    private InternalGroupAttribute addAttribute(InternalGroup group, String attributeName, String attributeValue) {
        InternalGroupAttribute attribute = new InternalGroupAttribute(group, attributeName, attributeValue);
        this.hibernateTemplate.save((Object)attribute);
        return attribute;
    }

    private InternalGroupWithAttributes populateAttributes(InternalGroup group) {
        Set attributes = group.getAttributes();
        return new InternalGroupWithAttributes(group, this.convertToAttributesMap(attributes));
    }

    public void removeAttribute(Group group, String attributeName) throws GroupNotFoundException {
        InternalGroup internalGroup = this.internalFindByGroup(group);
        internalGroup.getAttributes().removeIf(attribute -> attributeName.equals(attribute.getName()));
        this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from InternalGroupAttribute a where a.group.id = :groupId and a.name = :attributeName", new Object[]{internalGroup.getId(), attributeName}, new Type[]{LongType.INSTANCE, StringType.INSTANCE}));
    }

    private void removeAllAttributes(InternalGroup internalGroup) {
        this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from InternalGroupAttribute a where a.group.id = :groupId", new Object[]{internalGroup.getId()}, new Type[]{LongType.INSTANCE}));
    }

    public void remove(Group group) throws GroupNotFoundException {
        InternalGroup groupToRemove = (InternalGroup)this.findByName(group.getDirectoryId(), group.getName());
        this.membershipDao.removeAllGroupRelationships(groupToRemove);
        this.removeAllAttributes(groupToRemove);
        this.hibernateTemplate.delete((Object)groupToRemove);
    }

    public <T> List<T> search(long directoryId, EntityQuery<T> query) {
        if (query.getEntityDescriptor().getEntityType() != Entity.GROUP) {
            throw new IllegalArgumentException("GroupDAO can only evaluate EntityQueries for Entity.GROUP");
        }
        return (List)this.hibernateTemplate.executeWithNativeSession(HibernateSearch.forEntities(directoryId, query)::doInHibernate);
    }

    private List<InternalGroupAttribute> findGroupAttributes(long groupId) {
        return (List)this.hibernateTemplate.execute(session -> session.createQuery(HibernateGroupDao.createFindAttributeByIdQuery(session, groupId)).list());
    }

    private static CriteriaQuery<InternalGroupAttribute> createFindAttributeByIdQuery(Session session, long groupId) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery(InternalGroupAttribute.class);
        Root attr = query.from(InternalGroupAttribute.class);
        return query.where((Expression)builder.equal((Expression)attr.get("group").get("id"), (Object)groupId));
    }

    @Override
    public InternalGroup internalFindByName(long directoryId, String name) throws GroupNotFoundException {
        InternalGroup result = (InternalGroup)this.hibernateTemplate.execute(session -> (InternalGroup)session.createQuery(HibernateGroupDao.createFindGroupByNameQuery(session, directoryId, name)).uniqueResult());
        if (result == null) {
            throw new GroupNotFoundException(name);
        }
        return result;
    }

    private static CriteriaQuery<InternalGroup> createFindGroupByNameQuery(Session session, long directoryId, String name) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery(InternalGroup.class);
        Root group = query.from(InternalGroup.class);
        return query.where((Expression)builder.and((Expression)builder.equal((Expression)group.get("lowerName"), (Object)IdentifierUtils.toLowerCase((String)name)), (Expression)builder.equal((Expression)group.get("directory").get("id"), (Object)directoryId)));
    }
}

