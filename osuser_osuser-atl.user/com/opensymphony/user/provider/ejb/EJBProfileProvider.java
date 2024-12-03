/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertySet
 *  com.opensymphony.util.EJBUtils
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.ejb;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.Entity;
import com.opensymphony.user.UserManagerImplementationException;
import com.opensymphony.user.provider.ProfileProvider;
import com.opensymphony.user.provider.ejb.UserManager;
import com.opensymphony.user.provider.ejb.UserManagerHome;
import com.opensymphony.util.EJBUtils;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EJBProfileProvider
implements ProfileProvider {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$ejb$EJBProfileProvider == null ? (class$com$opensymphony$user$provider$ejb$EJBProfileProvider = EJBProfileProvider.class$("com.opensymphony.user.provider.ejb.EJBProfileProvider")) : class$com$opensymphony$user$provider$ejb$EJBProfileProvider));
    private UserManager session;
    static /* synthetic */ Class class$com$opensymphony$user$provider$ejb$EJBProfileProvider;
    static /* synthetic */ Class class$com$opensymphony$user$provider$ejb$UserManagerHome;

    public PropertySet getPropertySet(String name) {
        try {
            PropertySet ps = this.session.getUserPropertySet(name);
            if (ps == null) {
                ps = this.session.getGroupPropertySet(name);
            }
            return ps;
        }
        catch (Exception e) {
            throw new UserManagerImplementationException(e);
        }
    }

    public boolean create(String name) {
        return true;
    }

    public void flushCaches() {
    }

    public boolean handles(String name) {
        try {
            if (this.session.userExists(name)) {
                return true;
            }
            if (this.session.groupExists(name)) {
                return true;
            }
        }
        catch (RemoteException e) {
            log.error((Object)e.getMessage(), (Throwable)e);
        }
        return false;
    }

    public boolean init(Properties properties) {
        try {
            String managerLocation = properties.getProperty("location.manager", "ejb/osuser/Manager");
            this.session = ((UserManagerHome)EJBUtils.lookup((String)managerLocation, (Class)(class$com$opensymphony$user$provider$ejb$UserManagerHome == null ? (class$com$opensymphony$user$provider$ejb$UserManagerHome = EJBProfileProvider.class$("com.opensymphony.user.provider.ejb.UserManagerHome")) : class$com$opensymphony$user$provider$ejb$UserManagerHome))).create();
            return true;
        }
        catch (Exception e) {
            log.fatal((Object)"Unable to look up session bean", (Throwable)e);
            throw new UserManagerImplementationException("Unable to look up user manager session bean", e);
        }
    }

    public List list() {
        return null;
    }

    public boolean load(String name, Entity.Accessor accessor) {
        return true;
    }

    public boolean remove(String name) {
        try {
            PropertySet ps = this.getPropertySet(name);
            if (ps != null) {
                ps.remove();
            }
        }
        catch (Exception ex) {
            log.error((Object)("Error removing propertyset for " + name), (Throwable)ex);
        }
        return false;
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

