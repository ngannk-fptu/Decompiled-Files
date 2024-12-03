/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.webhook.Webhook
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.model.webhook;

import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.webhook.Webhook;
import com.google.common.base.Preconditions;
import java.util.Date;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class WebhookTemplate
implements Webhook {
    private final Long id;
    private final String endpointUrl;
    private final Application application;
    @Nullable
    final String token;
    @Nullable
    private Date oldestFailureDate;
    private long failuresSinceLastSuccess;

    private WebhookTemplate(@Nullable Long id, Application application, String endpointUrl, @Nullable String token, @Nullable Date oldestFailureDate, long failuresSinceLastSuccess) {
        this.id = id;
        this.endpointUrl = endpointUrl;
        this.application = application;
        this.token = token;
        this.oldestFailureDate = oldestFailureDate;
        this.failuresSinceLastSuccess = failuresSinceLastSuccess;
    }

    public WebhookTemplate(Application application, String endpointUrl, @Nullable String token) {
        this(null, application, endpointUrl, token, null, 0L);
    }

    public WebhookTemplate(Webhook webhook) {
        this(webhook.getId(), webhook.getApplication(), webhook.getEndpointUrl(), webhook.getToken(), webhook.getOldestFailureDate(), webhook.getFailuresSinceLastSuccess());
    }

    @Nullable
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

    public void setOldestFailureDate(Date oldestFailureDate) {
        this.oldestFailureDate = (Date)Preconditions.checkNotNull((Object)oldestFailureDate);
    }

    public void resetOldestFailureDate() {
        this.oldestFailureDate = null;
    }

    public void setFailuresSinceLastSuccess(long failuresSinceLastSuccess) {
        this.failuresSinceLastSuccess = failuresSinceLastSuccess;
    }

    public void resetFailuresSinceLastSuccess() {
        this.failuresSinceLastSuccess = 0L;
    }

    public String toString() {
        return "WebhookTemplate{id=" + this.id + ", endpointUrl='" + this.endpointUrl + '\'' + ", applicationID=" + this.application.getId() + ", token=" + StringUtils.isNotEmpty((CharSequence)this.token) + ", oldestFailureDate=" + this.oldestFailureDate + ", failuresSinceLastSuccess=" + this.failuresSinceLastSuccess + '}';
    }
}

