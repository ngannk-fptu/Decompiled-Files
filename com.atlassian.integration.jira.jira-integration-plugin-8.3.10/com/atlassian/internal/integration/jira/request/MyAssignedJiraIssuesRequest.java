/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  javax.annotation.Nonnull
 */
package com.atlassian.internal.integration.jira.request;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.internal.integration.jira.request.AbstractJiraPagedRequest;
import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

public class MyAssignedJiraIssuesRequest
extends AbstractJiraPagedRequest {
    private final ApplicationId applicationId;
    private final Set<String> fields;

    private MyAssignedJiraIssuesRequest(Builder builder) {
        super(builder);
        this.applicationId = builder.applicationId;
        this.fields = builder.fields.build();
    }

    @Nonnull
    public ApplicationId getApplicationId() {
        return this.applicationId;
    }

    @Nonnull
    public Set<String> getFields() {
        return this.fields;
    }

    public static class Builder
    extends AbstractJiraPagedRequest.AbstractBuilder<Builder, MyAssignedJiraIssuesRequest> {
        private final ApplicationId applicationId;
        private final ImmutableSet.Builder<String> fields;

        public Builder(@Nonnull ApplicationId applicationId) {
            this.applicationId = Objects.requireNonNull(applicationId, "applicationId");
            this.fields = ImmutableSet.builder();
        }

        @Override
        @Nonnull
        public MyAssignedJiraIssuesRequest build() {
            return new MyAssignedJiraIssuesRequest(this);
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

        @Override
        @Nonnull
        protected Builder self() {
            return this;
        }
    }
}

