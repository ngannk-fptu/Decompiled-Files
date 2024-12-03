/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.operations;

import com.atlassian.instrumentation.Instrument;
import com.atlassian.instrumentation.operations.OpInstrument;
import com.atlassian.instrumentation.operations.OpSnapshot;
import com.atlassian.instrumentation.utils.dbc.Assertions;
import java.util.concurrent.TimeUnit;

public final class OpCounter
implements OpInstrument {
    private final String name;
    private long invocationCount;
    private long elapsedTotal;
    private long elapsedMin = Long.MAX_VALUE;
    private long elapsedMax = 0L;
    private long resultSetSize;
    private long cpuTotal;
    private long cpuMin = Long.MAX_VALUE;
    private long cpuMax = 0L;

    public OpCounter(String name) {
        this.name = Assertions.notNull("name", name);
    }

    public OpCounter(OpSnapshot opSnapshot) {
        this(opSnapshot.getName(), opSnapshot.getInvocationCount(), opSnapshot.getElapsedTotalTime(TimeUnit.NANOSECONDS), opSnapshot.getElapsedMinTime(TimeUnit.NANOSECONDS), opSnapshot.getElapsedMaxTime(TimeUnit.NANOSECONDS), opSnapshot.getResultSetSize(), opSnapshot.getCpuTotalTime(TimeUnit.NANOSECONDS), opSnapshot.getCpuMinTime(TimeUnit.NANOSECONDS), opSnapshot.getCpuMaxTime(TimeUnit.NANOSECONDS));
    }

    public OpCounter(String name, long invocationCount, long elapsedTotal, long elapsedMin, long elapsedMax, long resultSetSize, long cpuTotal, long cpuMin, long cpuMax) {
        this.name = Assertions.notNull("name", name);
        this.invocationCount = invocationCount;
        this.elapsedTotal = elapsedTotal;
        this.elapsedMin = elapsedMin;
        this.elapsedMax = elapsedMax;
        this.resultSetSize = resultSetSize;
        this.cpuTotal = cpuTotal;
        this.cpuMin = cpuMin;
        this.cpuMax = cpuMax;
    }

    public OpCounter add(OpCounter opCounter) {
        Assertions.notNull("opCounter", opCounter);
        this.add(opCounter.snapshot());
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public OpCounter add(OpSnapshot opSnapshot) {
        Assertions.notNull("opSnapshot", opSnapshot);
        OpCounter opCounter = this;
        synchronized (opCounter) {
            this.invocationCount += opSnapshot.getInvocationCount();
            this.elapsedTotal += opSnapshot.getElapsedTotalTime(TimeUnit.NANOSECONDS);
            if (this.elapsedMin > opSnapshot.getElapsedMinTime(TimeUnit.NANOSECONDS)) {
                this.elapsedMin = opSnapshot.getElapsedMinTime(TimeUnit.NANOSECONDS);
            }
            if (this.elapsedMax < opSnapshot.getElapsedMaxTime(TimeUnit.NANOSECONDS)) {
                this.elapsedMax = opSnapshot.getElapsedMaxTime(TimeUnit.NANOSECONDS);
            }
            this.cpuTotal += opSnapshot.getCpuTotalTime(TimeUnit.NANOSECONDS);
            if (this.cpuMin > opSnapshot.getCpuMinTime(TimeUnit.NANOSECONDS)) {
                this.cpuMin = opSnapshot.getCpuMinTime(TimeUnit.NANOSECONDS);
            }
            if (this.cpuMax < opSnapshot.getCpuMaxTime(TimeUnit.NANOSECONDS)) {
                this.cpuMax = opSnapshot.getCpuMaxTime(TimeUnit.NANOSECONDS);
            }
            this.resultSetSize += opSnapshot.getResultSetSize();
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public OpSnapshot snapshot() {
        OpCounter opCounter = this;
        synchronized (opCounter) {
            return new OpSnapshot(this.name, this.invocationCount, this.elapsedTotal, this.elapsedMin, this.elapsedMax, this.resultSetSize, this.cpuTotal, this.cpuMin, this.cpuMax);
        }
    }

    @Override
    public long getInvocationCount() {
        return this.snapshot().getInvocationCount();
    }

    @Override
    public long getMillisecondsTaken() {
        return this.snapshot().getElapsedTotalTime(TimeUnit.MILLISECONDS);
    }

    @Override
    public long getElapsedTotalTime(TimeUnit unit) {
        return this.snapshot().getElapsedTotalTime(unit);
    }

    @Override
    public long getElapsedMinTime(TimeUnit unit) {
        return this.snapshot().getElapsedMinTime(unit);
    }

    @Override
    public long getElapsedMaxTime(TimeUnit unit) {
        return this.snapshot().getElapsedMaxTime(unit);
    }

    @Override
    public long getResultSetSize() {
        return this.snapshot().getResultSetSize();
    }

    @Override
    public long getCpuTime() {
        return this.snapshot().getCpuTotalTime(TimeUnit.NANOSECONDS);
    }

    @Override
    public long getCpuTotalTime(TimeUnit unit) {
        return this.snapshot().getCpuTotalTime(unit);
    }

    @Override
    public long getCpuMinTime(TimeUnit unit) {
        return this.snapshot().getCpuMinTime(unit);
    }

    @Override
    public long getCpuMaxTime(TimeUnit unit) {
        return this.snapshot().getCpuMaxTime(unit);
    }

    @Override
    public long getValue() {
        return this.snapshot().getValue();
    }

    @Override
    public int compareTo(Instrument that) {
        if (that == this) {
            return 0;
        }
        if (that == null) {
            return -1;
        }
        int rc = that.getName().compareTo(this.getName());
        if (rc == 0 && that instanceof OpCounter) {
            OpCounter thatOp = (OpCounter)that;
            rc = this.snapshot().compareTo(thatOp.snapshot());
        }
        return rc;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        OpCounter opCounter = (OpCounter)o;
        if (this.cpuTotal != opCounter.cpuTotal) {
            return false;
        }
        if (this.invocationCount != opCounter.invocationCount) {
            return false;
        }
        if (this.cpuMax != opCounter.cpuMax) {
            return false;
        }
        if (this.elapsedMax != opCounter.elapsedMax) {
            return false;
        }
        if (this.elapsedTotal != opCounter.elapsedTotal) {
            return false;
        }
        if (this.cpuMin != opCounter.cpuMin) {
            return false;
        }
        if (this.elapsedMin != opCounter.elapsedMin) {
            return false;
        }
        if (this.resultSetSize != opCounter.resultSetSize) {
            return false;
        }
        return this.name.equals(opCounter.name);
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + (int)(this.invocationCount ^ this.invocationCount >>> 32);
        result = 31 * result + (int)(this.elapsedTotal ^ this.elapsedTotal >>> 32);
        result = 31 * result + (int)(this.elapsedMin ^ this.elapsedMin >>> 32);
        result = 31 * result + (int)(this.elapsedMax ^ this.elapsedMax >>> 32);
        result = 31 * result + (int)(this.resultSetSize ^ this.resultSetSize >>> 32);
        result = 31 * result + (int)(this.cpuTotal ^ this.cpuTotal >>> 32);
        result = 31 * result + (int)(this.cpuMin ^ this.cpuMin >>> 32);
        result = 31 * result + (int)(this.cpuMax ^ this.cpuMax >>> 32);
        return result;
    }

    public String toString() {
        return "OpCounter{name='" + this.name + '\'' + ", invocationCount=" + this.invocationCount + ", elapsedTotal=" + this.elapsedTotal + ", elapsedMin=" + this.elapsedMin + ", elapsedMax=" + this.elapsedMax + ", resultSetSize=" + this.resultSetSize + ", cpuTotal=" + this.cpuTotal + ", cpuMin=" + this.cpuMin + ", cpuMax=" + this.cpuMax + '}';
    }
}

