/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.Term;
import java.io.Closeable;
import java.io.IOException;

public abstract class TermEnum
implements Closeable {
    public abstract boolean next() throws IOException;

    public abstract Term term();

    public abstract int docFreq();

    public abstract void close() throws IOException;
}

