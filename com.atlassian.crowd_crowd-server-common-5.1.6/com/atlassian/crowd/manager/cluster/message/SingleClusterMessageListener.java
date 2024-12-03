/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.service.cluster.ClusterMessageListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.cluster.message;

import com.atlassian.crowd.service.cluster.ClusterMessageListener;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleClusterMessageListener
implements ClusterMessageListener {
    private static final Logger log = LoggerFactory.getLogger(SingleClusterMessageListener.class);
    private final String channel;
    private final String message;
    private final Runnable callback;

    public SingleClusterMessageListener(String channel, String message, Runnable callback) {
        this.channel = channel;
        this.message = message;
        this.callback = callback;
    }

    public void handleMessage(String channel, String message) {
        if (Objects.equals(this.channel, channel) && Objects.equals(this.message, message)) {
            this.callback.run();
        } else {
            log.warn("Received unknown cluster message {} on channel {}, ignoring", (Object)message, (Object)channel);
        }
    }

    public String getChannel() {
        return this.channel;
    }

    public String getMessage() {
        return this.message;
    }
}

