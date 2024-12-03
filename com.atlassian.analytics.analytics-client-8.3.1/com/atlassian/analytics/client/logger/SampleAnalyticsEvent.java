/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.logger;

import com.atlassian.analytics.event.RawEvent;

public class SampleAnalyticsEvent
extends RawEvent {
    private final String helpMessage;

    public SampleAnalyticsEvent(RawEvent event, String helpMessage) {
        super(event.getName(), event.getServer(), event.getProduct(), event.getSubProduct(), event.getVersion(), event.getUser(), event.getSession(), event.getClientTime(), event.getReceivedTime(), event.getSen(), event.getSourceIP(), event.getAtlPath(), event.getAppAccess(), event.getRequestCorrelationId(), event.getProperties());
        this.helpMessage = helpMessage;
    }

    public String getHelpMessage() {
        return this.helpMessage;
    }
}

