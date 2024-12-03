/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.api;

import com.atlassian.troubleshooting.api.ListenerRegistration;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public interface ClusterMessagingProvider {
    public void sendMessage(@Nonnull String var1, @Nonnull String var2);

    public ListenerRegistration registerListener(@Nonnull String var1, @Nonnull Consumer<String> var2);
}

