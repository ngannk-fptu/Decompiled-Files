/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 *  lombok.NonNull
 */
package com.atlassian.migration.agent.service.check.app.webhook;

import com.atlassian.cmpt.check.base.CheckContext;
import java.util.Set;
import lombok.NonNull;

public class AppWebhookEndpointCheckContext
implements CheckContext {
    public final Set<String> appKeys;
    public final String cloudId;

    public AppWebhookEndpointCheckContext(@NonNull String cloudId, @NonNull Set<String> appKeys) {
        if (cloudId == null) {
            throw new NullPointerException("cloudId is marked non-null but is null");
        }
        if (appKeys == null) {
            throw new NullPointerException("appKeys is marked non-null but is null");
        }
        this.appKeys = appKeys;
        this.cloudId = cloudId;
    }
}

