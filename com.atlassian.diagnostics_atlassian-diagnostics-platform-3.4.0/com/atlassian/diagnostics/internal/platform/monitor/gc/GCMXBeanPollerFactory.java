/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nonnull.ReturnValuesAreNonnullByDefault
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.platform.monitor.gc;

import com.atlassian.annotations.nonnull.ReturnValuesAreNonnullByDefault;
import com.atlassian.diagnostics.internal.platform.monitor.gc.GCMXBeanPoller;
import java.lang.management.GarbageCollectorMXBean;
import java.time.Clock;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class GCMXBeanPollerFactory {
    private static final Logger log = LoggerFactory.getLogger(GCMXBeanPollerFactory.class);
    private final Collection<GarbageCollectorMXBean> garbageCollectorMXBeans;
    private final Clock clock;

    public GCMXBeanPollerFactory(Collection<GarbageCollectorMXBean> garbageCollectorMXBeans, Clock clock) {
        this.garbageCollectorMXBeans = garbageCollectorMXBeans;
        this.clock = clock;
    }

    public Optional<GCMXBeanPoller> getGCMXBeansPoller() {
        return this.getOldGenMXBean().map(garbageCollectorMXBean -> new GCMXBeanPoller((GarbageCollectorMXBean)garbageCollectorMXBean, this.clock));
    }

    private Optional<GarbageCollectorMXBean> getOldGenMXBean() {
        List result = this.garbageCollectorMXBeans.stream().filter(this::isOldGenGCMXBean).collect(Collectors.toList());
        if (result.isEmpty()) {
            log.warn("Received no Old Generation GarbageCollectorMXBean.");
            return Optional.empty();
        }
        if (result.size() > 1) {
            log.warn("Received multiple Old Generation GarbageCollectorMXBeans ({}), using only the first one.", result);
        }
        return Optional.of(result.get(0));
    }

    private boolean isOldGenGCMXBean(GarbageCollectorMXBean garbageCollectorMXBean) {
        String gcName = garbageCollectorMXBean.getName();
        return "MarkSweepCompact".equals(gcName) || "PS MarkSweep".equals(gcName) || "ConcurrentMarkSweep".equals(gcName) || "G1 Old Generation".equals(gcName);
    }
}

