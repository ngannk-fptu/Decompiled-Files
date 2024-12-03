/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.fst;

import java.io.IOException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.fst.Outputs;

public final class PositiveIntOutputs
extends Outputs<Long> {
    private static final Long NO_OUTPUT = new Long(0L);
    private static final PositiveIntOutputs singleton = new PositiveIntOutputs();

    private PositiveIntOutputs() {
    }

    public static PositiveIntOutputs getSingleton() {
        return singleton;
    }

    @Override
    public Long common(Long output1, Long output2) {
        assert (this.valid(output1));
        assert (this.valid(output2));
        if (output1 == NO_OUTPUT || output2 == NO_OUTPUT) {
            return NO_OUTPUT;
        }
        assert (output1 > 0L);
        assert (output2 > 0L);
        return Math.min(output1, output2);
    }

    @Override
    public Long subtract(Long output, Long inc) {
        assert (this.valid(output));
        assert (this.valid(inc));
        assert (output >= inc);
        if (inc == NO_OUTPUT) {
            return output;
        }
        if (output.equals(inc)) {
            return NO_OUTPUT;
        }
        return output - inc;
    }

    @Override
    public Long add(Long prefix, Long output) {
        assert (this.valid(prefix));
        assert (this.valid(output));
        if (prefix == NO_OUTPUT) {
            return output;
        }
        if (output == NO_OUTPUT) {
            return prefix;
        }
        return prefix + output;
    }

    @Override
    public void write(Long output, DataOutput out) throws IOException {
        assert (this.valid(output));
        out.writeVLong(output);
    }

    @Override
    public Long read(DataInput in) throws IOException {
        long v = in.readVLong();
        if (v == 0L) {
            return NO_OUTPUT;
        }
        return v;
    }

    private boolean valid(Long o) {
        assert (o != null);
        assert (o == NO_OUTPUT || o > 0L) : "o=" + o;
        return true;
    }

    @Override
    public Long getNoOutput() {
        return NO_OUTPUT;
    }

    @Override
    public String outputToString(Long output) {
        return output.toString();
    }

    public String toString() {
        return "PositiveIntOutputs";
    }
}

