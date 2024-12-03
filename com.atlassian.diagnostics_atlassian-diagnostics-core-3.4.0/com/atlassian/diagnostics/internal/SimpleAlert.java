/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Alert
 *  com.atlassian.diagnostics.AlertBuilder
 *  com.atlassian.diagnostics.AlertTrigger
 *  com.atlassian.diagnostics.AlertTrigger$Builder
 *  com.atlassian.diagnostics.Issue
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.AlertBuilder;
import com.atlassian.diagnostics.AlertTrigger;
import com.atlassian.diagnostics.Issue;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleAlert
implements Alert {
    private static final Logger log = LoggerFactory.getLogger(SimpleAlert.class);
    private final Object details;
    private final long id;
    private final Issue issue;
    private final String nodeName;
    private final Instant timestamp;
    private final AlertTrigger trigger;

    SimpleAlert(AbstractBuilder<?> builder) {
        this.id = ((AbstractBuilder)builder).id;
        this.details = ((AbstractBuilder)builder).details;
        this.issue = ((AbstractBuilder)builder).issue;
        this.nodeName = ((AbstractBuilder)builder).nodeName;
        this.timestamp = ((AbstractBuilder)builder).timestamp;
        this.trigger = ((AbstractBuilder)builder).trigger == null ? new AlertTrigger.Builder().build() : ((AbstractBuilder)builder).trigger;
    }

    public long getId() {
        return this.id;
    }

    @Nonnull
    public Issue getIssue() {
        return this.issue;
    }

    @Nonnull
    public Optional<Object> getDetails() {
        return Optional.ofNullable(this.details);
    }

    @Nonnull
    public String getNodeName() {
        return this.nodeName;
    }

    @Nonnull
    public Instant getTimestamp() {
        return this.timestamp;
    }

    @Nonnull
    public AlertTrigger getTrigger() {
        return this.trigger;
    }

    public static class Builder
    extends AbstractBuilder<Builder> {
        public Builder(@Nonnull Issue issue, @Nonnull String nodeName) {
            super(issue, nodeName);
        }

        @Nonnull
        public SimpleAlert build() {
            return new SimpleAlert(this);
        }

        @Override
        @Nonnull
        protected Builder self() {
            return this;
        }
    }

    protected static abstract class AbstractBuilder<B extends AbstractBuilder<B>>
    implements AlertBuilder {
        private final Issue issue;
        private final String nodeName;
        private Object details;
        private long id;
        private Instant timestamp;
        private AlertTrigger trigger;

        protected AbstractBuilder(@Nonnull Issue issue, @Nonnull String nodeName) {
            this.issue = Objects.requireNonNull(issue, "issue");
            this.nodeName = Objects.requireNonNull(nodeName, "nodeName");
        }

        @Nonnull
        public B details(Object value) {
            this.details = value;
            return this.self();
        }

        @Nonnull
        public B detailsAsJson(String value) {
            try {
                this.details = this.issue.getJsonMapper().parseJson(value);
            }
            catch (Exception e) {
                log.warn("Failed to parse json for alert details relating to issue '{}' {}", (Object)this.issue.getId(), (Object)(log.isDebugEnabled() ? value : ""));
                this.details = null;
            }
            return this.self();
        }

        @Nonnull
        public B id(long value) {
            this.id = value;
            return this.self();
        }

        @Nonnull
        public B timestamp(@Nonnull Instant value) {
            this.timestamp = Objects.requireNonNull(value, "timestamp");
            return this.self();
        }

        @Nonnull
        public B trigger(@Nullable AlertTrigger value) {
            this.trigger = value;
            return this.self();
        }

        @Nonnull
        protected abstract B self();
    }
}

