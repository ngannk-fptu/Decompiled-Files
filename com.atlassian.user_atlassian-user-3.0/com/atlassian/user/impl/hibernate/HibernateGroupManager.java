/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.hibernate.HibernateException
 *  net.sf.hibernate.ObjectDeletedException
 *  net.sf.hibernate.Query
 *  net.sf.hibernate.Session
 *  net.sf.hibernate.SessionFactory
 *  org.springframework.dao.DataAccessException
 *  org.springframework.orm.hibernate.HibernateCallback
 *  org.springframework.orm.hibernate.HibernateTemplate
 *  org.springframework.orm.hibernate.SessionFactoryUtils
 *  org.springframework.orm.hibernate.support.HibernateDaoSupport
 */
package com.atlassian.user.impl.hibernate;

import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.ExternalEntity;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.impl.DuplicateEntityException;
import com.atlassian.user.impl.RepositoryException;
import com.atlassian.user.impl.hibernate.DefaultHibernateGroup;
import com.atlassian.user.impl.hibernate.DefaultHibernateUser;
import com.atlassian.user.impl.hibernate.ExternalEntityDAO;
import com.atlassian.user.impl.hibernate.repository.HibernateRepository;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.page.DefaultPager;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerFactory;
import com.atlassian.user.util.Assert;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.ObjectDeletedException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.HibernateTemplate;
import org.springframework.orm.hibernate.SessionFactoryUtils;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class HibernateGroupManager
extends HibernateDaoSupport
implements GroupManager {
    public static final String GROUPNAME_FIELD = "groupname";
    public static final String GROUPID_FIELD = "groupid";
    public static final String ENTITYID_FIELD = "entityid";
    public static final String EXTERNAL_ENTITY_NAME_FIELD = "externalEntityName";
    private final RepositoryIdentifier identifier;
    protected final HibernateRepository repository;
    protected final UserManager userManager;
    protected final ExternalEntityDAO externalEntityDao;
    private static final boolean USE_EXPERIMENTAL_MAPPINGS = Boolean.getBoolean("com.atlassian.user.experimentalMapping");

    public HibernateGroupManager(RepositoryIdentifier identifier, HibernateRepository repository, UserManager userManager, ExternalEntityDAO externalEntityDao) {
        this.identifier = identifier;
        this.repository = repository;
        this.userManager = userManager;
        this.setSessionFactory(repository.getSessionFactory());
        this.externalEntityDao = externalEntityDao;
    }

    @Override
    public Pager<Group> getGroups() throws EntityException {
        List<Group> result;
        try {
            result = this.getGroupsFromHibernate();
        }
        catch (DataAccessException e) {
            throw new RepositoryException(e);
        }
        if (result == null) {
            return DefaultPager.emptyPager();
        }
        return new DefaultPager<Group>(result);
    }

    @Override
    public List<Group> getWritableGroups() {
        return this.getGroupsFromHibernate();
    }

    @Override
    public Pager<Group> getGroups(User user) throws EntityException {
        Collection<Group> groups = this.getAllGroupsForUser(user);
        return new DefaultPager<Group>(groups);
    }

    protected Collection<Group> getAllGroupsForUser(User user) throws RepositoryException {
        Assert.notNull(user, "User must not be null");
        if (this.isUserExternal(user)) {
            return this.getGroupsForExternalEntity(this.getCorrespondingExternalEntity(user));
        }
        return this.getGroupsForLocalUser(user);
    }

    protected boolean isUserExternal(User user) {
        return !(user instanceof DefaultHibernateUser);
    }

    private List<Group> getGroupsForLocalUser(User user) throws RepositoryException {
        Assert.notNull(user, "User must not be null");
        Assert.isInstanceOf(DefaultHibernateUser.class, user);
        if (this.isUserExternal(user)) {
            return Collections.emptyList();
        }
        return this.getLocalUserGroupsFromHibernate((DefaultHibernateUser)user);
    }

    private List<Group> getGroupsForExternalEntity(ExternalEntity externalEntity) throws RepositoryException {
        if (externalEntity == null) {
            throw new IllegalArgumentException("Input (externalEntity) is null.");
        }
        return this.getExternalUserGroupsFromHibernate(externalEntity);
    }

    @Override
    public Pager<String> getMemberNames(Group group) throws EntityException {
        if (group == null) {
            throw new IllegalArgumentException("Group cannot be null.");
        }
        if (!this.isHandledGroup(group)) {
            throw new IllegalArgumentException("Group passed to HibernateGroupManager must be of type 'DefaultHibernateGroup'");
        }
        return PagerFactory.getPager(this.getExternalMemberNames(group), this.getLocalMemberNames(group));
    }

    protected void validateGroup(Group group) {
        if (group == null) {
            throw new IllegalArgumentException("Input (group) is null.");
        }
    }

    @Override
    public Pager<String> getLocalMemberNames(Group group) throws EntityException {
        this.validateGroup(group);
        return new DefaultPager<String>(this.getLocalMemberNamesFromHibernate((DefaultHibernateGroup)group));
    }

    public Pager<User> getLocalMembers(Group group) throws RepositoryException {
        if (group == null) {
            throw new IllegalArgumentException("Input (group) is null.");
        }
        if (!this.isHandledGroup(group)) {
            return DefaultPager.emptyPager();
        }
        DefaultHibernateGroup defGroup = (DefaultHibernateGroup)group;
        return new DefaultPager<User>(new ArrayList<User>(defGroup.getLocalMembers()));
    }

    private boolean isHandledGroup(Group group) {
        return group instanceof DefaultHibernateGroup;
    }

    @Override
    public Pager<String> getExternalMemberNames(Group group) throws EntityException {
        if (group == null) {
            throw new IllegalArgumentException("Input (group) is null.");
        }
        if (!this.isHandledGroup(group)) {
            return DefaultPager.emptyPager();
        }
        DefaultHibernateGroup defGroup = (DefaultHibernateGroup)group;
        return new DefaultPager<String>(this.getExternalMemberNamesFromHibernate(defGroup));
    }

    public DefaultHibernateGroup getGroup(Group group) throws EntityException {
        if (group instanceof DefaultHibernateGroup) {
            try {
                return (DefaultHibernateGroup)this.getSession().get(DefaultHibernateGroup.class, (Serializable)Long.valueOf(((DefaultHibernateGroup)group).getId()));
            }
            catch (ObjectDeletedException e) {
                return null;
            }
            catch (HibernateException e) {
                throw new EntityException(e);
            }
        }
        return this.getGroup(group.getName());
    }

    @Override
    public DefaultHibernateGroup getGroup(final String groupname) throws EntityException {
        List result;
        if (groupname == null) {
            throw new IllegalArgumentException("Input (groupname) is null.");
        }
        DefaultHibernateGroup foundGroup = null;
        try {
            result = this.getHibernateTemplate().executeFind(new HibernateCallback(){

                public Object doInHibernate(Session session) throws HibernateException {
                    Query queryObject = session.getNamedQuery("atluser.group_find");
                    SessionFactoryUtils.applyTransactionTimeout((Query)queryObject, (SessionFactory)HibernateGroupManager.this.getSessionFactory());
                    if (groupname != null) {
                        queryObject.setParameter(HibernateGroupManager.GROUPNAME_FIELD, (Object)groupname);
                    }
                    return queryObject.list();
                }
            });
        }
        catch (DataAccessException e) {
            throw new RepositoryException(e);
        }
        try {
            foundGroup = (DefaultHibernateGroup)result.get(0);
        }
        catch (Exception e) {
            return foundGroup;
        }
        return foundGroup;
    }

    @Override
    public Group createGroup(String groupname) throws EntityException {
        if (groupname == null) {
            throw new IllegalArgumentException("Input (groupname) is null.");
        }
        DefaultHibernateGroup group = this.getGroup(groupname);
        if (group != null) {
            throw new DuplicateEntityException("Group [" + groupname + "] already exists in this repository (" + this.identifier.getName() + ")");
        }
        group = new DefaultHibernateGroup(groupname);
        this.getHibernateTemplate().save((Object)group);
        return group;
    }

    @Override
    public void removeGroup(Group group) throws EntityException {
        DefaultHibernateGroup groupInSession;
        DefaultHibernateGroup dGroup = groupInSession = this.getGroupInSession(group);
        dGroup.setExternalMembers(null);
        dGroup.setLocalMembers(null);
        this.getHibernateTemplate().delete((Object)groupInSession);
    }

    @Override
    public void addMembership(Group group, User user) throws EntityException {
        this.validateGroupAndUser(group, user);
        DefaultHibernateGroup dGroup = this.getGroupInSession(group);
        if (this.isUserExternal(user)) {
            this.addExternalUserMembership(user, dGroup);
        } else {
            this.addLocalUserMembership(user, dGroup);
        }
        this.getHibernateTemplate().saveOrUpdate((Object)dGroup);
    }

    private void addLocalUserMembership(User user, DefaultHibernateGroup dGroup) {
        if (USE_EXPERIMENTAL_MAPPINGS) {
            DefaultHibernateUser huser = (DefaultHibernateUser)user;
            huser.getGroups().add(dGroup);
            this.getHibernateTemplate().saveOrUpdate((Object)user);
        } else {
            if (dGroup.getLocalMembers() == null) {
                dGroup.setLocalMembers(new HashSet<User>());
            }
            dGroup.getLocalMembers().add(user);
        }
    }

    private void addExternalUserMembership(User user, DefaultHibernateGroup dGroup) throws RepositoryException {
        if (dGroup.getExternalMembers() == null) {
            dGroup.setExternalMembers(new HashSet<ExternalEntity>());
        }
        dGroup.getExternalMembers().add(this.getCorrespondingExternalEntity(user));
    }

    protected ExternalEntity getCorrespondingExternalEntity(User user) throws RepositoryException {
        if (user == null) {
            throw new IllegalArgumentException("Input (user) is null.");
        }
        ExternalEntity result = this.externalEntityDao.getExternalEntity(user.getName());
        if (result == null) {
            return this.externalEntityDao.createExternalEntity(user.getName());
        }
        return result;
    }

    @Override
    public boolean hasMembership(Group group, User user) throws EntityException {
        if (group == null || this.getGroup(group) == null) {
            return false;
        }
        this.validateGroupAndUser(group, user);
        DefaultHibernateGroup defGroup = this.getGroupInSession(group);
        if (this.isUserExternal(user)) {
            return this.hasExternalMembership(defGroup, user);
        }
        return this.hasLocalMembership(defGroup, (DefaultHibernateUser)user);
    }

    protected void validateGroupAndUser(Group group, User user) throws EntityException {
        if (group == null) {
            throw new IllegalArgumentException("Can't add membership for null group");
        }
        if (this.getGroup(group) == null) {
            throw new IllegalArgumentException("Group unknown: [" + group + "] in [" + this.identifier.getKey() + "]");
        }
        if (user == null) {
            throw new IllegalArgumentException("User unknown: [" + user + "] in [" + this.identifier.getKey() + "]");
        }
        if (!this.isHandledGroup(group)) {
            throw new IllegalArgumentException("Group is not a Hibernate entity [" + group.getClass().getName());
        }
    }

    protected boolean hasExternalMembership(final DefaultHibernateGroup defGroup, final User user) throws EntityException {
        try {
            return (Boolean)this.getHibernateTemplate().execute(new HibernateCallback(){

                public Object doInHibernate(Session session) throws HibernateException {
                    Query queryObject = session.getNamedQuery("atluser.externalEntity_hasMembership");
                    SessionFactoryUtils.applyTransactionTimeout((Query)queryObject, (SessionFactory)HibernateGroupManager.this.getSessionFactory());
                    queryObject.setLong(HibernateGroupManager.GROUPID_FIELD, defGroup.getId());
                    queryObject.setString(HibernateGroupManager.EXTERNAL_ENTITY_NAME_FIELD, user.getName());
                    Integer count = (Integer)queryObject.uniqueResult();
                    return count != null && count > 0;
                }
            });
        }
        catch (DataAccessException e) {
            throw new RepositoryException(e);
        }
    }

    protected boolean hasLocalMembership(DefaultHibernateGroup defGroup, DefaultHibernateUser defUser) throws EntityException {
        Collection<Group> usersGroups = this.getAllGroupsForUser(defUser);
        return usersGroups != null && usersGroups.contains(defGroup);
    }

    @Override
    public void removeMembership(Group group, User user) throws EntityException {
        DefaultHibernateGroup groupInSession;
        this.validateGroupAndUser(group, user);
        DefaultHibernateGroup hibernateGroup = groupInSession = this.getGroupInSession(group);
        HibernateTemplate hibernateTemplate = this.getHibernateTemplate();
        if (this.isUserExternal(user)) {
            ExternalEntity extUser = this.getCorrespondingExternalEntity(user);
            hibernateGroup.getExternalMembers().remove(extUser);
        } else if (USE_EXPERIMENTAL_MAPPINGS) {
            DefaultHibernateUser huser = (DefaultHibernateUser)user;
            huser.getGroups().remove(groupInSession);
            hibernateTemplate.saveOrUpdate((Object)huser);
        } else {
            hibernateGroup.getLocalMembers().remove(user);
            hibernateTemplate.saveOrUpdate((Object)groupInSession);
        }
        hibernateTemplate.flush();
    }

    @Override
    public boolean isReadOnly(Group group) throws EntityException {
        return this.getGroup(group) == null;
    }

    @Override
    public boolean supportsExternalMembership() throws EntityException {
        return true;
    }

    @Override
    public RepositoryIdentifier getIdentifier() {
        return this.identifier;
    }

    @Override
    public RepositoryIdentifier getRepository(Entity entity) throws EntityException {
        if (this.getGroup(entity.getName()) != null) {
            return this.identifier;
        }
        return null;
    }

    @Override
    public boolean isCreative() {
        return true;
    }

    private DefaultHibernateGroup getGroupInSession(Group group) throws EntityException {
        if (group == null) {
            throw new IllegalArgumentException("Input (group) is null.");
        }
        if (!this.isHandledGroup(group)) {
            throw new IllegalArgumentException("Group is not a Hibernate entity [" + group.getClass().getName());
        }
        return this.getGroup(group);
    }

    private List<Group> getGroupsFromHibernate() {
        return this.getHibernateTemplate().executeFind(new HibernateCallback(){

            public Object doInHibernate(Session session) throws HibernateException {
                Query queryObject = session.getNamedQuery("atluser.group_findAll");
                SessionFactoryUtils.applyTransactionTimeout((Query)queryObject, (SessionFactory)HibernateGroupManager.this.getSessionFactory());
                return queryObject.list();
            }
        });
    }

    private List<Group> getLocalUserGroupsFromHibernate(final DefaultHibernateUser defUser) throws RepositoryException {
        try {
            return this.getHibernateTemplate().executeFind(new HibernateCallback(){

                public Object doInHibernate(Session session) throws HibernateException {
                    Query queryObject = session.getNamedQuery("atluser.group_getGroupsForUser");
                    SessionFactoryUtils.applyTransactionTimeout((Query)queryObject, (SessionFactory)HibernateGroupManager.this.getSessionFactory());
                    queryObject.setLong(HibernateGroupManager.ENTITYID_FIELD, defUser.getId());
                    return queryObject.list();
                }
            });
        }
        catch (DataAccessException e) {
            throw new RepositoryException(e);
        }
    }

    private List<Group> getExternalUserGroupsFromHibernate(final ExternalEntity externalEntity) throws RepositoryException {
        try {
            return this.getHibernateTemplate().executeFind(new HibernateCallback(){

                public Object doInHibernate(Session session) throws HibernateException {
                    Query queryObject = session.getNamedQuery("atluser.group_getGroupsForExternalEntity");
                    SessionFactoryUtils.applyTransactionTimeout((Query)queryObject, (SessionFactory)HibernateGroupManager.this.getSessionFactory());
                    if (externalEntity != null) {
                        queryObject.setLong(HibernateGroupManager.ENTITYID_FIELD, externalEntity.getId());
                    }
                    return queryObject.list();
                }
            });
        }
        catch (DataAccessException e) {
            throw new RepositoryException(e);
        }
    }

    private List<String> getLocalMemberNamesFromHibernate(final DefaultHibernateGroup defGroup) throws RepositoryException {
        try {
            return this.getHibernateTemplate().executeFind(new HibernateCallback(){

                public Object doInHibernate(Session session) throws HibernateException {
                    Query queryObject = session.getNamedQuery("atluser.group_getLocalMemberNames");
                    SessionFactoryUtils.applyTransactionTimeout((Query)queryObject, (SessionFactory)HibernateGroupManager.this.getSessionFactory());
                    if (defGroup != null) {
                        queryObject.setLong(HibernateGroupManager.GROUPID_FIELD, defGroup.getId());
                    }
                    return queryObject.list();
                }
            });
        }
        catch (DataAccessException e) {
            throw new RepositoryException(e);
        }
    }

    private List<String> getExternalMemberNamesFromHibernate(final DefaultHibernateGroup defGroup) throws RepositoryException {
        List result;
        try {
            result = this.getHibernateTemplate().executeFind(new HibernateCallback(){

                public Object doInHibernate(Session session) throws HibernateException {
                    Query queryObject = session.getNamedQuery("atluser.group_getExternalMemberNames");
                    SessionFactoryUtils.applyTransactionTimeout((Query)queryObject, (SessionFactory)HibernateGroupManager.this.getSessionFactory());
                    if (defGroup != null) {
                        queryObject.setLong(HibernateGroupManager.GROUPID_FIELD, defGroup.getId());
                    }
                    return queryObject.list();
                }
            });
        }
        catch (DataAccessException e) {
            throw new RepositoryException(e);
        }
        return result;
    }
}

