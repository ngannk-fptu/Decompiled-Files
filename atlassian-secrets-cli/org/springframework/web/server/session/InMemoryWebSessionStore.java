/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.server.session;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.util.Assert;
import org.springframework.util.IdGenerator;
import org.springframework.util.JdkIdGenerator;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.WebSessionStore;
import reactor.core.publisher.Mono;

public class InMemoryWebSessionStore
implements WebSessionStore {
    private static final IdGenerator idGenerator = new JdkIdGenerator();
    private int maxSessions = 10000;
    private Clock clock = Clock.system(ZoneId.of("GMT"));
    private final Map<String, InMemoryWebSession> sessions = new ConcurrentHashMap<String, InMemoryWebSession>();
    private final ExpiredSessionChecker expiredSessionChecker = new ExpiredSessionChecker();

    public void setMaxSessions(int maxSessions) {
        this.maxSessions = maxSessions;
    }

    public int getMaxSessions() {
        return this.maxSessions;
    }

    public void setClock(Clock clock) {
        Assert.notNull((Object)clock, "Clock is required");
        this.clock = clock;
        this.removeExpiredSessions();
    }

    public Clock getClock() {
        return this.clock;
    }

    public Map<String, WebSession> getSessions() {
        return Collections.unmodifiableMap(this.sessions);
    }

    @Override
    public Mono<WebSession> createWebSession() {
        Instant now = this.clock.instant();
        this.expiredSessionChecker.checkIfNecessary(now);
        return Mono.fromSupplier(() -> new InMemoryWebSession(now));
    }

    @Override
    public Mono<WebSession> retrieveSession(String id) {
        Instant now = this.clock.instant();
        this.expiredSessionChecker.checkIfNecessary(now);
        InMemoryWebSession session = this.sessions.get(id);
        if (session == null) {
            return Mono.empty();
        }
        if (session.isExpired(now)) {
            this.sessions.remove(id);
            return Mono.empty();
        }
        session.updateLastAccessTime(now);
        return Mono.just((Object)session);
    }

    @Override
    public Mono<Void> removeSession(String id) {
        this.sessions.remove(id);
        return Mono.empty();
    }

    @Override
    public Mono<WebSession> updateLastAccessTime(WebSession session) {
        return Mono.fromSupplier(() -> {
            Assert.isInstanceOf(InMemoryWebSession.class, session);
            ((InMemoryWebSession)session).updateLastAccessTime(this.clock.instant());
            return session;
        });
    }

    public void removeExpiredSessions() {
        this.expiredSessionChecker.removeExpiredSessions(this.clock.instant());
    }

    private static enum State {
        NEW,
        STARTED,
        EXPIRED;

    }

    private class ExpiredSessionChecker {
        private static final int CHECK_PERIOD = 60000;
        private final ReentrantLock lock = new ReentrantLock();
        private Instant checkTime = InMemoryWebSessionStore.access$600(InMemoryWebSessionStore.this).instant().plus(60000L, ChronoUnit.MILLIS);

        private ExpiredSessionChecker() {
        }

        public void checkIfNecessary(Instant now) {
            if (this.checkTime.isBefore(now)) {
                this.removeExpiredSessions(now);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void removeExpiredSessions(Instant now) {
            if (InMemoryWebSessionStore.this.sessions.isEmpty()) {
                return;
            }
            if (this.lock.tryLock()) {
                try {
                    Iterator iterator = InMemoryWebSessionStore.this.sessions.values().iterator();
                    while (iterator.hasNext()) {
                        InMemoryWebSession session = (InMemoryWebSession)iterator.next();
                        if (!session.isExpired(now)) continue;
                        iterator.remove();
                        session.invalidate();
                    }
                }
                finally {
                    this.checkTime = now.plus(60000L, ChronoUnit.MILLIS);
                    this.lock.unlock();
                }
            }
        }
    }

    private class InMemoryWebSession
    implements WebSession {
        private final AtomicReference<String> id = new AtomicReference<String>(String.valueOf(InMemoryWebSessionStore.access$300().generateId()));
        private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();
        private final Instant creationTime;
        private volatile Instant lastAccessTime;
        private volatile Duration maxIdleTime = Duration.ofMinutes(30L);
        private final AtomicReference<State> state = new AtomicReference<State>(State.NEW);

        public InMemoryWebSession(Instant creationTime) {
            this.lastAccessTime = this.creationTime = creationTime;
        }

        @Override
        public String getId() {
            return this.id.get();
        }

        @Override
        public Map<String, Object> getAttributes() {
            return this.attributes;
        }

        @Override
        public Instant getCreationTime() {
            return this.creationTime;
        }

        @Override
        public Instant getLastAccessTime() {
            return this.lastAccessTime;
        }

        @Override
        public void setMaxIdleTime(Duration maxIdleTime) {
            this.maxIdleTime = maxIdleTime;
        }

        @Override
        public Duration getMaxIdleTime() {
            return this.maxIdleTime;
        }

        @Override
        public void start() {
            this.state.compareAndSet(State.NEW, State.STARTED);
        }

        @Override
        public boolean isStarted() {
            return this.state.get().equals((Object)State.STARTED) || !this.getAttributes().isEmpty();
        }

        @Override
        public Mono<Void> changeSessionId() {
            String currentId = this.id.get();
            InMemoryWebSessionStore.this.sessions.remove(currentId);
            String newId = String.valueOf(idGenerator.generateId());
            this.id.set(newId);
            InMemoryWebSessionStore.this.sessions.put(this.getId(), this);
            return Mono.empty();
        }

        @Override
        public Mono<Void> invalidate() {
            this.state.set(State.EXPIRED);
            this.getAttributes().clear();
            InMemoryWebSessionStore.this.sessions.remove(this.id.get());
            return Mono.empty();
        }

        @Override
        public Mono<Void> save() {
            if (InMemoryWebSessionStore.this.sessions.size() >= InMemoryWebSessionStore.this.maxSessions) {
                InMemoryWebSessionStore.this.expiredSessionChecker.removeExpiredSessions(InMemoryWebSessionStore.this.clock.instant());
                if (InMemoryWebSessionStore.this.sessions.size() >= InMemoryWebSessionStore.this.maxSessions) {
                    return Mono.error((Throwable)new IllegalStateException("Max sessions limit reached: " + InMemoryWebSessionStore.this.sessions.size()));
                }
            }
            if (!this.getAttributes().isEmpty()) {
                this.state.compareAndSet(State.NEW, State.STARTED);
            }
            InMemoryWebSessionStore.this.sessions.put(this.getId(), this);
            return Mono.empty();
        }

        @Override
        public boolean isExpired() {
            return this.isExpired(InMemoryWebSessionStore.this.clock.instant());
        }

        private boolean isExpired(Instant now) {
            if (this.state.get().equals((Object)State.EXPIRED)) {
                return true;
            }
            if (this.checkExpired(now)) {
                this.state.set(State.EXPIRED);
                return true;
            }
            return false;
        }

        private boolean checkExpired(Instant currentTime) {
            return this.isStarted() && !this.maxIdleTime.isNegative() && currentTime.minus(this.maxIdleTime).isAfter(this.lastAccessTime);
        }

        private void updateLastAccessTime(Instant currentTime) {
            this.lastAccessTime = currentTime;
        }
    }
}

