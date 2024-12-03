/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util.fst;

import com.atlassian.lucene36.store.DataInput;
import com.atlassian.lucene36.store.DataOutput;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Outputs<T> {
    public abstract T common(T var1, T var2);

    public abstract T subtract(T var1, T var2);

    public abstract T add(T var1, T var2);

    public abstract void write(T var1, DataOutput var2) throws IOException;

    public abstract T read(DataInput var1) throws IOException;

    public abstract T getNoOutput();

    public abstract String outputToString(T var1);

    public T merge(T first, T second) {
        throw new UnsupportedOperationException();
    }
}

