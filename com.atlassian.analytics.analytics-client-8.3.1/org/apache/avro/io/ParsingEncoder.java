/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io;

import java.io.IOException;
import java.util.Arrays;
import org.apache.avro.AvroTypeException;
import org.apache.avro.io.Encoder;

public abstract class ParsingEncoder
extends Encoder {
    private long[] counts = new long[10];
    protected int pos = -1;

    @Override
    public void setItemCount(long itemCount) throws IOException {
        if (this.counts[this.pos] != 0L) {
            throw new AvroTypeException("Incorrect number of items written. " + this.counts[this.pos] + " more required.");
        }
        this.counts[this.pos] = itemCount;
    }

    @Override
    public void startItem() throws IOException {
        int n = this.pos;
        this.counts[n] = this.counts[n] - 1L;
    }

    protected final void push() {
        if (++this.pos == this.counts.length) {
            this.counts = Arrays.copyOf(this.counts, this.pos + 10);
        }
        this.counts[this.pos] = 0L;
    }

    protected final void pop() {
        if (this.counts[this.pos] != 0L) {
            throw new AvroTypeException("Incorrect number of items written. " + this.counts[this.pos] + " more required.");
        }
        --this.pos;
    }

    protected final int depth() {
        return this.pos;
    }
}

