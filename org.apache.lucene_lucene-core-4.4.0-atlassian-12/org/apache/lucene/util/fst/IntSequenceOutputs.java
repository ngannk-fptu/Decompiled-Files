/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.fst;

import java.io.IOException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.fst.Outputs;

public final class IntSequenceOutputs
extends Outputs<IntsRef> {
    private static final IntsRef NO_OUTPUT = new IntsRef();
    private static final IntSequenceOutputs singleton = new IntSequenceOutputs();

    private IntSequenceOutputs() {
    }

    public static IntSequenceOutputs getSingleton() {
        return singleton;
    }

    @Override
    public IntsRef common(IntsRef output1, IntsRef output2) {
        assert (output1 != null);
        assert (output2 != null);
        int pos1 = output1.offset;
        int pos2 = output2.offset;
        int stopAt1 = pos1 + Math.min(output1.length, output2.length);
        while (pos1 < stopAt1 && output1.ints[pos1] == output2.ints[pos2]) {
            ++pos1;
            ++pos2;
        }
        if (pos1 == output1.offset) {
            return NO_OUTPUT;
        }
        if (pos1 == output1.offset + output1.length) {
            return output1;
        }
        if (pos2 == output2.offset + output2.length) {
            return output2;
        }
        return new IntsRef(output1.ints, output1.offset, pos1 - output1.offset);
    }

    @Override
    public IntsRef subtract(IntsRef output, IntsRef inc) {
        assert (output != null);
        assert (inc != null);
        if (inc == NO_OUTPUT) {
            return output;
        }
        if (inc.length == output.length) {
            return NO_OUTPUT;
        }
        assert (inc.length < output.length) : "inc.length=" + inc.length + " vs output.length=" + output.length;
        assert (inc.length > 0);
        return new IntsRef(output.ints, output.offset + inc.length, output.length - inc.length);
    }

    @Override
    public IntsRef add(IntsRef prefix, IntsRef output) {
        assert (prefix != null);
        assert (output != null);
        if (prefix == NO_OUTPUT) {
            return output;
        }
        if (output == NO_OUTPUT) {
            return prefix;
        }
        assert (prefix.length > 0);
        assert (output.length > 0);
        IntsRef result = new IntsRef(prefix.length + output.length);
        System.arraycopy(prefix.ints, prefix.offset, result.ints, 0, prefix.length);
        System.arraycopy(output.ints, output.offset, result.ints, prefix.length, output.length);
        result.length = prefix.length + output.length;
        return result;
    }

    @Override
    public void write(IntsRef prefix, DataOutput out) throws IOException {
        assert (prefix != null);
        out.writeVInt(prefix.length);
        for (int idx = 0; idx < prefix.length; ++idx) {
            out.writeVInt(prefix.ints[prefix.offset + idx]);
        }
    }

    @Override
    public IntsRef read(DataInput in) throws IOException {
        int len = in.readVInt();
        if (len == 0) {
            return NO_OUTPUT;
        }
        IntsRef output = new IntsRef(len);
        for (int idx = 0; idx < len; ++idx) {
            output.ints[idx] = in.readVInt();
        }
        output.length = len;
        return output;
    }

    @Override
    public IntsRef getNoOutput() {
        return NO_OUTPUT;
    }

    @Override
    public String outputToString(IntsRef output) {
        return output.toString();
    }
}

