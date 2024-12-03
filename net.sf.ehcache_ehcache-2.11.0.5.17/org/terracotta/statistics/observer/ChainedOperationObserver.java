/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics.observer;

import org.terracotta.statistics.observer.ChainedObserver;

public interface ChainedOperationObserver<T extends Enum<T>>
extends ChainedObserver {
    public void begin(long var1);

    public void end(long var1, T var3);

    public void end(long var1, T var3, long ... var4);
}

