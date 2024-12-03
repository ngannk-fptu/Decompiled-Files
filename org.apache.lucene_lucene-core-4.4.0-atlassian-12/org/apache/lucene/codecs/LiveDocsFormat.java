/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.IOException;
import java.util.Collection;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.MutableBits;

public abstract class LiveDocsFormat {
    protected LiveDocsFormat() {
    }

    public abstract MutableBits newLiveDocs(int var1) throws IOException;

    public abstract MutableBits newLiveDocs(Bits var1) throws IOException;

    public abstract Bits readLiveDocs(Directory var1, SegmentInfoPerCommit var2, IOContext var3) throws IOException;

    public abstract void writeLiveDocs(MutableBits var1, Directory var2, SegmentInfoPerCommit var3, int var4, IOContext var5) throws IOException;

    public abstract void files(SegmentInfoPerCommit var1, Collection<String> var2) throws IOException;
}

