/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.security.acl.Group
 *  org.jboss.security.SimpleGroup
 *  org.jboss.security.SimplePrincipal
 *  org.jboss.security.auth.spi.UsernamePasswordLoginModule
 */
package com.opensymphony.user.adapter.jboss;

import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManager;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.UsernamePasswordLoginModule;

public class OSUserLoginModule
extends UsernamePasswordLoginModule {
    private static UserManager um;

    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
        super.initialize(subject, callbackHandler, sharedState, options);
        um = UserManager.getInstance();
    }

    protected Group[] getRoleSets() throws LoginException {
        User user = null;
        try {
            user = um.getUser(this.getUsername());
        }
        catch (EntityNotFoundException e) {
            throw new FailedLoginException("Invalid User ID/Cannot Find User");
        }
        List groups = user.getGroups();
        SimpleGroup roles = new SimpleGroup("Roles");
        Iterator iterator = groups.iterator();
        while (iterator.hasNext()) {
            String group = (String)iterator.next();
            roles.addMember((Principal)new SimplePrincipal(group));
            System.out.println("adding as member of " + group);
        }
        return new Group[]{roles};
    }

    protected String getUsersPassword() throws LoginException {
        return "";
    }

    protected boolean validatePassword(String password, String empty) {
        System.out.println("getUsername() = " + this.getUsername());
        try {
            User user = um.getUser(this.getUsername());
            return user.authenticate(password);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}

