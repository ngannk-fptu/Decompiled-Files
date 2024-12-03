/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  javax.annotation.Nonnull
 */
package com.atlassian.internal.integration.jira.request;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.internal.integration.jira.request.AbstractJiraPagedRequest;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ProjectIssueTypeRequest
extends AbstractJiraPagedRequest {
    private final ApplicationId applicationId;
    private final String project;

    private ProjectIssueTypeRequest(Builder builder) {
        super(builder);
        this.applicationId = builder.applicationId;
        this.project = builder.project;
    }

    @Nonnull
    public ApplicationId getApplicationId() {
        return this.applicationId;
    }

    @Nonnull
    public String getProject() {
        return this.project;
    }

    public static class Builder
    extends AbstractJiraPagedRequest.AbstractBuilder<Builder, ProjectIssueTypeRequest> {
        private ApplicationId applicationId;
        private String project;

        public Builder(@Nonnull ApplicationId applicationId) {
            this.applicationId = Objects.requireNonNull(applicationId, "applicationId");
        }

        @Override
        @Nonnull
        public ProjectIssueTypeRequest build() {
            return new ProjectIssueTypeRequest(this);
        }

        @Nonnull
        public Builder project(@Nonnull String project) {
            this.project = Objects.requireNonNull(project, "project");
            return this.self();
        }

        @Override
        @Nonnull
        protected Builder self() {
            return this;
        }
    }
}

