/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import java.io.IOException;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.codecs.NormsFormat;
import org.apache.lucene.codecs.lucene40.Lucene40DocValuesReader;
import org.apache.lucene.codecs.lucene40.Lucene40FieldInfosReader;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;

@Deprecated
public class Lucene40NormsFormat
extends NormsFormat {
    @Override
    public DocValuesConsumer normsConsumer(SegmentWriteState state) throws IOException {
        throw new UnsupportedOperationException("this codec can only be used for reading");
    }

    @Override
    public DocValuesProducer normsProducer(SegmentReadState state) throws IOException {
        String filename = IndexFileNames.segmentFileName(state.segmentInfo.name, "nrm", "cfs");
        return new Lucene40DocValuesReader(state, filename, Lucene40FieldInfosReader.LEGACY_NORM_TYPE_KEY);
    }
}

