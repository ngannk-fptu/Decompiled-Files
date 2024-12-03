/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import org.apache.lucene.codecs.SegmentInfoFormat;
import org.apache.lucene.codecs.SegmentInfoReader;
import org.apache.lucene.codecs.SegmentInfoWriter;
import org.apache.lucene.codecs.lucene40.Lucene40SegmentInfoReader;
import org.apache.lucene.codecs.lucene40.Lucene40SegmentInfoWriter;

public class Lucene40SegmentInfoFormat
extends SegmentInfoFormat {
    private final SegmentInfoReader reader = new Lucene40SegmentInfoReader();
    private final SegmentInfoWriter writer = new Lucene40SegmentInfoWriter();
    public static final String SI_EXTENSION = "si";
    static final String CODEC_NAME = "Lucene40SegmentInfo";
    static final int VERSION_START = 0;
    static final int VERSION_CURRENT = 0;

    @Override
    public SegmentInfoReader getSegmentInfoReader() {
        return this.reader;
    }

    @Override
    public SegmentInfoWriter getSegmentInfoWriter() {
        return this.writer;
    }
}

