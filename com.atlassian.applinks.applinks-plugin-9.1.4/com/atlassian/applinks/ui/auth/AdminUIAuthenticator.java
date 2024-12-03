/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.ui.auth;

import com.atlassian.applinks.ui.auth.ServletSessionHandler;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminUIAuthenticator {
    public static final String ADMIN_USERNAME = "al_username";
    public static final String ADMIN_PASSWORD = "al_password";
    private static final String ADMIN_SESSION_KEY = "al_auth";
    private static final String ADMIN = "admin";
    private static final String SYSADMIN = "sysadmin";
    private final UserManager userManager;

    @Autowired
    public AdminUIAuthenticator(UserManager userManager) {
        this.userManager = userManager;
    }

    public boolean checkAdminUIAccessBySessionOrPasswordAndActivateAdminSession(String username, String password, SessionHandler sessionHandler) {
        if (this.isAdminSession(sessionHandler)) {
            return true;
        }
        if (this.checkAdminUIAccessByPasswordOrCurrentUser(username, password)) {
            sessionHandler.set(ADMIN_SESSION_KEY, ADMIN);
            return true;
        }
        return false;
    }

    public boolean checkSysadminUIAccessBySessionOrPasswordAndActivateSysadminSession(String username, String password, SessionHandler sessionHandler) {
        if (this.isSysadminSession(sessionHandler)) {
            return true;
        }
        if (this.checkSysadminUIAccessByPasswordOrCurrentUser(username, password)) {
            sessionHandler.set(ADMIN_SESSION_KEY, SYSADMIN);
            return true;
        }
        return false;
    }

    public boolean checkAdminUIAccessByPasswordOrCurrentUser(String username, String password) {
        if (username != null & password != null) {
            return this.userManager.authenticate(username, password) && this.userManager.isAdmin(new UserKey(username));
        }
        return this.isCurrentUserAdmin();
    }

    public boolean checkSysadminUIAccessByPasswordOrCurrentUser(String username, String password) {
        if (username != null & password != null) {
            return this.userManager.authenticate(username, password) && this.userManager.isSystemAdmin(new UserKey(username));
        }
        return this.isCurrentUserSysadmin();
    }

    public boolean checkAdminUIAccessBySessionOrCurrentUser(HttpServletRequest request) {
        UserKey userKey = this.userManager.getRemoteUserKey();
        return this.isAdminSession(request) || this.isAdmin(userKey);
    }

    public boolean checkSysadminUIAccessBySessionOrCurrentUser(HttpServletRequest request) {
        UserKey userKey = this.userManager.getRemoteUserKey();
        return this.isSysadminSession(request) || this.isSysadmin(userKey);
    }

    public boolean isCurrentUserAdmin() {
        return this.isAdmin(this.userManager.getRemoteUserKey());
    }

    public boolean isCurrentUserSysadmin() {
        return this.isSysadmin(this.userManager.getRemoteUserKey());
    }

    private boolean isAdmin(UserKey userKey) {
        return userKey != null && (this.userManager.isAdmin(userKey) || this.userManager.isSystemAdmin(userKey));
    }

    private boolean isSysadmin(UserKey userKey) {
        return userKey != null && this.userManager.isSystemAdmin(userKey);
    }

    private boolean isAdminSession(HttpServletRequest request) {
        return this.isAdminSession(new ServletSessionHandler(request));
    }

    private boolean isAdminSession(SessionHandler sessionHandler) {
        return ADMIN.equals(sessionHandler.get(ADMIN_SESSION_KEY)) || SYSADMIN.equals(sessionHandler.get(ADMIN_SESSION_KEY));
    }

    private boolean isSysadminSession(HttpServletRequest request) {
        return this.isSysadminSession(new ServletSessionHandler(request));
    }

    private boolean isSysadminSession(SessionHandler sessionHandler) {
        return SYSADMIN.equals(sessionHandler.get(ADMIN_SESSION_KEY));
    }

    public static interface SessionHandler {
        public void set(String var1, Object var2);

        public Object get(String var1);
    }
}

