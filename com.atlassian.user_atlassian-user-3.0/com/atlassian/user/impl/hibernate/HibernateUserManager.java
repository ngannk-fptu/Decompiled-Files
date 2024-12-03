/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.hibernate.HibernateException
 *  net.sf.hibernate.Query
 *  net.sf.hibernate.Session
 *  net.sf.hibernate.SessionFactory
 *  org.springframework.dao.DataAccessException
 *  org.springframework.orm.hibernate.HibernateCallback
 *  org.springframework.orm.hibernate.SessionFactoryUtils
 *  org.springframework.orm.hibernate.support.HibernateDaoSupport
 */
package com.atlassian.user.impl.hibernate;

import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.impl.DuplicateEntityException;
import com.atlassian.user.impl.RepositoryException;
import com.atlassian.user.impl.hibernate.DefaultHibernateGroup;
import com.atlassian.user.impl.hibernate.DefaultHibernateUser;
import com.atlassian.user.impl.hibernate.repository.HibernateRepository;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.page.DefaultPager;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.security.password.Credential;
import com.atlassian.user.security.password.PasswordEncryptor;
import com.atlassian.user.util.Assert;
import java.util.List;
import java.util.Set;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.SessionFactoryUtils;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class HibernateUserManager
extends HibernateDaoSupport
implements UserManager {
    private static final String USERNAME_FIELD = "username";
    public static final String ENTITYID_FIELD = "entityid";
    private final RepositoryIdentifier identifier;
    private final PasswordEncryptor passwordEncryptor;

    public HibernateUserManager(RepositoryIdentifier identifier, HibernateRepository repository, PasswordEncryptor passwordEncryptor) {
        this.identifier = identifier;
        this.passwordEncryptor = passwordEncryptor;
        this.setSessionFactory(repository.getSessionFactory());
    }

    @Override
    public Pager<User> getUsers() throws EntityException {
        try {
            return new DefaultPager<User>(this.getUsersFromHibernate());
        }
        catch (DataAccessException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Pager<String> getUserNames() throws EntityException {
        try {
            return new DefaultPager<String>(this.getUsernamesFromHibernate());
        }
        catch (DataAccessException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public User getUser(String username) throws EntityException {
        return this.internalGetUser(username);
    }

    @Override
    public User createUser(String username) throws EntityException {
        this.validateNewUserName(username);
        DefaultHibernateUser user = new DefaultHibernateUser(username);
        this.getHibernateTemplate().save((Object)user);
        return user;
    }

    @Override
    public User createUser(User userTemplate, Credential credential) throws EntityException {
        this.validateNewUserName(userTemplate.getName());
        DefaultHibernateUser user = new DefaultHibernateUser(userTemplate.getName());
        user.setFullName(userTemplate.getFullName());
        user.setEmail(userTemplate.getEmail());
        user.setPassword(this.passwordEncryptor.getEncryptedValue(credential));
        this.getHibernateTemplate().save((Object)user);
        return user;
    }

    private void validateNewUserName(String name) throws EntityException {
        if (name == null) {
            throw new IllegalArgumentException("Username cannot be null.");
        }
        User existingUser = this.getUser(name);
        if (existingUser != null) {
            throw new DuplicateEntityException("User with name [" + name + "] already exists in this repository (" + this.identifier.getName() + ")");
        }
    }

    @Override
    public void alterPassword(User user, String password) throws EntityException {
        DefaultHibernateUser foundUser = this.internalGetUser(user.getName());
        if (foundUser == null) {
            throw new EntityException("This repository [" + this.identifier.getName() + "] does not handle user [" + user.getName() + "]");
        }
        String encryptedPassword = this.passwordEncryptor.encrypt(password);
        foundUser.setPassword(encryptedPassword);
        this.getHibernateTemplate().saveOrUpdate((Object)foundUser);
    }

    @Override
    public void saveUser(User user) throws EntityException {
        Assert.notNull(user, "User must not be null");
        DefaultHibernateUser persistedUser = this.internalGetUser(user.getName());
        if (persistedUser == null) {
            throw new EntityException("This repository [" + this.identifier + "] does not handle user [" + user.getName() + "]");
        }
        persistedUser.setFullName(user.getFullName());
        persistedUser.setEmail(user.getEmail());
        this.getHibernateTemplate().saveOrUpdate((Object)persistedUser);
    }

    @Override
    public void removeUser(User user) throws EntityException {
        DefaultHibernateUser foundUser = this.internalGetUser(user.getName());
        if (foundUser == null) {
            throw new IllegalArgumentException("User can not be found in this user manager: [" + user + "]");
        }
        List<DefaultHibernateGroup> groups = this.getGroupsForLocalUser(foundUser);
        if (groups != null) {
            foundUser.setGroups(null);
            for (DefaultHibernateGroup group : groups) {
                Set<User> members = group.getLocalMembers();
                if (members != null) {
                    members.remove(foundUser);
                }
                this.getHibernateTemplate().saveOrUpdate((Object)group);
            }
        }
        this.getHibernateTemplate().delete((Object)foundUser);
    }

    private DefaultHibernateUser internalGetUser(final String username) throws RepositoryException {
        List result;
        Assert.notNull(username, "User must not be null");
        try {
            result = this.getHibernateTemplate().executeFind(new HibernateCallback(){

                public Object doInHibernate(Session session) throws HibernateException {
                    Query query = session.getNamedQuery("atluser.user_find");
                    SessionFactoryUtils.applyTransactionTimeout((Query)query, (SessionFactory)HibernateUserManager.this.getSessionFactory());
                    query.setCacheable(true);
                    query.setParameter(HibernateUserManager.USERNAME_FIELD, (Object)username);
                    return query.list();
                }
            });
        }
        catch (DataAccessException e) {
            throw new RepositoryException(e);
        }
        return result.isEmpty() ? null : (DefaultHibernateUser)result.get(0);
    }

    @Override
    public boolean isReadOnly(User user) throws EntityException {
        return false;
    }

    public PasswordEncryptor getPasswordEncryptor(User user) throws EntityException {
        return this.passwordEncryptor;
    }

    @Override
    public RepositoryIdentifier getIdentifier() {
        return this.identifier;
    }

    @Override
    public RepositoryIdentifier getRepository(Entity entity) throws EntityException {
        return this.getUser(entity.getName()) == null ? null : this.identifier;
    }

    @Override
    public boolean isCreative() {
        return true;
    }

    private List<User> getUsersFromHibernate() {
        List result = this.getHibernateTemplate().executeFind(new HibernateCallback(){

            public Object doInHibernate(Session session) throws HibernateException {
                Query queryObject = session.getNamedQuery("atluser.user_findAll");
                SessionFactoryUtils.applyTransactionTimeout((Query)queryObject, (SessionFactory)HibernateUserManager.this.getSessionFactory());
                return queryObject.list();
            }
        });
        return result;
    }

    private List<String> getUsernamesFromHibernate() {
        List result = this.getHibernateTemplate().executeFind(new HibernateCallback(){

            public Object doInHibernate(Session session) throws HibernateException {
                Query queryObject = session.getNamedQuery("atluser.user_findAllUserNames");
                SessionFactoryUtils.applyTransactionTimeout((Query)queryObject, (SessionFactory)HibernateUserManager.this.getSessionFactory());
                return queryObject.list();
            }
        });
        return result;
    }

    private List<DefaultHibernateGroup> getGroupsForLocalUser(final DefaultHibernateUser user) throws RepositoryException {
        Assert.notNull(user, "User must not be null");
        try {
            return this.getHibernateTemplate().executeFind(new HibernateCallback(){

                public Object doInHibernate(Session session) throws HibernateException {
                    Query queryObject = session.getNamedQuery("atluser.group_getGroupsForUser");
                    SessionFactoryUtils.applyTransactionTimeout((Query)queryObject, (SessionFactory)HibernateUserManager.this.getSessionFactory());
                    queryObject.setLong(HibernateUserManager.ENTITYID_FIELD, user.getId());
                    queryObject.setCacheable(true);
                    return queryObject.list();
                }
            });
        }
        catch (DataAccessException e) {
            throw new RepositoryException(e);
        }
    }
}

