/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerContext
 *  com.atlassian.spring.container.ContainerManager
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package com.atlassian.confluence.internal.diagnostics.ipd.http.session;

import com.atlassian.confluence.internal.diagnostics.ipd.http.session.ConfluenceHttpSession;
import com.atlassian.confluence.internal.diagnostics.ipd.http.session.MutableConfluenceHttpSession;
import com.atlassian.confluence.internal.diagnostics.ipd.http.session.SessionDeletionStrategy;
import com.atlassian.confluence.internal.diagnostics.ipd.http.session.SnapshotConfluenceHttpSession;
import com.atlassian.spring.container.ContainerContext;
import com.atlassian.spring.container.ContainerManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HttpSessionTracker {
    private final ConcurrentMap<String, MutableConfluenceHttpSession> sessionMap = new ConcurrentHashMap<String, MutableConfluenceHttpSession>();
    private final SessionDeletionStrategy deletionStrategy = new SessionDeletionStrategy();

    public static HttpSessionTracker getInstance() {
        if (ContainerManager.isContainerSetup()) {
            ContainerContext context = ContainerManager.getInstance().getContainerContext();
            return (HttpSessionTracker)context.getComponent((Object)"httpSessionTracker");
        }
        return null;
    }

    public static void recordInteraction(HttpServletRequest httpServletRequest) {
        HttpSessionTracker tracker = HttpSessionTracker.getInstance();
        if (tracker != null) {
            tracker.recordInteractionImpl(httpServletRequest);
        }
    }

    private void recordInteractionImpl(HttpServletRequest httpServletRequest) {
        this.deletionStrategy.deleteOldSessions(this.sessionMap);
        HttpSession httpSession = httpServletRequest.getSession(false);
        if (httpSession != null) {
            String sessionId = httpSession.getId();
            MutableConfluenceHttpSession session = (MutableConfluenceHttpSession)this.sessionMap.get(sessionId);
            if (session == null) {
                MutableConfluenceHttpSession newSession = new MutableConfluenceHttpSession(sessionId, httpServletRequest.getRemoteUser());
                MutableConfluenceHttpSession previousValue = this.sessionMap.putIfAbsent(sessionId, newSession);
                session = previousValue != null ? previousValue : newSession;
            }
            session.recordInteraction(httpServletRequest.getRemoteUser());
        }
    }

    public Map<String, ConfluenceHttpSession> getSnapshot() {
        return this.sessionMap.values().stream().map(SnapshotConfluenceHttpSession::new).collect(Collectors.toMap(SnapshotConfluenceHttpSession::getId, Function.identity()));
    }
}

