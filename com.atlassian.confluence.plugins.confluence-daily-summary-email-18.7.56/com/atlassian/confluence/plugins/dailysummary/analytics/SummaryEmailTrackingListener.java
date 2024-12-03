/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.emailtracker.api.EmailReadEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.plugins.dailysummary.analytics;

import com.atlassian.confluence.plugins.dailysummary.analytics.SummaryEmailTrackBackEvent;
import com.atlassian.confluence.plugins.emailtracker.api.EmailReadEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

public class SummaryEmailTrackingListener {
    public static final String DAILY_SUMMARY = "daily-summary";
    private final EventPublisher eventPublisher;

    public SummaryEmailTrackingListener(@ComponentImport EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onEmailRead(EmailReadEvent event) {
        if (!DAILY_SUMMARY.equals(event.getAction())) {
            return;
        }
        String schedule = event.get("schedule");
        this.eventPublisher.publish((Object)new SummaryEmailTrackBackEvent(schedule));
    }
}

