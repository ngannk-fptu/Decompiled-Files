/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import java.io.IOException;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.codecs.DocValuesFormat;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.codecs.lucene40.Lucene40DocValuesReader;
import org.apache.lucene.codecs.lucene40.Lucene40FieldInfosReader;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;

@Deprecated
public class Lucene40DocValuesFormat
extends DocValuesFormat {
    static final String VAR_INTS_CODEC_NAME = "PackedInts";
    static final int VAR_INTS_VERSION_START = 0;
    static final int VAR_INTS_VERSION_CURRENT = 0;
    static final byte VAR_INTS_PACKED = 0;
    static final byte VAR_INTS_FIXED_64 = 1;
    static final String INTS_CODEC_NAME = "Ints";
    static final int INTS_VERSION_START = 0;
    static final int INTS_VERSION_CURRENT = 0;
    static final String FLOATS_CODEC_NAME = "Floats";
    static final int FLOATS_VERSION_START = 0;
    static final int FLOATS_VERSION_CURRENT = 0;
    static final String BYTES_FIXED_STRAIGHT_CODEC_NAME = "FixedStraightBytes";
    static final int BYTES_FIXED_STRAIGHT_VERSION_START = 0;
    static final int BYTES_FIXED_STRAIGHT_VERSION_CURRENT = 0;
    static final String BYTES_VAR_STRAIGHT_CODEC_NAME_IDX = "VarStraightBytesIdx";
    static final String BYTES_VAR_STRAIGHT_CODEC_NAME_DAT = "VarStraightBytesDat";
    static final int BYTES_VAR_STRAIGHT_VERSION_START = 0;
    static final int BYTES_VAR_STRAIGHT_VERSION_CURRENT = 0;
    static final String BYTES_FIXED_DEREF_CODEC_NAME_IDX = "FixedDerefBytesIdx";
    static final String BYTES_FIXED_DEREF_CODEC_NAME_DAT = "FixedDerefBytesDat";
    static final int BYTES_FIXED_DEREF_VERSION_START = 0;
    static final int BYTES_FIXED_DEREF_VERSION_CURRENT = 0;
    static final String BYTES_VAR_DEREF_CODEC_NAME_IDX = "VarDerefBytesIdx";
    static final String BYTES_VAR_DEREF_CODEC_NAME_DAT = "VarDerefBytesDat";
    static final int BYTES_VAR_DEREF_VERSION_START = 0;
    static final int BYTES_VAR_DEREF_VERSION_CURRENT = 0;
    static final String BYTES_FIXED_SORTED_CODEC_NAME_IDX = "FixedSortedBytesIdx";
    static final String BYTES_FIXED_SORTED_CODEC_NAME_DAT = "FixedSortedBytesDat";
    static final int BYTES_FIXED_SORTED_VERSION_START = 0;
    static final int BYTES_FIXED_SORTED_VERSION_CURRENT = 0;
    static final String BYTES_VAR_SORTED_CODEC_NAME_IDX = "VarDerefBytesIdx";
    static final String BYTES_VAR_SORTED_CODEC_NAME_DAT = "VarDerefBytesDat";
    static final int BYTES_VAR_SORTED_VERSION_START = 0;
    static final int BYTES_VAR_SORTED_VERSION_CURRENT = 0;

    public Lucene40DocValuesFormat() {
        super("Lucene40");
    }

    @Override
    public DocValuesConsumer fieldsConsumer(SegmentWriteState state) throws IOException {
        throw new UnsupportedOperationException("this codec can only be used for reading");
    }

    @Override
    public DocValuesProducer fieldsProducer(SegmentReadState state) throws IOException {
        String filename = IndexFileNames.segmentFileName(state.segmentInfo.name, "dv", "cfs");
        return new Lucene40DocValuesReader(state, filename, Lucene40FieldInfosReader.LEGACY_DV_TYPE_KEY);
    }
}

