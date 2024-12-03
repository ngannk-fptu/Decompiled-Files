/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.labs.botkiller;

import com.atlassian.sal.api.user.UserManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotKiller {
    private static final Logger log = LoggerFactory.getLogger(BotKiller.class);
    private static final int LOW_INACTIVE_TIMEOUT = 60;
    private static final int USER_LOW_INACTIVE_TIMEOUT = 600;
    private final UserManager userManager;

    public BotKiller(UserManager userManager) {
        this.userManager = userManager;
    }

    void processRequest(HttpServletRequest httpServletRequest) {
        try {
            HttpSession httpSession = httpServletRequest.getSession(false);
            if (httpSession == null) {
                return;
            }
            this.fiddleWithSession(httpServletRequest, httpSession);
        }
        catch (IllegalStateException illegalStateException) {
            // empty catch block
        }
    }

    private void fiddleWithSession(HttpServletRequest httpServletRequest, HttpSession httpSession) throws IllegalStateException {
        Integer initialMaxInactiveTimeout = (Integer)httpSession.getAttribute(BotKiller.class.getName());
        if (initialMaxInactiveTimeout == null) {
            initialMaxInactiveTimeout = httpSession.getMaxInactiveInterval();
            if (initialMaxInactiveTimeout <= 600) {
                return;
            }
            int lowInactiveTimeout = 60;
            if (this.thereIsAUserInPlay(httpServletRequest)) {
                lowInactiveTimeout = 600;
            }
            httpSession.setMaxInactiveInterval(lowInactiveTimeout);
            httpSession.setAttribute(BotKiller.class.getName(), (Object)initialMaxInactiveTimeout);
            if (log.isDebugEnabled()) {
                log.debug("Lowering session inactivity timeout to " + lowInactiveTimeout);
            }
        } else if (httpSession.getMaxInactiveInterval() != initialMaxInactiveTimeout.intValue()) {
            httpSession.setMaxInactiveInterval(initialMaxInactiveTimeout.intValue());
            if (log.isDebugEnabled()) {
                log.debug("Upping session inactivity timeout to " + initialMaxInactiveTimeout);
            }
        }
    }

    private boolean thereIsAUserInPlay(HttpServletRequest httpServletRequest) {
        try {
            if (this.userManager.getRemoteUsername(httpServletRequest) != null) {
                return true;
            }
            if (httpServletRequest.getRemoteUser() != null) {
                return true;
            }
        }
        catch (Exception e) {
            log.error("Error occurred when figuring out if the session has a user, assuming there is no user.", (Throwable)e);
        }
        return false;
    }
}

