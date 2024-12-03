/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics.observer;

import org.terracotta.statistics.observer.ChainedObserver;

public interface ChainedEventObserver
extends ChainedObserver {
    public void event(long var1, long ... var3);
}

