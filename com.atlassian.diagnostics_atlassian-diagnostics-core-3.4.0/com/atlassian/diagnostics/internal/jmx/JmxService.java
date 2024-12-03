/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.diagnostics.internal.jmx;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface JmxService {
    public ThreadMXBean getThreadMXBean();

    @Nonnull
    public List<MemoryPoolMXBean> getMemoryPoolMXBeans();

    @Nonnull
    public List<GarbageCollectorMXBean> getGarbageCollectorMXBeans();

    public boolean hasObjectName(String var1);

    @Nullable
    public <T> T getJmxAttribute(@Nonnull String var1, @Nonnull String var2);

    @Nonnull
    public <T> List<T> getJmxAttributes(String var1, String[] var2);
}

