/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeState;
import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import com.hazelcast.internal.diagnostics.Diagnostics;
import com.hazelcast.internal.diagnostics.HealthMonitorLevel;
import com.hazelcast.internal.metrics.DoubleGauge;
import com.hazelcast.internal.metrics.LongGauge;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.logging.ILogger;
import com.hazelcast.memory.MemoryStats;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.ThreadUtil;
import java.util.concurrent.TimeUnit;

public class HealthMonitor {
    private static final String[] UNITS = new String[]{"", "K", "M", "G", "T", "P", "E"};
    private static final double PERCENTAGE_MULTIPLIER = 100.0;
    private static final double THRESHOLD_PERCENTAGE_INVOCATIONS = 70.0;
    private static final double THRESHOLD_INVOCATIONS = 1000.0;
    final HealthMetrics healthMetrics;
    private final ILogger logger;
    private final Node node;
    private final HealthMonitorLevel monitorLevel;
    private final int thresholdMemoryPercentage;
    private final int thresholdCPUPercentage;
    private final MetricsRegistry metricRegistry;
    private final HealthMonitorThread monitorThread;

    public HealthMonitor(Node node) {
        this.node = node;
        this.logger = node.getLogger(HealthMonitor.class);
        this.metricRegistry = node.nodeEngine.getMetricsRegistry();
        this.monitorLevel = this.getHealthMonitorLevel();
        this.thresholdMemoryPercentage = node.getProperties().getInteger(GroupProperty.HEALTH_MONITORING_THRESHOLD_MEMORY_PERCENTAGE);
        this.thresholdCPUPercentage = node.getProperties().getInteger(GroupProperty.HEALTH_MONITORING_THRESHOLD_CPU_PERCENTAGE);
        this.monitorThread = this.initMonitorThread();
        this.healthMetrics = new HealthMetrics();
    }

    private HealthMonitorThread initMonitorThread() {
        if (this.monitorLevel == HealthMonitorLevel.OFF) {
            return null;
        }
        int delaySeconds = this.node.getProperties().getSeconds(GroupProperty.HEALTH_MONITORING_DELAY_SECONDS);
        return new HealthMonitorThread(delaySeconds);
    }

    public HealthMonitor start() {
        if (this.monitorLevel == HealthMonitorLevel.OFF) {
            this.logger.finest("HealthMonitor is disabled");
            return this;
        }
        this.monitorThread.start();
        this.logger.finest("HealthMonitor started");
        return this;
    }

