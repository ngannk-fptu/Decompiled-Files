/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene42;

import java.io.IOException;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.codecs.DocValuesFormat;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.codecs.lucene42.Lucene42DocValuesConsumer;
import org.apache.lucene.codecs.lucene42.Lucene42DocValuesProducer;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;

public final class Lucene42DocValuesFormat
extends DocValuesFormat {
    final float acceptableOverheadRatio;
    private static final String DATA_CODEC = "Lucene42DocValuesData";
    private static final String DATA_EXTENSION = "dvd";
    private static final String METADATA_CODEC = "Lucene42DocValuesMetadata";
    private static final String METADATA_EXTENSION = "dvm";

    public Lucene42DocValuesFormat() {
        this(0.2f);
    }

    public Lucene42DocValuesFormat(float acceptableOverheadRatio) {
        super("Lucene42");
        this.acceptableOverheadRatio = acceptableOverheadRatio;
    }

    @Override
    public DocValuesConsumer fieldsConsumer(SegmentWriteState state) throws IOException {
        return new Lucene42DocValuesConsumer(state, DATA_CODEC, DATA_EXTENSION, METADATA_CODEC, METADATA_EXTENSION, this.acceptableOverheadRatio);
    }

    @Override
    public DocValuesProducer fieldsProducer(SegmentReadState state) throws IOException {
        return new Lucene42DocValuesProducer(state, DATA_CODEC, DATA_EXTENSION, METADATA_CODEC, METADATA_EXTENSION);
    }
}

