/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.api.healthcheck;

import java.lang.management.MemoryPoolMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.List;

public interface JvmMemoryInfo {
    public RuntimeMXBean getRuntimeMXBean();

    public List<MemoryPoolMXBean> getCodeCacheMemoryPoolMXBeans();
}

