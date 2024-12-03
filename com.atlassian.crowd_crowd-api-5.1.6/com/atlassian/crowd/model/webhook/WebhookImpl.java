/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.webhook.Webhook
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.webhook;

import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.webhook.Webhook;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.Nullable;

@Deprecated
public class WebhookImpl
implements Webhook,
Serializable {
    private Long id;
    private String endpointUrl;
    private Application application;
    @Nullable
    private String token;
    @Nullable
    private Date oldestFailureDate;
    private long failuresSinceLastSuccess;

    protected WebhookImpl() {
    }

    public WebhookImpl(Webhook other) {
        this.id = other.getId();
        this.endpointUrl = other.getEndpointUrl();
        this.application = other.getApplication();
        this.token = other.getToken();
        this.oldestFailureDate = other.getOldestFailureDate();
        this.failuresSinceLastSuccess = other.getFailuresSinceLastSuccess();
    }

    public void updateDetailsFrom(Webhook other) {
        this.token = other.getToken();
        this.oldestFailureDate = other.getOldestFailureDate();
        this.failuresSinceLastSuccess = other.getFailuresSinceLastSuccess();
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

    @Nullable
    public String getToken() {
        return this.token;
    }

    @Nullable
    public Date getOldestFailureDate() {
        return this.oldestFailureDate;
    }

    public long getFailuresSinceLastSuccess() {
        return this.failuresSinceLastSuccess;
    }

    private void setId(Long id) {
        this.id = id;
    }

    private void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    private void setToken(@Nullable String token) {
        this.token = token;
    }

    private void setOldestFailureDate(@Nullable Date oldestFailureDate) {
        this.oldestFailureDate = oldestFailureDate;
    }

    private void setFailuresSinceLastSuccess(long failuresSinceLastSuccess) {
        this.failuresSinceLastSuccess = failuresSinceLastSuccess;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        WebhookImpl webhook = (WebhookImpl)o;
        if (!this.application.equals(webhook.application)) {
            return false;
        }
        return this.endpointUrl.equals(webhook.endpointUrl);
    }

    public int hashCode() {
        int result = this.endpointUrl.hashCode();
        result = 31 * result + this.application.hashCode();
        return result;
    }
}

