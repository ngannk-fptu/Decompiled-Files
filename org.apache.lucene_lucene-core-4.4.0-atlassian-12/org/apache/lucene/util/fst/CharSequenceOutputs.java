/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.fst;

import java.io.IOException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.fst.Outputs;

public final class CharSequenceOutputs
extends Outputs<CharsRef> {
    private static final CharsRef NO_OUTPUT = new CharsRef();
    private static final CharSequenceOutputs singleton = new CharSequenceOutputs();

    private CharSequenceOutputs() {
    }

    public static CharSequenceOutputs getSingleton() {
        return singleton;
    }

    @Override
    public CharsRef common(CharsRef output1, CharsRef output2) {
        assert (output1 != null);
        assert (output2 != null);
        int pos1 = output1.offset;
        int pos2 = output2.offset;
        int stopAt1 = pos1 + Math.min(output1.length, output2.length);
        while (pos1 < stopAt1 && output1.chars[pos1] == output2.chars[pos2]) {
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
        return new CharsRef(output1.chars, output1.offset, pos1 - output1.offset);
    }

    @Override
    public CharsRef subtract(CharsRef output, CharsRef inc) {
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
        return new CharsRef(output.chars, output.offset + inc.length, output.length - inc.length);
    }

    @Override
    public CharsRef add(CharsRef prefix, CharsRef output) {
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
        CharsRef result = new CharsRef(prefix.length + output.length);
        System.arraycopy(prefix.chars, prefix.offset, result.chars, 0, prefix.length);
        System.arraycopy(output.chars, output.offset, result.chars, prefix.length, output.length);
        result.length = prefix.length + output.length;
        return result;
    }

    @Override
    public void write(CharsRef prefix, DataOutput out) throws IOException {
        assert (prefix != null);
        out.writeVInt(prefix.length);
        for (int idx = 0; idx < prefix.length; ++idx) {
            out.writeVInt(prefix.chars[prefix.offset + idx]);
        }
    }

    @Override
    public CharsRef read(DataInput in) throws IOException {
        int len = in.readVInt();
        if (len == 0) {
            return NO_OUTPUT;
        }
        CharsRef output = new CharsRef(len);
        for (int idx = 0; idx < len; ++idx) {
            output.chars[idx] = (char)in.readVInt();
        }
        output.length = len;
        return output;
    }

    @Override
    public CharsRef getNoOutput() {
        return NO_OUTPUT;
    }

    @Override
    public String outputToString(CharsRef output) {
        return output.toString();
    }
}

