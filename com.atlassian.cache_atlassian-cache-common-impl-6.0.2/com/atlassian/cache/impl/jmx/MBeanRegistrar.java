/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.cache.impl.jmx;

import javax.annotation.Nullable;
import javax.management.MBeanServer;

public interface MBeanRegistrar {
    public void registerMBeans(@Nullable MBeanServer var1);

    public void unregisterMBeans(@Nullable MBeanServer var1);
}

