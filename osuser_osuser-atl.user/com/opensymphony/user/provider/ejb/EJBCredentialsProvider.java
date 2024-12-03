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
import com.opensymphony.user.provider.CredentialsProvider;
import com.opensymphony.user.provider.ejb.UserManager;
import com.opensymphony.user.provider.ejb.UserManagerHome;
import com.opensymphony.util.EJBUtils;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;
import javax.ejb.CreateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EJBCredentialsProvider
implements CredentialsProvider {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$ejb$EJBCredentialsProvider == null ? (class$com$opensymphony$user$provider$ejb$EJBCredentialsProvider = EJBCredentialsProvider.class$("com.opensymphony.user.provider.ejb.EJBCredentialsProvider")) : class$com$opensymphony$user$provider$ejb$EJBCredentialsProvider));
    private UserManager session;
    static /* synthetic */ Class class$com$opensymphony$user$provider$ejb$EJBCredentialsProvider;
    static /* synthetic */ Class class$com$opensymphony$user$provider$ejb$UserManagerHome;

    public boolean authenticate(String name, String password) {
        if (log.isDebugEnabled()) {
            log.debug((Object)"EJBCredentialsProvider.authenticate");
        }
        try {
            return this.session.authenticate(name, password);
        }
        catch (Exception e) {
            throw new UserManagerImplementationException(e);
        }
    }

    public boolean changePassword(String name, String password) {
        if (log.isDebugEnabled()) {
            log.debug((Object)"EJBCredentialsProvider.changePassword");
        }
        try {
            return this.session.changePassword(name, password);
        }
        catch (Exception e) {
            throw new UserManagerImplementationException(e);
        }
    }

    public boolean create(String name) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("create() name = " + name));
        }
        try {
            return this.session.createUser(name);
        }
        catch (CreateException e) {
            log.error((Object)("CreateException creating : " + name), (Throwable)e);
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
            if (this.session.userExists(name)) {
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
            this.session = ((UserManagerHome)EJBUtils.lookup((String)managerLocation, (Class)(class$com$opensymphony$user$provider$ejb$UserManagerHome == null ? (class$com$opensymphony$user$provider$ejb$UserManagerHome = EJBCredentialsProvider.class$("com.opensymphony.user.provider.ejb.UserManagerHome")) : class$com$opensymphony$user$provider$ejb$UserManagerHome))).create();
            return true;
        }
        catch (Exception e) {
            log.fatal((Object)"Unable to look up session bean", (Throwable)e);
            throw new UserManagerImplementationException("Unable to look up user manager session bean", e);
        }
    }

    public List list() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"EJBCredentialsProvider.list");
        }
        try {
            return this.session.getUserNames();
        }
        catch (Exception e) {
            throw new UserManagerImplementationException(e);
        }
    }

    public boolean load(String name, Entity.Accessor accessor) {
        accessor.setMutable(true);
        return true;
    }

    public boolean remove(String name) {
        if (log.isDebugEnabled()) {
            log.debug((Object)"EJBCredentialsProvider.remove");
        }
        try {
            return this.session.removeUser(name);
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

