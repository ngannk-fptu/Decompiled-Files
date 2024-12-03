/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.operations;

import com.atlassian.instrumentation.operations.OpSnapshot;
import com.atlassian.instrumentation.operations.OpTimer;
import com.atlassian.instrumentation.utils.dbc.Assertions;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleOpTimer
implements OpTimer {
    private static final OpTimer.OnEndCallback NOOP = new OpTimer.OnEndCallback(){

        @Override
        public void onEndCalled(OpSnapshot opSnapshot) {
        }
    };
    private final String name;
    private final long then;
    private final long cpuThen;
    private final boolean captureCPUCost;
    private final AtomicReference<OpSnapshot> timerValue = new AtomicReference();
    private final OpTimer.OnEndCallback onEndCallback;

    public SimpleOpTimer(String name) {
        this(name, true, NOOP);
    }

    public SimpleOpTimer(String name, boolean captureCPUCost, OpTimer.OnEndCallback onEndCallback) {
        Assertions.notNull("name", name);
        Assertions.notNull("onEndCallback", onEndCallback);
        this.name = name;
        this.onEndCallback = onEndCallback;
        this.captureCPUCost = captureCPUCost;
        this.then = System.nanoTime();
        this.cpuThen = this.getCurrentThreadCpuTime();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public OpSnapshot snapshot() {
        if (this.timerValue.get() != null) {
            return this.timerValue.get();
        }
        long nanosTaken = Math.max(0L, System.nanoTime() - this.then);
        long cpuTimeTaken = Math.max(0L, this.getCurrentThreadCpuTime() - this.cpuThen);
        return OpSnapshot.createSingle(this.name, nanosTaken, 0L, cpuTimeTaken);
    }

    @Override
    public OpSnapshot end(final long resultSetSize) {
        return this.end(new OpTimer.HeisenburgResultSetCalculator(){

            @Override
            public long calculate() {
                return resultSetSize;
            }
        });
    }

    @Override
    public OpSnapshot end() {
        return this.end(0L);
    }

    @Override
    public OpSnapshot end(OpTimer.HeisenburgResultSetCalculator heisenburgResultSetCalculator) {
        OpSnapshot opSnapshot;
        long nanosTaken = Math.max(0L, System.nanoTime() - this.then);
        long cpuTimeTaken = Math.max(0L, this.getCurrentThreadCpuTime() - this.cpuThen);
        long resultSetSize = 0L;
        if (heisenburgResultSetCalculator != null) {
            resultSetSize = heisenburgResultSetCalculator.calculate();
        }
        if (!this.timerValue.compareAndSet(null, opSnapshot = OpSnapshot.createSingle(this.name, nanosTaken, resultSetSize, cpuTimeTaken))) {
            throw new IllegalStateException("The OpTimer has been re-used.  end() can only be called once!");
        }
        this.onEndCallback.onEndCalled(opSnapshot);
        return opSnapshot;
    }

    @Override
    public OpSnapshot endWithTime(long timeInMillis) {
        OpSnapshot opSnapshot = OpSnapshot.createSingle(this.name, TimeUnit.NANOSECONDS.convert(timeInMillis, TimeUnit.MILLISECONDS), 0L, 0L);
        if (!this.timerValue.compareAndSet(null, opSnapshot)) {
            throw new IllegalStateException("The OpTimer has been re-used.  end() can only be called once!");
        }
        this.onEndCallback.onEndCalled(opSnapshot);
        return opSnapshot;
    }

    private long getCurrentThreadCpuTime() {
        long cpuUsed = 0L;
        if (this.captureCPUCost) {
            try {
                ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
                if (threadMXBean.isCurrentThreadCpuTimeSupported()) {
                    cpuUsed = threadMXBean.getCurrentThreadCpuTime();
                }
            }
            catch (UnsupportedOperationException unsupportedOperationException) {
                // empty catch block
            }
        }
        return cpuUsed == -1L ? 0L : cpuUsed;
    }
}

