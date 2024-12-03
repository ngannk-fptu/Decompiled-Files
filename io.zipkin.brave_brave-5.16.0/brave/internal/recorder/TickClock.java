/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.recorder;

import brave.Clock;

final class TickClock
implements Clock {
    final long baseEpochMicros;
    final long baseTickNanos;

    TickClock(long baseEpochMicros, long baseTickNanos) {
        this.baseEpochMicros = baseEpochMicros;
        this.baseTickNanos = baseTickNanos;
    }

    @Override
    public long currentTimeMicroseconds() {
        return (System.nanoTime() - this.baseTickNanos) / 1000L + this.baseEpochMicros;
    }

    public String toString() {
        return "TickClock{baseEpochMicros=" + this.baseEpochMicros + ", baseTickNanos=" + this.baseTickNanos + "}";
    }
}

