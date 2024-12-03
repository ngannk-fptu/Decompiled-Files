/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi;

public interface ExtendedStatisticsSupport {
    public long getElementCountInMemory();

    public long getElementCountOnDisk();

    public long getSizeInMemory();
}

