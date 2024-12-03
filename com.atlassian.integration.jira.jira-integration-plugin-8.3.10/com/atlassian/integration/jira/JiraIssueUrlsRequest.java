/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.integration.jira;

import com.atlassian.integration.jira.AbstractJiraIssuesRequest;
import java.util.Set;
import javax.annotation.Nonnull;

public class JiraIssueUrlsRequest
extends AbstractJiraIssuesRequest {
    private JiraIssueUrlsRequest(Set<String> issueKeys, String entityKey) {
        super(issueKeys, entityKey);
    }

    public static class Builder
    extends AbstractJiraIssuesRequest.AbstractBuilder<Builder, JiraIssueUrlsRequest> {
        public Builder() {
        }

        public Builder(@Nonnull JiraIssueUrlsRequest request) {
            super(request);
        }

        @Override
        @Nonnull
        public JiraIssueUrlsRequest build() {
            return new JiraIssueUrlsRequest(this.buildIssueKeys(), this.entityKey);
        }

        @Override
        @Nonnull
        protected Builder self() {
            return this;
        }
    }
}

