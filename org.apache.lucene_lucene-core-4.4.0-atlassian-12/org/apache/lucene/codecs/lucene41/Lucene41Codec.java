/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene41;

import java.io.IOException;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.DocValuesFormat;
import org.apache.lucene.codecs.FieldInfosFormat;
import org.apache.lucene.codecs.LiveDocsFormat;
import org.apache.lucene.codecs.NormsFormat;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.codecs.SegmentInfoFormat;
import org.apache.lucene.codecs.StoredFieldsFormat;
import org.apache.lucene.codecs.StoredFieldsWriter;
import org.apache.lucene.codecs.TermVectorsFormat;
import org.apache.lucene.codecs.compressing.CompressingStoredFieldsFormat;
import org.apache.lucene.codecs.compressing.CompressionMode;
import org.apache.lucene.codecs.lucene40.Lucene40DocValuesFormat;
import org.apache.lucene.codecs.lucene40.Lucene40FieldInfosFormat;
import org.apache.lucene.codecs.lucene40.Lucene40LiveDocsFormat;
import org.apache.lucene.codecs.lucene40.Lucene40NormsFormat;
import org.apache.lucene.codecs.lucene40.Lucene40SegmentInfoFormat;
import org.apache.lucene.codecs.lucene40.Lucene40TermVectorsFormat;
import org.apache.lucene.codecs.perfield.PerFieldPostingsFormat;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;

@Deprecated
public class Lucene41Codec
extends Codec {
    private final StoredFieldsFormat fieldsFormat = new CompressingStoredFieldsFormat("Lucene41StoredFields", CompressionMode.FAST, 16384){

        @Override
        public StoredFieldsWriter fieldsWriter(Directory directory, SegmentInfo si, IOContext context) throws IOException {
            throw new UnsupportedOperationException("this codec can only be used for reading");
        }
    };
    private final TermVectorsFormat vectorsFormat = new Lucene40TermVectorsFormat();
    private final FieldInfosFormat fieldInfosFormat = new Lucene40FieldInfosFormat();
    private final SegmentInfoFormat infosFormat = new Lucene40SegmentInfoFormat();
    private final LiveDocsFormat liveDocsFormat = new Lucene40LiveDocsFormat();
    private final PostingsFormat postingsFormat = new PerFieldPostingsFormat(){

        @Override
        public PostingsFormat getPostingsFormatForField(String field) {
            return Lucene41Codec.this.getPostingsFormatForField(field);
        }
    };
    private final PostingsFormat defaultFormat = PostingsFormat.forName("Lucene41");
    private final DocValuesFormat dvFormat = new Lucene40DocValuesFormat();
    private final NormsFormat normsFormat = new Lucene40NormsFormat();

    public Lucene41Codec() {
        super("Lucene41");
    }

    @Override
    public StoredFieldsFormat storedFieldsFormat() {
        return this.fieldsFormat;
    }

    @Override
    public final TermVectorsFormat termVectorsFormat() {
        return this.vectorsFormat;
    }

    @Override
    public final PostingsFormat postingsFormat() {
        return this.postingsFormat;
    }

    @Override
    public FieldInfosFormat fieldInfosFormat() {
        return this.fieldInfosFormat;
    }

    @Override
    public final SegmentInfoFormat segmentInfoFormat() {
        return this.infosFormat;
    }

    @Override
    public final LiveDocsFormat liveDocsFormat() {
        return this.liveDocsFormat;
    }

    public PostingsFormat getPostingsFormatForField(String field) {
        return this.defaultFormat;
    }

    @Override
    public DocValuesFormat docValuesFormat() {
        return this.dvFormat;
    }

    @Override
    public NormsFormat normsFormat() {
        return this.normsFormat;
    }
}

