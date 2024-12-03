/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 */
package io.micrometer.core.instrument.binder.jvm;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Collections;

@NonNullApi
@NonNullFields
public class JvmThreadMetrics
implements MeterBinder {
    private final Iterable<Tag> tags;

    public JvmThreadMetrics() {
        this(Collections.emptyList());
    }

    public JvmThreadMetrics(Iterable<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        Gauge.builder("jvm.threads.peak", threadBean, ThreadMXBean::getPeakThreadCount).tags(this.tags).description("The peak live thread count since the Java virtual machine started or peak was reset").baseUnit("threads").register(registry);
        Gauge.builder("jvm.threads.daemon", threadBean, ThreadMXBean::getDaemonThreadCount).tags(this.tags).description("The current number of live daemon threads").baseUnit("threads").register(registry);
        Gauge.builder("jvm.threads.live", threadBean, ThreadMXBean::getThreadCount).tags(this.tags).description("The current number of live threads including both daemon and non-daemon threads").baseUnit("threads").register(registry);
        FunctionCounter.builder("jvm.threads.started", threadBean, ThreadMXBean::getTotalStartedThreadCount).tags(this.tags).description("The total number of application threads started in the JVM").baseUnit("threads").register(registry);
        try {
            threadBean.getAllThreadIds();
            for (Thread.State state : Thread.State.values()) {
                Gauge.builder("jvm.threads.states", threadBean, bean -> JvmThreadMetrics.getThreadStateCount(bean, state)).tags(Tags.concat(this.tags, "state", JvmThreadMetrics.getStateTagValue(state))).description("The current number of threads").baseUnit("threads").register(registry);
            }
        }
        catch (Error error) {
            // empty catch block
        }
    }

    static long getThreadStateCount(ThreadMXBean threadBean, Thread.State state) {
        return Arrays.stream(threadBean.getThreadInfo(threadBean.getAllThreadIds())).filter(threadInfo -> threadInfo != null && threadInfo.getThreadState() == state).count();
    }

    private static String getStateTagValue(Thread.State state) {
        return state.name().toLowerCase().replace("_", "-");
    }
}

