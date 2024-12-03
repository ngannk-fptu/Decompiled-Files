/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util.fst;

import com.atlassian.lucene36.store.DataInput;
import com.atlassian.lucene36.store.DataOutput;
import com.atlassian.lucene36.util.fst.Outputs;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class PositiveIntOutputs
extends Outputs<Long> {
    private static final Long NO_OUTPUT = new Long(0L);
    private final boolean doShare;
    private static final PositiveIntOutputs singletonShare = new PositiveIntOutputs(true);
    private static final PositiveIntOutputs singletonNoShare = new PositiveIntOutputs(false);

    private PositiveIntOutputs(boolean doShare) {
        this.doShare = doShare;
    }

    public static PositiveIntOutputs getSingleton(boolean doShare) {
        return doShare ? singletonShare : singletonNoShare;
    }

    @Override
    public Long common(Long output1, Long output2) {
        assert (this.valid(output1));
        assert (this.valid(output2));
        if (output1 == NO_OUTPUT || output2 == NO_OUTPUT) {
            return NO_OUTPUT;
        }
        if (this.doShare) {
            assert (output1 > 0L);
            assert (output2 > 0L);
            return Math.min(output1, output2);
        }
        if (output1.equals(output2)) {
            return output1;
        }
        return NO_OUTPUT;
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
        assert (o instanceof Long);
        assert (o == NO_OUTPUT || o > 0L);
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
        return "PositiveIntOutputs(doShare=" + this.doShare + ")";
    }
}

