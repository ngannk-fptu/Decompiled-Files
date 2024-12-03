/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.fst;

import java.io.IOException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.fst.Outputs;

public class PairOutputs<A, B>
extends Outputs<Pair<A, B>> {
    private final Pair<A, B> NO_OUTPUT;
    private final Outputs<A> outputs1;
    private final Outputs<B> outputs2;

    public PairOutputs(Outputs<A> outputs1, Outputs<B> outputs2) {
        this.outputs1 = outputs1;
        this.outputs2 = outputs2;
        this.NO_OUTPUT = new Pair(outputs1.getNoOutput(), outputs2.getNoOutput());
    }

    public Pair<A, B> newPair(A a, B b) {
        if (a.equals(this.outputs1.getNoOutput())) {
            a = this.outputs1.getNoOutput();
        }
        if (b.equals(this.outputs2.getNoOutput())) {
            b = this.outputs2.getNoOutput();
        }
        if (a == this.outputs1.getNoOutput() && b == this.outputs2.getNoOutput()) {
            return this.NO_OUTPUT;
        }
        Pair p = new Pair(a, b);
        assert (this.valid(p));
        return p;
    }

    private boolean valid(Pair<A, B> pair) {
        boolean noOutput1 = pair.output1.equals(this.outputs1.getNoOutput());
        boolean noOutput2 = pair.output2.equals(this.outputs2.getNoOutput());
        if (noOutput1 && pair.output1 != this.outputs1.getNoOutput()) {
            return false;
        }
        if (noOutput2 && pair.output2 != this.outputs2.getNoOutput()) {
            return false;
        }
        if (noOutput1 && noOutput2) {
            return pair == this.NO_OUTPUT;
        }
        return true;
    }

    @Override
    public Pair<A, B> common(Pair<A, B> pair1, Pair<A, B> pair2) {
        assert (this.valid(pair1));
        assert (this.valid(pair2));
        return this.newPair(this.outputs1.common(pair1.output1, pair2.output1), this.outputs2.common(pair1.output2, pair2.output2));
    }

    @Override
    public Pair<A, B> subtract(Pair<A, B> output, Pair<A, B> inc) {
        assert (this.valid(output));
        assert (this.valid(inc));
        return this.newPair(this.outputs1.subtract(output.output1, inc.output1), this.outputs2.subtract(output.output2, inc.output2));
    }

    @Override
    public Pair<A, B> add(Pair<A, B> prefix, Pair<A, B> output) {
        assert (this.valid(prefix));
        assert (this.valid(output));
        return this.newPair(this.outputs1.add(prefix.output1, output.output1), this.outputs2.add(prefix.output2, output.output2));
    }

    @Override
    public void write(Pair<A, B> output, DataOutput writer) throws IOException {
        assert (this.valid(output));
        this.outputs1.write(output.output1, writer);
        this.outputs2.write(output.output2, writer);
    }

    @Override
    public Pair<A, B> read(DataInput in) throws IOException {
        A output1 = this.outputs1.read(in);
        B output2 = this.outputs2.read(in);
        return this.newPair(output1, output2);
    }

    @Override
    public Pair<A, B> getNoOutput() {
        return this.NO_OUTPUT;
    }

    @Override
    public String outputToString(Pair<A, B> output) {
        assert (this.valid(output));
        return "<pair:" + this.outputs1.outputToString(output.output1) + "," + this.outputs2.outputToString(output.output2) + ">";
    }

    public String toString() {
        return "PairOutputs<" + this.outputs1 + "," + this.outputs2 + ">";
    }

    public static class Pair<A, B> {
        public final A output1;
        public final B output2;

        private Pair(A output1, B output2) {
            this.output1 = output1;
            this.output2 = output2;
        }

        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (other instanceof Pair) {
                Pair pair = (Pair)other;
                return this.output1.equals(pair.output1) && this.output2.equals(pair.output2);
            }
            return false;
        }

        public int hashCode() {
            return this.output1.hashCode() + this.output2.hashCode();
        }
    }
}

