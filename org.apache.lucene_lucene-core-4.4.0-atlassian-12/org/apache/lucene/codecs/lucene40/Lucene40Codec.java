/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.DocValuesFormat;
import org.apache.lucene.codecs.FieldInfosFormat;
import org.apache.lucene.codecs.LiveDocsFormat;
import org.apache.lucene.codecs.NormsFormat;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.codecs.SegmentInfoFormat;
import org.apache.lucene.codecs.StoredFieldsFormat;
import org.apache.lucene.codecs.TermVectorsFormat;
import org.apache.lucene.codecs.lucene40.Lucene40DocValuesFormat;
import org.apache.lucene.codecs.lucene40.Lucene40FieldInfosFormat;
import org.apache.lucene.codecs.lucene40.Lucene40LiveDocsFormat;
import org.apache.lucene.codecs.lucene40.Lucene40NormsFormat;
import org.apache.lucene.codecs.lucene40.Lucene40SegmentInfoFormat;
import org.apache.lucene.codecs.lucene40.Lucene40StoredFieldsFormat;
import org.apache.lucene.codecs.lucene40.Lucene40TermVectorsFormat;
import org.apache.lucene.codecs.perfield.PerFieldPostingsFormat;

@Deprecated
public class Lucene40Codec
extends Codec {
    private final StoredFieldsFormat fieldsFormat = new Lucene40StoredFieldsFormat();
    private final TermVectorsFormat vectorsFormat = new Lucene40TermVectorsFormat();
    private final FieldInfosFormat fieldInfosFormat = new Lucene40FieldInfosFormat();
    private final SegmentInfoFormat infosFormat = new Lucene40SegmentInfoFormat();
    private final LiveDocsFormat liveDocsFormat = new Lucene40LiveDocsFormat();
    private final PostingsFormat postingsFormat = new PerFieldPostingsFormat(){

        @Override
        public PostingsFormat getPostingsFormatForField(String field) {
            return Lucene40Codec.this.getPostingsFormatForField(field);
        }
    };
    private final DocValuesFormat defaultDVFormat = new Lucene40DocValuesFormat();
    private final NormsFormat normsFormat = new Lucene40NormsFormat();
    private final PostingsFormat defaultFormat = PostingsFormat.forName("Lucene40");

    public Lucene40Codec() {
        super("Lucene40");
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
    public FieldInfosFormat fieldInfosFormat() {
        return this.fieldInfosFormat;
    }

    @Override
    public final SegmentInfoFormat segmentInfoFormat() {
        return this.infosFormat;
    }

    @Override
    public DocValuesFormat docValuesFormat() {
        return this.defaultDVFormat;
    }

    @Override
    public NormsFormat normsFormat() {
        return this.normsFormat;
    }

    @Override
    public final LiveDocsFormat liveDocsFormat() {
        return this.liveDocsFormat;
    }

    public PostingsFormat getPostingsFormatForField(String field) {
        return this.defaultFormat;
    }
}

