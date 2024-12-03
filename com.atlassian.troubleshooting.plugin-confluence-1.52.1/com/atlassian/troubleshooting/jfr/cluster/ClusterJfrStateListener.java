/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.jfr.cluster;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.troubleshooting.api.ClusterMessagingService;
import com.atlassian.troubleshooting.api.ListenerRegistration;
import com.atlassian.troubleshooting.cluster.JsonSerialiser;
import com.atlassian.troubleshooting.jfr.domain.JfrSettings;
import com.atlassian.troubleshooting.jfr.event.JfrLocalStateChangedEvent;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public class ClusterJfrStateListener
implements LifecycleAware {
    public static final String JFR_CLUSTER_STATE_CHANNEL_NAME = "jfr_settings";
    private ListenerRegistration listenerRegistration;
    private final ClusterMessagingService clusterMessagingService;
    private final EventPublisher eventPublisher;
    private final JsonSerialiser jsonSerialiser;

    @Autowired
    public ClusterJfrStateListener(ClusterMessagingService clusterMessagingService, EventPublisher eventPublisher, JsonSerialiser jsonSerialiser) {
        this.clusterMessagingService = Objects.requireNonNull(clusterMessagingService);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.jsonSerialiser = Objects.requireNonNull(jsonSerialiser);
    }

    public void onStart() {
        this.listenerRegistration = this.registerListener();
    }

    public void onStop() {
        Optional.ofNullable(this.listenerRegistration).ifPresent(ListenerRegistration::unregister);
    }

    private ListenerRegistration registerListener() {
        return this.clusterMessagingService.registerListener(JFR_CLUSTER_STATE_CHANNEL_NAME, message -> {
            JfrSettings jfrSettings = this.jsonSerialiser.fromJson((String)message, JfrSettings.class);
            this.sendLocalStateChangedEvent(jfrSettings);
        });
    }

    private void sendLocalStateChangedEvent(JfrSettings jfrSettings) {
        boolean isJfrEnabled = jfrSettings.isEnabled();
        JfrLocalStateChangedEvent event = new JfrLocalStateChangedEvent(isJfrEnabled);
        this.eventPublisher.publish((Object)event);
    }
}

