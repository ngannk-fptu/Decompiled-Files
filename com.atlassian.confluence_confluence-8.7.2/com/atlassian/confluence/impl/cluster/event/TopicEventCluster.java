/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.cluster.event;

import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface TopicEventCluster<E, N> {
    public boolean allNodesInitialised();

    public void initialise(BiConsumer<N, E> var1, BiConsumer<N, UUID> var2, Consumer<N> var3);

    public Set<N> getOtherClusterMembers();

    public void publishEvent(E var1);

    public void publishAck(UUID var1);

    public E wrapEvent(Object var1);
}

