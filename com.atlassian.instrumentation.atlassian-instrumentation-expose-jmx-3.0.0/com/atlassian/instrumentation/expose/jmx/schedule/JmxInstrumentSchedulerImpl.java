/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  io.atlassian.util.concurrent.ThreadFactories
 *  io.atlassian.util.concurrent.ThreadFactories$Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.instrumentation.expose.jmx.schedule;

import com.atlassian.instrumentation.expose.jmx.JmxInstrumentExposer;
import com.atlassian.instrumentation.expose.jmx.schedule.JmxInstrumentScheduler;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmxInstrumentSchedulerImpl
implements JmxInstrumentScheduler,
LifecycleAware {
    private static final Logger log = LoggerFactory.getLogger(JmxInstrumentSchedulerImpl.class);
    private final JmxInstrumentExposer jmxInstrumentExposer;
    private ScheduledExecutorService schedulerThread;

    public JmxInstrumentSchedulerImpl(JmxInstrumentExposer jmxInstrumentExposer) {
        this.jmxInstrumentExposer = jmxInstrumentExposer;
    }

    private void schedule() {
        Runnable exposition = new Runnable(){

            @Override
            public void run() {
                JmxInstrumentSchedulerImpl.this.jmxInstrumentExposer.exposePeriodically();
            }
        };
        this.schedulerThread = Executors.newSingleThreadScheduledExecutor(ThreadFactories.namedThreadFactory((String)"atlassian-instrumentation-jmx", (ThreadFactories.Type)ThreadFactories.Type.DAEMON));
        this.schedulerThread.scheduleAtFixedRate(exposition, 10L, 120L, TimeUnit.SECONDS);
    }

    public void onStart() {
        log.warn("atlassian-instrumentation-jmx expose scheduler started.");
        this.schedule();
    }

    public void onStop() {
        log.warn("atlassian-instrumentation-jmx expose scheduler stopped.");
        this.schedulerThread.shutdownNow();
        this.jmxInstrumentExposer.deregister();
    }
}

