/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics.archive;

public interface Timestamped<T> {
    public T getSample();

    public long getTimestamp();
}

