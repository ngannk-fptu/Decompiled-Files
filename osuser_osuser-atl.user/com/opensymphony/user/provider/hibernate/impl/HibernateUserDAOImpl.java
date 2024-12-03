/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.hibernate.Hibernate
 *  net.sf.hibernate.HibernateException
 *  net.sf.hibernate.Session
 *  net.sf.hibernate.type.Type
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.hibernate.impl;

import com.opensymphony.user.provider.hibernate.dao.HibernateQueries;
import com.opensymphony.user.provider.hibernate.dao.HibernateUserDAO;
import com.opensymphony.user.provider.hibernate.dao.SessionManager;
import com.opensymphony.user.provider.hibernate.entity.HibernateUser;
import java.io.Serializable;
import java.util.List;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.type.Type;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HibernateUserDAOImpl
implements HibernateUserDAO {
    protected Log log = LogFactory.getLog((String)(class$com$opensymphony$user$provider$hibernate$impl$HibernateUserDAOImpl == null ? (class$com$opensymphony$user$provider$hibernate$impl$HibernateUserDAOImpl = HibernateUserDAOImpl.class$("com.opensymphony.user.provider.hibernate.impl.HibernateUserDAOImpl")) : class$com$opensymphony$user$provider$hibernate$impl$HibernateUserDAOImpl).getName());
    private SessionManager sessionManager;
    static /* synthetic */ Class class$com$opensymphony$user$provider$hibernate$impl$HibernateUserDAOImpl;

    public HibernateUserDAOImpl(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int deleteUserByUsername(String username) {
        int numberOfUsersDeleted = 0;
        Session session = null;
        try {
            session = this.sessionManager.getSession();
            numberOfUsersDeleted = session.delete(HibernateQueries.USER_BY_USERNAME, (Object)username, (Type)Hibernate.STRING);
        }
        catch (HibernateException he) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Unable to find user with username " + username), (Throwable)he);
            }
        }
        finally {
            this.sessionManager.closeSession(session);
        }
        return numberOfUsersDeleted;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public HibernateUser findUserByUsername(String username) {
        HibernateUser user = null;
        Session session = null;
        try {
            session = this.sessionManager.getSession();
            List ret = session.find(HibernateQueries.USER_BY_USERNAME, (Object)username, (Type)Hibernate.STRING);
            if (ret.size() > 0) {
                user = (HibernateUser)ret.get(0);
            }
        }
        catch (HibernateException he) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Unable to find user with username " + username), (Throwable)he);
            }
            user = null;
        }
        finally {
            this.sessionManager.closeSession(session);
        }
        return user;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public HibernateUser findUserByUsernameAndGroupname(String userName, String groupName) {
        Session session = null;
        HibernateUser user = null;
        try {
            session = this.sessionManager.getSession();
            List results = session.find(HibernateQueries.USER_BY_USERNAME_AND_GROUPNAME, new Object[]{userName, groupName}, new Type[]{Hibernate.STRING, Hibernate.STRING});
            if (results.size() == 0) {
                HibernateUser hibernateUser = null;
                return hibernateUser;
            }
            user = (HibernateUser)results.get(0);
        }
        catch (HibernateException he) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Unable to find user with username " + userName + " and groupname " + groupName), (Throwable)he);
            }
            user = null;
        }
        finally {
            this.sessionManager.closeSession(session);
        }
        return user;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List findUsers() {
        List users = null;
        Session session = null;
        try {
            session = this.sessionManager.getSession();
            users = session.find(HibernateQueries.ALL_USERS);
        }
        catch (HibernateException he) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"HibernateException retrieving all groups.", (Throwable)he);
            }
            users = null;
        }
        finally {
            this.sessionManager.closeSession(session);
        }
        return users;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean saveUser(HibernateUser user) {
        boolean result = false;
        Serializable id = null;
        Session session = null;
        try {
            session = this.sessionManager.getSession();
            id = session.save((Object)user);
            result = id != null;
        }
        catch (HibernateException he) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"HibernateException caught saving");
            }
            result = false;
        }
        finally {
            this.sessionManager.flushCloseSession(session);
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean updateUser(HibernateUser user) {
        boolean result = false;
        Session session = null;
        try {
            session = this.sessionManager.getSession();
            session.saveOrUpdate((Object)user);
            result = true;
        }
        catch (HibernateException he) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Unable to update user with username " + user.getName()), (Throwable)he);
            }
            result = false;
        }
        finally {
            this.sessionManager.flushCloseSession(session);
        }
        return result;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

