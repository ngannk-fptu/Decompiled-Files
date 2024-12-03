/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene42;

import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.DocValuesFormat;
import org.apache.lucene.codecs.FieldInfosFormat;
import org.apache.lucene.codecs.LiveDocsFormat;
import org.apache.lucene.codecs.NormsFormat;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.codecs.SegmentInfoFormat;
import org.apache.lucene.codecs.StoredFieldsFormat;
import org.apache.lucene.codecs.TermVectorsFormat;
import org.apache.lucene.codecs.lucene40.Lucene40LiveDocsFormat;
import org.apache.lucene.codecs.lucene40.Lucene40SegmentInfoFormat;
import org.apache.lucene.codecs.lucene41.Lucene41StoredFieldsFormat;
import org.apache.lucene.codecs.lucene42.Lucene42FieldInfosFormat;
import org.apache.lucene.codecs.lucene42.Lucene42NormsFormat;
import org.apache.lucene.codecs.lucene42.Lucene42TermVectorsFormat;
import org.apache.lucene.codecs.perfield.PerFieldDocValuesFormat;
import org.apache.lucene.codecs.perfield.PerFieldPostingsFormat;

public class Lucene42Codec
extends Codec {
    private final StoredFieldsFormat fieldsFormat = new Lucene41StoredFieldsFormat();
    private final TermVectorsFormat vectorsFormat = new Lucene42TermVectorsFormat();
    private final FieldInfosFormat fieldInfosFormat = new Lucene42FieldInfosFormat();
    private final SegmentInfoFormat infosFormat = new Lucene40SegmentInfoFormat();
    private final LiveDocsFormat liveDocsFormat = new Lucene40LiveDocsFormat();
    private final PostingsFormat postingsFormat = new PerFieldPostingsFormat(){

        @Override
        public PostingsFormat getPostingsFormatForField(String field) {
            return Lucene42Codec.this.getPostingsFormatForField(field);
        }
    };
    private final DocValuesFormat docValuesFormat = new PerFieldDocValuesFormat(){

        @Override
        public DocValuesFormat getDocValuesFormatForField(String field) {
            return Lucene42Codec.this.getDocValuesFormatForField(field);
        }
    };
    private final PostingsFormat defaultFormat = PostingsFormat.forName("Lucene41");
    private final DocValuesFormat defaultDVFormat = DocValuesFormat.forName("Lucene42");
    private final NormsFormat normsFormat = new Lucene42NormsFormat();

    public Lucene42Codec() {
        super("Lucene42");
    }

    @Override
    public final StoredFieldsFormat storedFieldsFormat() {
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
    public final FieldInfosFormat fieldInfosFormat() {
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

    public DocValuesFormat getDocValuesFormatForField(String field) {
        return this.defaultDVFormat;
    }

    @Override
    public final DocValuesFormat docValuesFormat() {
        return this.docValuesFormat;
    }

    @Override
    public final NormsFormat normsFormat() {
        return this.normsFormat;
    }
}

