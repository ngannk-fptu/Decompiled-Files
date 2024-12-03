/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.cluster.ClusterEvent
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapper
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.troubleshooting.api.ClusterMessagingProvider;
import com.atlassian.troubleshooting.api.ListenerRegistration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.beans.factory.annotation.Autowired;

@ParametersAreNonnullByDefault
public class ConfluenceClusterMessagingProvider
implements ClusterMessagingProvider,
LifecycleAware {
    private final EventPublisher eventPublisher;
    private final Map<String, Map<ConfluenceListenerRegistration, Consumer<String>>> listeners = new HashMap<String, Map<ConfluenceListenerRegistration, Consumer<String>>>();

    @Autowired
    public ConfluenceClusterMessagingProvider(EventPublisher eventPublisher) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public void onStart() {
        this.eventPublisher.register((Object)this);
    }

    public void onStop() {
        this.eventPublisher.unregister((Object)this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventListener
    public void onATSTClusterMessage(ClusterEventWrapper clusterEventWrapper) {
        Event wrappedEvent = clusterEventWrapper.getEvent();
        if (wrappedEvent instanceof ATSTClusterMessage) {
            ATSTClusterMessage event = (ATSTClusterMessage)wrappedEvent;
            Map<String, Map<ConfluenceListenerRegistration, Consumer<String>>> map = this.listeners;
            synchronized (map) {
                Map<ConfluenceListenerRegistration, Consumer<String>> listenersForChannel = this.listeners.get(event.channel);
                if (listenersForChannel != null) {
                    listenersForChannel.values().forEach(c -> c.accept(event.getMessage()));
                }
            }
        }
    }

    @Override
    public void sendMessage(@Nonnull String channel, @Nonnull String message) {
        this.eventPublisher.publish((Object)new ATSTClusterMessage(this, channel, message));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ListenerRegistration registerListener(@Nonnull String channel, @Nonnull Consumer<String> listener) {
        Map<String, Map<ConfluenceListenerRegistration, Consumer<String>>> map = this.listeners;
        synchronized (map) {
            Map listenersForChannel = this.listeners.computeIfAbsent(channel, key -> new HashMap());
            ConfluenceListenerRegistration key2 = new ConfluenceListenerRegistration();
            listenersForChannel.put(key2, listener);
            return key2;
        }
    }

    private class ConfluenceListenerRegistration
    implements ListenerRegistration {
        private ConfluenceListenerRegistration() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void unregister() {
            Map map = ConfluenceClusterMessagingProvider.this.listeners;
            synchronized (map) {
                ConfluenceClusterMessagingProvider.this.listeners.values().stream().forEach(m -> {
                    Consumer cfr_ignored_0 = (Consumer)m.remove(this);
                });
            }
        }
    }

    public static class ATSTClusterMessage
    extends ConfluenceEvent
    implements ClusterEvent {
        private final String channel;
        private final String message;

        public ATSTClusterMessage(Object src, String channel, String message) {
            super(src);
            this.channel = Objects.requireNonNull(channel);
            this.message = Objects.requireNonNull(message);
        }

        public String getChannel() {
            return this.channel;
        }

        public String getMessage() {
            return this.message;
        }
    }
}

