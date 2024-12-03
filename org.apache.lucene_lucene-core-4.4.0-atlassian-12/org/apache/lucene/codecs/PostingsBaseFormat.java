/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.IOException;
import org.apache.lucene.codecs.PostingsReaderBase;
import org.apache.lucene.codecs.PostingsWriterBase;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;

public abstract class PostingsBaseFormat {
    public final String name;

    protected PostingsBaseFormat(String name) {
        this.name = name;
    }

    public abstract PostingsReaderBase postingsReaderBase(SegmentReadState var1) throws IOException;

    public abstract PostingsWriterBase postingsWriterBase(SegmentWriteState var1) throws IOException;
}

