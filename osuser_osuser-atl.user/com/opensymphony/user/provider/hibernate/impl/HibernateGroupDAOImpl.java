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

import com.opensymphony.user.provider.hibernate.dao.HibernateGroupDAO;
import com.opensymphony.user.provider.hibernate.dao.HibernateQueries;
import com.opensymphony.user.provider.hibernate.dao.SessionManager;
import com.opensymphony.user.provider.hibernate.entity.HibernateGroup;
import java.io.Serializable;
import java.util.List;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.type.Type;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HibernateGroupDAOImpl
implements HibernateGroupDAO {
    protected Log log = LogFactory.getLog((String)(class$com$opensymphony$user$provider$hibernate$impl$HibernateGroupDAOImpl == null ? (class$com$opensymphony$user$provider$hibernate$impl$HibernateGroupDAOImpl = HibernateGroupDAOImpl.class$("com.opensymphony.user.provider.hibernate.impl.HibernateGroupDAOImpl")) : class$com$opensymphony$user$provider$hibernate$impl$HibernateGroupDAOImpl).getName());
    private SessionManager sessionManager;
    static /* synthetic */ Class class$com$opensymphony$user$provider$hibernate$impl$HibernateGroupDAOImpl;

    public HibernateGroupDAOImpl(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int deleteGroupByGroupname(String groupname) {
        int numberDeletedGroups = 0;
        Session session = null;
        try {
            session = this.sessionManager.getSession();
            numberDeletedGroups = session.delete(HibernateQueries.GROUP_BY_GROUPNAME, (Object)groupname, (Type)Hibernate.STRING);
        }
        catch (HibernateException he) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Unable to delete group with groupname " + groupname), (Throwable)he);
            }
        }
        finally {
            this.sessionManager.flushCloseSession(session);
        }
        return numberDeletedGroups;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public HibernateGroup findGroupByGroupname(String groupname) {
        HibernateGroup group = null;
        Session session = null;
        try {
            session = this.sessionManager.getSession();
            List ret = session.find(HibernateQueries.GROUP_BY_GROUPNAME, (Object)groupname, (Type)Hibernate.STRING);
            if (ret.size() > 0) {
                group = (HibernateGroup)ret.get(0);
            }
        }
        catch (HibernateException he) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Unable to find group with groupname " + groupname), (Throwable)he);
            }
            group = null;
        }
        finally {
            this.sessionManager.closeSession(session);
        }
        return group;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List findGroups() {
        List groups = null;
        Session session = null;
        try {
            session = this.sessionManager.getSession();
            groups = session.find(HibernateQueries.ALL_GROUPS);
        }
        catch (HibernateException he) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"HibernateException retrieving all groups.", (Throwable)he);
            }
            groups = null;
        }
        finally {
            this.sessionManager.closeSession(session);
        }
        return groups;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public boolean saveGroup(HibernateGroup group) {
        Session session;
        boolean result;
        block9: {
            result = false;
            Serializable id = null;
            session = null;
            session = this.sessionManager.getSession();
            id = session.save((Object)group);
            result = id != null;
            Object var8_5 = null;
            try {
                session.flush();
                break block9;
            }
            catch (HibernateException e) {
                this.log.error((Object)"did not flush group", (Throwable)e);
            }
            {
                break block9;
                catch (HibernateException he) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)"HibernateException caught saving");
                    }
                    boolean bl = false;
                    Object var8_6 = null;
                    try {
                        session.flush();
                    }
                    catch (HibernateException e) {
                        this.log.error((Object)"did not flush group", (Throwable)e);
                    }
                    this.sessionManager.flushCloseSession(session);
                    return bl;
                }
            }
            catch (Throwable throwable) {
                Object var8_7 = null;
                try {
                    session.flush();
                }
                catch (HibernateException e) {
                    this.log.error((Object)"did not flush group", (Throwable)e);
                }
                this.sessionManager.flushCloseSession(session);
                throw throwable;
            }
        }
        this.sessionManager.flushCloseSession(session);
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