    public void stop() {
        if (this.monitorLevel == HealthMonitorLevel.OFF) {
            return;
        }
        this.monitorThread.interrupt();
        try {
            this.monitorThread.join();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        this.logger.finest("HealthMonitor stopped");
    }

    private HealthMonitorLevel getHealthMonitorLevel() {
        String healthMonitorLevel = this.node.getProperties().getString(GroupProperty.HEALTH_MONITORING_LEVEL);
        return HealthMonitorLevel.valueOf(healthMonitorLevel);
    }

    private static String percentageString(double p) {
        return String.format("%.2f%%", p);
    }

    private static String numberToUnit(long number) {
        for (int i = 6; i > 0; --i) {
            double step = Math.pow(1024.0, i);
            if (!((double)number > step)) continue;
            return String.format("%3.1f%s", (double)number / step, UNITS[i]);
        }
        return Long.toString(number);
    }

    class HealthMetrics {
        final LongGauge clientEndpointCount;
        final LongGauge clusterTimeDiff;
        final LongGauge executorAsyncQueueSize;
        final LongGauge executorClientQueueSize;
        final LongGauge executorQueryClientQueueSize;
        final LongGauge executorBlockingClientQueueSize;
        final LongGauge executorClusterQueueSize;
        final LongGauge executorScheduledQueueSize;
        final LongGauge executorSystemQueueSize;
        final LongGauge executorIoQueueSize;
        final LongGauge executorQueryQueueSize;
        final LongGauge executorMapLoadQueueSize;
        final LongGauge executorMapLoadAllKeysQueueSize;
        final LongGauge eventQueueSize;
        final LongGauge gcMinorCount;
        final LongGauge gcMinorTime;
        final LongGauge gcMajorCount;
        final LongGauge gcMajorTime;
        final LongGauge gcUnknownCount;
        final LongGauge gcUnknownTime;
        final LongGauge runtimeAvailableProcessors;
        final LongGauge runtimeMaxMemory;
        final LongGauge runtimeFreeMemory;
        final LongGauge runtimeTotalMemory;
        final LongGauge runtimeUsedMemory;
        final LongGauge threadPeakThreadCount;
        final LongGauge threadThreadCount;
        final DoubleGauge osProcessCpuLoad;
        final DoubleGauge osSystemLoadAverage;
        final DoubleGauge osSystemCpuLoad;
        final LongGauge osTotalPhysicalMemorySize;
        final LongGauge osFreePhysicalMemorySize;
        final LongGauge osTotalSwapSpaceSize;
        final LongGauge osFreeSwapSpaceSize;
        final LongGauge operationServiceExecutorQueueSize;
        final LongGauge operationServiceExecutorPriorityQueueSize;
        final LongGauge operationServiceResponseQueueSize;
        final LongGauge operationServiceRunningOperationsCount;
        final LongGauge operationServiceCompletedOperationsCount;
        final LongGauge operationServicePendingInvocationsCount;
        final DoubleGauge operationServicePendingInvocationsPercentage;
        final LongGauge proxyCount;
        final LongGauge tcpConnectionActiveCount;
        final LongGauge tcpConnectionCount;
        final LongGauge tcpConnectionClientCount;
        private final StringBuilder sb;
        private double memoryUsedOfTotalPercentage;
        private double memoryUsedOfMaxPercentage;

        HealthMetrics() {
            this.clientEndpointCount = HealthMonitor.this.metricRegistry.newLongGauge("client.endpoint.count");
            this.clusterTimeDiff = HealthMonitor.this.metricRegistry.newLongGauge("cluster.clock.clusterTimeDiff");
            this.executorAsyncQueueSize = HealthMonitor.this.metricRegistry.newLongGauge("executor.hz:async.queueSize");
            this.executorClientQueueSize = HealthMonitor.this.metricRegistry.newLongGauge("executor.hz:client.queueSize");
            this.executorQueryClientQueueSize = HealthMonitor.this.metricRegistry.newLongGauge("executor.hz:client.query.queueSize");
            this.executorBlockingClientQueueSize = HealthMonitor.this.metricRegistry.newLongGauge("executor.hz:client.blocking.queueSize");
            this.executorClusterQueueSize = HealthMonitor.this.metricRegistry.newLongGauge("executor.hz:cluster.queueSize");
            this.executorScheduledQueueSize = HealthMonitor.this.metricRegistry.newLongGauge("executor.hz:scheduled.queueSize");
            this.executorSystemQueueSize = HealthMonitor.this.metricRegistry.newLongGauge("executor.hz:system.queueSize");
            this.executorIoQueueSize = HealthMonitor.this.metricRegistry.newLongGauge("executor.hz:io.queueSize");
            this.executorQueryQueueSize = HealthMonitor.this.metricRegistry.newLongGauge("executor.hz:query.queueSize");
            this.executorMapLoadQueueSize = HealthMonitor.this.metricRegistry.newLongGauge("executor.hz:map-load.queueSize");
            this.executorMapLoadAllKeysQueueSize = HealthMonitor.this.metricRegistry.newLongGauge("executor.hz:map-loadAllKeys.queueSize");
            this.eventQueueSize = HealthMonitor.this.metricRegistry.newLongGauge("event.eventQueueSize");
            this.gcMinorCount = HealthMonitor.this.metricRegistry.newLongGauge("gc.minorCount");
            this.gcMinorTime = HealthMonitor.this.metricRegistry.newLongGauge("gc.minorTime");
            this.gcMajorCount = HealthMonitor.this.metricRegistry.newLongGauge("gc.majorCount");
            this.gcMajorTime = HealthMonitor.this.metricRegistry.newLongGauge("gc.majorTime");
            this.gcUnknownCount = HealthMonitor.this.metricRegistry.newLongGauge("gc.unknownCount");
            this.gcUnknownTime = HealthMonitor.this.metricRegistry.newLongGauge("gc.unknownTime");
            this.runtimeAvailableProcessors = HealthMonitor.this.metricRegistry.newLongGauge("runtime.availableProcessors");
            this.runtimeMaxMemory = HealthMonitor.this.metricRegistry.newLongGauge("runtime.maxMemory");
            this.runtimeFreeMemory = HealthMonitor.this.metricRegistry.newLongGauge("runtime.freeMemory");
            this.runtimeTotalMemory = HealthMonitor.this.metricRegistry.newLongGauge("runtime.totalMemory");
            this.runtimeUsedMemory = HealthMonitor.this.metricRegistry.newLongGauge("runtime.usedMemory");
            this.threadPeakThreadCount = HealthMonitor.this.metricRegistry.newLongGauge("thread.peakThreadCount");
            this.threadThreadCount = HealthMonitor.this.metricRegistry.newLongGauge("thread.threadCount");
            this.osProcessCpuLoad = HealthMonitor.this.metricRegistry.newDoubleGauge("os.processCpuLoad");
            this.osSystemLoadAverage = HealthMonitor.this.metricRegistry.newDoubleGauge("os.systemLoadAverage");
            this.osSystemCpuLoad = HealthMonitor.this.metricRegistry.newDoubleGauge("os.systemCpuLoad");
            this.osTotalPhysicalMemorySize = HealthMonitor.this.metricRegistry.newLongGauge("os.totalPhysicalMemorySize");
            this.osFreePhysicalMemorySize = HealthMonitor.this.metricRegistry.newLongGauge("os.freePhysicalMemorySize");
            this.osTotalSwapSpaceSize = HealthMonitor.this.metricRegistry.newLongGauge("os.totalSwapSpaceSize");
            this.osFreeSwapSpaceSize = HealthMonitor.this.metricRegistry.newLongGauge("os.freeSwapSpaceSize");
            this.operationServiceExecutorQueueSize = HealthMonitor.this.metricRegistry.newLongGauge("operation.queueSize");
            this.operationServiceExecutorPriorityQueueSize = HealthMonitor.this.metricRegistry.newLongGauge("operation.priorityQueueSize");
            this.operationServiceResponseQueueSize = HealthMonitor.this.metricRegistry.newLongGauge("operation.responseQueueSize");
            this.operationServiceRunningOperationsCount = HealthMonitor.this.metricRegistry.newLongGauge("operation.runningCount");
            this.operationServiceCompletedOperationsCount = HealthMonitor.this.metricRegistry.newLongGauge("operation.completedCount");
            this.operationServicePendingInvocationsCount = HealthMonitor.this.metricRegistry.newLongGauge("operation.invocations.pending");
            this.operationServicePendingInvocationsPercentage = HealthMonitor.this.metricRegistry.newDoubleGauge("operation.invocations.used");
            this.proxyCount = HealthMonitor.this.metricRegistry.newLongGauge("proxy.proxyCount");
            this.tcpConnectionActiveCount = HealthMonitor.this.metricRegistry.newLongGauge("tcp.connection.activeCount");
            this.tcpConnectionCount = HealthMonitor.this.metricRegistry.newLongGauge("tcp.connection.count");
            this.tcpConnectionClientCount = HealthMonitor.this.metricRegistry.newLongGauge("tcp.connection.clientCount");
            this.sb = new StringBuilder();
        }

        public void update() {
            this.memoryUsedOfTotalPercentage = 100.0 * (double)this.runtimeUsedMemory.read() / (double)this.runtimeTotalMemory.read();
            this.memoryUsedOfMaxPercentage = 100.0 * (double)this.runtimeUsedMemory.read() / (double)this.runtimeMaxMemory.read();
        }

        boolean exceedsThreshold() {
            if (this.memoryUsedOfMaxPercentage > (double)HealthMonitor.this.thresholdMemoryPercentage) {
                return true;
            }
            if (this.osProcessCpuLoad.read() > (double)HealthMonitor.this.thresholdCPUPercentage) {
                return true;
            }
            if (this.osSystemCpuLoad.read() > (double)HealthMonitor.this.thresholdCPUPercentage) {
                return true;
            }
            if (this.operationServicePendingInvocationsPercentage.read() > 70.0) {
                return true;
            }
            return (double)this.operationServicePendingInvocationsCount.read() > 1000.0;
        }

        public String render() {
            this.update();
            this.sb.setLength(0);
            this.renderProcessors();
            this.renderPhysicalMemory();
            this.renderSwap();
            this.renderHeap();
            this.renderNativeMemory();
            this.renderGc();
            this.renderLoad();
            this.renderThread();
            this.renderCluster();
            this.renderEvents();
            this.renderExecutors();
            this.renderOperationService();
            this.renderProxy();
            this.renderClient();
            this.renderConnection();
            return this.sb.toString();
        }

        private void renderConnection() {
            this.sb.append("connection.active.count=").append(this.tcpConnectionActiveCount.read()).append(", ");
            this.sb.append("client.connection.count=").append(this.tcpConnectionClientCount.read()).append(", ");
            this.sb.append("connection.count=").append(this.tcpConnectionCount.read());
        }

        private void renderClient() {
            this.sb.append("clientEndpoint.count=").append(this.clientEndpointCount.read()).append(", ");
        }

        private void renderProxy() {
            this.sb.append("proxy.count=").append(this.proxyCount.read()).append(", ");
        }

        private void renderLoad() {
            this.sb.append("load.process").append('=').append(String.format("%.2f", this.osProcessCpuLoad.read())).append("%, ");
            this.sb.append("load.system").append('=').append(String.format("%.2f", this.osSystemCpuLoad.read())).append("%, ");
            double value = this.osSystemLoadAverage.read();
            if (value < 0.0) {
                this.sb.append("load.systemAverage").append("=n/a ");
            } else {
                this.sb.append("load.systemAverage").append('=').append(String.format("%.2f", this.osSystemLoadAverage.read())).append(", ");
            }
        }

        private void renderProcessors() {
            this.sb.append("processors=").append(this.runtimeAvailableProcessors.read()).append(", ");
        }

        private void renderPhysicalMemory() {
            this.sb.append("physical.memory.total=").append(HealthMonitor.numberToUnit(this.osTotalPhysicalMemorySize.read())).append(", ");
            this.sb.append("physical.memory.free=").append(HealthMonitor.numberToUnit(this.osFreePhysicalMemorySize.read())).append(", ");
        }

        private void renderSwap() {
            this.sb.append("swap.space.total=").append(HealthMonitor.numberToUnit(this.osTotalSwapSpaceSize.read())).append(", ");
            this.sb.append("swap.space.free=").append(HealthMonitor.numberToUnit(this.osFreeSwapSpaceSize.read())).append(", ");
        }

        private void renderHeap() {
            this.sb.append("heap.memory.used=").append(HealthMonitor.numberToUnit(this.runtimeUsedMemory.read())).append(", ");
            this.sb.append("heap.memory.free=").append(HealthMonitor.numberToUnit(this.runtimeFreeMemory.read())).append(", ");
            this.sb.append("heap.memory.total=").append(HealthMonitor.numberToUnit(this.runtimeTotalMemory.read())).append(", ");
            this.sb.append("heap.memory.max=").append(HealthMonitor.numberToUnit(this.runtimeMaxMemory.read())).append(", ");
            this.sb.append("heap.memory.used/total=").append(HealthMonitor.percentageString(this.memoryUsedOfTotalPercentage)).append(", ");
            this.sb.append("heap.memory.used/max=").append(HealthMonitor.percentageString(this.memoryUsedOfMaxPercentage)).append(", ");
        }

        private void renderEvents() {
            this.sb.append("event.q.size=").append(this.eventQueueSize.read()).append(", ");
        }

        private void renderCluster() {
            this.sb.append("cluster.timeDiff=").append(this.clusterTimeDiff.read()).append(", ");
        }

        private void renderThread() {
            this.sb.append("thread.count=").append(this.threadThreadCount.read()).append(", ");
            this.sb.append("thread.peakCount=").append(this.threadPeakThreadCount.read()).append(", ");
        }

        private void renderGc() {
            this.sb.append("minor.gc.count=").append(this.gcMinorCount.read()).append(", ");
            this.sb.append("minor.gc.time=").append(this.gcMinorTime.read()).append("ms, ");
            this.sb.append("major.gc.count=").append(this.gcMajorCount.read()).append(", ");
            this.sb.append("major.gc.time=").append(this.gcMajorTime.read()).append("ms, ");
            if (this.gcUnknownCount.read() > 0L) {
                this.sb.append("unknown.gc.count=").append(this.gcUnknownCount.read()).append(", ");
                this.sb.append("unknown.gc.time=").append(this.gcUnknownTime.read()).append("ms, ");
            }
        }

        private void renderNativeMemory() {
            MemoryStats memoryStats = HealthMonitor.this.node.getNodeExtension().getMemoryStats();
            if (memoryStats.getMaxNative() <= 0L) {
                return;
            }
            long usedNative = memoryStats.getUsedNative();
            this.sb.append("native.memory.used=").append(HealthMonitor.numberToUnit(usedNative)).append(", ");
            this.sb.append("native.memory.free=").append(HealthMonitor.numberToUnit(memoryStats.getFreeNative())).append(", ");
            this.sb.append("native.memory.total=").append(HealthMonitor.numberToUnit(memoryStats.getCommittedNative())).append(", ");
            this.sb.append("native.memory.max=").append(HealthMonitor.numberToUnit(memoryStats.getMaxNative())).append(", ");
            long maxMeta = memoryStats.getMaxMetadata();
            if (maxMeta > 0L) {
                long usedMeta = memoryStats.getUsedMetadata();
                this.sb.append("native.meta.memory.used=").append(HealthMonitor.numberToUnit(usedMeta)).append(", ");
                this.sb.append("native.meta.memory.free=").append(HealthMonitor.numberToUnit(maxMeta - usedMeta)).append(", ");
                this.sb.append("native.meta.memory.percentage=").append(HealthMonitor.percentageString(100.0 * (double)usedMeta / (double)(usedNative + usedMeta))).append(", ");
            }
        }

        private void renderExecutors() {
            this.sb.append("executor.q.async.size=").append(this.executorAsyncQueueSize.read()).append(", ");
            this.sb.append("executor.q.client.size=").append(this.executorClientQueueSize.read()).append(", ");
            this.sb.append("executor.q.client.query.size=").append(this.executorQueryClientQueueSize.read()).append(", ");
            this.sb.append("executor.q.client.blocking.size=").append(this.executorBlockingClientQueueSize.read()).append(", ");
            this.sb.append("executor.q.query.size=").append(this.executorQueryQueueSize.read()).append(", ");
            this.sb.append("executor.q.scheduled.size=").append(this.executorScheduledQueueSize.read()).append(", ");
            this.sb.append("executor.q.io.size=").append(this.executorIoQueueSize.read()).append(", ");
            this.sb.append("executor.q.system.size=").append(this.executorSystemQueueSize.read()).append(", ");
            this.sb.append("executor.q.operations.size=").append(this.operationServiceExecutorQueueSize.read()).append(", ");
            this.sb.append("executor.q.priorityOperation.size=").append(this.operationServiceExecutorPriorityQueueSize.read()).append(", ");
            this.sb.append("operations.completed.count=").append(this.operationServiceCompletedOperationsCount.read()).append(", ");
            this.sb.append("executor.q.mapLoad.size=").append(this.executorMapLoadQueueSize.read()).append(", ");
            this.sb.append("executor.q.mapLoadAllKeys.size=").append(this.executorMapLoadAllKeysQueueSize.read()).append(", ");
            this.sb.append("executor.q.cluster.size=").append(this.executorClusterQueueSize.read()).append(", ");
        }

        private void renderOperationService() {
            this.sb.append("executor.q.response.size=").append(this.operationServiceResponseQueueSize.read()).append(", ");
            this.sb.append("operations.running.count=").append(this.operationServiceRunningOperationsCount.read()).append(", ");
            this.sb.append("operations.pending.invocations.percentage=").append(String.format("%.2f", this.operationServicePendingInvocationsPercentage.read())).append("%, ");
            this.sb.append("operations.pending.invocations.count=").append(this.operationServicePendingInvocationsCount.read()).append(", ");
        }
    }

    private final class HealthMonitorThread
    extends Thread {
        private final int delaySeconds;
        private boolean performanceLogHint;

        private HealthMonitorThread(int delaySeconds) {
            super(ThreadUtil.createThreadName(((HealthMonitor)HealthMonitor.this).node.hazelcastInstance.getName(), "HealthMonitor"));
            this.setDaemon(true);
            this.delaySeconds = delaySeconds;
            this.performanceLogHint = HealthMonitor.this.node.getProperties().getBoolean(Diagnostics.ENABLED);
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public void run() {
            try {
                while (HealthMonitor.this.node.getState() == NodeState.ACTIVE) {
                    HealthMonitor.this.healthMetrics.update();
                    switch (HealthMonitor.this.monitorLevel) {
                        case NOISY: {
                            if (HealthMonitor.this.healthMetrics.exceedsThreshold()) {
                                this.logDiagnosticsHint();
                            }
                            HealthMonitor.this.logger.info(HealthMonitor.this.healthMetrics.render());
                            break;
                        }
                        case SILENT: {
                            if (!HealthMonitor.this.healthMetrics.exceedsThreshold()) break;
                            this.logDiagnosticsHint();
                            HealthMonitor.this.logger.info(HealthMonitor.this.healthMetrics.render());
                            break;
                        }
                        default: {
                            throw new IllegalStateException("Unrecognized HealthMonitorLevel: " + (Object)((Object)HealthMonitor.this.monitorLevel));
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(this.delaySeconds);
                    }
                    catch (InterruptedException e) {
                        HealthMonitorThread.currentThread().interrupt();
                        return;
                    }
                }
            }
            catch (OutOfMemoryError e) {
                OutOfMemoryErrorDispatcher.onOutOfMemory(e);
                return;
            }
            catch (Throwable t) {
                HealthMonitor.this.logger.warning("Health Monitor failed", t);
            }
        }

        private void logDiagnosticsHint() {
            if (!this.performanceLogHint) {
                return;
            }
            this.performanceLogHint = false;
            HealthMonitor.this.logger.info(String.format("The HealthMonitor has detected a high load on the system. For more detailed information,%nenable the Diagnostics by adding the property -D%s=true", Diagnostics.ENABLED));
        }
    }
}

