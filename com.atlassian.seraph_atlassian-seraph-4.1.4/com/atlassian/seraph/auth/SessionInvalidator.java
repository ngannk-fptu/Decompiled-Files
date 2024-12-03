/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.seraph.auth;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionInvalidator {
    private static final Logger log = LoggerFactory.getLogger(SessionInvalidator.class);
    private final List<String> excludeList;

    public SessionInvalidator(List<String> excludeList) {
        if (excludeList == null) {
            throw new IllegalArgumentException("excludeList must not be null");
        }
        this.excludeList = excludeList;
    }

    public void invalidateSession(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        if (session != null && !session.isNew()) {
            if (log.isDebugEnabled()) {
                this.dumpInfo(httpServletRequest, session);
            }
            Map<String, Object> contents = this.getSessionContentsToKeep(session);
            try {
                session.invalidate();
                HttpSession newSession = httpServletRequest.getSession(true);
                SessionInvalidator.setAll(newSession, contents);
            }
            catch (IllegalStateException e) {
                log.warn("Couldn't invalidate for request because " + e.getMessage());
            }
        }
    }

    private static void setAll(HttpSession dest, Map<String, Object> source) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            dest.setAttribute(entry.getKey(), entry.getValue());
        }
    }

    private Map<String, Object> getSessionContentsToKeep(HttpSession session) {
        HashMap<String, Object> sessionContents = new HashMap<String, Object>();
        Enumeration attributes = session.getAttributeNames();
        while (attributes.hasMoreElements()) {
            String name = (String)attributes.nextElement();
            if (this.excludeList.contains(name)) continue;
            sessionContents.put(name, session.getAttribute(name));
        }
        return sessionContents;
    }

    private void dumpInfo(HttpServletRequest httpServletRequest, HttpSession session) {
        log.debug("invalidating session from request: " + httpServletRequest.getMethod() + " " + session.getId() + " " + httpServletRequest.getRequestURI() + " ");
        Enumeration attributes = session.getAttributeNames();
        while (attributes.hasMoreElements()) {
            String name = (String)attributes.nextElement();
            log.debug("session attribute: " + name);
        }
    }
}

