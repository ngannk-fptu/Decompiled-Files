/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.event.EventManager
 *  com.atlassian.seraph.auth.Authenticator
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.event.events.security.LogoutEvent;
import com.atlassian.confluence.util.SeraphUtils;
import com.atlassian.event.Event;
import com.atlassian.event.EventManager;
import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.config.SecurityConfigFactory;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationHelper {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationHelper.class);

    public static boolean userLogout(Principal user, HttpServletRequest request, HttpServletResponse response, EventManager eventManager, Object source) {
        return AuthenticationHelper.logout(user, request, response, eventManager, source, true);
    }

    public static boolean systemLogout(Principal user, HttpServletRequest request, HttpServletResponse response, EventManager eventManager, Object source) {
        return AuthenticationHelper.logout(user, request, response, eventManager, source, false);
    }

    @Deprecated
    public static boolean logout(Principal user, HttpServletRequest request, HttpServletResponse response, EventManager eventManager, Object source) {
        return AuthenticationHelper.userLogout(user, request, response, eventManager, source);
    }

    private static boolean logout(Principal user, HttpServletRequest request, HttpServletResponse response, EventManager eventManager, Object source, boolean userInitiatedLogout) {
        try {
            Authenticator authenticator = SecurityConfigFactory.getInstance().getAuthenticator();
            boolean logoutSuccessful = authenticator.logout(request, response);
            if (!logoutSuccessful) {
                return false;
            }
            if (user != null) {
                HttpSession session = request.getSession();
                LogoutEvent logoutEvent = userInitiatedLogout ? new LogoutEvent(source, user.getName(), session.getId(), request.getRemoteHost(), request.getRemoteAddr()) : new LogoutEvent(source, user.getName(), session.getId());
                eventManager.publishEvent((Event)logoutEvent);
            }
            AuthenticationHelper.invalidateSession(request);
            return true;
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    private static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        AuthenticationHelper.clearUnsafeValuesFromSession(session);
        if (Boolean.getBoolean("com.atlassian.logout.invalidatesession")) {
            log.warn("System property 'com.atlassian.logout.invalidatesession' no longer has any effect. Sessions are invalidated by default. You can remove it from your configuration.");
        }
        if (!Boolean.getBoolean("com.atlassian.logout.disable.session.invalidation")) {
            try {
                session.invalidate();
            }
            catch (IllegalStateException ex) {
                log.debug("Cannot invalidate already invalid session");
            }
        }
    }

    private static void clearUnsafeValuesFromSession(HttpSession session) {
        try {
            AuthenticationHelper.getNonSeraphAttributeKeys(session).stream().forEach(arg_0 -> ((HttpSession)session).removeAttribute(arg_0));
        }
        catch (IllegalStateException ex) {
            log.debug("Failed to clear unsafe values from session - session is already invalid");
        }
    }

    private static List<String> getNonSeraphAttributeKeys(HttpSession session) {
        ArrayList attributeNames = Collections.list(session.getAttributeNames());
        return attributeNames.stream().filter(key -> {
            if (!key.startsWith("seraph")) {
                if (log.isDebugEnabled()) {
                    log.debug("Removing value from session on logout: " + key + " value: " + session.getAttribute(key));
                }
                return true;
            }
            if (log.isDebugEnabled()) {
                log.debug("Not removing seraph value from session on logout: " + key + " value: " + session.getAttribute(key));
            }
            return false;
        }).collect(Collectors.toList());
    }

    public static String getLoginUrl() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String contextPath = StringUtils.defaultString((String)request.getContextPath());
        String loginURL = SeraphUtils.getLoginURL(request);
        if (log.isDebugEnabled()) {
            log.debug("Seraph login.url is " + loginURL);
        }
        if (StringUtils.isNotEmpty((CharSequence)contextPath) && StringUtils.defaultString((String)loginURL).startsWith(contextPath)) {
            loginURL = loginURL.substring(contextPath.length());
        }
        return loginURL;
    }
}

