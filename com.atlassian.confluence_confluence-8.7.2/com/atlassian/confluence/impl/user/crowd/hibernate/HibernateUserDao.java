/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.atlassianuser.EmbeddedCrowdUser
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.UserAlreadyExistsException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.user.InternalUser
 *  com.atlassian.crowd.model.user.InternalUserAttribute
 *  com.atlassian.crowd.model.user.InternalUserWithAttributes
 *  com.atlassian.crowd.model.user.TimestampedUser
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.util.BatchResult
 *  com.atlassian.crowd.util.persistence.hibernate.batch.BatchFinder
 *  com.atlassian.crowd.util.persistence.hibernate.batch.BatchProcessor
 *  com.atlassian.crowd.util.persistence.hibernate.batch.HibernateOperation
 *  com.atlassian.crowd.util.persistence.hibernate.batch.TransactionGroup
 *  com.atlassian.crowd.util.persistence.hibernate.batch.TransactionGroup$Builder
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  javax.persistence.PersistenceException
 *  javax.persistence.criteria.CriteriaBuilder
 *  javax.persistence.criteria.CriteriaQuery
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.criteria.Root
 *  javax.persistence.criteria.Selection
 *  org.hibernate.Hibernate
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.hibernate.type.LongType
 *  org.hibernate.type.StringType
 *  org.hibernate.type.Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.impl.user.crowd.hibernate;

