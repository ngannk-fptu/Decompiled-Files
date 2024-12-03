/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.jmx;

import com.atlassian.diagnostics.internal.jmx.JmxService;
import com.atlassian.diagnostics.internal.jmx.ThreadMemoryAllocation;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadMemoryAllocationService {
    private static final Logger logger = LoggerFactory.getLogger(ThreadMemoryAllocationService.class);
    private final ThreadMXBean threadMXBean;

    public ThreadMemoryAllocationService(@Nonnull JmxService jmxService) {
        this.threadMXBean = jmxService.getThreadMXBean();
    }

    @Nonnull
    public List<ThreadMemoryAllocation> getThreadMemoryAllocations(long minimumMemoryAllocation) {
        return this.getThreadMemoryAllocations(minimumMemoryAllocation, Integer.MAX_VALUE);
    }

    @Nonnull
    public List<ThreadMemoryAllocation> getThreadMemoryAllocations(long minimumMemoryAllocation, int maxStackTraceDepth) {
        Map<Long, Long> threadIdMemoryAllocationsAboveThreshold = this.threadIdMemoryAllocations().entrySet().stream().filter(e -> (Long)e.getValue() >= minimumMemoryAllocation).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (!threadIdMemoryAllocationsAboveThreshold.isEmpty()) {
            return this.threadMemoryAllocations(threadIdMemoryAllocationsAboveThreshold, maxStackTraceDepth);
        }
        return Collections.emptyList();
    }

    @Nonnull
    public List<ThreadMemoryAllocation> getTopThreadMemoryAllocations(int numberOfThreadMemoryAllocations) {
        return this.getTopThreadMemoryAllocations(numberOfThreadMemoryAllocations, Integer.MAX_VALUE);
    }

    @Nonnull
    public List<ThreadMemoryAllocation> getTopThreadMemoryAllocations(int numberOfThreadMemoryAllocations, int maxStackTraceDepth) {
        Map topThreadsByMemoryAllocationSize = this.threadIdMemoryAllocations().entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder())).limit(Math.max(0, numberOfThreadMemoryAllocations)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2, LinkedHashMap::new));
        if (!topThreadsByMemoryAllocationSize.isEmpty()) {
            return this.threadMemoryAllocations(topThreadsByMemoryAllocationSize, maxStackTraceDepth);
        }
        return Collections.emptyList();
    }

    private Map<Long, Long> threadIdMemoryAllocations() {
        long[] memoryAllocations;
        long[] threadIds = this.threadMXBean.getAllThreadIds();
        if (threadIds.length == (memoryAllocations = this.getMemoryAllocations(threadIds)).length) {
            HashMap<Long, Long> threadIdMemoryAllocations = new HashMap<Long, Long>();
            for (int i = 0; i < threadIds.length; ++i) {
                threadIdMemoryAllocations.put(threadIds[i], memoryAllocations[i]);
            }
            return threadIdMemoryAllocations;
        }
        return Collections.emptyMap();
    }

    private long[] getMemoryAllocations(long[] threadIds) {
        try {
            return ((com.sun.management.ThreadMXBean)this.threadMXBean).getThreadAllocatedBytes(threadIds);
        }
        catch (ClassCastException | NoClassDefFoundError e) {
            logger.debug("Plugins cannot compile this class as com.sun.management.* is filtered out of the ClassLoader. Compile from the product instead", e);
            return new long[0];
        }
    }

    private List<ThreadMemoryAllocation> threadMemoryAllocations(Map<Long, Long> threadIdMemoryAllocations, int maxStackTraceDepth) {
        if (!threadIdMemoryAllocations.isEmpty()) {
            long[] threadIds = threadIdMemoryAllocations.keySet().stream().mapToLong(l -> l).toArray();
            ThreadInfo[] threadInfos = this.threadMXBean.getThreadInfo(threadIds, Math.max(maxStackTraceDepth, 0));
            return Arrays.stream(threadInfos).filter(Objects::nonNull).map(threadInfo -> new ThreadMemoryAllocation(threadInfo.getThreadName(), (Long)threadIdMemoryAllocations.get(threadInfo.getThreadId()), threadInfo.getStackTrace())).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}

