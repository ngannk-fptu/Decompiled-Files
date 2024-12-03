/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.context.ApplicationListener
 *  org.springframework.core.log.LogMessage
 *  org.springframework.util.Assert
 */
package org.springframework.security.core.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.session.AbstractSessionEvent;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionIdChangedEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.util.Assert;

public class SessionRegistryImpl
implements SessionRegistry,
ApplicationListener<AbstractSessionEvent> {
    protected final Log logger = LogFactory.getLog(SessionRegistryImpl.class);
    private final ConcurrentMap<Object, Set<String>> principals;
    private final Map<String, SessionInformation> sessionIds;

    public SessionRegistryImpl() {
        this.principals = new ConcurrentHashMap<Object, Set<String>>();
        this.sessionIds = new ConcurrentHashMap<String, SessionInformation>();
    }

    public SessionRegistryImpl(ConcurrentMap<Object, Set<String>> principals, Map<String, SessionInformation> sessionIds) {
        this.principals = principals;
        this.sessionIds = sessionIds;
    }

    @Override
    public List<Object> getAllPrincipals() {
        return new ArrayList<Object>(this.principals.keySet());
    }

    @Override
    public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
        Set sessionsUsedByPrincipal = (Set)this.principals.get(principal);
        if (sessionsUsedByPrincipal == null) {
            return Collections.emptyList();
        }
        ArrayList<SessionInformation> list = new ArrayList<SessionInformation>(sessionsUsedByPrincipal.size());
        for (String sessionId : sessionsUsedByPrincipal) {
            SessionInformation sessionInformation = this.getSessionInformation(sessionId);
            if (sessionInformation == null || !includeExpiredSessions && sessionInformation.isExpired()) continue;
            list.add(sessionInformation);
        }
        return list;
    }

    @Override
    public SessionInformation getSessionInformation(String sessionId) {
        Assert.hasText((String)sessionId, (String)"SessionId required as per interface contract");
        return this.sessionIds.get(sessionId);
    }

    public void onApplicationEvent(AbstractSessionEvent event) {
        SessionIdChangedEvent sessionIdChangedEvent;
        String oldSessionId;
        if (event instanceof SessionDestroyedEvent) {
            SessionDestroyedEvent sessionDestroyedEvent = (SessionDestroyedEvent)event;
            String sessionId = sessionDestroyedEvent.getId();
            this.removeSessionInformation(sessionId);
        } else if (event instanceof SessionIdChangedEvent && this.sessionIds.containsKey(oldSessionId = (sessionIdChangedEvent = (SessionIdChangedEvent)event).getOldSessionId())) {
            Object principal = this.sessionIds.get(oldSessionId).getPrincipal();
            this.removeSessionInformation(oldSessionId);
            this.registerNewSession(sessionIdChangedEvent.getNewSessionId(), principal);
        }
    }

    @Override
    public void refreshLastRequest(String sessionId) {
        Assert.hasText((String)sessionId, (String)"SessionId required as per interface contract");
        SessionInformation info = this.getSessionInformation(sessionId);
        if (info != null) {
            info.refreshLastRequest();
        }
    }

    @Override
    public void registerNewSession(String sessionId, Object principal) {
        Assert.hasText((String)sessionId, (String)"SessionId required as per interface contract");
        Assert.notNull((Object)principal, (String)"Principal required as per interface contract");
        if (this.getSessionInformation(sessionId) != null) {
            this.removeSessionInformation(sessionId);
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)LogMessage.format((String)"Registering session %s, for principal %s", (Object)sessionId, (Object)principal));
        }
        this.sessionIds.put(sessionId, new SessionInformation(principal, sessionId, new Date()));
        this.principals.compute(principal, (key, sessionsUsedByPrincipal) -> {
            if (sessionsUsedByPrincipal == null) {
                sessionsUsedByPrincipal = new CopyOnWriteArraySet<String>();
            }
            sessionsUsedByPrincipal.add(sessionId);
            this.logger.trace((Object)LogMessage.format((String)"Sessions used by '%s' : %s", (Object)principal, sessionsUsedByPrincipal));
            return sessionsUsedByPrincipal;
        });
    }

    @Override
    public void removeSessionInformation(String sessionId) {
        Assert.hasText((String)sessionId, (String)"SessionId required as per interface contract");
        SessionInformation info = this.getSessionInformation(sessionId);
        if (info == null) {
            return;
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.debug((Object)("Removing session " + sessionId + " from set of registered sessions"));
        }
        this.sessionIds.remove(sessionId);
        this.principals.computeIfPresent(info.getPrincipal(), (key, sessionsUsedByPrincipal) -> {
            this.logger.debug((Object)LogMessage.format((String)"Removing session %s from principal's set of registered sessions", (Object)sessionId));
            sessionsUsedByPrincipal.remove(sessionId);
            if (sessionsUsedByPrincipal.isEmpty()) {
                this.logger.debug((Object)LogMessage.format((String)"Removing principal %s from registry", (Object)info.getPrincipal()));
                sessionsUsedByPrincipal = null;
            }
            this.logger.trace((Object)LogMessage.format((String)"Sessions used by '%s' : %s", (Object)info.getPrincipal(), (Object)sessionsUsedByPrincipal));
            return sessionsUsedByPrincipal;
        });
    }
}

