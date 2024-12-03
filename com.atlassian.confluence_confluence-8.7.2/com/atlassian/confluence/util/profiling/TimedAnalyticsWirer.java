/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.events.AnalyticsPluginReadyEvent
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.analytics.api.events.AnalyticsPluginReadyEvent;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.impl.profiling.TimedAnalyticsImpl;
import com.atlassian.confluence.util.ConnectableConsumer;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class TimedAnalyticsWirer {
    private static final ConnectableConsumer<TimedAnalyticsImpl.TimedAnalyticsEvent> consumer = new ConnectableConsumer();
    static final TimedAnalyticsImpl INSTANCE = new TimedAnalyticsImpl(consumer);
    private final EventPublisher eventPublisher;

    public TimedAnalyticsWirer(EventPublisher eventPublisher) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @EventListener
    public void onAnalyticsReady(AnalyticsPluginReadyEvent ev) {
        consumer.connect(arg_0 -> ((EventPublisher)this.eventPublisher).publish(arg_0));
    }
}

