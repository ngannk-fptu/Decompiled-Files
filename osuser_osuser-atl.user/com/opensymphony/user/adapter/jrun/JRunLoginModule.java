/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jrun.security.RolesCallback
 *  jrun.security.SimplePrincipal
 */
package com.opensymphony.user.adapter.jrun;

import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.UserManager;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import jrun.security.RolesCallback;
import jrun.security.SimplePrincipal;

public class JRunLoginModule
implements LoginModule {
    private CallbackHandler cbHandler;
    private Map options;
    private Map sharedState;
    private SimplePrincipal userPrincipal;
    private String loginMode = "USER";
    private String password = null;
    private String username = null;
    private Subject subject;
    private UserManager um;
    private boolean commitSucceeded = false;
    private boolean succeeded = false;

    public boolean abort() throws LoginException {
        if (!this.succeeded) {
            return false;
        }
        if (this.succeeded && !this.commitSucceeded) {
            this.succeeded = false;
            this.username = null;
            if (this.password != null) {
                this.password = null;
            }
            this.userPrincipal = null;
        } else {
            this.logout();
        }
        return true;
    }

    public boolean commit() throws LoginException {
        if (!this.succeeded) {
            return false;
        }
        this.userPrincipal = new SimplePrincipal(this.username);
        if (!this.subject.getPrincipals().contains(this.userPrincipal)) {
            this.subject.getPrincipals().add((Principal)this.userPrincipal);
        }
        this.username = null;
        this.password = null;
        this.commitSucceeded = true;
        return true;
    }

    public void initialize(Subject subj, CallbackHandler cbh, Map sharedState, Map options) {
        this.subject = subj;
        this.cbHandler = cbh;
        this.sharedState = sharedState;
        this.options = options;
        this.loginMode = (String)this.options.get("mode");
        this.um = UserManager.getInstance();
    }

    public boolean login() throws LoginException {
        if (this.loginMode.equals("ROLE")) {
            return this.validateRole();
        }
        return this.loginUser();
    }

    public boolean logout() throws LoginException {
        this.subject.getPrincipals().remove(this.userPrincipal);
        this.username = null;
        this.password = null;
        this.userPrincipal = null;
        return true;
    }

    protected List getUserRoles() throws LoginException {
        try {
            return this.um.getUser(this.username).getGroups();
        }
        catch (EntityNotFoundException e) {
            return Collections.EMPTY_LIST;
        }
    }

    protected boolean loginUser() throws LoginException {
        NameCallback n = new NameCallback("User Name - ", "Guest");
        PasswordCallback p = new PasswordCallback("Password - ", false);
        Callback[] callbacks = new Callback[]{n, p};
        try {
            this.cbHandler.handle(callbacks);
        }
        catch (IOException e) {
            return false;
        }
        catch (UnsupportedCallbackException e) {
            return false;
        }
        this.username = n.getName().trim();
        this.password = new String(p.getPassword());
        try {
            this.succeeded = this.um.getUser(this.username).authenticate(this.password);
        }
        catch (EntityNotFoundException e) {
            this.succeeded = false;
        }
        return this.succeeded;
    }

    protected boolean validateRole() throws LoginException {
        boolean userRoleFound = false;
        RolesCallback rcb = new RolesCallback();
        Callback[] callbacks = new Callback[]{rcb};
        try {
            this.cbHandler.handle(callbacks);
        }
        catch (IOException e) {
            return false;
        }
        catch (UnsupportedCallbackException e) {
            return false;
        }
        Principal p = rcb.getPrincipal();
        this.username = p.getName().trim();
        Collection rolesToCheck = rcb.getRoles();
        List rolesFromDatabase = this.getUserRoles();
        Iterator i = rolesToCheck.iterator();
        while (i.hasNext() && !userRoleFound) {
            String thisRoleName = (String)i.next();
            int numberOfRolesFromDB = rolesFromDatabase.size();
            for (int index = 0; index < numberOfRolesFromDB; ++index) {
                String dbRoleName = (String)rolesFromDatabase.get(index);
                if (!thisRoleName.equals(dbRoleName.trim())) continue;
                this.succeeded = true;
                userRoleFound = true;
            }
        }
        return userRoleFound;
    }
}

