/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;
import com.hazelcast.internal.diagnostics.DiagnosticsPlugin;
import com.hazelcast.spi.NamedOperation;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationexecutor.OperationExecutor;
import com.hazelcast.spi.impl.operationexecutor.OperationRunner;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import com.hazelcast.util.ItemCounter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class OperationThreadSamplerPlugin
extends DiagnosticsPlugin {
    public static final HazelcastProperty PERIOD_SECONDS = new HazelcastProperty("hazelcast.diagnostics.operationthreadsamples.period.seconds", 0, TimeUnit.SECONDS);
    public static final HazelcastProperty SAMPLER_PERIOD_MILLIS = new HazelcastProperty("hazelcast.diagnostics.operationthreadsamples.sampler.period.millis", 100, TimeUnit.MILLISECONDS);
    public static final HazelcastProperty INCLUDE_NAME = new HazelcastProperty("hazelcast.diagnostics.operationthreadsamples.includeName", false);
    public static final float HUNDRED = 100.0f;
    private final long periodMillis;
    private final long samplerPeriodMillis;
    private final ItemCounter<String> partitionSpecificSamples = new ItemCounter();
    private final ItemCounter<String> genericSamples = new ItemCounter();
    private final OperationExecutor executor;
    private final NodeEngineImpl nodeEngine;
    private final boolean includeName;

    public OperationThreadSamplerPlugin(NodeEngineImpl nodeEngine) {
        super(nodeEngine.getLogger(OperationThreadSamplerPlugin.class));
        this.nodeEngine = nodeEngine;
        InternalOperationService operationService = nodeEngine.getOperationService();
        this.executor = ((OperationServiceImpl)operationService).getOperationExecutor();
        HazelcastProperties props = nodeEngine.getProperties();
        this.periodMillis = props.getMillis(PERIOD_SECONDS);
        this.samplerPeriodMillis = props.getMillis(SAMPLER_PERIOD_MILLIS);
        this.includeName = props.getBoolean(INCLUDE_NAME);
    }

    @Override
    public long getPeriodMillis() {
        return this.periodMillis;
    }

    @Override
    public void onStart() {
        this.logger.info("Plugin:active: period-millis:" + this.periodMillis + " sampler-period-millis:" + this.samplerPeriodMillis);
        new SampleThread().start();
    }

    @Override
    public void run(DiagnosticsLogWriter writer) {
        writer.startSection("OperationThreadSamples");
        this.write(writer, "Partition", this.partitionSpecificSamples);
        this.write(writer, "Generic", this.genericSamples);
        writer.endSection();
    }

    private void write(DiagnosticsLogWriter writer, String text, ItemCounter<String> samples) {
        writer.startSection(text);
        long total = samples.total();
        for (String name : samples.descendingKeys()) {
            long s = samples.get(name);
            writer.writeKeyValueEntry(name, s + " " + 100.0f * (float)s / (float)total + "%");
        }
        writer.endSection();
    }

    private class SampleThread
    extends Thread {
        private SampleThread() {
        }

        @Override
        public void run() {
            long nextRunMillis = System.currentTimeMillis();
            while (OperationThreadSamplerPlugin.this.nodeEngine.isActive()) {
                LockSupport.parkUntil(nextRunMillis);
                nextRunMillis = OperationThreadSamplerPlugin.this.samplerPeriodMillis;
                this.sample(OperationThreadSamplerPlugin.this.executor.getPartitionOperationRunners(), OperationThreadSamplerPlugin.this.partitionSpecificSamples);
                this.sample(OperationThreadSamplerPlugin.this.executor.getGenericOperationRunners(), OperationThreadSamplerPlugin.this.genericSamples);
            }
        }

        private void sample(OperationRunner[] runners, ItemCounter<String> samples) {
            for (OperationRunner runner : runners) {
                Object task = runner.currentTask();
                if (task == null) continue;
                samples.inc(this.toKey(task));
            }
        }

        private String toKey(Object task) {
            String name = OperationThreadSamplerPlugin.this.includeName ? (task instanceof NamedOperation ? task.getClass().getName() + "#" + ((NamedOperation)task).getName() : task.getClass().getName()) : task.getClass().getName();
            return name;
        }
    }
}

