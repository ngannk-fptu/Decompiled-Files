/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Nullable
 */
package com.atlassian.plugin.webresource.analytics;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.webresource.analytics.events.RequestServingCacheEvent;
import java.util.Optional;
import java.util.SplittableRandom;
import javax.annotation.Nullable;

public class EventFiringHelper {
    private static final SplittableRandom splittableRandom = new SplittableRandom();
    private static final int THROTTLE_LOWER_BOUND = 0;
    private static final int THROTTLE_SERVER_REQUEST_UPPER_BOUND = 20;

    private EventFiringHelper() {
    }

    public static void publishIfEventPublisherNonNull(@Nullable EventPublisher optionalEventPublisher, Object event) {
        Optional.ofNullable(optionalEventPublisher).ifPresent(eventPublisher -> eventPublisher.publish(event));
    }

    public static void publishedThrottledEventIfEventPublisherNonNull(@Nullable EventPublisher optionalEventPublisher, RequestServingCacheEvent event) {
        if (splittableRandom.nextInt(0, 20) == 0) {
            EventFiringHelper.publishIfEventPublisherNonNull(optionalEventPublisher, event);
        }
    }
}

