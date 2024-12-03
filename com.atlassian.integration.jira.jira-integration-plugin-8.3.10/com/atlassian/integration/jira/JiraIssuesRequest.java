/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  javax.annotation.Nonnull
 */
package com.atlassian.integration.jira;

import com.atlassian.integration.jira.AbstractJiraIssuesRequest;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.annotation.Nonnull;

public class JiraIssuesRequest
extends AbstractJiraIssuesRequest {
    private final Set<String> fields;
    private final int minimum;
    private final boolean showErrors;

    public JiraIssuesRequest(Builder builder) {
        super(builder.buildIssueKeys(), builder.entityKey);
        this.fields = builder.fields.build();
        this.minimum = builder.minimum;
        this.showErrors = builder.showErrors;
    }

    @Nonnull
    public Set<String> getFields() {
        return this.fields;
    }

    public int getMinimum() {
        return this.minimum;
    }

    public boolean hasMinimum() {
        return this.minimum != 0;
    }

    public boolean showErrors() {
        return this.showErrors;
    }

    public static class Builder
    extends AbstractJiraIssuesRequest.AbstractBuilder<Builder, JiraIssuesRequest> {
        private final ImmutableSet.Builder<String> fields;
        private int minimum;
        private boolean showErrors;

        public Builder() {
            this.fields = ImmutableSet.builder();
        }

        public Builder(@Nonnull JiraIssuesRequest request) {
            super(request);
            this.fields = ImmutableSet.builder().addAll(request.getFields());
            this.minimum = request.getMinimum();
            this.showErrors = request.showErrors();
        }

        @Override
        @Nonnull
        public JiraIssuesRequest build() {
            return new JiraIssuesRequest(this);
        }

        @Nonnull
        public Builder field(@Nonnull String value, String ... values) {
            this.fields.add((Object)value).add((Object[])values);
            return this.self();
        }

        @Nonnull
        public Builder fields(@Nonnull Iterable<String> values) {
            this.fields.addAll(values);
            return this.self();
        }

        @Nonnull
        public Builder minimum(int value) {
            this.minimum = Math.max(0, value);
            return this.self();
        }

        @Nonnull
        public Builder showErrors(boolean value) {
            this.showErrors = value;
            return this.self();
        }

        @Override
        @Nonnull
        protected Builder self() {
            return this;
        }
    }
}

