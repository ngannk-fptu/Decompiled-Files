/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.cluster;

import com.atlassian.troubleshooting.api.ClusterMessagingProvider;
import com.atlassian.troubleshooting.api.ClusterMessagingService;
import com.atlassian.troubleshooting.api.ListenerRegistration;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.beans.factory.annotation.Autowired;

@ParametersAreNonnullByDefault
public class DefaultClusterMessagingService
implements ClusterMessagingService {
    private final ClusterMessagingProvider clusterMessagingProvider;

    @Autowired
    public DefaultClusterMessagingService(ClusterMessagingProvider clusterMessagingProvider) {
        this.clusterMessagingProvider = clusterMessagingProvider;
    }

    @Override
    public void sendMessage(@Nonnull String channel, @Nonnull String message) {
        this.checkChannelNameLength(channel);
        if (message.length() > 200) {
            throw new IllegalArgumentException(String.format("The message '%s' is too long. maximum length is %d.", message, 200));
        }
        this.clusterMessagingProvider.sendMessage(channel, message);
    }

    @Override
    public ListenerRegistration registerListener(@Nonnull String channel, @Nonnull Consumer<String> listener) {
        this.checkChannelNameLength(channel);
        return this.clusterMessagingProvider.registerListener(channel, listener);
    }

    private void checkChannelNameLength(String name) {
        if (name.length() > 20) {
            throw new IllegalArgumentException(String.format("The channel name '%s' is too large. maximum length is %d.", name, 20));
        }
    }
}

