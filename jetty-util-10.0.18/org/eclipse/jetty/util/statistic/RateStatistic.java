/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util.statistic;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.thread.AutoLock;

public class RateStatistic {
    private final AutoLock _lock = new AutoLock();
    private final Deque<Long> _samples = new ArrayDeque<Long>();
    private final long _nanoPeriod;
    private final TimeUnit _units;
    private long _max;
    private long _count;

    public RateStatistic(long period, TimeUnit units) {
        this._nanoPeriod = TimeUnit.NANOSECONDS.convert(period, units);
        this._units = units;
    }

    public long getPeriod() {
        return this._units.convert(this._nanoPeriod, TimeUnit.NANOSECONDS);
    }

    public TimeUnit getUnits() {
        return this._units;
    }

    public void reset() {
        try (AutoLock l = this._lock.lock();){
            this._samples.clear();
            this._max = 0L;
            this._count = 0L;
        }
    }

    private void update() {
        this.update(NanoTime.now());
    }

    private void update(long now) {
        Long head = this._samples.peekFirst();
        while (head != null && NanoTime.elapsed(head, now) > this._nanoPeriod) {
            this._samples.removeFirst();
            head = this._samples.peekFirst();
        }
    }

    protected void age(long period, TimeUnit units) {
        long increment = TimeUnit.NANOSECONDS.convert(period, units);
        try (AutoLock l = this._lock.lock();){
            int size = this._samples.size();
            for (int i = 0; i < size; ++i) {
                this._samples.addLast(this._samples.removeFirst() - increment);
            }
            this.update();
        }
    }

    public int record() {
        long now = NanoTime.now();
        try (AutoLock l = this._lock.lock();){
            ++this._count;
            this._samples.add(now);
            this.update(now);
            int rate = this._samples.size();
            if ((long)rate > this._max) {
                this._max = rate;
            }
            int n = rate;
            return n;
        }
    }

    public int getRate() {
        try (AutoLock l = this._lock.lock();){
            this.update();
            int n = this._samples.size();
            return n;
        }
    }

    public long getMax() {
        try (AutoLock l = this._lock.lock();){
            long l2 = this._max;
            return l2;
        }
    }

    public long getOldest(TimeUnit units) {
        try (AutoLock l = this._lock.lock();){
            Long head = this._samples.peekFirst();
            if (head == null) {
                long l2 = -1L;
                return l2;
            }
            long l3 = units.convert(NanoTime.since(head), TimeUnit.NANOSECONDS);
            return l3;
        }
    }

    public long getCount() {
        try (AutoLock l = this._lock.lock();){
            long l2 = this._count;
            return l2;
        }
    }

    public String dump() {
        return this.dump(TimeUnit.MINUTES);
    }

    public String dump(TimeUnit units) {
        long now = NanoTime.now();
        try (AutoLock l = this._lock.lock();){
            String samples = this._samples.stream().mapToLong(t -> units.convert(NanoTime.elapsed(t, now), TimeUnit.NANOSECONDS)).mapToObj(Long::toString).collect(Collectors.joining(System.lineSeparator()));
            String string = String.format("%s%n%s", this.toString(now), samples);
            return string;
        }
    }

    public String toString() {
        return this.toString(NanoTime.now());
    }

    private String toString(long nanoTime) {
        try (AutoLock l = this._lock.lock();){
            this.update(nanoTime);
            String string = String.format("%s@%x{count=%d,max=%d,rate=%d per %d %s}", new Object[]{this.getClass().getSimpleName(), this.hashCode(), this._count, this._max, this._samples.size(), this._units.convert(this._nanoPeriod, TimeUnit.NANOSECONDS), this._units});
            return string;
        }
    }
}

