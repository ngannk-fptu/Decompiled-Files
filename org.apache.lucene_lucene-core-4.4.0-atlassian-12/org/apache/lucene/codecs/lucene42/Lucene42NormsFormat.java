/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene42;

import java.io.IOException;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.codecs.NormsFormat;
import org.apache.lucene.codecs.lucene42.Lucene42DocValuesConsumer;
import org.apache.lucene.codecs.lucene42.Lucene42DocValuesProducer;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;

public final class Lucene42NormsFormat
extends NormsFormat {
    final float acceptableOverheadRatio;
    private static final String DATA_CODEC = "Lucene41NormsData";
    private static final String DATA_EXTENSION = "nvd";
    private static final String METADATA_CODEC = "Lucene41NormsMetadata";
    private static final String METADATA_EXTENSION = "nvm";

    public Lucene42NormsFormat() {
        this(7.0f);
    }

    public Lucene42NormsFormat(float acceptableOverheadRatio) {
        this.acceptableOverheadRatio = acceptableOverheadRatio;
    }

    @Override
    public DocValuesConsumer normsConsumer(SegmentWriteState state) throws IOException {
        return new Lucene42DocValuesConsumer(state, DATA_CODEC, DATA_EXTENSION, METADATA_CODEC, METADATA_EXTENSION, this.acceptableOverheadRatio);
    }

    @Override
    public DocValuesProducer normsProducer(SegmentReadState state) throws IOException {
        return new Lucene42DocValuesProducer(state, DATA_CODEC, DATA_EXTENSION, METADATA_CODEC, METADATA_EXTENSION);
    }
}

