/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.webhooks.WebhookEvent
 *  com.atlassian.webhooks.WebhookEventProvider
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.internal.webhooks;

import com.atlassian.confluence.internal.webhooks.ApplicationWebhookEvent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.WebhookEventProvider;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WebhookEventProvider.class})
public class ApplicationWebhookEventProvider
implements WebhookEventProvider {
    private static final List<WebhookEvent> EVENTS = ImmutableList.copyOf((Object[])ApplicationWebhookEvent.values());

    @Nullable
    public WebhookEvent forId(String id) {
        return ApplicationWebhookEvent.forId(id);
    }

    public List<WebhookEvent> getEvents() {
        return EVENTS;
    }

    public int getWeight() {
        return 10;
    }
}

