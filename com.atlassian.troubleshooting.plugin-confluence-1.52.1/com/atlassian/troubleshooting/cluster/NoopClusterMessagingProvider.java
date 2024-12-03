/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.cluster;

import com.atlassian.troubleshooting.api.ClusterMessagingProvider;
import com.atlassian.troubleshooting.api.ListenerRegistration;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public class NoopClusterMessagingProvider
implements ClusterMessagingProvider {
    @Override
    public void sendMessage(@Nonnull String channel, @Nonnull String message) {
    }

    @Override
    public ListenerRegistration registerListener(@Nonnull String channel, @Nonnull Consumer<String> listener) {
        return new ListenerRegistration(){

            @Override
            public void unregister() {
            }
        };
    }
}

