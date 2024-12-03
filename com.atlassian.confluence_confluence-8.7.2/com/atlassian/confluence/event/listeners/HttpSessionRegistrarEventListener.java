/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.event.user.UserCredentialUpdatedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  javax.servlet.http.HttpSession
 *  org.springframework.security.core.session.SessionInformation
 *  org.springframework.security.core.session.SessionRegistry
 */
package com.atlassian.confluence.event.listeners;

import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.event.events.cluster.ExpireUserSessionsClusterNotificationEvent;
import com.atlassian.confluence.security.seraph.ConfluenceUserPrincipal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.crowd.event.user.UserCredentialUpdatedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.features.DarkFeatureManager;
import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;

public final class HttpSessionRegistrarEventListener {
    private static final String HTTP_SESSION_REGISTRAR = "http.session.registrar";
    private final UserAccessor userAccessor;
    private final HttpContext httpContext;
    private final EventPublisher eventPublisher;
    private final DarkFeatureManager darkFeatureManager;
    private final SessionRegistry registry;

    public HttpSessionRegistrarEventListener(UserAccessor userAccessor, HttpContext httpContext, EventPublisher eventPublisher, DarkFeatureManager darkFeatureManager, SessionRegistry registry) {
        this.userAccessor = userAccessor;
        this.httpContext = httpContext;
        this.eventPublisher = eventPublisher;
        this.darkFeatureManager = darkFeatureManager;
        this.registry = registry;
    }

    @EventListener
    public void onUserCredentialUpdatedEvent(UserCredentialUpdatedEvent event) {
        if (!this.darkFeatureManager.isEnabledForAllUsers(HTTP_SESSION_REGISTRAR).orElse(true).booleanValue()) {
            return;
        }
        HttpSession httpSession = this.httpContext.getSession(false);
        if (null == httpSession) {
            return;
        }
        ConfluenceUser user = this.userAccessor.getUserByName(event.getUsername());
        ConfluenceUserPrincipal principal = ConfluenceUserPrincipal.of((Principal)((Object)user));
        List sessionInformationList = this.registry.getAllSessions((Object)principal, false);
        sessionInformationList.stream().filter(sessionInformation -> !sessionInformation.getSessionId().equals(httpSession.getId())).forEach(SessionInformation::expireNow);
        this.eventPublisher.publish((Object)new ExpireUserSessionsClusterNotificationEvent(this, principal));
    }

    @EventListener
    public void onExpireUserSessionsClusterNotificationEvent(ClusterEventWrapper clusterEventWrapper) {
        if (!(clusterEventWrapper.getEvent() instanceof ExpireUserSessionsClusterNotificationEvent)) {
            return;
        }
        ExpireUserSessionsClusterNotificationEvent event = (ExpireUserSessionsClusterNotificationEvent)clusterEventWrapper.getEvent();
        List sessionInformationList = this.registry.getAllSessions((Object)event.getPrincipal(), false);
        sessionInformationList.forEach(SessionInformation::expireNow);
    }
}

