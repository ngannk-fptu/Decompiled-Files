/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.confluence.api.util.JodaTimeUtils
 *  com.atlassian.event.api.EventPublisher
 *  net.jcip.annotations.ThreadSafe
 *  org.joda.time.Duration
 */
package com.atlassian.confluence.event;

import com.atlassian.annotations.Internal;
import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.confluence.api.util.JodaTimeUtils;
import com.atlassian.confluence.util.profiling.DurationThresholdWarningTimingHelper;
import com.atlassian.confluence.util.profiling.DurationThresholdWarningTimingHelperFactory;
import com.atlassian.event.api.EventPublisher;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import net.jcip.annotations.ThreadSafe;
import org.joda.time.Duration;

@ThreadSafe
@Internal
public class TimingEventPublisher
implements EventPublisher {
    public static final String EVENT_PUBLISH_WARN_THRESHOLD_SYSTEM_PROPERTY = "confluence.events.publishWarnThresholdMillis";
    public static final Duration EVENT_PUBLISH_WARN_THRESHOLD_DEFAULT = Duration.millis((long)5000L);
    private final DurationThresholdWarningTimingHelper timingHelper;
    private final EventPublisher delegate;
    private final AtomicBoolean applicationStarted = new AtomicBoolean(false);

    public static TimingEventPublisher create(EventPublisher delegate) {
        return new TimingEventPublisher(DurationThresholdWarningTimingHelperFactory.createFromSystemProperty(EVENT_PUBLISH_WARN_THRESHOLD_SYSTEM_PROPERTY, JodaTimeUtils.convert((Duration)EVENT_PUBLISH_WARN_THRESHOLD_DEFAULT)), delegate);
    }

    private TimingEventPublisher(DurationThresholdWarningTimingHelper timingHelper, EventPublisher delegate) {
        this.timingHelper = timingHelper;
        this.delegate = delegate;
    }

    public void register(Object listener) {
        this.delegate.register(listener);
    }

    public void unregister(Object listener) {
        this.delegate.unregister(listener);
    }

    public void unregisterAll() {
        this.delegate.unregisterAll();
    }

    public void publish(Object event) {
        DurationThresholdWarningTimingHelper.Timer timer = this.timingHelper.newDescribedTimer(this.descriptionOf(event), this.areWarningsDisabled()).start();
        try {
            this.delegate.publish(event);
        }
        finally {
            timer.stopAndCheckTiming();
        }
        this.applicationStarted.compareAndSet(false, event instanceof ApplicationStartedEvent);
    }

    private Supplier<String> descriptionOf(Object event) {
        return () -> "publishing event " + event;
    }

    private boolean areWarningsDisabled() {
        return !this.applicationStarted.get();
    }
}

