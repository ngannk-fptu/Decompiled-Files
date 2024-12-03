/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.extra.widgetconnector.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.extra.widgetconnector.WidgetRenderer;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.widgetconnector.renderer.match")
public class RendererMatchAnalyticsEvent {
    private final String serviceName;
    private final WidgetRenderer widgetRenderer;

    public RendererMatchAnalyticsEvent(WidgetRenderer widgetRenderer) {
        this.widgetRenderer = widgetRenderer;
        this.serviceName = widgetRenderer.getServiceName();
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public WidgetRenderer getWidgetRenderer() {
        return this.widgetRenderer;
    }
}

