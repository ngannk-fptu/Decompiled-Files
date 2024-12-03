/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene3x;

import org.apache.lucene.codecs.SegmentInfoFormat;
import org.apache.lucene.codecs.SegmentInfoReader;
import org.apache.lucene.codecs.SegmentInfoWriter;
import org.apache.lucene.codecs.lucene3x.Lucene3xSegmentInfoReader;
import org.apache.lucene.index.SegmentInfo;

@Deprecated
public class Lucene3xSegmentInfoFormat
extends SegmentInfoFormat {
    private final SegmentInfoReader reader = new Lucene3xSegmentInfoReader();
    public static final int FORMAT_DIAGNOSTICS = -9;
    public static final int FORMAT_HAS_VECTORS = -10;
    public static final int FORMAT_3_1 = -11;
    public static final String UPGRADED_SI_EXTENSION = "si";
    public static final String UPGRADED_SI_CODEC_NAME = "Lucene3xSegmentInfo";
    public static final int UPGRADED_SI_VERSION_START = 0;
    public static final int UPGRADED_SI_VERSION_CURRENT = 0;
    public static final String DS_OFFSET_KEY = Lucene3xSegmentInfoFormat.class.getSimpleName() + ".dsoffset";
    public static final String DS_NAME_KEY = Lucene3xSegmentInfoFormat.class.getSimpleName() + ".dsname";
    public static final String DS_COMPOUND_KEY = Lucene3xSegmentInfoFormat.class.getSimpleName() + ".dscompound";
    public static final String NORMGEN_KEY = Lucene3xSegmentInfoFormat.class.getSimpleName() + ".normgen";
    public static final String NORMGEN_PREFIX = Lucene3xSegmentInfoFormat.class.getSimpleName() + ".normfield";

    @Override
    public SegmentInfoReader getSegmentInfoReader() {
        return this.reader;
    }

    @Override
    public SegmentInfoWriter getSegmentInfoWriter() {
        throw new UnsupportedOperationException("this codec can only be used for reading");
    }

    public static int getDocStoreOffset(SegmentInfo si) {
        String v = si.getAttribute(DS_OFFSET_KEY);
        return v == null ? -1 : Integer.parseInt(v);
    }

    public static String getDocStoreSegment(SegmentInfo si) {
        String v = si.getAttribute(DS_NAME_KEY);
        return v == null ? si.name : v;
    }

    public static boolean getDocStoreIsCompoundFile(SegmentInfo si) {
        String v = si.getAttribute(DS_COMPOUND_KEY);
        return v == null ? false : Boolean.parseBoolean(v);
    }
}

