/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.event.SmartApplicationListener
 *  org.springframework.lang.Nullable
 *  org.springframework.messaging.Message
 *  org.springframework.messaging.MessageHeaders
 *  org.springframework.messaging.simp.SimpMessageHeaderAccessor
 *  org.springframework.messaging.simp.user.DestinationUserNameProvider
 *  org.springframework.messaging.simp.user.SimpSession
 *  org.springframework.messaging.simp.user.SimpSubscription
 *  org.springframework.messaging.simp.user.SimpSubscriptionMatcher
 *  org.springframework.messaging.simp.user.SimpUser
 *  org.springframework.messaging.simp.user.SimpUserRegistry
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.messaging;

import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.user.DestinationUserNameProvider;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpSubscriptionMatcher;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.util.Assert;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

public class DefaultSimpUserRegistry
implements SimpUserRegistry,
SmartApplicationListener {
    private int order = Integer.MAX_VALUE;
    private final Map<String, LocalSimpUser> users = new ConcurrentHashMap<String, LocalSimpUser>();
    private final Map<String, LocalSimpSession> sessions = new ConcurrentHashMap<String, LocalSimpSession>();
    private final Object sessionLock = new Object();

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return AbstractSubProtocolEvent.class.isAssignableFrom(eventType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onApplicationEvent(ApplicationEvent event) {
        String subscriptionId;
        LocalSimpSession session;
        AbstractSubProtocolEvent subProtocolEvent = (AbstractSubProtocolEvent)event;
        Message<byte[]> message = subProtocolEvent.getMessage();
        MessageHeaders headers = message.getHeaders();
        String sessionId = SimpMessageHeaderAccessor.getSessionId((Map)headers);
        Assert.state((sessionId != null ? 1 : 0) != 0, (String)"No session id");
        if (event instanceof SessionSubscribeEvent) {
            LocalSimpSession session2 = this.sessions.get(sessionId);
            if (session2 != null) {
                String id = SimpMessageHeaderAccessor.getSubscriptionId((Map)headers);
                String destination = SimpMessageHeaderAccessor.getDestination((Map)headers);
                if (id != null && destination != null) {
                    session2.addSubscription(id, destination);
                }
            }
        } else if (event instanceof SessionConnectedEvent) {
            Principal user = subProtocolEvent.getUser();
            if (user == null) {
                return;
            }
            String name = user.getName();
            if (user instanceof DestinationUserNameProvider) {
                name = ((DestinationUserNameProvider)user).getDestinationUserName();
            }
            Object destination = this.sessionLock;
            synchronized (destination) {
                LocalSimpUser simpUser = this.users.get(name);
                if (simpUser == null) {
                    simpUser = new LocalSimpUser(name, user);
                    this.users.put(name, simpUser);
                }
                LocalSimpSession session3 = new LocalSimpSession(sessionId, simpUser);
                simpUser.addSession(session3);
                this.sessions.put(sessionId, session3);
            }
        } else if (event instanceof SessionDisconnectEvent) {
            Object user = this.sessionLock;
            synchronized (user) {
                LocalSimpSession session4 = this.sessions.remove(sessionId);
                if (session4 != null) {
                    LocalSimpUser user2 = session4.getUser();
                    user2.removeSession(sessionId);
                    if (!user2.hasSessions()) {
                        this.users.remove(user2.getName());
                    }
                }
            }
        } else if (event instanceof SessionUnsubscribeEvent && (session = this.sessions.get(sessionId)) != null && (subscriptionId = SimpMessageHeaderAccessor.getSubscriptionId((Map)headers)) != null) {
            session.removeSubscription(subscriptionId);
        }
    }

    public boolean supportsSourceType(@Nullable Class<?> sourceType) {
        return true;
    }

    @Nullable
    public SimpUser getUser(String userName) {
        return this.users.get(userName);
    }

    public Set<SimpUser> getUsers() {
        return new HashSet<SimpUser>(this.users.values());
    }

    public int getUserCount() {
        return this.users.size();
    }

    public Set<SimpSubscription> findSubscriptions(SimpSubscriptionMatcher matcher) {
        HashSet<SimpSubscription> result = new HashSet<SimpSubscription>();
        for (LocalSimpSession session : this.sessions.values()) {
            for (SimpSubscription subscription : session.subscriptions.values()) {
                if (!matcher.match(subscription)) continue;
                result.add(subscription);
            }
        }
        return result;
    }

    public String toString() {
        return "users=" + this.users;
    }

    private static class LocalSimpSubscription
    implements SimpSubscription {
        private final String id;
        private final LocalSimpSession session;
        private final String destination;

        public LocalSimpSubscription(String id, String destination, LocalSimpSession session) {
            Assert.notNull((Object)id, (String)"Id must not be null");
            Assert.hasText((String)destination, (String)"Destination must not be empty");
            Assert.notNull((Object)session, (String)"Session must not be null");
            this.id = id;
            this.destination = destination;
            this.session = session;
        }

        public String getId() {
            return this.id;
        }

        public LocalSimpSession getSession() {
            return this.session;
        }

        public String getDestination() {
            return this.destination;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof SimpSubscription)) {
                return false;
            }
            SimpSubscription otherSubscription = (SimpSubscription)other;
            return this.getId().equals(otherSubscription.getId()) && this.getSession().getId().equals(otherSubscription.getSession().getId());
        }

        public int hashCode() {
            return this.getId().hashCode() * 31 + this.getSession().getId().hashCode();
        }

        public String toString() {
            return "destination=" + this.destination;
        }
    }

    private static class LocalSimpSession
    implements SimpSession {
        private final String id;
        private final LocalSimpUser user;
        private final Map<String, SimpSubscription> subscriptions = new ConcurrentHashMap<String, SimpSubscription>(4);

        public LocalSimpSession(String id, LocalSimpUser user) {
            Assert.notNull((Object)id, (String)"Id must not be null");
            Assert.notNull((Object)user, (String)"User must not be null");
            this.id = id;
            this.user = user;
        }

        public String getId() {
            return this.id;
        }

        public LocalSimpUser getUser() {
            return this.user;
        }

        public Set<SimpSubscription> getSubscriptions() {
            return new HashSet<SimpSubscription>(this.subscriptions.values());
        }

        void addSubscription(String id, String destination) {
            this.subscriptions.put(id, new LocalSimpSubscription(id, destination, this));
        }

        void removeSubscription(String id) {
            this.subscriptions.remove(id);
        }

        public boolean equals(@Nullable Object other) {
            return this == other || other instanceof SimpSubscription && this.getId().equals(((SimpSubscription)other).getId());
        }

        public int hashCode() {
            return this.getId().hashCode();
        }

        public String toString() {
            return "id=" + this.getId() + ", subscriptions=" + this.subscriptions;
        }
    }

    private static class LocalSimpUser
    implements SimpUser {
        private final String name;
        private final Principal user;
        private final Map<String, SimpSession> userSessions = new ConcurrentHashMap<String, SimpSession>(1);

        public LocalSimpUser(String userName, Principal user) {
            Assert.notNull((Object)userName, (String)"User name must not be null");
            this.name = userName;
            this.user = user;
        }

        public String getName() {
            return this.name;
        }

        @Nullable
        public Principal getPrincipal() {
            return this.user;
        }

        public boolean hasSessions() {
            return !this.userSessions.isEmpty();
        }

        @Nullable
        public SimpSession getSession(@Nullable String sessionId) {
            return sessionId != null ? this.userSessions.get(sessionId) : null;
        }

        public Set<SimpSession> getSessions() {
            return new HashSet<SimpSession>(this.userSessions.values());
        }

        void addSession(SimpSession session) {
            this.userSessions.put(session.getId(), session);
        }

        void removeSession(String sessionId) {
            this.userSessions.remove(sessionId);
        }

        public boolean equals(@Nullable Object other) {
            return this == other || other instanceof SimpUser && this.getName().equals(((SimpUser)other).getName());
        }

        public int hashCode() {
            return this.getName().hashCode();
        }

        public String toString() {
            return "name=" + this.getName() + ", sessions=" + this.userSessions;
        }
    }
}

