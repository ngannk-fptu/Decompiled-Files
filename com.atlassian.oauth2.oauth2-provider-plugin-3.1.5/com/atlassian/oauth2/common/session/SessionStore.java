/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpSession
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.common.session;

import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionStore<T extends Serializable> {
    private static final Logger logger = LoggerFactory.getLogger(SessionStore.class);
    private final String attributePrefix;
    private final Clock clock;
    private final Duration entryLifetime;

    public SessionStore(@Nonnull String attributePrefix, @Nonnull Clock clock, @Nonnull Duration entryLifetime) {
        this.attributePrefix = attributePrefix;
        this.clock = clock;
        this.entryLifetime = entryLifetime;
    }

    public void store(@Nonnull HttpSession session, @Nonnull String id, T data) throws IllegalArgumentException {
        this.store(session, id, Objects::isNull, data);
    }

    public void store(@Nonnull HttpSession session, @Nonnull String id, @Nonnull Predicate<T> predicate, T data) throws IllegalArgumentException {
        String attribute = this.attributePrefix + id;
        Entry existing = (Entry)session.getAttribute(attribute);
        Preconditions.checkArgument((boolean)predicate.test(existing == null ? null : (Serializable)existing.value));
        session.setAttribute(attribute, new Entry<T>(this.clock.instant(), data));
    }

    public void removeIfPresent(@Nonnull HttpSession session, @Nonnull String id) {
        session.removeAttribute(this.attributePrefix + id);
    }

    public Optional<T> remove(@Nonnull HttpSession session, @Nonnull String id) {
        String attribute = this.attributePrefix + id;
        Entry existing = (Entry)session.getAttribute(attribute);
        session.removeAttribute(attribute);
        if (existing == null) {
            logger.debug("Attribute [{}] wasn't present in the session", (Object)attribute);
            return Optional.empty();
        }
        if (existing.creationTime.plus(this.entryLifetime).isBefore(this.clock.instant())) {
            logger.debug("Entry {} already expired", (Object)id);
            return Optional.empty();
        }
        return Optional.ofNullable(existing.value);
    }

    private static class Entry<T>
    implements Serializable {
        private static final long serialVersionUID = 2256246906235620948L;
        private final Instant creationTime;
        private final T value;

        public Entry(Instant creationTime, T value) {
            this.creationTime = creationTime;
            this.value = value;
        }
    }
}

