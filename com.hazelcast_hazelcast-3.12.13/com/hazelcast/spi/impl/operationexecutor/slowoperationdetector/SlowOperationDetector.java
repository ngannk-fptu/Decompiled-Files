/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.spi.impl.operationexecutor.slowoperationdetector;

import com.hazelcast.internal.management.dto.SlowOperationDTO;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.spi.impl.operationexecutor.OperationRunner;
import com.hazelcast.spi.impl.operationexecutor.slowoperationdetector.SlowOperationLog;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ThreadUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class SlowOperationDetector {
    private static final int FULL_LOG_FREQUENCY = 100;
    private static final long ONE_SECOND_IN_NANOS = TimeUnit.SECONDS.toNanos(1L);
    private static final long SLOW_OPERATION_THREAD_MAX_WAIT_TIME_TO_FINISH = TimeUnit.SECONDS.toMillis(10L);
    private final ConcurrentHashMap<Integer, SlowOperationLog> slowOperationLogs = new ConcurrentHashMap();
    private final StringBuilder stackTraceStringBuilder = new StringBuilder();
    private final ILogger logger;
    private final long slowOperationThresholdNanos;
    private final long logPurgeIntervalNanos;
    private final long logRetentionNanos;
    private final boolean isStackTraceLoggingEnabled;
    private final OperationRunner[] genericOperationRunners;
    private final OperationRunner[] partitionOperationRunners;
    private final CurrentOperationData[] genericCurrentOperationData;
    private final CurrentOperationData[] partitionCurrentOperationData;
    private final DetectorThread detectorThread;
    private final boolean enabled;
    private boolean isFirstLog = true;

    @SuppressFBWarnings(value={"EI_EXPOSE_REP2"})
    public SlowOperationDetector(LoggingService loggingServices, OperationRunner[] genericOperationRunners, OperationRunner[] partitionOperationRunners, HazelcastProperties hazelcastProperties, String hzName) {
        this.logger = loggingServices.getLogger(SlowOperationDetector.class);
        this.slowOperationThresholdNanos = hazelcastProperties.getNanos(GroupProperty.SLOW_OPERATION_DETECTOR_THRESHOLD_MILLIS);
        this.logPurgeIntervalNanos = hazelcastProperties.getNanos(GroupProperty.SLOW_OPERATION_DETECTOR_LOG_PURGE_INTERVAL_SECONDS);
        this.logRetentionNanos = hazelcastProperties.getNanos(GroupProperty.SLOW_OPERATION_DETECTOR_LOG_RETENTION_SECONDS);
        this.isStackTraceLoggingEnabled = hazelcastProperties.getBoolean(GroupProperty.SLOW_OPERATION_DETECTOR_STACK_TRACE_LOGGING_ENABLED);
        this.genericOperationRunners = genericOperationRunners;
        this.partitionOperationRunners = partitionOperationRunners;
        this.genericCurrentOperationData = this.initCurrentOperationData(genericOperationRunners);
        this.partitionCurrentOperationData = this.initCurrentOperationData(partitionOperationRunners);
        this.enabled = hazelcastProperties.getBoolean(GroupProperty.SLOW_OPERATION_DETECTOR_ENABLED);
        this.detectorThread = this.newDetectorThread(hzName);
    }

    public List<SlowOperationDTO> getSlowOperationDTOs() {
        ArrayList<SlowOperationDTO> slowOperationDTOs = new ArrayList<SlowOperationDTO>(this.slowOperationLogs.size());
        for (SlowOperationLog slowOperationLog : this.slowOperationLogs.values()) {
            slowOperationDTOs.add(slowOperationLog.createDTO());
        }
        return slowOperationDTOs;
    }

    public void start() {
        if (this.enabled) {
            this.detectorThread.start();
        } else {
            this.logger.warning("The SlowOperationDetector is disabled! Slow operations will not be reported.");
        }
    }

    public void shutdown() {
        this.detectorThread.shutdown();
    }

    private CurrentOperationData[] initCurrentOperationData(OperationRunner[] operationRunners) {
        CurrentOperationData[] currentOperationDataArray = new CurrentOperationData[operationRunners.length];
        for (int i = 0; i < currentOperationDataArray.length; ++i) {
            currentOperationDataArray[i] = new CurrentOperationData();
            currentOperationDataArray[i].operationHashCode = -1;
        }
        return currentOperationDataArray;
    }

    private DetectorThread newDetectorThread(String hzName) {
        DetectorThread thread = new DetectorThread(hzName);
        return thread;
    }

    private static class CurrentOperationData {
        private int operationHashCode;
        private long startNanos;
        private SlowOperationLog.Invocation invocation;

        private CurrentOperationData() {
        }
    }

    private final class DetectorThread
    extends Thread {
        private volatile boolean running;

        private DetectorThread(String hzName) {
            super(ThreadUtil.createThreadName(hzName, "SlowOperationDetectorThread"));
            this.running = true;
        }

        @Override
        public void run() {
            long lastLogPurge = System.nanoTime();
            while (this.running) {
                long nowNanos = System.nanoTime();
                long nowMillis = System.currentTimeMillis();
                this.scan(nowNanos, nowMillis, SlowOperationDetector.this.genericOperationRunners, SlowOperationDetector.this.genericCurrentOperationData);
                this.scan(nowNanos, nowMillis, SlowOperationDetector.this.partitionOperationRunners, SlowOperationDetector.this.partitionCurrentOperationData);
                if (this.purge(nowNanos, lastLogPurge)) {
                    lastLogPurge = nowNanos;
                }
                if (!this.running) continue;
                this.sleepInterval(nowNanos);
            }
        }

        private void scan(long nowNanos, long nowMillis, OperationRunner[] operationRunners, CurrentOperationData[] currentOperationDataArray) {
            for (int i = 0; i < operationRunners.length && this.running; ++i) {
                this.scanOperationRunner(nowNanos, nowMillis, operationRunners[i], currentOperationDataArray[i]);
            }
        }

        private void scanOperationRunner(long nowNanos, long nowMillis, OperationRunner operationRunner, CurrentOperationData operationData) {
            Object operation = operationRunner.currentTask();
            if (operation == null) {
                return;
            }
            int operationHashCode = System.identityHashCode(operation);
            if (operationData.operationHashCode != operationHashCode) {
                operationData.operationHashCode = operationHashCode;
                operationData.startNanos = nowNanos;
                operationData.invocation = null;
                return;
            }
            long durationNanos = nowNanos - operationData.startNanos;
            if (durationNanos < SlowOperationDetector.this.slowOperationThresholdNanos) {
                return;
            }
            if (operationData.invocation != null) {
                operationData.invocation.update(nowNanos, (int)TimeUnit.NANOSECONDS.toMillis(durationNanos));
                return;
            }
            String stackTrace = this.getStackTraceOrNull(operationRunner, operation);
            if (stackTrace != null) {
                SlowOperationLog log = this.getOrCreate(stackTrace, operation);
                int totalInvocations = log.totalInvocations.incrementAndGet();
                operationData.invocation = log.getOrCreate(operationHashCode, operation, durationNanos, nowNanos, nowMillis);
                this.logSlowOperation(log, totalInvocations);
            }
        }

        private String getStackTraceOrNull(OperationRunner operationRunner, Object operation) {
            String prefix = "";
            for (StackTraceElement stackTraceElement : operationRunner.currentThread().getStackTrace()) {
                SlowOperationDetector.this.stackTraceStringBuilder.append(prefix).append(stackTraceElement.toString());
                prefix = "\n\t";
            }
            if (operationRunner.currentTask() != operation) {
                SlowOperationDetector.this.stackTraceStringBuilder.setLength(0);
                return null;
            }
            String stackTrace = SlowOperationDetector.this.stackTraceStringBuilder.toString();
            SlowOperationDetector.this.stackTraceStringBuilder.setLength(0);
            return stackTrace;
        }

        private SlowOperationLog getOrCreate(String stackTrace, Object operation) {
            Integer stackTraceHashCode = stackTrace.hashCode();
            SlowOperationLog candidate = (SlowOperationLog)SlowOperationDetector.this.slowOperationLogs.get(stackTraceHashCode);
            if (candidate != null) {
                return candidate;
            }
            candidate = new SlowOperationLog(stackTrace, operation);
            SlowOperationDetector.this.slowOperationLogs.put(stackTraceHashCode, candidate);
            return candidate;
        }

        private void logSlowOperation(SlowOperationLog log, int totalInvocations) {
            if (SlowOperationDetector.this.isStackTraceLoggingEnabled) {
                this.logWithStackTrace(log, totalInvocations);
            } else if (!SlowOperationDetector.this.isFirstLog) {
                this.logWithoutStackTrace(log, totalInvocations);
            } else {
                this.logWithConfigHint(log);
            }
        }

        private void logWithStackTrace(SlowOperationLog log, int totalInvocations) {
            if (totalInvocations == 1) {
                SlowOperationDetector.this.logger.warning(String.format("Slow operation detected: %s%n%s", log.operation, log.stackTrace));
            } else {
                SlowOperationDetector.this.logger.warning(String.format("Slow operation detected: %s (%d invocations)%n%s", log.operation, totalInvocations, totalInvocations % 100 == 0 ? log.stackTrace : log.shortStackTrace));
            }
        }

        private void logWithoutStackTrace(SlowOperationLog log, int totalInvocations) {
            if (totalInvocations == 1) {
                SlowOperationDetector.this.logger.warning(String.format("Slow operation detected: %s", log.operation));
            } else {
                SlowOperationDetector.this.logger.warning(String.format("Slow operation detected: %s (%d invocations)", log.operation, totalInvocations));
            }
        }

        private void logWithConfigHint(SlowOperationLog log) {
            SlowOperationDetector.this.logger.warning(String.format("Slow operation detected: %s%nHint: You can enable the logging of stacktraces with the following system property: -D%s", log.operation, GroupProperty.SLOW_OPERATION_DETECTOR_STACK_TRACE_LOGGING_ENABLED));
            SlowOperationDetector.this.isFirstLog = false;
        }

        private boolean purge(long nowNanos, long lastLogPurge) {
            if (nowNanos - lastLogPurge <= SlowOperationDetector.this.logPurgeIntervalNanos) {
                return false;
            }
            for (SlowOperationLog log : SlowOperationDetector.this.slowOperationLogs.values()) {
                if (!this.running) {
                    return false;
                }
                if (!log.purgeInvocations(nowNanos, SlowOperationDetector.this.logRetentionNanos)) continue;
                SlowOperationDetector.this.slowOperationLogs.remove(log.stackTrace.hashCode());
            }
            return true;
        }

        private void sleepInterval(long nowNanos) {
            try {
                TimeUnit.NANOSECONDS.sleep(ONE_SECOND_IN_NANOS - (System.nanoTime() - nowNanos));
            }
            catch (Exception ignored) {
                EmptyStatement.ignore(ignored);
            }
        }

        private void shutdown() {
            this.running = false;
            SlowOperationDetector.this.detectorThread.interrupt();
            try {
                SlowOperationDetector.this.detectorThread.join(SLOW_OPERATION_THREAD_MAX_WAIT_TIME_TO_FINISH);
            }
            catch (InterruptedException ignored) {
                DetectorThread.currentThread().interrupt();
            }
        }
    }
}

