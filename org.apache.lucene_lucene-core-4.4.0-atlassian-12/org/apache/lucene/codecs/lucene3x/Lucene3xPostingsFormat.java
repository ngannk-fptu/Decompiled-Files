/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene3x;

import java.io.IOException;
import org.apache.lucene.codecs.FieldsConsumer;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.codecs.lucene3x.Lucene3xFields;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;

@Deprecated
class Lucene3xPostingsFormat
extends PostingsFormat {
    public static final String TERMS_EXTENSION = "tis";
    public static final String TERMS_INDEX_EXTENSION = "tii";
    public static final String FREQ_EXTENSION = "frq";
    public static final String PROX_EXTENSION = "prx";

    public Lucene3xPostingsFormat() {
        super("Lucene3x");
    }

    @Override
    public FieldsConsumer fieldsConsumer(SegmentWriteState state) throws IOException {
        throw new UnsupportedOperationException("this codec can only be used for reading");
    }

    @Override
    public FieldsProducer fieldsProducer(SegmentReadState state) throws IOException {
        return new Lucene3xFields(state.directory, state.fieldInfos, state.segmentInfo, state.context, state.termsIndexDivisor);
    }
}

