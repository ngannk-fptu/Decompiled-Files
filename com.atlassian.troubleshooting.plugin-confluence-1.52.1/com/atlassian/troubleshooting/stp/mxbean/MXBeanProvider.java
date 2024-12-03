/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.mxbean;

import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.List;

public interface MXBeanProvider {
    public RuntimeMXBean getRuntimeMXBean();

    public MemoryMXBean getMemoryMXBean();

    public List<MemoryManagerMXBean> getMemoryManagerMXBeans();

    public List<MemoryPoolMXBean> getMemoryPoolMXBeans();
}

