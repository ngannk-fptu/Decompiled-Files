/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.pramati.security.util.GroupImpl
 */
package com.opensymphony.user.adapter.pramati;

import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManager;
import com.pramati.security.util.GroupImpl;
import java.io.IOException;
import java.security.Principal;
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

public class PramatiLoginModule
implements LoginModule {
    private CallbackHandler callbackHandler;
    private List lstGroups = null;
    private Map options;
    private Map sharedState;
    private String password = null;
    private String realmName;
    private String username = null;
    private Subject subject;
    private User user;
    private UserManager um;
    private boolean commitSucceeded = false;
    private boolean populateGroups = true;
    private boolean succeeded = false;

    public boolean abort() throws LoginException {
        if (!this.succeeded) {
            return false;
        }
        if (this.succeeded && !this.commitSucceeded) {
            this.user = null;
            this.username = null;
            this.password = null;
            this.lstGroups = null;
        } else {
            this.logout();
        }
        return this.succeeded;
    }

    public boolean commit() throws LoginException {
        if (!this.succeeded) {
            return false;
        }
        if (!this.subject.getPrincipals().contains(this.user)) {
            this.subject.getPrincipals().add(this.user);
        }
        if (this.populateGroups) {
            Iterator groupsForUser = this.lstGroups.iterator();
            while (groupsForUser.hasNext()) {
                GroupImpl group = new GroupImpl(this.realmName, (String)groupsForUser.next());
                if (this.subject.getPrincipals().contains(group)) continue;
                this.subject.getPrincipals().add((Principal)group);
            }
        }
        this.commitSucceeded = true;
        return true;
    }

    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
        this.populateGroups = true;
        this.um = UserManager.getInstance();
        this.realmName = "osuser";
        if (options.containsKey("realmName")) {
            this.realmName = (String)options.get("realmName");
        }
    }

    public boolean login() throws LoginException {
        this.getUsernamePassword();
        try {
            this.user = this.um.getUser(this.username);
        }
        catch (EntityNotFoundException e) {
            throw new LoginException(e.getMessage());
        }
        if (!this.user.authenticate(this.password)) {
            throw new LoginException("Authentication failure: Incorrect password");
        }
        if (this.populateGroups) {
            this.lstGroups = this.user.getGroups();
        }
        this.succeeded = true;
        return this.succeeded;
    }

    public boolean logout() throws LoginException {
        this.subject.getPrincipals().clear();
        this.succeeded = false;
        this.commitSucceeded = false;
        this.username = null;
        this.password = null;
        this.lstGroups = null;
        this.user = null;
        return true;
    }

    private void getUsernamePassword() throws LoginException {
        if (this.callbackHandler == null) {
            throw new LoginException("Error: no CallbackHandler available to garner authentication information from the user");
        }
        Callback[] callbacks = new Callback[]{new NameCallback("username: "), new PasswordCallback("password: ", false)};
        try {
            this.callbackHandler.handle(callbacks);
            this.username = ((NameCallback)callbacks[0]).getName();
            char[] tmpPassword = ((PasswordCallback)callbacks[1]).getPassword();
            if (tmpPassword == null) {
                tmpPassword = new char[]{};
            }
            this.password = new String(tmpPassword);
        }
        catch (IOException ioe) {
            throw new LoginException(ioe.toString());
        }
        catch (UnsupportedCallbackException uce) {
            throw new LoginException("Error: " + uce.getCallback().toString() + " not available to garner authentication information " + "from the user");
        }
    }
}

