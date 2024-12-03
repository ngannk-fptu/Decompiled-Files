/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import java.io.IOException;
import org.apache.lucene.codecs.PostingsBaseFormat;
import org.apache.lucene.codecs.PostingsReaderBase;
import org.apache.lucene.codecs.PostingsWriterBase;
import org.apache.lucene.codecs.lucene40.Lucene40PostingsReader;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;

@Deprecated
public final class Lucene40PostingsBaseFormat
extends PostingsBaseFormat {
    public Lucene40PostingsBaseFormat() {
        super("Lucene40");
    }

    @Override
    public PostingsReaderBase postingsReaderBase(SegmentReadState state) throws IOException {
        return new Lucene40PostingsReader(state.directory, state.fieldInfos, state.segmentInfo, state.context, state.segmentSuffix);
    }

    @Override
    public PostingsWriterBase postingsWriterBase(SegmentWriteState state) throws IOException {
        throw new UnsupportedOperationException("this codec can only be used for reading");
    }
}

