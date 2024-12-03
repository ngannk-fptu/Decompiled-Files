/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermEnum;
import java.io.Closeable;
import java.io.IOException;

public interface TermDocs
extends Closeable {
    public void seek(Term var1) throws IOException;

    public void seek(TermEnum var1) throws IOException;

    public int doc();

    public int freq();

    public boolean next() throws IOException;

    public int read(int[] var1, int[] var2) throws IOException;

    public boolean skipTo(int var1) throws IOException;

    public void close() throws IOException;
}

