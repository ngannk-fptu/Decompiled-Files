/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.EJBUtils
 *  javax.ejb.CreateException
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.ejb;

import com.opensymphony.user.Entity;
import com.opensymphony.user.UserManagerImplementationException;
import com.opensymphony.user.provider.AccessProvider;
import com.opensymphony.user.provider.ejb.UserManager;
import com.opensymphony.user.provider.ejb.UserManagerHome;
import com.opensymphony.util.EJBUtils;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.ejb.CreateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EJBAccessProvider
implements AccessProvider {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$ejb$EJBProfileProvider == null ? (class$com$opensymphony$user$provider$ejb$EJBProfileProvider = EJBAccessProvider.class$("com.opensymphony.user.provider.ejb.EJBProfileProvider")) : class$com$opensymphony$user$provider$ejb$EJBProfileProvider));
    private UserManager session;
    static /* synthetic */ Class class$com$opensymphony$user$provider$ejb$EJBProfileProvider;
    static /* synthetic */ Class class$com$opensymphony$user$provider$ejb$UserManagerHome;

    public boolean addToGroup(String username, String groupname) {
        if (this.inGroup(username, groupname)) {
            return true;
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("adding user (" + username + ") to group (" + groupname + ")"));
            }
            return this.session.addToGroup(username, groupname);
        }
        catch (Exception e) {
            throw new UserManagerImplementationException(e);
        }
    }

    public boolean create(String name) {
        try {
            return this.session.createGroup(name);
        }
        catch (CreateException e) {
            return false;
        }
        catch (Exception e) {
            throw new UserManagerImplementationException(e);
        }
    }

    public void flushCaches() {
    }

    public boolean handles(String name) {
        try {
            if (this.session.groupExists(name)) {
                return true;
            }
            if (this.session.userExists(name)) {
                return true;
            }
        }
        catch (RemoteException e) {
            log.error((Object)e.getMessage(), (Throwable)e);
        }
        return false;
    }

    public boolean inGroup(String username, String groupname) {
        try {
            return this.session.isUserInGroup(username, groupname);
        }
        catch (RemoteException e) {
            log.error((Object)e.getMessage(), (Throwable)e);
            return false;
        }
    }

    public boolean init(Properties properties) {
        try {
            String managerLocation = properties.getProperty("location.manager", "ejb/osuser/Manager");
            this.session = ((UserManagerHome)EJBUtils.lookup((String)managerLocation, (Class)(class$com$opensymphony$user$provider$ejb$UserManagerHome == null ? (class$com$opensymphony$user$provider$ejb$UserManagerHome = EJBAccessProvider.class$("com.opensymphony.user.provider.ejb.UserManagerHome")) : class$com$opensymphony$user$provider$ejb$UserManagerHome))).create();
            return true;
        }
        catch (Exception e) {
            log.fatal((Object)"Unable to look up session bean", (Throwable)e);
            throw new UserManagerImplementationException("Unable to look up user manager session bean", e);
        }
    }

    public List list() {
        try {
            return this.session.getGroupNames();
        }
        catch (RemoteException e) {
            log.error((Object)e.getMessage(), (Throwable)e);
            return Collections.EMPTY_LIST;
        }
    }

    public List listGroupsContainingUser(String username) {
        try {
            return this.session.getUserGroups(username);
        }
        catch (Exception e) {
            log.info((Object)e);
            throw new UserManagerImplementationException(e);
        }
    }

    public List listUsersInGroup(String groupname) {
        try {
            return this.session.getUsersInGroup(groupname);
        }
        catch (Exception e) {
            e.printStackTrace();
            log.info((Object)e);
            throw new UserManagerImplementationException(e);
        }
    }

    public boolean load(String name, Entity.Accessor accessor) {
        accessor.setMutable(true);
        return true;
    }

    public boolean remove(String name) {
        try {
            return this.session.removeGroup(name);
        }
        catch (Exception e) {
            throw new UserManagerImplementationException(e);
        }
    }

    public boolean removeFromGroup(String username, String groupname) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("removing user (" + username + ") from group (" + groupname + ")"));
        }
        try {
            return this.session.removeFromGroup(username, groupname);
        }
        catch (Exception e) {
            throw new UserManagerImplementationException(e);
        }
    }

    public boolean store(String name, Entity.Accessor accessor) {
        return true;
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

