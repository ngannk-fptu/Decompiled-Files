/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene41;

import java.io.IOException;
import org.apache.lucene.codecs.PostingsBaseFormat;
import org.apache.lucene.codecs.PostingsReaderBase;
import org.apache.lucene.codecs.PostingsWriterBase;
import org.apache.lucene.codecs.lucene41.Lucene41PostingsReader;
import org.apache.lucene.codecs.lucene41.Lucene41PostingsWriter;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;

public final class Lucene41PostingsBaseFormat
extends PostingsBaseFormat {
    public Lucene41PostingsBaseFormat() {
        super("Lucene41");
    }

    @Override
    public PostingsReaderBase postingsReaderBase(SegmentReadState state) throws IOException {
        return new Lucene41PostingsReader(state.directory, state.fieldInfos, state.segmentInfo, state.context, state.segmentSuffix);
    }

    @Override
    public PostingsWriterBase postingsWriterBase(SegmentWriteState state) throws IOException {
        return new Lucene41PostingsWriter(state);
    }
}

