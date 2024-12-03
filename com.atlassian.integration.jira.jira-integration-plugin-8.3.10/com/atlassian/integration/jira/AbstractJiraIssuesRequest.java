/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.integration.jira;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractJiraIssuesRequest {
    private final String entityKey;
    private final Set<String> issueKeys;

    protected AbstractJiraIssuesRequest(Set<String> issueKeys, String entityKey) {
        this.entityKey = entityKey;
        this.issueKeys = issueKeys;
    }

    @Nullable
    public String getEntityKey() {
        return this.entityKey;
    }

    @Nonnull
    public Set<String> getIssueKeys() {
        return this.issueKeys;
    }

    public static abstract class AbstractBuilder<B extends AbstractBuilder<B, R>, R extends AbstractJiraIssuesRequest> {
        protected final ImmutableSet.Builder<String> issueKeys = ImmutableSet.builder();
        protected String entityKey;

        protected AbstractBuilder() {
        }

        protected AbstractBuilder(@Nonnull R request) {
            this();
            this.entityKey = ((AbstractJiraIssuesRequest)Preconditions.checkNotNull(request, (Object)"request")).getEntityKey();
            this.issueKeys.addAll(((AbstractJiraIssuesRequest)request).getIssueKeys());
        }

        @Nonnull
        public abstract R build();

        @Nonnull
        public B entityKey(@Nullable String value) {
            this.entityKey = Strings.emptyToNull((String)value);
            return this.self();
        }

        @Nonnull
        public B issueKey(@Nonnull String value, String ... values) {
            this.issueKeys.add((Object)value).add((Object[])values);
            return this.self();
        }

        @Nonnull
        public B issueKeys(@Nonnull Iterable<String> values) {
            this.issueKeys.addAll(values);
            return this.self();
        }

        @Nonnull
        protected Set<String> buildIssueKeys() {
            ImmutableSet keys = this.issueKeys.build();
            if (keys.isEmpty()) {
                throw new IllegalStateException("At least one issue key is required");
            }
            return keys;
        }

        @Nonnull
        protected abstract B self();
    }
}

