/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.webhook.Webhook
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.model.webhook;

import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ImmutableApplication;
import com.atlassian.crowd.model.webhook.Webhook;
import com.google.common.base.MoreObjects;
import java.util.Date;
import java.util.Objects;

public class ImmutableWebhook
implements Webhook {
    private final Long id;
    private final String endpointUrl;
    private final ImmutableApplication application;
    private final String token;
    private final Date oldestFailureDate;
    private final long failuresSinceLastSuccess;

    protected ImmutableWebhook(Long id, String endpointUrl, ImmutableApplication application, String token, Date oldestFailureDate, long failuresSinceLastSuccess) {
        this.id = id;
        this.endpointUrl = endpointUrl;
        this.application = application;
        this.token = token;
        this.oldestFailureDate = oldestFailureDate;
        this.failuresSinceLastSuccess = failuresSinceLastSuccess;
    }

    public Long getId() {
        return this.id;
    }

    public String getEndpointUrl() {
        return this.endpointUrl;
    }

    public Application getApplication() {
        return this.application;
    }

    public String getToken() {
        return this.token;
    }

    public Date getOldestFailureDate() {
        return this.oldestFailureDate;
    }

    public long getFailuresSinceLastSuccess() {
        return this.failuresSinceLastSuccess;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Webhook data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImmutableWebhook that = (ImmutableWebhook)o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getEndpointUrl(), that.getEndpointUrl()) && Objects.equals(this.getApplication(), that.getApplication()) && Objects.equals(this.getToken(), that.getToken()) && Objects.equals(this.getOldestFailureDate(), that.getOldestFailureDate()) && Objects.equals(this.getFailuresSinceLastSuccess(), that.getFailuresSinceLastSuccess());
    }

    public int hashCode() {
        return Objects.hash(this.getId(), this.getEndpointUrl(), this.getApplication(), this.getToken(), this.getOldestFailureDate(), this.getFailuresSinceLastSuccess());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.getId()).add("endpointUrl", (Object)this.getEndpointUrl()).add("application", (Object)this.getApplication()).add("token", (Object)this.getToken()).add("oldestFailureDate", (Object)this.getOldestFailureDate()).add("failuresSinceLastSuccess", this.getFailuresSinceLastSuccess()).toString();
    }

    public static final class Builder {
        private Long id;
        private String endpointUrl;
        private ImmutableApplication application;
        private String token;
        private Date oldestFailureDate;
        private long failuresSinceLastSuccess;

        private Builder() {
        }

        private Builder(Webhook webhook) {
            this.id = webhook.getId();
            this.endpointUrl = webhook.getEndpointUrl();
            this.token = webhook.getToken();
            this.oldestFailureDate = webhook.getOldestFailureDate();
            this.failuresSinceLastSuccess = webhook.getFailuresSinceLastSuccess();
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setEndpointUrl(String endpointUrl) {
            this.endpointUrl = endpointUrl;
            return this;
        }

        public Builder setApplication(ImmutableApplication application) {
            this.application = application;
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder setOldestFailureDate(Date oldestFailureDate) {
            this.oldestFailureDate = oldestFailureDate;
            return this;
        }

        public Builder setFailuresSinceLastSuccess(long failuresSinceLastSuccess) {
            this.failuresSinceLastSuccess = failuresSinceLastSuccess;
            return this;
        }

        public ImmutableWebhook build() {
            return new ImmutableWebhook(this.id, this.endpointUrl, this.application, this.token, this.oldestFailureDate, this.failuresSinceLastSuccess);
        }
    }
}

