/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.store.DataInput
 *  org.apache.lucene.store.DataOutput
 *  org.apache.lucene.util.fst.Outputs
 */
package org.apache.lucene.util.fst;

import java.io.IOException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.fst.Outputs;

public final class UpToTwoPositiveIntOutputs
extends Outputs<Object> {
    private static final Long NO_OUTPUT = new Long(0L);
    private final boolean doShare;
    private static final UpToTwoPositiveIntOutputs singletonShare = new UpToTwoPositiveIntOutputs(true);
    private static final UpToTwoPositiveIntOutputs singletonNoShare = new UpToTwoPositiveIntOutputs(false);

    private UpToTwoPositiveIntOutputs(boolean doShare) {
        this.doShare = doShare;
    }

    public static UpToTwoPositiveIntOutputs getSingleton(boolean doShare) {
        return doShare ? singletonShare : singletonNoShare;
    }

    public Long get(long v) {
        if (v == 0L) {
            return NO_OUTPUT;
        }
        return v;
    }

    public TwoLongs get(long first, long second) {
        return new TwoLongs(first, second);
    }

    public Long common(Object _output1, Object _output2) {
        assert (this.valid(_output1, false));
        assert (this.valid(_output2, false));
        Long output1 = (Long)_output1;
        Long output2 = (Long)_output2;
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

    public Long subtract(Object _output, Object _inc) {
        assert (this.valid(_output, false));
        assert (this.valid(_inc, false));
        Long output = (Long)_output;
        Long inc = (Long)_inc;
        assert (output >= inc);
        if (inc == NO_OUTPUT) {
            return output;
        }
        if (output.equals(inc)) {
            return NO_OUTPUT;
        }
        return output - inc;
    }

    public Object add(Object _prefix, Object _output) {
        assert (this.valid(_prefix, false));
        assert (this.valid(_output, true));
        Long prefix = (Long)_prefix;
        if (_output instanceof Long) {
            Long output = (Long)_output;
            if (prefix == NO_OUTPUT) {
                return output;
            }
            if (output == NO_OUTPUT) {
                return prefix;
            }
            return prefix + output;
        }
        TwoLongs output = (TwoLongs)_output;
        long v = prefix;
        return new TwoLongs(output.first + v, output.second + v);
    }

    public void write(Object _output, DataOutput out) throws IOException {
        assert (this.valid(_output, true));
        if (_output instanceof Long) {
            Long output = (Long)_output;
            out.writeVLong(output << 1);
        } else {
            TwoLongs output = (TwoLongs)_output;
            out.writeVLong(output.first << 1 | 1L);
            out.writeVLong(output.second);
        }
    }

    public Object read(DataInput in) throws IOException {
        long code = in.readVLong();
        if ((code & 1L) == 0L) {
            long v = code >>> 1;
            if (v == 0L) {
                return NO_OUTPUT;
            }
            return v;
        }
        long first = code >>> 1;
        long second = in.readVLong();
        return new TwoLongs(first, second);
    }

    private boolean valid(Long o) {
        assert (o != null);
        assert (o instanceof Long);
        assert (o == NO_OUTPUT || o > 0L);
        return true;
    }

    private boolean valid(Object _o, boolean allowDouble) {
        if (!allowDouble) {
            assert (_o instanceof Long);
            return this.valid((Long)_o);
        }
        if (_o instanceof TwoLongs) {
            return true;
        }
        return this.valid((Long)_o);
    }

    public Object getNoOutput() {
        return NO_OUTPUT;
    }

    public String outputToString(Object output) {
        return output.toString();
    }

    public Object merge(Object first, Object second) {
        assert (this.valid(first, false));
        assert (this.valid(second, false));
        return new TwoLongs((Long)first, (Long)second);
    }

    public static final class TwoLongs {
        public final long first;
        public final long second;

        public TwoLongs(long first, long second) {
            this.first = first;
            this.second = second;
            assert (first >= 0L);
            assert (second >= 0L);
        }

        public String toString() {
            return "TwoLongs:" + this.first + "," + this.second;
        }

        public boolean equals(Object _other) {
            if (_other instanceof TwoLongs) {
                TwoLongs other = (TwoLongs)_other;
                return this.first == other.first && this.second == other.second;
            }
            return false;
        }

        public int hashCode() {
            return (int)(this.first ^ this.first >>> 32 ^ (this.second ^ this.second >> 32));
        }
    }
}

