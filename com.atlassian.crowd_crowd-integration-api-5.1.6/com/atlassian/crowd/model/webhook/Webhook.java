/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.webhook;

import com.atlassian.crowd.model.application.Application;
import java.util.Date;
import javax.annotation.Nullable;

public interface Webhook {
    public Long getId();

    public String getEndpointUrl();

    public Application getApplication();

    @Nullable
    public String getToken();

    @Nullable
    public Date getOldestFailureDate();

    public long getFailuresSinceLastSuccess();
}

