/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.Closeable;
import java.io.IOException;
import org.apache.lucene.codecs.BlockTermState;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.Bits;

public abstract class PostingsReaderBase
implements Closeable {
    protected PostingsReaderBase() {
    }

    public abstract void init(IndexInput var1) throws IOException;

    public abstract BlockTermState newTermState() throws IOException;

    public abstract void nextTerm(FieldInfo var1, BlockTermState var2) throws IOException;

    public abstract DocsEnum docs(FieldInfo var1, BlockTermState var2, Bits var3, DocsEnum var4, int var5) throws IOException;

    public abstract DocsAndPositionsEnum docsAndPositions(FieldInfo var1, BlockTermState var2, Bits var3, DocsAndPositionsEnum var4, int var5) throws IOException;

    @Override
    public abstract void close() throws IOException;

    public abstract void readTermsBlock(IndexInput var1, FieldInfo var2, BlockTermState var3) throws IOException;
}

