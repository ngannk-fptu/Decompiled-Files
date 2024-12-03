/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import org.apache.lucene.codecs.SegmentInfoReader;
import org.apache.lucene.codecs.SegmentInfoWriter;

public abstract class SegmentInfoFormat {
    protected SegmentInfoFormat() {
    }

    public abstract SegmentInfoReader getSegmentInfoReader();

    public abstract SegmentInfoWriter getSegmentInfoWriter();
}

