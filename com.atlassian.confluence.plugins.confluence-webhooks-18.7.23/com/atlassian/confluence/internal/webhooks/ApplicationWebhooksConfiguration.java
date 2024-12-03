/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.webhooks.WebhooksConfiguration
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.internal.webhooks;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.webhooks.WebhooksConfiguration;
import org.springframework.stereotype.Component;

@Component(value="webhooksConfiguration")
@ExportAsService(value={WebhooksConfiguration.class})
public class ApplicationWebhooksConfiguration
implements WebhooksConfiguration {
    public boolean isStatisticsEnabled() {
        return true;
    }

    public boolean isInvocationHistoryEnabled() {
        return true;
    }
}

