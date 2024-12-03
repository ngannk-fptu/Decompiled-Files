/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.internal.cflowstack;

public interface ThreadCounter {
    public void inc();

    public void dec();

    public boolean isNotZero();

    public void removeThreadCounter();
}

