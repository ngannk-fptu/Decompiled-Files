/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;

public class SegmentReadState {
    public final Directory directory;
    public final SegmentInfo segmentInfo;
    public final FieldInfos fieldInfos;
    public final IOContext context;
    public int termsIndexDivisor;
    public final String segmentSuffix;

    public SegmentReadState(Directory dir, SegmentInfo info, FieldInfos fieldInfos, IOContext context, int termsIndexDivisor) {
        this(dir, info, fieldInfos, context, termsIndexDivisor, "");
    }

    public SegmentReadState(Directory dir, SegmentInfo info, FieldInfos fieldInfos, IOContext context, int termsIndexDivisor, String segmentSuffix) {
        this.directory = dir;
        this.segmentInfo = info;
        this.fieldInfos = fieldInfos;
        this.context = context;
        this.termsIndexDivisor = termsIndexDivisor;
        this.segmentSuffix = segmentSuffix;
    }

    public SegmentReadState(SegmentReadState other, String newSegmentSuffix) {
        this.directory = other.directory;
        this.segmentInfo = other.segmentInfo;
        this.fieldInfos = other.fieldInfos;
        this.context = other.context;
        this.termsIndexDivisor = other.termsIndexDivisor;
        this.segmentSuffix = newSegmentSuffix;
    }
}

