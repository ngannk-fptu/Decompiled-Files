/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics.archive;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.terracotta.statistics.archive.CircularBuffer;
import org.terracotta.statistics.archive.DevNull;
import org.terracotta.statistics.archive.SampleSink;
import org.terracotta.statistics.archive.Timestamped;

public class StatisticArchive<T>
implements SampleSink<Timestamped<T>> {
    private final SampleSink<? super Timestamped<T>> overspill;
    private volatile int size;
    private volatile CircularBuffer<Timestamped<T>> buffer;

    public StatisticArchive(int size) {
        this(size, DevNull.DEV_NULL);
    }

    public StatisticArchive(int size, SampleSink<? super Timestamped<T>> overspill) {
        this.size = size;
        this.overspill = overspill;
    }

    public synchronized void setCapacity(int samples) {
        if (samples != this.size) {
            this.size = samples;
            if (this.buffer != null) {
                CircularBuffer<Timestamped<T>> newBuffer = new CircularBuffer<Timestamped<T>>(this.size);
                for (Timestamped<T> sample : this.getArchive()) {
                    this.overspill.accept(newBuffer.insert(sample));
                }
                this.buffer = newBuffer;
            }
        }
    }

    @Override
    public synchronized void accept(Timestamped<T> object) {
        if (this.buffer == null) {
            this.buffer = new CircularBuffer(this.size);
        }
        this.overspill.accept(this.buffer.insert(object));
    }

    public synchronized void clear() {
        this.buffer = null;
    }

    public List<Timestamped<T>> getArchive() {
        CircularBuffer<Timestamped<T>> read = this.buffer;
        if (read == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(Arrays.asList((Timestamped[])read.toArray(Timestamped[].class)));
    }
}

