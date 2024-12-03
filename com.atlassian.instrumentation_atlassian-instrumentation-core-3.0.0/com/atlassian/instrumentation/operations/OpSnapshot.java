/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.operations;

import com.atlassian.instrumentation.Instrument;
import com.atlassian.instrumentation.compare.InstrumentComparator;
import com.atlassian.instrumentation.operations.OpInstrument;
import com.atlassian.instrumentation.utils.dbc.Assertions;
import java.util.concurrent.TimeUnit;

public final class OpSnapshot
implements OpInstrument {
    private final String name;
    private final long invocationCount;
    private final long elapsedTotal;
    private final long elapsedMin;
    private final long elapsedMax;
    private final long resultSetSize;
    private final long cpuTotal;
    private final long cpuMin;
    private final long cpuMax;

    public OpSnapshot(String name, long invocationCount, long elapsedTotal, long elapsedMin, long elapsedMax, long resultSetSize, long cpuTotal, long cpuMin, long cpuMax) {
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

    public static OpSnapshot createSingle(String name, long elapsedTime, long resultSetSize, long cpuTime) {
        return new OpSnapshot(name, 1L, elapsedTime, elapsedTime, elapsedTime, resultSetSize, cpuTime, cpuTime, cpuTime);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getInvocationCount() {
        return this.invocationCount;
    }

    @Override
    public long getMillisecondsTaken() {
        return this.getElapsedTotalTime(TimeUnit.MILLISECONDS);
    }

    @Override
    public long getElapsedTotalTime(TimeUnit unit) {
        return unit.convert(this.elapsedTotal, TimeUnit.NANOSECONDS);
    }

    @Override
    public long getElapsedMinTime(TimeUnit unit) {
        return this.invocationCount == 0L ? 0L : unit.convert(this.elapsedMin, TimeUnit.NANOSECONDS);
    }

    @Override
    public long getElapsedMaxTime(TimeUnit unit) {
        return this.invocationCount == 0L ? 0L : unit.convert(this.elapsedMax, TimeUnit.NANOSECONDS);
    }

    @Override
    public long getResultSetSize() {
        return this.resultSetSize;
    }

    @Override
    public long getCpuTime() {
        return this.getCpuTotalTime(TimeUnit.NANOSECONDS);
    }

    @Override
    public long getCpuTotalTime(TimeUnit unit) {
        return unit.convert(this.cpuTotal, TimeUnit.NANOSECONDS);
    }

    @Override
    public long getCpuMinTime(TimeUnit unit) {
        return this.invocationCount == 0L ? 0L : unit.convert(this.cpuMin, TimeUnit.NANOSECONDS);
    }

    @Override
    public long getCpuMaxTime(TimeUnit unit) {
        return this.invocationCount == 0L ? 0L : unit.convert(this.cpuMax, TimeUnit.NANOSECONDS);
    }

    @Override
    public long getValue() {
        return this.getElapsedTotalTime(TimeUnit.MILLISECONDS);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        OpSnapshot snapshot = (OpSnapshot)o;
        if (this.cpuTotal != snapshot.cpuTotal) {
            return false;
        }
        if (this.invocationCount != snapshot.invocationCount) {
            return false;
        }
        if (this.cpuMax != snapshot.cpuMax) {
            return false;
        }
        if (this.elapsedMax != snapshot.elapsedMax) {
            return false;
        }
        if (this.elapsedTotal != snapshot.elapsedTotal) {
            return false;
        }
        if (this.cpuMin != snapshot.cpuMin) {
            return false;
        }
        if (this.elapsedMin != snapshot.elapsedMin) {
            return false;
        }
        if (this.resultSetSize != snapshot.resultSetSize) {
            return false;
        }
        return this.name.equals(snapshot.name);
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
        return "OpSnapshot{name='" + this.name + '\'' + ", invocationCount=" + this.invocationCount + ", elapsedTotal=" + this.elapsedTotal + ", elapsedMin=" + this.elapsedMin + ", elapsedMax=" + this.elapsedMax + ", resultSetSize=" + this.resultSetSize + ", cpuTotal=" + this.cpuTotal + ", cpuMin=" + this.cpuMin + ", cpuMax=" + this.cpuMax + '}';
    }

    @Override
    public int compareTo(Instrument that) {
        if (that == null) {
            return -1;
        }
        if (that instanceof OpSnapshot) {
            OpSnapshot thatSnapshot = (OpSnapshot)that;
            long rc = this.elapsedTotal - thatSnapshot.elapsedTotal;
            if (rc == 0L && (rc = this.invocationCount - thatSnapshot.invocationCount) == 0L) {
                rc = this.resultSetSize - thatSnapshot.resultSetSize;
            }
            return rc > 0L ? 1 : (rc < 0L ? -1 : 0);
        }
        return new InstrumentComparator().compare(this, that);
    }

    public OpSnapshot substract(OpSnapshot previousSnapshot) {
        Assertions.notNull("previousSnapshot", previousSnapshot);
        return new OpSnapshot(this.name, this.invocationCount - previousSnapshot.invocationCount, this.elapsedTotal - previousSnapshot.elapsedTotal, Math.min(this.elapsedMin, previousSnapshot.elapsedMin), Math.max(this.elapsedMax, previousSnapshot.elapsedMax), this.resultSetSize - previousSnapshot.resultSetSize, this.cpuTotal - previousSnapshot.cpuTotal, Math.min(this.cpuMin, previousSnapshot.cpuMin), Math.max(this.cpuMax, previousSnapshot.cpuMax));
    }

    public OpSnapshot add(OpSnapshot otherOpSnapshot) {
        return new OpSnapshot(this.name, this.invocationCount + otherOpSnapshot.invocationCount, this.elapsedTotal + otherOpSnapshot.elapsedTotal, Math.min(this.elapsedMin, otherOpSnapshot.elapsedMin), Math.max(this.elapsedMax, otherOpSnapshot.elapsedMax), this.resultSetSize + otherOpSnapshot.resultSetSize, this.cpuTotal + otherOpSnapshot.cpuTotal, Math.min(this.cpuMin, otherOpSnapshot.cpuMin), Math.max(this.cpuMax, otherOpSnapshot.cpuMax));
    }
}

