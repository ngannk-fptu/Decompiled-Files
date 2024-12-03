/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.util.JodaTimeUtils
 *  com.atlassian.event.config.ListenerHandlersConfiguration
 *  com.atlassian.event.internal.AnnotatedMethodsListenerHandler
 *  com.atlassian.event.legacy.LegacyListenerHandler
 *  com.atlassian.event.spi.ListenerHandler
 *  com.atlassian.event.spi.ListenerInvoker
 *  com.atlassian.plugin.event.PluginEventListener
 *  org.joda.time.Duration
 */
package com.atlassian.confluence.event;

import com.atlassian.confluence.api.util.JodaTimeUtils;
import com.atlassian.confluence.server.ApplicationState;
import com.atlassian.confluence.server.ApplicationStatusService;
import com.atlassian.confluence.util.profiling.DurationThresholdWarningTimingHelper;
import com.atlassian.confluence.util.profiling.DurationThresholdWarningTimingHelperFactory;
import com.atlassian.event.config.ListenerHandlersConfiguration;
import com.atlassian.event.internal.AnnotatedMethodsListenerHandler;
import com.atlassian.event.legacy.LegacyListenerHandler;
import com.atlassian.event.spi.ListenerHandler;
import com.atlassian.event.spi.ListenerInvoker;
import com.atlassian.plugin.event.PluginEventListener;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.joda.time.Duration;

public class ConfluenceListenerHandlersConfiguration
implements ListenerHandlersConfiguration {
    public static final String EVENT_LISTENER_WARN_THRESHOLD_SYSTEM_PROPERTY = "confluence.events.listenerWarnThresholdMillis";
    @Deprecated(forRemoval=true)
    public static final Duration EVENT_LISTENER_WARN_THRESHOLD_DEFAULT = null;
    private final DurationThresholdWarningTimingHelper timingHelper;
    private final ApplicationStatusService applicationStatusService;

    public ConfluenceListenerHandlersConfiguration(ApplicationStatusService applicationStatusService) {
        this.applicationStatusService = applicationStatusService;
        this.timingHelper = DurationThresholdWarningTimingHelperFactory.createFromSystemProperty(EVENT_LISTENER_WARN_THRESHOLD_SYSTEM_PROPERTY, JodaTimeUtils.convert((Duration)EVENT_LISTENER_WARN_THRESHOLD_DEFAULT));
    }

    public List<ListenerHandler> getListenerHandlers() {
        return Arrays.asList(new TimingListenerHandler((ListenerHandler)new LegacyListenerHandler()), new TimingListenerHandler((ListenerHandler)new AnnotatedMethodsListenerHandler()), new TimingListenerHandler((ListenerHandler)new AnnotatedMethodsListenerHandler(PluginEventListener.class)));
    }

    private class TimedListenerInvoker
    implements ListenerInvoker {
        private final ListenerInvoker listenerInvoker;
        private final Object listener;

        TimedListenerInvoker(ListenerInvoker listenerInvoker, Object listener) {
            this.listenerInvoker = listenerInvoker;
            this.listener = listener;
        }

        public Set<Class<?>> getSupportedEventTypes() {
            return this.listenerInvoker.getSupportedEventTypes();
        }

        public void invoke(Object event) {
            DurationThresholdWarningTimingHelper.Timer timer = ConfluenceListenerHandlersConfiguration.this.timingHelper.newDescribedTimer(this.descriptionOf(this.listener, event), this.areWarningsDisabled()).start();
            try {
                this.listenerInvoker.invoke(event);
            }
            finally {
                timer.stopAndCheckTiming();
            }
        }

        public boolean supportAsynchronousEvents() {
            return this.listenerInvoker.supportAsynchronousEvents();
        }

        public Optional<String> getScope() {
            return this.listenerInvoker.getScope();
        }

        public int hashCode() {
            return this.listenerInvoker.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof TimedListenerInvoker)) {
                return false;
            }
            TimedListenerInvoker that = (TimedListenerInvoker)obj;
            return this.listenerInvoker.equals(that.listenerInvoker);
        }

        public String toString() {
            return this.listenerInvoker.toString() + " (timed)";
        }

        private boolean areWarningsDisabled() {
            return ConfluenceListenerHandlersConfiguration.this.applicationStatusService.getState() != ApplicationState.RUNNING;
        }

        private Supplier<String> descriptionOf(Object listener, Object event) {
            return () -> "publishing event " + event + " to listener " + listener.getClass();
        }
    }

    private class TimingListenerHandler
    implements ListenerHandler {
        private final ListenerHandler delegate;

        TimingListenerHandler(ListenerHandler delegate) {
            this.delegate = delegate;
        }

        public List<ListenerInvoker> getInvokers(Object listener) {
            return this.delegate.getInvokers(listener).stream().map(listenerInvoker -> new TimedListenerInvoker((ListenerInvoker)listenerInvoker, listener)).collect(Collectors.toList());
        }
    }
}

