/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.evaluator.cache;

import java.util.function.Consumer;

public interface DistributedQueue<E> {
    public Consumer<E> sender();

    public void registerReceiver(Consumer<E> var1);
}

