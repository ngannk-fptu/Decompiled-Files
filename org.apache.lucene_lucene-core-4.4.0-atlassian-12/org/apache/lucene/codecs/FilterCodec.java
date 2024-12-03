/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.DocValuesFormat;
import org.apache.lucene.codecs.FieldInfosFormat;
import org.apache.lucene.codecs.LiveDocsFormat;
import org.apache.lucene.codecs.NormsFormat;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.codecs.SegmentInfoFormat;
import org.apache.lucene.codecs.StoredFieldsFormat;
import org.apache.lucene.codecs.TermVectorsFormat;

public abstract class FilterCodec
extends Codec {
    protected final Codec delegate;

    protected FilterCodec(String name, Codec delegate) {
        super(name);
        this.delegate = delegate;
    }

    @Override
    public DocValuesFormat docValuesFormat() {
        return this.delegate.docValuesFormat();
    }

    @Override
    public FieldInfosFormat fieldInfosFormat() {
        return this.delegate.fieldInfosFormat();
    }

    @Override
    public LiveDocsFormat liveDocsFormat() {
        return this.delegate.liveDocsFormat();
    }

    @Override
    public NormsFormat normsFormat() {
        return this.delegate.normsFormat();
    }

    @Override
    public PostingsFormat postingsFormat() {
        return this.delegate.postingsFormat();
    }

    @Override
    public SegmentInfoFormat segmentInfoFormat() {
        return this.delegate.segmentInfoFormat();
    }

    @Override
    public StoredFieldsFormat storedFieldsFormat() {
        return this.delegate.storedFieldsFormat();
    }

    @Override
    public TermVectorsFormat termVectorsFormat() {
        return this.delegate.termVectorsFormat();
    }
}

