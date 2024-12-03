/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.evermind.security.User
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.orion;

import com.evermind.security.User;
import com.opensymphony.user.provider.CredentialsProvider;
import com.opensymphony.user.provider.orion.OrionProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OrionCredentialsProvider
extends OrionProvider
implements CredentialsProvider {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$orion$OrionCredentialsProvider == null ? (class$com$opensymphony$user$provider$orion$OrionCredentialsProvider = OrionCredentialsProvider.class$("com.opensymphony.user.provider.orion.OrionCredentialsProvider")) : class$com$opensymphony$user$provider$orion$OrionCredentialsProvider));
    static /* synthetic */ Class class$com$opensymphony$user$provider$orion$OrionCredentialsProvider;

    public boolean authenticate(String name, String password) {
        if (this.roleManager == null) {
            return false;
        }
        try {
            this.roleManager.login(name, password);
            return true;
        }
        catch (SecurityException ex) {
            return false;
        }
    }

    public boolean changePassword(String name, String password) {
        try {
            User user = this.userManager.getUser(name);
            user.setPassword(password);
            this.userManager.store();
            return true;
        }
        catch (Exception ex) {
            log.warn((Object)("unable to change password for user " + name), (Throwable)ex);
            return false;
        }
    }

    public boolean create(String name) {
        try {
            this.userManager.createUser(name, null);
            this.userManager.store();
            return true;
        }
        catch (InstantiationException ex) {
            log.warn((Object)("Cannot create user " + name + ": User already exists"));
        }
        catch (Exception ex) {
            log.error((Object)("Error creating user " + name), (Throwable)ex);
        }
        return false;
    }

    public boolean handles(String name) {
        return this.userManager.getUser(name) != null;
    }

    public List list() {
        try {
            List users = this.userManager.getUsers(0, this.userManager.getUserCount());
            ArrayList<String> result = new ArrayList<String>(users.size());
            Iterator iter = users.iterator();
            while (iter.hasNext()) {
                User user = (User)iter.next();
                result.add(user.getName());
            }
            return Collections.unmodifiableList(result);
        }
        catch (Exception ex) {
            log.error((Object)"Error getting list of users", (Throwable)ex);
            return null;
        }
    }

    public boolean remove(String name) {
        try {
            this.userManager.remove(this.userManager.getUser(name));
            this.userManager.store();
            if (log.isDebugEnabled()) {
                log.debug((Object)("Removed " + name));
            }
            return true;
        }
        catch (Exception ex) {
            log.error((Object)("Error removing user " + name), (Throwable)ex);
            return false;
        }
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

