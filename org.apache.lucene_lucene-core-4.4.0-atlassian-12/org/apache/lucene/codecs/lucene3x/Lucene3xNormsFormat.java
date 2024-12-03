/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene3x;

import java.io.IOException;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.codecs.NormsFormat;
import org.apache.lucene.codecs.lucene3x.Lucene3xNormsProducer;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;

@Deprecated
class Lucene3xNormsFormat
extends NormsFormat {
    Lucene3xNormsFormat() {
    }

    @Override
    public DocValuesConsumer normsConsumer(SegmentWriteState state) throws IOException {
        throw new UnsupportedOperationException("this codec can only be used for reading");
    }

    @Override
    public DocValuesProducer normsProducer(SegmentReadState state) throws IOException {
        return new Lucene3xNormsProducer(state.directory, state.segmentInfo, state.fieldInfos, state.context);
    }
}

