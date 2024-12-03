/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.fst;

import java.io.IOException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.fst.Outputs;

public final class ByteSequenceOutputs
extends Outputs<BytesRef> {
    private static final BytesRef NO_OUTPUT = new BytesRef();
    private static final ByteSequenceOutputs singleton = new ByteSequenceOutputs();

    private ByteSequenceOutputs() {
    }

    public static ByteSequenceOutputs getSingleton() {
        return singleton;
    }

    @Override
    public BytesRef common(BytesRef output1, BytesRef output2) {
        assert (output1 != null);
        assert (output2 != null);
        int pos1 = output1.offset;
        int pos2 = output2.offset;
        int stopAt1 = pos1 + Math.min(output1.length, output2.length);
        while (pos1 < stopAt1 && output1.bytes[pos1] == output2.bytes[pos2]) {
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
        return new BytesRef(output1.bytes, output1.offset, pos1 - output1.offset);
    }

    @Override
    public BytesRef subtract(BytesRef output, BytesRef inc) {
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
        return new BytesRef(output.bytes, output.offset + inc.length, output.length - inc.length);
    }

    @Override
    public BytesRef add(BytesRef prefix, BytesRef output) {
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
        BytesRef result = new BytesRef(prefix.length + output.length);
        System.arraycopy(prefix.bytes, prefix.offset, result.bytes, 0, prefix.length);
        System.arraycopy(output.bytes, output.offset, result.bytes, prefix.length, output.length);
        result.length = prefix.length + output.length;
        return result;
    }

    @Override
    public void write(BytesRef prefix, DataOutput out) throws IOException {
        assert (prefix != null);
        out.writeVInt(prefix.length);
        out.writeBytes(prefix.bytes, prefix.offset, prefix.length);
    }

    @Override
    public BytesRef read(DataInput in) throws IOException {
        int len = in.readVInt();
        if (len == 0) {
            return NO_OUTPUT;
        }
        BytesRef output = new BytesRef(len);
        in.readBytes(output.bytes, 0, len);
        output.length = len;
        return output;
    }

    @Override
    public BytesRef getNoOutput() {
        return NO_OUTPUT;
    }

    @Override
    public String outputToString(BytesRef output) {
        return output.toString();
    }
}