import com.atlassian.confluence.core.BatchOperationManager;
import com.atlassian.confluence.core.persistence.hibernate.SessionHelper;
import com.atlassian.confluence.impl.user.crowd.hibernate.HibernateSearch;
import com.atlassian.confluence.impl.user.crowd.hibernate.InternalMembershipDao;
import com.atlassian.confluence.impl.user.crowd.hibernate.InternalUserDao;
import com.atlassian.confluence.impl.user.crowd.hibernate.batch.operation.RemoveUserOperation;
import com.atlassian.confluence.impl.user.crowd.hibernate.batch.operation.SaveOrUpdateOperation;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.atlassianuser.EmbeddedCrowdUser;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.UserAlreadyExistsException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.user.InternalUser;
import com.atlassian.crowd.model.user.InternalUserAttribute;
import com.atlassian.crowd.model.user.InternalUserWithAttributes;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.util.BatchResult;
import com.atlassian.crowd.util.persistence.hibernate.batch.BatchFinder;
import com.atlassian.crowd.util.persistence.hibernate.batch.BatchProcessor;
import com.atlassian.crowd.util.persistence.hibernate.batch.HibernateOperation;
import com.atlassian.crowd.util.persistence.hibernate.batch.TransactionGroup;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public final class HibernateUserDao
implements InternalUserDao<InternalUser> {
    private static final Logger log = LoggerFactory.getLogger(HibernateUserDao.class);
    private static final String ATTRIBUTE_NAME = "DISABLED_BY_LDAP_SYNC";
    private final Supplier<DirectoryDao> directoryDao;
    private final InternalMembershipDao membershipDao;
    private final ConfluenceUserDao confluenceUserDao;
    private final BatchProcessor<Session> batchProcessor;
    private final BatchFinder batchFinder;
    private final BatchOperationManager batchOperationManager;
    private final HibernateTemplate hibernateTemplate;

    public HibernateUserDao(SessionFactory sessionFactory, Supplier<DirectoryDao> directoryDao, InternalMembershipDao membershipDao, ConfluenceUserDao confluenceUserDao, BatchProcessor<Session> batchProcessor, BatchFinder batchFinder, BatchOperationManager batchOperationManager) {
        this.directoryDao = directoryDao;
        this.membershipDao = membershipDao;
        this.confluenceUserDao = confluenceUserDao;
        this.batchProcessor = batchProcessor;
        this.batchFinder = batchFinder;
        this.batchOperationManager = batchOperationManager;
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public void remove(com.atlassian.crowd.model.user.User user) throws UserNotFoundException {
        InternalUser internalUser = this.internalFindByUser(user);
        this.membershipDao.removeAllUserRelationships(internalUser);
        this.hibernateTemplate.executeWithNativeSession(session -> {
            SessionHelper.delete(session, "from InternalUserAttribute a where a.user.id = :userId", new Object[]{internalUser.getId()}, new Type[]{LongType.INSTANCE});
            session.delete((Object)internalUser);
            return null;
        });
        log.debug("Removed the user {}.", (Object)user.getName());
    }

    public void removeAttribute(com.atlassian.crowd.model.user.User user, String attributeName) throws UserNotFoundException {
        InternalUser internalUser = this.internalFindByUser(user);
        this.removeAttribute(internalUser, attributeName);
    }

    private void removeAttribute(InternalUser internalUser, String attributeName) {
        internalUser.getAttributes().removeIf(attribute -> attributeName.equals(attribute.getName()));
        this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from InternalUserAttribute a where a.user.id = :userId and a.name = :attributeName", new Object[]{internalUser.getId(), attributeName}, new Type[]{LongType.INSTANCE, StringType.INSTANCE}));
    }

    public com.atlassian.crowd.model.user.User rename(com.atlassian.crowd.model.user.User user, String newName) throws UserNotFoundException, UserAlreadyExistsException {
        String oldName = user.getName();
        if (newName == null) {
            throw new IllegalArgumentException("New username cannot be null");
        }
        if (!IdentifierUtils.equalsInLowerCase((String)user.getName(), (String)newName) && this.internalFindUser(user.getDirectoryId(), newName).isPresent()) {
            throw new UserAlreadyExistsException(user.getDirectoryId(), newName);
        }
        InternalUser internalUser = this.internalFindByUser(user);
        internalUser.renameTo(newName);
        internalUser.setUpdatedDateToNow();
        this.hibernateTemplate.execute(session -> {
            session.update((Object)internalUser);
            return null;
        });
        this.membershipDao.rename(oldName, internalUser);
        return internalUser;
    }

    @Override
    public InternalUser internalFindByUser(com.atlassian.crowd.model.user.User user) throws UserNotFoundException {
        return this.internalFindByName(user.getDirectoryId(), user.getName());
    }

    public BatchResult<String> removeAllUsers(long directoryId, Set<String> userNames) {
        List<String> usersHavingLocalGroup = this.getUserHavingLocalGroup(directoryId);
        return this.batchProcessor.execute((HibernateOperation)new RemoveUserOperation(directoryId, usersHavingLocalGroup), userNames);
    }

    private List<String> getUserHavingLocalGroup(long directoryId) {
        return (List)this.hibernateTemplate.executeWithNativeSession(session -> {
            String hql = "select user.name from HibernateMembership as hm join hm.userMember as user join hm.parentGroup as pGroup  where user.directory.id = :directoryId  and user.active = 'T'  and pGroup.active = 'T'  and pGroup.local = 'T' ";
            Query query = SessionHelper.createQuery(session, "select user.name from HibernateMembership as hm join hm.userMember as user join hm.parentGroup as pGroup  where user.directory.id = :directoryId  and user.active = 'T'  and pGroup.active = 'T'  and pGroup.local = 'T' ", new Object[]{directoryId}, new Type[]{LongType.INSTANCE});
            return query.list();
        });
    }

    @Override
    public void removeAllUsers(long directoryId) {
        this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from InternalUserAttribute userAttribute where userAttribute.directory.id = :directoryId", new Object[]{directoryId}, new Type[]{LongType.INSTANCE}));
        this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from InternalUser internalUser where internalUser.directory.id = :directoryId", new Object[]{directoryId}, new Type[]{LongType.INSTANCE}));
        this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from InternalUserCredentialRecord credentialRecord where credentialRecord.user.directory.id = :directoryId", new Object[]{directoryId}, new Type[]{LongType.INSTANCE}));
    }

    @Override
    public Collection<InternalUser> findByNames(long directoryID, Collection<String> usernames) {
        return this.batchFinder.find(directoryID, usernames, InternalUser.class);
    }

    public void updateCredential(com.atlassian.crowd.model.user.User user, PasswordCredential credential, int maxCredentialHistory) throws UserNotFoundException {
        InternalUser internalUser = this.internalFindByUser(user);
        internalUser.updateCredentialTo(credential, maxCredentialHistory);
        this.hibernateTemplate.execute(session -> session.save((Object)internalUser));
    }

    public <T> List<T> search(long directoryId, EntityQuery<T> query) {
        if (query.getEntityDescriptor().getEntityType() != Entity.USER) {
            throw new IllegalArgumentException("UserDAO can only evaluate EntityQueries for Entity.USER");
        }
        return (List)this.hibernateTemplate.executeWithNativeSession(HibernateSearch.forEntities(directoryId, query)::doInHibernate);
    }

    public BatchResult<com.atlassian.crowd.model.user.User> addAll(Set<UserTemplateWithCredentialAndAttributes> users) {
        HashSet<TransactionGroup> usersToAdd = new HashSet<TransactionGroup>();
        HashSet<UserTemplateWithCredentialAndAttributes> usersFailedValidation = new HashSet<UserTemplateWithCredentialAndAttributes>();
        for (UserTemplateWithCredentialAndAttributes userTemplateWithCredentialAndAttributes : users) {
            try {
                Directory directory = this.directoryDao.get().findById(userTemplateWithCredentialAndAttributes.getDirectoryId());
                Hibernate.initialize((Object)directory);
                InternalUser userToAdd = new InternalUser((com.atlassian.crowd.model.user.User)userTemplateWithCredentialAndAttributes, directory, userTemplateWithCredentialAndAttributes.getCredential());
                userToAdd.setCreatedDateToNow();
                userToAdd.setUpdatedDateToNow();
                HashSet<Object> dependantObjects = new HashSet<Object>();
                for (Map.Entry entry : userTemplateWithCredentialAndAttributes.getAttributes().entrySet()) {
                    for (String value : (Set)entry.getValue()) {
                        dependantObjects.add(new InternalUserAttribute(userToAdd, (String)entry.getKey(), value));
                    }
                }
                if (this.confluenceUserDao.findByUsername(userTemplateWithCredentialAndAttributes.getName()) == null) {
                    dependantObjects.add(new ConfluenceUserImpl((User)new EmbeddedCrowdUser((com.atlassian.crowd.embedded.api.User)userTemplateWithCredentialAndAttributes)));
                }
                usersToAdd.add(new TransactionGroup.Builder((Serializable)userToAdd).withDependantObjects(dependantObjects).build());
            }
            catch (PersistenceException e) {
                log.error("Could not add user [ {} ]", (Object)userTemplateWithCredentialAndAttributes.getName(), (Object)e);
            }
            catch (DirectoryNotFoundException | IllegalArgumentException e) {
                log.error("Could not add user [ {} ]: {}", (Object)userTemplateWithCredentialAndAttributes.getName(), (Object)e.getMessage());
                usersFailedValidation.add(userTemplateWithCredentialAndAttributes);
            }
        }
        BatchResult daoResult = this.batchProcessor.execute((HibernateOperation)new SaveOrUpdateOperation(), usersToAdd);
        if (daoResult.getFailedEntities().size() > 0) {
            log.error("The following users could not be processed:");
            for (TransactionGroup entity : daoResult.getFailedEntities()) {
                Serializable primary = entity.getPrimaryObject();
                if (!(primary instanceof InternalUser)) continue;
                log.error(((InternalUser)primary).getName());
            }
            log.error("Please try to resolve any errors with these users, and try again.");
        }
        BatchResult batchResult = new BatchResult(users.size());
        for (TransactionGroup transactionGroup : daoResult.getSuccessfulEntities()) {
            batchResult.addSuccess((Object)((com.atlassian.crowd.model.user.User)transactionGroup.getPrimaryObject()));
        }
        for (TransactionGroup transactionGroup : daoResult.getFailedEntities()) {
            batchResult.addFailure((Object)((com.atlassian.crowd.model.user.User)transactionGroup.getPrimaryObject()));
        }
        batchResult.addFailures(usersFailedValidation);
        return batchResult;
    }

    public com.atlassian.crowd.model.user.User update(com.atlassian.crowd.model.user.User user) throws UserNotFoundException {
        InternalUser existingUser = this.internalFindByUser(user);
        Optional<InternalUserAttribute> attribute = existingUser.getAttributes().stream().filter(attr -> ATTRIBUTE_NAME.equals(attr.getName())).findAny();
        UserTemplate userTemplate = new UserTemplate(user);
        if (attribute.isPresent()) {
            userTemplate.setActive(true);
            existingUser.getAttributes().remove(attribute.get());
        }
        existingUser.updateDetailsFrom((com.atlassian.crowd.model.user.User)userTemplate);
        existingUser.setUpdatedDateToNow();
        this.hibernateTemplate.execute(session -> {
            session.update((Object)existingUser);
            attribute.ifPresent(arg_0 -> ((Session)session).delete(arg_0));
            return null;
        });
        return existingUser;
    }

    public void storeAttributes(com.atlassian.crowd.model.user.User user, Map<String, Set<String>> attributes, boolean updateTimestamp) throws UserNotFoundException {
        InternalUser internalUser = this.internalFindByUser(user);
        List<InternalUserAttribute> attributesList = this.getUserAttributes(internalUser);
        HashMap<String, Set> existingAttributesMap = new HashMap<String, Set>();
        for (InternalUserAttribute internalUserAttribute : attributesList) {
            Set existingVals = existingAttributesMap.computeIfAbsent(internalUserAttribute.getName(), k -> new HashSet());
            existingVals.add(internalUserAttribute);
        }
        for (Map.Entry entry : attributes.entrySet()) {
            if (!existingAttributesMap.containsKey(entry.getKey())) {
                for (String newVal : (Set)entry.getValue()) {
                    internalUser.getAttributes().add(this.addAttribute(internalUser, (String)entry.getKey(), newVal));
                }
                continue;
            }
            Set newVals = (Set)entry.getValue();
            Set existingVals = (Set)existingAttributesMap.get(entry.getKey());
            ArrayList<String> valsToAdd = new ArrayList<String>();
            HashSet<InternalUserAttribute> valsToRemove = new HashSet<InternalUserAttribute>(existingVals);
            this.resolveNoOps(newVals, existingVals, valsToRemove, valsToAdd);
            this.hibernateTemplate.execute(session -> {
                for (InternalUserAttribute existingVal : valsToRemove) {
                    if (valsToAdd.isEmpty()) {
                        internalUser.getAttributes().remove(existingVal);
                        session.delete((Object)existingVal);
                        continue;
                    }
                    String newVal = (String)valsToAdd.remove(0);
                    existingVal.setValue(newVal);
                    session.saveOrUpdate((Object)existingVal);
                }
                return null;
            });
            for (String newVal : valsToAdd) {
                internalUser.getAttributes().add(this.addAttribute(internalUser, (String)entry.getKey(), newVal));
            }
        }
    }

    private void resolveNoOps(Set<String> newVals, Set<InternalUserAttribute> existingVals, Set<InternalUserAttribute> valsToRemove, List<String> valsToAdd) {
        for (String newVal : newVals) {
            boolean valueAlreadyExists = false;
            for (InternalUserAttribute existingVal : existingVals) {
                if (!newVal.equals(existingVal.getValue())) continue;
                valsToRemove.remove(existingVal);
                valueAlreadyExists = true;
                break;
            }
            if (valueAlreadyExists) continue;
            valsToAdd.add(newVal);
        }
    }

    private InternalUserAttribute addAttribute(InternalUser user, String attributeName, String attributeValue) {
        InternalUserAttribute attribute = new InternalUserAttribute(user, attributeName, attributeValue);
        this.hibernateTemplate.execute(session -> session.save((Object)attribute));
        return attribute;
    }

    @Override
    public InternalUser add(com.atlassian.crowd.model.user.User user, PasswordCredential credential) throws UserAlreadyExistsException, IllegalArgumentException, DirectoryNotFoundException {
        if (this.internalFindUser(user.getDirectoryId(), user.getName()).isPresent()) {
            throw new UserAlreadyExistsException(user.getDirectoryId(), user.getName());
        }
        Directory directory = this.directoryDao.get().findById(user.getDirectoryId());
        InternalUser internalUser = new InternalUser(user, directory, credential);
        internalUser.setCreatedDateToNow();
        internalUser.setUpdatedDateToNow();
        this.hibernateTemplate.execute(session -> session.save((Object)internalUser));
        if (this.confluenceUserDao.findByUsername(user.getName()) == null) {
            log.debug("Creating ConfluenceUser for user {}.", (Object)user.getName());
            this.confluenceUserDao.create(new ConfluenceUserImpl((User)new EmbeddedCrowdUser((com.atlassian.crowd.embedded.api.User)user)));
        } else {
            log.debug("ConfluenceUser already existed for the user {}.", (Object)user.getName());
        }
        return internalUser;
    }

    public List<PasswordCredential> getCredentialHistory(long directoryId, String userName) throws UserNotFoundException {
        InternalUser user = this.internalFindByName(directoryId, userName);
        return user.getCredentialHistory();
    }

    public PasswordCredential getCredential(long directoryId, String userName) throws UserNotFoundException {
        InternalUser user = this.internalFindByName(directoryId, userName);
        return user.getCredential();
    }

    public UserWithAttributes findByNameWithAttributes(long directoryId, String userName) throws UserNotFoundException {
        InternalUser user = this.internalFindByName(directoryId, userName);
        return this.populateAttributes(user);
    }

    private UserWithAttributes populateAttributes(InternalUser user) {
        Set attributes = user.getAttributes();
        return new InternalUserWithAttributes(user, this.convertToAttributesMap(attributes));
    }

    private Map<String, Set<String>> convertToAttributesMap(Iterable<InternalUserAttribute> attributesList) {
        HashMap<String, Set<String>> attributesMap = new HashMap<String, Set<String>>();
        for (InternalUserAttribute attribute : attributesList) {
            if (!attributesMap.containsKey(attribute.getName())) {
                attributesMap.put(attribute.getName(), new HashSet());
            }
            ((Set)attributesMap.get(attribute.getName())).add(attribute.getValue());
        }
        return attributesMap;
    }

    private List<InternalUserAttribute> getUserAttributes(InternalUser user) {
        return this.find(InternalUserAttribute.class, Query::list, (builder, query) -> {
            Root attr = query.from(InternalUserAttribute.class);
            query.where((Expression)builder.equal((Expression)attr.get("user").get("id"), (Object)user.getId()));
        });
    }

    public TimestampedUser findByName(long directoryId, String userName) throws UserNotFoundException {
        return this.internalFindByName(directoryId, userName);
    }

    public TimestampedUser findByExternalId(long directoryId, String externalId) throws UserNotFoundException {
        return this.internalFindByExternalId(directoryId, externalId);
    }

    @Override
    public InternalUser internalFindByName(long directoryId, String userName) throws UserNotFoundException {
        return this.internalFindUser(directoryId, userName).orElseThrow(() -> new UserNotFoundException(userName));
    }

    public void setAttributeForAllInDirectory(long directoryId, String attrName, String attrValue) {
        List<InternalUser> usersToUpdate = this.internalFindAllUsers(directoryId);
        this.batchOperationManager.applyInBatches(usersToUpdate, usersToUpdate.size(), user -> {
            this.hibernateTemplate.executeWithNativeSession(session -> {
                List attrsToUpdate = session.createQuery("from InternalUserAttribute attr where attr.user = :user and attr.name = :attrName").setParameter("user", user).setParameter("attrName", (Object)attrName).list();
                if (attrsToUpdate.isEmpty()) {
                    this.addAttribute((InternalUser)user, attrName, attrValue);
                } else {
                    for (InternalUserAttribute attr : attrsToUpdate) {
                        if (attrValue.equals(attr.getValue())) continue;
                        attr.setValue(attrValue);
                        session.save((Object)attr);
                    }
                }
                return null;
            });
            return null;
        });
    }

    public Set<String> getAllExternalIds(long directoryId) {
        List<InternalUser> internalUsers = this.internalFindAllUsersWithExternalId(directoryId);
        return ImmutableSet.copyOf((Collection)Lists.transform(internalUsers, InternalUser::getExternalId));
    }

    public long getUserCount(long directoryId) {
        return DataAccessUtils.longResult((Collection)((Collection)this.hibernateTemplate.execute(session -> session.createQuery("select count(*) from InternalUser user where user.directory.id = :directoryId").setParameter("directoryId", (Object)directoryId).list())));
    }

    public Set<Long> findDirectoryIdsContainingUserName(String username) {
        return (Set)this.hibernateTemplate.execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(Long.class);
            Root user = query.from(InternalUser.class);
            query.select((Selection)user.get("directory").get("id")).where((Expression)builder.equal((Expression)user.get("lowerName"), (Object)IdentifierUtils.toLowerCase((String)username)));
            return ImmutableSet.copyOf((Collection)session.createQuery(query).list());
        });
    }

    public Map<String, String> findByExternalIds(long directoryId, Set<String> externalIds) {
        return (Map)this.hibernateTemplate.execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery query = builder.createTupleQuery();
            Root user = query.from(InternalUser.class);
            query.multiselect(new Selection[]{user.get("externalId").alias("externalId"), user.get("name").alias("name")});
            query.where((Expression)builder.and((Expression)user.get("externalId").in((Collection)externalIds), (Expression)builder.equal((Expression)user.get("directory").get("id"), (Object)directoryId)));
            try (Stream stream = session.createQuery(query).stream();){
                Map<String, String> map = stream.collect(Collectors.toMap(t -> (String)t.get("externalId", String.class), t -> (String)t.get("name", String.class)));
                return map;
            }
        });
    }

    private InternalUser internalFindByExternalId(long directoryId, String externalId) throws UserNotFoundException {
        return this.internalFindUserByExternalId(directoryId, externalId).orElseThrow(() -> new UserNotFoundException(externalId));
    }

    private List<InternalUser> internalFindAllUsers(long directoryId) {
        return this.findMultipleUsers((builder, query) -> {
            Root internalUser = query.from(InternalUser.class);
            query.where((Expression)builder.equal((Expression)internalUser.get("directory").get("id"), (Object)directoryId));
        });
    }

    private List<InternalUser> internalFindAllUsersWithExternalId(long directoryId) {
        return this.findMultipleUsers((builder, query) -> {
            Root internalUser = query.from(InternalUser.class);
            query.where(new Predicate[]{builder.equal((Expression)internalUser.get("directory").get("id"), (Object)directoryId), builder.isNotNull((Expression)internalUser.get("externalId")), builder.notEqual((Expression)internalUser.get("externalId"), (Object)"")});
        });
    }

    private Optional<InternalUser> internalFindUser(long directoryId, String userName) {
        return this.findSingleUser((builder, query) -> {
            Root internalUser = query.from(InternalUser.class);
            query.where(new Predicate[]{builder.equal((Expression)internalUser.get("directory").get("id"), (Object)directoryId), builder.equal((Expression)internalUser.get("lowerName"), (Object)IdentifierUtils.toLowerCase((String)userName))});
        });
    }

    private Optional<InternalUser> internalFindUserByExternalId(long directoryId, String externalId) {
        return this.findSingleUser((builder, query) -> {
            Root internalUser = query.from(InternalUser.class);
            query.where(new Predicate[]{builder.equal((Expression)internalUser.get("directory").get("id"), (Object)directoryId), builder.equal((Expression)internalUser.get("externalId"), (Object)externalId)});
        });
    }

    private Optional<InternalUser> findSingleUser(BiConsumer<CriteriaBuilder, CriteriaQuery<InternalUser>> queryPopulator) {
        return this.find(InternalUser.class, Query::uniqueResultOptional, queryPopulator);
    }

    private List<InternalUser> findMultipleUsers(BiConsumer<CriteriaBuilder, CriteriaQuery<InternalUser>> queryPopulator) {
        return this.find(InternalUser.class, Query::list, queryPopulator);
    }

    private <T, O> O find(Class<T> returnType, Function<Query<T>, O> resultExtractor, BiConsumer<CriteriaBuilder, CriteriaQuery<T>> queryPopulator) {
        return (O)this.hibernateTemplate.execute(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(returnType);
            queryPopulator.accept(builder, query);
            return resultExtractor.apply(session.createQuery(query));
        });
    }
}

