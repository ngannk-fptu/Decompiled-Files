/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.fst;

import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.fst.Outputs;

public final class NoOutputs
extends Outputs<Object> {
    static final Object NO_OUTPUT = new Object(){

        public int hashCode() {
            return 42;
        }

        public boolean equals(Object other) {
            return other == this;
        }
    };
    private static final NoOutputs singleton = new NoOutputs();

    private NoOutputs() {
    }

    public static NoOutputs getSingleton() {
        return singleton;
    }

    @Override
    public Object common(Object output1, Object output2) {
        assert (output1 == NO_OUTPUT);
        assert (output2 == NO_OUTPUT);
        return NO_OUTPUT;
    }

    @Override
    public Object subtract(Object output, Object inc) {
        assert (output == NO_OUTPUT);
        assert (inc == NO_OUTPUT);
        return NO_OUTPUT;
    }

    @Override
    public Object add(Object prefix, Object output) {
        assert (prefix == NO_OUTPUT) : "got " + prefix;
        assert (output == NO_OUTPUT);
        return NO_OUTPUT;
    }

    @Override
    public Object merge(Object first, Object second) {
        assert (first == NO_OUTPUT);
        assert (second == NO_OUTPUT);
        return NO_OUTPUT;
    }

    @Override
    public void write(Object prefix, DataOutput out) {
    }

    @Override
    public Object read(DataInput in) {
        return NO_OUTPUT;
    }

    @Override
    public Object getNoOutput() {
        return NO_OUTPUT;
    }

    @Override
    public String outputToString(Object output) {
        return "";
    }
}

