/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.fst;

import java.io.IOException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;

public abstract class Outputs<T> {
    public abstract T common(T var1, T var2);

    public abstract T subtract(T var1, T var2);

    public abstract T add(T var1, T var2);

    public abstract void write(T var1, DataOutput var2) throws IOException;

    public void writeFinalOutput(T output, DataOutput out) throws IOException {
        this.write(output, out);
    }

    public abstract T read(DataInput var1) throws IOException;

    public T readFinalOutput(DataInput in) throws IOException {
        return this.read(in);
    }

    public abstract T getNoOutput();

    public abstract String outputToString(T var1);

    public T merge(T first, T second) {
        throw new UnsupportedOperationException();
    }
}

